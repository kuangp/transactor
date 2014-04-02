/*
	Code generation for the following grammar segment:
	
	ConstructorAttributes :=
		("public" | "protected" | "private")*
*/

package salsac.definitions;

import salsac.*;

public class ConstructorAttributes extends SimpleNode {

	public ConstructorAttributes(int id) 					{ super(id); }
	public ConstructorAttributes(SalsaParser p, int id)	{ super(p, id); }


	public String getChildCode() {
		Token current = first_token;

		String code = "";		
		for (; current != null; current = current.next) {
			code += current.image + " ";
		}
		return code;
	}
}