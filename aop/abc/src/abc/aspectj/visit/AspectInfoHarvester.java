
package abc.aspectj.visit;

import abc.aspectj.ast.*;
import abc.weaving.aspectinfo.*;

import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.frontend.*;

import java.util.*;

public class AspectInfoHarvester extends ContextVisitor {
    private static Map pc_decl_map = new HashMap();

    private GlobalAspectInfo gai;
    private String current_aspect_name;
    private Aspect current_aspect;

    public AspectInfoHarvester(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf);
	gai = GlobalAspectInfo.v();
    }

    public NodeVisitor enter(Node parent, Node n) {
	ParsedClassType scope = context().currentClassScope();
	if (scope != null) {
	    String clname = scope.fullName();
	    if (!clname.equals(current_aspect_name)) {
		current_aspect_name = clname;
		current_aspect = gai.getAspect(clname);
	    }
	}
	if (n instanceof ContainsAspectInfo) {
	    ((ContainsAspectInfo)n).update(gai, current_aspect);
	}
	//System.out.println(n.getClass());
	return super.enter(parent, n);
    }

    public static AbcType toAbcType(polyglot.types.Type t) {
	return new AbcType(soot.javaToJimple.Util.getSootType(t));
    }

    public static int convertModifiers(Flags flags) {
	return soot.javaToJimple.Util.getModifier(flags);
    }

    /** Convert a list of polyglot nodes representing argument patterns.
     *  @param nodes a list containing {@link polyglot.ast.Local}, {@link polyglot.types.TypeNode},
     *               {@link abc.aspectj.ast.ArgStar} and {@link abc.aspectj.ast.ArgDotDot} objects.
     *  @return a list of {@link abc.weaving.aspectinfo.ArgPattern} objects.
     */
    public static List/*<ArgPattern>*/ convertArgPatterns(List/*<Node>*/ nodes) {
	List aps = new ArrayList();
	Iterator ni = nodes.iterator();
	while (ni.hasNext()) {
	    Node n = (Node) ni.next();
	    abc.weaving.aspectinfo.ArgPattern ap;
	    if (n instanceof Local) {
		ap = new abc.weaving.aspectinfo.ArgVar(new Var(((Local)n).name(), n.position()), n.position());
	    } else if (n instanceof TypeNode) {
		ap = new abc.weaving.aspectinfo.ArgType(toAbcType(((TypeNode)n).type()), n.position());
	    } else if (n instanceof ArgStar) {
		ap = new abc.weaving.aspectinfo.ArgAny(n.position());
	    } else if (n instanceof ArgDotDot) {
		ap = new abc.weaving.aspectinfo.ArgFill(n.position());
	    } else {
		throw new RuntimeException("Unknown argument pattern type: "+n.getClass());
	    }
	    aps.add(ap);
	}
	return aps;
    }

    public static List/*<abc.weaving.aspectinfo.Formal>*/ convertFormals(List/*<polyglot.ast.Formal>*/ pformals) {
	List formals = new ArrayList();
	Iterator mdfi = pformals.iterator();
	while (mdfi.hasNext()) {
	    polyglot.ast.Formal mdf = (polyglot.ast.Formal)mdfi.next();
	    formals.add(new abc.weaving.aspectinfo.Formal(toAbcType((polyglot.types.Type)mdf.type().type()),
							  mdf.name(), mdf.position()));
	}
	return formals;
    }

    public static MethodSig makeMethodSig(MethodDecl md) {
	ReferenceType mcc = md.methodInstance().container();
	if (!(mcc instanceof ParsedClassType)) {
	    throw new RuntimeException("Error in aspect info generation: Method is not in a named class");
	}
	int mod = convertModifiers(md.flags());
	AbcClass cl = GlobalAspectInfo.v().getClass(((ParsedClassType)mcc).fullName());
	AbcType rtype = toAbcType(md.returnType().type());
	String name = md.name();
	List formals = convertFormals(md.formals());
	List exc = new ArrayList();
	Iterator ti = md.throwTypes().iterator();
	while (ti.hasNext()) {
	    TypeNode t = (TypeNode)ti.next();
	    exc.add(t.type().toString());
	}
	return new MethodSig(mod, cl, rtype, name, formals, exc, md.position());
    }
    
    
    public static MethodSig convertSig(MethodInstance mi) {
	List formals = new ArrayList();
	Iterator fi = mi.formalTypes().iterator(); 
	int index = 0;
	while (fi.hasNext()) {
	    Type ft = (Type)fi.next();
	    formals.add(new abc.weaving.aspectinfo.Formal(AspectInfoHarvester.toAbcType(ft),"a"+index, mi.position()));
	    index++;
	}
	List exc = new ArrayList();
	Iterator ti = mi.throwTypes().iterator();
	while (ti.hasNext()) {
	    Type t = (Type)ti.next();
	    exc.add(t.toString());
	}
	AbcClass container = GlobalAspectInfo.v().getClass(mi.container().toClass().toString());
	AbcType returnType = AspectInfoHarvester.toAbcType(mi.returnType());
	int mod = AspectInfoHarvester.convertModifiers(mi.flags());
	return new MethodSig (mod,container,returnType,mi.name(),formals,exc,mi.position());	
    }

    public static Map pointcutDeclarationMap() {
	return pc_decl_map;
    }
}
