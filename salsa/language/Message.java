/**
 * This is the Message class.
 *
 *
 * **********************************************************
 * GC support:
 *   1. The local object reference sharing problem is solved by
 *       -> serialize/deserialize method arguments when initialization,
 *          which (1) forces to create a new copy of arguments
 *                (2) mutes UniversalActors (disable salsa GC protocol)
 *       -> store target and source actor references as WeakReference
 *          instead of UniversalActor for disabling salsa GC protocol.
 *       -> The muted UniversalActors should be re-activated again while
 *          the message is about to be processed by the target actor.
 *   2. There are two kinds of messages. One is the normal message which
 *      always needs an ACK. The other is the ACK-free message which is
 *      usually used by the salsa library
 *   3. When a message is created, its arguments are scaned so that
 *      UniversalActors are found and triggered the 2-phase remote
 *      reference registration protocol.
 *
 * Asynchronous ACK protocol:
 *   1. waitAck(): The method to increase the counter of a reference, which
 *        is required by the asynchronous ACK protocol.
 *   2. When a theater receives a message which requires an ACK, it will response
 *        an ACK immediately. The actor to receive the ACK will decrease the counter
 *        of the corresponding reference.
 *
 * Reference passing protocol:
 *   1. The method "referenceSplitting()" serializes a message and
 *      identifies references in it.

 * by WeiJen Wang
 * **********************************************************
 */

package salsa.language;

import gc.*;
import java.io.*;
import java.util.Vector;

public class Message implements java.io.Serializable {
        /**
         *  needAck: to check if the message needs an Acknoweldgement
         *  If true is set, then the receiver must reply an ACK
         */
        private boolean needAck;
        public void setNeedAck(boolean val) {needAck=val;}
        public boolean getNeedAck() {return needAck;}


	/**
	 *	messageId is a unique message generated for each
	 *	message and is used for token resolution.
	 */
	private String		messageId;
	public	String		getMessageId()		{ return messageId; }

	/**
	 *	Actor that the message is being sent from and
	 *	Actor that the message is being sent to.
	 */
	private	WeakReference	source, target;
        private boolean hasActorReferenceArgs=false;
	    public boolean getHasActorReferenceArgs() { return hasActorReferenceArgs; }

        public Vector refSummary=null;



	public	ActorReference	getSource(){
          if (source==null) {
            return target;
            //return null;
          }
          return source;
        }
	public	ActorReference	getTarget() {
          return target;
        }

		public WeakReference getWeakRefSource()
		{
			if (source == null)
			{
				return target;
				//return null;
			}
			return source;
		}
	public WeakReference getWeakRefTarget()
		{
			return target;
		}

	public String getSourceName () {
		if (source == null) return "";
		else if (source.getUAN () != null) return source.getUAN ().toString ();
		else if (source.getUAL () != null) return source.getUAL ().toString ();
		else return "";
	}

	public String getTargetName () {
		if (target == null) return "";
		else if (target.getUAN () != null) return target.getUAN ().toString ();
		else if (target.getUAL () != null) return target.getUAL ().toString ();
		else return "";
	}

	/**
	 *	Name of the method that should be invoked.
	 */
	private	String 		methodName;
	public	String 		getMethodName()		{ return methodName; }

	/**
	 *	Array of values that are copied to be the arguments of the method. If one of
	 *	the arguments is the value of a yet to be evaluated token an instance of the
	 *	Token class should be subituted for it until it is evaluated.
	 */
        private Object[] arguments;

	/**
	 *	This is the number of tokens which must be received by the message before
	 *	it can be processed, and a vector of those tokens.
	 */
	//private	int		requiredTokens = 0;
	//public	int		getRequiredTokens()	{ return requiredTokens; }

	/**
	 *	After this message has been processed, it's return token is passed
	 *	to the joinDirector and the continuationToken, if they aren't null.
	 */
	//private int		joinPosition = 0;
        //public  void            setJoinPosition(int pos) {joinPosition=pos;}
        //public  int             getJoinPosition() {return joinPosition;}

