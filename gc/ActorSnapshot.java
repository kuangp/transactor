package gc;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

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

public class ActorSnapshot implements java.io.Serializable{
  public String name;
  //public boolean unblocked=false;
  public Object[] fList=null;
  public Object[] iList=null;

  //transient public boolean mark=false;
  public int mark;
  transient public static final int NOTMARKED=0;
  transient public static final int UNBLOCKED=1;
  transient public static final int NONLOCAL=2;
  transient public static final int ROOT=3;

  //for special marking algorithms
  transient public Object markObj=null;

  public ActorSnapshot(String name,boolean isUnblocked,Hashtable forwardList,Hashtable inverseList) {
    this.name=name;
    //unblocked=isUnblocked;
    if (isUnblocked) {mark=UNBLOCKED;}
    if (forwardList!=null) fList=forwardList.keySet().toArray();
    if (inverseList!=null) iList=inverseList.keySet().toArray();
  }

  public boolean isUnblocked() {return mark==UNBLOCKED;}

  public boolean isNonlocal() {return mark==NONLOCAL;}

  public boolean isNotMarked() {return mark==NOTMARKED;}

  public boolean isRoot() {return mark==ROOT;}

  public boolean isLive() {return mark==ROOT || mark==NONLOCAL;}

  public boolean isPotentiallyLive() {return mark==UNBLOCKED || mark==ROOT || mark==NONLOCAL;}

  public void markColor(int color) {mark=color;}

  public boolean isGarbageSuperSetOf(ActorSnapshot actor) {
    if (actor.mark!=UNBLOCKED) {return false;}
    for (int i=0;i<actor.iList.length;i++) {
      if (!findInvActor((String)actor.iList[i])) {return false;}
    }
    for (int i=0;i<actor.fList.length;i++) {
      if (!findForwardActor((String)actor.fList[i])) {return false;}
    }
    return true;
  }

  private boolean findInvActor(String name) {
    try {
      for (int i=0;i<iList.length;i++) {
        String targetName=(String)iList[i];
        if (targetName.equals(name)) {return true;}
      }
    } catch (Exception e) {}
    return false;
  }

  private boolean findForwardActor(String name) {
    try {
      for (int i=0;i<fList.length;i++) {
        String targetName=(String)fList[i];
        if (targetName.equals(name)) {return true;}
      }
    } catch (Exception e) {}
    return false;
  }

  public void showState() {
    System.out.print("(["+name+","+mark+"]:");
    try{
      for (int i = 0; i < fList.length; i++) {
        System.out.print("->"+(String)fList[i]+"  ");
      }
    }catch (Exception e) {}
    try{
      for (int i = 0; i < iList.length; i++) {
        System.out.print("<-"+(String)iList[i]+"  ");

      }
    }catch (Exception e) {}
    System.out.println(")");
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
          out.defaultWriteObject();
          out.flush();
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
          in.defaultReadObject();
  }

}
