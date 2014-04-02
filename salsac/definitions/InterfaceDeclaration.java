/*
	Code generation for the following grammar segment:
	
	InterfaceDeclaration := "interface" <IDENTIFIER> ["extends" Name()] InterfaceBody()
*/


package salsac.definitions;

import salsac.*;

public class InterfaceDeclaration extends SimpleNode {

	public InterfaceDeclaration(int id) 			{ super(id); }
	public InterfaceDeclaration(SalsaParser p, int id)	{ super(p, id); }

	public String extendsName = null;

	public String getChildCode() {
		String code = "interface " + getToken(1);

		int i = 2;
		if (tokens.length < 3) {
			extendsName = "ActorReference";
			code += " extends ActorReference";
			code += " " + getChild(0).getJavaCode();
		} else {
			extendsName = getChild(i-2).getJavaCode();
			code += " extends " + getChild(i-2).getJavaCode();
		code += " " + getChild(1).getJavaCode();
		}
		return code;
	}
}
