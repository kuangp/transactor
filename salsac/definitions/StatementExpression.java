/*
	Code generation for the following grammar segment:
	
	StatementExpression := Value [AssignmentOperator Expression]
*/

package salsac.definitions;

import salsac.*;

public class StatementExpression extends SimpleNode {

	public StatementExpression(int id) 			{ super(id); }
	public StatementExpression(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		String code = "";
		if (children.length == 1) {
			code = getChild(0).getJavaCode();
		} else {
			Value value = (Value)getChild(0);
			String valueType = value.getType();

			if (valueType != null && (valueType.equals("token") || valueType.equals("next"))) {
				//this is a token declaration;
				code += getChild(0).getJavaCode() + " ";

				if ( !getChild(1).getJavaCode().equals("=") ) {
					System.err.println("Syntax Error: line " + getChild(1).getToken(0).beginLine);
					System.err.println("\tA named token can only be reassigned with the \"=\" operator.");
					System.exit(0);
				} else {
					code += "= ";
				}

				String symbolName = getChild(2).getJavaCode();
				String symbolType = SalsaCompiler.symbolTable.getSymbolType( symbolName );

				if (symbolType != null && (symbolType.equals("token") || symbolType.equals("next")) ) {
					code += getChild(2).getJavaCode();
				} else {
					code += "new Token( \"" + getChild(0).getJavaCode() + "\", " + getChild(2).getJavaCode() + " )";
				}
			} else {
				code = getChild(0).getJavaCode() + " " + getChild(1).getJavaCode() + " " + getChild(2).getJavaCode();
			}
		}

		if ( ((SimpleNode)parent).parent instanceof Block) {
			return SalsaCompiler.getIndent() + code;
		} else {
			return code;
		}
	}
}
