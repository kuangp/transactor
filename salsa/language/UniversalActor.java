
/**
 * $Id: UniversalActor.java,v 1.6 2010/06/11 05:49:36 wangw5 Exp $
 *
 * This is the main Actor class.
 *
 * ******************************************************
 * GC support:
 *   1. As an user-defined actor reference dies, both
 *      the target and the source actors should be notified.
 *   2. A silent actor reference is used by the comminucation
 *      system. It will be replaced by WeakReference at
 *      a latter SALSA version.
 *   3. A silent actor reference (UniversalActor) is one
 *      whose silent = true. It is the default state.
 *   4. WeakReference sourceActorReference is used when
 *      the UniversalActor is finalized to notify the source
 *      actor. It is meaningless if silent = true.
 *   5. Only ActivateGC() and muteGC() can set the variable
 *      silent outside the class. SALSA Programmers should not
 *      be aware of them.
 *   7. GC variables: sourceActorReference, silent, and temporarySilent
 *   8. state of silent/temporarySilent
 *      True /False : The reference not belonging to GC
 *      False/True  : Impossible state
 *      False/False : An activated reference which can issues GC messages
 *                    to its target and source actors. The state of it transits to
 *                    F/T while it is put in a message (as an argument, etc...).
 *      True /True  : The temporary state of a user-defined actor reference
 *                    in a message. It transits to F/F while an actor is about
 *                    to execute the message.
 *
 *   9. toInMessageState()= True/False, toActivateGCState() = False/False
 *  10. there are two types of actors - service and normal actors.
 *      To create actor remotely, a service actor uses createRemotely(UAN uan, UAL targetUAL, String actorName)
 *      while a normal actor uses createRemotely(UAN uan, UAL targetUAL, String actorName, ActorReference sourceRef)
 *  by WeiJen
 */

package salsa.language;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.lang.reflect.Method;

import java.util.Hashtable;
import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

import salsa.language.exceptions.CurrentContinuationException;

import salsa.messaging.TheaterService;
import salsa.messaging.TransportService;

import salsa.naming.NamingService;
import salsa.naming.UAL;
import salsa.naming.UAN;
import salsa.naming.MalformedUALException;
import salsa.naming.MalformedUANException;
import salsa.naming.HOST;

import salsa.resources.OutputService;
import salsa.resources.ErrorService;
import salsa.resources.SystemService;
import salsa.resources.InputService;

import gc.*;
import gc.message.*;


public class UniversalActor implements ActorReference, java.io.Serializable {
	private transient TransportService transportService = ServiceFactory.getTransport();
	private transient NamingService namingService = ServiceFactory.getNaming();

	private UAN uan = null;
	private UAL ual = null;

	/*
	 * These methods retrieve the UAN and UAL of the actor
	 */
	public UAN getUAN() { return uan; }
	public void setUAN(UAN uan) { this.uan = uan; }

	public UAL getUAL() { return ual; }
	public void setUAL(UAL ual) { this.ual = ual; }

	public String getIdentifier() {
		if (uan == null) return ual.getIdentifier();
		else return uan.getIdentifier();
	}

        public String getID() {
		if (uan != null) {
			return uan.toString();
		} else if (ual != null) {
			return ual.toString();
		}
		return "unknown_id";
	}

	/*
	 * These new constructors have replaced getReferenceByName and getReferenceByLocation
	 * They prevent messy actor creation and syntactically make more sense.
	 * By default, the compiler uses the ActorReference(UAL) constructor, as all actorreferneces
	 * begin as anonymous actors, and become non-anonymous when they are bound to a UAN
	 */
	public UniversalActor construct() { return this; }

        public void createRemotely(UAN uan, UAL targetUAL, String actorName) {
		RunTime.receivedUniversalActor();
		this.uan = uan;
		ual = namingService.generateUAL();

		 /**
		   * First we create a placeholder at the current theater, and
		   * the actor at the remote theater.  After the actor at the
		   * remote theater has finished being created, we can remove
		   * the placeholder, and send the messages to the remote
		   * actor
		   */
		namingService.setEntry(uan, ual, new Placeholder(uan, ual));
		namingService.update(uan, ual);

		UniversalActor remoteSystem = (UniversalActor)UniversalActor.getReferenceByLocation( targetUAL.getLocation() + "salsa/System" );
		SystemService localSystem = ServiceFactory.getSystem();
		UniversalActor self = null;

		if (uan != null) {
			self = (UniversalActor)UniversalActor.getReferenceByName(uan);
			self.setUAL(ual);
		} else {
			System.err.println("Remote Actor Creation error:");
			System.err.println("\tImproper creation request for actor: " + toString());
			System.err.println("\tException: Cannot create an actor remotely if it does not have a UAN.");
			return;
		}
		//remoteTheater<-createActor(uan, ual, actorName) @
		//localTheater<-removePlaceholder(uan, ual);
		Token token1 = new Token();
		Object[] createActorArgs = { uan, targetUAL, actorName};
		Message createActorMsg = new Message(self, remoteSystem, "createActor", createActorArgs, null, token1,false);

		Object[] removePlaceholderArgs = { uan, ual };
		Message removePlaceholderMsg = new Message(self, localSystem, "removePlaceholder", removePlaceholderArgs, token1, null,false);

		remoteSystem.send(createActorMsg);
		localSystem.send(removePlaceholderMsg);
	}

