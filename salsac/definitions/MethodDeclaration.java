/*
	Code generation for the following grammar segment:
	
	MethodDeclaration :=
		MethodAttributes (Type | "void") <IDENTIFIER> FormalParameters ["throws" Exceptions] Block
*/

package salsac.definitions;

import salsac.*;

public class MethodDeclaration extends SimpleNode {

	public MethodDeclaration(int id) 		{ super(id); }
	public MethodDeclaration(SalsaParser p, int id)	{ super(p, id); }

	public String getMethodName() {
		if ( !getToken(0).image.equals("void") ) return getToken(0).image;
		else return getToken(1).image;
	}

	public String[] getParameterTypes() {
		if ( getChild(1) instanceof FormalParameters ) return ((FormalParameters)getChild(1)).getParameterTypes();
		else return ((FormalParameters)getChild(2)).getParameterTypes();
	}

	public String getJavaCode() {
		String code = getChild(0).getJavaCode();
		if (code.equals("")) code += "public ";


		String methodName = getMethodName();
		if ( System.getProperty("silent") == null &&
		     ( methodName.equals("start")
		    || methodName.equals("stop")
		    || methodName.equals("sleep")
		    || methodName.equals("suspend")
		    || methodName.equals("resume")
		    || methodName.equals("wait")
		    || methodName.equals("notify")
		    || methodName.equals("main") 
		    || methodName.equals("construct")
		    || methodName.equals("putMessageInMailbox")
		    || methodName.equals("addClassName")
		    || methodName.equals("addMethodsForClasses")
		    || methodName.equals("migrate")
		    || methodName.equals("bind")
		    || methodName.equals("getMatches")
		    || methodName.equals("addMethod")
		    || methodName.equals("updateSelf")
		    || methodName.equals("resolveToken")
		    || methodName.equals("getMessage")
		    || methodName.equals("process")
		    || methodName.equals("run")
		    || methodName.equals("sendGeneratedMessages")
		    || methodName.equals("equals")
		    || methodName.equals("MessageTargetNotFound")
		    || methodName.equals("reName")
		    || methodName.equals("readObject")
		    || methodName.equals("writeObject")) ) {
			System.err.print("Salsa Compiler Warning: Line ");

			if (getToken(0).image.equals("void")) System.err.println( getToken(1).beginLine );
			else System.err.println( getToken(0).beginLine );

			System.err.println("\tAttempting to override the reserved SALSA method: " + methodName);
			System.err.println("\tThis could cause serious problems with the SALSA runtime and is not recommended.");
		}

		int i = 0;
		if ( !getToken(0).image.equals("void") ) {
			code += getChild(1).getJavaCode() + " " + getToken(0).image + "(" + getChild(2).getJavaCode() + ")";

			if (tokens.length > 1) {
				code += " throws " + getChild(3).getJavaCode();
				i = 4;
			} else {
				code += " ";
				i = 3;
			}
		} else {
			code += getToken(0).image + " " + getToken(1).image + "(" + getChild(1).getJavaCode() +")";

			if (tokens.length > 2) {
				code += " throws " + getChild(2).getJavaCode();
				i = 3;
			} else {
				code += " ";
				i = 2;
			}
		}
		code += getChild(i).getJavaCode();

		return code;
	}
}
