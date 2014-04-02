/*
	Code generation for the following grammar segment:
	
	SwitchLabel := 	  
		  "case" Expression() ":"
		| "default" ":"
*/

package salsac.definitions;

import salsac.*;

public class SwitchLabel extends SimpleNode {

	public SwitchLabel(int id) 			{ super(id); }
	public SwitchLabel(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		if (children == null) return SalsaCompiler.getIndent() + "default: ";
		else return SalsaCompiler.getIndent() + "case " + getChild(0).getJavaCode() + ": ";
	}
}
