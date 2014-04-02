/*
	Code generation for the following grammar segment:
	
	NullLiteral := "null"
*/

package salsac.definitions;

import salsac.*;

public class NullLiteral extends SimpleNode {

	public NullLiteral(int id) 					{ super(id); }
	public NullLiteral(SalsaParser p, int id)	{ super(p, id); }

}