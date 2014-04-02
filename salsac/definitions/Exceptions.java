/*
	Code generation for the following grammar segment:
	
	Exceptions := Name ("," Name)*
*/

package salsac.definitions;

import salsac.*;

public class Exceptions extends SimpleNode {
	public Exceptions(int id) 					{ super(id); }
	public Exceptions(SalsaParser p, int id)	{ super(p, id); }
}