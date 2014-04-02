/*
	Code generation for the following grammar segment:
	
	Statement :=
		  Block()
		| LocalVariableDeclaration()
		| EmptyStatement()
		| StatementExpression()
		| LabeledStatement()
		| SwitchStatement()
		| IfStatement()
		| WhileStatement()
		| DoStatement()
		| ForStatement()
		| BreakStatement()
		| ContinueStatement()
		| ReturnStatement()
		| ThrowStatement()
		| SynchronizedStatement()
		| TryStatement()
		| NestedBehaviorDeclaration()
		| MethodDeclaration()
		| ContinuationStatement()
*/

package salsac.definitions;

import salsac.*;

public class Statement extends SimpleNode {

	public Statement(int id) 					{ super(id); }
	public Statement(SalsaParser p, int id)	{ super(p, id); }

	public String getPostCode() {
		if (getChild(0) instanceof LocalVariableDeclaration ||
		    getChild(0) instanceof StatementExpression) {
			return ";\n";
		}
		return "";
	}
}