	private Token		continuationToken = null;
        public  void            setContinuationToken(Token cont) { continuationToken=cont; }
	public	Token		getContinuationToken()	{ return continuationToken; }

	/**
	 *	A message can be sent with a property. These methods
	 *	handle assigning of properties and their information
	 *	to a method, and are used by the compiler.  They also
	 *	provide access to this information for use by the
	 *	salsa.language package.
	 */
	private String		propertyName = null;
	private Object[]	propertyParameters = null;
	public	String		getProperty() { return propertyName; }
	public	Object[]	getPropertyParameters() { return propertyParameters; }

	public	void setProperty(String propertyName, Object[] propertyParameters) {
		this.propertyName = propertyName;
		this.propertyParameters = propertyParameters;

		if (propertyName.equals("waitfor")) {
			for (int i = 0; i < propertyParameters.length; i++) {
				if ( !(propertyParameters[i] instanceof Token) ) {
					System.err.println("Message Creation Error:");
					System.err.println("\tA parameter of a waitfor property was not a token.");
					System.err.println("\tOn message: " + toString());
					return;
				}
				Token currentToken = (Token)propertyParameters[i];
				if (currentToken.getValue() == null) {
                                  if (continuationToken==null) {
                                    continuationToken=new Token("",this.target.getID());
                                  }
                                  continuationToken.addSync(currentToken.getOwner());
					//requiredTokens++;
					if (target != null) currentToken.addTarget(target, messageId, -3);
					else currentToken.addTarget(source, messageId, -3);
				}
			}
                      } else if (propertyName.equals("delayWaitfor")) {
                        try{
                          for (int i = 1; i < propertyParameters.length; i++) {
                            Token currentToken = (Token)propertyParameters[i];
                            if (currentToken.getValue() == null) {
                              if (continuationToken==null) {
                                continuationToken=new Token("",this.target.getID());
                              }
                              continuationToken.addSync(currentToken.getOwner());
                              //requiredTokens++;
                              if (target != null)
                                currentToken.addTarget(target, messageId, -3);
                              else
                                currentToken.addTarget(source, messageId, -3);
                            }
                          }
                        }catch (Exception e) {
                                              System.err.println("Message Creation Error:");
                                              System.err.println("\tA parameter of a waitfor property was not a token.");
                                              System.err.println("\tOn message: " + toString());
                                              return;
                        }
                      }

	}

	/**
	 *	The process method in actor calls resolveContinuations,
	 *	with the return value for processing this message.  That
	 *	token is sent to the messages joinDirector and continuationToken's
	 *	targets, if they exist.
	 */
	public void resolveContinuations(Object value) {
		/*
		 * We need to specify where the resolve message is coming from.
		 * if this message being resolved, that means it's at its
		 * target.
		 */
		if (continuationToken != null) {
			if (!continuationToken.isJoinInput()) {
                          continuationToken.resolve(target, value);
                        }
			else {
                          continuationToken.resolveJoin(target,value);
				/**
				 * If this message was part of a join block, we need to also send
				 * the token to the join director, specifying where that token needs
				 * to go in the array returned by the join director.  We do this by
				 * simply sending a message to the join director.
				 */
				//ActorReference joinDirectorReference = continuationToken.getJoinDirector();

				//Object[] arguments = { new Integer(joinPosition), value };
				//joinDirectorReference.send( new Message(target, joinDirectorReference, "resolveJoinToken", arguments, null, null,false) );
			}
		}
	}

