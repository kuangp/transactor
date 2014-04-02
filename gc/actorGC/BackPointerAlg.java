package gc.actorGC;

/**
 * <p>Title: local actor garbage collector for traditional actor garbage collection</p>
 * <p>Description: The local gabage finder uses the back-pointer algorithm. There are
 *      three kinds of roots:
 *      1) Real roots such as Actor Services
 *      2) unblocked (active) mobile actors
 *      3) unblocked reachable actors with remote outgoing references and
 *         actors with remote incoming references. </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */



import java.lang.ClassCastException;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import wwc.naming.WWCNamingService;
import salsa.language.*;
import salsa.naming.UAL;
import salsa.naming.UAN;
import gc.*;
import gc.message.RemoveInverseRefMsg;
import salsa.messaging.TheaterService;

public class BackPointerAlg implements LocalCollector{

//the local naming service
  private WWCNamingService nameSvc;

  //the actors in the snapshot
  private Hashtable actorArray;

  //the internal data structure for mark and sweep
  private LinkedList BFSMemVector;

  //the temporary storage for the system massage "RemoveInverseRefMsg"
  private Vector removeInvRefTarget;

  //the garbage array identified by the Back Pointer Algorithm
  private Hashtable garbageArray;

  //snapshotTable: the table for the GC snapshot
  private SnapshotList snapshotTable;

  //nonLocalSnapshotTable: the table for pure non-local reachable snapshot actors.
  private SnapshotList nonLocalSnapshotTable;

  private Object globalGCTask=new Object();

  //the counter for the throughput of GC
  private int throughPut=0;

  private int nonlocalCount=0;

  //private String lock=null;

  private Vector actor2kill=new Vector();

  public class invTargetWrap {
    public String target;
    public String argument;
    int debt;
    public invTargetWrap(String target,String arg,int debt) {
      this.target=target;this.argument=arg;this.debt=debt;
    }
  }

  public BackPointerAlg() {
    try {
      nameSvc = (WWCNamingService) ServiceFactory.getNaming();
    } catch (ClassCastException e) {nameSvc=null;}
    actorArray=new Hashtable();
    BFSMemVector=new LinkedList();
    removeInvRefTarget=new Vector();
    garbageArray=new Hashtable();
    snapshotTable=new SnapshotList(ServiceFactory.getTheater().getLocation());
    nonLocalSnapshotTable=new SnapshotList(ServiceFactory.getTheater().getLocation());
    throughPut=0;
  }

  public synchronized ActorSnapshot isNonLocalActor(String actorID) {
      return nonLocalSnapshotTable.get(actorID);
  }

  public synchronized boolean isInLocalHost(String actorID) {
    Hashtable uanTable=nameSvc.getUANTable();
    if (uanTable.get(actorID)!=null) {return true;}
    else {
      Hashtable ualTable=nameSvc.getUALTable();
      if (ualTable.get(actorID) != null) {
        return true;
      }
    }
    return false;
  }

  public synchronized UAL getRemoteUAL(UAN uanData) {
    Hashtable cacheTable=nameSvc.getUANUALTable();
    UAL data=(UAL)cacheTable.get(uanData.toString());
    if (data!=null) {
      return data;
    }
    return null;
  }

  public synchronized boolean isGlobalGC() {
    //synchronized (globalGCTask) {
      return locked;
    //}
  }

  public synchronized void collect() {
    if (!locked) {
      //local garbage collection
      killing(); // terminate garbage actors
      throughPut = 0;
      formGroup(); // decide actors to be in GC
      System.gc();
      if (actorArray.size() == 0) {
        nonlocalCount=0;
        return;
      }
//System.out.println("=======1111111=========");
      processSystemMessages(); // processing system messages at this time
      startSnapshot();  // change actors' state to GC mode
      getSnapshot();    // put snapshots in snapshotTable
      endSnapshot();    // let actors back to normal
      snapshotMarkFromUnblocked(); // start to mark live actors
      nonlocalCount=0;
      snapshotGlobalRoot(); // identify global roots
      int ret=snapshotMarkFromRoots(); // mark actors that are reachable from global roots
      nonlocalCount+=ret;
      getSnapshotGarbageActorAndAcqInvRef();
      collectGarbage();
      snapshotFinalize();
      //if (nonlocalCount>0) {extractNonLocal();}
    } else {
      //global garbage collection
    }
  }

