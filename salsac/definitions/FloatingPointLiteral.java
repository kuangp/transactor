/*
	Code generation for the following grammar segment:
	
	FloatingPointLiteral := <FLOATING_POINT_LITERAL>
*/

package salsac.definitions;

import salsac.*;

public class FloatingPointLiteral extends SimpleNode {
	public FloatingPointLiteral(int id) 					{ super(id); }
	public FloatingPointLiteral(SalsaParser p, int id)	{ super(p, id); }
}