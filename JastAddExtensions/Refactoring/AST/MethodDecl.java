
package AST;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.FileNotFoundException;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import sun.text.normalizer.UTF16;import changes.*;import main.FileRange;


public class MethodDecl extends MemberDecl implements Cloneable,  SimpleSet,  Iterator,  Methodoid,  Named {
    public void flushCache() {
        super.flushCache();
        accessibleFrom_TypeDecl_values = null;
        throwsException_TypeDecl_values = null;
        signature_computed = false;
        signature_value = null;
        moreSpecificThan_MethodDecl_values = null;
        overrides_MethodDecl_values = null;
        hides_MethodDecl_values = null;
        parameterDeclaration_String_values = null;
        type_computed = false;
        type_value = null;
        canOverrideOrHide_MethodDecl_values = null;
        overrides_computed = false;
        overrides_value = null;
        handlesException_TypeDecl_values = null;
        MethodDecl_definiteUses_visited = false;
        MethodDecl_definiteUses_computed = false;
        MethodDecl_definiteUses_value = null;
    MethodDecl_definiteUses_contributors = new java.util.HashSet();
        MethodDecl_uses_visited = false;
        MethodDecl_uses_computed = false;
        MethodDecl_uses_value = null;
    MethodDecl_uses_contributors = new java.util.HashSet();
        MethodDecl_overriders_visited = false;
        MethodDecl_overriders_computed = false;
        MethodDecl_overriders_value = null;
    MethodDecl_overriders_contributors = new java.util.HashSet();
    }
    public Object clone() throws CloneNotSupportedException {
        MethodDecl node = (MethodDecl)super.clone();
        node.accessibleFrom_TypeDecl_values = null;
        node.throwsException_TypeDecl_values = null;
        node.signature_computed = false;
        node.signature_value = null;
        node.moreSpecificThan_MethodDecl_values = null;
        node.overrides_MethodDecl_values = null;
        node.hides_MethodDecl_values = null;
        node.parameterDeclaration_String_values = null;
        node.type_computed = false;
        node.type_value = null;
        node.canOverrideOrHide_MethodDecl_values = null;
        node.overrides_computed = false;
        node.overrides_value = null;
        node.handlesException_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    public ASTNode copy() {
      try {
          MethodDecl node = (MethodDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
    public ASTNode fullCopy() {
        MethodDecl res = (MethodDecl)copy();
        for(int i = 0; i < getNumChild(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BoundNames.jrag at line 68


  public Access createBoundAccess(List args) {
    if(isStatic()) {
      return hostType().createQualifiedAccess().qualifiesAccess(
        new BoundMethodAccess(name(), args, this)
      );
    }
    return new BoundMethodAccess(name(), args, this);
  }

    // Declared in DataStructures.jrag at line 125

  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }

    // Declared in DataStructures.jrag at line 131

  private MethodDecl iterElem;

    // Declared in DataStructures.jrag at line 132

  public Iterator iterator() { iterElem = this; return this; }

    // Declared in DataStructures.jrag at line 133

  public boolean hasNext() { return iterElem != null; }

    // Declared in DataStructures.jrag at line 134

  public Object next() { Object o = iterElem; iterElem = null; return o; }

    // Declared in DataStructures.jrag at line 135

  public void remove() { throw new UnsupportedOperationException(); }

    // Declared in Modifiers.jrag at line 118

  
  // 8.4.3
  public void checkModifiers() {
    super.checkModifiers();
    if(hostType().isClassDecl()) {
      // 8.4.3.1
      if(isAbstract() && !hostType().isAbstract())
        error("class must be abstract to include abstract methods");
      // 8.4.3.1
      if(isAbstract() && isPrivate())
        error("method may not be abstract and private");
      // 8.4.3.1
      // 8.4.3.2
      if(isAbstract() && isStatic())
        error("method may not be abstract and static");
      if(isAbstract() && isSynchronized())
        error("method may not be abstract and synchronized");
      // 8.4.3.4
      if(isAbstract() && isNative())
        error("method may not be abstract and native");
      if(isAbstract() && isStrictfp())
        error("method may not be abstract and strictfp");
      if(isNative() && isStrictfp())
        error("method may not be native and strictfp");
    }
    if(hostType().isInterfaceDecl()) {
      // 9.4
      if(isStatic())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be static");
      if(isStrictfp())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be strictfp");
      if(isNative())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be native");
      if(isSynchronized())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be synchronized");
      if(isProtected())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be protected");
      if(isPrivate())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be private");
      else if(isFinal())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be final");
    }
  }

    // Declared in NameCheck.jrag at line 84


  public void nameCheck() {
    // 8.4
    // 8.4.2
    if(!hostType().methodsSignature(signature()).contains(this))
      error("method with signature " + signature() + " is multiply declared in type " + hostType().typeName());
    // 8.4.3.4
    if(isNative() && hasBlock())
      error("native methods must have an empty semicolon body");
    // 8.4.5
    if(isAbstract() && hasBlock())
      error("abstract methods must have an empty semicolon body");
    // 8.4.5
    if(!hasBlock() && !(isNative() || isAbstract()))
      error("only abstract and native methods may have an empty semicolon body");
  }

    // Declared in PrettyPrint.jadd at line 189


  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    getTypeAccess().toString(s);
    s.append(" " + name() + "(");
    if(getNumParameter() > 0) {
      getParameter(0).toString(s);
      for(int i = 1; i < getNumParameter(); i++) {
        s.append(", ");
        getParameter(i).toString(s);
      }
    }
    s.append(")");
    for(int i = 0; i < getNumEmptyBracket(); i++) {
      s.append("[]");
    }
    if(getNumException() > 0) {
      s.append(" throws ");
      getException(0).toString(s);
      for(int i = 1; i < getNumException(); i++) {
        s.append(", ");
        getException(i).toString(s);
      }
    }
    if(hasBlock()) {
      s.append(" ");
      getBlock().toString(s);
    }
    else {
      s.append(";\n");
    }
  }

    // Declared in TypeCheck.jrag at line 375


  public void typeCheck() {
    // Thrown vs super class method see MethodDecl.nameCheck
    // 8.4.4
    TypeDecl exceptionType = typeThrowable();
    for(int i = 0; i < getNumException(); i++) {
      TypeDecl typeDecl = getException(i).type();
      if(!typeDecl.instanceOf(exceptionType))
        error(signature() + " throws non throwable type " + typeDecl.fullName());
    }

    // check returns
    if(!isVoid() && hasBlock() && getBlock().canCompleteNormally())
      error("the body of a non void method may not complete normally");

  }

    // Declared in Methodoid.jadd at line 16

	public boolean hasBody() { return !(isAbstract() || isNative()); }

    // Declared in Names.jadd at line 20

	public void refined_Names_changeID(String id) { setID(id); }

    // Declared in RenameMethod.jrag at line 7

	
	public void cascadingRename(String new_name) throws RefactoringException {
		// first check which methods override this one/are overriden by this
		// obviously, we need to do that before we change its name
		// (obvious after I got tripped up by it, that is)
		SimpleSet overrides = overrides();
		java.util.Set overriders = overriders();
		// rename the method itself
		this.rename(new_name);
		// rename every method it overrides
		for(Iterator i = overrides.iterator();i.hasNext();)
			((MethodDecl)i.next()).rename(new_name);
		// rename every method it is overridden by
		for(Iterator i = overriders.iterator();i.hasNext();)
			((MethodDecl)i.next()).rename(new_name);
	}

    // Declared in RenameMethod.jrag at line 24


	// rename a method, don't care about overriding/overridden methods
	public void rename(String new_name) throws RefactoringException {
		if(getID().equals(new_name))
			return;
		String sig = signature();
		int idx = sig.indexOf('(');
		String new_sig = new_name + sig.substring(idx);
		// make sure there isn't already a method with the same signature in this type
		if(!hostType().localMethodsSignature(new_sig).isEmpty())
			throw new RefactoringException("couldn't rename: method signature clash");
		// if there are any like-named methods in superclasses, we must be able to override them
		for(Iterator i = hostType().ancestorMethods(new_sig).iterator();i.hasNext();) {
			MethodDecl md = (MethodDecl)i.next();
			if(!canOverrideOrHide(md))
				throw new RefactoringException("renamed method cannot override or hide "+md.hostType().typeName()+"."+md.signature());
		}
		AdjustmentTable table = find_uses(new_name);
		changeID(new_name);
		programRoot().clear();
		table.adjust();
	}

    // Declared in RenameMethod.jrag at line 45


	private AdjustmentTable find_uses(String new_name) {
		AdjustmentTable table = new AdjustmentTable();
		/* first, collect all uses of the method we are renaming */
		for(Iterator i = definiteUses().iterator(); i.hasNext();) {
			Access a = (Access)i.next();
			table.add(a);
		}
		/* now, collect all uses of methods that we might be shadowing after renaming */
		for(Iterator i = lookupMethod(new_name).iterator(); i.hasNext();) {
			MethodDecl md = (MethodDecl)i.next();
			for(Iterator j = md.definiteUses().iterator(); j.hasNext();) {
				Access acc = (Access)j.next();
				table.add(acc);
			}
		}
		return table;
	}

    // Declared in java.ast at line 3
    // Declared in java.ast line 89

    public MethodDecl() {
        super();

        setChild(null, 0);
        setChild(null, 1);
        setChild(new List(), 2);
        setChild(new List(), 3);
        setChild(new List(), 4);
        setChild(new Opt(), 5);

    }

    // Declared in java.ast at line 16


    // Declared in java.ast line 89
    public MethodDecl(Modifiers p0, Access p1, String p2, List p3, List p4, List p5, Opt p6) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setChild(p6, 5);
    }

    // Declared in java.ast at line 26


  protected int numChildren() {
    return 6;
  }

    // Declared in java.ast at line 29

  public boolean mayHaveRewrite() { return false; }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    public void setTypeAccess(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    private String tokenString_ID;

    // Declared in java.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 6

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    public void setParameterList(List list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    private int getNumParameter = 0;

    // Declared in java.ast at line 7

    public int getNumParameter() {
        return getParameterList().getNumChild();
    }

    // Declared in java.ast at line 11


    public ParameterDeclaration getParameter(int i) {
        return (ParameterDeclaration)getParameterList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addParameter(ParameterDeclaration node) {
        List list = getParameterList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setParameter(ParameterDeclaration node, int i) {
        List list = getParameterList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List getParameterList() {
        return (List)getChild(2);
    }

    // Declared in java.ast at line 28


    public List getParameterListNoTransform() {
        return (List)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    public void setEmptyBracketList(List list) {
        setChild(list, 3);
    }

    // Declared in java.ast at line 6


    private int getNumEmptyBracket = 0;

    // Declared in java.ast at line 7

    public int getNumEmptyBracket() {
        return getEmptyBracketList().getNumChild();
    }

    // Declared in java.ast at line 11


    public EmptyBracket getEmptyBracket(int i) {
        return (EmptyBracket)getEmptyBracketList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addEmptyBracket(EmptyBracket node) {
        List list = getEmptyBracketList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setEmptyBracket(EmptyBracket node, int i) {
        List list = getEmptyBracketList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List getEmptyBracketList() {
        return (List)getChild(3);
    }

    // Declared in java.ast at line 28


    public List getEmptyBracketListNoTransform() {
        return (List)getChildNoTransform(3);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    public void setExceptionList(List list) {
        setChild(list, 4);
    }

    // Declared in java.ast at line 6


    private int getNumException = 0;

    // Declared in java.ast at line 7

    public int getNumException() {
        return getExceptionList().getNumChild();
    }

    // Declared in java.ast at line 11


    public Access getException(int i) {
        return (Access)getExceptionList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addException(Access node) {
        List list = getExceptionList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setException(Access node, int i) {
        List list = getExceptionList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List getExceptionList() {
        return (List)getChild(4);
    }

    // Declared in java.ast at line 28


    public List getExceptionListNoTransform() {
        return (List)getChildNoTransform(4);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 89
    public void setBlockOpt(Opt opt) {
        setChild(opt, 5);
    }

    // Declared in java.ast at line 6


    public boolean hasBlock() {
        return getBlockOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


    public Block getBlock() {
        return (Block)getBlockOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setBlock(Block node) {
        getBlockOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

    public Opt getBlockOpt() {
        return (Opt)getChild(5);
    }

    // Declared in java.ast at line 21


    public Opt getBlockOptNoTransform() {
        return (Opt)getChildNoTransform(5);
    }

    // Declared in Undo.jadd at line 33

	  public void changeID(String id) {
		programRoot().pushUndo(new Rename(this, id));
		refined_Names_changeID(id);
	}

    protected java.util.Map accessibleFrom_TypeDecl_values;
    // Declared in AccessControl.jrag at line 68
    public boolean accessibleFrom(TypeDecl type) {
        Object _parameters = type;
if(accessibleFrom_TypeDecl_values == null) accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
        if(accessibleFrom_TypeDecl_values.containsKey(_parameters))
            return ((Boolean)accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
        if(isFinal && num == boundariesCrossed)
            accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
        return accessibleFrom_TypeDecl_value;
    }

    private boolean accessibleFrom_compute(TypeDecl type)  {
    if(isPublic()) {
      return true;
    }
    else if(isProtected()) {
      if(hostPackage().equals(type.hostPackage()))
        return true;
      if(type.withinBodyThatSubclasses(hostType()) != null)
        return true;
      return false;
    }
    else if(isPrivate())
      return hostType().topLevelType() == type.topLevelType();
    else
      return hostPackage().equals(type.hostPackage());
  }

    // Declared in DataStructures.jrag at line 123
    public int size() {
        int size_value = size_compute();
        return size_value;
    }

    private int size_compute() {  return  1;  }

    // Declared in DataStructures.jrag at line 124
    public boolean isEmpty() {
        boolean isEmpty_value = isEmpty_compute();
        return isEmpty_value;
    }

    private boolean isEmpty_compute() {  return  false;  }

    // Declared in DataStructures.jrag at line 128
    public boolean contains(Object o) {
        boolean contains_Object_value = contains_compute(o);
        return contains_Object_value;
    }

    private boolean contains_compute(Object o) {  return  this == o;  }

    // Declared in ErrorCheck.jrag at line 22
    public int lineNumber() {
        int lineNumber_value = lineNumber_compute();
        return lineNumber_value;
    }

    private int lineNumber_compute()  {
    for(int i = 0; i < getNumChild(); i++)
      if(getChild(i).getStart() != 0)
        return getChild(i).lineNumber();
    return super.lineNumber();
  }

    protected java.util.Map throwsException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 114
    public boolean throwsException(TypeDecl exceptionType) {
        Object _parameters = exceptionType;
if(throwsException_TypeDecl_values == null) throwsException_TypeDecl_values = new java.util.HashMap(4);
        if(throwsException_TypeDecl_values.containsKey(_parameters))
            return ((Boolean)throwsException_TypeDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean throwsException_TypeDecl_value = throwsException_compute(exceptionType);
        if(isFinal && num == boundariesCrossed)
            throwsException_TypeDecl_values.put(_parameters, Boolean.valueOf(throwsException_TypeDecl_value));
        return throwsException_TypeDecl_value;
    }

    private boolean throwsException_compute(TypeDecl exceptionType)  {
    for(int i = 0; i < getNumException(); i++)
      if(exceptionType.instanceOf(getException(i).type()))
        return true;
    return false;
  }

    // Declared in LookupMethod.jrag at line 111
    public String name() {
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return  getID();  }

    protected boolean signature_computed = false;
    protected String signature_value;
    // Declared in LookupMethod.jrag at line 114
    public String signature() {
        if(signature_computed)
            return signature_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        signature_value = signature_compute();
        if(isFinal && num == boundariesCrossed)
            signature_computed = true;
        return signature_value;
    }

    private String signature_compute()  {
    StringBuffer s = new StringBuffer();
    s.append(name() + "(");
    for(int i = 0; i < getNumParameter(); i++) {
      if(i != 0) s.append(", ");
      s.append(getParameter(i).type().typeName());
    }
    s.append(")");
    return s.toString();
  }

    // Declared in LookupMethod.jrag at line 126
    public boolean sameSignature(MethodDecl m) {
        boolean sameSignature_MethodDecl_value = sameSignature_compute(m);
        return sameSignature_MethodDecl_value;
    }

    private boolean sameSignature_compute(MethodDecl m) {  return  signature().equals(m.signature());  }

    protected java.util.Map moreSpecificThan_MethodDecl_values;
    // Declared in LookupMethod.jrag at line 128
    public boolean moreSpecificThan(MethodDecl m) {
        Object _parameters = m;
if(moreSpecificThan_MethodDecl_values == null) moreSpecificThan_MethodDecl_values = new java.util.HashMap(4);
        if(moreSpecificThan_MethodDecl_values.containsKey(_parameters))
            return ((Boolean)moreSpecificThan_MethodDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean moreSpecificThan_MethodDecl_value = moreSpecificThan_compute(m);
        if(isFinal && num == boundariesCrossed)
            moreSpecificThan_MethodDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_MethodDecl_value));
        return moreSpecificThan_MethodDecl_value;
    }

    private boolean moreSpecificThan_compute(MethodDecl m)  {
    if(getNumParameter() == 0)
      return false;
    for(int i = 0; i < getNumParameter(); i++) {
      if(!getParameter(i).type().instanceOf(m.getParameter(i).type()))
        return false;
    }
    return true;
  }

    protected java.util.Map overrides_MethodDecl_values;
    // Declared in LookupMethod.jrag at line 169
    public boolean overrides(MethodDecl m) {
        Object _parameters = m;
if(overrides_MethodDecl_values == null) overrides_MethodDecl_values = new java.util.HashMap(4);
        if(overrides_MethodDecl_values.containsKey(_parameters))
            return ((Boolean)overrides_MethodDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean overrides_MethodDecl_value = overrides_compute(m);
        if(isFinal && num == boundariesCrossed)
            overrides_MethodDecl_values.put(_parameters, Boolean.valueOf(overrides_MethodDecl_value));
        return overrides_MethodDecl_value;
    }

    private boolean overrides_compute(MethodDecl m) {  return 
    !isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && 
     hostType().instanceOf(m.hostType()) && m.signature().equals(signature());  }

    protected java.util.Map hides_MethodDecl_values;
    // Declared in LookupMethod.jrag at line 173
    public boolean hides(MethodDecl m) {
        Object _parameters = m;
if(hides_MethodDecl_values == null) hides_MethodDecl_values = new java.util.HashMap(4);
        if(hides_MethodDecl_values.containsKey(_parameters))
            return ((Boolean)hides_MethodDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean hides_MethodDecl_value = hides_compute(m);
        if(isFinal && num == boundariesCrossed)
            hides_MethodDecl_values.put(_parameters, Boolean.valueOf(hides_MethodDecl_value));
        return hides_MethodDecl_value;
    }

    private boolean hides_compute(MethodDecl m) {  return 
    isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && 
     hostType().instanceOf(m.hostType()) && m.signature().equals(signature());  }

    protected java.util.Map parameterDeclaration_String_values;
    // Declared in LookupVariable.jrag at line 102
    public SimpleSet parameterDeclaration(String name) {
        Object _parameters = name;
if(parameterDeclaration_String_values == null) parameterDeclaration_String_values = new java.util.HashMap(4);
        if(parameterDeclaration_String_values.containsKey(_parameters))
            return (SimpleSet)parameterDeclaration_String_values.get(_parameters);
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet parameterDeclaration_String_value = parameterDeclaration_compute(name);
        if(isFinal && num == boundariesCrossed)
            parameterDeclaration_String_values.put(_parameters, parameterDeclaration_String_value);
        return parameterDeclaration_String_value;
    }

    private SimpleSet parameterDeclaration_compute(String name)  {
    for(int i = 0; i < getNumParameter(); i++)
      if(getParameter(i).name().equals(name))
        return (ParameterDeclaration)getParameter(i);
    return SimpleSet.emptySet;
  }

    // Declared in Modifiers.jrag at line 204
    public boolean isSynthetic() {
        boolean isSynthetic_value = isSynthetic_compute();
        return isSynthetic_value;
    }

    private boolean isSynthetic_compute() {  return  getModifiers().isSynthetic();  }

    // Declared in Modifiers.jrag at line 209
    public boolean isPublic() {
        boolean isPublic_value = isPublic_compute();
        return isPublic_value;
    }

    private boolean isPublic_compute() {  return  getModifiers().isPublic() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 210
    public boolean isPrivate() {
        boolean isPrivate_value = isPrivate_compute();
        return isPrivate_value;
    }

    private boolean isPrivate_compute() {  return  getModifiers().isPrivate();  }

    // Declared in Modifiers.jrag at line 211
    public boolean isProtected() {
        boolean isProtected_value = isProtected_compute();
        return isProtected_value;
    }

    private boolean isProtected_compute() {  return  getModifiers().isProtected();  }

    // Declared in Modifiers.jrag at line 212
    public boolean isAbstract() {
        boolean isAbstract_value = isAbstract_compute();
        return isAbstract_value;
    }

    private boolean isAbstract_compute() {  return  getModifiers().isAbstract() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 213
    public boolean isStatic() {
        boolean isStatic_value = isStatic_compute();
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return  getModifiers().isStatic();  }

    // Declared in Modifiers.jrag at line 215
    public boolean isFinal() {
        boolean isFinal_value = isFinal_compute();
        return isFinal_value;
    }

    private boolean isFinal_compute() {  return  getModifiers().isFinal() || hostType().isFinal() || isPrivate();  }

    // Declared in Modifiers.jrag at line 216
    public boolean isSynchronized() {
        boolean isSynchronized_value = isSynchronized_compute();
        return isSynchronized_value;
    }

    private boolean isSynchronized_compute() {  return  getModifiers().isSynchronized();  }

    // Declared in Modifiers.jrag at line 217
    public boolean isNative() {
        boolean isNative_value = isNative_compute();
        return isNative_value;
    }

    private boolean isNative_compute() {  return  getModifiers().isNative();  }

    // Declared in Modifiers.jrag at line 218
    public boolean isStrictfp() {
        boolean isStrictfp_value = isStrictfp_compute();
        return isStrictfp_value;
    }

    private boolean isStrictfp_compute() {  return  getModifiers().isStrictfp();  }

    // Declared in PrettyPrint.jadd at line 949
    public String dumpString() {
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return  getClass().getName() + " [" + getID() + "]";  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 266
    public TypeDecl type() {
        if(type_computed)
            return type_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute()  {
    TypeDecl type = getTypeAccess().type();
    for(int i = 0; i < getNumEmptyBracket(); i++)
      type = type.arrayType();
    return type;
  }

    // Declared in TypeAnalysis.jrag at line 274
    public boolean isVoid() {
        boolean isVoid_value = isVoid_compute();
        return isVoid_value;
    }

    private boolean isVoid_compute() {  return  type().isVoid();  }

    // Declared in TypeHierarchyCheck.jrag at line 228
    public boolean mayOverrideReturn(MethodDecl m) {
        boolean mayOverrideReturn_MethodDecl_value = mayOverrideReturn_compute(m);
        return mayOverrideReturn_MethodDecl_value;
    }

    private boolean mayOverrideReturn_compute(MethodDecl m) {  return  type() == m.type();  }

    // Declared in ControlFlowGraph.jrag at line 331
    public boolean hasControlFlow() {
        boolean hasControlFlow_value = hasControlFlow_compute();
        return hasControlFlow_value;
    }

    private boolean hasControlFlow_compute() {  return  true;  }

    // Declared in ControlFlowGraph.jrag at line 337
    public Stmt exitBlock() {
        Stmt exitBlock_value = exitBlock_compute();
        return exitBlock_value;
    }

    private Stmt exitBlock_compute() {  return  getBlock();  }

    // Declared in ControlFlowGraph.jrag at line 343
    public Stmt entryBlock() {
        Stmt entryBlock_value = entryBlock_compute();
        return entryBlock_value;
    }

    private Stmt entryBlock_compute()  {
		Block block = getBlock();
		if (block.getNumStmt() > 0) {
			Stmt stmt = block.getStmt(0);
			assert(stmt.isEntryBlock());
			// Hacky solution to deal with first sets in the entryBlock 
			// e.g. ForStmt, DoStmt etc has a first node other than this
			Set first = stmt.first();
			for (Iterator itr = first.iterator(); itr.hasNext(); ) {
				return (Stmt)itr.next();
			}	
		}
		assert(block.isEntryBlock());
		return block;
	}

    protected java.util.Map canOverrideOrHide_MethodDecl_values;
    // Declared in Overriding.jrag at line 3
    public boolean canOverrideOrHide(MethodDecl md) {
        Object _parameters = md;
if(canOverrideOrHide_MethodDecl_values == null) canOverrideOrHide_MethodDecl_values = new java.util.HashMap(4);
        if(canOverrideOrHide_MethodDecl_values.containsKey(_parameters))
            return ((Boolean)canOverrideOrHide_MethodDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean canOverrideOrHide_MethodDecl_value = canOverrideOrHide_compute(md);
        if(isFinal && num == boundariesCrossed)
            canOverrideOrHide_MethodDecl_values.put(_parameters, Boolean.valueOf(canOverrideOrHide_MethodDecl_value));
        return canOverrideOrHide_MethodDecl_value;
    }

    private boolean canOverrideOrHide_compute(MethodDecl md)  {
		// this code is copy-n-pasted from TypeHierarchyCheck.jrag sans error messages
		if(!isStatic() && !md.isPrivate() && 
				md.accessibleFrom(hostType()) && hostType().instanceOf(md.hostType())) {
			if(md.isStatic())
				return false;
			if(!mayOverrideReturn(md))
				return false;
			for(int i = 0; i < getNumException(); i++) {
				Access e = getException(i);
				boolean found = false;
				for(int j = 0; !found && j < md.getNumException(); j++) {
					if(e.type().instanceOf(md.getException(j).type()))
						found = true;
				}
				if(!found && e.type().isUncheckedException())
					return false;
			}
			if(md.isPublic() && !isPublic() ||
		       md.isProtected() && !(isPublic() || isProtected()) ||
		       !md.isPrivate() && !md.isProtected() && !md.isPublic() && isPrivate())
				return false;
			if(md.isFinal())
				return false;
		} else if(isStatic() && !md.isPrivate() && 
				md.accessibleFrom(hostType()) && hostType().instanceOf(md.hostType())) {
			if(!md.isStatic())
				return false;
			if(type() != md.type())
				return false;
			for(int i = 0; i < getNumException(); i++) {
				Access e = getException(i);
				boolean found = false;
				for(int j = 0; !found && j < md.getNumException(); j++) {
					if(e.type().instanceOf(md.getException(j).type()))
						found = true;
				}
				if(!found)
					return false;
			}
			if(md.isPublic() && !isPublic() ||
			   md.isProtected() && !(isPublic() || isProtected()) ||
			   !md.isPrivate() && !md.isProtected() && !md.isPublic() && isPrivate())
				return false;
			if(md.isFinal())
				return false;
		}
		return true;
	}

    protected boolean overrides_computed = false;
    protected SimpleSet overrides_value;
    // Declared in Uses.jrag at line 63
    public SimpleSet overrides() {
        if(overrides_computed)
            return overrides_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        overrides_value = overrides_compute();
        if(isFinal && num == boundariesCrossed)
            overrides_computed = true;
        return overrides_value;
    }

    private SimpleSet overrides_compute()  {
		SimpleSet anc = hostType().ancestorMethods(signature());
		for(Iterator i=anc.iterator();i.hasNext();)
			if(!overrides((MethodDecl)i.next()))
				i.remove();
		return anc;
	}

    protected java.util.Map handlesException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 28
    public boolean handlesException(TypeDecl exceptionType) {
        Object _parameters = exceptionType;
if(handlesException_TypeDecl_values == null) handlesException_TypeDecl_values = new java.util.HashMap(4);
        if(handlesException_TypeDecl_values.containsKey(_parameters))
            return ((Boolean)handlesException_TypeDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
        if(isFinal && num == boundariesCrossed)
            handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
        return handlesException_TypeDecl_value;
    }

    // Declared in LookupMethod.jrag at line 5
    public MethodDecl unknownMethod() {
        MethodDecl unknownMethod_value = getParent().Define_MethodDecl_unknownMethod(this, null);
        return unknownMethod_value;
    }

    // Declared in Modifiers.jrag at line 258
    public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBePrivate(this, caller);
    }

    // Declared in LocalDeclaration.jrag at line 39
    public java.util.Set Define_java_util_Set_visibleLocalDecls(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
		HashSet decls = new HashSet();
		for(int i=0;i<getNumParameter();++i)
			decls.add(getParameter(i));
		return decls;
	}
        return getParent().Define_java_util_Set_visibleLocalDecls(this, caller);
    }

    // Declared in ExceptionHandling.jrag at line 111
    public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
        if(caller == getBlockOptNoTransform()) {
            return 
    throwsException(exceptionType) || handlesException(exceptionType);
        }
        return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }

    // Declared in Modifiers.jrag at line 261
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeFinal(this, caller);
    }

    // Declared in TypeAnalysis.jrag at line 584
    public BodyDecl Define_BodyDecl_hostBodyDecl(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return  this;
        }
        return getParent().Define_BodyDecl_hostBodyDecl(this, caller);
    }

    // Declared in Modifiers.jrag at line 256
    public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBePublic(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 70
    public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  true;
        }
        return getParent().Define_boolean_isMethodParameter(this, caller);
    }

    // Declared in Modifiers.jrag at line 257
    public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeProtected(this, caller);
    }

    // Declared in ControlFlowGraph.jrag at line 404
    public Set Define_Set_following(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return  Set.empty().union(getBlock().exitBlock());
        }
        return getParent().Define_Set_following(this, caller);
    }

    // Declared in Modifiers.jrag at line 263
    public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeNative(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 72
    public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  false;
        }
        return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 72
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getExceptionListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  NameType.TYPE_NAME;
        }
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  NameType.TYPE_NAME;
        }
        if(caller == getTypeAccessNoTransform()) {
            return  NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 133
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return  isStatic();
        }
        return getParent().Define_boolean_inStaticContext(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 874
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockOptNoTransform()) {
            return  v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? false : true;
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in VariableDeclaration.jrag at line 71
    public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  false;
        }
        return getParent().Define_boolean_isConstructorParameter(this, caller);
    }

    // Declared in Domination.jrag at line 61
    public Block Define_Block_getBlock(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  getBlock();
        }
        return getParent().Define_Block_getBlock(this, caller);
    }

    // Declared in AccessType.jrag at line 79
    public Access Define_Access_accessType(ASTNode caller, ASTNode child, TypeDecl td, boolean ambiguous) {
        if(caller == getBlockOptNoTransform()) {
		Access acc = accessType(td, ambiguous);
		if(acc != null && !parameterDeclaration(td.getID()).isEmpty()) {
			if(acc instanceof AbstractDot) {
				Expr left = ((AbstractDot)acc).getLeft();
				if(left.isPackageAccess()) {
					Access pkgacc = accessPackage(((PackageAccess)left).getPackage());
					if(pkgacc == null) return null;
					return pkgacc.qualifiesAccess(((AbstractDot)acc).getRight());
				} else if(left.isTypeAccess()) {
					Access tacc = accessType(((TypeAccess)left).decl(), ambiguous);
					if(tacc == null) return null;
					return tacc.qualifiesAccess(((AbstractDot)acc).getRight());
				} else {
					assert(false);
				}
			} else {
				if(td.isNestedType() && !td.isLocalClass()) {
					TypeDecl enc = td.enclosingType();
					Access encacc = getBlock().accessType(enc, ambiguous);
					if(encacc == null) return null;
					Access innacc = enc.getBodyDecl(0).accessType(td, ambiguous);
					if(acc == null) return null;
					return encacc.qualifiesAccess(acc);
				} else if(!td.packageName().equals("") && accessPackage(td.packageName()) != null) {
					return accessPackage(td.packageName()).qualifiesAccess(acc);
				} else {
					return null;
				}
			}
		} else {
			return acc;
		}
	}
        return getParent().Define_Access_accessType(this, caller, td, ambiguous);
    }

    // Declared in LookupVariable.jrag at line 47
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return  parameterDeclaration(name);
        }
        if(caller == getBlockOptNoTransform()) {
    SimpleSet set = parameterDeclaration(name);
    // A declaration of a method parameter name shadows any other variable declarations
    if(!set.isEmpty()) return set;
    // Delegate to other declarations in scope
    return lookupVariable(name);
  }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in Modifiers.jrag at line 264
    public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeStrictfp(this, caller);
    }

    // Declared in Modifiers.jrag at line 259
    public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeAbstract(this, caller);
    }

    // Declared in AccessField.jrag at line 156
    public Access Define_Access_accessField(ASTNode caller, ASTNode child, FieldDeclaration fd) {
        if(caller == getBlockOptNoTransform()) {
		Access acc = accessField(fd);
		if(acc != null) {
			if(!parameterDeclaration(fd.getID()).isEmpty()) {
				// if acc is already qualified, nothing needs to change
				if(acc instanceof AbstractDot)
					return acc;
				else
					return new ThisAccess("this").qualifiesAccess(acc);
			} else {
				return acc;
			}
		}
		return null;
	}
        return getParent().Define_Access_accessField(this, caller, fd);
    }

    // Declared in UnreachableStatements.jrag at line 24
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 434
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockOptNoTransform()) {
            return  v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? true : isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in RenameParameter.jrag at line 10
    public RefactoringException Define_RefactoringException_canRenameTo(ASTNode caller, ASTNode child, String new_name) {
        if(caller == getParameterListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
 {
		if(!parameterDeclaration(new_name).isEmpty())
			return new RefactoringException("parameter of the same name exists");
		if(this.hasBody())
			return getBlock().acceptLocal(new_name);
		return null;
	}
}
        return getParent().Define_RefactoringException_canRenameTo(this, caller, new_name);
    }

    // Declared in TypeCheck.jrag at line 394
    public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return  type();
        }
        return getParent().Define_TypeDecl_returnType(this, caller);
    }

    // Declared in Modifiers.jrag at line 262
    public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeSynchronized(this, caller);
    }

    // Declared in NameCheck.jrag at line 229
    public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return  this;
        }
        return getParent().Define_ASTNode_enclosingBlock(this, caller);
    }

    // Declared in Modifiers.jrag at line 260
    public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return  true;
        }
        return getParent().Define_boolean_mayBeStatic(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

    protected boolean MethodDecl_definiteUses_visited = false;
    protected boolean MethodDecl_definiteUses_computed = false;
    protected HashSet MethodDecl_definiteUses_value;
    // Declared in Uses.jrag at line 44
    public HashSet definiteUses() {
        if(MethodDecl_definiteUses_computed)
            return MethodDecl_definiteUses_value;
        if(MethodDecl_definiteUses_visited)
            throw new RuntimeException("Circular definition of attr: definiteUses in class: ");
        MethodDecl_definiteUses_visited = true;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        MethodDecl_definiteUses_value = definiteUses_compute();
        if(isFinal && num == boundariesCrossed)
            MethodDecl_definiteUses_computed = true;
        MethodDecl_definiteUses_visited = false;
        return MethodDecl_definiteUses_value;
    }

    java.util.HashSet MethodDecl_definiteUses_contributors = new java.util.HashSet();
    private HashSet definiteUses_compute() {
        ASTNode node = this;
        while(node.getParent() != null)
            node = node.getParent();
        Program root = (Program)node;
        root.collect_contributors_MethodDecl_definiteUses();
        MethodDecl_definiteUses_value = new HashSet();
        for(java.util.Iterator iter = MethodDecl_definiteUses_contributors.iterator(); iter.hasNext(); ) {
            ASTNode contributor = (ASTNode)iter.next();
            contributor.contributeTo_MethodDecl_MethodDecl_definiteUses(MethodDecl_definiteUses_value);
        }
        return MethodDecl_definiteUses_value;
    }

    protected boolean MethodDecl_uses_visited = false;
    protected boolean MethodDecl_uses_computed = false;
    protected HashSet MethodDecl_uses_value;
    // Declared in Uses.jrag at line 48
    public HashSet uses() {
        if(MethodDecl_uses_computed)
            return MethodDecl_uses_value;
        if(MethodDecl_uses_visited)
            throw new RuntimeException("Circular definition of attr: uses in class: ");
        MethodDecl_uses_visited = true;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        MethodDecl_uses_value = uses_compute();
        if(isFinal && num == boundariesCrossed)
            MethodDecl_uses_computed = true;
        MethodDecl_uses_visited = false;
        return MethodDecl_uses_value;
    }

    java.util.HashSet MethodDecl_uses_contributors = new java.util.HashSet();
    private HashSet uses_compute() {
        ASTNode node = this;
        while(node.getParent() != null)
            node = node.getParent();
        Program root = (Program)node;
        root.collect_contributors_MethodDecl_uses();
        MethodDecl_uses_value = new HashSet();
        for(java.util.Iterator iter = MethodDecl_uses_contributors.iterator(); iter.hasNext(); ) {
            ASTNode contributor = (ASTNode)iter.next();
            contributor.contributeTo_MethodDecl_MethodDecl_uses(MethodDecl_uses_value);
        }
        return MethodDecl_uses_value;
    }

    protected boolean MethodDecl_overriders_visited = false;
    protected boolean MethodDecl_overriders_computed = false;
    protected HashSet MethodDecl_overriders_value;
    // Declared in Uses.jrag at line 60
    public HashSet overriders() {
        if(MethodDecl_overriders_computed)
            return MethodDecl_overriders_value;
        if(MethodDecl_overriders_visited)
            throw new RuntimeException("Circular definition of attr: overriders in class: ");
        MethodDecl_overriders_visited = true;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        MethodDecl_overriders_value = overriders_compute();
        if(isFinal && num == boundariesCrossed)
            MethodDecl_overriders_computed = true;
        MethodDecl_overriders_visited = false;
        return MethodDecl_overriders_value;
    }

    java.util.HashSet MethodDecl_overriders_contributors = new java.util.HashSet();
    private HashSet overriders_compute() {
        ASTNode node = this;
        while(node.getParent() != null)
            node = node.getParent();
        Program root = (Program)node;
        root.collect_contributors_MethodDecl_overriders();
        MethodDecl_overriders_value = new HashSet();
        for(java.util.Iterator iter = MethodDecl_overriders_contributors.iterator(); iter.hasNext(); ) {
            ASTNode contributor = (ASTNode)iter.next();
            contributor.contributeTo_MethodDecl_MethodDecl_overriders(MethodDecl_overriders_value);
        }
        return MethodDecl_overriders_value;
    }

    protected void collect_contributors_MethodDecl_uses() {
        // Declared in Uses.jrag at line 58
        for(Iterator iter = (overriders()).iterator(); iter.hasNext(); ) {
            MethodDecl ref = (MethodDecl)iter.next();
            if(ref != null)
            ref.MethodDecl_uses_contributors.add(this);
        }
        super.collect_contributors_MethodDecl_uses();
    }
    protected void collect_contributors_MethodDecl_overriders() {
        // Declared in Uses.jrag at line 61
        for(Iterator iter = (overrides()).iterator(); iter.hasNext(); ) {
            MethodDecl ref = (MethodDecl)iter.next();
            if(ref != null)
            ref.MethodDecl_overriders_contributors.add(this);
        }
        super.collect_contributors_MethodDecl_overriders();
    }
    protected void contributeTo_MethodDecl_MethodDecl_overriders(HashSet collection) {
        collection.add(this);
    }

    protected void contributeTo_MethodDecl_MethodDecl_uses(HashSet collection) {
        collection.addAll(uses());
    }

}
