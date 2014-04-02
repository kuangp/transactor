/*
	Code generation for the following grammar segment:

	BehaviorBody :=
	"{"
		(
			  Initializer
			| NestedBehaviorDeclaration
			| StateVariableDeclaration
			| ConstructorDeclaration
			| MethodDeclaration
		)*
	"}"
*/

//*******************************
//modified by weijen
//  target: Generate different code for normal actors and service actors accordingly
//          A service actor implements ActorService interface
//          - getServicePreCode() for actor services
//          - getPreCode() for all actors which calls getServicePreCode() if required
//
//*******************************

package salsac.definitions;

import salsac.SalsaCompiler;
import salsac.SalsaParser;
import salsac.SimpleNode;


public class BehaviorBody extends SimpleNode {

	public BehaviorBody(int id) 			{ super(id); }
	public BehaviorBody(SalsaParser p, int id)	{ super(p, id); }


        public String getServicePreCode()		{
                SalsaCompiler.indent++;
                String actorName = parent.getToken(1).image;

                String code = "{\n";

                /**
                        Create and send a message which will invoke act on the actor.
                */
                code += SalsaCompiler.getIndent() + "public static void main(String args[]) {\n";

                code += SalsaCompiler.getIndent() + "\tUAN uan = null;\n";
                code += SalsaCompiler.getIndent() + "\tUAL ual = null;\n";
                code += SalsaCompiler.getIndent() + "\tif (System.getProperty(\"uan\") != null) {\n";
                code += SalsaCompiler.getIndent() + "\t\tuan = new UAN( System.getProperty(\"uan\") );\n";
                code += SalsaCompiler.getIndent() + "\t\tServiceFactory.getTheater();\n";
                code += SalsaCompiler.getIndent() + "\t\tRunTime.receivedUniversalActor();\n";
                code += SalsaCompiler.getIndent() + "\t}\n";
                code += SalsaCompiler.getIndent() + "\tif (System.getProperty(\"ual\") != null) {\n";
                code += SalsaCompiler.getIndent() + "\t\tual = new UAL( System.getProperty(\"ual\") );\n\n";
                code += SalsaCompiler.getIndent() + "\t\tif (uan == null) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"Actor Creation Error:\");\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tuan: \" + uan);\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tual: \" + ual);\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tIdentifier: \" + System.getProperty(\"identifier\"));\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tCannot specify an actor to have a ual at runtime without a uan.\");\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tTo give an actor a specific ual at runtime, use the identifier system property.\");\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.exit(0);\n";
                code += SalsaCompiler.getIndent() + "\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t\tRunTime.receivedUniversalActor();\n";
                code += SalsaCompiler.getIndent() + "\t}\n";

                code += SalsaCompiler.getIndent() + "\tif (System.getProperty(\"identifier\") != null) {\n";
                code += SalsaCompiler.getIndent() + "\t\tif (ual != null) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"Actor Creation Error:\");\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tuan: \" + uan);\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tual: \" + ual);\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tIdentifier: \" + System.getProperty(\"identifier\"));\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tCannot specify an identifier and a ual with system properties when creating an actor.\");\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.exit(0);\n";
                code += SalsaCompiler.getIndent() + "\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t\tual = new UAL( ServiceFactory.getTheater().getLocation() + System.getProperty(\"identifier\"));\n";
                code += SalsaCompiler.getIndent() + "\t}\n";

                code += SalsaCompiler.getIndent() + "\t" + actorName + " instance = (" + actorName + ")new " + actorName + "(uan, ual,null).construct();\n";

                code += SalsaCompiler.getIndent() + "\t{\n";
                code += SalsaCompiler.getIndent() + "\t\tObject[] _arguments = { args };\n";
                code += SalsaCompiler.getIndent() + "\t\tinstance.send( new Message(instance, instance, \"act\", _arguments, null, null) );\n";
                code += SalsaCompiler.getIndent() + "\t}\n";

                code += SalsaCompiler.getIndent() + "}\n\n";

                /**
                        Generate code for the constructors.
                        ActorName(boolean o, UAN uan) is used for getReferenceByName
                        ActorName(boolean o, UAL ual) is used for get ReferenceByLocation

                        ActorName(UAN uan, UAL ual,null) is for remote actor creation.
                        ActorName(UAN uan,null) is for local binding.
                        ActorName(UAL ual,null) is for specifying an actor's UAL.
                        ActorName(null) is for anonymous actor creation.
                */
                code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByName(UAN uan)\t{ return new " + actorName + "(false, uan); }\n";
                code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByName(String uan)\t{ return " + actorName + ".getReferenceByName(new UAN(uan)); }\n";
                code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByLocation(UAL ual)\t{ return new " + actorName + "(false, ual); }\n\n";
                code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByLocation(String ual)\t{ return " + actorName + ".getReferenceByLocation(new UAL(ual)); }\n";

                code += SalsaCompiler.getIndent() + "public " + actorName + "(boolean o, UAN __uan)\t{ super(false,__uan); }\n";
                code += SalsaCompiler.getIndent() + "public " + actorName + "(boolean o, UAL __ual)\t{ super(false,__ual); }\n\n";

                code += SalsaCompiler.getIndent() + "public " + actorName + "(UAN __uan,UniversalActor.State sourceActor)\t{ this(__uan, null,null); }\n";
                code += SalsaCompiler.getIndent() + "public " + actorName + "(UAL __ual,UniversalActor.State sourceActor)\t{ this(null, __ual,null); }\n";
                code += SalsaCompiler.getIndent() + "public " + actorName + "(UniversalActor.State sourceActor)\t\t{ this(null, null,null);  }\n";
                code += SalsaCompiler.getIndent() + "public " + actorName + "()\t\t{  }\n";
                code += SalsaCompiler.getIndent() + "public " + actorName + "(UAN __uan, UAL __ual,Object sourceActor) {\n";
                code += SalsaCompiler.getIndent() + "\tif (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {\n";

                String actorModule = SalsaCompiler.getCompilationUnit().getModule();
                String fullActorName = "";
                if (!actorModule.equals("")) fullActorName += actorModule + ".";
                fullActorName += SalsaCompiler.getActorName();

                code += SalsaCompiler.getIndent() + "\t\tcreateRemotely(__uan, __ual, \"" + fullActorName + "\");\n";
                code += SalsaCompiler.getIndent() + "\t} else {\n";
                code += SalsaCompiler.getIndent() + "\t\tState state = new State(__uan, __ual);\n";
                code += SalsaCompiler.getIndent() + "\t\tstate.updateSelf(this);\n";
                code += SalsaCompiler.getIndent() + "\t\tServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);\n";
                code += SalsaCompiler.getIndent() + "\t\tif (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());\n";
                code += SalsaCompiler.getIndent() + "\t}\n";
                code += SalsaCompiler.getIndent() + "}\n\n";


                boolean hasBasic = false;
                if (children != null) {
                        for (int i = 0; i < children.length; i++) {
                                if (getChild(i) instanceof ConstructorDeclaration) {
                                        if (getChild(i).getChild(1).children == null) hasBasic = true;
                                        code += SalsaCompiler.getIndent() + "public UniversalActor construct (" + getChild(i).getChild(1).getJavaCode() + ") {\n";
                                        code += SalsaCompiler.getIndent() + "\tObject[] __arguments = { " + ((FormalParameters)getChild(i).getChild(1)).getNonPrimitiveNames() + " };\n";
                                        code += SalsaCompiler.getIndent() + "\tthis.send( new Message(this, this, \"construct\", __arguments, null, null) );\n";
                                        code += SalsaCompiler.getIndent() + "\treturn this;\n";
                                        code += SalsaCompiler.getIndent() + "}\n\n";
                                }
                        }
                }
                if (!hasBasic) {
                        code += SalsaCompiler.getIndent() + "public UniversalActor construct() {\n";
                        code += SalsaCompiler.getIndent() + "\tObject[] __arguments = { };\n";
                        code += SalsaCompiler.getIndent() + "\tthis.send( new Message(this, this, \"construct\", __arguments, null, null) );\n";
                        code += SalsaCompiler.getIndent() + "\treturn this;\n";
                        code += SalsaCompiler.getIndent() + "}\n\n";
                }

                /**
                        Actually create the state.
                        If this actor extends another actor, have it extend that actors state
                */
                String extendsName = ((BehaviorDeclaration)parent).extendsName;
                code += SalsaCompiler.getIndent() + "public class State extends " + ((BehaviorDeclaration)parent).extendsName + ".State";
                if ( ((BehaviorDeclaration)parent).implementNames.size() > 0) {
                        code += " implements ";
                        for (int i = 0; i < ((BehaviorDeclaration)parent).implementNames.size(); i++) {

                          //if the actor is a service, its state must implements salsa.resources.ActorServiceState
                                if (((BehaviorDeclaration)parent).implementNames.get(i).equals("ActorService") ||
                                    ((BehaviorDeclaration)parent).implementNames.get(i).equals("salsa.resource.ActorService")) {
                                  code += "salsa.resources.ActorServiceState";
                                } else {
                                  code += ( (BehaviorDeclaration) parent).implementNames.get(i) +
                                      ".State";
                                }
                                if (i+1 != ((BehaviorDeclaration)parent).implementNames.size()) code += ", ";
                        }
                }
                code += " {\n";
                SalsaCompiler.indent++;

/**********
 * generated code for updateSelf:
             public void updateSelf(ActorReference actorReference) {
                               self = new WeakReference( actorReference);
                               self.setUAN(getUAN());
                               self.setUAL(getUAL());
                               actorReference.setUAL(getUAL());
                               actorReference.setUAN(getUAN());
             }
 **********/
code += SalsaCompiler.getIndent() + "public " + actorName + " self;\n";

code += SalsaCompiler.getIndent() + "public void updateSelf(ActorReference actorReference) {\n";
code += SalsaCompiler.getIndent() + "\t((" + actorName + ")actorReference).setUAL(getUAL());\n";
code += SalsaCompiler.getIndent() + "\t((" + actorName + ")actorReference).setUAN(getUAN());\n";
code += SalsaCompiler.getIndent() + "\tself = new " + actorName + "(false,getUAL());\n";
code += SalsaCompiler.getIndent() + "\tself.setUAN(getUAN());\n";
code += SalsaCompiler.getIndent() + "\tself.setUAL(getUAL());\n";
code += SalsaCompiler.getIndent() + "\tself.muteGC();\n";
code += SalsaCompiler.getIndent() + "}\n\n";

/*
                code += SalsaCompiler.getIndent() + "public WeakReference self;\n";
                code += SalsaCompiler.getIndent() + "public void updateSelf(ActorReference actorReference) {\n";
                code += SalsaCompiler.getIndent() + "\tself = new WeakReference( actorReference);\n";
                code += SalsaCompiler.getIndent() + "\tself.setUAN(getUAN());\n";
                code += SalsaCompiler.getIndent() + "\tself.setUAL(getUAL());\n";
                code += SalsaCompiler.getIndent() + "\tactorReference.setUAL(getUAL());\n";
                code += SalsaCompiler.getIndent() + "\tactorReference.setUAN(getUAN());\n";
                code += SalsaCompiler.getIndent() + "}\n\n";
*/
                code += SalsaCompiler.getIndent() + "public State() {\n";
                code += SalsaCompiler.getIndent() + "\tthis(null, null);\n";
                code += SalsaCompiler.getIndent() + "}\n\n";

                code += SalsaCompiler.getIndent() + "public State(UAN __uan, UAL __ual) {\n";
                code += SalsaCompiler.getIndent() + "\tsuper(__uan, __ual);\n";
                 code += SalsaCompiler.getIndent() + "\taddClassName( \"";
                String module = SalsaCompiler.getCompilationUnit().getModule();
                if (!module.equals("")) code += module + ".";
                code += SalsaCompiler.getActorName() + "$State\" );\n";

                code += SalsaCompiler.getIndent() + "\taddMethodsForClasses();\n";
                code += SalsaCompiler.getIndent() + "}\n\n";

                if (!hasBasic) {
                        code += SalsaCompiler.getIndent() + "public void construct() {}\n\n";
                }

                code += SalsaCompiler.getIndent() + "public void process(Message message) {\n";
                code += SalsaCompiler.getIndent() + "\tMethod[] matches = getMatches(message.getMethodName());\n";
                code += SalsaCompiler.getIndent() + "\tObject returnValue = null;\n";
                code += SalsaCompiler.getIndent() + "\tException exception = null;\n\n";

                code += SalsaCompiler.getIndent() + "\tif (matches != null) {\n";
				code += SalsaCompiler.getIndent() + "\t\tif (!message.getMethodName().equals(\"die\")) {activateArgsGC(message);}\n";
                code += SalsaCompiler.getIndent() + "\t\tfor (int i = 0; i < matches.length; i++) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\ttry {\n";

                code += SalsaCompiler.getIndent() + "\t\t\t\tif (matches[i].getParameterTypes().length != message.getArguments().length) continue;\n";

                code += SalsaCompiler.getIndent() + "\t\t\t\treturnValue = matches[i].invoke(this, message.getArguments());\n";
                code += SalsaCompiler.getIndent() + "\t\t\t} catch (Exception e) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\tif (e.getCause() instanceof CurrentContinuationException) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t\tsendGeneratedMessages();\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t\treturn;\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t} else if (e instanceof InvocationTargetException) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t\tsendGeneratedMessages();\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t\texception = (Exception)e.getCause();\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t\tbreak;\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t} else {\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t\tcontinue;\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\t}\n";

                code += SalsaCompiler.getIndent() + "\t\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t\t\tsendGeneratedMessages();\n";
                code += SalsaCompiler.getIndent() + "\t\t\tcurrentMessage.resolveContinuations(returnValue);\n";
                code += SalsaCompiler.getIndent() + "\t\t\treturn;\n";
                code += SalsaCompiler.getIndent() + "\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t}\n\n";


                code += SalsaCompiler.getIndent() + "\tSystem.err.println(\"Message processing exception:\");\n";
                code += SalsaCompiler.getIndent() + "\tif (message.getSource() != null) {\n";
                code += SalsaCompiler.getIndent() + "\t\tSystem.err.println(\"\\tSent by: \" + message.getSource().toString());\n";
                code += SalsaCompiler.getIndent() + "\t} else System.err.println(\"\\tSent by: unknown\");\n";
                code += SalsaCompiler.getIndent() + "\tSystem.err.println(\"\\tReceived by actor: \" + toString());\n";
                code += SalsaCompiler.getIndent() + "\tSystem.err.println(\"\\tMessage: \" + message.toString());\n";
                code += SalsaCompiler.getIndent() + "\tif (exception == null) {\n";
                code += SalsaCompiler.getIndent() + "\t\tif (matches == null) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\\tNo methods with the same name found.\");\n";
                code += SalsaCompiler.getIndent() + "\t\t\treturn;\n";
                code += SalsaCompiler.getIndent() + "\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t\tSystem.err.println(\"\\tDid not match any of the following: \");\n";
                code += SalsaCompiler.getIndent() + "\t\tfor (int i = 0; i < matches.length; i++) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.print(\"\\t\\tMethod: \" + matches[i].getName() + \"( \");\n";
                code += SalsaCompiler.getIndent() + "\t\t\tClass[] parTypes = matches[i].getParameterTypes();\n";
                code += SalsaCompiler.getIndent() + "\t\t\tfor (int j = 0; j < parTypes.length; j++) {\n";
                code += SalsaCompiler.getIndent() + "\t\t\t\tSystem.err.print(parTypes[j].getName() + \" \");\n";
                code += SalsaCompiler.getIndent() + "\t\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\")\");\n";
                code += SalsaCompiler.getIndent() + "\t\t}\n";
                code += SalsaCompiler.getIndent() + "\t} else {\n";
                code += SalsaCompiler.getIndent() + "\t\tSystem.err.println(\"\\tThrew exception: \" + exception);\n";
                code += SalsaCompiler.getIndent() + "\t\texception.printStackTrace();\n";
                code += SalsaCompiler.getIndent() + "\t}\n";
                code += SalsaCompiler.getIndent() + "}\n\n";

                return code;
        }


