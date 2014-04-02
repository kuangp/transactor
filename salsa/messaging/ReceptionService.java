package salsa.messaging;

import java.net.Socket;

/**
        This interface contains method for message sending.

        @author stepha
*/
public interface ReceptionService {

        /**
                This method processes objects from an incoming socket.
        */
        public void process( Socket incoming );
}
