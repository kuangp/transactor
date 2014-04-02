/*
	Code generation for the following grammar segment:
	
	Name := <IDENTIFIER> ("." <IDENTIFIER>)*
*/

package salsac.definitions;

import salsac.*;

public class Name extends SimpleNode {

	public Name(int id) 			{ super(id); }
	public Name(SalsaParser p, int id)	{ super(p, id); }

	public String getChildCode() {
		String code = "";
		for (int i = 0; i < tokens.length; i++) {
			code += getToken(i);
		}
		return code;
	}
}
