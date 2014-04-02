/*
	Code generation for the following grammar segment:

	Variable := ["(" Type ")"] (Literal | Name | "this" | "super" | AllocationExpression | "(" Expression ")")
*/


package salsac.definitions;

import salsac.*;

public class Variable extends SimpleNode {

	public Variable(int id) 			{ super(id); }
	public Variable(SalsaParser p, int id)		{ super(p, id); }

	public String getType() {
		if (children == null) {
			if (getToken(0).image.equals("this")) return SalsaCompiler.getActorName();
			else return SalsaCompiler.getExtendsName();
		} else {
			if (getChild(0) instanceof Type) return getChild(0).getJavaCode();
			else {
				if (getChild(0) instanceof Literal) return ((Literal)getChild(0)).getType();
				else if (getChild(0) instanceof Name) return SalsaCompiler.symbolTable.getSymbolType( getChild(0).getJavaCode() );
				else if (getChild(0) instanceof AllocationExpression) return ((AllocationExpression)getChild(0)).getType();
				else {
					//getChild(0) instanceof Expression
					return ((Expression)getChild(0)).getType();
				}
			}
		}
	}

	public String getJavaCode() {
		String code = "";
		int pos = 0;
		if (children != null) {
			if (getChild(0) instanceof Type) {
				code += "(" + getChild(0).getJavaCode() + ")";
				pos++;
			}
                        try {
                          if (getChild(pos)instanceof Expression)
                            code += "(" + getChild(pos).getJavaCode() + ")";
                          else
                            code += getChild(pos).getJavaCode();
                        }catch (Exception e) {
                          for (int i=2;i<this.tokens.length;i++) {
                            code+=getToken(i).image;
                          }
                          return code;
                        }
		} else {
			code = getToken(0).image;
		}

		return code;
	}

}
