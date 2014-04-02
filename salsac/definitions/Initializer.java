/*
	Code generation for the following grammar segment:
	
	Initializer := ["static"] Block()
*/

package salsac.definitions;

import salsac.*;


public class Initializer extends SimpleNode {

	public Initializer(int id) 					{ super(id); }
	public Initializer(SalsaParser p, int id)	{ super(p, id); }

	public String getPreCode() {
		if (tokens.length != 0) return "static ";
		else return "";
	}
}
