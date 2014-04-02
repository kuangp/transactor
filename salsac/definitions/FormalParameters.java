/*
	Code generation for the following grammar segment:

	FormalParameters :=
		"(" ["final" Type <IDENTIFIER> ( "[" "]" )* ("," ["final"] Type <IDENTIFIER> ("[" "]")* )*] ")"	
*/

package salsac.definitions;

import salsac.*;

public class FormalParameters extends SimpleNode {
	public FormalParameters(int id) 		{ super(id); }
	public FormalParameters(SalsaParser p, int id)	{ super(p, id); }

	public String[] getParameterTypes() {
		if (children != null) {
			String[] parameterTypes = new String[children.length];
			for (int i = 0; i < children.length; i++) parameterTypes[i] = getChild(i).getJavaCode();
			return parameterTypes;
		} else {
			return null;
		}
	}

	public String getNonPrimitiveNames() {
		String code = "";

		int childPlace = 0;

		if (children != null) {
			for (int i = 0; i < tokens.length; i++) {
				if ( getToken(i).image.equals("(") || getToken(i).image.equals(",") ) {
					if (getToken(i+1).image.equals("final")) i++;

					if (getChild(childPlace).getJavaCode().equals("int"))		code += "new Integer(" + getToken(i+1) + ")";
					else if (getChild(childPlace).getJavaCode().equals("long"))	code += "new Long(" + getToken(i+1) + ")";
					else if (getChild(childPlace).getJavaCode().equals("float"))	code += "new Float(" + getToken(i+1) + ")";
					else if (getChild(childPlace).getJavaCode().equals("double"))	code += "new Double(" + getToken(i+1) + ")";
					else if (getChild(childPlace).getJavaCode().equals("char"))	code += "new Character(" + getToken(i+1) + ")";
					else if (getChild(childPlace).getJavaCode().equals("boolean"))	code += "new Boolean(" + getToken(i+1) + ")";
					else if (getChild(childPlace).getJavaCode().equals("byte"))	code += "new Byte(" + getToken(i+1) + ")";
					else code += getToken(i+1);
					childPlace++;

					if (childPlace != children.length) code += ", ";
				}
			}
		}

		return code;
	}

	public String getJavaCode() {
		String code = "";

		int i = 0;
		int j = 0;
		while (i < tokens.length) {
			if (getToken(i).image.equals("(") || getToken(i).image.equals(",")) {
				if (getToken(i).image.equals(",")) code += ", ";

				if (!getToken(i+1).image.equals("final")) {
					if (children != null) code += getChild(j).getJavaCode() + " ";
					j++;
				}
			} else {
				if (!getToken(i).image.equals(")")) code += getToken(i).image;
			}

			i++;
		}

		return code;
	}
}
