/*
	Code generation for the following grammar segment:

	PrimitiveType :=
		"boolean" | "char" | "byte" | "short" | "int" | "long" | "float" | "double"
*/

package salsac.definitions;

import salsac.*;

public class PrimitiveType extends SimpleNode {

	public PrimitiveType(int id) 			{ super(id); }
	public PrimitiveType(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		return getToken(0).image;
	}
}
