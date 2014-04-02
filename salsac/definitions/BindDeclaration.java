/*
	Code generation for the following grammar segment:

	BindDeclaration := "at" "(" Expression ["," Expression] ")"
*/


package salsac.definitions;

import salsac.*;

public class BindDeclaration extends SimpleNode {

	public BindDeclaration(int id) 			{ super(id); }
	public BindDeclaration(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = getChild(0).getJavaCode();
		if (children.length > 1) code += ", " + getChild(1).getJavaCode();
		return code;
	}
}
