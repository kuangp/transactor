package wwc.naming;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import java.util.Hashtable;
import java.util.Vector;
import java.util.LinkedList;

import salsa.language.Actor;
import salsa.language.ActorReference;
import salsa.language.ServiceFactory;
import salsa.language.exceptions.SalsaException;
import salsa.language.exceptions.SalsaWrapperException;
import salsa.naming.NamingService;
import salsa.naming.UAL;
import salsa.naming.UAN;
import salsa.messaging.TheaterService;

/**
 * This class implements a UANP client. After the class is constructed, several
 * threads may pass through it to conduct UANP operations through the
 * NamingService interface in salsa.language.
 *
 * @version $Id: WWCNamingService.java,v 1.4 2007/02/17 15:43:05 laporj2 Exp $
 * @author Abe Stephens -- stepha@rpi.edu
 * @author Travis Desell
 * @author Carlos Varela
 */
public class WWCNamingService implements NamingService {

	public WWCNamingService() {}

        /**
         * These methods provide a way to generate a unique identifier
         * for every token used by a SALSA program and a default UAL for
         * new actors in the theater.
         */
	private int generatedTokens = 0;
	private int generatedLocations = 0;
	private TheaterService theater = ServiceFactory.getTheater();
        private long timestamp=System.currentTimeMillis();
	public synchronized String getUniqueMessageId() {
		return theater.getLocation() + "MSG/" + timestamp+"/"+(++generatedTokens);
	}
	public synchronized UAL generateUAL() {
		return new UAL(theater.getLocation() + "UAL/"+ timestamp+"/"+(++generatedTokens));
	}
        public synchronized UAN generateUAN(String nameServer) {
          return new UAN("uan://"+ nameServer + "/UAN/" +theater.getID()+"/" + timestamp+"/"+(++generatedTokens));
        }

        /**
         * The following members and functions are used for
         * persistent socket connections to the name servers
         */

        public class SocketWrap {
          String hostID=null;
          Socket socket=null;
          BufferedReader in =null;
          PrintWriter out =null;

