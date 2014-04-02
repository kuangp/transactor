package salsa.messaging;

import salsa.language.Actor;
import salsa.naming.UAL;
import salsa.naming.UAN;
import salsa.language.ActorReference;

/**
	This Interface represents the most basic services a theater must provide a salsa program.

	@author stepha
*/

public interface TheaterService {
	/**
		This is used to find the location of the theater
	*/
	public String getLocation();
	public String getHost();

	/**
		This method returns the port that the theater is currently listening on i.e. 4040.
		If the theater is not actively listening, -1 is returned.
	*/
	public int getPort();
        public String getID();

        public boolean isRestricted();
        public boolean checkSecurityEntry(String ref) ;
        public void registerSecurityEntry(String  ref) ;
        public void removeSecurityEntry(String ref) ;
}
