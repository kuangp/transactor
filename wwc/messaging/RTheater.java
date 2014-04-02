package wwc.messaging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;

import salsa.language.Actor;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import salsa.messaging.ReceptionService;
import salsa.messaging.TheaterService;
import salsa.naming.UAL;
import salsa.naming.UAN;
import salsa.language.ActorReference;
import gc.LocalCollector;
import gc.actorGC.*;

/**
 * The RTheater is the reference implementation of the TheaterService
 * interface.
 *
 * @version $Id: RTheater.java,v 1.1 2006/09/22 03:38:51 wangw5 Exp $
 * This theater is a restricted theater which can only create immobile actors.
 * All local services are banned.
 *
 * @author stepha
 * @author deselt
 * @author weijen
 */

public class RTheater implements TheaterService {
        public static String version;

        private static int port = 0;
        public int getPort() 		{ return RTheater.port; }

        private static boolean netIfSpecified = false;
        private static InetAddress inetAddr = null;

        public synchronized boolean isRestricted() {return true;}

        private boolean applet = false;
        SecurityValidation security=new SecurityValidation();
        public void isApplet() { applet = true; }

        public static void main(String[] arguments) {
                if (arguments.length == 1) {
                        RTheater.port = Integer.parseInt(arguments[0]);
                } else if (arguments.length > 1) {
                        System.out.println( "WWC Theater Version: " + version);
                        System.out.println( "Usage: java wwc.messaging.RTheater [<port>]" );
                        System.out.println( "or invoke a Actor with VM option -Dtheater=wwc.messaging.RTheater and -Dport=<port>" );
                        return;
                } else {
                        RTheater.port = 4040;
                }
                System.setProperty("nodie","theater");
                RunTime.receivedUniversalActor();
                TheaterService theater = new RTheater();
                ServiceFactory.setTheater(theater);
                //LocalCollector gc=new NColorAlg();
                //LocalCollector gc=new BackPointerAlg();
                LocalCollector gc=new PushPull();
                ServiceFactory.setGC(gc);
                if ( System.getProperty("silent") == null ) System.out.println("Theater listening on: " + theater.getLocation());
        }

        /**
         * Constructor for Theater.
         */
        public RTheater() {
          version  = "$Id: RTheater.java,v 1.1 2006/09/22 03:38:51 wangw5 Exp $";

          //Initialize the port
          String propertyPort = null;
          if (!applet) propertyPort = System.getProperty("port");
          if (propertyPort != null) RTheater.port = Integer.parseInt(propertyPort);

          // Check to see if a valid network interface is specified
    	  String propertyNetIf = System.getProperty("netif");
    	  if (propertyNetIf != null) {
    		NetworkInterface netIf = null;

            try {
              netIf = NetworkInterface.getByName( propertyNetIf );
            }
            catch (SocketException e) {
              System.err.println("Theater Service error:");
              System.err.println("\tCould not specify network interface.");
              System.err.println("\tException: " + e);
            }

            if (netIf != null) {
              Enumeration<InetAddress> inetAddrs = netIf.getInetAddresses();
              for (InetAddress inetAddr : Collections.list( inetAddrs )) {
                byte[] rawInetAddr = inetAddr.getAddress();
                if (rawInetAddr.length == 4) {	// make sure the address is IPv4
                  RTheater.inetAddr = inetAddr;
                  RTheater.netIfSpecified = true;
                  break;
                }
              }
    		}

            if (!RTheater.netIfSpecified)
              System.err.println( "Warning: no valid IPv4 address found for the netif '" + propertyNetIf + "', use default" );
          }

            // Create a server socket.
          ServerSocket server = null;
          try {
    		if (RTheater.netIfSpecified)
    			server = new ServerSocket( RTheater.port, 1, RTheater.inetAddr );
    		else
    			server = new ServerSocket( RTheater.port );

            //System.err.println("Recv Buffer Size: "+server.getReceiveBufferSize());

          } catch (IOException e) {
            System.err.println("Theater Service error:");
            System.err.println("\tCould not start theater.");
            System.err.println("\tException: " + e);
          }

          // Check to see what the port actually is.
          RTheater.port = server.getLocalPort();

          final ServerSocket passive = server;

          // Accept connections.
          Runnable listenLoop = new Runnable() {
            java.util.Vector socketVector=new java.util.Vector();

            public void run() {
              int i = 0;
              while (true) {
                try {

                  final Socket incoming = passive.accept();
//System.out.println("accept .........");
                  ReceptionService handler = ServiceFactory.getReception();
                   //*****recovery************
                  Runnable handlerThread = new Runnable() {
                    public void run() {
                      ReceptionService handler = ServiceFactory.getReception();
                      handler.process(incoming);
                    }
                  };
                  new Thread(handlerThread, "Handler Loop " + i).start();

                } catch (IOException e) {
                  System.err.println("Theater Service error: ");
                  System.err.println("\tFailed to accept connection.");
                  System.err.println("\teException: " + e);
                }
                i++;
              }
            }
          };

          // Start the listening thread.
          new Thread( listenLoop, "Listen Loop Thread" ).start();

        }

        public boolean checkSecurityEntry(String ref) {
          return security.checkSecurityEntry(ref);
        }

        public void registerSecurityEntry(String ref) {
          security.registerSecurityEntry(ref);
        }

        public void removeSecurityEntry(String ref) {
          security.removeSecurityEntry(ref);
        }

        /**
         * These methods provide a way to generate a unique identifier
         * for every token used by a SALSA program and a default UAL for
         * new actors in the theater.
         */
        private String hostName = null;
        public synchronized String getHost() {
                if (hostName != null) {
                        return hostName;
                } else {
                        if (RTheater.netIfSpecified) {
                                hostName = RTheater.inetAddr.getHostAddress();
                        }
                        else {     	
                                try {
                                        hostName = InetAddress.getLocalHost().getHostAddress();
                                } catch (UnknownHostException e) {
                                        hostName = "localhost";
                                }
                        }
                        return hostName;
                }
        }

        public synchronized String getLocation() {
                return "rmsp://" + getHost() + ":" + RTheater.port + "/";
        }

        public synchronized String getID() {
          return  getHost() + ":" + RTheater.port ;
        }

}