	/**
	 *	This method is called when a token has been received that this
	 *	message is waiting for.  This updates the corresponding pendingToken
	 *	in the message.  When all pendingTokens have been resolved,
	 *	this message can be placed in it's target actors mailbox.
	 */
	public void resolveToken(Object value, int position, String sourceRef) {
//System.out.println("&&& token pos="+position);

		/**
		 * Tokens are resolved according to their position,
		 * position >= 0 is that position in the array of
		 * arguments. -1 is the target actor of the message,
		 * -2 is the methodname of the message. -3 corresponds
		 * to a synchronization token, so nothing needs to be
		 * updated.
		 */

		if (position >= 0) {
			arguments[position] = value;
		} else if (position == -1) {
			if (value instanceof ActorReference) {
				target = new WeakReference((ActorReference)value);
			} else {
				System.err.println("Token Resolution Error:");
				System.err.println("\tA message was sent a token to resolve it's target which was");
				System.err.println("\tnot an actor.");
				System.err.println("\tOccured in message: " + toString());
			}
		} else if (position == -2) {
			if (value instanceof String) {
				methodName = (String)value;
			} else {
				System.err.println("Token Resolution Error:");
				System.err.println("\tA message was sent a token to resolve it's method name which was");
				System.err.println("\tnot a String.");
				System.err.println("\tOccured in message: " + toString());
			}
		}

		synchronized(methodName) {
			//requiredTokens--;
                        this.continuationToken.resolveSync(sourceRef);
			if (this.continuationToken.isResolved()) {
                                this.setNeedAck(false);
				target.send(this);
			}
		}
	}

        public void resolveJoinToken(Object value, int position, String sourceRef) {

          continuationToken.receiveJoinToken(value,position);
          if (continuationToken.isJoinResolved()) {
            int joinPosition=continuationToken.getJoinPosition();
            if (joinPosition>=0) {
              arguments[joinPosition] = continuationToken.getJoinData().getJoinArgs();
            }
          }

          synchronized(methodName) {
                  //requiredTokens--;
                  this.continuationToken.resolveSync(sourceRef);
                  if (this.continuationToken.isResolved()) {
                          this.setNeedAck(false);
                          target.send(this);
                  }
          }
        }


        public Message(ActorReference source, Object target, Object methodName, Object[] arguments, Token synchronizationToken, Token continuationToken)  {
          this(source, target, methodName, arguments, synchronizationToken, continuationToken,true,true);
        }

        public Message(WeakReference source, WeakReference target, String methodName, Object[] arguments, boolean requireAck) {
          this.needAck=requireAck;
          this.target=target;
          this.source=source;
          this.methodName=methodName;
          this.arguments=arguments;
          this.continuationToken=null;
          if (needAck) {this.waitAck();}
        }

        public Message(ActorReference source, Object target, Object methodName, Object[] arguments, Token synchronizationToken, Token continuationToken, boolean requireAck) {
          this(source,target,methodName,arguments,synchronizationToken, continuationToken, requireAck, true);
        }

