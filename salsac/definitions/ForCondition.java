/*
	Code generation for the following grammar segment:
	
	ForCondition := [Expression]
*/

package salsac.definitions;

import salsac.*;

public class ForCondition extends SimpleNode {

	public ForCondition(int id) 			{ super(id); }
	public ForCondition(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "";
		if (children != null) {
			code += getChild(0).getJavaCode();
		}
		return code;
	}
}
