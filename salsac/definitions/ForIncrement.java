/*
	Code generation for the following grammar segment:
	
	ForIncrement := [StatementExpression ("," StatementExpression)*]
*/

package salsac.definitions;

import salsac.*;

public class ForIncrement extends SimpleNode {

	public ForIncrement(int id) 			{ super(id); }
	public ForIncrement(SalsaParser p, int id)	{ super(p, id); }

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
