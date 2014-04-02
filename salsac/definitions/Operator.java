/*
	Code generation for the following grammar segment:

	Operator := "||" | "&&" | "|" | "^" | "&" | "==" | "!=" | ">" | "<" | "<=" | ">=" | "<<" | ">>" | ">>>" | "+" | "-" | "*" | "/" | "%"	
*/


package salsac.definitions;

import salsac.*;

public class Operator extends SimpleNode {

	public Operator(int id) 			{ super(id); }
	public Operator(SalsaParser p, int id)		{ super(p, id); }

	public String getJavaCode() {
		String code = getToken(0).image;

		if (code.equals("instanceof")) code = " instanceof ";
		return code;
	}

}