  public void extractNonLocal() {
      nonLocalSnapshotTable.clear();
      for (Enumeration e = snapshotTable.elements(); e.hasMoreElements(); ) {
        ActorSnapshot actor = (ActorSnapshot) e.nextElement();
        if (actor.isNonlocal()) {
          this.nonLocalSnapshotTable.add(actor);
        }
    }
//System.out.println(" **** non local size=" + nonLocalSnapshotTable.size());
  }

/*  algorithm of getting a snapshot includes
 *  1. startSnapshot(): change the state of involved actors to GC mode
 *  2. getSnapshot(): cast each involved actors to snapshots and put them
 *        in snapshotTanle. Also move unblocked actors to the marking queue
 *        for starting a BFS search
 *  3. requestGetSnapshot(): simpily cast actors to snapshots and put them
 *        in snapshotTanle.
 *  4. requestDuplicateSnapshot(): similar to requestGetSnapshot(), but
 *     it returns a snpashotList
 *  5. endSnapshot(): each involved actor is back to the normal state
 *
 *   During snapshot, mutation operations are restricted:
 *  1) All reference modifications are preserved.
 *     Actors can only change its state from blocked to unblocked
 *  2) Snapshot the actors, identify unblocked actors
 *  3) Resume normal states of actors
 */
  public void startSnapshot() {
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      if (actor.getActorMemory().startAGC()) {
        //condense the forward references
        actor.scanRefInMailboxAGC();
        this.removeAcquaintanceInvRef(actor.getActorMemory().GCGetForwardList(), false);
      } else {
        //the actor has disapeared, remove it from GC (most likely it has migrated.)
        actorArray.remove(actor.getActorMemory().GCGetForwardList().getSelfRef());
      }
    }
  }

  public void getSnapshot() {
    snapshotTable.clear();
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      ActorSnapshot snapshotA=actor.getActorMemory().castToActorSnapshotAGC();
      snapshotTable.add(snapshotA);
      if (snapshotA.isUnblocked()) {
        BFSMemVector.addLast(snapshotA);
//System.out.println("(===unblocked===)"+snapshotA.name);
      }
    }
  }

  public void requestGetSnapshot() {
    snapshotTable.clear();
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      ActorSnapshot snapshotA=actor.getActorMemory().castToActorSnapshotAGC();
      snapshotTable.add(snapshotA);
    }
  }

  public SnapshotList requestDuplicateSnapshot() {
    snapshotTable.clear();
    SnapshotList ret=new SnapshotList(snapshotTable.getID());
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      ActorSnapshot snapshotA=actor.getActorMemory().castToActorSnapshotAGC();
      ret.add(snapshotA);
      snapshotTable.add(snapshotA);
    }
    return ret;

  }

  public void endSnapshot() {
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      actor.getActorMemory().endAGC();
    }
  }

  private Vector outgoingGlobalRoots=new Vector();

  public int snapshotMarkFromUnblocked() {
    ActorSnapshot currentSnapshot;
    TheaterService theater=ServiceFactory.getTheater();
    int ret=0;
    for (;;) {
      try {currentSnapshot = (ActorSnapshot) BFSMemVector.removeFirst();}
      catch (NoSuchElementException e) {break;}
      if (theater.checkSecurityEntry(currentSnapshot.name)) {
        currentSnapshot.markColor(ActorSnapshot.NONLOCAL);
      }

      Object[] flist=currentSnapshot.fList;
      if (flist==null || flist.length==0) {continue;}
      for (int i=0;i< flist.length;i++) {

        Object nextActorObj=snapshotTable.get((String)flist[i]);
        if (nextActorObj!=null) {
          ActorSnapshot nextActor = (ActorSnapshot) nextActorObj;
          if (!nextActor.isNotMarked()) {
            continue;
          } else {
//System.out.println("(===unblocked===)"+nextActor.name);
            nextActor.markColor(ActorSnapshot.UNBLOCKED);
            BFSMemVector.addLast(nextActor);
            ret++;
          }
        } else {
          currentSnapshot.mark=ActorSnapshot.NONLOCAL;
        }
      }
    }
    BFSMemVector.clear();
    return ret;
  }

  public int snapshotMarkFromRoots() {
    ActorSnapshot currentSnapshot;
    int ret=0;
    for (;;) {
      try {currentSnapshot = (ActorSnapshot) BFSMemVector.removeFirst();}
      catch (NoSuchElementException e) {break;}

      Object[] flist=currentSnapshot.fList;
      if (flist==null || flist.length==0) {continue;}
      for (int i=0;i< flist.length;i++) {
        Object nextActorObj=snapshotTable.get((String)flist[i]);
        if (nextActorObj!=null) {
          ActorSnapshot nextActor = (ActorSnapshot) nextActorObj;
          if (nextActor.mark!=ActorSnapshot.NONLOCAL) {
            nextActor.markColor(ActorSnapshot.NONLOCAL);
            BFSMemVector.addLast(nextActor);
//System.out.println("mark(===roots===)"+nextActor.name);
            ret++;
          }
        }
      }
      Object[] invList= currentSnapshot.iList;
      if (invList==null || invList.length==0) {continue;}

      if (invList!=null) {
        for (int i = 0; i < invList.length; i++) {
          Object nextActorObj = snapshotTable.get( (String) invList[i]);
          if (nextActorObj != null) {
            ActorSnapshot nextActor = (ActorSnapshot) nextActorObj;
            if (nextActor.mark == ActorSnapshot.UNBLOCKED) {
              nextActor.markColor(ActorSnapshot.NONLOCAL);
              BFSMemVector.addLast(nextActor);
//System.out.println("mark (===roots===)" + nextActor.name);
              ret++;
            }
          }
        }
      }
    }
    BFSMemVector.clear();
    return ret;
  }

  /* There are three kinds of roots:
   * 1. potentially live actors with an outgoing reference
   * 2. actors with remote incoming references
   * 3. actors which is potentially live and mobile
   */
  public void snapshotGlobalRoot() {
    TheaterService theater=ServiceFactory.getTheater();
    for (Enumeration e = snapshotTable.elements();  e.hasMoreElements(); ) {
      ActorSnapshot actor=(ActorSnapshot)e.nextElement();
      if (theater.checkSecurityEntry(actor.name) && actor.mark==ActorSnapshot.UNBLOCKED) {
        actor.markColor(ActorSnapshot.NONLOCAL);
        BFSMemVector.addLast(actor);
        nonlocalCount++;
//System.out.println("(===roots===)"+actor.name);
        continue;
      }
      if (actor.mark==ActorSnapshot.NONLOCAL) {
        BFSMemVector.addLast(actor);
        nonlocalCount++;
//System.out.println("(===roots===)"+actor.name);
        continue;
      }
      if (actor.iList==null) {continue;}
      for (int i=0;i<actor.iList.length;i++) {
        if (actor.isNotMarked()&& snapshotTable.get((String)actor.iList[i])==null) {
          actor.markColor(ActorSnapshot.NONLOCAL);
          BFSMemVector.addLast(actor);
          nonlocalCount++;
//System.out.println("(===roots===)"+actor.name);
          break;
        }
      }
    }
  }

  protected void getSnapshotGarbageActorAndAcqInvRef() {
    for (Enumeration e = snapshotTable.elements();  e.hasMoreElements(); ) {
      ActorSnapshot actor=(ActorSnapshot) e.nextElement();
//if (actor.mark==ActorSnapshot.NONLOCAL) {System.out.print("***");actor.showState();}
      if (actor.mark==ActorSnapshot.UNBLOCKED) {
        //handling active garbage actors
//System.out.println("find an active garbage actor:"+actor.name);
//actor.showState();
        UniversalActor.State garbage=(UniversalActor.State) actorArray.get(actor.name);
        garbageArray.put(actor.name,garbage);
        garbage.forceAllRefSilent();
        garbage.GCdestroy();
      } else if (actor.mark==ActorSnapshot.NOTMARKED) {
        //handling passive garbage actors
        UniversalActor.State garbage=(UniversalActor.State) actorArray.get(actor.name);
        garbageArray.put(actor.name,garbage);
        removeAcquaintanceInvRef(garbage.getActorMemory().GCGetForwardList(),true);
      }
    }
//System.out.println("---------------------------------------------");
  }

  protected void snapshotFinalize() {
    notifyInvAcq();
    this.removeInvRefTarget.clear();
    this.garbageArray.clear();
    this.actorArray.clear();
    this.BFSMemVector.clear();
    System.gc();
  }


