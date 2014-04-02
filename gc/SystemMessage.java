package gc;
import salsa.language.Message;
/**
 * <p>Title: System Message Interface</p>
 * <p>Description: All system messages wll not get any ack message.
 *                 notice that an ack message is also a system message</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: wwc</p>
 * @author WeiJen
 * @version 1.0
 */

public interface SystemMessage {
  public String getMethodName();
  public WeakReference getTarget();
  public WeakReference getSource();

  //the target address (UAN or UAL) rpresented as a string
  //public String getArgument();

  //the total number of instances of the first argument
  //public int getSecondArgument();
  //public String getThirdArgument();
  public void setUAN(salsa.naming.UAN uan);
  public void setUAL(salsa.naming.UAL ual);
  public Message castToMessage();
}