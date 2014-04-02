/*
	Code generation for the following grammar segment:
	
	SynchronizedStatement := "synchronized" "(" Expression ")" Block
*/

package salsac.definitions;

import salsac.*;

public class SynchronizedStatement extends SimpleNode {

	public SynchronizedStatement(int id) 					{ super(id); }
	public SynchronizedStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "synchronized (";
		
		code += getChild(0).getJavaCode();
		
		code += ") {\n";
		for (int i = 1; i < children.length; i++) {
			code += getChild(i).getJavaCode();
		}
		code += "}\n";
		
		return code;
	}
}