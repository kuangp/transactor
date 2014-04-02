package gc.serverGC;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
/**
 * <p>Title: Virtual actor For virtual migration</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class VirtualActor implements java.io.Serializable{
  public String name;
  public boolean pseudoRoot;
  public Object[] fList;
  public Object[] iList;
  transient public boolean mark=false;

  public VirtualActor(String name,boolean isPseudoRoot,Hashtable forwardList,Hashtable inverseList) {
    this.name=name;
    pseudoRoot=isPseudoRoot;
    Vector key=new Vector();
    if (forwardList!=null) {
      for (Enumeration e = forwardList.keys(); e.hasMoreElements(); ) {
        key.addElement(e.nextElement());
      }
      fList = key.toArray();
      key.clear();
    } else {
      Object[] newfList={};
      fList=newfList;
    }
    for (Enumeration e = inverseList.keys();  e.hasMoreElements(); ) {
      key.addElement(e.nextElement());
    }
    iList=key.toArray();
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
          out.defaultWriteObject();
          out.flush();
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
          in.defaultReadObject();
          mark=false;
  }

}