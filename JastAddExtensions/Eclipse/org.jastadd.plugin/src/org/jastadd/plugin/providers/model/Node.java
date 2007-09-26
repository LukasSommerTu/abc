package org.jastadd.plugin.providers.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.jastadd.plugin.AST.IOutlineNode;
import org.jastadd.plugin.AST.IJastAddNode;

public class Node {
	
	private Set<Node> children;
	private Node parent;
	private IJastAddNode node;
	
	public Node(IJastAddNode node) {
		children = new HashSet<Node>();
		this.node = node;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public Collection getChildren() {
		return children;
	}
	
	public Node addChild(Node node) {
		children.add(node);
		node.parent = this;
		return this;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public String getLabel() {
		if (node instanceof IOutlineNode)
			return ((IOutlineNode)node).contentOutlineLabel();
		return node.getClass().toString();
	}
	
	public Image getImage() {
		if (node instanceof IOutlineNode)
			return ((IOutlineNode)node).contentOutlineImage();
		return null;
	}
	
	public IJastAddNode getJastAddNode() {
		return node;
	}
	
}