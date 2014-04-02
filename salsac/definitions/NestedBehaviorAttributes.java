/*
	Code generation for the following grammar segment:
	
	NestedBehaviorAttributes := ("abstract" | "public" | "final" | "protected" | "private" | "static")*
*/

package salsac.definitions;

import salsac.*;

public class NestedBehaviorAttributes extends SimpleNode {

	public NestedBehaviorAttributes(int id) 					{ super(id); }
	public NestedBehaviorAttributes(SalsaParser p, int id)	{ super(p, id); }


	public String getChildCode() {
		Token current = first_token;

		String code = "";		
		for (; current != null; current = current.next) {
			code += current.image + " ";
		}
		return code;
	}
}