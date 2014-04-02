package gc;

/**
 * <p>Title: The Inverse List Entry for the inverse list</p>
 * <p>Description: When an actor has no inverse acquaintance,
 *      and neither it is active nor has messages in its mail box,
 *      this actor is garbage.
 *      A reference entry has no inverse acquaintance can be deleted.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author WeiJen Wang
 * @version 1.0
 */

public class InverseListEntry implements java.io.Serializable{
  private int referenceCounter=0;
  public InverseListEntry() {
    referenceCounter=1;
  }

  public int incReferenceCounter() {
    referenceCounter++;
    return referenceCounter;
  }

  public int decReferenceCounter() {
    referenceCounter--;
    return referenceCounter;
  }

  public int decReferenceCounter(int val) {
    referenceCounter=referenceCounter-val;
    return referenceCounter;
  }


  public int getReferenceCounter() {
    return referenceCounter;
  }

  public void setReferenceCounter(int newValue) {
    referenceCounter=newValue;
  }

  //isFree()=true means the actor is not referenced by a given reference
  public boolean isFree() {
    return referenceCounter==0;
  }

  public String toString() {
    return "invRef Couner="+this.referenceCounter;
  }
}