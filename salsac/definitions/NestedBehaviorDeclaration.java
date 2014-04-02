/*
	Code generation for the following code segment:
	
	NestedBehaviorDeclaration := NestedBehaviorAttributes() BehaviorDeclaration()
*/

package salsac.definitions;

import salsac.*;

public class NestedBehaviorDeclaration extends SimpleNode {

	public NestedBehaviorDeclaration(int id) 					{ super(id); }
	public NestedBehaviorDeclaration(SalsaParser p, int id)	{ super(p, id); }

}