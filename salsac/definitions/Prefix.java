/*
	Code generation for the following grammar segment:
	
	Prefix := "++" | "--" | "~" | "!"
*/


package salsac.definitions;

import salsac.*;

public class Prefix extends SimpleNode {

	public Prefix(int id) 			{ super(id); }
	public Prefix(SalsaParser p, int id)	{ super(p, id); }

}
