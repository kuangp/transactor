/*
	Code generation for the following grammar segment:
	
	ReturnStatement := "return" [Expression] ";"
*/

package salsac.definitions;

import salsac.*;

public class ReturnStatement extends SimpleNode {

	public ReturnStatement(int id) 			{ super(id); }
	public ReturnStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "";
		if (children != null) {
			code += SalsaCompiler.getIndent() + "return " + getChild(0).getJavaCode() + ";\n";
		} else {
			code += SalsaCompiler.getIndent() + "return;\n";
		}

		return code;
	}
}
