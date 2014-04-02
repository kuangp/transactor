/*
	Code generation for the following grammar segment:
	
	StateVariableAttributes :=
		("public" | "protected" | "private" | "volatile" | "static" | "final" | "transient")*
*/

package salsac.definitions;

import salsac.*;

public class StateVariableAttributes extends SimpleNode {

	public StateVariableAttributes(int id) 					{ super(id); }
	public StateVariableAttributes(SalsaParser p, int id)	{ super(p, id); }

	public String getChildCode() {
		Token current = first_token;

		String code = "";		
		for (; current != null; current = current.next) {
			code += current.image + " ";
		}
		return code;
	}
}