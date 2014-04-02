package salsa.naming;

import salsa.language.Actor;
import salsa.language.ActorReference;

/**
	This interface contains methods for manipulating a naming service.

	@author stepha
*/
public interface NamingService {

	public UAL generateUAL();
	public String getUniqueMessageId();
        public UAN generateUAN(String nameServer);
        public Object sysGetTarget(ActorReference actorReference);
	public Object getTarget(ActorReference actorReference);
	public void setEntry(UAN uan, UAL ual, Actor actor);
	public Actor remove(UAN uan, UAL ual);
        public Actor getSourceActor(ActorReference sourceRef);
	public void refreshReference(ActorReference actorReference);
        public UAL validateEntry(ActorReference actorReference);
        public java.util.Hashtable getUANUALTable();
        public java.util.Hashtable getUANTable();
        public java.util.Hashtable getUALTable();
        public String queryLocation(UAN uan);
	/**
		This method returns the location associated with the specified UAN
		or null if the UAN does not exist.
	*/
	public UAL get( UAN name );

	/**
		This method adds the specified name location mapping to the name service.
	*/
	public void add( UAN name, UAL location );

	/**
		This method updates the specified name location mapping in the name service.
	*/
	public void update( UAN name, UAL location );

	/**
		This method deletes the specified name location mapping from the name service.
	*/
	public void delete( UAN name);
}
