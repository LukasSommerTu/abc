package abc.aspectj.ast;

import polyglot.ast.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import java.util.*;

public class PCCall_c extends Pointcut_c implements PCCall
{
    protected MethodConstructorPattern pat;

    public PCCall_c(Position pos, MethodConstructorPattern pat)  {
	super(pos);
        this.pat = pat;
    }

    public Precedence precedence() {
	return Precedence.LITERAL;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("call(");
        print(pat, w, tr);
        w.write(")");
    }

    public abc.weaving.aspectinfo.Pointcut makeAIPointcut() {
	if (pat instanceof MethodPattern) {
	    return new abc.weaving.aspectinfo.ShadowPointcut
		(new abc.weaving.aspectinfo.MethodCall(((MethodPattern)pat).makeAIMethodPattern()),
		 position());
	} else if (pat instanceof ConstructorPattern) {
	    return new abc.weaving.aspectinfo.ShadowPointcut
		(new abc.weaving.aspectinfo.ConstructorCall(((ConstructorPattern)pat).makeAIConstructorPattern()),
		 position());
	} else {
	    throw new RuntimeException("Unexpected MethodConstructorPattern type in call pointcut: "+pat);
	}
    }
}
