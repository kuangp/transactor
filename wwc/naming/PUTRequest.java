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

import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import salsa.naming.UAL;
import salsa.naming.MalformedUALException;

/**
 * Parsing and processing of a PUT request.
 *
 * @version %I%, %G%
 * @author Gregory Haik, Carlos Varela
 */
public class PUTRequest {
	String localName;
	String newUAL;

	/**
	 * Parses the request (already tokenized) in order to extract the local name and
	 * the new location (UAL) of the actor to be deleted. If the request is not properly
	 * parsed, a <code>BadUANPRequestException</code> is thronw.
	 *
	 * @param st    the StringTokenizer where the request should be. Notice that
	 *              the request type (GET, PUT, DEL) is supposed to be already
	 *              parsed. Thus, it sould not be anymore in the tokenizer.
	 */
	public PUTRequest(StringTokenizer st) throws BadUANPRequestException {
		try {
			localName = st.nextToken();
			newUAL = st.nextToken();
			st.nextToken();
		} catch (NoSuchElementException e) {
			throw new BadUANPRequestException();
		} catch (MalformedUALException e) {
			System.out.println("MalformedUALException thrown");
			throw new BadUANPRequestException();
		}
	}

	/**
	 * Generates the response.
	 *
	 * @param mapping the Hashtable that stands for the mapping from UANs to UALs.
	 * @return a string containing the response. If the request has been properly
	 * parsed, the response will be  <code>201 Database Modified</code>.
	 *
	 */
	public String process(Hashtable map) {
		map.put(localName, newUAL);
		String response = new String();
		return new String(UANProtocol.VERSION
						  +" "
						  +UANProtocol.MODIF_STATUS_CODE
						  +" "
						  +UANProtocol.MODIF_STATUS_STR
						  +"\n");
	}
}
