package gc;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import gc.ActorSnapshot;
/**
 * <p>Title: Actor Memory</p>
 * <p>Description: This class simulates the memory and its operations</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class ActorMemory implements java.io.Serializable{

  //snapshot indicates that this actor is currently in GC
  transient private boolean snapshot=false;
  transient private boolean snapshotAGC=false;


  //active indicates that an actor is processing or has messages in its message box.
  transient private boolean active=false;
  transient private boolean activeAlternative;

  //fList stores information of its forward acquaintances and their related information
  private ForwardList fList;

  //iList stores information of its inverse acquaintances, and their related information
  private InverseList iList;

  //fMailboxList is a data structure to store forward references in the mail box
  private MailboxRefList fMailboxList;

  //iMailboxList stores information of its inverse acquaintances in the mail box,
  // and their related information
  private MailboxRefList iMailboxList;

  //locks an actor for waiting an incoming message.
  //private int lockCounter=0;
  transient private boolean islocked=false;
  transient private boolean ispending=false;

  //I am not sure "pendingMessages" is required or not
  //It is used to test if there exists any pending message
  //According to my experiments, it does not affect the result
  //However, it is theoretically necessary.
  transient Hashtable pendingMessages;

  public ActorMemory(String selfRef) {
    fList=new ForwardList(selfRef);
    iList=new InverseList(selfRef);
  }

  public void setPendingMessages(Hashtable pendingMessages) {
    this.pendingMessages=pendingMessages;
  }

  public synchronized boolean isPending() {
    if (snapshot) {return ispending || (pendingMessages.size()>0);}
    else {
      return (pendingMessages.size()>0);
    }
  }

  public synchronized ActorSnapshot castToActorSnapshot() {
    return new ActorSnapshot(fList.getSelfRef(),
                             isActive() ||  fList.isPseudoRoot(),
                             fList.getRealForwardList(),
                             iList.getRealInverseList());
  }

  public synchronized ActorSnapshot castToActorSnapshotAGC() {
    Hashtable flist=fList.getRealForwardList();
    Hashtable ilist=iList.getRealInverseList();
    if (flist!=null) {
      fMailboxList.getRefList().putAll(flist);
    }
    if (ilist!=null) {
      iMailboxList.getRefList().putAll(ilist);
    }
    return new ActorSnapshot(fList.getSelfRef(),
                             isActive() ||  fList.isPseudoRoot(),
                             fMailboxList.getRefList(),
                             iMailboxList.getRefList());
  }

  public synchronized MailboxRefList getForwardMailboxRefList() {return fMailboxList;}
  public synchronized MailboxRefList getInverseMailboxRefList() {return iMailboxList;}

  public synchronized ForwardList GCGetForwardList() {return fList;}
  public synchronized InverseList GCGetinverseList() {return iList;}

  public synchronized ForwardList getForwardList() {return fList;}

  public synchronized InverseList getInverseList() {
    if (snapshot) {
      try {
        this.islocked=true;
        wait();
      }
      catch (Exception e) {
        System.err.println("GC error at Class ActorMemory, method getInverseList():"+e);
      }
    }
    return iList;
  }


  /***During GC:
   *  1. An actor can only become unblocked, but cannot become blocked
   *  2. val is stored in the variable activeAlternative
   */
  public synchronized void setActive(boolean val) {
    if (snapshot) {
      if (val==true) {
        active = true;
        activeAlternative=true;
      } else {
        activeAlternative=false;
      }
    } else {active=val;}
  }

  public  synchronized boolean isActive() {
    return (active || isPending());
    //return active;
  }

  /***
   * If GC ends, set active=activeAlternative to restore its real value
   * If GC starts, set activeAlternative=active
   */
  public synchronized boolean startGC() {
    if (snapshot) return false;
    activeAlternative=active;
    snapshot=true;
    ispending=this.pendingMessages.size()>0;
    return snapshot;
  }

  public synchronized void endGC() {
    active=activeAlternative;
    snapshot=false;
    notifyAll();
  }

  public synchronized boolean startAGC() {
    if (snapshotAGC) return false;
    activeAlternative=active;
    snapshot=true;
    snapshotAGC=true;
    ispending=this.pendingMessages.size()>0;
    fMailboxList=new MailboxRefList(fList.getSelfRef());
    iMailboxList=new MailboxRefList(fList.getSelfRef());
    return snapshot;
  }

  public synchronized void endAGC() {
    active=activeAlternative;
    snapshot=false;
    snapshotAGC=false;
    fMailboxList=null;
    iMailboxList=null;
    notifyAll();
  }

  public  synchronized boolean getSnapshotBit() {return snapshot;}
  public  synchronized boolean getSnapshotBitAGC() {return snapshotAGC;}

  public synchronized String toString() {
    String res="[== " + fList.getSelfRef() + " ==], active="+isActive()+"\n";;
    res+=fList.toString()+"\n------------\n"+this.iList.toString();
    return res+"\n[-------------------]\n";
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException{
    out.defaultWriteObject();
    out.flush();
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException{
    in.defaultReadObject();
    active=false;
    islocked=false;
  }
}