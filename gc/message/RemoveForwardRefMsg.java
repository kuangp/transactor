package gc.message;

/**
 * <p>Title: Removing forward reference message</p>
 * <p>Description: RemoveForwardRefMsg message is one of the last messages that a
 *    user-defined actor reference sends</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */
import gc.*;
import salsa.language.ActorReference;


public class RemoveForwardRefMsg  implements SystemMessage, java.io.Serializable{
  //private String methodName="removeForwardRef";
  private WeakReference target;

  /*
   * argument stores the reference (represented as a string) to remove
   */
  private String argument;

  public RemoveForwardRefMsg(ActorReference _target, ActorReference toRemove) {
    target=new WeakReference(_target);
    if (toRemove.getUAN()!=null) {
      argument=toRemove.getUAN().toString();
    } else if (toRemove.getUAL()!=null) {
      argument=toRemove.getUAL().toString();
    }
  }
  public String getMethodName() {return "removeForwardRef";}
  public WeakReference getTarget() {return target;}
  public WeakReference getSource() {return null;}
  //public String getArgument() {return argument;}
  //public int getSecondArgument() {return 1;}
  //public String getThirdArgument() {return "";}

  public void setUAN(salsa.naming.UAN uan) {  target.setUAN(uan); }
  public void setUAL(salsa.naming.UAL ual) {target.setUAL(ual); }
  public salsa.language.Message castToMessage() {
    Object[] args={argument,new Integer(1)};
    return new salsa.language.Message(null,target,"removeForwardRef",args,false);
  }

}