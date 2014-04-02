/*
	Code generation for the following grammar segment:
	
	LabeledStatement := <IDENTIFIER> ":" Statement
*/

package salsac.definitions;

import salsac.*;

public class LabeledStatement extends SimpleNode {

	public LabeledStatement(int id) 					{ super(id); }
	public LabeledStatement(SalsaParser p, int id)	{ super(p, id); }

	public String getJavaCode() {
		return getToken(0).image + getToken(1).image + " " + getChild(0);
	}
}