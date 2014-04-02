/**
 * $Id: Actor.java,v 1.2 2006/09/22 03:38:22 wangw5 Exp $
 *
 * This is the Actor interface, used by the WWC and IO, as well as Local Salsa.
 *
 * *************************
 * GC support:
 * 1. add two abstract functions for processing system messages:
 *     public void putMessageInMailbox(SystemMessage message);
 *     public void process(SystemMessage message);
 *
 */

package salsa.language;

import java.util.Vector;
import java.lang.reflect.Method;

import salsa.language.exceptions.CurrentContinuationException;
import salsa.naming.UAN;
import salsa.naming.UAL;

import gc.SystemMessage;

public interface Actor {
        /*******
         * gc interface :
         * 1. public void putMessageInMailbox(SystemMessage message);
         * 2. public void process(SystemMessage message);
         ******/

         public void putMessageInMailbox(SystemMessage message);
         public void process(SystemMessage message);

	public UAN getUAN();
	public UAL getUAL();
	public void setUAL(UAL ual);

	public void start();
	public void destroy();
	public void process(Message message);
	public void putMessageInMailbox(Message message);
	public void sendGeneratedMessages();
	public void updateSelf(ActorReference actorReference);

	public Method[] getMatches(String methodName);
	public void addMethod(String methodName, Method method);

	public String toString();
        public boolean equals(Object o);
}
