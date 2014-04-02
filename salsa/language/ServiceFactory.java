package salsa.language;

import java.lang.reflect.Constructor;

import salsa.naming.NamingService;
import salsa.naming.UAL;

import salsa.messaging.TheaterService;
import salsa.messaging.TransportService;
import salsa.messaging.ReceptionService;

import salsa.resources.OutputService;
import salsa.resources.ErrorService;
import salsa.resources.StorageService;
import salsa.resources.SystemService;
import salsa.resources.InputService;
import salsa.resources.Dummy;
import gc.LocalCollector;
import gc.serverGC.GCAgent;

/**
 * The ServiceFactory manufactures references to the standard location dependent
 * services used by an actor. These include the TheaterService,
 * TransportService, NamingService, and ReceptionService.
 *
 * In the case of a standalone actor, the ServiceFactory will create a theater
 * instance if one is needed. If a theater is already running a reference to
 * will be passed to the requesting actor.
 *
 * The ServiceFactory is implemented as a synchronized, passive, singleton.
 *
 * The default services are:
 *	-Dtheater=wwc.messaging.Theater
 *	-Dtransport=wwc.messaging.WWCTransportService
 *	-Dreception=wwc.messaging.WWCReceptionService
 *	-Dnaming=wwc.naming.WWCNamingService
 ***NEW**
 *	-Doutput=wwc.resources.StandardOutput
 *	-Derror=wwc.resources.StandardError
 *	-Dstorage=wwc.resources.StandardDisk
 *
 * Classes defined in system properties must have no argument contructors and
 * should implement the their respective service interface.
 *
 * @author stepha
 */

public class ServiceFactory {
	// Service implementations.
	private static TheaterService		theater		= null;
	private static TransportService		transport	= null;
	private static NamingService		naming		= null;
	private static ActorReference		output		= null;
        private static ActorReference           input           = null;
	private static ActorReference		error		= null;
	private static ActorReference		storage		= null;
	private static SystemService		system		= null;
        private static LocalCollector           GC              = null;
        private static GCAgent                  gcAgent         = null;
        private static ActorReference           dummySVC        = null;

	//default service classes
	private static String			theaterClass	= "wwc.messaging.Theater";

	private static String			transportClass	= "wwc.messaging.WWCTransportService";
	private static String			receptionClass	= "wwc.messaging.WWCReceptionService";

	private static String			namingClass	= "wwc.naming.WWCNamingService";
	private static String			outputClass	= "wwc.resources.StandardOutput";
        private static String			inputClass	= "wwc.resources.StandardInput";
	private static String			errorClass	= "wwc.resources.StandardError";
	private static String			storageClass	= "wwc.resources.StandardDisk";

	private static String			systemClass	= "wwc.messaging.WWCSystem";
        private static String			GCClass	        = "gc.serverGC.SLocalPRID";
        private static String			GCAgentClass	= "gc.serverGC.GCAgent";
        private static String                   DummySVCClass   = "salsa.resources.Dummy";

	//Applets cannot access System, so we have to make sure
	//this theater is not an AppletTheater before doing so.
	private static boolean 			applet 		= false;
	public static void isApplet() { applet = true; }

	/**
	 * Modify setttings for Service Factory
	 * This allows for different service implementations
	 * to be specified at runtime.
	 */

	public synchronized static void setTheaterClass(String theater)			{ ServiceFactory.theaterClass	= theater; }
	public synchronized static void setReceptionClass(String reception)		{ ServiceFactory.receptionClass	= reception; }
	public synchronized static void setTransportClass(String transport)		{ ServiceFactory.transportClass	= transport; }
	public synchronized static void setNamingClass(String naming)			{ ServiceFactory.namingClass	= naming; }
	public synchronized static void setOutputClass(String output)			{ ServiceFactory.outputClass	= output; }
        public synchronized static void setInputClass(String input)			{ ServiceFactory.inputClass	= input; }
	public synchronized static void setErrorClass(String error)			{ ServiceFactory.errorClass	= error; }
	public synchronized static void setStorageClass(String storage)			{ ServiceFactory.storageClass	= storage; }
	public synchronized static void setSystemClass(String system)			{ ServiceFactory.systemClass	= system; }

