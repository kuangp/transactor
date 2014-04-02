package gc;

import java.io.*;
import java.util.Vector;
import salsa.language.UniversalActor;
import salsa.language.Message;
import salsa.language.ServiceFactory;
import salsa.language.ActorReference;
/**
 * <p>Title: GC Object Input Stream</p>
 * <p>Description: This class resolves objects serialized by
 *                 ObjectOutputStream, and start GC
 *                 synchronization protocol if necessary.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: wwc</p>
 * @author WeiJen
 * @version 1.0
 */

public class GCObjectInputStream extends ObjectInputStream {
  public static int SPLITTING_ONLY=0;
  public static int MUTE_GC=1;
  public static int ACTIVATE_GC=2;

  // "sourceRef" indicates the id string of the current actor which is executing this object
  // "sourceActor" means the current actor (not an ActorReference!)
  //
  private int operationType=0;
  private WeakReference sourceRef;
  private String initRefStr=null;
  private UniversalActor.State sourceActor=null;
  private Vector refSummary=null;
  private Vector mails=null;

  public GCObjectInputStream(InputStream in, int opType, WeakReference _sourceRef, WeakReference _initRef) throws IOException,SecurityException{
    super(in);
    operationType=opType;
    setSourceReference(_sourceRef);
    refSummary=new Vector();
    mails=new Vector();
    enableResolveObject(true) ;
    if (_initRef.getUAN()!=null){
      initRefStr = _initRef.getUAN().toString();
    } else {
      initRefStr = _initRef.getUAL().toString();
    }
  }

  //public GCObjectInputStream(InputStream in, WeakReference _sourceRef) throws IOException,SecurityException{
  //  this( in, 0, _sourceRef);
  //  operationType=MUTE_GC;
  //}

  //public GCObjectInputStream(InputStream in, WeakReference _sourceRef, WeakReference _initRef) throws IOException,SecurityException{
  //  this( in, 0, _sourceRef);
  //  if (_initRef.getUAN()!=null){
  //    initRefStr = _initRef.getUAN().toString();
  //  } else {
  //    initRefStr = _initRef.getUAL().toString();
  //  }
  //  operationType=ACTIVATE_GC;
  //}


  public void setSourceReference(WeakReference _ref) {
    sourceActor=getSourceActor(_ref);
    sourceRef=_ref;
  }

  public Vector getRefSummary() {return refSummary;}
	public Vector getMails() { return mails; }
	public void clearMails() { mails.clear(); }
  public void sendMails() {
    for (int i=0;i<mails.size();i++) {
      Message msg=(Message)mails.get(i);
      msg.getTarget().send(msg);
    }
    mails.clear();
  }


  /********
   * This function filters all actor references,
   * 1. case "in-transit to active": makes them GC active, and summarizes their information in
   *    refSummary. Lock the  target actor at this moment.
   * 2. case "active to in-transit": make them GC temporary silent, and summarizes their information in
   *    refSummary. Register the inverse acquaintance at this moment.
   * 3. Other case: it has the effect to create a copy only.
   ********/
  protected Object resolveObject(Object obj) {
    if (obj instanceof UniversalActor) {
      //case 1:
      //convert in-transit state to active GC state
      if (operationType==ACTIVATE_GC) {
         activateGCAndSendInvRefUnlockMsg((UniversalActor )obj);
      }

      //case 2:
      //convert active GC state to in-transit state
      else if (operationType==MUTE_GC) {
        //muteGCAndSendLockMsg((UniversalActor )obj);
        muteGCAndLockRef((UniversalActor )obj);
      }
    }
    return obj;
  }

  private void activateGCAndSendInvRefUnlockMsg(UniversalActor obj) {
    WeakReference ref=new WeakReference(obj);
    if ( obj.isInMessageState()) {
      //set the new owner of the references
      obj.setSource(sourceRef);
      obj.toActivateGCState();
      if ( obj.getUAN() != null) {
        refSummary.addElement( obj.getUAN().toString());
      }
      else {
        refSummary.addElement( obj.getUAL().toString());
      }
      if (sourceRef!=null) {
        if (sourceRef.getUAN()!=null) {
          Object[] args={sourceRef.getUAN().toString(),this.initRefStr};
          //ref.send(new Message(sourceRef,ref,"putInvRefAndUnlock",args,null,null));
          mails.addElement(new Message(sourceRef,ref,"putInvRefAndUnlock",args,null,null));
        } else if (sourceRef.getUAL()!=null) {
          Object[] args={sourceRef.getUAL().toString(),this.initRefStr};
          //ref.send(new Message(sourceRef,ref,"putInvRefAndUnlock",args,null,null));
          mails.addElement(new Message(sourceRef,ref,"putInvRefAndUnlock",args,null,null));
        }
      }
    }
  }


  private void muteGCAndLockRef(UniversalActor obj) {
    WeakReference ref=new WeakReference(obj);
    if ( obj.isInActiveGCState()) {
      obj.setSource(null);
      obj.toInMessageState();
      String objID="";
      if (obj.getUAN() != null) {
        objID=obj.getUAN().toString();
      }
      else {
        objID=obj.getUAL().toString();
      }
      refSummary.addElement(objID );

      if (objID!=null) {
        sourceActor.waitAck(objID);
      }
      if (this.initRefStr!=null) {
        sourceActor.waitAck(initRefStr);
      }
    }
  }

  private UniversalActor.State getSourceActor(ActorReference sourceRef) {
    Object sourceActor=ServiceFactory.getNaming().getSourceActor(sourceRef);
    if (sourceActor!=null && sourceActor instanceof UniversalActor.State) {
      return (UniversalActor.State)sourceActor;
    }
    return null;
  }


}