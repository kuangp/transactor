/*
	Code generation for the following grammar segment:
	
	WhileStatement := "while" "(" Expression ")" Statement
*/

package salsac.definitions;

import salsac.*;

public class WhileStatement extends SimpleNode {

	public WhileStatement(int id) 					{ super(id); }
	public WhileStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		return SalsaCompiler.getIndent() + "while (" + getChild(0).getJavaCode() + ") " + getChild(1).getJavaCode();
	}
}
