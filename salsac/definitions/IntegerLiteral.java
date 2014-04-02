/*
	Code generation for the following grammar segment:
	
	IntegerLiteral := <INTEGER_LITERAL>
*/

package salsac.definitions;

import salsac.*;

public class IntegerLiteral extends SimpleNode {
	public IntegerLiteral(int id) 					{ super(id); }
	public IntegerLiteral(SalsaParser p, int id)	{ super(p, id); }

}