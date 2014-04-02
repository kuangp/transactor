/*
	Code generation for the following grammar segment:
	
	Arguments := "(" [Expression ("," Expression)*] ")"
*/

package salsac.definitions;

import salsac.*;

public class Arguments extends SimpleNode {

	public Arguments(int id) 			{ super(id); }
	public Arguments(SalsaParser p, int id)		{ super(p, id); }

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

	public String getPropertyCode() {
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


	public String getJavaCode() {
		String code = "";

		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				code += getChild(i).getJavaCode();
			
				if (i != children.length-1) code += ", ";
			}
		}
		return "(" + code + ")";
	}
}
