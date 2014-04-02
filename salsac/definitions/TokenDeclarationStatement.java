/*
	Code generation for the following grammar segment:
	
		TokenDeclarationStatement :=
			"token" <IDENTIFIER> "=" Expression()
*/

package salsac.definitions;

import salsac.*;

public class TokenDeclarationStatement extends SimpleNode {
	public TokenDeclarationStatement(int id) 		{ super(id); }
	public TokenDeclarationStatement(SalsaParser p, int id)	{ super(p, id); }


	public String getJavaCode() { 
		String code = SalsaCompiler.getIndent() + "Token " + getToken(1) + " = ";

		SalsaCompiler.symbolTable.addSymbol( getToken(1).image, "token" );

		String symbolType = SalsaCompiler.symbolTable.getSymbolType( getChild(0).getJavaCode() );

		if (symbolType != null) {
			if (symbolType.equals("token") || symbolType.equals("next")) {
				code += getChild(0).getJavaCode() + ";\n";
			}
			return code;
		}

		code += "new Token( \"" + getToken(1).image + "\", " + getChild(0).getJavaCode() + " );\n";
		return code;
	}
}
