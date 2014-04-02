package gc.message;

/**
 * <p>Title: Removing inverse reference message</p>
 * <p>Description: RemoveInverseRefMsg message is one of the last messages that a
 *    user-defined actor reference sends</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC </p>
 * @author WeiJen Wang
 * @version 1.0
 */

import gc.*;
import salsa.language.ActorReference;

public class RemoveInverseRefMsg implements SystemMessage, java.io.Serializable{
  //private String methodName="removeInverseRef";
  private WeakReference target;

  /*
   * argument stores the reference (represented as a string) to remove
   */
  private String argument;
  private int no;

  public RemoveInverseRefMsg(ActorReference _target, ActorReference toRemove) {
    target=new WeakReference(_target);
    if (toRemove.getUAN()!=null) {
      argument=toRemove.getUAN().toString();
    } else if (toRemove.getUAL()!=null) {
      argument=toRemove.getUAL().toString();
    }
    no=1;
  }

  public RemoveInverseRefMsg(ActorReference _target, String toRemove) {
    target=new WeakReference(_target);
    argument=toRemove;
    no=1;
  }

  public RemoveInverseRefMsg(ActorReference _target, ActorReference toRemove, int no) {
    this(_target,toRemove);
    this.no=no;
  }

  public RemoveInverseRefMsg(ActorReference _target, String toRemove, int no) {
    this(_target,toRemove);
    this.no=no;
  }

  public String getMethodName() {return "removeInverseRef";}
  public WeakReference getTarget() {return target;}
  public WeakReference getSource() {return null;}
  //public String getArgument() {return argument;}
  //public int getSecondArgument() {return no;}
  //public String getThirdArgument() {return "";}

  public void setUAN(salsa.naming.UAN uan) {  target.setUAN(uan); }
  public void setUAL(salsa.naming.UAL ual) {target.setUAL(ual); }
  public salsa.language.Message castToMessage() {
    Object[] args={argument,new Integer(no)};
    return new salsa.language.Message(null,target,"removeInverseRef",args,false);
  }
}
