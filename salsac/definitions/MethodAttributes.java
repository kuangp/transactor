/*
	Code generation for the following grammar segment:
	
	MethodAttributes := 
		("public" | "protected" | "private" | "static" | "abstract" | "final" | "native" | "synchronized")*
*/

package salsac.definitions;

import salsac.*;

public class MethodAttributes extends SimpleNode {

	public MethodAttributes(int id) 					{ super(id); }
	public MethodAttributes(SalsaParser p, int id)	{ super(p, id); }


	public String getChildCode() {
		if (tokens == null) return "";

		String code = "";
		for (int i = 0; i < tokens.length; i++) {
			code += getToken(i).image + " ";
		}
		return code;
	}
}
