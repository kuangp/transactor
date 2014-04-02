package gc.message;

/**
 * <p>Title: Acknowledgement</p>
 * <p>Description: Acknowledgement message for each normal message</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC </p>
 * @author WeiJen
 * @version 1.0
 */

import gc.*;
import salsa.language.ActorReference;
import salsa.naming.UAL;
import salsa.naming.UAN;

public class Ack implements SystemMessage, java.io.Serializable{
  private WeakReference target;
  private String ackTarget;
  public Ack(ActorReference toSend,String ackTargetRef) {
    target=new WeakReference(toSend);
    ackTarget=ackTargetRef;
  }

  public String getMethodName() {return "receiveAck";}
  public WeakReference getTarget() {return target;}
  public WeakReference getSource() {
    if (ackTarget.charAt(0)=='u') {return new WeakReference(new UAN(ackTarget),null);}
    return new WeakReference(null,new UAL(ackTarget));
  }
  //public String getArgument() {return ackTarget;}
  //public int getSecondArgument() {return 1;}
  //public String getThirdArgument() {return "";}

  public void setUAN(UAN uan) {  target.setUAN(uan); }
  public void setUAL(UAL ual) {target.setUAL(ual); }
  public salsa.language.Message castToMessage() {
    Object[] args={ackTarget,new Integer(1)};
    return new salsa.language.Message(null,target,"receiveAck",args,false);
  }
}