	public void createRemotely(UAN uan, UAL targetUAL, String actorName, ActorReference sourceRef) {
		RunTime.receivedUniversalActor();
		this.uan = uan;
		ual = namingService.generateUAL();
		/**
		 * First we create a placeholder at the current theater, and
		 * the actor at the remote theater.  After the actor at the
		 * remote theater has finished being created, we can remove
		 * the placeholder, and send the messages to the remote
		 * actor
		 */
		namingService.setEntry(uan, ual, new Placeholder(uan, ual));
		namingService.update(uan, ual);

		UniversalActor remoteSystem = (UniversalActor)UniversalActor.getReferenceByLocation( targetUAL.getLocation() + "salsa/System" );
		SystemService localSystem = ServiceFactory.getSystem();
		UniversalActor self = null;

		if (uan != null) {
			self = (UniversalActor)UniversalActor.getReferenceByName(uan);
			self.setUAL(ual);
		}
		else {
			System.err.println("Remote Actor Creation error:");
			System.err.println("\tImproper creation request for actor: " + toString());
			System.err.println("\tException: Cannot create an actor remotely if it does not have a UAN.");
			return;
		}
		//remoteTheater<-createActor(uan, ual, actorName) @
		//localTheater<-removePlaceholder(uan, ual);
		Token token1 = new Token();

		Object[] createActorArgs = { uan, targetUAL, actorName, sourceRef };
		Message createActorMsg = new Message(self, remoteSystem, "createActor", createActorArgs, null, token1, false);

		Object[] removePlaceholderArgs = { uan, ual };
		Message removePlaceholderMsg = new Message(self, localSystem, "removePlaceholder", removePlaceholderArgs, token1, null,false);

		remoteSystem.send(createActorMsg);
		localSystem.send(removePlaceholderMsg);
	}


	public UniversalActor()		{}
	public UniversalActor(UAN uan)	{
		//called by getReferenceByName
		this.uan = uan;
		this.ual = ServiceFactory.getNaming().get(uan);
	}
	public UniversalActor(UAL ual)	{ this.ual = ual; }

        public UniversalActor(boolean o,UAL ual)	{ this.ual = ual; }

	public UniversalActor(boolean o,UAN uan)        {this(uan);}

	/**
	 * The following methods provide a way to get a reference to an actor by name or by
	 * location.
	 */
	public static ActorReference getReferenceByName(String uan) throws MalformedUANException {
		return UniversalActor.getReferenceByName( new UAN(uan) );
	}
	public static ActorReference getReferenceByName(UAN uan) {
		return new UniversalActor(uan);
	}

	public static ActorReference getReferenceByLocation(String ual) throws MalformedUALException {
		return UniversalActor.getReferenceByLocation( new UAL(ual) );
	}
	public static ActorReference getReferenceByLocation(UAL ual) {
		return new UniversalActor(ual);
	}



	/*
	 * The following cover local and remote message sending for Actors
	 */
	public void send(Message message) {
		Object target = namingService.getTarget(this);

		if (target instanceof Actor) {
			try {
				((Actor)target).putMessageInMailbox(message);
			} catch (Exception e) {
				System.err.println("Error putting message in mailbox: ");
				System.err.println("Exception: " + e);
				System.err.println("\t Message: " + message);
				e.printStackTrace();
			}
		} else if (target instanceof UAL) {
			this.ual = (UAL)target;
			transportService.send(message,this);
		} else {
			System.err.println("Message Sending Error:");
			System.err.println("\tActorReference: " + this.toString());
			System.err.println("\tMessage: " + message.toString());
			System.err.println("\tNamingService could not determine target for message.");
		}
	}

	/*
	 * Methods for printing out the identity of an actor
	 */
	public String toString() {
		String name = " <";
		name +=  getClass().getName() + ":";
		if (uan != null) name += uan.toString() + ", ";
		else name += "null, ";
		if (ual != null) name += ual.toString();
		else name += "null";
		//return this.sourceActorReference+" -> "+name;
                return name+"> ";
	}

	public boolean equals(Object o) {
		if (o instanceof ActorReference) {
			ActorReference ref = (ActorReference)o;

			if (uan != null && ref.getUAN() != null) return uan.equals( ref.getUAN() );
			else if (ual != null && ref.getUAL() != null) return ual.equals( ref.getUAL() );
		} else if (o instanceof Actor) {
			Actor ref = (Actor)o;

			if (uan != null && ref.getUAN() != null) return uan.equals( ref.getUAN() );
			else if (ual != null && ref.getUAL() != null) return ual.equals( ref.getUAL() );
		}
		return false;
	}

	/*
	 * Methods for Actor serializability
	 */
	private void writeObject(java.io.ObjectOutputStream out) {
		//sending reference to local anonymous actor
		//need to keep the theater alive now since the reference has been
		//sent to the rest of the theater system.

		try {
			out.writeObject(uan);
			out.writeObject(ual);
                        out.writeBoolean(silent);
                        out.writeBoolean(temporarySilent);
                        out.writeObject(sourceActorReference);
			out.flush();
		} catch (IOException e) {
			System.err.println("Error during actor reference serialization: ");
			System.err.println("\tBy actor reference: " + toString());
			System.err.println("\tException: " + e);
		}
	}

	private void readObject(java.io.ObjectInputStream in) {
		try {
			uan = (UAN)in.readObject();
			ual = (UAL)in.readObject();
                        silent = in.readBoolean();
                        temporarySilent=in.readBoolean();
                        sourceActorReference=(WeakReference) in.readObject();
		} catch (Exception e) {
			System.err.println("Error during actor reference deserialization: ");
			System.err.println("\tBy actor reference: " + toString());
			System.err.println("\tException: " + e);
		}
		//get new instances of the services used by the Actor Reference
		transportService = ServiceFactory.getTransport();
		namingService = ServiceFactory.getNaming();
	}


	/********
		*  GC code section:
		*  GC-support functions: muteGC() and activateGC()
		*  GC source actor information: setSource()and getSource()
		*  GC auto-notification: finalize()
		*  GC actor reference state transition function:
		*    1. toInMessageState(): GC temporary inactive
		*    2. toActivateGCState(): GC active
		*    3. toStopGCSinkState(): to prevent sending GC messages
		*       while an actor reference becomes garbage
		*    4. setRefGCState(boolean,boolean): low level transition
		*       function
	*********/

        private WeakReference sourceActorReference = null;
        private boolean silent = true;
        private boolean temporarySilent = false;

        public boolean isSilent()		{ return silent; }
        public boolean isTemporarySilent()	{ return temporarySilent; }
        public boolean isWeakRef()		{ return silent && !temporarySilent; }
        public boolean isInMessageState()	{ return silent && temporarySilent; }
        public boolean isInActiveGCState()	{ return !temporarySilent && !silent; }

	public void muteGC() {
		silent = true;
		temporarySilent = false;
	}

	public void activateGC() {
		silent = false;
		temporarySilent = false;
	}

