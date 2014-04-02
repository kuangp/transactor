package gc;

/**
 * <p>Title: mailbox reference list for storing references in the mail box</p>
 * <p>Description: Each actor has a mailbox, an unsolved mailbox, and a resolveToken vector.
 *    traditional actor garbage collection needs to know the references in them</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: WWC</p>
 * @author WeiJen
 * @version 1.0
 */
import java.util.Hashtable;
import salsa.naming.URI;
import java.util.Enumeration;
import java.util.Vector;

public class MailboxRefList implements java.io.Serializable{

  private Hashtable refList;
  private String selfRef="";
  public MailboxRefList(String _selfRef) {
    refList=new Hashtable();
    selfRef=_selfRef;
  }

  public synchronized Hashtable getRefList() {
    return refList;
  }

  public  synchronized void putReference(URI ref) {
    putReference(ref.toString());
  }

  public  synchronized void putReference(String ref) {
    if (ref.equals(selfRef)) return;
    Integer data = (Integer) refList.get(ref);

    // if there is no information for this inverse reference,
    // create an entry for it. Otherwise, increase its count
    if (data==null) {
      refList.put(ref.toString(),new Integer(1));
    }
  }

  public synchronized void putReference(Vector refs) {
    if (refs==null) {return;}
    for (int i=0;i<refs.size();i++) {
      putReference((String)(refs.get(i)));
    }
  }

  public  synchronized void removeReference(String ref) {
    Object data = refList.get(ref);
    // if there is no information for this inverse reference,
    // the system must have a error. Otherwise, decrease its count
    if (data==null) {
      System.err.println("Inverse List error: try to remove a non-existent inverse reference entry");
      System.err.println(this.selfRef+" <- " + ref);
    }
    else {
        refList.remove(ref.toString());
    }
  }

  public  synchronized boolean isEmpty() {
    return (refList==null || refList.size()==0) ;
  }

  public  synchronized String toString() {
    String res="Reference list size="+refList.size();
    for (Enumeration e = this.refList.keys() ; e.hasMoreElements() ;) {
      String key = (String) e.nextElement();
      InverseListEntry invlistEntry=(InverseListEntry)this.refList.get(key);
      res=res+"\n\tID:"+key+","+invlistEntry.toString();
    }
    return res;
  }

}
