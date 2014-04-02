/*
	Code generation for the following grammar segment:
	
	BreakStatement := "break" [<IDENTIFIER>] ";"
*/

package salsac.definitions;

import salsac.*;

public class BreakStatement extends SimpleNode {
	public BreakStatement(int id) 					{ super(id); }
	public BreakStatement(SalsaParser p, int id)	{ super(p, id); }
}