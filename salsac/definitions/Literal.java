/*
	Code generation for the following grammar segment:
	
	Literal :=
			  IntegerLiteral()
			| FloatingPointLiteral()
			| CharacterLiteral()
			| StringLiteral()
			| BooleanLiteral()
			| NullLiteral()
			| TokenLiteral()
*/

package salsac.definitions;

import salsac.*;

public class Literal extends SimpleNode {
	public Literal(int id) 			{ super(id); }
	public Literal(SalsaParser p, int id)	{ super(p, id); }

	public String getType() {
		if (getChild(0) instanceof IntegerLiteral) return "int";
		else if (getChild(0) instanceof IntegerLiteral) return "int";
		else if (getChild(0) instanceof FloatingPointLiteral) return "double";
		else if (getChild(0) instanceof CharacterLiteral) return "char";
		else if (getChild(0) instanceof StringLiteral) return "String";
		else if (getChild(0) instanceof BooleanLiteral) return "boolean";
		else if (getChild(0) instanceof NullLiteral) return "null";
		else return "token";
	}
}
