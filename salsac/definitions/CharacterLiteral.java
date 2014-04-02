/*
	Code generation for the following grammar segment:
	
	CharacterLiteral := <CHARACTER_LITERAL>
*/

package salsac.definitions;

import salsac.*;

public class CharacterLiteral extends SimpleNode {
	public CharacterLiteral(int id) 					{ super(id); }
	public CharacterLiteral(SalsaParser p, int id)	{ super(p, id); }
}