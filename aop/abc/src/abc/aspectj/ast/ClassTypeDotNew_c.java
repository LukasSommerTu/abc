package abc.aspectj.ast;

import polyglot.ast.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import java.util.*;

import polyglot.ext.jl.ast.Node_c;

public class ClassTypeDotNew_c extends Node_c implements ClassTypeDotNew
{
    protected ClassnamePatternExpr base;
   
    public ClassTypeDotNew_c(Position pos, 
			      ClassnamePatternExpr base)  {
	super(pos);
        this.base = base;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	if (base != null) {
	    w.write("(");
	    print(base,w,tr);
	    w.write(").");
	}
	w.write("new");
    }

    public String toString() {
	if(base != null) return "("+base+")."+"new";
	else return "new";
    }

}
