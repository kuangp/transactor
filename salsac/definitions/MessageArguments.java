/*
	Code generation for the following grammar segment:
	
	MessageArguments := "(" [Expression ("," Expression)*] ")"
*/

package salsac.definitions;

import salsac.*;

public class MessageArguments extends SimpleNode {

	public MessageArguments(int id) 		{ super(id); }
	public MessageArguments(SalsaParser p, int id)	{ super(p, id); }


	public boolean isInteger(String s) {
		try {
			Integer i = new Integer(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean isDouble(String s) {
		try {
			Double d = new Double(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean hasNamedToken() {
		if (children == null) return false;

		for (int i = 0; i < children.length; i++) {
			String currentArgument = getChild(i).getJavaCode();
			if ( SalsaCompiler.symbolTable.getSymbolType(currentArgument) != null &&
			     SalsaCompiler.symbolTable.getSymbolType(currentArgument).equals("token") ) {
				return true;
			}
		}
		return false;
	}

	public boolean containsInput(String input) {
		if (children == null) return false;

		for (int i = 0; i < children.length; i++) {
			String currentArgument = getChild(i).getJavaCode();
			if ( currentArgument.equals(input) ) return true;
			if ( currentArgument.equals("token") && SalsaCompiler.symbolTable.getContinuationInput().equals(input)) return true;
		}
		return false;
	}

	public String getMessageArguments(String target) {
		String code = "";

		if (children != null) {		
			for (int i = 0; i < children.length; i++) {
				String currentArgument = getChild(i).getJavaCode();

				if ( currentArgument.equals("token") ) {
					code += SalsaCompiler.symbolTable.getContinuationInput();
				} else {
					if ( isInteger( currentArgument ) ) currentArgument = "new Integer(" + currentArgument + ")";
					else if ( isDouble( currentArgument ) ) currentArgument = "new Double(" + currentArgument + ")";

					code += currentArgument;
				}
			
				if (i != children.length-1) code += ", ";
			}
		}
		return code;
	}
}
