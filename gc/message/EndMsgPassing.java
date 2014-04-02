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

public class EndMsgPassing implements SystemMessage, java.io.Serializable{
  private WeakReference target;

  /*
   * argument stores the reference (represented as a string) to remove
   */
  private String targetActor;
  private String referencedActor;

  public EndMsgPassing(WeakReference MsgTarget, String targetActor, String referencedActor) {
    this.targetActor=targetActor;
    this.referencedActor=referencedActor;
    this.target=MsgTarget;
  }

  public String getMethodName() {return "endMsgPassing";}
  public WeakReference getTarget() {return target;}
  public WeakReference getSource() {return null;}
  //public String getArgument() {return targetActor;}
  //public int getSecondArgument() {return 0;}
  //public String getThirdArgument() {return referencedActor;}
  public void setUAN(salsa.naming.UAN uan) {  target.setUAN(uan); }
  public void setUAL(salsa.naming.UAL ual) {target.setUAL(ual); }
  public salsa.language.Message castToMessage() {
    Object[] args={targetActor,referencedActor};
    return new salsa.language.Message(null,target,"endMsgPassing",args,false);
  }
}
