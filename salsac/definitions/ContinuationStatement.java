/*
	Code generation for the following grammar segment:
	
	ContinuationStatement :=
		(MessageStatement "@")* MessageStatement ["currentContinuation"] ";"
*/

package salsac.definitions;

import salsac.*;
import salsac.SalsaCompiler;

import java.util.Vector;

public class ContinuationStatement extends SimpleNode {

	public ContinuationStatement(int id) 			{ super(id); }
	public ContinuationStatement(SalsaParser p, int id)	{ super(p, id); }

	public Vector namedTokensUsed = new Vector();

	public String getNamedTokenCode() {
		String code = "";
		
		int numNamedgetToken = 0;
		for (int i = 0; i < children.length; i++) {
			if (getChild(i).children.length == 2) {
				//add named token declarations outside the message sending block,
				//so future code may use them.
				if (getChild(i).getChild(0).getToken(0).image.equals("token")) {
					String tokenName = getChild(i).getChild(0).getToken(1).image;
				
					code += SalsaCompiler.getIndent() + "Token " + tokenName + " = new Token(\"" + tokenName + "\");\n";

					SalsaCompiler.symbolTable.addSymbol(tokenName, "token");
				}
			}
		}

		return code;
	}

	public String getUnnamedTokenCode() {
		String code = "";
		int i = 0;
		for (; i < children.length-1; i++) {
			if (getChild(i).children.length == 1) {
				code += SalsaCompiler.getIndent() + "Token token_" + SalsaCompiler.symbolTable.getBlockLevel() + "_" + i + " = new Token();\n";
			}
		}
		return code;
	}


	public String getJavaCode() {
		String code = "";

		code += getNamedTokenCode();

		//create a new code block for the message sending code
		code += SalsaCompiler.getIndent() + "{\n";
		SalsaCompiler.indent++;

		SalsaCompiler.symbolTable = new SymbolTable(SalsaCompiler.symbolTable);

		code += getUnnamedTokenCode();
		
		String continuationInput = SalsaCompiler.symbolTable.getContinuationInput();
		String continuationOutput = SalsaCompiler.symbolTable.getContinuationOutput();
		//if continuation output and input aren't null, this contiunation is inside of a join block,
		if (continuationOutput == null) continuationOutput = "null";
		else {
			SalsaCompiler.symbolTable.setJoinBlockOutput(continuationOutput);
		}
		if (continuationInput == null) continuationInput = "null";

		if (tokens.length > 1 && getToken(tokens.length-2).image.equals("currentContinuation")) {
			if (continuationOutput.equals("null")) continuationOutput = "currentContinuation";
			else {
				System.err.println("Syntax Error: line " + getToken(tokens.length-1).beginLine);
				System.err.println("\tcurrentContinuation not allowed within a join block, symantically unclear.");
				System.exit(0);
			}
		}

		SalsaCompiler.symbolTable.setContinuationInput( continuationInput );
		for (int i = 0; i < children.length; i++) {

			if (i == children.length-1) {
				SalsaCompiler.symbolTable.setContinuationOutput( continuationOutput );
			} else if (getChild(i).children.length != 2) {
				SalsaCompiler.symbolTable.setContinuationOutput( "token_" + SalsaCompiler.symbolTable.getBlockLevel() + "_" + i );
			}

			if ( i == (children.length-1) ) {
				if ( getChild(i).getChild( getChild(i).children.length-1 ) instanceof MessageSend ) {
					(( MessageSend )getChild(i).getChild( getChild(i).children.length-1 ) ).lastMessageInContinuation = true;
				}
			}

			code += getChild(i).getJavaCode();

			if (getChild(i).children.length != 2) {
				SalsaCompiler.symbolTable.setContinuationInput( "token_" + SalsaCompiler.symbolTable.getBlockLevel() + "_" + i );
			}
		}

		SalsaCompiler.symbolTable.setJoinBlockOutput(null);

		//move back up the symbol table as the code block is left
		SalsaCompiler.symbolTable = SalsaCompiler.symbolTable.parent;
		SalsaCompiler.indent--;


		if ( (tokens.length > 1) && getToken(tokens.length-2).image.equals("currentContinuation") ) {
			code += SalsaCompiler.getIndent() + "\tthrow new CurrentContinuationException();\n";
		}


		code += SalsaCompiler.getIndent() + "}\n";

		return code;
	}
}
