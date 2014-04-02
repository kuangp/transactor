package wwc.messaging;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.Vector;

import salsa.language.Message;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;

import salsa.naming.UAL;
import gc.SystemMessage;

public class OutgoingSocketHandler extends Thread {
        private int number;
        private Vector objects = new Vector();
        private String host=null;
        private int port=-1;
        private Socket socket = null;
        private boolean alive=true;
        private boolean tryNewRoute=true;
        public int retry=3;
        private ObjectOutputStream outputStream;
        private int failingSleepTime=500;

/*static long count=0;

public static synchronized void incCount() {
          count++;
System.out.println("msg count:"+count);
        }
*/
        public OutgoingSocketHandler(UAL target) {
          try{
            host=target.getHost();
            port=target.getPort();
            socket=new Socket(host,port);
            outputStream = new ObjectOutputStream( socket.getOutputStream());
            start();
          } catch (Exception e) {
            System.err.println("WWC Transport Service Error: ");
            System.err.println("\tThe target host '" + host+":"+port + "' is not available");
            System.err.println("\t" + e);
          }
        }

        public synchronized int size() {return this.objects.size();}

        public String getID() {
          return this.host+":"+this.port;
        }

        public synchronized boolean decRetryAndCheckExpired() {
          retry--;
          failingSleepTime=1000;
//System.out.println("retry="+retry);
          if (this.objects.size()>0) {
            tryNewRoute=true;
            try{
                if (socket==null) {socket=new Socket(host,port);
                  outputStream = new ObjectOutputStream( socket.getOutputStream());
                  if (!this.isAlive()) {start();}
                }
            } catch (Exception e) {}
            return ((retry+3)< 0);
          }
          return retry<0;
        }

        public synchronized void resetRetry() {
          retry=3;
        }

        public synchronized void die() {
          alive=false;
          notify();
        }

        public void run() {
                while (alive) {send();}
                try{
                  outputStream.close();
                  socket.close();
                } catch (Exception e) {}
                socket=null;
        }

        public synchronized void put(Object o) {
                RunTime.sendingOutgoingObject();
                objects.addElement(o);
                notify();
        }

        public synchronized void send() {
          if (objects.isEmpty()) {
            try {
              wait();      // The lock of Mailbox means it is empty.
            } catch (InterruptedException ie){
              System.err.println("WWC Transport Service Error: ");
              System.err.println("\tError getting object to remote send");
              System.err.println("\t" + ie);
            }
          }

          Object o=null;
          try {
            o = objects.remove(0);
          }catch (Exception e) {o=null;return;}

          try {
            //refresh the timestamp

            //incCount();
            outputStream.writeObject(o);
            outputStream.flush();
            resetRetry();
            failingSleepTime=500;
//if (o instanceof Message)
//System.out.println("    send msg:"+ ((Message)o).getMethodName());
//if (o instanceof gc.SystemMessage)
//System.out.println("    send sys:"+ ((gc.SystemMessage)o).getMethodName());
          } catch (ConnectException e) {
            System.err.println("WWC Transport Service Error:");
            System.err.println("\tError sending: " + o);
            System.err.println("\tThrew Exception: " + e);
            System.err.println();
          } catch (SocketException e) {

            //It is possible that the <UAN,UAL> pair
            //  in the local naming service is not correct
            //Try to verify it.
            if (tryNewRoute) {
              tryNewRoute=false;
              UAL newTarget;
              if (o instanceof Message) {
                newTarget=ServiceFactory.getNaming().validateEntry(((Message)o).getTarget());
              } else {
                newTarget=ServiceFactory.getNaming().validateEntry(((SystemMessage)o).getTarget());
              }
              if (newTarget!=null) {
                host=newTarget.getHost();
                port=newTarget.getPort();
              } else {
                RunTime.sendingOutgoingObject();
System.out.println("....The remote target actor at "+host+":"+port+" had terminated....");
                return;
              }
            }
            try{Thread.sleep(failingSleepTime);}catch (Exception newe) {}
            /*
             * socket re-connection
             */
            try {
              outputStream.close();
              socket.close();
            } catch (Exception newe) {}

            try {
              socket=new Socket(host,port);
              outputStream = new ObjectOutputStream( socket.getOutputStream());
            } catch (Exception newe) {
              //System.out.println(o);
              System.err.println("SOCKETEXCEPTION: " + newe + ", failed to connect to " + host+":"+port );
            }
            failingSleepTime+=failingSleepTime;
            RunTime.sendingOutgoingObject();
            objects.insertElementAt(o,0);
          } catch (IOException e) {
            System.err.println("WWC Transport Service Error:");
            System.err.println("\tError sending: " + o);
            System.err.println("\tThrew Exception: " + e);
            System.err.println();
          } catch (Exception e) {
            System.err.println("WWC Transport Service Unknown Error:");
            System.err.println("\tError sending: " + o);
            System.err.println("\tThrew Exception: " + e);
            System.err.println();
          }

          RunTime.sentOutgoingObject();
        }

        protected void finalize() {
          if (this.objects.size()>0) {
            System.err.println("Error: "+ " Number of Message Loss=" +this.objects.size() );
          }
        }
}