	public synchronized static void setTheater(TheaterService theater)		{ ServiceFactory.theater	= theater; }
	public synchronized static void setTransport(TransportService transport)	{ ServiceFactory.transport	= transport; }
	public synchronized static void setNaming(NamingService naming)			{ ServiceFactory.naming		= naming; }
	public synchronized static void setOutput(OutputService output)			{ ServiceFactory.output		= output; }
	public synchronized static void setError(ErrorService error)			{ ServiceFactory.error		= error; }
	public synchronized static void setStorage(StorageService storage)		{ ServiceFactory.storage	= storage; }
	public synchronized static void setSystem(SystemService system)			{ ServiceFactory.system		= system; }
        public synchronized static void setGC(LocalCollector gc)			{ ServiceFactory.GC		= gc; }
        public synchronized static void setGCAgent(GCAgent gcAgent)                     { ServiceFactory.gcAgent	= gcAgent; }
        public synchronized static void setDummySVC(ActorReference dummySVC)            { ServiceFactory.dummySVC	= dummySVC;}


	public static void printErrorMessage(String className, Exception e) {
		System.err.println("Service Factory error: ");
		System.err.println("\tError creating output service: " + className);
		System.err.println("\tGenerated exception: " + e);
		System.err.println("\tWith message: " +e.getMessage());
		e.printStackTrace();
	}

	/**
	 * Instantiates an actor with the given className, uan and ual identifier.
	 */
	public static ActorReference createActor(String className, String identifier) {
		try {
			Class[] parTypes = { Class.forName("salsa.naming.UAL") };
			Constructor actorConstructor = Class.forName(className).getConstructor(parTypes);

			Object[] args = { new UAL(getTheater().getLocation() + identifier) };
			return ((ActorReference)actorConstructor.newInstance( args )).construct();
		} catch (Exception e) {
			//System.err.println("Service Factory Error:");
			//System.err.println("\tCould not create system actor: " + className);
			//System.err.println("\tWith identifier: " + identifier);
			//System.err.println("\tThrew exception: " + e);
			//e.printStackTrace();
		}
                try {
                        Class[] parTypes = { Class.forName("salsa.naming.UAL"),Class.forName("salsa.language.UniversalActor$State") };
                        Constructor actorConstructor = Class.forName(className).getConstructor(parTypes);

                        Object[] args = { new UAL(getTheater().getLocation() + identifier), null };
                        return ((ActorReference)actorConstructor.newInstance( args )).construct();
                } catch (Exception e) {
                        System.err.println("Service Factory Error:");
                        System.err.println("\tCould not create system actor: " + className);
                        System.err.println("\tWith identifier: " + identifier);
                        System.err.println("\tThrew exception: " + e);
                        e.printStackTrace();
                }

		return null;
	}