	public void toInMessageState() {
		if (!silent && !temporarySilent) {
			silent = true;
			this.temporarySilent = true;
		}
	}

	public void toActivateGCState() {
		if (temporarySilent && silent) {
			temporarySilent=false;
			silent=false;
		}
	}

	public void toStopGCSinkState() {
		temporarySilent=false;
		silent=true;
	}

        public void setRefGCState(boolean _silent,boolean _temporarySilent) {
		silent = _silent;
		temporarySilent = _temporarySilent;
        }

        public void setSource(ActorReference ref) {
		if (ref == null) sourceActorReference = null;
		else sourceActorReference = new WeakReference(ref);
        }

        public void setSource(UAN __uan, UAL __ual) {
		sourceActorReference = new WeakReference(__uan,__ual);
        }

        public ActorReference getSource() {
		return this.sourceActorReference;
        }

        protected void finalize() {
		if (!silent && this.sourceActorReference != null) {
			//notify the target and the source actors
			//skip the case that self==sourceActorReference

			if (sourceActorReference.getUAN() != null && uan != null && sourceActorReference.getUAN().equals(uan)) {
				return;
			} else if (sourceActorReference.getUAL()!=null && ual!=null && sourceActorReference.getUAL().equals(ual)) {
				return;
			}

			//update forward reference information
			WeakReference myself = new WeakReference(this);
			sourceActorReference.send(new RemoveForwardRefMsg(sourceActorReference, myself));
		}
	}

/**Begin UniversalActor.State**/

public abstract class State extends Thread implements Actor, java.io.Serializable {
  /*****
   * GC support:
   *  stateMemory: the soft memory that simulates the actor memory.
   *
   */
        private ActorMemory stateMemory;
        public ActorMemory getActorMemory() {return stateMemory;}
    public void setActorMemory(ActorMemory new_stateMemory) { this.stateMemory = new_stateMemory; }

	private UAN uan;
	private UAL ual;
	public	UAN getUAN()		{ return uan; }
	public	UAL getUAL()		{ return ual; }
	public void setUAL(UAL ual)	{ this.ual = ual; }
        public UAN generateUAN(String nameServer) {
		return ServiceFactory.getNaming().generateUAN(nameServer);
        }

	public String getIdentifier() {
		if (uan == null) return ual.getIdentifier();
		else return uan.getIdentifier();
	}

        public String getRefStr() {
		if (uan != null) return uan.toString();
		else return ual.toString();
        }

	private transient NamingService namingService;
	private transient TheaterService theaterService;
	public	transient ActorReference standardOutput;
	public	transient ActorReference standardError;
        public	transient ActorReference standardInput;

	public Vector __messages = new Vector();
	public Message currentMessage = null;

        public transient Vector joinTokenList = null;

	String[] classNames = new String[0];
	public transient Hashtable methods = new Hashtable();


	public State(UAN uan, UAL ual) {
                namingService = ServiceFactory.getNaming();
		theaterService = ServiceFactory.getTheater();

		this.uan = uan;
		/**
		 * If no ual is specified, we need to generate one.
		 * if the specified ual is for the current theater,
		 * we can simply assign that ual to the actor. if not
		 * we need to migrate the actor to where it's supposed to be.
		 */
		if (uan == null && ual != null && !ual.getLocation().equals(theaterService.getLocation())) {
			System.err.println("Actor Creation Error:");
			System.err.println("\tAttempting to create a non-local actor without a UAN");
			System.err.println("\tActor: " + toString());
			return;
		}
		this.ual = ual;
		if (this.ual == null) this.ual = namingService.generateUAL();
                if (this.uan!=null) {
                  stateMemory = new ActorMemory(this.uan.toString());
                  stateMemory.setPendingMessages(pendingMessages);
                } else {
                  stateMemory = new ActorMemory(this.ual.toString());
                  stateMemory.setPendingMessages(pendingMessages);
                }
		addClassName("salsa.language.UniversalActor$State");
		setName(getClass().getName());
                if (!(this instanceof salsa.resources.EnvironmentalServiceState)) {
                  RunTime.createdUniversalActor();
                }

	}

	public Method[] getMatches(String methodName) {
		return (Method[])methods.get(methodName);
	}

	public void addMethod(String methodName, Method method) {
		Method[] addedMethods = (Method[])methods.get(methodName);

		if (addedMethods == null) {
			addedMethods = new Method[1];
			addedMethods[0] = method;
			methods.put(methodName, addedMethods);
		} else {
			Method[] newAddedMethods = new Method[addedMethods.length + 1];
			newAddedMethods[0] = method;
			System.arraycopy(addedMethods, 0, newAddedMethods, 1, addedMethods.length);
			methods.put(methodName, newAddedMethods);
		}
	}

	public void addClassName(String className) {
		String[] tempNames = new String[classNames.length+1];
		tempNames[0] = className;
		System.arraycopy(classNames, 0, tempNames, 1, classNames.length);
		classNames = tempNames;
	}

	public void addMethodsForClasses() {
		methods = new Hashtable();

		for (int i = 0; i < classNames.length; i++) {
			try {
				Method[] declaredMethods = Class.forName(classNames[i]).getDeclaredMethods();
				for (int j = 0; j < declaredMethods.length; j++) {
					String name = declaredMethods[j].getName();
					addMethod( name, declaredMethods[j] );
				}
			} catch (Exception e) {
				System.err.println("Error generating methods for invocation, something is horribly wrong.");
				System.err.println("\tActor: " + toString() );
				System.err.println("\tError: " + e);
				e.printStackTrace();
			}
		}
	}

	public void messageTargetNotFound(Message message) {
		if (message.getTarget() instanceof ActorReference) {
			ActorReference target = (ActorReference)message.getTarget();
			UAN uan = target.getUAN();

			if (uan != null) {
				UAL ual = target.getUAL();
				namingService.refreshReference(target);
				UAL newUAL = target.getUAL();

				if ( !ual.equals(newUAL) || (namingService.getTarget(target) instanceof Actor)) {
					target.send(message);
					return;
				}
			}
		}
		System.err.println("Message sending error:");
		System.err.println("\tMessage: " + message.toString());
		System.err.println("\tFrom: " + toString());
		System.err.println("\tTo: " + message.getTarget().toString());
		System.err.println("\tException: Could not find the target actor.");
		System.err.println("\t\tIt has no UAN and was not found at the theater of its UAL.");
	}

