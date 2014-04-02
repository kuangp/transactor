/*
	Code generation for the following grammar segment:

	ArrayDimsAndInits :=
		  ("[" Expression() "]")+ ("[" "]")*
		| ("[" "]")+ ArrayInitializer()
*/

package salsac.definitions;

import salsac.*;

public class ArrayDimsAndInits extends SimpleNode {

	public ArrayDimsAndInits(int id) 					{ super(id); }
	public ArrayDimsAndInits(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		if (getChild(0) instanceof Expression) {
			int i = 0;
			String code = "";

			for (; i < children.length; i++) {
				code += "[" + getChild(i).getJavaCode() + "]";
			}

			i = i * 2;
			for (; i < tokens.length; i++) {
				code += getToken(i);
			}
			return code;
		} else {
			return getTokenCode() + getChild(0).getJavaCode();
		}
	}
}
