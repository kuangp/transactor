/*
	Code generation for the following grammar segment:

	AllocationExpression :=
		  "new" PrimitiveType() ArrayDimsAndInits()
		| "new" Name() (ArrayDimsAndInits() | (Arguments() [BehaviorBody()])) [BindDeclaration]
*/

package salsac.definitions;

import salsac.*;

public class AllocationExpression extends SimpleNode {

	public AllocationExpression(int id) 			{ super(id); }
	public AllocationExpression(SalsaParser p, int id)	{ super(p, id); }

	public String getType() {
		return getChild(0).getJavaCode();
	}

	public String getJavaCode() {
          //if ((getChild(0) instanceof Name) && SalsaCompiler.isActor( getChild(0).getJavaCode() ) ) {
          boolean res=SalsaCompiler.isActor( getChild(0).getJavaCode());
            //System.out.println("testing:" + getChild(0).getJavaCode()+":"+res);
          //}
		if ((getChild(0) instanceof Name) && SalsaCompiler.isActor( getChild(0).getJavaCode() ) && (getChild(1) instanceof Arguments)) {
			String code = "((" + getChild(0).getJavaCode() + ")new " + getChild(0).getJavaCode() + "(";

			if (getChild( children.length-1 ) instanceof BindDeclaration) {
				BindDeclaration bindDeclaration = (BindDeclaration)getChild( children.length -1);
				code += bindDeclaration.getJavaCode() + ",";
			}
			code += "this).construct" + ((Arguments)getChild(1)).getJavaCode() + ")";
			return code;
		} else {
			return "new " + getChildCode();
		}
	}
}
