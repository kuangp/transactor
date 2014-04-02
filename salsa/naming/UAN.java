/* SALSA/World Wide Computer Project
 *
 * UAN : Universal Actor Name.
 *
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 *
 */

package salsa.naming;

import java.net.*;

/**
 * Universal Actor Name. Contains enough data to retrieve the location (UAL) of an acor
 * running in the World Wide Computer, that is, the hostname and the port number of the
 * UANDaemon (location server), and its local name on this server.
 * These informations are authoritative (actor may have migrated), but those
 * contained in the UAL (Universal Actor Name) are not.
 */
public class UAN extends URI {

    public UAN(String s) throws MalformedUANException {
	try {
	    protocol = "uan";
	    int colonPosition = s.indexOf(':');
	    if ( ! (protocol.equals(s.substring(0, colonPosition))))
		throw new Exception();

	    url = new URL("http"+s.substring(colonPosition));


	} catch (Exception e) { throw new MalformedUANException("Malformed UAN : "+s); }
    }

    public String getLocation() {
        return "uan://" + url.getHost() + ":" + url.getPort() + "/";
    }

    public String getID() {
      if (url.getPort()==-1)
      return url.getHost() ;
      return url.getHost() + ":" + url.getPort();
    }

	/**
	 * This method returns the specified server port, or 3030 if the port has not been set.
	 *
	 * @see salsa.language.URI#getPort()
	 */
	public int getPort() {

		if (url.getPort() == -1) {
			return 3030;
		}

		return url.getPort();
	}

        public String toString() {
//System.out.println("the uan="+"uan://" + url.getHost() + ":" + getPort()  + url.getFile());
          return "uan://" + url.getHost() + ":" + getPort()  + url.getFile();
        }



/**
 * Returns the location (UAL) given by the UANDaemon running at the actor's bith place.
 * The birth place is the host contained in the UAN.
 */
/*
    public UAL getLocation() throws UANException {
	try {
	    Socket s = new Socket(url.getHost(), url.getPort());
	    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
	    out.println("GET "
			+ this.getIdentifier()
			+ " "
			+ UANProtocol.VERSION
			+ "\n");

	    String inputLine = in.readLine();
	    if (inputLine == null) throw new UANIOException("Server not responding.");

	    StringTokenizer st = new StringTokenizer(inputLine);
	    st.nextToken();
	    String returnCode = new String(st.nextToken());
	    if (!returnCode.equals("200"))
		throw new UANException(inputLine);

	    return new UAL((new StringTokenizer(in.readLine())).nextToken());

	} catch (IOException e) {
	    throw new UANIOException(e.getMessage());
	} catch (MalformedUALException e) {
	    throw new UANException("Location service has replied a malformed UAL"+
			       e.getMessage());
	    }
    }
	*/
    /**
     * Updates the UAN's location to the given UAL.
     * @param ual the new Universal Actor Location.
     */
    /*
    public void updateLocation(UAL ual) throws UANIOException{
	try {
	    Socket socket = new Socket(url.getHost(), url.getPort());
	    BufferedReader in = new BufferedReader
		(new InputStreamReader(socket.getInputStream()));
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    out.println("PUT "+this.getIdentifier()+" "+ual+" "+UANProtocol.VERSION);
	    out.flush();
	    String status = in.readLine();
	    // cvarela: Future UANP versions should read headers here.
	    String response = in.readLine();
// 	    System.out.println("Naming Server: "+status + response);
	    in.close();
	    out.close();
	    socket.close();
	} catch (IOException ioe){
	    throw new UANIOException(ioe.getMessage());
	}
    }
    */
}
