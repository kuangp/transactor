/*
	Code generation for the following grammar segment:
	
	InterfaceBody :=
	"{" 
		(
			| StateVariableDeclaration
			| MethodLookahead ";"
		)* 
	"}"
*/

package salsac.definitions;

import salsac.*;
import java.util.Vector;


public class InterfaceBody extends SimpleNode {
	
	public InterfaceBody(int id) 			{ super(id); }
	public InterfaceBody(SalsaParser p, int id)	{ super(p, id); }

	public String getPreCode()		{
		SalsaCompiler.indent++;
		String actorName = parent.getToken(1).image;

		String code = "{\n";
		/**
			Actually create the interface.
			If this actor extends another actor, have it extend that actors state
		*/
		String extendsName = ((InterfaceDeclaration)parent).extendsName;
		if ( extendsName.equals("ActorReference") ) {
			code += SalsaCompiler.getIndent() + "public interface State extends Actor {\n";
		} else {
			code += SalsaCompiler.getIndent() + "public interface State extends " + actorName + ".State {\n";
		}
		SalsaCompiler.indent++;

		return code;
	}

	public String getPostCode()	{
		SalsaCompiler.indent--;

		String code = SalsaCompiler.getIndent() + "}\n";
		code += "}";
	
		return code;
	}
	
	public String getChildCode() {
		String code = "";

		if (children != null) {		
			for (int i = 0; i < children.length; i++) {
				code += SalsaCompiler.getIndent() + getChild(i).getJavaCode();
				if (getChild(i) instanceof MethodLookahead) code += ";\n";
			}
		}
		
		return code;
	}
}
