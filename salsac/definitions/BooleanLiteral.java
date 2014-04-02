/*
	Code generation for the following grammar segment:
	
	BooleanLiteral := "true" | "false"
*/

package salsac.definitions;

import salsac.*;

public class BooleanLiteral extends SimpleNode {
	public BooleanLiteral(int id) 					{ super(id); }
	public BooleanLiteral(SalsaParser p, int id)	{ super(p, id); }
}