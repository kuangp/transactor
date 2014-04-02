/*
	Code generation for the following grammar segment:
	
	VariableDeclaration :=
		<IDENTIFIER> ("[" "]")* ["=" (Expression | ArrayInitializer)]
*/

package salsac.definitions;

import salsac.*;

public class VariableDeclaration extends SimpleNode {

	public VariableDeclaration(int id) 			{ super(id); }
	public VariableDeclaration(SalsaParser p, int id)	{ super(p, id); }


	public String getName() {
		return getToken(0).image;
	}

	public String getJavaCode() {
		String code = getToken(0).image;

		if (tokens.length > 1) {
			int i = 1;		
			while ( i < tokens.length && (getToken(i).image.equals("[") || getToken(i).image.equals("]")) ) {
				code += getToken(i);
				i++;
			}

			if ( (i < tokens.length) && getToken(i).image.equals("=")) {
				code += " = " + getChild(0).getJavaCode();
			}
		}

		return code;
	}
}
