package gc;

/**
 * <p>Title: Inverse list for inverse acquaintances</p>
 * <p>Description: Each actor has an inverse list. An actor is a candidate of
 *      garbage actors only if this list is empty. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen
 * @version 1.0
 */
import java.util.Hashtable;
import salsa.naming.URI;
import java.util.Enumeration;

public class InverseList implements java.io.Serializable{

  private Hashtable  inverseList;
  private String selfRef="";
  public InverseList(String _selfRef) {
    inverseList=new Hashtable();
    selfRef=_selfRef;
  }

  public synchronized Hashtable getRealInverseList() {
    return inverseList;
  }

  public  synchronized void putInverseReference(URI ref) {
    putInverseReference(ref.toString());
  }


  public  synchronized void putInverseReference(String ref) {
    if (ref.equals(selfRef)) return;
    InverseListEntry iListEntry = (InverseListEntry) inverseList.get(ref);

    // if there is no information for this inverse reference,
    // create an entry for it. Otherwise, increase its count
    if (iListEntry==null) {
      inverseList.put(ref.toString(),new InverseListEntry());
    }
    else {
      iListEntry.incReferenceCounter();
    }
  }

  public  synchronized void removeInverseReference(URI ref,int no) {
    removeInverseReference(ref.toString(),no);
  }

  public  synchronized void removeInverseReference(String ref,int no) {
    InverseListEntry iListEntry = (InverseListEntry) inverseList.get(ref);
    // if there is no information for this inverse reference,
    // the system must have a error. Otherwise, decrease its count
    if (iListEntry==null) {
      System.err.println("Inverse List error: try to remove a non-existent inverse reference entry");
      System.err.println(this.selfRef+" <- " + ref);
    }
    else {
      iListEntry.decReferenceCounter(no);
      if (iListEntry.isFree()) {
        inverseList.remove(ref.toString());
      }
    }
  }

  public  synchronized boolean isEmpty() {
    return (inverseList==null || inverseList.size()==0) ;
  }

  public  synchronized String toString() {
    String res="Inverse list size="+inverseList.size();
    for (Enumeration e = this.inverseList.keys() ; e.hasMoreElements() ;) {
      String key = (String) e.nextElement();
      InverseListEntry invlistEntry=(InverseListEntry)this.inverseList.get(key);
      res=res+"\n\tID:"+key+","+invlistEntry.toString();
    }
    return res;
  }

}