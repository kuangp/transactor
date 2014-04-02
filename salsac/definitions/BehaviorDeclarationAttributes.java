/*
	Code generation for the following grammar segment:
	
	BehaviorDeclarationAttributes := ("abstract" | "public" | "final")*
*/

package salsac.definitions;

import salsac.*;

public class BehaviorDeclarationAttributes extends SimpleNode {

	public BehaviorDeclarationAttributes(int id) 					{ super(id); }
	public BehaviorDeclarationAttributes(SalsaParser p, int id)	{ super(p, id); }

	public String getChildCode() {
		if (tokens == null || tokens.length == 0) return "public ";

		String code = "";
		for (int i = 0; i < tokens.length; i++) code += getToken(i).image + " ";
		return code;
	}
}
