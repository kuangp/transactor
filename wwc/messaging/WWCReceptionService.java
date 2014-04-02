/** SALSA/World Wide Computer Project
 *  ObjectHandler recieves incoming objects
 *  and determines if they are actors or messages.
 *  upon recieving actors, it will start them up.
 *  messages get sent to the appropriate actor.
 *
 *  author Travis Desell
 */
package wwc.messaging;

import java.net.*;
import java.io.*;
import salsa.language.Actor;
import salsa.language.Message;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import salsa.language.ActorReference;
import salsa.messaging.*;

import salsa.naming.NamingService;

import salsa.language.Placeholder;
import salsa.language.UniversalActor;
import salsa.naming.UAL;
import gc.*;


public class WWCReceptionService implements ReceptionService {
        NamingService namingService = ServiceFactory.getNaming();

        public WWCReceptionService() {
                ServiceFactory.getSystem();
                ServiceFactory.getGCAgent();
        }

        public void processActor(Actor incomingState, Socket incoming) {
                System.err.println("Reception Service Error:");
                System.err.println("\tError receiving actor: " + incomingState.toString());
                System.err.println("\tActors should not be serialized and sent directly to a theater via the transport service.\n");

                RunTime.receivedUniversalActor();
        }

        public void processMessage(Message message) {
                // Send the message.
                Object ref=namingService.getTarget(message.getTarget());
                if (ref==null) {
System.out.println("message loss:"+message);                  //message loss
                }
                /*else if (ref instanceof UAL) {
                        //sourceSystem<-MessageTargetNotFound(message);
                        if (message.getSource() == null) {
                                System.err.println("Error sending messageTargetNotFound message:");
                                //System.err.println("\tSource is: " + message.getSource());
                                System.err.println("\tTarget is: " + message.getTarget());
                                System.err.println("\tMessage: " + message.getMethodName());
                        }

                        WWCSystem sourceSystem = (WWCSystem)WWCSystem.getReferenceByLocation(message.getSource().getUAL().getLocation() + "salsa/System");

                        Object[] arguments = { message };
                        Message NoTargetMessage = new Message(null, sourceSystem, "messageTargetNotFound", arguments, null, null,false);

                        sourceSystem.send(NoTargetMessage);

//System.err.println("Target not found:"+message.toString());
                }*/
                else {
                        message.getTarget().send(message);
                }
        }

        public void processSystemMessage(SystemMessage sysMessage) {
          // Send the message.
          ActorReference ref=sysMessage.getTarget();
          Object target=namingService.sysGetTarget(ref);
          if (target != null) {
             sysMessage.getTarget().send(sysMessage);
           }
         }

        public void process(Socket incoming) {
          // Read in the object.
          Object received = null;
          ObjectInputStream stream = null;

            try {
              stream = new ObjectInputStream(incoming.getInputStream());
            }
            catch (IOException e) {
              System.err.println("Reception Service error: ");
              System.err.println("\tIOException occured reading incoming object.");
              System.err.println("\tException: " + e);
              System.err.println("\tException Message: " + e.getMessage());
      //			e.printStackTrace();
              return;
            }

            while(true) {
              try{
                received = stream.readObject();
              }
              catch (ClassNotFoundException e) {
                System.err.println("Reception Service error: ");
                System.err.println("\tCould not load class for an incoming object");
                System.err.println("\tIs it in the theater CLASSPATH?");
                System.err.println("\tException: " + e.getMessage());
                e.printStackTrace();
                return;
              }
              catch (IOException e) {
                //System.err.println("Reception Service error: ");
                //System.err.println("\tIOException occured closing connection.");
                //System.err.println("\tException: " + e.getMessage());
                //e.printStackTrace();
                return;
              }

            if (received instanceof Actor) {
              Actor incomingActor = (Actor) received;
              processActor(incomingActor, incoming);
            }
            else if (received instanceof Message) { // just serializing data
              Message message = (Message) received;
              processMessage(message);
//System.out.println("  rec:"+message.getMethodName());
            }
            else if (received instanceof SystemMessage) {
              SystemMessage sysMessage = (SystemMessage) received;
              processSystemMessage(sysMessage);
//System.out.println("  rec:"+sysMessage.getMethodName());
            }

          }
        }
}