	/**
	 * Returns an implementation of the system service. If no service has been specified,
	 * it checks the system property "-Dsystem=<systemservice>" and instantiates that
	 * implementation
	 * @return SystemService
	 */
	public synchronized static SystemService getSystem() {
		if (system == null) {
			// Check for the messagingService.
			String className = null;
			if (!applet) className = System.getProperty( "system" );
			if (className == null) className = systemClass;

			try {
				system = (SystemService)createActor(className, "salsa/System");
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return system;
	}

        public synchronized static LocalCollector getGC() {
                if (GC == null) {
                        // Check for the messagingService.
                        String className = null;
                        if (!applet) className = System.getProperty( "GC" );
                        if (className == null) className = GCClass;

                        try {
                                Class[] parTypes = { };
                                Object[] args={};
                                Constructor actorConstructor = Class.forName(className).getConstructor(parTypes);
                                GC=(LocalCollector) actorConstructor.newInstance( args );
                                return GC;

                        } catch (Exception e) {
                                System.err.println("Service Factory Error:");
                                System.err.println("\tCould not create system class: " + className);
                                System.err.println("\tThrew exception: " + e);
                                e.printStackTrace();
                        }
                }

                return GC;
        }

        public synchronized static GCAgent getGCAgent() {
                if (gcAgent == null) {
                  String className = null;
                  if (!applet) className = System.getProperty("GCAgent");
                  if (className == null) className = GCAgentClass;

                  try { gcAgent = (GCAgent) createActor(className, "salsa/GCAgent");}
                  catch (Exception e) { printErrorMessage(className, e);}
                }
                return gcAgent;
        }


	/**
	 * Returns an implementation of the output service. If no service has been specified,
	 * it checks the system property "-Doutput=<transportservice>" and instantiates that
	 * implementation
	 * @return OutputService
	 */
	public synchronized static ActorReference getOutput() {
		if (output == null) {
                  if (theater==null || theater.isRestricted()) {
                    output=getDummy();
                    return output;
                  }
			// Check for the messagingService.
			String className = null;
			if (!applet) className = System.getProperty( "output" );
			if (className == null) className = outputClass;

			try {
				output = (OutputService)createActor(className, "salsa/StandardOutput");
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return output;
	}

        /**
         * Returns an implementation of the input service. If no service has been specified,
         * it checks the system property "-Dinput=<transportservice>" and instantiates that
         * implementation
         * @return InputService
         */
        public synchronized static ActorReference getInput() {
                if (input == null) {
                  if (theater==null || theater.isRestricted()) {
                    input=getDummy();
                    return input;
                  }


                        // Check for the messagingService.
                        String className = null;
                        if (!applet) className = System.getProperty( "input" );
                        if (className == null) className = inputClass;

                        try {
                                input = (InputService)createActor(className, "salsa/StandardInput");
                        } catch (Exception e) {
                                printErrorMessage(className, e);
                        }
                }

                return input;
        }


	/**
	 * Returns an implementation of the error service. If no service has been specified,
	 * it checks the system property "-Derror=<errorservice>" and instantiates that
	 * implementation
	 * @return ErrorService
	 */
	public synchronized static ActorReference getError() {
		if (error == null) {
                  if (theater==null || theater.isRestricted()) {
                    error=getDummy();
                    return error;
                  }
			// Check for the messagingService.
			String className = null;
			if (!applet) className = System.getProperty( "error" );
			if (className == null) className = errorClass;

			try {
				error = (ErrorService)createActor(className, "salsa/StandardError");
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return error;
	}

	/**
	 * Returns an implementation of the transport service. If no service has been specified,
	 * it checks the system property "-storage=<storageservice>" and instantiates that
	 * implementation
	 * @return StorageService
	 */
	public synchronized static ActorReference getStorage() {
		if (storage == null) {
                  if (theater==null || theater.isRestricted()) {
                    storage=getDummy();
                    return storage;
                  }
			// Check for the messagingService.
			String className = null;
			if (!applet) className = System.getProperty( "storage" );
			if (className == null) className = storageClass;

			try {
				storage = (StorageService)Class.forName( className ).newInstance();
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return storage;
	}




	/**
	 * Returns an implementation of the transport service. If no service has been specified,
	 * it checks the system property "-Dtransport=<transportservice>" and instantiates that
	 * implementation
	 * @return TransportService
	 */
	public synchronized static TransportService getTransport() {
		if (transport == null) {
			// Check for the messagingService.
			String className = null;
			if (!applet) className = System.getProperty( "transport" );
			if (className == null) className = transportClass;

			try {
				transport = (TransportService)Class.forName( className ).newInstance();
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return transport;
	}

	/**
	 * Returns the ReceptionService. If no reception service is specified,
	 * it uses the system property "-Dreception=<receptionservice>" to
	 * instantiate the given service.
	 * @return ReceptionService
	 */
	public synchronized static ReceptionService getReception() {
		ReceptionService reception = null;

		// Check for the migration service.
		String className = null;
		if (!applet) className = System.getProperty( "reception" );
		if (className == null) className = receptionClass;

		try {
			reception = (ReceptionService)Class.forName( className ).newInstance();
		} catch (Exception e) {
			printErrorMessage(className, e);
		}

		return reception;
	}

	/**
	 * Returns the namingImpl.
	 * @return NamingService
	 */
	public synchronized static NamingService getNaming() {
		if (naming == null) {
			// Check for the naming service.
			String className = null;
			if (!applet) className = System.getProperty( "naming" );
			if (className == null) className = namingClass;

			try {
				naming = (NamingService)Class.forName( className ).newInstance();
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return naming;
	}

	/**
	 * Returns the theaterImpl.
	 * @return TheaterService
	 */
	public synchronized static TheaterService getTheater() {
		if (theater == null) {
			String className = null;
			if (!applet) className = System.getProperty( "theater" );
			if (className == null) className = theaterClass;

			try {
				theater = (TheaterService)Class.forName( className ).newInstance();
			} catch (Exception e) {
				printErrorMessage(className, e);
			}
		}

		return theater;
	}

        public synchronized static ActorReference getDummy() {
                if (dummySVC == null) {
                        String className = null;
                        if (!applet) className = System.getProperty( "dummySVC" );
                        if (className == null) className = DummySVCClass;
                        try {
                                dummySVC = (ActorReference)createActor(className, "salsa/DummySVC");
                        } catch (Exception e) {
                                printErrorMessage(className, e);
                        }

                }
                return dummySVC;
        }

}
