/**********
 * GC support: public void process(SystemMessage msg)
 */


package salsa.language;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import java.util.Vector;
import java.util.Hashtable;

import salsa.naming.UAN;
import salsa.naming.UAL;

import salsa.resources.OutputService;
import salsa.resources.ErrorService;
import gc.SystemMessage;
import gc.WeakReference;

public class Placeholder implements Actor {
	private UAN __uan;
	private UAL __ual;
	public	UAN getUAN()	{ return __uan; }
	public	UAL getUAL()	{ return __ual; }

	public Placeholder(UAN uan, UAL ual) {
		__uan = uan;
		__ual = ual;
RunTime.createdUniversalActor();
	}

	public void setUAL(UAL ual) {
		System.err.println("SALSA System Error:");
		System.err.println("\tPlaceholder attempted to setUAL, which is not allowed");
		System.err.println("Actor: " + toString());
	}
        public void process(SystemMessage message) {
                System.err.println("SALSA System Error:");
                System.err.println("\tPlaceholder attempted to process a system message");
                System.err.println("\tNamed: " + toString());
        }
	public void process(Message message) {
		System.err.println("SALSA System Error:");
		System.err.println("\tPlaceholder attempted to process a message");
		System.err.println("\tNamed: " + toString());
	}
	public void destroy() {
		System.err.println("SALSA System Error:");
		System.err.println("\tAttempted to destroy a placeholder: " + toString());
	}
	public void start() {
		System.err.println("SALSA System Error:");
		System.err.println("\tPlaceholder attempted to start processing messages");
		System.err.println("\tNamed: " + toString());
	}
	public void sendGeneratedMessages() {
		System.err.println("SALSA System Error:");
		System.err.println("\tPlaceholder attempted to send generated messages (when none could be generated)");
		System.err.println("\tNamed: " + toString());
	}
	public Method[] getMatches(String match) {
		System.err.println("SALSA System Error:");
		System.err.println("\tPlaceholder attempted to get matches, when placeholder should be doing nothing.");
		System.err.println("\tNamed: " + toString());
		return null;
	}
	public void addMethod(String name, Method method) {
		System.err.println("SALSA System Error:");
		System.err.println("\tPlaceholder attempted to add a method for processing, when placeholder should be doing nothing.");
		System.err.println("\tNamed: " + toString());
	}
	public void replace(Placeholder placeholder) {
		System.err.println("SALSA System Error:");
		System.err.println("\tTrying to replace a placeholder with another placeholder.");
		System.err.println("\tNamed: " + toString());
	}
	public void updateSelf(ActorReference actorReference) {
		System.err.println("SALSA System Error:");
		System.err.println("\tTrying to invoke updateself on a placeholder.");
		System.err.println("\tNamed: " + toString());
	}

	public Vector mailbox = new Vector();
        boolean dead=false;

	public synchronized void putMessageInMailbox(Message message) {
//System.out.println("put message in PH#####################");
//System.out.println("              " + message.getMethodName()+", to "+message.getTarget());
          if (dead) {
            WeakReference self = new WeakReference(__uan, null);
            self.send(message);
          }
          else {mailbox.addElement(message);}
	}

        public synchronized void putMessageInMailbox(SystemMessage message) {
//System.out.println("put SYSmessage in PH#####################");
//System.out.println("              " + message.getMethodName()+", to "+message.getTarget());
          if (dead) {
            WeakReference self = new WeakReference(__uan, null);
            self.send(message);
          }
          else {mailbox.addElement(message);}
        }


	public synchronized void sendAllMessages() {
                WeakReference self = new WeakReference(__uan, null);
                Message sendPlaceholderMsg;
                  Object[] args = { mailbox.toArray()};
                  sendPlaceholderMsg = new Message(null, self, "getPlaceholderMsg",args, false);

                self.send(sendPlaceholderMsg);
//System.out.println("send all messages in PH################,size="+mailbox.size());
                mailbox.clear();
                dead=true;
	}

	public String toString() {
		String name = "";
		name +=  getClass().getName() + ": ";
		if (__uan != null) name += __uan.toString() + ", ";
		else name += "null, ";
		if (__ual != null) name += __ual.toString();
		else name += "null";
		return name;
	}

	public boolean equals(Object o) {
		if (o instanceof ActorReference) {
			ActorReference ref = (ActorReference)o;

			if (__uan != null && ref.getUAN() != null) return __uan.equals(ref.getUAN());
			else if (__ual != null && ref.getUAL() != null) return __ual.equals(ref.getUAN());
		} if (o instanceof Actor) {
			Actor ref = (Actor)o;

			if (__uan != null && ref.getUAN() != null) return __uan.equals(ref.getUAN());
			else if (__ual != null && ref.getUAL() != null) return __ual.equals(ref.getUAN());
		}
		return false;
	}

        public void finalize() {
          WeakReference self = new WeakReference(__uan, null);
          int i=this.mailbox.size();
          if (i>0) {
            for (i=0;i<mailbox.size();i++) {
              Object mail=mailbox.get(i);
              if (mail instanceof Message) {
                //self.send( (Message)mail);
                Message xmail=(Message)mail;
System.out.println(xmail.getMethodName()+", to "+xmail.getTarget());
              } else {
                //self.send((SystemMessage)mail);
                SystemMessage xmail=(SystemMessage)mail;
System.out.println(xmail.getMethodName()+", to "+xmail.getTarget());

              }
            }
            System.err.println("**************************************");
            System.err.println("Message Loss: resending "+ i + " messages!");
            System.err.println("**************************************");
          }
        }
}
