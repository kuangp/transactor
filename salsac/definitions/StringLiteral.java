/*
	Code generation for the following grammar segment:
	
	StringLiteral:= <STRING_LITERAL>
*/

package salsac.definitions;

import salsac.*;

public class StringLiteral extends SimpleNode {

	public StringLiteral(int id) 					{ super(id); }
	public StringLiteral(SalsaParser p, int id)	{ super(p, id); }

}