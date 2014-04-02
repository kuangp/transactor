/*
	Code generation for the following code segment:
	
	StateVariableDeclaration :=
		StateVariableAttributes Type VariableDeclaration ("," VariableDeclaration)* ";"
*/

package salsac.definitions;

import salsac.*;

public class StateVariableDeclaration extends SimpleNode {

	public StateVariableDeclaration(int id) 					{ super(id); }
	public StateVariableDeclaration(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		//Add this variable to the symbol table
		SalsaCompiler.symbolTable.addSymbol( ((VariableDeclaration)getChild(2)).getName(), getChild(1).getJavaCode() );

		String code = getChild(0).getJavaCode();
		if (!code.equals("")) code += " ";

		code += getChild(1).getJavaCode() + " " + getChild(2).getJavaCode();

		for (int i = 3; i < children.length; i++) {
			code += ", " + getChild(i).getJavaCode();
		}
		return code + ";\n";
	}
}
