/*
	Code generation for the following grammar segment:

	ConstructorDeclaration :=
		MethodAttributes() <IDENTIFIER> FormalParameters() ["throws" Exceptions()] "{" [ExplicitConstructorInvocation()] Statement()* "}"
*/

package salsac.definitions;

import salsac.*;
import salsac.SalsaCompiler;

public class ConstructorDeclaration extends SimpleNode {
	public ConstructorDeclaration(int id) 			{ super(id); }
	public ConstructorDeclaration(SalsaParser p, int id)	{ super(p, id); }

	private int i = 0;

	public String getPreCode() 	{
		//a new block creates a lower level of the symbol table
		SalsaCompiler.symbolTable = new SymbolTable(SalsaCompiler.symbolTable);
		SalsaCompiler.indent++;

		if ( !SalsaCompiler.getActorName().equals(getToken(0).image) ) {
                    try{
                      System.err.println("Salsa Compiler Error:");
                      System.err.println("\tLine: " + getChild(i).getToken(0).beginLine);
                      System.err.println("\tMethod \"" + getChild(i).getToken(0).image +
                                         "\" does not have a return type.");
                      System.exit(0);
                    }catch (Exception e) {
                      System.err.println("Salsa Compiler Error:");
                      System.err.println("\tLine: " + getToken(0).beginLine);
                      System.err.println("\tMethod \"" + getToken(0).image +
                                         "\" does not have a type.");
                      System.exit(0);
                    }
		}

		String code = getChild(0).getJavaCode();

		code += "void construct(" + getChild(1).getJavaCode() + ")";

		i = 2;
		if (getToken(1).image.equals("throws")) {
			code += " throws " + getChild(2).getJavaCode();
			i = 3;
		}
		code += "{\n";

		return code;
	}

	public String getPostCode() {
		//move back up the symbol table as we leave the block
		SalsaCompiler.symbolTable = SalsaCompiler.symbolTable.parent;
		SalsaCompiler.indent--;

		return SalsaCompiler.getIndent() + "}\n";
	}

	public String getChildCode() {
		String code = "";

		if (i < children.length) {
			if (getChild(i) instanceof ExplicitConstructorInvocation) {
				code += getChild(i).getJavaCode();
				i++;
			}
		}

		for (; i < children.length; i++) {
			if ( !(getChild(i) instanceof ContinuationStatement) ) {
				code += SalsaCompiler.getIndent();
			}
			code += getChild(i).getJavaCode();
		}

		return code;
	}


}
