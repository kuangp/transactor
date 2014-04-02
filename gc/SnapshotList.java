package gc;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Map;
import salsa.language.UniversalActor;
import gc.ActorSnapshot;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class SnapshotList implements java.io.Serializable{
  private String ID="";
  private Hashtable virtualAtorList=new Hashtable();

  public SnapshotList(String myid) {
    ID=myid;
  }

  public String getID() {
    return ID;
  }

  public void add(UniversalActor.State actor) {
    ActorMemory mem=actor.getActorMemory();
    add(mem.castToActorSnapshot());
  }

  public synchronized void add(ActorSnapshot actor) {
    this.virtualAtorList.put(actor.name,actor);
  }

  public synchronized void add(SnapshotList data) {
    this.virtualAtorList.putAll(data.virtualAtorList);
  }

  public synchronized ActorSnapshot get(String name) {
    return (ActorSnapshot)virtualAtorList.get(name);
  }

  public synchronized ActorSnapshot remove(String name) {
    return (ActorSnapshot)virtualAtorList.remove(name);
  }

  public Enumeration elements() {
    return this.virtualAtorList.elements();
  }

  public Enumeration keys() {
    return this.virtualAtorList.keys();
  }

  public void clear() {
    this.virtualAtorList.clear();
    //this.virtualAtorList=new Hashtable();
  }

  public int size() {return virtualAtorList.size();}

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    out.defaultWriteObject();
    out.flush();
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    in.defaultReadObject();
    //mark=false;
  }

}