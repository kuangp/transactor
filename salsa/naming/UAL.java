/* SALSA/World Wide Computer Porject
 *
 * UAL - Universal Actor Location
 *
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 *
 */

package salsa.naming;

import java.lang.Exception;
import java.lang.String;
import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

/**
 * Universal Actor Location. Contains enough data for an actor to be reached in
 * the World Wide Computer, that is, the hostname and the port number of the
 * RMSPDaemon (Remote Message Sending Protocol), and its local name.
 * These informations are not authoritative (actor may have migrated), but those
 * contained in the UAN (Universal Actor Name) are.
 */
public class UAL extends URI {

	public String getLocation() {
		return "rmsp://" + url.getHost() + ":" + url.getPort() + "/";
	}

	/**
	 * Builds a UAL from a given string.
	 * @param s   the string standing for the UAL.
	 */
	public UAL(String s) throws MalformedUALException {
		try {
			protocol = "rmsp";
			int colonPosition = s.indexOf(':');
			if ( ! (protocol.equals( s.substring(0, colonPosition)))) throw new Exception();

			url = new URL("http"+s.substring(colonPosition));
		} catch (Exception e) { throw new MalformedUALException("Malformed UAL : "+s); }

		//resolve the host name, to fix problems with UAL resolution
		try {
			url = new URL(url.getProtocol(),
				 InetAddress.getByName(translateLocalHostName(url.getHost())).getHostAddress(),
				 url.getPort(),
				 url.getFile());
		} catch (UnknownHostException e) {
			if (url.getHost().equals("localhost")) {
				try {
					url = new URL( "http"+s.substring(s.indexOf(':')) );
				} catch (MalformedURLException e2) {
					throw new MalformedUALException("Malformed UAL : "+s);
				}
			} else {
				throw new MalformedUALException("Unknown host for UAL : "+s);
			}
		} catch (MalformedURLException e) {
			throw new MalformedUALException("Malformed UAL : "+s);
		}
	}

	public int getPort() {
		if (url.getPort() == -1) return 4040;
		return url.getPort();
	}

        public String toString() {
          //System.out.println("the uan="+"uan://" + url.getHost() + ":" + getPort()  + url.getFile());
          return "rmsp://" + url.getHost() + ":" + getPort()  + url.getFile();
        }

}

