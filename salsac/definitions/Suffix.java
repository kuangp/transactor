/*
	Code generation for the following grammar segment:
	
	Suffix := "++" | "--"
*/


package salsac.definitions;

import salsac.*;

public class Suffix extends SimpleNode {

	public Suffix(int id) 			{ super(id); }
	public Suffix(SalsaParser p, int id)	{ super(p, id); }

}
