package salsac;

import java.util.Vector;

public class SymbolTable {

	public SymbolTable parent = null;
	private Vector symbols = new Vector();
	private int blockLevel;

	private Vector children = new Vector();


	private String continuationInput = null;
	private String continuationOutput = null;
	private String joinBlockOutput = null;
	

	public SymbolTable() {
		blockLevel = 0;
	}

	public SymbolTable(SymbolTable _parent) {
		parent = _parent;
		blockLevel = parent.getBlockLevel() + 1;

		if (parent != null) {
			parent.addChild(this);
		}
	}

	public void addChild(SymbolTable child) {
		children.add(child);
	}

	public int getBlockLevel() {
		return blockLevel;
	}

	public void addSymbol(String symbolName, String symbolType) {
		symbols.add( new Symbol(symbolName, symbolType) );
	}

	public boolean contains(String symbolName) {
		for (int i = 0; i < symbols.size(); i++) {
			if (symbolName.equals( ((Symbol)symbols.get(i)).name )) {
				return true;
			}
		}

		if (parent != null) {
			return parent.contains(symbolName);
		}
		
		return false;
	}
	
	public String getSymbolType(String symbolName) {
		for (int i = 0; i < symbols.size(); i++) {
			if (symbolName.equals( ((Symbol)symbols.get(i)).name )) {
				return ((Symbol)symbols.get(i)).type;
			}
		}

		if (parent != null) {
			return parent.getSymbolType(symbolName);
		}
		
		return null;
	}

	public String getJoinBlockOutput() {
		if (joinBlockOutput != null) return joinBlockOutput;
		else if (parent == null) return "null";
		else return parent.getJoinBlockOutput();
	}
	public String getContinuationOutput() {
		if (continuationOutput != null) return continuationOutput;
		else if (parent == null) return "null";
		else return parent.getContinuationOutput();
	}
	public String getContinuationInput() {
		if (continuationInput != null) return continuationInput;
		else if (parent == null) return "null";
		else return parent.getContinuationInput();
	}

	public void setJoinBlockOutput(String output) {
		joinBlockOutput = output;
	}
	public void setContinuationOutput(String output) {
		continuationOutput = output;
	}
	public void setContinuationInput(String input) {
		continuationInput = input;
	}
}