/**********************************************
*   the pure local part
***********************************************/
  //private synchronized boolean lockGC(String name) {
  //  if (this.lock==null) {this.lock=name;return true;}
  //  else if (this.lock.equals(name)) {return true;}
  //  else {return false;}
  //}

  //private synchronized void freeLock(String name) {
  //  if (name.equals(this.lock)) { this.lock=null;}
  //}

  public int getThroughPut() {return throughPut;}

  protected void processSystemMessages(){
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      if (actor.isMigrating()==true) {actorArray.remove(actor.getActorMemory().GCGetForwardList().getSelfRef());}
      for (;;) {
        try {
          Message msg = (Message) actor.sysMailbox.remove(0);
          if (msg.getMethodName().equals("receiveAck")) {
            actor.getActorMemory().GCGetForwardList().receiveAck((String)(msg.getArguments())[0]);
          } else if (msg.getMethodName().equals("removeForwardRef")) {
            actor.getActorMemory().GCGetForwardList().removeReference((String)(msg.getArguments())[0]);
          } else if (msg.getMethodName().equals("removeInverseRef")) {
            actor.getActorMemory().GCGetinverseList().removeInverseReference((String)(msg.getArguments())[0],((Integer)(msg.getArguments())[1]).intValue());
          } else if (msg.getMethodName().equals("endMsgPassing")) {
            actor.endMsgPassing((String)(msg.getArguments())[0],(String)(msg.getArguments())[1]);
          }
        } catch (java.lang.ArrayIndexOutOfBoundsException aie) {break;}
      }
    }
  }

  protected void formGroup() {
    snapshotUANReg(nameSvc.getUANTable());
    snapshotUALReg(nameSvc.getUALTable());
    BFSMemVector.clear();
    removeInvRefTarget.clear();
    garbageArray.clear();
  }

  //try to remove useless forward references and put them in 'removeInvRefTarget'
  //killed = true means that every forward reference has to be removed.
  //killed = false means that only dead forward references (count=0) are removed
  private void removeAcquaintanceInvRef(ForwardList fList, boolean killed) {
    //find all unused forward references
    try{
      for (Enumeration e = fList.getRealForwardList().keys(); e.hasMoreElements(); ) {
        String key = (String) e.nextElement();
        Hashtable flistTable = fList.getRealForwardList();
        ForwardListEntry fEntry = (ForwardListEntry) flistTable.get(key);

        //If the forward reference is useless, remove it, and store their information in 'removeInvRefTarget'
        //The forward references are useless if
        //  1. the source actor is live but some of its forward references are dead
        //  2. the source actor is garbage.
        //Notice that is killed=true, every forward reference is removed.
        if (fEntry.isDead() || killed) {
          Object res = flistTable.remove(key);
          if (res != null) {
            //put the inverse reference information in 'removeInvRefTarget'
            invTargetWrap invData = new invTargetWrap(key, fList.getSelfRef(),
                fEntry.getDebt());
            removeInvRefTarget.addElement(invData);
          }
        }
      }
    }catch (NullPointerException npe) {return;}
  }

  protected void collectGarbage() {
    throughPut=0;
    for (Enumeration e = garbageArray.elements();  e.hasMoreElements();) {
      UniversalActor.State gActor=((UniversalActor.State)e.nextElement());
      nameSvc.remove(gActor.getUAN(),gActor.getUAL());
      gActor.GCdie();
//if (gActor.getUAN()!=null) System.out.println("remove:"+gActor.getUAN());
      throughPut++;
    }
    RunTime.deletedUniversalActor(throughPut);
  }

  protected void GCFailed() {
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor = (UniversalActor.State)e.nextElement();
      try {actor.getActorMemory().endAGC();}
      catch (Exception exc) {
        System.err.println("GC debug info:"+e);
        continue;
      }
    }
    this.removeInvRefTarget.clear();
    this.garbageArray.clear();
    this.actorArray.clear();
    this.BFSMemVector.clear();
    System.gc();
  }

  /**
   * firstly, remove all unnecessary msgs whose targets are garbage.
   * Then start sending messages to unregister inverse references
   * This function is used in GCFinalize()
   */
  private void notifyInvAcq() {
    //remove all unnecessary system messages which will be sent to a garbage actor
    for (int i=0;i<removeInvRefTarget.size();i++) {
      invTargetWrap data=(invTargetWrap)removeInvRefTarget.get(i);
      if (garbageArray.get(data.target)==null) {
        //send system messages to the inverse acquaintances
        //to remove the inverse references
        WeakReference invActor;
        if (data.target.charAt(0)=='u') {invActor = new WeakReference(new UAN(data.target), null);}
        else {invActor = new WeakReference(null, new UAL(data.target));}
        invActor.send(new RemoveInverseRefMsg(invActor,data.argument, data.debt));
      }
    }
  }

  //pass the uan registry hashtable that stores actors
  // to find them and put them in the 'actorArray'
  private void snapshotUANReg(Hashtable reg) {
//int a=0;
    for (Enumeration e = reg.elements();  e.hasMoreElements(); ) {
      Object actor = e.nextElement();
      if (actor instanceof UniversalActor.State) {
        if (actor instanceof salsa.resources.EnvironmentalServiceState) {continue;}
        if (actor instanceof salsa.resources.ActorServiceState) {continue;}
        UniversalActor.State target=(UniversalActor.State)actor;
        //if (!target.getActorMemory().getSnapshotBit())
          actorArray.put(target.getUAN().toString(),actor);
//a++;
      }
    }
//System.out.println("UAN:"+a);
  }

  private void snapshotUALReg(Hashtable reg) {
//int a=0;
    for (Enumeration e = reg.elements();  e.hasMoreElements(); ) {
      Object actor = e.nextElement();
      if (actor instanceof UniversalActor.State) {
        if (actor instanceof salsa.resources.EnvironmentalServiceState) {continue;}
        if (actor instanceof salsa.resources.ActorServiceState) {continue;}
        UniversalActor.State target=(UniversalActor.State)actor;
        //if (!target.getActorMemory().getSnapshotBit())
          actorArray.put(((UniversalActor.State)actor).getUAL().toString(),actor);
//a++;
      }
    }
//System.out.println("UAL:"+a);
  }


  //debugging tool:
  public void showActorState() {
    for (Enumeration e = actorArray.elements();  e.hasMoreElements(); ) {
      UniversalActor.State actor=(UniversalActor.State)e.nextElement();
      System.out.println(actor.getActorMemory().toString());
    }
  }



