/*
	Code generation for the following grammar segment:
	
	Block := "{" Statement* "}"
*/

package salsac.definitions;

import salsac.*;

public class Block extends SimpleNode {

	public Block(int id) 					{ super(id); }
	public Block(SalsaParser p, int id)	{ super(p, id); }

	public boolean containsMessageSend() {
		for (int i = 0; i < children.length; i++) {
			if ( getChild(i) instanceof ContinuationStatement ) return true;
		}
		return false;
	}

	public String getPreCode() 	{ 
		//a new block creates a lower level of the symbol table
		SalsaCompiler.symbolTable = new SymbolTable(SalsaCompiler.symbolTable);
		SalsaCompiler.indent++;

		return "{\n";
	}

	public String getPostCode() {
		//move back up the symbol table as we leave the block
		SalsaCompiler.symbolTable = SalsaCompiler.symbolTable.parent;
		SalsaCompiler.indent--;

		return SalsaCompiler.getIndent() + "}\n";
	}
	
	public String getChildCode() {
		String code = "";

		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				code += getChild(i).getJavaCode();
			}
		}

		return code;
	}
}
