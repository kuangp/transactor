/* SALSA/World Wide Computer Porject
 *
 * Location Server - Universal Actor Name Daemon
 * Provides the location (Universal Actor Location) of an actor running on
 * the World Wide Computer from its name (Universal Actor Name).
 *
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 *
 */

package wwc.naming;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
* Parsing and processing of the initial request comming from a stream.
*
* @version %I%, %G%
* @author Gregory Haik, Carlos Varela
*/
public class LocationRequest {
	String unprocessedRequest;
	Hashtable locationMap;

	/**
	 * Initializes instances variables by reading on the stream and setting up the
	 * local reference of the location mapping.
	 * @param in    the BufferedReader the request goes out from.
	 * @param map   the Hashtable that stands for the mapping from UAN to UAL.
	 */
	LocationRequest(BufferedReader in, Hashtable map) throws BadUANPRequestException {
		this.locationMap = map;
		try {
			this.unprocessedRequest = in.readLine();
			System.out.println("Request: "+unprocessedRequest);
			if  (unprocessedRequest == null)
				throw new BadUANPRequestException();
		} catch (IOException e) {
			throw new BadUANPRequestException();
		}
	}

	/**
	 * Tokenizes the request and initiates the parsing.
	 */
	public String process() throws BadUANPRequestException {
		StringTokenizer st = new StringTokenizer(unprocessedRequest);
		String requestType = new String(st.nextToken());
		if (requestType.equals("GET"))
			return (new GETRequest(st)).process(locationMap);
		else if (requestType.equals("PUT"))
			return (new PUTRequest(st)).process(locationMap);
		else if (requestType.equals("DEL"))
			return (new DELRequest(st)).process(locationMap);
		else
			throw new BadUANPRequestException();
	}
}

