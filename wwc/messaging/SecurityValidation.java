package wwc.messaging;

/**
 * <p>Title: SecurityValidation</p>
 * <p>Description: this class is used by a theater to validate actors' accesses</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

import salsa.language.ActorReference;
import java.util.Hashtable;

public class SecurityValidation {
  private Hashtable mobiles = new Hashtable();


  public SecurityValidation() {
  }

  public synchronized boolean checkSecurityEntry(String ref) {
    try{
      return mobiles.containsKey(ref);
    }catch (Exception e) {return false;}
  }

  //only allow actors with UAN to register
  public synchronized void registerSecurityEntry(String ref) {
    if (ref!=null) {
      mobiles.put(ref,ref);
    }
  }

  public synchronized void removeSecurityEntry(String ref) {
    if (ref!=null) {
      mobiles.remove(ref);
    }
  }

}