/*
	Code generation for the following grammar segment:

	JoinBlock := "join" Block()
*/

package salsac.definitions;

import salsac.*;

public class JoinBlock extends SimpleNode {

	public JoinBlock(int id) 		{ super(id); }
	public JoinBlock(SalsaParser p, int id)	{ super(p, id); }

	public String getChildCode() {
		String joinName = SalsaCompiler.symbolTable.getContinuationOutput();
		String code = "";

		if (joinName.equals("currentContinuation")) joinName = "currentMessage.getContinuationToken()";

		code += SalsaCompiler.getIndent() + joinName + ".setJoinDirector();\n";

		if (getChild(0).children == null) {
			System.err.println("Syntax Error: line: " + getToken(0).beginLine);
			System.err.println("\tEmpty Join Blocks are not allowed.");
			System.exit(0);
		} else {
			for (int i = 0; i < getChild(0).children.length; i++) {
				code += getChild(0).getChild(i).getJavaCode();
			}
		}

		//code += SalsaCompiler.getIndent() + joinName + ".createJoinDirector();\n";
                code += SalsaCompiler.getIndent() + "addJoinToken(" + joinName + ");\n";

		return code;
	}
}
