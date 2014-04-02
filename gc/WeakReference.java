/**
 * <p>Title: WeakReference</p>
 * <p>Description: This class only contains necessary information of an
 *      actor reference. Used in Message class for storing 'target' and
 *      'source'. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: wwc</p>
 * @author WeiJen Wang
 * @version 1.0
 */


package gc;

import java.io.*;
import salsa.language.exceptions.SalsaException;
import salsa.language.*;
import salsa.naming.UAL;
import salsa.naming.UAN;

import salsa.language.Message;
import java.lang.reflect.Method;

import java.util.Hashtable;
import java.util.Vector;

import salsa.messaging.TheaterService;
import salsa.messaging.TransportService;
import salsa.naming.NamingService;
import salsa.naming.UAL;
import salsa.naming.UAN;
import salsa.naming.MalformedUALException;
import salsa.naming.MalformedUANException;

import salsa.resources.OutputService;
import salsa.resources.ErrorService;
import salsa.resources.SystemService;




public class WeakReference implements ActorReference, java.io.Serializable{
  private UAN uan;
  private UAL ual;
  private transient TransportService transportService = ServiceFactory.getTransport();
  private transient NamingService namingService = ServiceFactory.getNaming();

  /*public UniversalActor Cast2UniversalActor() {
    if (uan!=null) {
      return new UniversalActor(uan);
    } if (ual!=null) {
      return new UniversalActor(ual);
    }
    return null;
  }*/

  public static WeakReference parseWeakReference(String name) {
    if (name.charAt(0)=='u') {return new WeakReference(new UAN(name),null);}
    else if (name.charAt(0)=='r') {return new WeakReference(null,new UAL(name));}
    return null;
  }

  public WeakReference(ActorReference ref) {
    this(ref.getUAN(),ref.getUAL());
  }

  public WeakReference (UAN _uan, UAL _ual) {
    if (_uan!=null) {uan=_uan;}
    else {uan=null;}
    if (_ual!=null) {ual=_ual;}
    else {ual=null;}
  }

  public String key() {
    if (uan!=null) return uan.toString();
    return ual.toString();
  }

  public UAN getUAN() {return uan;}
  public void setUAN(UAN _uan) {uan=_uan;}
  public UAL getUAL() {return ual;}
  public void setUAL(UAL _ual) {ual=_ual;}

  public String getID() {
    if (uan != null) {
      return uan.toString();
    }
    else {
      if (ual != null) {
        return ual.toString();
      }
    }
    return "";
  }


  public UniversalActor construct() {
    throw new SalsaException("Constructing an actor from an illegal actor reference.");
    //return null;
  }
  public void createRemotely(UAN uan, UAL ual, String actorName,ActorReference sourceRef) {
    throw new SalsaException("Creating a remote actor from an illegal actor reference.");
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

  public void  send(SystemMessage message) {
    Object target = namingService.getTarget(this);
    if (target instanceof Actor) {
            try {
                    ((Actor)target).putMessageInMailbox(message);
            } catch (Exception e) {
                    System.err.println("Error putting System message in mailbox: ");
                    System.err.println("Exception: " + e);
                    System.err.println("\t Message: " + message);
                    e.printStackTrace();
            }
    } else if (target instanceof UAL) {
            this.ual = (UAL)target;
            transportService.send(message,this);
    } else {
        if (message instanceof gc.message.RemoveForwardRefMsg) {return;}
        else if (message instanceof gc.message.RemoveInverseRefMsg) {return;}
            System.err.println("System Message Sending Error:");
            System.err.println("\tActorReference: " + this.toString());
            System.err.println("\tMessage: " + message.toString());
            System.err.println("\tNamingService could not determine target for message.");
    }
  }


  public String toString() {
         String name = "";
         name +=  getClass().getName() + ": ";
         if (uan != null) name += uan.toString() + ", ";
         else name += "null, ";
         if (ual != null) name += ual.toString();
         else name += "null";
         return name;
 }

 public boolean equals(Object o) {
         if (o instanceof WeakReference) {
                 WeakReference ref = (WeakReference)o;

                 if (uan != null && ref.getUAN() != null) return uan.equals(ref.getUAN());
                 else if (ual != null && ref.getUAL() != null) return ual.equals(ref.getUAN());
         } if (o instanceof UniversalActor.State) {
                 UniversalActor.State ref = (UniversalActor.State)o;

                 if (this.uan != null && ref.getUAN() != null) return this.uan.equals(ref.getUAN());
                 else if (ual != null && ref.getUAL() != null) return ual.equals(ref.getUAN());
         } if (o instanceof UniversalActor) {
                 UniversalActor ref = (UniversalActor)o;
                 if (uan != null && ref.getUAN() != null) return uan.equals(ref.getUAN());
                 else if (ual != null && ref.getUAL() != null) return ual.equals(ref.getUAN());
         }
         return false;
 }

 private void writeObject(java.io.ObjectOutputStream out) {
         try {
                 out.writeObject(uan);
                 out.writeObject(ual);
                 out.flush();
         } catch (IOException e) {
                 System.err.println("Error during WeakReference serialization: ");
                 System.err.println("\tBy WeakReference: " + toString());
                 System.err.println("\tException: " + e);
         }
 }

 private void readObject(java.io.ObjectInputStream in) {
         try {
                 uan = (UAN)in.readObject();
                 ual = (UAL)in.readObject();
         } catch (Exception e) {
           System.err.println("Error during WeakReference deserialization: ");
           System.err.println("\tBy WeakReference: " + toString());
           System.err.println("\tException: " + e);

         }
         //get new instances of the services used by the Actor Reference
         transportService = ServiceFactory.getTransport();
         namingService = ServiceFactory.getNaming();
 }

}