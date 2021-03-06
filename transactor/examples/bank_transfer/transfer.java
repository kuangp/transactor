package transactor.examples.bank_transfer;

// Import declarations generated by the SALSA compiler, do not modify.
import java.io.IOException;
import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import salsa.language.Actor;
import salsa.language.ActorReference;
import salsa.language.Message;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import gc.WeakReference;
import salsa.language.Token;
import salsa.language.exceptions.*;
import salsa.language.exceptions.CurrentContinuationException;

import salsa.language.UniversalActor;

import salsa.naming.UAN;
import salsa.naming.UAL;
import salsa.naming.MalformedUALException;
import salsa.naming.MalformedUANException;

import salsa.resources.SystemService;

import salsa.resources.ActorService;

// End SALSA compiler generated import delcarations.

import transactor.language.*;
import java.util.*;

public class transfer extends Transactor {
	public static void main(String args[]) {
		UAN uan = null;
		UAL ual = null;
		if (System.getProperty("uan") != null) {
			uan = new UAN( System.getProperty("uan") );
			ServiceFactory.getTheater();
			RunTime.receivedUniversalActor();
		}
		if (System.getProperty("ual") != null) {
			ual = new UAL( System.getProperty("ual") );

			if (uan == null) {
				System.err.println("Actor Creation Error:");
				System.err.println("	uan: " + uan);
				System.err.println("	ual: " + ual);
				System.err.println("	Identifier: " + System.getProperty("identifier"));
				System.err.println("	Cannot specify an actor to have a ual at runtime without a uan.");
				System.err.println("	To give an actor a specific ual at runtime, use the identifier system property.");
				System.exit(0);
			}
			RunTime.receivedUniversalActor();
		}
		if (System.getProperty("identifier") != null) {
			if (ual != null) {
				System.err.println("Actor Creation Error:");
				System.err.println("	uan: " + uan);
				System.err.println("	ual: " + ual);
				System.err.println("	Identifier: " + System.getProperty("identifier"));
				System.err.println("	Cannot specify an identifier and a ual with system properties when creating an actor.");
				System.exit(0);
			}
			ual = new UAL( ServiceFactory.getTheater().getLocation() + System.getProperty("identifier"));
		}
		RunTime.receivedMessage();
		transfer instance = (transfer)new transfer(uan, ual,null).construct();
		gc.WeakReference instanceRef=new gc.WeakReference(uan,ual);
		{
			Object[] _arguments = { args };

			//preAct() for local actor creation
			//act() for remote actor creation
			if (ual != null && !ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {instance.send( new Message(instanceRef, instanceRef, "act", _arguments, false) );}
			else {instance.send( new Message(instanceRef, instanceRef, "preAct", _arguments, false) );}
		}
		RunTime.finishedProcessingMessage();
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new transfer(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return transfer.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new transfer(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return transfer.getReferenceByLocation(new UAL(ual)); }
	public transfer(boolean o, UAN __uan)	{ super(false,__uan); }
	public transfer(boolean o, UAL __ual)	{ super(false,__ual); }
	public transfer(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public transfer(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public transfer(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public transfer()		{  }
	public transfer(UAN __uan, UAL __ual, Object obj) {
		//decide the type of sourceActor
		//if obj is null, the actor must be the startup actor.
		//if obj is an actorReference, this actor is created by a remote actor

		if (obj instanceof UniversalActor.State || obj==null) {
			  UniversalActor.State sourceActor;
			  if (obj!=null) { sourceActor=(UniversalActor.State) obj;}
			  else {sourceActor=null;}

			  //remote creation message sent to a remote system service.
			  if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			    WeakReference sourceRef;
			    if (sourceActor!=null && sourceActor.getUAL() != null) {sourceRef = new WeakReference(sourceActor.getUAN(),sourceActor.getUAL());}
			    else {sourceRef = null;}
			    if (sourceActor != null) {
			      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}
			      else if (__ual!=null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}

			      //update the source of this actor reference
			      setSource(sourceActor.getUAN(), sourceActor.getUAL());
			      activateGC();
			    }
			    createRemotely(__uan, __ual, "transactor.examples.bank_transfer.transfer", sourceRef);
			  }

			  // local creation
			  else {
			    State state = new State(__uan, __ual);

			    //assume the reference is weak
			    muteGC();

			    //the source actor is  the startup actor
			    if (sourceActor == null) {
			      state.getActorMemory().getInverseList().putInverseReference("rmsp://me");
			    }

			    //the souce actor is a normal actor
			    else if (sourceActor instanceof UniversalActor.State) {

			      // this reference is part of garbage collection
			      activateGC();

			      //update the source of this actor reference
			      setSource(sourceActor.getUAN(), sourceActor.getUAL());

			      /* Garbage collection registration:
			       * register 'this reference' in sourceActor's forward list @
			       * register 'this reference' in the forward acquaintance's inverse list
			       */
			      String inverseRefString=null;
			      if (sourceActor.getUAN()!=null) {inverseRefString=sourceActor.getUAN().toString();}
			      else if (sourceActor.getUAL()!=null) {inverseRefString=sourceActor.getUAL().toString();}
			      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}
			      else if (__ual != null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}
			      else {sourceActor.getActorMemory().getForwardList().putReference(state.getUAL());}

			      //put the inverse reference information in the actormemory
			      if (inverseRefString!=null) state.getActorMemory().getInverseList().putInverseReference(inverseRefString);
			    }
			    state.updateSelf(this);
			    ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);
			    if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
			  }
		}

		//creation invoked by a remote message
		else if (obj instanceof ActorReference) {
			  ActorReference sourceRef= (ActorReference) obj;
			  State state = new State(__uan, __ual);
			  muteGC();
			  state.getActorMemory().getInverseList().putInverseReference("rmsp://me");
			  if (sourceRef.getUAN() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAN());}
			  else if (sourceRef.getUAL() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAL());}
			  state.updateSelf(this);
			  ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(),state);
			  if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
		}
	}

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends Transactor.State {
		public transfer self;
		public void updateSelf(ActorReference actorReference) {
			((transfer)actorReference).setUAL(getUAL());
			((transfer)actorReference).setUAN(getUAN());
			self = new transfer(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public void preAct(String[] arguments) {
			getActorMemory().getInverseList().removeInverseReference("rmsp://me",1);
			{
				Object[] __args={arguments};
				self.send( new Message(self,self, "act", __args, null,null,false) );
			}
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "transactor.examples.bank_transfer.transfer$State" );
			addMethodsForClasses();
		}

		public void construct() {}

		public void process(Message message) {
			Method[] matches = getMatches(message.getMethodName());
			Object returnValue = null;
			Exception exception = null;

			if (matches != null) {
				if (!message.getMethodName().equals("die")) {activateArgsGC(message);}
				for (int i = 0; i < matches.length; i++) {
					try {
						if (matches[i].getParameterTypes().length != message.getArguments().length) continue;
						returnValue = matches[i].invoke(this, message.getArguments());
					} catch (Exception e) {
						if (e.getCause() instanceof CurrentContinuationException) {
							sendGeneratedMessages();
							return;
						} else if (e instanceof InvocationTargetException) {
							sendGeneratedMessages();
							exception = (Exception)e.getCause();
							break;
						} else {
							continue;
						}
					}
					sendGeneratedMessages();
					currentMessage.resolveContinuations(returnValue);
					return;
				}
			}

			System.err.println("Message processing exception:");
			if (message.getSource() != null) {
				System.err.println("\tSent by: " + message.getSource().toString());
			} else System.err.println("\tSent by: unknown");
			System.err.println("\tReceived by actor: " + toString());
			System.err.println("\tMessage: " + message.toString());
			if (exception == null) {
				if (matches == null) {
					System.err.println("\tNo methods with the same name found.");
					return;
				}
				System.err.println("\tDid not match any of the following: ");
				for (int i = 0; i < matches.length; i++) {
					System.err.print("\t\tMethod: " + matches[i].getName() + "( ");
					Class[] parTypes = matches[i].getParameterTypes();
					for (int j = 0; j < parTypes.length; j++) {
						System.err.print(parTypes[j].getName() + " ");
					}
					System.err.println(")");
				}
			} else {
				System.err.println("\tThrew exception: " + exception);
				exception.printStackTrace();
			}
		}

		public void act(String[] args) {
			bankaccount savings = ((bankaccount)new bankaccount(new UAN("uan://localhost/savings"),this).construct(100));
			bankaccount checking = ((bankaccount)new bankaccount(new UAN("uan://localhost/checking"),this).construct(100));
			teller atm = ((teller)new teller(new UAN("uan://localhost/teller"),this).construct());
			pinger acct_pinger = ((pinger)new pinger(new UAN("uan://localhost/pinger"),this).construct());
			{
				Token token_2_0 = new Token();
				Token token_2_1 = new Token();
				Token token_2_2 = new Token();
				Token token_2_3 = new Token();
				Token token_2_4 = new Token();
				Token token_2_5 = new Token();
				Token token_2_6 = new Token();
				Token token_2_7 = new Token();
				Token token_2_8 = new Token();
				Token token_2_9 = new Token();
				Token token_2_10 = new Token();
				Token token_2_11 = new Token();
				Token token_2_12 = new Token();
				Token token_2_13 = new Token();
				Token token_2_14 = new Token();
				Token token_2_15 = new Token();
				Token token_2_16 = new Token();
				Token token_2_17 = new Token();
				Token token_2_18 = new Token();
				Token token_2_19 = new Token();
				Token token_2_20 = new Token();
				Token token_2_21 = new Token();
				Token token_2_22 = new Token();
				Token token_2_23 = new Token();
				Token token_2_24 = new Token();
				Token token_2_25 = new Token();
				Token token_2_26 = new Token();
				// acct_pinger<-init(atm)
				{
					Object _arguments[] = { atm };
					Message message = new Message( self, acct_pinger, "init", _arguments, null, token_2_0 );
					__messages.add( message );
				}
				// savings<-initialize()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, savings, "initialize", _arguments, token_2_0, token_2_1 );
					__messages.add( message );
				}
				// checking<-initialize()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, checking, "initialize", _arguments, token_2_1, token_2_2 );
					__messages.add( message );
				}
				// atm<-initialize()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, atm, "initialize", _arguments, token_2_2, token_2_3 );
					__messages.add( message );
				}
				// standardOutput<-println("SAVINGS ACCOUNT: ")
				{
					Object _arguments[] = { "SAVINGS ACCOUNT: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_3, token_2_4 );
					__messages.add( message );
				}
				// savings<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, savings, "printData", _arguments, token_2_4, token_2_5 );
					__messages.add( message );
				}
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_5, token_2_6 );
					__messages.add( message );
				}
				// standardOutput<-println("CHECKING ACCOUNT: ")
				{
					Object _arguments[] = { "CHECKING ACCOUNT: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_6, token_2_7 );
					__messages.add( message );
				}
				// checking<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, checking, "printData", _arguments, token_2_7, token_2_8 );
					__messages.add( message );
				}
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_8, token_2_9 );
					__messages.add( message );
				}
				// standardOutput<-println("TELLER: ")
				{
					Object _arguments[] = { "TELLER: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_9, token_2_10 );
					__messages.add( message );
				}
				// atm<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, atm, "printData", _arguments, token_2_10, token_2_11 );
					__messages.add( message );
				}
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_11, token_2_12 );
					__messages.add( message );
				}
				// standardOutput<-println("PINGER: ")
				{
					Object _arguments[] = { "PINGER: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_12, token_2_13 );
					__messages.add( message );
				}
				// acct_pinger<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, acct_pinger, "printData", _arguments, token_2_13, token_2_14 );
					__messages.add( message );
				}
				// atm<-transfer(500, savings, checking, acct_pinger)
				{
					Object _arguments[] = { new Integer(500), savings, checking, acct_pinger };
					Message message = new Message( self, atm, "transfer", _arguments, token_2_14, token_2_15 );
					__messages.add( message );
				}
				// standardOutput<-println("====================================")
				{
					Object _arguments[] = { "====================================" };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_15, token_2_16 );
					Object[] _propertyInfo = { new Integer(5000) };
					message.setProperty( "delay", _propertyInfo );
					__messages.add( message );
				}
				// standardOutput<-println("SAVINGS ACCOUNT: ")
				{
					Object _arguments[] = { "SAVINGS ACCOUNT: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_16, token_2_17 );
					__messages.add( message );
				}
				// savings<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, savings, "printData", _arguments, token_2_17, token_2_18 );
					__messages.add( message );
				}
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_18, token_2_19 );
					__messages.add( message );
				}
				// standardOutput<-println("CHECKING ACCOUNT: ")
				{
					Object _arguments[] = { "CHECKING ACCOUNT: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_19, token_2_20 );
					__messages.add( message );
				}
				// checking<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, checking, "printData", _arguments, token_2_20, token_2_21 );
					__messages.add( message );
				}
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_21, token_2_22 );
					__messages.add( message );
				}
				// standardOutput<-println("TELLER: ")
				{
					Object _arguments[] = { "TELLER: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_22, token_2_23 );
					__messages.add( message );
				}
				// atm<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, atm, "printData", _arguments, token_2_23, token_2_24 );
					__messages.add( message );
				}
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_24, token_2_25 );
					__messages.add( message );
				}
				// standardOutput<-println("PINGER: ")
				{
					Object _arguments[] = { "PINGER: " };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_25, token_2_26 );
					__messages.add( message );
				}
				// acct_pinger<-printData()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, acct_pinger, "printData", _arguments, token_2_26, null );
					__messages.add( message );
				}
			}
		}
	}
}