          private int retry=2;
          public SocketWrap(String serverString,Socket serverSocket) {
            this.hostID=serverString;
            this.socket=serverSocket;
            try {
              in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {e.printStackTrace();}
          }
          public synchronized Socket getSocket() {retry=2;return this.socket;}
          public synchronized BufferedReader getIn() {retry=2;return in;}
          public synchronized PrintWriter getOut() {retry=2;return out;}

          public String getID() {return this.hostID;}
          public synchronized void die() {
            try{
              in.close();
              out.close();
              socket.close();
            }catch (Exception e) {return ;}
          }

          public synchronized boolean resumeSocket() {
            die();
            int index=0;
            String host="";
            int port=4040;
            try {
              for (index=0;index<hostID.length();index++) {
                if (hostID.charAt(index)==':') {break;}
              }
              if (index< hostID.length()-1) {
                port=Integer.parseInt(hostID.substring(index+1,hostID.length()));
              }
              host=hostID.substring(0,index);
              socket=new Socket(host,port);
              in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {
              return false;
            }
//System.out.println("resume socket!=================================");
            return true;
          }

          public  synchronized boolean isDead() {return retry<0;}
          public synchronized void decRetry() {retry--;}
        }

        private Vector nameServer=new Vector();

        public synchronized void removeNameServerSocketWrap(String targetName) {
          for (int i=0;i<nameServer.size();i++) {
            SocketWrap swarp = (SocketWrap) nameServer.get(i);
            if (swarp.getID().equals(targetName)) {
              nameServer.remove(i);
//System.out.println("  socket found:"+targetName);
              return;
            }
          }
        }

        public synchronized SocketWrap getNameServerSocketWrap(UAN name) {
          String targetFromName=name.getHost()+":"+ name.getPort();
          for (int i=0;i<nameServer.size();i++) {
            SocketWrap swarp=(SocketWrap)nameServer.get(i);
            if ( swarp.getID().equals(targetFromName)) {
//System.out.println("  socket found:"+targetFromName);
              return swarp;
            }
          }
          //no name server socket is found, which means a new one has to be created.
          try {
            Socket s = new Socket(name.getHost(), name.getPort());
//System.out.println("  socket created:"+targetFromName);
            SocketWrap ret=new SocketWrap(targetFromName,s);
            nameServer.addElement(ret);
            return ret;
          }catch (Exception e) {
            e.printStackTrace();
            return null;
          }
        }

	/*
	 *	The UAL Registry contains UAL to UAN mappings in
	 *	the case of an actor with a universal name,
	 *	and UAL to actor mappings in the case of anonymous
	 *	actors.
	 *	?? Occasionally an actor will be renamed (UAL changed
	 *	?? without having a UAN), in this case the ualRegistry
	 *	?? has a mapping to a UAL.
	 */
	private Hashtable ualRegistry = new Hashtable();

	/*
	 *	The UAN Registry contains UAN to actor mappings for
	 *	local actors.
	 */
	private Hashtable uanActorRegistry = new Hashtable();

        /*
         *      UAN to UAL Mappings for remote actors.
         */
        private Hashtable uanUalRegistry=new Hashtable();

        public synchronized void refreshUanUalTable() {
          if (uanUalRegistry.size()>1000) uanUalRegistry.clear();
        }

        public synchronized String queryLocation(UAN uan) {
          UAL retVal;
          try {
            if (uan != null) {
              Object value = uanActorRegistry.get(uan.toString());
              if (value == null) {
                value = uanUalRegistry.get(uan.toString());
              }
              else
                return ((Actor)value).getUAL().getLocation();
              if (value == null) {
                value = get(uan);
              }
              else
                return ((UAL)value).getLocation();
              if (value == null) {
                uanUalRegistry.remove(uan.toString());
                return null;
              }
              else {
                refreshUanUalTable();
                uanUalRegistry.put(uan.toString(), value);
                return ((UAL)value).getLocation();
              }
            }
          }catch (Exception e) {}
          return null;
        }

	public synchronized Object getTarget(ActorReference actorReference) {
		UAN uan = actorReference.getUAN();
		Object retVal = null;

		if (actorReference.getUAN() == null && actorReference.getUAL() == null) {
			System.err.println("Message Sending Error:");
			System.err.println("\tActor Reference is invalid, and has no UAN and UAL");
                        return null;
		}

		if (uan != null) {
                  Object value = uanActorRegistry.get(uan.toString());
                  if (value == null) {
                    value = uanUalRegistry.get(uan.toString());
                  }
                  if (value == null) {
                    retVal = get(uan);
                    if (retVal==null) {
                      uanUalRegistry.remove(uan.toString());
                      return null;
                    }
refreshUanUalTable();
                    uanUalRegistry.put(uan.toString(), retVal);

                  } else retVal = value;
		} else {
			retVal = ualRegistry.get( actorReference.getUAL().toString() );
			if (retVal == null) retVal = actorReference.getUAL();
			//else if (retVal instanceof UAN) {
			//	actorReference.setUAN((UAN)retVal);
			//	retVal = getTarget(actorReference);
			//}
		}
		return retVal;
	}

        public synchronized Object sysGetTarget(ActorReference actorReference) {
                UAN uan = actorReference.getUAN();
                Object retVal = null;

                if (actorReference.getUAN() == null && actorReference.getUAL() == null) {
                        System.err.println("Message Sending Error:");
                        System.err.println("\tActor Reference is invalid, and has no UAN or UAL");
                        return null;
                }

                if (uan != null) {
                        Object value = uanActorRegistry.get(uan.toString());
                        if (value==null) {value = uanUalRegistry.get(uan.toString());}
                        if (value == null) {
                                retVal = get(uan);
                                if (retVal==null) {
                                  uanUalRegistry.remove(uan.toString());
                                  return null;
                                }
refreshUanUalTable();
                                uanUalRegistry.put(uan.toString(), retVal);
                        } else retVal = value;
                } else {
                        retVal = ualRegistry.get( actorReference.getUAL().toString() );
                        if (retVal == null) return null;
                        //else if (retVal instanceof UAN) {
                        //        actorReference.setUAN((UAN)retVal);
                        //        retVal = getTarget(actorReference);
                        //}
                }
                return retVal;
        }

        //To see if some <UAN,UAL> entry of the local naming service is
        //  consistent to the naming server.
        //  Return a UAL if
        //     1. the local naming service has an entry <UAN,UAL>
        //     2. the entry <UAN,UAL> in the naming server is different
        //  Return null otherwise.
        public synchronized UAL validateEntry(ActorReference actorReference) {
          UAN uan = actorReference.getUAN();
          Object retVal = null;

          if (actorReference.getUAN() == null && actorReference.getUAL() == null) {
                  System.err.println("Message Sending Error:");
                  System.err.println("\tActor Reference is invalid, and has no UAN or UAL");
                  return null;
          }

          if (uan != null) {
                  Object value = uanUalRegistry.get(uan.toString());
                  retVal = get(uan);
                  if (retVal ==null) {
                    uanUalRegistry.remove(uan.toString());
                    return null;
                  }
                  else if (retVal instanceof UAL) {
                    if (!((UAL)retVal).equals((UAL)value)) {
refreshUanUalTable();
                      uanUalRegistry.put(uan.toString(), retVal);
                      return (UAL)retVal;
                    }
                  }
          }
          return null;
        }

	public synchronized void setEntry(UAN uan, UAL ual, Actor actor) {
          if (uan != null) {
            if (actor != null) {
              uanActorRegistry.put(uan.toString(), actor);
              uanUalRegistry.remove(uan.toString());
            }
            else {
              refreshUanUalTable();
              uanUalRegistry.put(uan.toString(), ual);
            }
              //ualRegistry.put(ual.toString(), uan);
          } else {
            ualRegistry.put(ual.toString(), actor);
          }
        }


        public synchronized Actor getSourceActor(ActorReference sourceRef) {
          if (sourceRef==null) return null;
          Object value=null;
          if (sourceRef.getUAL() != null) value = ualRegistry.get(sourceRef.getUAL().toString());
          if (sourceRef.getUAN() != null) value = uanActorRegistry.get(sourceRef.getUAN().toString());
          if (value instanceof Actor) return (Actor)value;
          else return null;
        }

        public synchronized Hashtable getUALTable() {
          return this.ualRegistry;
        }

        public synchronized Hashtable getUANTable() {
          return this.uanActorRegistry;
        }

        public synchronized Hashtable getUANUALTable() {
          return this.uanUalRegistry;
        }

	public synchronized Actor remove(UAN uan, UAL ual) {
		Object value = null;

		if (ual != null) value = ualRegistry.remove(ual.toString());
                if (value instanceof Actor) {
                  return (Actor) value;
                }

                if (uan != null) {
                  uanUalRegistry.remove(uan.toString());
                  value = uanActorRegistry.remove(uan.toString());
                }

		if (value instanceof Actor) {
                  return (Actor) value;
                }
		else return null;
	}

	public synchronized void refreshReference(ActorReference actorReference) {
		if (getTarget(actorReference) instanceof Actor) return;

		UAN uan = actorReference.getUAN();
		UAL newUAL = get(uan);
                if (newUAL==null) {
                  uanUalRegistry.remove(uan.toString());
                }
		else if ( (uanActorRegistry.get(uan)==null) ) {
                  actorReference.setUAL( newUAL );
refreshUanUalTable();
                  uanUalRegistry.put(uan.toString(), newUAL);
		}

	}

/*
 *	The following methods implement the basic functionality for
 *	accessing the Naming Server
*/
	/**
	 * This method provides UANP GET functionality.
	 *
	 * @see salsa.language.NamingService#resolve(salsa.language.UAN)
	 */
	public UAL get(UAN name) {
          if (name==null) {return null;}
          String version=null;
          String returnCode=null;
          String result=null;
          String request=UANProtocol.VERSION + "\n" +
                            UANProtocol.GET_REQUEST_CODE + "\n" +
                            name.getIdentifier() + "\n";
          SocketWrap socketWrap=getNameServerSocketWrap(name);
          synchronized (this) {
            try {
              BufferedReader in = socketWrap.getIn();
              PrintWriter out = socketWrap.getOut();
              out.println(request);
              out.flush();
              version = in.readLine();
              returnCode = in.readLine();
              result = in.readLine();
              in.readLine();
            }
            catch (java.net.SocketException e) {
              if (socketWrap.resumeSocket()) {
                try {
                  BufferedReader in = socketWrap.getIn();
                  PrintWriter out = socketWrap.getOut();
                  out.println(request);
                  out.flush();
                  version = in.readLine();
                  returnCode = in.readLine();
                  result = in.readLine();
                  in.readLine();
                }
                catch (IOException ioe) {}
              }
              else {
                //e.printStackTrace();
                throw new SalsaWrapperException("UANP GET Failed: " + name, e);
              }
            }
            catch (IOException e) {
              throw new SalsaWrapperException("UANP GET Failed: " + name, e);
            }
          }

          if (!version.equals(UANProtocol.VERSION)){
            throw new SalsaException("UANP " + "Version Error: " +
                                     UANProtocol.VERSION + " and " + version);
          }
          else if (!returnCode.equals(UANProtocol.FOUND_STATUS_CODE)){
            //throw new SalsaException("UANP " + name + " Error: " + result);
            return null;
          }
          else
            return new UAL(result);
        }

	/**
	 * This method provides UANP PUT functionality.
	 *
	 * @see salsa.language.NamingService#add(salsa.language.UAN, salsa.language.UAL)
	 */
	public void add(UAN name, UAL location) {
          String version=null;
          String returnCode=null;
          String result=null;
          SocketWrap socketWrap=getNameServerSocketWrap(name);
          String request=UANProtocol.VERSION + "\n" +
              UANProtocol.PUT_REQUEST_CODE + "\n" +
              name.getIdentifier() + "\n" + location;
          synchronized (this) {
            try {
              BufferedReader in =socketWrap.getIn();
              PrintWriter out =socketWrap.getOut();
              out.println(request);
              out.flush();
              version = in.readLine();
              returnCode = in.readLine();
              result = in.readLine();
              in.readLine();
            }
            catch (java.net.SocketException e) {
              if (socketWrap.resumeSocket()) {
                try{
                  BufferedReader in =socketWrap.getIn();
                  PrintWriter out =socketWrap.getOut();
                  out.println(request);
                  out.flush();
                  version = in.readLine();
                  returnCode = in.readLine();
                  result = in.readLine();
                  in.readLine();
                }
                catch (IOException ioe) {
                  throw new SalsaWrapperException( "UANP PUT Failed in the Second Try: ", e );
                }
              } else {
                throw new SalsaWrapperException( "UANP PUT Failed in the Second Try: ", e );
              }

            }
            catch (IOException e) {
//e.printStackTrace();
              throw new SalsaWrapperException( "UANP PUT Failed: ", e );
            }
          }
          if (!version.equals(UANProtocol.VERSION))
            throw new SalsaException("UANP " + "Version Error: " + UANProtocol.VERSION +" and " +version);
          else if (!returnCode.equals(UANProtocol.MODIF_STATUS_CODE))
            System.err.println("UANP PUT " + name + " Error: " + result);
        }

	/**
         * This method is equivlent to add( name, location );
         *
         * @see salsa.language.NamingService#update(salsa.language.UAN, salsa.language.UAL)
         */
	public void update(UAN name, UAL location) {
		add( name, location );
	}

	/**
	 * This method is not implemented.
	 *
	 * @see salsa.language.NamingService#delete(salsa.language.UAN, salsa.language.UAL)
	 */
	public void delete(UAN name) {
          if (name==null) {return;}
          String version=null;
          String returnCode=null;
          String result=null;
          SocketWrap socketWrap=getNameServerSocketWrap(name);
          String request=UANProtocol.VERSION + "\n" +
              UANProtocol.DEL_REQUEST_CODE + "\n" +
              name.getIdentifier() + "\n" ;
          synchronized (this) {
            uanActorRegistry.remove(name.toString());
            uanUalRegistry.remove(name.toString());
            try {
              BufferedReader in =socketWrap.getIn();
              PrintWriter out =socketWrap.getOut();
              out.println(request);
              out.flush();
              version = in.readLine();
              returnCode = in.readLine();
              result = in.readLine();
              in.readLine();
            }
            catch (java.net.SocketException e) {
              if (socketWrap.resumeSocket()) {
                try{
                  BufferedReader in =socketWrap.getIn();
                  PrintWriter out =socketWrap.getOut();
                  out.println(request);
                  out.flush();
                  version = in.readLine();
                  returnCode = in.readLine();
                  result = in.readLine();
                  in.readLine();
                }
                catch (IOException ioe) {
                  throw new SalsaWrapperException( "UANP DEL Failed in the Second Try: ", e );
                }
              } else {
                throw new SalsaWrapperException( "UANP DEL Failed in the Second Try: ", e );
              }
            }
            catch (IOException e) {
//e.printStackTrace();
              throw new SalsaWrapperException( "UANP PUT Failed: ", e );
            }
          }
          if (!version.equals(UANProtocol.VERSION))
            System.err.println("UANP DEL " + name + " Error: " + result);
            //throw new SalsaException("UANP " + "Version Error: " + UANProtocol.VERSION +" and " +version);
          else if (!returnCode.equals(UANProtocol.DEL_STATUS_CODE))
            System.err.println("UANP DEL " + name + " Error: " + result);
//          throw new SalsaException( "This naming service does not implement delete." );

	}
}
