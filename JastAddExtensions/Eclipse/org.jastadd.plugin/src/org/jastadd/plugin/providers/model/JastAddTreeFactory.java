package org.jastadd.plugin.providers.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jastadd.plugin.AST.IOutlineNode;
import org.jastadd.plugin.AST.IJastAddNode;

public class JastAddTreeFactory {
	
	private Map<IJastAddNode,JastAddNode> map;
	
	public JastAddTreeFactory() {
		map = new HashMap<IJastAddNode,JastAddNode>();
	}
	
	public JastAddNode build(IJastAddNode astNode) {
		if(map.containsKey(astNode))
			return map.get(astNode);
		JastAddNode node = new JastAddNode(astNode);
		map.put(astNode, node);
		return node;
	}
	
	boolean hasNode(IJastAddNode astNode) {
		return map.containsKey(astNode);
	}
	
	public Collection<JastAddNode> buildTree(Collection<IJastAddNode> results) {
		Collection<JastAddNode> roots = new HashSet<JastAddNode>();
		for(IJastAddNode n : results) {
			IJastAddNode child = n;
			synchronized (child.treeLockObject()) {
				while(n.getParent() != null) {
					if(n.getParent() instanceof IOutlineNode && 
							((IOutlineNode)n.getParent()).showInContentOutline()) {
						boolean stop = hasNode(n.getParent());
						JastAddNode node = build(child);
						JastAddNode parent = build(n.getParent());
						parent.addChild(node);
						if(stop)
							break;
						child = n.getParent();
					}
					n = n.getParent();
				}
				if(n.getParent() == null)
					roots.add(build(child));
			}
		}
		return roots;
	}
}
