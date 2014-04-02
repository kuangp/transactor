/**
 * SALSA/World Wide Computer Project
 *
 * Location Server - Universal Actor Name Daemon
 * Provides the location (Universal Actor Location) of an actor running on
 * the World Wide Computer from its name (Universal Actor Name).
 *
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 * Modified by WeiJen to support UANP 0.2
 */
package wwc.naming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Contains the main method of the UAN daemon.
 *
 * @version %I%, %G%
 * @author Gregory Haik, Carlos Varela, WeiJen Wang
 */
public class WWCNamingServer {
  static long accessCount=0;
  static int portNumber = 3030;							// The default port number is set to 3030.
  static ServerSocket serverSocket = null;				// The server will listen via this socket.
  static final String serverVersion = new String("uand/0.2");	// Location server version.
  static Hashtable locationMap;							// Mapping from UAN's to UAL's.
								// Used for communications.
  static private boolean flag = true;							// Flag variable for while loop.

  static private boolean netIfSpecified = false;
  static private InetAddress inetAddr = null;

  public static void main(String[] argv) {
    for (int x=0; x<argv.length; x++) {
      if (argv[x].equals("-h"))
        helpMessage();
      else if (argv[x].equals("-v"))
        printServerVersion();
      else if (argv[x].equals("-p")) {
        x++;
        try {
          portNumber = Integer.parseInt(argv[x]);
        } catch (Exception e) {
          helpMessage("Invalid port number: "+argv[x]);
        }
      }
      else
        helpMessage("illegal option");
    }

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
            WWCNamingServer.inetAddr = inetAddr;
            WWCNamingServer.netIfSpecified = true;
            break;
          }
        }
      }

      if (!WWCNamingServer.netIfSpecified)
          System.err.println( "Warning: no valid IPv4 address found for the netif '" + propertyNetIf + "', use default" );
    }

    try {
      if (WWCNamingServer.netIfSpecified)
        serverSocket = new ServerSocket( portNumber, 1, WWCNamingServer.inetAddr );
      else
        serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println("Could not listen on port: "+portNumber+", "+e);
      System.exit(1);
    }

    if (WWCNamingServer.netIfSpecified)
      System.out.println("WWCNamingServer listening on " + WWCNamingServer.inetAddr + ":" + portNumber);
    else
      System.out.println("WWCNamingServer listening on port: " + portNumber);

    locationMap = new Hashtable();
    int i=0;
    while (flag) {
      try {
        final Socket clientSocket = serverSocket.accept();

        Runnable handlerThread = new Runnable() {

          public void run() {
            handleRequest(clientSocket);
          }
        };
        new Thread(handlerThread, "NamingServer Thread " + i).start();
        i++;
      } catch (IOException e) {
        System.out.println("Accept failed: "+portNumber+", "+e);
        System.exit(1);
      }
    }
  }

  static void printServerVersion() {
    System.out.println("Universal Actor Name Daemon.");
    System.out.println(serverVersion);
    System.exit(0);
  }

  static void helpMessage(String s) {
    System.out.println(s);
    WWCNamingServer.printHelpMessage();
    System.exit(1);
  }

  static void helpMessage() {
    WWCNamingServer.printHelpMessage();
    System.exit(0);
  }

  static void printHelpMessage() {
    System.out.println("usage:");
    System.out.println("  java ...WWCNamingServer");
    System.out.println("  java ...WWCNamingServer -h");
    System.out.println("  java ...WWCNamingServer -v");
    System.out.println("  java ...WWCNamingServer -p portNumber");
    System.out.println("options:");
    System.out.println("  -h : print this message");
    System.out.println("  -v : print version");
    System.out.println("  -p portNumber : set the listening port to portNumber");
    System.out.println("                  default port number is 3030");
  }



  /*
   *
   */
  public static void handleRequest(Socket clientSocket) {
    BufferedReader ins=null ;
    PrintWriter outs=null;
    try{
      ins = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      outs = new PrintWriter(clientSocket.getOutputStream(), true);

      for (;;) {
        String version = ins.readLine();
        if (version==null) {continue;}
        String command = ins.readLine();
        String parameter1 = ins.readLine();
        String parameter2 = ins.readLine();
        if (!version.equals(UANProtocol.VERSION)) {
          outs.println(UANProtocol.VERSION + "\n"
                       + UANProtocol.BAD_REQ_STATUS_CODE + "\n"
                       + "Wrong Version" + "\n"
                       );
          continue;
        }
        else {
          //handle GET command
          if (command.equals(UANProtocol.GET_REQUEST_CODE)) {
            String mappedUAL="";
            synchronized(locationMap) {
              mappedUAL = (String) locationMap.get(parameter1);
            }
            if (mappedUAL!=null) {
              outs.println(UANProtocol.VERSION + "\n"
                           + UANProtocol.FOUND_STATUS_CODE + "\n"
                           + mappedUAL + "\n"
                           );
              System.out.println("Query " + parameter1+ " Get "+ mappedUAL +", from "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
            } else {
              outs.println(UANProtocol.VERSION + "\n"
                           + UANProtocol.NOT_FOUND_STATUS_CODE + "\n"
                           + UANProtocol.NOT_FOUND_STATUS_STR + "\n"
                           );
              System.out.println("Query " + parameter1+ " Get "+ mappedUAL +", from "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
            }
            continue;

          //handle PUT command
          } else if (command.equals(UANProtocol.PUT_REQUEST_CODE)) {
            if (parameter2!=null) {
              synchronized (locationMap) {
                locationMap.put(parameter1, parameter2);
              }
              outs.println(UANProtocol.VERSION + "\n"
                           + UANProtocol.MODIF_STATUS_CODE + "\n"
                           + UANProtocol.MODIF_STATUS_STR + "\n"
                           );
            }
            System.out.println("Bind " + parameter1+ " to "+ parameter2 +", from "+clientSocket.getInetAddress()+":"+clientSocket.getPort());

          } else if (command.equals(UANProtocol.DEL_REQUEST_CODE)) {
            Object obj=null;
            synchronized(locationMap) {
              obj=locationMap.remove(parameter1);
            }
              if ( obj!= null) {
                outs.println(UANProtocol.VERSION + "\n"
                             + UANProtocol.DEL_STATUS_CODE + "\n"
                             + UANProtocol.DEL_STATUS_STR + "\n"
                             );
                System.out.println("Delete " + parameter1 + ", from " +
                                   clientSocket.getInetAddress() + ":" +
                                   clientSocket.getPort());
              }
          } else {
            outs.println(UANProtocol.VERSION + "\n"
                         + UANProtocol.BAD_REQ_STATUS_CODE + "\n"
                         + UANProtocol.BAD_REQ_STATUS_STR + "\n"
                         );
            continue;
          }
        }
      }
    }catch (IOException e) {
      System.out.println("Connection Closed: "+clientSocket.getInetAddress().getHostName()+":"+clientSocket.getPort());
      try {
        outs.close();
        ins.close();
        clientSocket.close();
      } catch (Exception exc) {}
      return;
    }
  }
}