	/** Die kills the actor when its mailbox has been
	  * emptied
	  */
	private boolean dead = false;
	public	void 	die()		{ dead = true; }
	public	boolean	isDead()	{ return dead; }

	/** Destroy immediately kills the actor without processing any
	  * more messages
	  */
	private boolean destroyed = false;
	public	void	destroy()	{ destroyed = true; }
	public	boolean	isDestroyed()	{ return destroyed; }

	/**
	 * Mailbox is used to store messages with no unresolved tokens.
	 * pendingMessages stores all the messages with unresolved tokens.
	 * unresolvedTokens stores the tokens that made it to the actor beforee
	 * their target message.
	 */
	public Vector mailbox = new Vector();
        public Vector sysMailbox=new Vector();
	public Hashtable pendingMessages = new Hashtable();
	public Vector unresolvedTokens = new Vector();
	/**
	 * This class wraps the information from a resolveToken message,
	 * into an object so only one Vector is needed to store unresolved
	 * tokens.
	 */
	private class UnresolvedTokenWrapper implements java.io.Serializable {
		protected String	messageId;
		protected int		position;
		protected Object	value;
                protected String        fromRef;
                protected boolean       isJoin;
                protected Object[]      refs;

		public UnresolvedTokenWrapper(String messageId, int position, Object value, String fromRef, boolean isJoin, Object[] refs) {
			this.messageId = messageId;
			this.position = position;
			this.value = value;
                        this.fromRef=fromRef;
                        this.isJoin=isJoin;
                        this.refs=refs;
		}
	}

	/**
	 * resolveToken messages are sent when a message has completed processing, and
	 * returns a token that other messages are waiting for.
	 * This method updates the specified message with the given value, if the message
	 * has already been received, if not the information is placed in an
	 * UnresolvedTokenWrapper and put in a Vector of unresolved tokens.
	 */
	public synchronized void resolveJoinToken(String messageId, int position, Object value, String fromRef) {
          Message targetMessage = (Message)pendingMessages.get(messageId);

          if (targetMessage != null) {
              if (currentMessage.refSummary!=null) {
                if (targetMessage.refSummary==null) {targetMessage.refSummary=new Vector();}
                targetMessage.refSummary.addAll(currentMessage.refSummary);
              }
              targetMessage.resolveJoinToken(value, position, fromRef);
            if (targetMessage.getContinuationToken().isResolved() == true) {
                pendingMessages.remove(messageId);
                RunTime.finishedProcessingMessage();
            }

          } else {
                  unresolvedTokens.add( new UnresolvedTokenWrapper(messageId, position, value, fromRef,true,currentMessage.refSummary.toArray()));
          }
        }

        public synchronized void resolveJoinToken(String messageId, int position, Object value, String fromRef, Object[] refs) {
          Message targetMessage = (Message)pendingMessages.get(messageId);

          if (targetMessage != null) {
              if (refs!=null) {
                if (targetMessage.refSummary==null) {targetMessage.refSummary=new Vector();}
                for (int i = 0; i < refs.length; i++) {
                  targetMessage.refSummary.addElement(refs[i]);
                }
              }
              targetMessage.resolveJoinToken(value, position, fromRef);
            if (targetMessage.getContinuationToken().isResolved() == true) {
                pendingMessages.remove(messageId);
                RunTime.finishedProcessingMessage();
            }

          } else {
                  unresolvedTokens.add( new UnresolvedTokenWrapper(messageId, position, value, fromRef,true,currentMessage.refSummary.toArray()));
          }
        }


	public synchronized void resolveToken(String messageId, int position, Object value, String fromRef) {
		/**
		 *	Try and resolve this token, if no matches are found
		 *	add it to the unresolvedHashtables because its
		 *	target message hasn't reached this actor yet.
		 */
		Message targetMessage = (Message)pendingMessages.get(messageId);
                if (targetMessage != null) {
                  if (targetMessage.refSummary==null) {targetMessage.refSummary=new Vector();}
                  if (currentMessage.refSummary!=null) targetMessage.refSummary.addAll(currentMessage.refSummary);
                    targetMessage.resolveToken(value, position, fromRef);
                  if (targetMessage.getContinuationToken().isResolved() == true) {
                      pendingMessages.remove(messageId);
                      RunTime.finishedProcessingMessage();
                  }

		} else {
			unresolvedTokens.add( new UnresolvedTokenWrapper(messageId, position, value, fromRef,false,currentMessage.refSummary.toArray()) );
		}
	}

        public synchronized void resolveToken(String messageId, int position, Object value, String fromRef, Object[] refs) {
                /**
                 *	Try and resolve this token, if no matches are found
                 *	add it to the unresolvedHashtables because its
                 *	target message hasn't reached this actor yet.
                 */
                Message targetMessage = (Message)pendingMessages.get(messageId);
                if (targetMessage != null) {
                  if (refs!=null) {
                    if (targetMessage.refSummary==null) {targetMessage.refSummary=new Vector();}
                    for (int i = 0; i < refs.length; i++) {
                      targetMessage.refSummary.addElement(refs[i]);
                    }
                  }
                    targetMessage.resolveToken(value, position, fromRef);
                  if (targetMessage.getContinuationToken().isResolved() == true) {
                      pendingMessages.remove(messageId);
                      RunTime.finishedProcessingMessage();
                  }

                } else {
                        unresolvedTokens.add( new UnresolvedTokenWrapper(messageId, position, value, fromRef,false,currentMessage.refSummary.toArray()) );
                }
        }

	/**
	 *	This method puts a message in this actors mailbox,
	 *	if it is not waiting for any tokens. If it is, the
	 *	message goes in the Hashtable of pendingMessages.
	 */

	public void putMessageInMailbox(Message message)
	{
		stateMemory.setActive(true);
		RunTime.receivedMessage();
		responseAck(message);
		putMessageInMailboxImp(message);
	}

