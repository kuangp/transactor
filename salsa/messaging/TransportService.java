/*************
 * GC support:
 *  public void send( SystemMessage message, ActorReference target );
 *************/


package salsa.messaging;

import salsa.language.Actor;
import salsa.language.ActorReference;
import salsa.language.Message;
import salsa.naming.UAL;

import java.util.Vector;
import gc.SystemMessage;
/**
	This interface contains method for message sending.

	@author stepha
*/
public interface TransportService {

	/**
		This method sends the specified message to an actor at the target theater specified
		by UAL.
	*/
	public void send( Message message, ActorReference target );
        public void send( SystemMessage message, ActorReference target );
        public void timeoutSend(long timeout,ActorReference target);

	/**
		This method migrates the specified actor to the target theater.
	*/
	public void migrate( Actor actor, UAL target );
        public boolean isMessageInTransit();
}