// server GC part:
//******************************************************************************
//******************************************************************************


  //private String currentTask;
  boolean locked=false;
  //boolean isSnapshotFresh=false;
  int lockCounter=0;
  public void setGlobalGCTask() {
      //synchronized (this.globalGCTask) {
        lockCounter++;
        locked=true;
      //}
  }

  public void clearGlobalGCTask() {
    //synchronized (this.globalGCTask) {
      lockCounter--;
      if (lockCounter==0) {locked=false;}
    //}
  }

  protected void formNonLocalGroup() {
    Hashtable uantable=this.nameSvc.getUANTable();
    Hashtable ualtable=this.nameSvc.getUALTable();
    actorArray.clear();
    for (Enumeration e = nonLocalSnapshotTable.elements();  e.hasMoreElements(); ) {
      ActorSnapshot actor=(ActorSnapshot) e.nextElement();
      Object result=uantable.get(actor.name);
      if (result!=null) {
        actorArray.put(actor.name, result);
      } else {
        result=ualtable.get(actor.name);
        if (result!=null) {
          actorArray.put(actor.name, result);
        }
      }
    }
  }


  public synchronized boolean requestSnapShot() {
    setGlobalGCTask();
//System.out.println("111111111111111111111111,lockCounter="+lockCounter);
if (lockCounter>1) {return false;} //else {isSnapshotFresh=false;}
    formNonLocalGroup();
    System.gc();
    if (actorArray.size() == 0) {

      return false;
    }
    processSystemMessages();
    startSnapshot();

    return true;
  }


  public synchronized SnapshotList requestVirtualMigration() {
//System.out.println("2a2222222222222222222222,lockCounter="+lockCounter);
    //if (isSnapshotFresh) {
    //  clearGlobalGCTask();
    //  return ret;
    //}
    requestGetSnapshot();
    if (lockCounter==1) endSnapshot();
    SnapshotList ret=this.snapshotTable;
    this.snapshotTable=new SnapshotList(ret.getID());
    //isSnapshotFresh=true;
    clearGlobalGCTask();
    return ret;
  }

  public synchronized void requestKilling(Vector victimList) {
    actor2kill.addAll(victimList);
//for (int i=0;i<victimList.size();i++) {
//System.out.println("to kill:" + victimList.get(i));
//}
    /*
    this.throughPut=0;
    setGlobalGCTask();
    formGroup();
    //processSystemMessages();
    //startSnapshot();

    for (int i=0;i<victimList.size();i++) {
      UniversalActor.State garbageActor=(UniversalActor.State) actorArray.get((String)victimList.get(i));
      ActorMemory currentMem;
      if (garbageActor!=null) {
        this.throughPut++;
        currentMem = garbageActor.getActorMemory();
        garbageArray.put(currentMem.GCGetForwardList().getSelfRef(),garbageActor);
        removeAcquaintanceInvRef(currentMem.GCGetForwardList(), true);
      }
    }
    collectGarbage();
    snapshotFinalize();

    //if (System.getProperty("gcverbose")!=null) {
    //  System.out.println("Global GC throughPut=" + this.throughPut);
    //}
    if (this.throughPut>0) {System.out.println("Global GC throughPut=" + this.throughPut);}
    clearGlobalGCTask();
    */
  }

  public void killing() {
    Vector victimList=    actor2kill;
    if (victimList.size()==0) {return;}
    this.throughPut=0;
    formGroup();
    //processSystemMessages();
    //startSnapshot();
    for (int i=0;i<victimList.size();i++) {
      UniversalActor.State garbageActor=(UniversalActor.State) actorArray.get((String)victimList.get(i));
      ActorMemory currentMem;
      if (garbageActor!=null) {
        this.throughPut++;
//System.out.println("to kill:" + victimList.get(i));
        currentMem = garbageActor.getActorMemory();
        garbageArray.put(currentMem.GCGetForwardList().getSelfRef(),garbageActor);
        removeAcquaintanceInvRef(currentMem.GCGetForwardList(), true);
      }
    }
    collectGarbage();
    snapshotFinalize();
    if (this.throughPut>0) {System.out.println("Global GC throughPut=" + this.throughPut);}
    victimList.clear();
  }

}
