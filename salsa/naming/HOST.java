package salsa.naming;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class HOST extends UAL{

  public HOST(String hostName) {
    super("rmsp://"+hostName+"/");
  }

}