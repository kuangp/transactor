/*
	Code generation for the following grammar segment:

	MessageSend :=
		[Value "<-"] <IDENTIFIER> MessageArguments [":" MessageProperty]
*/

package salsac.definitions;

import salsac.*;

public class MessageSend extends SimpleNode {

	public MessageSend(int id) 			{ super(id); }
	public MessageSend(SalsaParser p, int id)	{ super(p, id); }

	public boolean lastMessageInContinuation = false;


	public int getRequiredTokens() {
		int requiredTokens = 0;

		MessageArguments args = null;
		if (children[0] instanceof MessageArguments)	args = (MessageArguments)getChild(0);
		else 						args = (MessageArguments)getChild(1);

		int length = 0;
		if (args.children != null) length = args.children.length;
		for (int i = 0; i < length; i++) {
			String currentArgument = args.getChild(i).getJavaCode();

			if ( currentArgument.equals("token") ) {
				requiredTokens++;
			} else {
				String symbolType = SalsaCompiler.symbolTable.getSymbolType( currentArgument );

				if (symbolType != null && (symbolType.equals("next") || symbolType.equals("token")) ) {
					requiredTokens++;
				}
			}
		}

		return requiredTokens;
	}

	public String getArgumentsCode() {
		String code = "";
		if (children[0] instanceof MessageArguments) {
			code = "Object _arguments[] = { " + ((MessageArguments)getChild(0)).getMessageArguments("self") + " };\n";
		} else {
			code = "Object _arguments[] = { " + ((MessageArguments)getChild(1)).getMessageArguments(getChild(0).getJavaCode()) + " };\n";
		}
		return code;
	}

	public String getMessageSendCode() {
		//new Message(<sender>, <target>, <message name>, <arguments>, <input token>, <output token>);
		String code = "";

		code = "Message message = new Message( self, ";

		if (children[0] instanceof Value) {
			String methodName = getToken(1).image;
			code += getChild(0).getJavaCode() + ", ";

                        if (SalsaCompiler.symbolTable.getSymbolType(methodName) != null &&
			    SalsaCompiler.symbolTable.getSymbolType(methodName).equals("token") ) {
				code += methodName + "";
			} else {
				code += "\"" + methodName + "\"";
			}
		} else {
			String methodName = getToken(0).image;
			code += "self, ";

                        if (SalsaCompiler.symbolTable.getSymbolType(methodName) != null &&
			    SalsaCompiler.symbolTable.getSymbolType(methodName).equals("token") ) {
				code += methodName + "";
			} else {
				code += "\"" + methodName + "\"";
			}
		}
		code += ", _arguments, ";

		String input = SalsaCompiler.symbolTable.getContinuationInput();
		String output = SalsaCompiler.symbolTable.getContinuationOutput();
		String joinOutput = SalsaCompiler.symbolTable.getJoinBlockOutput();

		String outputType = SalsaCompiler.symbolTable.getSymbolType(output);

		if (!joinOutput.equals("null") && outputType != null && outputType.equals("token") ) {
			System.err.println("Named tokens are not allowed inside join blocks.");
			System.err.println("\tline: " + getToken(0).beginLine);
			System.exit(0);
		}

		if (output.equals("null")) {
			output = joinOutput;
		} else if (output.equals("currentContinuation")) {
			output = "currentMessage.getContinuationToken()";
		}

		if (joinOutput.equals("currentContinuation")) {
			joinOutput = "currentMessage.getContinuationToken()";
		}
                if (System.getProperty( "PassByReference" )!=null) {
                  code += input + ", " + output + ",true,false );\n";
                } else {
                  code += input + ", " + output + " );\n";
                }
		return code;
	}

	public String getProperty() {
		if (children.length == 3)							return getChild(2).getJavaCode();
		else if (children.length == 2 && getChild(0) instanceof MessageArguments)	return getChild(1).getJavaCode();

		return "";
	}

	public String getJavaCode() {
		String code = "";

		code += SalsaCompiler.getIndent() + "{\n";
		SalsaCompiler.indent++;
		code += SalsaCompiler.getIndent() + getArgumentsCode();
		code += SalsaCompiler.getIndent() + getMessageSendCode();
		code += getProperty();
		code += SalsaCompiler.getIndent() + "__messages.add( message );\n";

		SalsaCompiler.indent--;
		code += SalsaCompiler.getIndent() + "}\n";

		return code;
	}
}
