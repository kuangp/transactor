package gc;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */
import java.util.Vector;

public interface LocalCollector {
  //public void setGlobalGCTask();
  //public void clearGlobalGCTask();
  public boolean requestSnapShot();
  public SnapshotList requestVirtualMigration();
  public void requestKilling(Vector victimList);
  public boolean isGlobalGC();
  public ActorSnapshot isNonLocalActor(String actorID);
  public boolean isInLocalHost(String actorID);


  //public  boolean occupy(String detectiveName) ;
  //public  void free(String detectiveName) ;
  //public  Hashtable getNonlocalReachableSet(String detectiveName) ;
  public void collect();
  //debugging tool:
  public void showActorState();
  public int getThroughPut();
}