	public synchronized void putMessageInMailboxImp(Message message) {
                if (message.getMethodName().equals("getPlaceholderMsg")) {
                  Object[] args=message.getArguments();
                  getPlaceholderMsg((Object[])args[0]);
                  return;
                }
                if (stateMemory.getSnapshotBitAGC()) {
                  if (message.refSummary!=null)
                    stateMemory.getForwardMailboxRefList().putReference(message.refSummary);
                }

                //responseAck(message);
		/**
		 * If this message is waiting for any tokens, it cannot
		 * be put in the normal mailbox, because then it would be
		 * processed without those tokens being resolved. We put
		 * it in a seperate hashtable of pending messages instead.
		 */
		if (message.getContinuationToken()!=null && !message.getContinuationToken().isResolved()) {
			String messageId = message.getMessageId();
			pendingMessages.put(messageId, message);

			/**
			 * We need to check and see if any tokens that were meant for
			 * this message have already been receieved.  This requires
			 * going through the vector of unresolved tokens, and resolving
			 * the ones intended for this message.
			 */
			for (int i = 0; i < unresolvedTokens.size(); i++) {
				UnresolvedTokenWrapper unresolvedToken = (UnresolvedTokenWrapper)unresolvedTokens.get(i);

				if (unresolvedToken.messageId.equals(messageId)) {
                                        if (unresolvedToken.isJoin) {
                                          resolveJoinToken(messageId,unresolvedToken.position,
                                                           unresolvedToken.value,unresolvedToken.fromRef,unresolvedToken.refs);
                                        }
                                        else {
                                          resolveToken(messageId, unresolvedToken.position,
                                                       unresolvedToken.value, unresolvedToken.fromRef,unresolvedToken.refs);
                                        }
					unresolvedTokens.remove(i);
					i--;
				}
			}
		} else {
			if (message.getMethodName().equals("construct") || (message.getProperty() != null && message.getProperty().equals("priority"))) {
                /******* Transactor support ******/
                if (message.getProperty() != null && message.getProperty().equals("priority") && message.getPropertyParameters() != null && ((String)message.getPropertyParameters()[0]).equals("highPriority")) {
                    // This makes sure this message is processed next after a Transactor evaluates the message dependencies in recvMsg
                    // or sets its WV after new transactor creation
                    mailbox.insertElementAt(message, 0);
                    /*********************************/
                }
                else {

                    //This makes sure that the message doesn't preceeed messages with high priority
                    if(mailbox.size()!=0){
                        int i = 0;
                        while( i<mailbox.size() && (((Message)mailbox.get(i)).getProperty() != null && ((Message)mailbox.get(i)).getProperty().equals("priority"))) i++;
                        mailbox.insertElementAt(message, i);
                    }
                    else
                        mailbox.insertElementAt(message, 0);
                }

			} else {
				mailbox.addElement(message);
			}

			if (message.getMethodName().equals("construct")) {
                                stateMemory.setActive(true);
				start();
			} else {
                          notify();
			}
		}
	}

	public synchronized Message getMessage() {
		if (mailbox.isEmpty()) {
			try {
                          if (this.unresolvedTokens.isEmpty() && this.pendingMessages.isEmpty()) {
                            stateMemory.setActive(false);
                          }
				//System.out.println("waiting:" + this.getIdentifier());
				wait();      // The lock of Mailbox means it is empty.
				//System.out.println("wakeup :" + this.getIdentifier());
			} catch (InterruptedException ie){
				System.err.println("Error from within salsa.language.Actor: " + toString());
				System.err.println("\tError getting a new message:");
				System.err.println("\t" + ie);
			}
		}
                 try {
                    Message nextMessage = (Message) mailbox.remove(0);
                    return nextMessage;
                 }catch (Exception e) {return null;}

	}

    /******* Transactor support ******/
    public boolean rollingback = false;
    /*********************************/

	public void run() {
		standardOutput = ServiceFactory.getOutput();
		standardError = ServiceFactory.getError();
                standardInput = ServiceFactory.getInput();

                while (isLive()) {
                        currentMessage = getMessage();
                        if (currentMessage==null) {continue;}
						//System.out.println("process:" + this.getIdentifier() + ", m=" + currentMessage.getMethodName());
			process(currentMessage);
                        if (!currentMessage.getMethodName().equals("die") ) {
                              RunTime.finishedProcessingMessage();
                        }
                        currentMessage=null;
		}
                if (!migrating && !rollingback) {
                  if (this.destroyed) {RunTime.finishedProcessingMessage(mailbox.size()+this.pendingMessages.size()-1);}

                  if (uan!=null) ServiceFactory.getTheater().removeSecurityEntry(uan.toString());
                  ServiceFactory.getNaming().delete(this.getUAN());
                }
                //update security info
	}

        public boolean isLive() {return (!rollingback) && (!destroyed) && (!migrating) && (!dead || !mailbox.isEmpty());}

        public void addJoinToken(Token joinToken) {
          if (this.joinTokenList==null) {
            joinTokenList=new Vector();
          }
          this.joinTokenList.add(joinToken);
        }

	public void sendGeneratedMessages() {
          /*create join director at this moment*/
          if (joinTokenList!=null) {
            for (int i = 0; i < this.joinTokenList.size(); i++) {
              Token joinToken = (Token)this.joinTokenList.get(i);
              joinToken.createJoinDirector();
              joinToken.setJoinInputsTarget();
            }
            joinTokenList.clear();
          }

		/**
		 * When a message is processed, if it sends any messages they are placed in
		 * a vector called messages. After the messages are processed, this sends
		 * those messages to their targets.
		 */
		for (int i = 0; i < __messages.size(); i++) {
			final Message current = (Message)__messages.get(i);
			final ActorReference target = current.getTarget();
			/**
			 * If this message uses the delay property, this creates a thread to
			 * wait until that time has elapsed to send the message, if not the
			 * message is simply sent.
			 */
			if (current.getProperty() != null && (current.getProperty().equals("delay") || current.getProperty().equals("delayWaitfor"))) {
				Object[] parameters = current.getPropertyParameters();
				final long sleepTime = ((Integer)parameters[0]).intValue();

				Runnable delayThread = new Runnable() {
					public void run() {
						try {
							Thread.sleep(sleepTime);
						} catch (Exception e) {
							System.err.println("Error Sending Message: ");
							System.err.println("\tError in delay thread caused by using the message delay property.");
							System.err.println("\tMessage: " + current.toString());
							System.err.println("\tException: " + e);
						}
						target.send(current);
                                                RunTime.finishedProcessingMessage();

					}
				};
                                RunTime.receivedMessage();
				new Thread(delayThread, "Delay Thread").start();
			} else {
//System.out.println("sending:" + this.getIdentifier() + ", target=" + current.getTarget());
				target.send( current );
			}
		}
		__messages.clear();
	}

