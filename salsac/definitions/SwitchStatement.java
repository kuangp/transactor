/*
	Code generation for the following grammar segment:
	
	SwitchStatement := "switch" "(" Expression ")" "{" (SwitchLabel Statement* )* "}"
*/

package salsac.definitions;

import salsac.*;

public class SwitchStatement extends SimpleNode {

	public SwitchStatement(int id) 			{ super(id); }
	public SwitchStatement(SalsaParser p, int id)	{ super(p, id); }


	public String getJavaCode() {
		String code = SalsaCompiler.getIndent() + "switch (" + getChild(0).getJavaCode() + ") {\n";

		for (int i = 1; i < children.length; i++) {
			if (getChild(i).id == SalsaParserTreeConstants.JJTSWITCHLABEL) ;

			code += getChild(i).getJavaCode();
		}

		code += SalsaCompiler.getIndent() + "}\n";
		
		return code;
	}
}
