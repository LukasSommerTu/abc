package abc.aspectj.ast;

import polyglot.ast.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import java.util.*;

import abc.aspectj.visit.AspectInfoHarvester;

public class PCThis_c extends Pointcut_c implements PCThis
{
    protected Node pat; // AmbTypeOrLocal, becomes TypeNode, Local, or TPEUniversal

    public PCThis_c(Position pos, AmbTypeOrLocal pat)  {
	super(pos);
        this.pat = pat;
    }

    public Precedence precedence() {
	return Precedence.LITERAL;
    }
    
    
	/** Reconstruct the pointcut. */
	protected PCThis_c reconstruct(Node pat) {
	 if (pat != this.pat) {
		   PCThis_c n = (PCThis_c) copy();
		   n.pat = pat;
		   return n;
		}
		return this;
	}

		/** Visit the children of the pointcut. */
	public Node visitChildren(NodeVisitor v) {
		Node pat = (Node) visitChild(this.pat, v);
		return reconstruct(pat);
	}

	/** type check the use of  this */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
	   TypeSystem ts = tc.typeSystem();
	   Context c = tc.context();
	   
		if (pat instanceof TPEUniversal)
			return this;
		
		if (! (((Typed)pat).type() instanceof ReferenceType))
		   throw new SemanticException("Argument of \"this\" must be of reference type",pat.position());
		   
		return this;
	}
	
	public Collection mayBind() throws SemanticException {
		Collection result = new HashSet();
		if (pat instanceof Local) {
				String l = ((Local)pat).name();
				if (l == Pointcut_c.initialised)
							throw new SemanticException("cannot explicitly bind local \"" + l + "\"", pat.position());
				result.add(((Local)pat).name());
		}
		return result;
	}
   
	public Collection mustBind() {
		Collection result = new HashSet();
			if (pat instanceof Local)
				result.add(((Local)pat).name());
			 return result;
	}
 

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("this(");
        print(pat, w, tr);
        w.write(")");
    }
    
    public abc.weaving.aspectinfo.Pointcut makeAIPointcut() {
	if (pat instanceof Local) {
	    return new abc.weaving.aspectinfo.ConditionPointcut
		(new abc.weaving.aspectinfo.ThisVar
		 (new abc.weaving.aspectinfo.Var(((Local)pat).name(),((Local)pat).position())),
		 position());
	} else if (pat instanceof TypeNode) {
	    return new abc.weaving.aspectinfo.ConditionPointcut
		(new abc.weaving.aspectinfo.ThisType
		 (AspectInfoHarvester.toAbcType(((TypeNode)pat).type())),
		 position());
	} else if (pat instanceof TPEUniversal) {
	    return new abc.weaving.aspectinfo.ConditionPointcut
		(new abc.weaving.aspectinfo.ThisAny(),
		 position());
	} else {
	    throw new RuntimeException("Unexpected pattern in this pointcut: "+pat);
	}
    }
    

}
