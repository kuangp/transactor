/*
	Code generation for the following grammar segment:
	
	ThrowStatement := "throw" Expression ";"
*/

package salsac.definitions;

import salsac.*;

public class ThrowStatement extends SimpleNode {

	public ThrowStatement(int id) 					{ super(id); }
	public ThrowStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		return SalsaCompiler.getIndent() + "throw " + getChild(0).getJavaCode() + ";\n";
	}
}
