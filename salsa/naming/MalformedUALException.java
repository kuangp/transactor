/* SALSA/World Wide Computer Porject
 *
 * UAL - Universal Actor Location
 * 
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 *
 */

package salsa.naming;

import salsa.language.exceptions.SalsaException;

/**
 * Thrown whenever parsing of a UAL has failed.
 */
public class MalformedUALException extends SalsaException {
    public MalformedUALException(java.lang.String s) {
	super(s);
    }
}
