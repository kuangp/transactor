package gc;

import java.io.*;
import java.lang.reflect.*;
import salsa.language.UniversalActor;
/**
 * <p>Title: GC ObjectOutputStream</p>
 * <p>Description:This class is used for object serialization.
 *    All Actor References are traced. Some information is
 *    inserted during serialization.
 *    The function replaceObject filters all UniversalActor objects,
 *    and mute all active actor references.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: wwc</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class GCObjectOutputStream extends ObjectOutputStream {

  public static int FORCE_WEAK=1;
  public static int ACTIVATE_GC=2;

  private int operationType=0;

  public GCObjectOutputStream( int opType) throws IOException, SecurityException {
    super(new GCDummyOutputStream());
    enableReplaceObject(true);
    operationType=opType;
  }

  public Object replaceObject(Object targetObj) throws IOException {
    if (targetObj instanceof UniversalActor) {
      if (operationType==this.FORCE_WEAK) {return muteGC((UniversalActor)targetObj);}
      else if (operationType==this.ACTIVATE_GC) {return activateGC((UniversalActor)targetObj);}
    }
    return targetObj;
  }

  private Object activateGC(UniversalActor ref) {
    ref.toActivateGCState();
    return ref;
  }

  private Object muteGC(UniversalActor ref) {
    ref.toStopGCSinkState();
    return ref;
  }
}