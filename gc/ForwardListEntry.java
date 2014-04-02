package gc;

/**
 * <p>Title: Forward reference list entry for the class, ForwardList</p>
 * <p>Description: A Forward list is a hashtable which uses
 *    an actor address (UAN od UAL) string as the key.
 *    The ForwardListEntry is the result when querying this list.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: WWC</p>
 * @author: WeiJen Wang
 * @version 1.0
 */

import java.io.Serializable;

public class ForwardListEntry implements java.io.Serializable{

  private int referenceCounter=0;
  private int debt=0;

  //the counter for the expected acknowledgement messages of
  // a given reference.
  // An actor is a pseudo root if there this counter is not zero
  private int expectedAckCounter=0;


  public ForwardListEntry() {
    referenceCounter=1;
    debt=1;
  }

  public ForwardListEntry(int refCount,int expAckCount) {
    referenceCounter=refCount;
    debt=refCount;
    expectedAckCounter=expAckCount;
  }

  public synchronized int incReferenceCounter(int val) {
    referenceCounter+=val;
    debt+=val;
    return referenceCounter;
  }

  public  synchronized int incReferenceCounter() {
    referenceCounter++;
    debt++;
    return referenceCounter;
  }

  public  synchronized int decReferenceCounter() {
    referenceCounter--;
    return referenceCounter;
  }

  public  synchronized int getReferenceCounter() {
    return referenceCounter;
  }

  public  synchronized void setReferenceCounter(int newValue) {
    referenceCounter=newValue;
  }

  public synchronized int getDebt() {return debt;}

  public synchronized void setDebt(int val) {debt=val;}

  public synchronized void decDebt() {debt--;}


  public  synchronized int incExpectedCounter() {
    expectedAckCounter++;
    return expectedAckCounter;
  }

  public  synchronized int decExpectedCounter() {
    expectedAckCounter--;
    return expectedAckCounter;
  }

  public  synchronized int getExpectedCounter() {
    return expectedAckCounter;
  }

  public  synchronized void setExpectedCounter(int newValue) {
    expectedAckCounter=newValue;
  }

  public  synchronized boolean isDead() {
    if (expectedAckCounter==0 && referenceCounter==0) {
      return true;
    }
    return false;
  }
  public String toString() {
    return "Ref Couner="+this.referenceCounter+", Exp Ack="+this.expectedAckCounter;
  }
}