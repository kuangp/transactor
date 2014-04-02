/*
	Code generation for the following grammar segment:

	BehaviorDeclaration := "behavior" <IDENTIFIER> ["extends" Name()] ["implements" Name() ("," Name())*] BehaviorBody()
*/

//****************
//modification of getChildCode() by weijen
//  target: to see if the acotr is a system service
//          an actor is a service only if it implements EnvironmentalService.
//          if so, set 'SalsaCompiler.isService' true
//          BehaviorBody use 'SalsaCompiler.isService' to generate different code accordingly.
//****************


package salsac.definitions;

import salsac.*;
import java.util.Vector;

public class BehaviorDeclaration extends SimpleNode {

	public BehaviorDeclaration(int id) 			{ super(id); }
	public BehaviorDeclaration(SalsaParser p, int id)	{ super(p, id); }

	public String extendsName = null;
	public Vector implementNames = new Vector();

	public String getChildCode() {
		String code = "class " + getToken(1);					//"behavior <IDENTIFIER>"

		if (tokens.length == 2 || !getToken(2).image.equals("extends")) {
			String actorType = System.getProperty("actor");
			if (actorType == null) {
				extendsName = "UniversalActor ";
				code += " extends UniversalActor ";
			} else {
				extendsName = actorType;
				code += " extends " + actorType + " ";
			}
		}

                boolean isActorServiceExisted=false;
                boolean isImplement=false;
		int i = 2;
                if (!SalsaCompiler.isService) {
//*********************
// $ start of label 0
// if not -Dservice in compiler arguments
// ********************
                  for (; i < tokens.length; i++) {
                    if (getToken(i).image.equals("extends")) {
                      extendsName = getChild(i - 2).getJavaCode();
                      code += " " + getToken(i).image + " " + getChild(i - 2).getJavaCode();
                    }
                    else if (getToken(i).image.equals(",")) {
                      implementNames.add(getChild(i - 2).getJavaCode());

                      // to see if it is a system service
                      if (getChild(i - 2).getJavaCode().equals("ActorService")) {
                        SalsaCompiler.isService = true;
                      }

                      code += ", " + getChild(i - 2).getJavaCode();
                    }
                    else if (getToken(i).image.equals("implements")) {
                      code += " implements ";

                      // to see if it is a system service
                      implementNames.add(getChild(i - 2).getJavaCode());
                      if (getChild(i - 2).getJavaCode().equals("ActorService")) {
                        SalsaCompiler.isService = true;
                      }
                      code += getChild(i - 2).getJavaCode();
                    }
                  }
//*********************
// $ end of label 0
// ********************

                } else {
//*********************
// $ start of label 1
// if the compiler wants to produce the system service code (by
//  specifying the compiler argument -Dservice)
// ********************
                   for (; i < tokens.length; i++) {
                     if (getToken(i).image.equals("extends")) {
                       extendsName = getChild(i - 2).getJavaCode();
                       code += " " + getToken(i).image + " " + getChild(i - 2).getJavaCode();
                     }
                     else if (getToken(i).image.equals(",")) {
                       implementNames.add(getChild(i - 2).getJavaCode());

                       // to see if it is a system service
                       if (getChild(i - 2).getJavaCode().equals("ActorService")) {
                         SalsaCompiler.isService = true;
                         isActorServiceExisted=true;
                       }

                       code += ", " + getChild(i - 2).getJavaCode();
                     }
                     else if (getToken(i).image.equals("implements")) {
                       code += " implements ";
                       isImplement=true;
                       // to see if it is a system service
                       implementNames.add(getChild(i - 2).getJavaCode());
                       if (getChild(i - 2).getJavaCode().equals("ActorService")) {
                         SalsaCompiler.isService = true;
                         isActorServiceExisted=true;
                       }
                       code += getChild(i - 2).getJavaCode();
                     }
                   }
                   if (!isImplement) {
                     implementNames.add("ActorService");
                     code += " implements ActorService ";
                   } else if (!isActorServiceExisted) {
                     implementNames.add("ActorService");
                     code += ", ActorService ";
                   }
//*********************
// $ end of label 1
// ********************

                }

		SalsaCompiler.extendsName = extendsName;
		SalsaCompiler.implementNames = implementNames;
		SalsaCompiler.actorName = getToken(1).image;


		code += " " + getChild(i-2).getJavaCode();

		return code;
	}
}
