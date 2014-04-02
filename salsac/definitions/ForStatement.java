/*
	Code generation for the following grammar segment:
	
	ForStatement:=
		"for" "(" ForInit ";" ForCondition ";" ForIncrement ")" Statement
*/

package salsac.definitions;

import salsac.*;

public class ForStatement extends SimpleNode {

	public ForStatement(int id) 			{ super(id); }
	public ForStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "for (" + getChild(0).getJavaCode() + "; " + getChild(1).getJavaCode() + "; " + getChild(2).getJavaCode() + ")" + 
				getChild(3).getJavaCode();
		
		if (((SimpleNode)parent).parent instanceof Block) {
			return SalsaCompiler.getIndent() + code;
		} else {
			return code;
		}
	}
}
