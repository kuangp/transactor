package gc;


/**
 * <p>Title: Forward list for the actor reference</p>
 * <p>Description: Each actor has a forward list to keep tracking
 *      the status of its actor references.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */
import java.util.Hashtable;
import java.util.Enumeration;
import salsa.naming.URI;

public class ForwardList implements java.io.Serializable{
  private Hashtable  forwardList=null;
  private long totalExpectedAck;
  private String selfRef="";

  public ForwardList(String _selfRef) {
    //forwardList=new Hashtable();
    totalExpectedAck=0;
    selfRef=_selfRef;
  }

  public synchronized String getSelfRef() {return selfRef;}

  public synchronized Hashtable getRealForwardList() {return forwardList;}

  public synchronized int size() {
    if (this.forwardList==null) {return 0;}
    return forwardList.size();
  }

  public synchronized  void putReference(URI ref) {
    putReference(ref.toString());
  }

  public  synchronized void putReference(String ref) {
    if (ref.equals(selfRef))  return;
    if (this.forwardList==null) {this.forwardList=new Hashtable();}
//System.out.println("+++\nput "+this.selfRef+"->"+ref);
    ForwardListEntry fListEntry= (ForwardListEntry) forwardList.get(ref);
    if (fListEntry==null) {
      forwardList.put(ref.toString(),new ForwardListEntry());
    }
    else {
      fListEntry.incReferenceCounter();
    }
  }

  public synchronized void putReference(String ref,int count) {
        if (this.forwardList==null) {this.forwardList=new Hashtable();}
    ForwardListEntry fListEntry= (ForwardListEntry) forwardList.get(ref);
    if (fListEntry==null) {
      forwardList.put(ref.toString(),new ForwardListEntry(count,0));
    }
    else {
//System.out.println("+++\nput "+this.selfRef+"->"+ref);
      fListEntry.incReferenceCounter(count);
    }
  }

//removeReferenceImmediately() is used by ActorService

  public synchronized boolean removeReferenceImmediately(URI ref) {
    return removeReferenceImmediately(ref.toString());
  }

  public synchronized boolean removeReferenceImmediately(String ref) {
    if (ref.equals(selfRef))  return false;
    ForwardListEntry fListEntry= (ForwardListEntry) forwardList.get(ref);
//System.out.println("+++remove "+this.selfRef+"->"+ref);
    if (fListEntry==null) {
      System.err.println("Forward Reference List Error: Try to delete a non-existent reference: "+ref);
      return false;
    }
    else {
        fListEntry.decReferenceCounter();
        if (fListEntry.getReferenceCounter()==0) {forwardList.remove(ref);}
    }
    return true;
  }

  public  synchronized boolean removeReference(URI ref) {
//System.out.println("+++\nremove "+this.selfRef+"->"+ref);
    return removeReference(ref.toString());
  }

  public  synchronized boolean removeReference(String ref) {
    if (ref.equals(selfRef))  return false;
    ForwardListEntry fListEntry= (ForwardListEntry) forwardList.get(ref);
//System.out.println("+++remove "+this.selfRef+"->"+ref);
    if (fListEntry==null) {
      System.err.println("Forward Reference List Error: Try to delete a non-existent reference: "+ref);
      return false;
    }
    else {
      if (fListEntry.decReferenceCounter()==0) {
        return true;
      }
    }
    return false;
  }

  public  synchronized void waitAck(URI ref) {
    waitAck(ref.toString());
  }


  public  synchronized void waitAck(String ref) {
    if (ref.equals(selfRef))  return;
    if (this.forwardList==null) {this.forwardList=new Hashtable();}
    ForwardListEntry fListEntry= (ForwardListEntry) forwardList.get(ref);
    if (fListEntry==null) {
      //System.err.println("Forward Reference List Error: Try to use a non-existent reference: "+this.selfRef+ " -> " + ref);
      this.totalExpectedAck++;
      forwardList.put(ref,new ForwardListEntry(0,1) );
    }
    else {
      this.totalExpectedAck++;
      fListEntry.incExpectedCounter();
    }
//System.out.println("#####ack+#####: "+selfRef+" -> "+ ref);
  }

  public  synchronized void receiveAck(URI ref) {
    receiveAck(ref.toString());
  }


  public  synchronized void receiveAck(String ref) {
    if (ref.equals(selfRef))  return;
    ForwardListEntry fListEntry= (ForwardListEntry) forwardList.get(ref);
    if (fListEntry==null) {
      System.err.println("Forward Reference List Error: acknowledgement to a non-existent reference.");
      System.err.println(selfRef+" <--ack-- "+ref);
    }
    else {
      if (fListEntry.getExpectedCounter()==0) {
        System.err.println("Forward Reference List Error: acknowledgement to a deleted reference.");
        System.err.println(selfRef+" <--ack-- "+ref);
      }

      this.totalExpectedAck--;
      fListEntry.decExpectedCounter();
      //if (fListEntry.isDead() && !disableRemoving) {
      //  forwardList.remove(ref.toString());
      //}
//System.out.println("%%%%%ack-%%%%%: "+selfRef+" -> "+ ref);
    }
  }

  public synchronized boolean isPseudoRoot() {
    return totalExpectedAck!=0;
  }

  public synchronized void  clear() {
    if (this.forwardList==null) {
      forwardList.clear();
      this.totalExpectedAck = 0;
    }
  }

  public String toString() {
    String res="Forward list size="+forwardList.size();
    res=res+ "\nTotal Exp Ack:::"+this.totalExpectedAck ;
    for (Enumeration e = this.forwardList.keys() ; e.hasMoreElements() ;) {
      String key = (String) e.nextElement();
      ForwardListEntry flistEntry=(ForwardListEntry)this.forwardList.get(key);
      res=res+"\n\tID:"+key+","+flistEntry.toString();
    }
    return res;
  }
}