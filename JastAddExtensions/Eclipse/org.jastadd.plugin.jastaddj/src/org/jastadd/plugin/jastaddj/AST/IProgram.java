package org.jastadd.plugin.jastaddj.AST;

import java.util.Collection;
import java.util.Iterator;

import org.jastadd.plugin.AST.IJastAddNode;
import org.jastadd.plugin.AST.IOutlineNode;

public interface IProgram extends IJastAddNode {
	Iterator compilationUnitIterator();

	Collection files();

	void flushSourceFiles(String fileName);
	
	public void addKeyValueOption(String name);	
	
	void addOptions(String[] options);

	void addSourceFile(String name);

	void addSourceFile(String fileName, String contents);

	IOutlineNode[] mainTypes();
	
	

}