	/**
	 * These are the constructors for message.  The second is used if the message
	 * is part of a join continuation.  The constructor discovers which tokens are
	 * needed for it to be processed, and tries to resolve tokens which already have
	 * a user specified value. (ie done through code like: token x = 5;)
	 * This constructor allows for the target actor and method name to be tokens as
	 * well as the parameters.
	 */
	public Message(ActorReference source, Object target, Object methodName, Object[] arguments, Token synchronizationToken, Token continuationToken, boolean requireAck, boolean isCallByValue) {
          Token currentJoinToken=null;
          ActorReference targetActor;

          if (requireAck) {
                  if (target instanceof salsa.resources.EnvironmentalService) {
                    needAck = false;
                  }
                  else if (!source.equals(target)) {
                    needAck = true;
                  }
                  else needAck=false;
                } else {needAck=false;}

		this.messageId = ServiceFactory.getNaming().getUniqueMessageId();
		if (source!=null) {this.source = new WeakReference(source);}
                else {this.source=null;}


		/**
		 *	The target actor can be either a token or an actor
		 *	reference, if it is an actor reference we do nothing
		 *	special.
		 */
		if (target instanceof ActorReference) {
			this.target = new WeakReference((ActorReference)target);
			targetActor = this.target;
		} else {
			Token targetToken = (Token)target;
			Object value = targetToken.getValue();
			if (value != null) this.target = new WeakReference((ActorReference)value);

			if (this.target == null) {
//requiredTokens++;
				/**
				 *	Here we add the source actor as the target for this token, because
				 *	when a message gets sent with an unknown target actor, it gets placed
				 *	in its source's mailbox until that actor has been resolved.
				 */
				targetActor = source;
				targetToken.addTarget( targetActor, messageId, -1 );
			} else {
				targetActor = this.target;
			}
		}

                /**
                 * To handle the statement: join{...} @join{...}@....
                 * The system need to map it into the following statement:
                 *   join{...}@dummy()@join{....}@.....
                 */
                if (continuationToken!=null && continuationToken.isJoinDirector() &&
                    synchronizationToken!=null && synchronizationToken.isJoinDirector()) {
                  Token connectionToken=synchronizationToken.getJoinJoinToken();
                  if (connectionToken==null){
                    Object sourceActor = ServiceFactory.getNaming().getSourceActor(this.
                        source);
                    if (sourceActor != null && sourceActor instanceof UniversalActor.State) {
                      connectionToken = new Token("",targetActor.getID());
                      Object _arguments[] = {};
                      Message message = new Message(source, targetActor, "isDead",
                                                    _arguments, synchronizationToken,
                                                    connectionToken);
                      ((UniversalActor.State)sourceActor).__messages.add(message);
                      synchronizationToken.setJoinJoinToken(connectionToken);
                    }
                  }
                  synchronizationToken=connectionToken;
                }


                /**
                 * If there exists a continuation token,
                 *  create a new token that will send to the to the continuation token.
                 *  The target of the new token can be resolved until
                 *  the target of the continuation token is known.
                 */

                if (continuationToken!=null) {
                 if (continuationToken.isJoinDirector()) {
                   currentJoinToken = continuationToken;
                   continuationToken = new Token("",targetActor.getID());
                   currentJoinToken.addSync(targetActor.getID());
                   continuationToken.setisJoinInput(true);
                   currentJoinToken.addJoinInputTokens(continuationToken);
                 }else {
                   continuationToken.setOwner(targetActor.getID());
                 }
                }

		/**
		 *	The methodName can be either a token or a string,
		 *	if it is a string we do nothing special, if it is
		 *	a token we need to wait for it to be resolved.
		 */
		if (methodName instanceof String) {
			this.methodName = (String)methodName;
		} else {
			Token methodNameToken = (Token)methodName;
			Object value = methodNameToken.getValue();
			if (value != null) this.methodName = (String)value;
			else {
//requiredTokens++;
				methodNameToken.addTarget( targetActor, messageId, -2 );
			}
		}

		/**
		 *	We must find the tokens in the arguments, and set
		 *	up some information about them, so they can be
		 *	resolved at a later time.
		 */
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] instanceof Token) {
				Token token = (Token)arguments[i];
				arguments[i] = token.getValue();
				if (arguments[i] == null) {
                                  if (token.isJoinDirector()) {
                                    if (!token.isJoinResolved()) token.setJoinPosition(i);
                                      else {arguments[i]=new Object[0];}
                                  } else {
                                    if (continuationToken==null) {
                                      continuationToken=new Token("",targetActor.getID());
                                    }
                                    token.addTarget(targetActor, messageId, i);
                                    continuationToken.addSync(token.getOwner());
                                  }
				}
			}
		}

                if (needAck) {this.waitAck();}
                this.arguments=arguments;
                if (isCallByValue) {this.referenceSplitting();}

		/**
		 *	If there is a synchronization token, we add it to
		 *	this messages list of pending tokens as well.
		 */
		if (synchronizationToken != null && synchronizationToken.getValue() == null) {
			//requiredTokens++;
                        if (synchronizationToken.isJoinDirector()) {
                          if (!synchronizationToken.isResolved()) {
                            if (continuationToken == null) {
                              continuationToken = new Token("",targetActor.getID());
                            }
                            continuationToken.addSync(synchronizationToken.getSync());
                            continuationToken.setJoinData(synchronizationToken.getJoinData());
                            //continuationToken.setJoinPosition(synchronizationToken.getJoinPosition());
                            synchronizationToken.setOwner(targetActor.getID());
                            synchronizationToken.addJoinMessageId(this.messageId);
                          }
                        } else {
                          if (continuationToken==null) {
                            continuationToken = new Token("",targetActor.getID());
                          }
			  synchronizationToken.addTarget( targetActor, messageId, -3 );
                          continuationToken.addSync(synchronizationToken.getOwner());
                        }
		} else {

                }

		/**
		 *	If the results of this message invocation need to
		 * 	be sent anywhere, the continuationToken contains
		 *	the information about this.
		 */

		this.continuationToken = continuationToken;

	}

	/**
	 *	The following methods return a human readable String representation
	 *	of this message.
	 */
	public String toString() {
		String description = methodName + "(";

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] == null) {
				description += "null";
			} else {
				description += arguments[i].getClass().getName();
			}
			if (i+1 != arguments.length) description += ", ";
		}
		description += "), target: ";

		if (target != null) description += target.toString();
		else description += "null";

		description += ", source: ";

		if (source != null) description += source.toString();
		else description += "null";

		return description;
	}

    /*****
     * GC code section:
     * by WeiJen
     *****/

        public void referenceSplitting() {
          byte[] serializedArguments;
          if (arguments==null || arguments.length==0) {
            return;
          }
          if (this.methodName.equals("addActor")) {return;}

          try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream( bos);
            outStream.writeObject(arguments);
            outStream.flush();
            serializedArguments=bos.toByteArray();
            outStream.close();
            bos.close();
          }
          catch (Exception e) {System.err.println("Message Class, referenceSplitting() method: Error on serializing method arguments:"+e); return;}

          try {
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedArguments);
            GCObjectInputStream inStream;
            inStream = new GCObjectInputStream(bis,GCObjectInputStream.MUTE_GC,source,target);
            arguments= (Object[]) inStream.readObject();
            inStream.close();
            bis.close();
            refSummary=inStream.getRefSummary();
            hasActorReferenceArgs=(refSummary.size()>0);
          }
          catch (Exception e) {System.err.println("Message Class, referenceSplitting() method:Error on deserializing method arguments:"+e); }
        }

        public	Object[]	getArguments()	{return arguments;}

	    public void setArguments(Object[] args) { arguments = args; }

        //*********************************************************
        //This method is invoked while a message is about processing
        //*********************************************************
        public void activateArgsGC(UniversalActor.State sourceActor) {
          byte[] serializedArguments;
          if (arguments==null) {return;}
          if (!hasActorReferenceArgs) {return;}
          if (this.methodName.equals("addActor")) {return;}
          try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream( bos);
            outStream.writeObject(arguments);
            outStream.flush();
            serializedArguments=bos.toByteArray();
            outStream.close();
            bos.close();
          }
          catch (Exception e) {System.err.println("Message Class, getArguments() method: Error on serializing method arguments:"+e); return;}

          try {
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedArguments);
            GCObjectInputStream inStream = new GCObjectInputStream(bis,GCObjectInputStream.ACTIVATE_GC,target,source);
            arguments= (Object[]) inStream.readObject();
            refSummary=inStream.getRefSummary();

            //register forward references
            for (int i=0;i<refSummary.size();i++) {
              sourceActor.getActorMemory().getForwardList().putReference(((String)(refSummary.get(i))));
            }
            inStream.sendMails();

            inStream.close();
            bis.close();
          }
          catch (Exception e) {System.err.println("Message Class, activateArgsGC() method:Error on deserializing method arguments:"+e); }
          return;
        }

        public void waitAck() {
          if (!needAck) {return;}
          if (this.target!=null) {
            String targetRefStr=null;
            String sourceRefStr=null;
            if (this.target.getUAN()!=null) {targetRefStr=this.target.getUAN().toString();}
            else if (this.target.getUAL()!=null) {targetRefStr=this.target.getUAL().toString();}

            //increase the expected ack number!
             Object sourceActor=ServiceFactory.getNaming().getSourceActor(this.source);
             if (sourceActor!=null && sourceActor instanceof UniversalActor.State) {
               if (this.target.getUAN()!=null) {targetRefStr=this.target.getUAN().toString();}
               else if (this.target.getUAL()!=null) {targetRefStr=this.target.getUAL().toString();}
               if (targetRefStr!=null) {
                 ( (UniversalActor.State) sourceActor).waitAck(targetRefStr);
               }
             }
          }
        }
}
