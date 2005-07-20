package abc.tm.weaving.weaver;

import java.util.*;

import com.sun.rsasign.i;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import abc.soot.util.LocalGeneratorEx;
import abc.soot.util.UnUsedParams;
import abc.tm.weaving.aspectinfo.*;
import abc.tm.weaving.matching.TMStateMachine;
import abc.weaving.aspectinfo.Formal;


/**
 * Fills in method stubs for tracematch classes.
 * @author Pavel Avgustinov
 */
public class TraceMatchCodeGen {
    // TODO: Perhaps have a dedicated flag for tracematch codegen
    private static void debug(String message)
    { if (abc.main.Debug.v().aspectCodeGen)
        System.err.println("ACG*** " + message);
    }

    protected String getConstraintClassName(TraceMatch tm) {
        return "Constraint$" + tm.getName();
    }
    
    protected String getDisjunctClassName(TraceMatch tm) {
        return "Disjunct$" + tm.getName();
    }
    
    /**
     * Create the classes needed to keep constraints for a given tracematch. Classes are
     * the Constraint set of disjuncts and the disjunct class, plus any helper classes.
     * Could, at some point, specialise the constraints to each FSA state.
     * @param tm The relevant tracematch
     */
    protected void createConstraintClasses(TraceMatch tm) {
        // the SootClasses for the constraint and the main disjunct class for the tracematch 
        SootClass constraint = new SootClass(getConstraintClassName(tm));
        SootClass disjunct = new SootClass(getDisjunctClassName(tm));
        tm.setConstraintClass(constraint);
        
    }
    
    /**
     * Fills in the method stubs that have been generated for this tracematch.
     * @param tm the tracematch in question
     */
    protected void fillInAdviceBodies(TraceMatch tm) {
        
    }
    
    protected void prepareAdviceBody(SootMethod sm, List names, Collection unused) {
    	List paramTypes = new LinkedList();
    	List paramNames = new LinkedList();
    	Iterator ptIter = sm.getParameterTypes().iterator();
    	Iterator nameIter = names.iterator();
    	while (ptIter.hasNext()) {
    		Type pt = (Type) ptIter.next();
    		String name = (String) nameIter.next();
    		if (!unused.contains(name)){
    			paramTypes.add(pt); 
    			paramNames.add(name);
    		}
    	}
    	

    	
    	Body body = sm.getActiveBody();
    	Unit u = null;
    	Chain units = body.getUnits();
    	for (Iterator unitIter = units.iterator(); unitIter.hasNext(); ) {
    		u = (Unit) unitIter.next();
			if (u instanceof InvokeStmt) {
				InvokeStmt is = (InvokeStmt) u;
				InvokeExpr ie = is.getInvokeExpr();
				String name = ie.getMethodRef().name();
				if  (name.equals("proceed"))
					break;
			} else u = null;
    	}
    
    	if (u == null) return;
		
		SootClass scIter = Scene.v().getSootClass("java.util.Iterator");
		paramTypes.add(scIter.getType());
		sm.setParameterTypes(paramTypes);
		
    	Local iterLocal = body.getParameterLocal(paramTypes.size()-1);
    	LocalGeneratorEx lgen = new LocalGeneratorEx(body);
    	
    	// is there still a binding remaining?
    	Local hasNext = lgen.generateLocal(BooleanType.v(),"hasNext");
    	SootMethodRef smrHasNext = scIter.getMethod("hasNext",new LinkedList()).makeRef();
    	InvokeExpr e = Jimple.v().newVirtualInvokeExpr(iterLocal,smrHasNext,new LinkedList());
		AssignStmt ass = Jimple.v().newAssignStmt(hasNext,e);
		units.insertBefore(u,ass);
		u.redirectJumpsToThisTo(ass);
		
		// if not, proceed
		EqExpr ce = Jimple.v().newEqExpr(hasNext,IntConstant.v(0));
    	Stmt stmtIfHasNext = Jimple.v().newIfStmt(ce,u);
    	units.insertBefore(u,stmtIfHasNext);
    
    	// otherwise recursively call the body with another binding
        // FIXME: this is missing!

        // jump over the normal proceed
		Unit elsetarget = (Unit) units.getSuccOf(u);
		Stmt stmtJump = Jimple.v().newGotoStmt(elsetarget);
    	units.insertBefore(u,elsetarget);
    	
		System.out.println(body.toString());		
    }
    /**
     * Fills in the method stubs generated by the frontend for a given tracematch.
     * @param tm the tracecmatch to deal with.
     */
    public void fillInTraceMatch(TraceMatch tm) {
        TMStateMachine tmsm = (TMStateMachine)tm.getState_machine();

		Collection unused = UnUsedParams.unusedFormals(tm.getBodyMethod(),tm.getFormalNames());
        
        tmsm.prepareForMatching(tm.getSymbols(), tm.getFormalNames(), tm.getSym_to_vars(), 
                                                UnUsedParams.unusedFormals(tm.getBodyMethod(),tm.getFormalNames()),
                                                tm.getPosition());
        
        
        prepareAdviceBody(tm.getBodyMethod(),tm.getFormalNames(),unused);
        
        // Create the constraint class(es). A constraint is represented in DNF as a set of
        // disjuncts, which are conjuncts of positive or negative bindings. For now, we 
        // only create one kind of disjunct class for each tracematch, specialised to have
        // fields for the tracecmatch variables. A potential optimisation is to specialise
        // the disjunct class to each state, as negative bindings needn't be kept for all
        // states in general -- this may/will be done in time.
        createConstraintClasses(tm);
        
        // Fill in the advice bodies. The method stubs have been created by the frontend and
        // can be obtained from the TraceMatch object; code to keep track of changing 
        // constraints and to run the tracematch advice when appropriate, with the necessary
        // bindings, should be added.
        fillInAdviceBodies(tm);
    }
}
