/*
	Code generation for the following grammar segment:

	IfStatement := "if" "(" Expression ")" Statement ["else" Statement]
*/

package salsac.definitions;

import salsac.*;

public class IfStatement extends SimpleNode {

	public IfStatement(int id) 					{ super(id); }
	public IfStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "if (" + getChild(0).getJavaCode() + ") {" + getChild(1).getJavaCode()+"}";

		if (children.length > 2) {
			code += SalsaCompiler.getIndent() + "else {" + getChild(2).getJavaCode()+"}";
		}

		if ( ((SimpleNode)parent).parent instanceof Block) {
			return SalsaCompiler.getIndent() + code;
		} else {
			return code;
		}
	}
}
