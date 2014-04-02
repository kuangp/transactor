package salsa.language;

import gc.*;
import salsa.messaging.TransportService;

/**
 * Runtime class.
 *
 * Initially just keep a counter for unprocessed actor messages.
 * When there are no unprocessed actor messages and all actors are
 * local and are waiting for messages:  we can exit the Java
 * virtual machine.
 */

public class RunTime {
  // Here we have a lower priority thread that checks for
  // conditions to terminate the JVM:
  // (1) All actors are local.  (no universal actors)
  // (2) All actor mailboxes are empty.
  // (3) No actor is currently processing a message.

  static Thread exitThread;
  static int messagesInMailboxes = 0;
  //static int outgoingObjects = 0;
  static public int universalActors = 0;
  //static int actors=0;
  static boolean started = false;
  static boolean initialized = false;
  static boolean nogc=false;
  static boolean nodie=false;
  static boolean gcverbose=false;
  static int gcInterval=20;
  //static gc.localGC.LocalPRID gc=new gc.localGC.LocalPRID();
  static LocalCollector gc;

  private static void init() {
    try{
      if (System.getProperty( "nogc" )!=null){nogc=true;}
    } catch (Exception e) {nogc=false;}

    try{
      if (System.getProperty( "gcverbose" )!=null){gcverbose=true;}
    } catch (Exception e) {gcverbose=false;}
    try{
      if (System.getProperty("gcInterval")!=null) {
        gcInterval=(int)(Float.parseFloat(System.getProperty("gcInterval"))/100);
        if (gcInterval<1) {gcInterval=1;}
      }
    } catch (Exception e) {gcInterval=20;}

    if (!nogc) {gc=ServiceFactory.getGC();}
    if (System.getProperty("nodie") == null) {nodie=false;} else {nodie=true;}
    Runnable checkExit = new Runnable() {
      public void run() {
        long currentMem=Long.MAX_VALUE;
        int i=0;
        while ( live() ) {
          try {
            Thread.sleep(100);
            if (!nogc) {
              long data=Runtime.getRuntime().totalMemory();
              i++;
              if (data>currentMem || i>gcInterval) {
                i=0;

                gc.collect();
                int throughput=gc.getThroughPut();
                //deletedUniversalActor(throughput);
                if (gcverbose) {
                  System.out.println("GC:garbage throughput=" +
                                     throughput +
                                     ",max=" + Runtime.getRuntime().maxMemory() +
                                     ",total=" +
                                     Runtime.getRuntime().totalMemory() +
                                     ",free=" + Runtime.getRuntime().freeMemory()
                                     );
                }
              }
              currentMem=Runtime.getRuntime().totalMemory();
            }
          } catch (Exception ie) {ie.printStackTrace();}
        }
        System.err.flush();
        System.out.flush();
        TransportService ts=ServiceFactory.getTransport();
        if (sentOutgoingObj) {
          for (;;) {
            if (!ts.isMessageInTransit()) {break;}
            try {
              Thread.sleep(300);
            } catch (Exception e) {}
          }
        }

        System.runFinalization();
        System.exit(0);
      }
    };

    exitThread = new Thread(checkExit);
    exitThread.setDaemon(true);
    //exitThread.setPriority(Thread.MIN_PRIORITY);

  }

  //public static boolean passedRef=false;
  public static boolean sentOutgoingObj=false;

  public static synchronized boolean live() {
    //if (sentOutgoingObj)
    return nodie || universalActors>0 || messagesInMailboxes>0;
    //return nodie || messagesInMailboxes>0;
  }

  public synchronized static void receivedMessage(int val) {
    messagesInMailboxes+=val;
    //System.out.println("---msgInMail="+ messagesInMailboxes);
    if (!initialized) {
      initialized = true;
      init();
    }
    if (!started) {
      started = true;
      exitThread.setName( "RunTime Exit Thead" );
      exitThread.start();
    }
  }

  public static void receivedMessage() {receivedMessage(1);}

  public synchronized static void finishedProcessingMessage() {
    messagesInMailboxes--;
    //System.out.println("---msgInMail="+ messagesInMailboxes);
  }

  public synchronized static void finishedProcessingMessage(int val) {
    messagesInMailboxes-=val;
    //System.out.println("---msgInMail="+ messagesInMailboxes);
  }

  /*
   * deprecated receivedUniversalActor() and
   * removedUniversalActor()
   */
  public static void receivedUniversalActor() {}
  public static void removedUniversalActor() {}


  public synchronized static void createdUniversalActor() {
    universalActors++;
    //System.out.println("---UA="+ universalActors);
  }

  public synchronized static void deletedUniversalActor() {
    universalActors--;
    //System.out.println("---UA="+ universalActors);
  }

  public synchronized static void deletedUniversalActor(int val) {
    universalActors-=val;
    //System.out.println("---UA="+ universalActors);
  }


  public  static void sendingOutgoingObject() {
    sentOutgoingObj=true;
  }

  public static void sentOutgoingObject() {
    //outgoingObjects--;
  }
}
