package salsac;

public class NodeTable {

	public NodeTable parent = null;
	public SimpleNode current = null;

	public NodeTable() {};
	public NodeTable(NodeTable _parent) {
		parent = _parent;
	}
}
