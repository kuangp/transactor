/*
	Code generation for the following grammar segment:
	
	LocalVariableDeclaration :=
		["final"] Type VariableDeclaration ("," VariableDeclaration)*
*/

package salsac.definitions;

import salsac.*;

public class LocalVariableDeclaration extends SimpleNode {

	public LocalVariableDeclaration(int id) 					{ super(id); }
	public LocalVariableDeclaration(SalsaParser p, int id)	{ super(p, id); }


	public String getJavaCode() {
		//Add this variable to the symbol table
		SalsaCompiler.symbolTable.addSymbol( ((VariableDeclaration)getChild(1)).getName(), getChild(0).getJavaCode() );

		String code = "";
		if (tokens != null && getToken(0).image.equals("final")) {
			code += "final ";
		}
		code += getChild(0).getJavaCode() + " " + getChild(1).getJavaCode();
		
		for (int i = 2; i < children.length; i++) {
			code += ", " + getChild(i).getJavaCode();
		}
		
		if ( ((SimpleNode)parent).parent instanceof Block) {
			return SalsaCompiler.getIndent() + code;
		} else {
			return code;
		}
	}
}