	/**
	 * This method moves the actor from one theater to another.
	 */
	private boolean migrating = false;
	public	boolean isMigrating() { return migrating; }

        public void migrate (String newUAL) throws MalformedUALException {
          try { migrate (new UAL (newUAL)); }
          catch (MalformedUALException e) { migrate ((UAL) new HOST (newUAL)); }
        }

	// migrate (UAL) is the primary entrance point for migration
	public void migrate (UAL targetUAL) {
                migrating = true;

                //security check
                  if (uan==null || !ServiceFactory.getTheater().checkSecurityEntry(uan.toString())) {
                    migrating=false;
                    System.err.println("Actor migration warnning: Migrating to the same host due to security restrictions.");
                    System.err.println("\tException: Cannot migrate an actor: either 1) no UAN exists, or 2) there is a security restriction.");
                    return;
                }


                UAL myual=namingService.get(uan);
                if (myual.getHost().equals(targetUAL.getHost()) && targetUAL.getPort()==myual.getPort()) {
                  migrating=false;
                  System.err.println("Actor migration warnning: Migrating to the same host!: Name unchanged.");
                  return;
                }

		/**
		 *	Stop the actor from processing more messages while we wait for it
		 *	to migrate.
		 */


		/**
		 * createPlaceholder() @
		 * remoteSystem<-addActor(this) @
		 * localSystem<-removePlaceholder() @
		 * currentContinuation;
		 */
		//namingService.remove(uan, ual);
                ServiceFactory.getTheater().removeSecurityEntry(uan.toString());
		namingService.setEntry(uan, ual, new Placeholder(uan, ual));
                namingService.update(uan,targetUAL);

		WeakReference remoteSystem = new WeakReference((UniversalActor)UniversalActor.getReferenceByLocation( targetUAL.getLocation() + "salsa/System" ));

		SystemService localSystem = ServiceFactory.getSystem();

		UniversalActor self = (UniversalActor)UniversalActor.getReferenceByName(this.uan);

		Token token1 = new Token();

                //updating ual and uan
                UAL oldUAL=this.ual;
                this.ual=targetUAL;
                self.ual=targetUAL;

                //lock self and
                //must be removed after receiving the messages from placeholder
                if (!(this instanceof salsa.resources.EnvironmentalServiceState) &&
                    !(this instanceof salsa.resources.ActorServiceState))
                  {stateMemory.getInverseList().putInverseReference("rmsp://me");}

                //serialize this actor
                byte[] serializedArguments;
                try {
                  ByteArrayOutputStream bos = new ByteArrayOutputStream();
                  ObjectOutputStream outStream = new ObjectOutputStream( bos);
                  outStream.writeObject(this);
                  outStream.flush();
                  serializedArguments=bos.toByteArray();
                  outStream.close();
                  bos.close();
                }
                catch (Exception e) {System.err.println("UniversalActor.State Class, migrate() method: Error on serializing this actor:"+e); return;}
                RunTime.finishedProcessingMessage(mailbox.size() +
                                                  this.pendingMessages.size());

		Object[] addActorArgs = { serializedArguments };
		Message addActorMsg = new Message(self, remoteSystem, "addActor", addActorArgs, null, token1,false);

		Object[] removePlaceholderArgs = { uan, oldUAL };
		Message removePlaceholderMsg = new Message(self, localSystem, "removePlaceholder", removePlaceholderArgs, token1, currentMessage.getContinuationToken(),false);

		remoteSystem.send(addActorMsg);
                RunTime.deletedUniversalActor();
		localSystem.send(removePlaceholderMsg);
                this.forceAllRefSilent();

		throw new CurrentContinuationException();
	}

	/**
	 * This prints a human readable version of the actor, with its uan and ual
	 * This will be equivalent to what a reference to this actor would return
	 * with toString().
	 */
	public String toString() {
		String name = "";
		name +=  getClass().getName() + ": ";
		if (uan != null) name += uan.toString() + ", ";
		else name += "null, ";
		if (ual != null) name += ual.toString();
		else name += "null";
		return name;
	}

	/**
	 * This returns true if two Actors are equal, it will also return true
	 * if an actor and a reference pointing to that actor are compared.
	 */
	public boolean equals(Object o) {
		if (o instanceof UniversalActor) {
			UniversalActor ref = (UniversalActor)o;

			if (uan != null && ref.getUAN() != null) return uan.equals(ref.getUAN());
			else if (ual != null && ref.getUAL() != null) return ual.equals(ref.getUAN());
		} if (o instanceof UniversalActor.State) {
                        UniversalActor.State ref = (UniversalActor.State)o;

			if (this.uan != null && ref.getUAN() != null) return this.uan.equals(ref.getUAN());
			else if (ual != null && ref.getUAL() != null) return ual.equals(ref.getUAN());
		}
		return false;
        }

	/** The following are used to serialize and deserialize an Actor
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.flush();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		// Get service references from the ServiceFactory.
		namingService = ServiceFactory.getNaming();
		theaterService = ServiceFactory.getTheater();

		standardOutput = ServiceFactory.getOutput();
		standardError = ServiceFactory.getError();
                standardInput = ServiceFactory.getInput();
		addMethodsForClasses();

		setName(getClass().getName());

		migrating = false;
		destroyed = false;
                stateMemory.setPendingMessages(pendingMessages);
                RunTime.receivedMessage(mailbox.size()+this.pendingMessages.size());
	}

        public synchronized void putMessageInMailbox(SystemMessage message) {
          Message msg =message.castToMessage();
          if (this instanceof  salsa.resources.EnvironmentalServiceState ||
              (this instanceof salsa.resources.ActorServiceState)) {
            //we do not need to make service actors alive
            //  so system message Ack can be ignored!
            if (message instanceof Ack) {return;}
            RunTime.receivedMessage();
            mailbox.addElement(msg);
            notify();
          } else {
            sysMailbox.addElement(msg);
          }
        }

        public void process(SystemMessage message) {
          /***********
           * for future implementation
           ***********/
        }

