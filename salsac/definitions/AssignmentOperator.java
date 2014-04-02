/*
	Code generation for the following grammar segment:
	
	AssignmentOperator :=
		"=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|="
*/

package salsac.definitions;

import salsac.*;

public class AssignmentOperator extends SimpleNode {
	public AssignmentOperator(int id) 					{ super(id); }
	public AssignmentOperator(SalsaParser p, int id)	{ super(p, id); }
}