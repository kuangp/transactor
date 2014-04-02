/*
	Code generation for the following grammar segment:
	
	Type := PrimitiveType | Name ( "[" "]" )*
*/

package salsac.definitions;

import salsac.*;

public class Type extends SimpleNode {
	public Type(int id) 			{ super(id); }
	public Type(SalsaParser p, int id)	{ super(p, id); }

	public String getPostCode() {
		String code = "";
		if ( tokens != null ) {
			for (int i = 0; i < tokens.length; i++) code += getToken(i).image;
		}
		return code;
	}
}