    /**********************
    * GC function required
    * 1. scanRefInMailboxAGC(): get references in mailbox (including tokens),
    *      called by an Actor garbage collector.
    * 2. removeInv(ref) : remove inverse reference
    * 3. receiveAck() and waitAck(): acknoweledgement msg counter operations
    *    -> receiveAck(ref) : get an ack msg and thus need to decrease the
    *       expected ack counter for a given reference
    *    -> waitAck(ref) : increase the expected ack counter for a
    *       given reference
    * 4. responseAck(msg): create an Ack message to response a normal message 'msg'
    * 5. GCdie() : call by the local garbage collector
    * 6. ServiceDie() : one can invoke it to remove a service actor.
    **********************/

        public synchronized void scanRefInMailboxAGC() {
          /**
           * scan the mailbox first, then pendingMessages, and then
           *   unresolved tokens. Don't forget the current message.
           */
//System.out.print("** scan "+ual.getIdentifier()+": ");
          MailboxRefList fmList=stateMemory.getForwardMailboxRefList();
          MailboxRefList imList=stateMemory.getInverseMailboxRefList();
          Message processingMsg= currentMessage;
          if (processingMsg!=null && processingMsg.getContinuationToken()!=null) {
              // put target addresses in the actor memory for actor GC
              Vector targetActors=processingMsg.getContinuationToken().getTargetActors();
              for (int j=0;j<targetActors.size();j++) {
                ActorReference ref=(ActorReference)targetActors.get(j);
                fmList.putReference(ref.getID());
//System.out.print("-c->"+ref.getID()+" ");
              }
              Vector sourceActors=processingMsg.getContinuationToken().getSync();
              if (sourceActors!=null) {
                for (int j = 0; j < sourceActors.size(); j++) {
                  String ref = (String) sourceActors.get(j);
                  imList.putReference(ref);
//System.out.print("<-c-"+ref+" ");
                }
              }
          }

          for (int i=0;i<mailbox.size();i++) {
            Message msg=(Message) mailbox.get(i);
            fmList.putReference(msg.refSummary);
            Token msgToken=msg.getContinuationToken();
            if (msgToken!=null) {
              // put target addresses in the actor memory for actor GC
              Vector targetActors=msgToken.getTargetActors();
              for (int j=0;j<targetActors.size();j++) {
                ActorReference ref=(ActorReference)targetActors.get(j);
                fmList.putReference(ref.getID());
//System.out.print("-m->"+ref.getID()+" ");
              }
              Vector sourceActors=msgToken.getSync();
              if (sourceActors!=null) {
                for (int j = 0; j < sourceActors.size(); j++) {
                  String ref = (String) sourceActors.get(j);
                  imList.putReference(ref);
//System.out.print("<-m-"+ref+" ");
                }
              }

            }
          }
          for (Enumeration e = this.pendingMessages.elements();  e.hasMoreElements(); ) {
            Message msg=(Message)e.nextElement();
            Token msgToken=msg.getContinuationToken();
            if (msgToken!=null) {
              // put target addresses in the actor memory for actor GC
              Vector targetActors=msgToken.getTargetActors();
              for (int j=0;j<targetActors.size();j++) {
                ActorReference ref=(ActorReference)targetActors.get(j);
                fmList.putReference(ref.getID());
//System.out.print("-p->"+ref.getID()+" ");
              }
              //then put source address that the message is waiting for
              Vector sourceActors=msgToken.getSync();
              for (int j=0;j<sourceActors.size();j++) {
                String ref=(String)sourceActors.get(j);
                imList.putReference(ref);
//System.out.print("<-p-"+ref+" ");
              }
            }

          }
//System.out.println();
        }

        public synchronized void removeInverseRef(String ref, Integer no) {
          if (this instanceof  salsa.resources.EnvironmentalServiceState ||
                    (this instanceof salsa.resources.ActorServiceState)) {return;}
          stateMemory.getInverseList().removeInverseReference(ref,no.intValue());
        }

        public synchronized void putInvRefAndUnlock(String refStr,String initRefStr) {
          if (this instanceof  salsa.resources.EnvironmentalServiceState ||
                    (this instanceof salsa.resources.ActorServiceState)) {return;}
          stateMemory.getInverseList().putInverseReference(refStr);
          EndMsgPassing endMsg=null;
          WeakReference initRef=WeakReference.parseWeakReference(initRefStr);
          if (this.uan!=null) {
            endMsg = new EndMsgPassing(initRef,refStr, this.uan.toString());
          } else {
            endMsg = new EndMsgPassing(initRef,refStr, this.ual.toString());
          }
          initRef.send(endMsg);
       }

       public synchronized void endMsgPassing(String targetActor, String referencedActor) {
             if (this instanceof  salsa.resources.EnvironmentalServiceState ||
                       (this instanceof salsa.resources.ActorServiceState)) {return;}
             stateMemory.getForwardList().receiveAck(referencedActor);
             stateMemory.getForwardList().receiveAck(targetActor);
       }

       public synchronized void removeForwardRef(String ref,Integer i) {

          if (((this instanceof  salsa.resources.EnvironmentalServiceState)||
                    (this instanceof salsa.resources.ActorServiceState))) {
            if (stateMemory.getForwardList().removeReferenceImmediately(ref)) {
              WeakReference invActor;
              if (ref.charAt(0)=='u') {invActor = new WeakReference(new UAN(ref), null);}
              else {invActor = new WeakReference(null, new UAL(ref));}
              invActor.send(new RemoveInverseRefMsg(invActor,new WeakReference(getUAN(),getUAL()), 1));
            }

          } else {
            stateMemory.getForwardList().removeReference(ref);
          }
        }

