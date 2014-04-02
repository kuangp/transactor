/*
	Code generation for the following grammar segment:

	MessageProperty := <IDENTIFIER> [Arguments]	
*/

package salsac.definitions;

import salsac.*;

public class MessageProperty extends SimpleNode {

	public MessageProperty(int id)				{ super(id); }
	public MessageProperty(SalsaParser p, int id)		{ super(p, id); }

	public String getJavaCode() {
		String code = "";

		code += SalsaCompiler.getIndent() + "Object[] _propertyInfo = { ";
		if (children != null) {
			Arguments arguments = (Arguments)getChild(0);
			code += arguments.getPropertyCode();
		}
		code += " };\n";

		code += SalsaCompiler.getIndent() + "message.setProperty( \"" + getToken(0).image + "\", _propertyInfo );\n";
		return code;
	}
}
