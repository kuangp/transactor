/*
	Code generation for the following grammar segment:
	
	MethodDeclaration :=
		MethodAttributes (Type | "void") <IDENTIFIER> FormalParameters ["throws" Exceptions]
*/

package salsac.definitions;

import salsac.*;

public class MethodLookahead extends SimpleNode {

	public MethodLookahead(int id)			{ super(id); }
	public MethodLookahead(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = getChild(0).getJavaCode();
		if (!code.equals("")) code += " ";
		else code += "public ";

		if ( !getToken(0).image.equals("void") ) {
			code += getChild(1).getJavaCode() + " " + getToken(0).image + "(" + getChild(2).getJavaCode() + ")";

			if (tokens.length > 1) {
				code += " throws " + getChild(3).getJavaCode();
			}
		} else {
			code += getToken(0).image + " " + getToken(1).image + "(" + getChild(1).getJavaCode() + ")";

			if (tokens.length > 2) {
				code += " throws " + getChild(2).getJavaCode();
			}
		}

		return code;
	}
}
