package abc.weaving.aspectinfo;

import soot.*;

import polyglot.util.Position;

import abc.weaving.matching.*;
import abc.weaving.residues.*;

/** Handler for <code>handler</code> shadow pointcut. */
public class Handler extends ShadowPointcut {
    private ClassnamePattern pattern;

    public Handler(ClassnamePattern pattern,Position pos) {
	super(pos);
	this.pattern = pattern;
    }

    public ClassnamePattern getPattern() {
	return pattern;
    }

    static private ShadowType shadowType=new TrapShadowType();
    static public void registerShadowType() {
	ShadowPointcut.registerShadowType(shadowType);
    }

    public ShadowType getShadowType() {
	return shadowType;
    }

    protected Residue matchesAt(MethodPosition position) {
	if(!(position instanceof TrapMethodPosition)) return null;
	Trap trap=((TrapMethodPosition) position).getTrap();

	// FIXME: Hack should be removed when patterns are added
	if(getPattern()==null) return AlwaysMatch.v;

	if(!getPattern().matchesClass(trap.getException())) return null;
	return AlwaysMatch.v;

    }

    public String toString() {
	return "handler("+pattern+")";
    }
}