	public String getPreCode()		{
          if (SalsaCompiler.isService) {return getServicePreCode();}
		SalsaCompiler.indent++;
		String actorName = parent.getToken(1).image;

		String code = "{\n";

		/**
			Create and send a message which will invoke act on the actor.
		*/
		code += SalsaCompiler.getIndent() + "public static void main(String args[]) {\n";

		code += SalsaCompiler.getIndent() + "\tUAN uan = null;\n";
		code += SalsaCompiler.getIndent() + "\tUAL ual = null;\n";
		code += SalsaCompiler.getIndent() + "\tif (System.getProperty(\"uan\") != null) {\n";
		code += SalsaCompiler.getIndent() + "\t\tuan = new UAN( System.getProperty(\"uan\") );\n";
		code += SalsaCompiler.getIndent() + "\t\tServiceFactory.getTheater();\n";
		code += SalsaCompiler.getIndent() + "\t\tRunTime.receivedUniversalActor();\n";
		code += SalsaCompiler.getIndent() + "\t}\n";
		code += SalsaCompiler.getIndent() + "\tif (System.getProperty(\"ual\") != null) {\n";
		code += SalsaCompiler.getIndent() + "\t\tual = new UAL( System.getProperty(\"ual\") );\n\n";
		code += SalsaCompiler.getIndent() + "\t\tif (uan == null) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"Actor Creation Error:\");\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tuan: \" + uan);\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tual: \" + ual);\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tIdentifier: \" + System.getProperty(\"identifier\"));\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tCannot specify an actor to have a ual at runtime without a uan.\");\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tTo give an actor a specific ual at runtime, use the identifier system property.\");\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.exit(0);\n";
		code += SalsaCompiler.getIndent() + "\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t\tRunTime.receivedUniversalActor();\n";
		code += SalsaCompiler.getIndent() + "\t}\n";

		code += SalsaCompiler.getIndent() + "\tif (System.getProperty(\"identifier\") != null) {\n";
		code += SalsaCompiler.getIndent() + "\t\tif (ual != null) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"Actor Creation Error:\");\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tuan: \" + uan);\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tual: \" + ual);\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tIdentifier: \" + System.getProperty(\"identifier\"));\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\tCannot specify an identifier and a ual with system properties when creating an actor.\");\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.exit(0);\n";
		code += SalsaCompiler.getIndent() + "\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t\tual = new UAL( ServiceFactory.getTheater().getLocation() + System.getProperty(\"identifier\"));\n";
		code += SalsaCompiler.getIndent() + "\t}\n";
                code += SalsaCompiler.getIndent() + "\tRunTime.receivedMessage();\n";
		code += SalsaCompiler.getIndent() + "\t" + actorName + " instance = (" + actorName + ")new " + actorName + "(uan, ual,null).construct();\n";
                code += SalsaCompiler.getIndent() + "\tgc.WeakReference instanceRef=new gc.WeakReference(uan,ual);\n";

		code += SalsaCompiler.getIndent() + "\t{\n";
                code += SalsaCompiler.getIndent() + "\t\tObject[] _arguments = { args };\n\n";
                code += SalsaCompiler.getIndent() + "\t\t//preAct() for local actor creation\n";
                code += SalsaCompiler.getIndent() + "\t\t//act() for remote actor creation\n";

                code += SalsaCompiler.getIndent() + "\t\tif (ual != null && !ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {instance.send( new Message(instanceRef, instanceRef, \"act\", _arguments, false) );}\n";
                code += SalsaCompiler.getIndent() + "\t\telse {instance.send( new Message(instanceRef, instanceRef, \"preAct\", _arguments, false) );}\n";
		code += SalsaCompiler.getIndent() + "\t}\n";
                code += SalsaCompiler.getIndent() + "\tRunTime.finishedProcessingMessage();\n";
		code += SalsaCompiler.getIndent() + "}\n\n";

		/**
			Generate code for the constructors.
			ActorName(boolean o, UAN uan) is used for getReferenceByName
			ActorName(boolean o, UAL ual) is used for get ReferenceByLocation

			ActorName(UAN uan, UAL ual) is for remote actor creation.
			ActorName(UAN uan) is for local binding.
			ActorName(UAL ual) is for specifying an actor's UAL.
			ActorName() is for anonymous actor creation.
		*/
		code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByName(UAN uan)\t{ return new " + actorName + "(false, uan); }\n";
		code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByName(String uan)\t{ return " + actorName + ".getReferenceByName(new UAN(uan)); }\n";
		code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByLocation(UAL ual)\t{ return new " + actorName + "(false, ual); }\n\n";
		code += SalsaCompiler.getIndent() + "public static ActorReference getReferenceByLocation(String ual)\t{ return " + actorName + ".getReferenceByLocation(new UAL(ual)); }\n";

		code += SalsaCompiler.getIndent() + "public " + actorName + "(boolean o, UAN __uan)\t{ super(false,__uan); }\n";
		code += SalsaCompiler.getIndent() + "public " + actorName + "(boolean o, UAL __ual)\t{ super(false,__ual); }\n";
                //code += SalsaCompiler.getIndent() + "public " + actorName + "()\t{ super(null,null); }\n\n";

		code += SalsaCompiler.getIndent() + "public " + actorName + "(UAN __uan,UniversalActor.State sourceActor)\t{ this(__uan, null, sourceActor); }\n";
		code += SalsaCompiler.getIndent() + "public " + actorName + "(UAL __ual,UniversalActor.State sourceActor)\t{ this(null, __ual, sourceActor); }\n";
		code += SalsaCompiler.getIndent() + "public " + actorName + "(UniversalActor.State sourceActor)\t\t{ this(null, null, sourceActor);  }\n";
                code += SalsaCompiler.getIndent() + "public " + actorName + "()\t\t{  }\n";
		code += SalsaCompiler.getIndent() + "public " + actorName + "(UAN __uan, UAL __ual, Object obj) {\n";


		String actorModule = SalsaCompiler.getCompilationUnit().getModule();
		String fullActorName = "";
		if (!actorModule.equals("")) fullActorName += actorModule + ".";
		fullActorName += SalsaCompiler.getActorName();

                code += SalsaCompiler.getIndent() +"\t//decide the type of sourceActor\n";
                code += SalsaCompiler.getIndent() +"\t//if obj is null, the actor must be the startup actor.\n";
                code += SalsaCompiler.getIndent() +"\t//if obj is an actorReference, this actor is created by a remote actor\n\n";

                code += SalsaCompiler.getIndent() +"\tif (obj instanceof UniversalActor.State || obj==null) {\n";
                code += SalsaCompiler.getIndent() +"\t\t  UniversalActor.State sourceActor;\n";
                code += SalsaCompiler.getIndent() +"\t\t  if (obj!=null) { sourceActor=(UniversalActor.State) obj;}\n";
                code += SalsaCompiler.getIndent() +"\t\t  else {sourceActor=null;}\n\n";

                code += SalsaCompiler.getIndent() +"\t\t  //remote creation message sent to a remote system service.\n";
                code += SalsaCompiler.getIndent() +"\t\t  if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {\n";
                code += SalsaCompiler.getIndent() +"\t\t    WeakReference sourceRef;\n";
                code += SalsaCompiler.getIndent() +"\t\t    if (sourceActor!=null && sourceActor.getUAL() != null) {sourceRef = new WeakReference(sourceActor.getUAN(),sourceActor.getUAL());}\n";
                code += SalsaCompiler.getIndent() +"\t\t    else {sourceRef = null;}\n";
                code += SalsaCompiler.getIndent() +"\t\t    if (sourceActor != null) {\n";
                code += SalsaCompiler.getIndent() +"\t\t      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}\n";
                code += SalsaCompiler.getIndent() +"\t\t      else if (__ual!=null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}\n\n";
                code += SalsaCompiler.getIndent() +"\t\t      //update the source of this actor reference\n";
                code += SalsaCompiler.getIndent() +"\t\t      setSource(sourceActor.getUAN(), sourceActor.getUAL());\n";
                code += SalsaCompiler.getIndent() +"\t\t      activateGC();\n";

                code += SalsaCompiler.getIndent() +"\t\t    }\n";
                code += SalsaCompiler.getIndent() +"\t\t    createRemotely(__uan, __ual, \"" + fullActorName + "\", sourceRef);\n";
                code += SalsaCompiler.getIndent() +"\t\t  }\n\n";

                code += SalsaCompiler.getIndent() +"\t\t  // local creation\n";
                code += SalsaCompiler.getIndent() +"\t\t  else {\n";
                code += SalsaCompiler.getIndent() +"\t\t    State state = new State(__uan, __ual);\n\n";
                code += SalsaCompiler.getIndent() +"\t\t    //assume the reference is weak\n";
                code += SalsaCompiler.getIndent() +"\t\t    muteGC();\n\n";

                code += SalsaCompiler.getIndent() +"\t\t    //the source actor is  the startup actor\n";
                code += SalsaCompiler.getIndent() +"\t\t    if (sourceActor == null) {\n";
                code += SalsaCompiler.getIndent() +"\t\t      state.getActorMemory().getInverseList().putInverseReference(\"rmsp://me\");\n";
                code += SalsaCompiler.getIndent() +"\t\t    }\n\n";
                code += SalsaCompiler.getIndent() +"\t\t    //the souce actor is a normal actor\n";
                code += SalsaCompiler.getIndent() +"\t\t    else if (sourceActor instanceof UniversalActor.State) {\n\n";
                code += SalsaCompiler.getIndent() +"\t\t      // this reference is part of garbage collection\n";
                code += SalsaCompiler.getIndent() +"\t\t      activateGC();\n\n";

                code += SalsaCompiler.getIndent() +"\t\t      //update the source of this actor reference\n";
                code += SalsaCompiler.getIndent() +"\t\t      setSource(sourceActor.getUAN(), sourceActor.getUAL());\n\n";

                code += SalsaCompiler.getIndent() +"\t\t      /* Garbage collection registration:\n";
                code += SalsaCompiler.getIndent() +"\t\t       * register 'this reference' in sourceActor's forward list @\n";
                code += SalsaCompiler.getIndent() +"\t\t       * register 'this reference' in the forward acquaintance's inverse list\n";
                code += SalsaCompiler.getIndent() +"\t\t       */\n";
                code += SalsaCompiler.getIndent() +"\t\t      String inverseRefString=null;\n";
                code += SalsaCompiler.getIndent() +"\t\t      if (sourceActor.getUAN()!=null) {inverseRefString=sourceActor.getUAN().toString();}\n";
                code += SalsaCompiler.getIndent() +"\t\t      else if (sourceActor.getUAL()!=null) {inverseRefString=sourceActor.getUAL().toString();}\n";

                code += SalsaCompiler.getIndent() +"\t\t      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}\n";
                code += SalsaCompiler.getIndent() +"\t\t      else if (__ual != null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}\n";
                code += SalsaCompiler.getIndent() +"\t\t      else {sourceActor.getActorMemory().getForwardList().putReference(state.getUAL());}\n\n";

                code += SalsaCompiler.getIndent() +"\t\t      //put the inverse reference information in the actormemory\n";
                code += SalsaCompiler.getIndent() +"\t\t      if (inverseRefString!=null) state.getActorMemory().getInverseList().putInverseReference(inverseRefString);\n";
                code += SalsaCompiler.getIndent() +"\t\t    }\n";
                code += SalsaCompiler.getIndent() +"\t\t    state.updateSelf(this);\n";
                code += SalsaCompiler.getIndent() +"\t\t    ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);\n";
                code += SalsaCompiler.getIndent() +"\t\t    if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());\n";
                code += SalsaCompiler.getIndent() +"\t\t  }\n";
                code += SalsaCompiler.getIndent() +"\t}\n\n";

                code += SalsaCompiler.getIndent() +"\t//creation invoked by a remote message\n";
                code += SalsaCompiler.getIndent() +"\telse if (obj instanceof ActorReference) {\n";
                code += SalsaCompiler.getIndent() +"\t\t  ActorReference sourceRef= (ActorReference) obj;\n";
                code += SalsaCompiler.getIndent() +"\t\t  State state = new State(__uan, __ual);\n";
                code += SalsaCompiler.getIndent() +"\t\t  muteGC();\n";
                code += SalsaCompiler.getIndent() +"\t\t  state.getActorMemory().getInverseList().putInverseReference(\"rmsp://me\");\n";

                code += SalsaCompiler.getIndent() +"\t\t  if (sourceRef.getUAN() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAN());}\n";
                code += SalsaCompiler.getIndent() +"\t\t  else if (sourceRef.getUAL() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAL());}\n";
                code += SalsaCompiler.getIndent() +"\t\t  state.updateSelf(this);\n";
                code += SalsaCompiler.getIndent() +"\t\t  ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(),state);\n";
                code += SalsaCompiler.getIndent() +"\t\t  if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());\n";
                code += SalsaCompiler.getIndent() +"\t}\n";
                code += SalsaCompiler.getIndent() +"}\n\n";



		boolean hasBasic = false;
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				if (getChild(i) instanceof ConstructorDeclaration) {
					if (getChild(i).getChild(1).children == null) hasBasic = true;
					code += SalsaCompiler.getIndent() + "public UniversalActor construct (" + getChild(i).getChild(1).getJavaCode() + ") {\n";
					code += SalsaCompiler.getIndent() + "\tObject[] __arguments = { " + ((FormalParameters)getChild(i).getChild(1)).getNonPrimitiveNames() + " };\n";
					code += SalsaCompiler.getIndent() + "\tthis.send( new Message(this, this, \"construct\", __arguments, null, null) );\n";
					code += SalsaCompiler.getIndent() + "\treturn this;\n";
					code += SalsaCompiler.getIndent() + "}\n\n";
				}
			}
		}
		if (!hasBasic) {
			code += SalsaCompiler.getIndent() + "public UniversalActor construct() {\n";
			code += SalsaCompiler.getIndent() + "\tObject[] __arguments = { };\n";
			code += SalsaCompiler.getIndent() + "\tthis.send( new Message(this, this, \"construct\", __arguments, null, null) );\n";
			code += SalsaCompiler.getIndent() + "\treturn this;\n";
			code += SalsaCompiler.getIndent() + "}\n\n";
		}

		/**
			Actually create the state.
			If this actor extends another actor, have it extend that actors state
		*/
		String extendsName = ((BehaviorDeclaration)parent).extendsName;
		code += SalsaCompiler.getIndent() + "public class State extends " + ((BehaviorDeclaration)parent).extendsName + ".State";
		if ( ((BehaviorDeclaration)parent).implementNames.size() > 0) {
			code += " implements ";
			for (int i = 0; i < ((BehaviorDeclaration)parent).implementNames.size(); i++) {
				code += ((BehaviorDeclaration)parent).implementNames.get(i) + ".State";
				if (i+1 != ((BehaviorDeclaration)parent).implementNames.size()) code += ", ";
			}
		}
		code += " {\n";
		SalsaCompiler.indent++;

		code += SalsaCompiler.getIndent() + "public " + actorName + " self;\n";

                code += SalsaCompiler.getIndent() + "public void updateSelf(ActorReference actorReference) {\n";
                code += SalsaCompiler.getIndent() + "\t((" + actorName + ")actorReference).setUAL(getUAL());\n";
                code += SalsaCompiler.getIndent() + "\t((" + actorName + ")actorReference).setUAN(getUAN());\n";
                code += SalsaCompiler.getIndent() + "\tself = new " + actorName + "(false,getUAL());\n";
                code += SalsaCompiler.getIndent() + "\tself.setUAN(getUAN());\n";
		code += SalsaCompiler.getIndent() + "\tself.setUAL(getUAL());\n";
		code += SalsaCompiler.getIndent() + "\tself.activateGC();\n";
		code += SalsaCompiler.getIndent() + "}\n\n";

                if (SalsaCompiler.actMethodExist) {
                  code += SalsaCompiler.getIndent() +
                      "public void preAct(String[] arguments) {\n";
                  code += SalsaCompiler.getIndent() +
                      "\tgetActorMemory().getInverseList().removeInverseReference(\"rmsp://me\",1);\n";
                  //code += SalsaCompiler.getIndent() + "\tact(arguments);\n";
                  code += SalsaCompiler.getIndent() + "\t{\n";
                  code += SalsaCompiler.getIndent() + "\t\tObject[] __args={arguments};\n";
                  code += SalsaCompiler.getIndent() + "\t\tself.send( new Message(self,self, \"act\", __args, null,null,false) );\n";
                  code += SalsaCompiler.getIndent() + "\t}\n";
                  code += SalsaCompiler.getIndent() + "}\n\n";
                }


		code += SalsaCompiler.getIndent() + "public State() {\n";
		code += SalsaCompiler.getIndent() + "\tthis(null, null);\n";
		code += SalsaCompiler.getIndent() + "}\n\n";

		code += SalsaCompiler.getIndent() + "public State(UAN __uan, UAL __ual) {\n";
		code += SalsaCompiler.getIndent() + "\tsuper(__uan, __ual);\n";
 		code += SalsaCompiler.getIndent() + "\taddClassName( \"";
		String module = SalsaCompiler.getCompilationUnit().getModule();
		if (!module.equals("")) code += module + ".";
		code += SalsaCompiler.getActorName() + "$State\" );\n";

		code += SalsaCompiler.getIndent() + "\taddMethodsForClasses();\n";
		code += SalsaCompiler.getIndent() + "}\n\n";

		if (!hasBasic) {
			code += SalsaCompiler.getIndent() + "public void construct() {}\n\n";
		}

		code += SalsaCompiler.getIndent() + "public void process(Message message) {\n";
		code += SalsaCompiler.getIndent() + "\tMethod[] matches = getMatches(message.getMethodName());\n";
		code += SalsaCompiler.getIndent() + "\tObject returnValue = null;\n";
		code += SalsaCompiler.getIndent() + "\tException exception = null;\n\n";

		code += SalsaCompiler.getIndent() + "\tif (matches != null) {\n";
		code += SalsaCompiler.getIndent() + "\t\tif (!message.getMethodName().equals(\"die\")) {activateArgsGC(message);}\n";

		code += SalsaCompiler.getIndent() + "\t\tfor (int i = 0; i < matches.length; i++) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\ttry {\n";

		code += SalsaCompiler.getIndent() + "\t\t\t\tif (matches[i].getParameterTypes().length != message.getArguments().length) continue;\n";

		code += SalsaCompiler.getIndent() + "\t\t\t\treturnValue = matches[i].invoke(this, message.getArguments());\n";
		code += SalsaCompiler.getIndent() + "\t\t\t} catch (Exception e) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\tif (e.getCause() instanceof CurrentContinuationException) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t\tsendGeneratedMessages();\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t\treturn;\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t} else if (e instanceof InvocationTargetException) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t\tsendGeneratedMessages();\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t\texception = (Exception)e.getCause();\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t\tbreak;\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t} else {\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t\tcontinue;\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\t}\n";

		code += SalsaCompiler.getIndent() + "\t\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t\t\tsendGeneratedMessages();\n";
		code += SalsaCompiler.getIndent() + "\t\t\tcurrentMessage.resolveContinuations(returnValue);\n";
		code += SalsaCompiler.getIndent() + "\t\t\treturn;\n";
		code += SalsaCompiler.getIndent() + "\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t}\n\n";


		code += SalsaCompiler.getIndent() + "\tSystem.err.println(\"Message processing exception:\");\n";
		code += SalsaCompiler.getIndent() + "\tif (message.getSource() != null) {\n";
		code += SalsaCompiler.getIndent() + "\t\tSystem.err.println(\"\\tSent by: \" + message.getSource().toString());\n";
		code += SalsaCompiler.getIndent() + "\t} else System.err.println(\"\\tSent by: unknown\");\n";
		code += SalsaCompiler.getIndent() + "\tSystem.err.println(\"\\tReceived by actor: \" + toString());\n";
		code += SalsaCompiler.getIndent() + "\tSystem.err.println(\"\\tMessage: \" + message.toString());\n";
		code += SalsaCompiler.getIndent() + "\tif (exception == null) {\n";
		code += SalsaCompiler.getIndent() + "\t\tif (matches == null) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\"\\tNo methods with the same name found.\");\n";
		code += SalsaCompiler.getIndent() + "\t\t\treturn;\n";
		code += SalsaCompiler.getIndent() + "\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t\tSystem.err.println(\"\\tDid not match any of the following: \");\n";
		code += SalsaCompiler.getIndent() + "\t\tfor (int i = 0; i < matches.length; i++) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.print(\"\\t\\tMethod: \" + matches[i].getName() + \"( \");\n";
		code += SalsaCompiler.getIndent() + "\t\t\tClass[] parTypes = matches[i].getParameterTypes();\n";
		code += SalsaCompiler.getIndent() + "\t\t\tfor (int j = 0; j < parTypes.length; j++) {\n";
		code += SalsaCompiler.getIndent() + "\t\t\t\tSystem.err.print(parTypes[j].getName() + \" \");\n";
		code += SalsaCompiler.getIndent() + "\t\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t\t\tSystem.err.println(\")\");\n";
		code += SalsaCompiler.getIndent() + "\t\t}\n";
		code += SalsaCompiler.getIndent() + "\t} else {\n";
		code += SalsaCompiler.getIndent() + "\t\tSystem.err.println(\"\\tThrew exception: \" + exception);\n";
		code += SalsaCompiler.getIndent() + "\t\texception.printStackTrace();\n";
		code += SalsaCompiler.getIndent() + "\t}\n";
		code += SalsaCompiler.getIndent() + "}\n\n";

		return code;
	}

	public String getPostCode() {
		SalsaCompiler.indent--;

		String code = SalsaCompiler.getIndent() + "}\n";
		code += "}";

		return code;
	}

	public String getChildCode() {
		String code = "";

		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				code += SalsaCompiler.getIndent() + getChild(i).getJavaCode();
			}
		}

		return code;
	}
}
