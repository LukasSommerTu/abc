package abc.weaving.aspectinfo;

import java.util.*;
import polyglot.util.Position;
import soot.*;
import abc.weaving.matching.*;
import abc.weaving.residues.*;


/** Cast from one pointcut variable to another. 
 *  This can appear after inlining
 *  @author Ganesh Sittampalam
 */
public class CastPointcutVar extends Pointcut {
    private Var from;
    private Var to;

    public CastPointcutVar(Var from,Var to,Position pos) {
	super(pos);
	this.from=from;
	this.to=to;
    }
    

    public Var getFrom() {
	return from;
    }

    public Var getTo() {
	return to;
    }

    public String toString() {
	return "cast("+from+","+to+")";
    }

    public Residue matchesAt(WeavingEnv we,
			     SootClass cls,
			     SootMethod method,
			     ShadowMatch sm) {
	Type fromType=we.getAbcType(from).getSootType();
	Type toType=we.getAbcType(to).getSootType();
	if(fromType instanceof PrimType && 
	   toType.equals(Scene.v().getSootClass("java.lang.Object").getType()))
	    return new Box(we.getWeavingVar(from),we.getWeavingVar(to));
	
	// no need to cast, because the rules guarantee this is an upcast...
	return new Copy(we.getWeavingVar(from),we.getWeavingVar(to));
    }

    protected Pointcut inline(Hashtable renameEnv,
			      Hashtable typeEnv,
			      Aspect context) {
	Var from=this.from;
	if(renameEnv.containsKey(from.getName()))
	   from=(Var) renameEnv.get(from.getName());

	Var to=this.to;
	if(renameEnv.containsKey(to.getName()))
	   to=(Var) renameEnv.get(to.getName());

	if(from != this.from || to != this.to)
	    return new CastPointcutVar(from,to,getPosition());
	else return this;
	   
    }
    public void registerSetupAdvice(Aspect context,Hashtable typeMap) {}
    public void getFreeVars(Set/*<String>*/ result) {
	result.add(to.getName());
    }

    public boolean equivalent(Pointcut otherpc) {
	if (otherpc instanceof CastPointcutVar) {
	    return (   (from.equals(((CastPointcutVar)otherpc).getFrom()))
		    && (  to.equals(((CastPointcutVar)otherpc).getTo())));
	} else return false;
    }

}
