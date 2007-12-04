
package AST;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.FileNotFoundException;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;

	
	// a local declaration is either a variable declaration or a parameter declaration
	
	public interface LocalDeclaration {
    // Declared in LocalDeclaration.jrag at line 6
 
		Access getTypeAccess();

    // Declared in LocalDeclaration.jrag at line 7

		String getID();

    // Declared in LocalDeclaration.jrag at line 8

		Block getBlock();

    // Declared in LocalDeclaration.jrag at line 9

		ParameterDeclaration asParameterDeclaration();

    // Declared in LocalDeclaration.jrag at line 10

		VariableDeclaration asVariableDeclaration();

    // Declared in Liveness.jrag at line 41
    public boolean mayDefBetween(Stmt begin, Stmt end);
    // Declared in Liveness.jrag at line 50
    public boolean accessedOutside(Stmt begin, Stmt end);
    // Declared in Liveness.jrag at line 53
    public boolean accessedBefore(Stmt stmt);
    // Declared in Liveness.jrag at line 66
    public boolean accessedAfter(Stmt stmt);
}
