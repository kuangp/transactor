/*
 * Created on Sep 17, 2003
 *
 */

package wwc.messaging;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import salsa.language.*;
import salsa.language.exceptions.*;
import salsa.naming.*;
import salsa.messaging.*;
import gc.SystemMessage;
import gc.message.TimeoutMsg;
import salsa.language.ServiceFactory;

/**
 * This class is the reference implementation of the Persistent MessagingService. The
 * service is implemented as a RMSP client.
 *
 * This implementation uses the NamingService interface and the salsa.language.
 * ServiceFactory for UAN resolution.
 *
 * @authors Travis Desell, Kaoutar El Maghraoui
 */

public class WWCTransportService implements TransportService {
  /**
   * Constructor for Messenger.
   */

  private Hashtable socketTable=new Hashtable();

  public WWCTransportService() {
    Runnable cleanSocket = new Runnable() {
      public void run() {
        while (true) {
          try {
            Thread.sleep(10000); //sleep 10 seconds
            socketGarbageCollection();
          }catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    };
    new Thread( cleanSocket, "Clear Socket Thread" ).start();
  }

  public void socketGarbageCollection() {
//System.out.println("cleaning the socket table...");
    for (Enumeration e = socketTable.elements();  e.hasMoreElements(); ) {
      OutgoingSocketHandler channel=(OutgoingSocketHandler) e.nextElement();
      if (channel.decRetryAndCheckExpired()) {
//System.out.println("   die -->"+channel.getID()+",retry="+channel.retry);
        channel.die();
        //if (!channel.isAlive()) {channel.start();}
        socketTable.remove(channel.getID());

      }
    }
  }

  public boolean isMessageInTransit() {
    synchronized (this){
      for (Enumeration e = socketTable.elements();  e.hasMoreElements(); ) {
        OutgoingSocketHandler channel = (OutgoingSocketHandler) e.nextElement();
        if (channel.size()>0) {return true;}
      }
    }
    return false;
  }

  public void migrate(Actor state, UAL target){}

  public void send(Message message, ActorReference target) {
    UAL targetUAL=target.getUAL();
    String targetHost=targetUAL.getHost()+":"+targetUAL.getPort();
    OutgoingSocketHandler channel=null;
    synchronized (this){
      channel = (OutgoingSocketHandler) socketTable.get(targetHost);
      if (channel != null) {channel.put(message);}
      else {
        //1. create a channel for communication
        //2. validate the <UAN,UAL> pair
        UAL newUAL=ServiceFactory.getNaming().validateEntry(message.getTarget());
        if (newUAL==null) {newUAL=targetUAL;}
        targetHost=newUAL.getHost()+":"+newUAL.getPort();
        channel = (OutgoingSocketHandler) socketTable.get(targetHost);
        if (channel != null) {channel.put(message);return;}
        channel = new OutgoingSocketHandler(newUAL);
        socketTable.put(targetHost,channel);
        channel.put(message);
      }
    }
  }

  public void send(SystemMessage message, ActorReference target) {
    UAL targetUAL=target.getUAL();
    String targetHost=targetUAL.getHost()+":"+targetUAL.getPort();
    OutgoingSocketHandler channel=null;
    synchronized (this){
      channel = (OutgoingSocketHandler) socketTable.get(targetHost);
      if (channel != null) {channel.put(message);}
      else {
        //create a channel for communication
        /*channel = new OutgoingSocketHandler(targetUAL);
                            socketTable.put(targetHost,channel);
                            channel.put(message);
         */
        //1. create a channel for communication
        //2. validate the <UAN,UAL> pair
        UAL newUAL=ServiceFactory.getNaming().validateEntry(message.getTarget());
        if (newUAL==null) {newUAL=targetUAL;}
        targetHost=newUAL.getHost()+":"+newUAL.getPort();
        channel = (OutgoingSocketHandler) socketTable.get(targetHost);
        if (channel != null) {channel.put(message);return;}
        channel = new OutgoingSocketHandler(newUAL);
        socketTable.put(targetHost,channel);
        channel.put(message);
      }
    }
  }

  public void timeoutSend(long timeout,ActorReference target) {
    final long timeout2awake=timeout;
    final ActorReference target2send=target;
    Runnable timeoutThread = new Runnable() {
      public void run() {
          try {
            Thread.sleep(timeout2awake);
            TimeoutMsg message2send=new TimeoutMsg(target2send);
            send(message2send,target2send);
          }catch (Exception e) {
            e.printStackTrace();
          }
      }
    };
    new Thread( timeoutThread, "Timeout").start();
  }

}



