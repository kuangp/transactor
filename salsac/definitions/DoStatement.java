/*
	Code generation for the following grammar segment:
	
	DoStatement := "do" Statement "while" "(" Expression ")" ";"
*/

package salsac.definitions;

import salsac.*;

public class DoStatement extends SimpleNode {

	public DoStatement(int id) 					{ super(id); }
	public DoStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		return "do " + getChild(0).getJavaCode() + " while (" + getChild(1).getJavaCode() + ");\n";
	}
}
