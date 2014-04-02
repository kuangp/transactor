/*
	Code generation for the following grammar segment:
	
	PrimarySuffix :=
			  "." "this"
			| "." AllocationExpression()
			| "[" Expression() "]"
			| "." <IDENTIFIER>
			| Arguments()
*/

package salsac.definitions;

import salsac.*;

public class PrimarySuffix extends SimpleNode {

	public PrimarySuffix(int id) 					{ super(id); }
	public PrimarySuffix(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		if (children == null) {
			return getTokenCode();
		}
		
		switch (getChild(0).id) {
			case SalsaParserTreeConstants.JJTEXPRESSION:
				return "[" + getChild(0).getJavaCode() + "]";

			case SalsaParserTreeConstants.JJTALLOCATIONEXPRESSION:
				return "." + getChild(0).getJavaCode();
			
			case SalsaParserTreeConstants.JJTARGUMENTS:
				return getChild(0).getJavaCode();
		}

		return "";
	}
}
