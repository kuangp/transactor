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

/**
 * Parsing and processing of a GET request.
 *
 * @version %I%, %G%
 * @author Gregory Haik, Carlos Varela
 */
public class GETRequest {
	String localName;

	/**
	 * Parses the request (already tokenized) in order to extract the local name of
	 * the actor to be located. If the request is not properly parsed, a
	 * <code>BadUANPRequestException</code> is thronw.
	 *
	 * @param st    the StringTokenizer where the request should be. Notice that
	 *              the request type (GET, PUT, DEL) is supposed to be already
	 *              parsed. Thus, it sould not be anymore in the tokenizer.
	 */
	public GETRequest(StringTokenizer st) throws BadUANPRequestException {
		try {
			localName = st.nextToken();
			st.nextToken();
		} catch (NoSuchElementException e) {
			throw new BadUANPRequestException();
		}
	}

	/**
	 * Generates the response.
	 *
	 * @param mapping the Hashtable that stands for the mapping from UANs to UALs.
	 * @return a string containing the response. If the local name is not found on this
	 * server, the response is <code>404 Name Not Found</code>. Otherwise it is
	 * <code>200 Location Found</code>.
	 */
	public String process(Hashtable map) {
		String response = new String( UANProtocol.VERSION );
		String mappedUAL = (String) map.get(localName);
		if (mappedUAL == null)
			response += " "
						+UANProtocol.NOT_FOUND_STATUS_CODE
						+" "
						+UANProtocol.NOT_FOUND_STATUS_STR
						+"\n";
		else
			response += " "
						+UANProtocol.FOUND_STATUS_CODE
						+" "
						+UANProtocol.FOUND_STATUS_STR
						+"\n"
						+mappedUAL
						+"\n";
		return response;
	}
}




