package salsac;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;


public class DefinitionGenerator {
	public static void createFile(String className, String production) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("./definitions/" + className + ".java"));
			String output = "/*\n";
			output += "\t" + production + "\n";
			output += "*/\n";
			output += "package salsac.definitions;\n\n";

			output += "import salsac.SimpleNode;\n\n";

			output += "public class " + className + " extends SimpleNode {\n";
			output += "\tpublic " + className + "()\t{ super(); }\n";
			output += "\tpublic " + className + "(int id)\t{ super(id); }\n";
			output += "}";
			out.write(output);
			out.close();
		} catch (Exception e) {
			System.err.println("Error in file creation: " + e);
			e.printStackTrace();
		}
	}

	public static void main(String[] arguments) {
		String file = arguments[0];

		BufferedReader in;
		String line;

		try {
			in = new BufferedReader(new FileReader(file));
			while( (line = in.readLine()) != null ) {
				String className = "", production = "";

				int pos = line.indexOf(":") - 1;
				className = line.substring(0, pos);
				production = line;

				DefinitionGenerator.createFile(className, production);
			}
		} catch (Exception e) {
			System.err.println("Error in file reading: " + e);
			e.printStackTrace();
		}
	}
}
