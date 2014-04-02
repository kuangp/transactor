/*
	Code generation for the following grammar segment:
	
	ContinueStatement := "continue" [<IDENTIFIER>] ";"
*/

package salsac.definitions;

import salsac.*;

public class ContinueStatement extends SimpleNode {
	public ContinueStatement(int id) 					{ super(id); }
	public ContinueStatement(SalsaParser p, int id)	{ super(p, id); }

}