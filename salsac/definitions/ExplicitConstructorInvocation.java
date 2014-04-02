/*
	Code generation for the following grammar segment:
	
		ExplicitConstructorDeclaration :=
			"super" Arguments ";"
*/

package salsac.definitions;

import salsac.*;

public class ExplicitConstructorInvocation extends SimpleNode {
	public ExplicitConstructorInvocation(int id) 			{ super(id); }
	public ExplicitConstructorInvocation(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = SalsaCompiler.getIndent() + "super.construct( ";
		code += getChild(0).getJavaCode();
		code += " );\n";

		return code;
	}
}
