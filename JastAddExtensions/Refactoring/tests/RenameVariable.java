package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;
import AST.ASTNode;
import AST.FieldDeclaration;
import AST.LocalDeclaration;
import AST.ParameterDeclaration;
import AST.Program;
import AST.RefactoringException;
import AST.TypeDecl;
import AST.VariableDeclaration;

public abstract class RenameVariable extends TestCase {
	
	public RenameVariable(String arg0) {
		super(arg0);
	}
	
	public void runFieldRenameTest(String name) {
        String infile = getTestBase()+"/"+name+"/in/A.java";
        String resfile = getTestBase()+"/"+name+"/out/A.java";
        try {
        	BufferedReader br = new BufferedReader(new FileReader(infile));
        	String cmd = br.readLine();
        	assertTrue(cmd.matches("^// .*$"));
        	String[] fields = cmd.substring(3).split("\\s+");
        	Program prog;
        	if(fields.length == 3) {
        		prog = rename(fields[0], fields[1], fields[2]);
        	} else {
        		assertTrue(fields.length == 5);
        		prog = rename(fields[0], fields[1], fields[2], fields[3], fields[4]);
        	}
        	try {
        		File rf = new File(resfile);
        		FileReader rfr = new FileReader(rf);
        		long l = rf.length();
        		char[] buf = new char[(int)l];
        		rfr.read(buf);
        		assertEquals(new String(buf), prog.toString()+"\n");
        	} catch(FileNotFoundException fnfe) {
        		fail(name+" was supposed to fail but yielded result "+prog);
        	}
        } catch(IOException ioe) {
        	fail("unable to read from file");
        } catch(RefactoringException rfe) {
        	assertFalse(new File(resfile).exists());
        }
	}

	private Program rename(String file, String pkg, String tp, String fld, String newname) 
			throws RefactoringException {
		Iterator iter;
		Program prog = TestHelper.compile(file);
        assertNotNull(prog);
        String path[] = tp.split("\\.");
        TypeDecl d = (TypeDecl)prog.lookupType(pkg, path[0]);
        assertNotNull(d);
        for(int i=1;i<path.length;++i) {
        	iter = d.memberTypes(path[i]).iterator();
        	assertTrue(iter.hasNext());
            d = (TypeDecl)iter.next();
        }
        iter = d.localFields(fld).iterator();
        assertTrue(iter.hasNext());
        FieldDeclaration f = (FieldDeclaration)iter.next();
        f.rename(newname);
        return prog;
	}
	
	private Program rename(String file, String varname, String newname) 
			throws RefactoringException {
		Program prog = TestHelper.compile(file);
		assertNotNull(prog);
		LocalDeclaration v = findVariable(prog, varname);
		assertNotNull(v);
		if(v instanceof VariableDeclaration) {
			((VariableDeclaration)v).rename(newname);
		} else {
			assert(v instanceof ParameterDeclaration);
			((ParameterDeclaration)v).rename(newname);
		}
		return prog;
	}
	
	private LocalDeclaration findVariable(ASTNode n, String name) {
		if(n == null) return null;
		if(n instanceof LocalDeclaration &&
				((LocalDeclaration)n).getID().equals(name))
			return (LocalDeclaration)n;
		for(int i=0;i<n.getNumChild();++i) {
			LocalDeclaration v = findVariable(n.getChild(i), name);
			if(v != null) return v;
		}
		return null;
	}

	protected abstract String getTestBase();

}
