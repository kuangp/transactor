/*
	Code generation for the following grammar segment:
	
	TryStatement :=
		"try" Block ("catch" "(" ["final"] Type <IDENTIFIER> ")" Block)*  ["finally" Block]
*/

package salsac.definitions;

import salsac.*;

public class TryStatement extends SimpleNode {

	public TryStatement(int id) 			{ super(id); }
	public TryStatement(SalsaParser p, int id)	{ super(p, id); }


	public String getJavaCode() {
		String code = "";
		code += SalsaCompiler.getIndent() + "try ";
		code += getChild(0).getJavaCode();

		int i = 1;
		int j = 1;

		while ( (i < tokens.length) && getToken(i).image.equals("catch")) {
			code += SalsaCompiler.getIndent() + "catch (";
			i += 2;

			if (getToken(i).image.equals("final")) {
				code += "final ";
				i++;
			}

			code += getChild(j).getJavaCode() + " " + getToken(i) + ") ";
			i += 2;
			j++;

			code += getChild(j).getJavaCode();
			j++;
		}

		if (i < tokens.length) {
			code += SalsaCompiler.getIndent() + "finally ";
			code += getChild(j).getJavaCode();
		}
		code += "\n";

		return code;
	}
}
