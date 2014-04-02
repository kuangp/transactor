/*
	Code generation for the following grammar segment:
	
	ImportDeclaration := "import" <IDENTIFIER> ("." (<IDENTIFIER> | "*") )* ";"
*/


package salsac.definitions;

import salsac.*;


public class ImportDeclaration extends SimpleNode {

	public ImportDeclaration(int id) 					{ super(id); }
	public ImportDeclaration(SalsaParser p, int id)	{ super(p, id); }

	public String getChildCode() {
		String code = "";
		
		for (int i = 1; i < tokens.length-1; i++) {
			code += getToken(i);
		}
		
		if (getToken(tokens.length-2).image.equals("*")) {
			SalsaCompiler.addPackage( code.substring(0, code.length()-3) );
		} else {
			SalsaCompiler.addImport( code );
		}

		return "import " + code + ";\n";
	}
}
