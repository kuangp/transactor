/* SALSA/World Wide Computer Porject
 *
 * Reference Server - Remote Message Sending Protocol Daemon
 * In order to deliver a message to a remote actor, messengers
 * reach this daemon (that can also host actors) to get the (unique)
 * hash code of the target actor. The rmspd is in charge to forward
 * messengers to the target's stage (rmspd's host or remote applet).
 *
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 *
 */
package wwc.messaging;


/**
 * Defines the constants of the Remote Message Sending Protocol v0.1.
 * Can not be instanciated.
 * 
 * @version %I%, %G%
 * @author Gregory Haik, Carlos Varela
 */

public class RMSProtocol {
/**
 * This private constructor guarantees that this class will never
 * be instanciated.
 */    
    private RMSProtocol() {}; 
 
    public static String VERSION = "RMSPD/0.1";

    public static boolean usingMessengers = false;
}