        public synchronized void receiveAck(String ref,Integer i) {
          if (this instanceof  salsa.resources.EnvironmentalServiceState ||
                    (this instanceof salsa.resources.ActorServiceState)) {return;}
          stateMemory.getForwardList().receiveAck(ref);
        }

        public synchronized void waitAck(String ref) {
          if (this instanceof  salsa.resources.EnvironmentalServiceState ||
                    (this instanceof salsa.resources.ActorServiceState)) {return;}
          stateMemory.getForwardList().waitAck(ref);
        }

        protected void responseAck(Message msg) {
          if (msg.getNeedAck()==false) {return;}
          if (msg.getMethodName().equals("construct")) {return;}
          WeakReference ackTarget=(WeakReference)msg.getSource();
          WeakReference sourceRef=(WeakReference)msg.getTarget();
          String argument=null;
          String sourceStr=null;
          if (ackTarget.getUAN()!=null) {sourceStr=ackTarget.getUAN().toString();}
          else if (ackTarget.getUAL()!=null) {sourceStr=ackTarget.getUAL().toString();}
          if (sourceRef.getUAN()!=null) {argument=sourceRef.getUAN().toString();}
          else if (sourceRef.getUAL()!=null) {argument=sourceRef.getUAL().toString();}

          if (argument!=null) {
            if (argument.equals(sourceStr)) {return;}
            Ack ackMsg = new Ack( ackTarget, argument);
            ackTarget.send(ackMsg);
          }
        }

		//*********************************************************
		//This method is invoked while a message is about processing
		//*********************************************************
		public void  activateArgsGC(Message msg)
		{
			byte[] serializedArguments;
			if (!msg.getHasActorReferenceArgs()) { return; }
			if (msg.getMethodName().equals("addActor")) { return; }
			//System.out.println("activate:" + getIdentifier() + ", (=1=)");
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream outStream = new ObjectOutputStream(bos);
				outStream.writeObject(msg.getArguments());
				outStream.flush();
				serializedArguments = bos.toByteArray();
				outStream.close();
				bos.close();
			}
			catch (Exception e) { System.err.println("Message Class, getArguments() method: Error on serializing method arguments:" + e); return; }
			//System.out.println("activate:" + getIdentifier() + ", (=2=)");
			try
			{
				ByteArrayInputStream bis = new ByteArrayInputStream(serializedArguments);
				GCObjectInputStream inStream = new GCObjectInputStream(bis, GCObjectInputStream.ACTIVATE_GC, msg.getWeakRefTarget(), msg.getWeakRefSource());
				msg.setArguments((Object[])inStream.readObject());
				//System.out.println("activate:" + getIdentifier() + ", (=3=)");
				msg.refSummary = inStream.getRefSummary();
				//System.out.println("activate:" + getIdentifier() + ", (=4=)");
				//register forward references
				for (int i = 0; i < msg.refSummary.size(); i++)
				{
					stateMemory.getForwardList().putReference(((String)(msg.refSummary.get(i))));
				}
				//System.out.println("activate:" + getIdentifier() + ", (=5=)");
				__messages.addAll(inStream.getMails());
				//for (int i = 0; i < mails.size(); i++)
				//{
				//	Message sysmsg = (Message)mails.get(i);
				//	sysmsg.getTarget().send(sysmsg);
				//}
				inStream.clearMails();
				//System.out.println("activate:" + getIdentifier() + ", (=6=)");
				inStream.close();
				bis.close();
				//System.out.println("activate:" + getIdentifier() + ", (=7=)");

			}
			catch (Exception e) { System.err.println("Message Class, activateArgsGC() method:Error on deserializing method arguments:" + e); }
			return;
		}


        /**
         *  It forces all references in this actor becoming silent
         *  Because of the GCDummyOutputStream, the ObjectOutputStream
         *  writes nothing but filters all actor references and mute them.
         */
        public synchronized void forceAllRefSilent() {
          try {
            GCObjectOutputStream outStream =
                new GCObjectOutputStream(GCObjectOutputStream.FORCE_WEAK);
            outStream.writeObject(this);
            outStream.flush();
            outStream.close();
          }
          catch (Exception e) {System.err.println("UniversalActor$State Class, forceAllRefSilent() method: "+e); return;}
        }

        public synchronized void ReactivateGC() {
          try {
            GCObjectOutputStream outStream =
                new GCObjectOutputStream(GCObjectOutputStream.ACTIVATE_GC);
            outStream.writeObject(this);
            outStream.flush();
            outStream.close();
          }
          catch (Exception e) {System.err.println("UniversalActor$State Class, ReactivateGC() method: "+e); return;}
        }

        public void finalize() {
        }

        public synchronized void GCdie() {
          Object[] args={};
          Message msg=new Message(null,null,"die",args,false);
          mailbox.addElement(msg);
          notify();
        }

        public synchronized void GCdestroy() {
          Object[] args={};
          Message msg=new Message(null,null,"destroy",args,false);
          mailbox.add(0,msg);
          notify();
        }

        public synchronized void ServiceDie() {
          dead = true;
          cleanReference();
          if (uan!=null) this.namingService.delete(uan);
          else namingService.remove(uan,ual);
          RunTime.finishedProcessingMessage(mailbox.size()+this.pendingMessages.size());
        }

        private void cleanReference() {
          //need implemenation
        }

        public synchronized String getSelfRefStr() {
          return stateMemory.getForwardList().getSelfRef();
        }

        public synchronized void getPlaceholderMsg(Object[] msgs) {

          if (!(this instanceof salsa.resources.EnvironmentalServiceState)&&
                    !(this instanceof salsa.resources.ActorServiceState)) {
            stateMemory.getInverseList().removeInverseReference("rmsp://me",1);
          }
          if (msgs==null || msgs.length==0) {
            notify();return;
          }
          for (int i=0;i<msgs.length;i++) {
            Object msg=msgs[i];
            if (msg instanceof SystemMessage) {
              this.putMessageInMailbox((SystemMessage)msg);
            } else {
              this.putMessageInMailbox((Message)msg);
            }
          }
        }
}
/**END OF UniversalActor.State **/
}

