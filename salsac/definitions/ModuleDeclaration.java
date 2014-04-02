/*
	Code generation for the following grammar segment:
	
	ModuleDeclaration := "module" Name() ";"
*/

package salsac.definitions;

import salsac.*;

public class ModuleDeclaration extends SimpleNode {

	public ModuleDeclaration(int id)			{ super(id); }
	public ModuleDeclaration(SalsaParser p, int id)		{ super(p, id); }

	public String getModule() {
		return getChild(0).getJavaCode();
	}

	public String getJavaCode() {
		SalsaCompiler.addPackage( getChild(0).getJavaCode() );
		SalsaCompiler.moduleName = getChild(0).getJavaCode();

		return "package " + getChild(0).getJavaCode() + ";\n\n";
	}
}
