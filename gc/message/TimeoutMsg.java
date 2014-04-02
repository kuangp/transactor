package gc.message;

/**
 * <p>Title: TimeoutMsg</p>
 * <p>Description: Timeout message to awaken some actor with timeout </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC </p>
 * @author WeiJen
 * @version 1.0
 */

import gc.*;
import salsa.language.ActorReference;
import salsa.naming.UAL;
import salsa.naming.UAN;

public class TimeoutMsg implements SystemMessage, java.io.Serializable{
  private WeakReference target;
  private String arg1="";
  private int arg2=-1;
  public TimeoutMsg(ActorReference toSend) {
    target=new WeakReference(toSend);
  }

  public TimeoutMsg(ActorReference toSend,String arg1,int arg2) {
    target=new WeakReference(toSend);
    this.arg1=arg1;
    this.arg2=arg2;
  }


  public String getMethodName() {return "timeoutMsg";}
  public WeakReference getTarget() {return target;}
  public WeakReference getSource() {
    return target;
  }
  //public String getArgument() {return arg1;}
  //public int getSecondArgument() {return arg2;}
  //public String getThirdArgument() {return "";}

  public void setUAN(UAN uan) {  target.setUAN(uan); }
  public void setUAL(UAL ual) {target.setUAL(ual); }
  public salsa.language.Message castToMessage() {
    Object[] args={};
    return new salsa.language.Message(null,target,"timeoutMsg",args,false);
  }
}
