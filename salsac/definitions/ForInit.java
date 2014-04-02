/*
	Code generation for the following grammar segment:
	
	ForInit :=
		[ LocalVariableDeclaration | (StatementExpression ("," StatementExpression)*)]
*/

package salsac.definitions;

import salsac.*;

public class ForInit extends SimpleNode {

	public ForInit(int id) 			{ super(id); }
	public ForInit(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "";
		if (children != null) {
			code += getChild(0).getJavaCode();

			if (tokens != null) {
				for (int i = 0; i < tokens.length; i++) {
					code += ", " + getChild(i+1).getJavaCode();
				}
			}
		}
		return code;
	}
}
