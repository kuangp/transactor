/*
	Code generation for the following grammar segment:
	
	ArrayInitializer :=
		"{" [(Expression | ArrayInitializer) ("," (Expression | ArrayInitializer))*] "}"
*/

package salsac.definitions;

import salsac.*;

public class ArrayInitializer extends SimpleNode {
	public ArrayInitializer(int id) 					{ super(id); }
	public ArrayInitializer(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "";
		for (int i = 0; i < children.length; i++) {
			code += getChild(i).getJavaCode();
			if (i != children.length-1) code += ", ";
		}
		return "{ " + code + " }";
	}
}