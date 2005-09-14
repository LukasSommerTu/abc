/* abc - The AspectBench Compiler
 * Copyright (C) 2005
 *
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This compiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
/*
 * Created on May 15, 2005
 *
 */
package abc.om.visit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import abc.aspectj.ast.ClassnamePatternExpr;
import abc.aspectj.ast.NamePattern;
import abc.aspectj.ast.PointcutDecl;
import abc.aspectj.types.AspectType_c;
import abc.aspectj.visit.PCNode;
import abc.aspectj.visit.PCStructure;
import abc.om.AbcExtension;
import abc.om.ExtensionInfo;
import abc.om.ast.SigMember;
import abc.polyglot.util.ErrorInfoFactory;
import abc.weaving.aspectinfo.AbcClass;
import abc.weaving.aspectinfo.AbcFactory;
import abc.weaving.aspectinfo.AbstractAdviceDecl;
import abc.weaving.aspectinfo.AndPointcut;
import abc.weaving.aspectinfo.Aspect;
import abc.weaving.aspectinfo.CflowSetup;
import abc.weaving.aspectinfo.ClassnamePattern;
import abc.weaving.aspectinfo.GlobalAspectInfo;
import abc.weaving.aspectinfo.OrPointcut;
import abc.weaving.aspectinfo.Per;
import abc.weaving.aspectinfo.Pointcut;
import abc.weaving.aspectinfo.Singleton;
import abc.weaving.matching.ConstructorCallShadowMatch;
import abc.weaving.matching.GetFieldShadowMatch;
import abc.weaving.matching.MethodCallShadowMatch;
import abc.weaving.matching.SetFieldShadowMatch;
import abc.weaving.matching.ShadowMatch;
import abc.weaving.matching.WeavingEnv;
import abc.weaving.residues.AndResidue;
import abc.weaving.residues.NeverMatch;
import abc.weaving.residues.OrResidue;
import abc.weaving.residues.Residue;
import abc.weaving.weaver.Weaver;

/**
 * @author Neil Ongkingco
 *  
 */
public class ModuleStructure {

    private Map moduleNodes;

    private Map aspectNodes;

    private Map classNodes;
    
    private ExtensionInfo ext;

    //pseudo-singleton, just so that OMMethodCall can access ModuleStructure
    // without knowing ext.
    //TODO: Remove this once matching is moved to AdviceApplication
    private static ModuleStructure instance;
    
    public ModuleStructure(ExtensionInfo ext) {
        moduleNodes = new HashMap();
        aspectNodes = new HashMap();
        classNodes = new HashMap();
        ModuleStructure.instance = this;
        this.ext = ext;
    }
    
    private Map getMap(int type) {
        switch (type) {
        case ModuleNode.TYPE_ASPECT:
            return aspectNodes;
        case ModuleNode.TYPE_CLASS:
            return classNodes;
        case ModuleNode.TYPE_MODULE:
            return moduleNodes;
        }
        return null;
    }

    public static ModuleStructure v() {
        return ModuleStructure.instance;
    }

    //only for modules
    public ModuleNode addModuleNode(String name, boolean isRoot) {
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        ModuleNode n = (ModuleNode) nodeMap.get(name);
        if (n != null) {
            return null;
        }
        n = new ModuleNodeModule(name, isRoot);
        nodeMap.put(n.name(), n);
        return n;
    }
    
    //for aspect members
    public ModuleNode addAspectNode(String name, NamePattern aspectNamePattern) {
        Map nodeMap = getMap(ModuleNode.TYPE_ASPECT);
        ModuleNode n = (ModuleNode) nodeMap.get(name);
        if (n!= null) {
            return null;
        }
        n = new ModuleNodeAspect(name, aspectNamePattern);
        nodeMap.put(n.name(), n);
        return n;
    }

    //for class members
    public ModuleNode addClassNode(String parentName, ClassnamePatternExpr cpe) {
        Map nodeMap = getMap(ModuleNode.TYPE_CLASS);
        ModuleNode n = new ModuleNodeClass(parentName, cpe);
        nodeMap.put(n.name(), n);
        return n;
    }

    /**
     * Adds a member to a module node. Returns the node of the member on
     * success, null on error (didn't want to make a new exception)
     */
    public ModuleNode addMember(String name, ModuleNode member) {
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        ModuleNode n = (ModuleNode) nodeMap.get(name);
        if (n == null) {
            return null;
        }

        if (member.getParent() != null) {
            return null;
        }
        member.setParent(n);
        ((ModuleNodeModule)n).addMember(member);
        return member;
    }

    /**
     * Adds a signature member to a module node. Returns the module on success,
     * null on error
     */
    public ModuleNode addSigMember(String name, SigMember sigMember) {
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        ModuleNode n = (ModuleNode) nodeMap.get(name);
        if (n == null) {
            return null;
        }

        ((ModuleNodeModule)n).addSigMember(sigMember);
        return n;
    }

    /**
     * Returns the node that matches the given name and type
     */
    public ModuleNode getNode(String name, int type) {
        Map nodeMap = getMap(type);
        return (ModuleNode) nodeMap.get(name);
    }

    /**
     * Returns the owner of an aspect.
     */
    public ModuleNode getOwner(String name, int type) {
        assert(type == ModuleNode.TYPE_ASPECT);
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        for (Iterator iter = nodeMap.values().iterator(); iter.hasNext();) {
            ModuleNode n = (ModuleNode) iter.next();
            if (n instanceof ModuleNodeModule) {
	            if (((ModuleNodeModule)n).containsMember(name, type)) {
	                return n;
	            }
            }
        }
        return null;
    }

    /**
     * Gets the owner of the class/aspect represented by node
     */
    public ModuleNode getOwner(PCNode node) {
        //iterate through all module nodes
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        for (Iterator iter = nodeMap.values().iterator(); iter.hasNext();) {
            ModuleNode n = (ModuleNode) iter.next();
            if (n.isModule() && ((ModuleNodeModule)n).containsMember(node)) {
                return n;
            }
        }
        return null;
    }

    public boolean hasMultipleOwners(PCNode node) {
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        boolean foundOnce = false;
        for (Iterator iter = nodeMap.values().iterator(); iter.hasNext();) {
            ModuleNode n = (ModuleNode) iter.next();
            if (n.isModule() && ((ModuleNodeModule)n).containsMember(node)) {
                if (foundOnce == false) {
                    foundOnce = true;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the modules other than the given module and its children. If node is
     * null, return all modules
     */
    public Collection getOtherModules(ModuleNode node) {
        Map nodeMap = getMap(ModuleNode.TYPE_MODULE);
        if (node != null && !node.isModule()) {
            throw new InternalCompilerError(
                    "Expecting a ModuleNode of type TYPE_MODULE");
        }
        List otherModules = new LinkedList();
        for (Iterator iter = nodeMap.values().iterator(); iter.hasNext();) {
            ModuleNode currNode = (ModuleNode) iter.next();
            //if currNode is a module, and does not have node as an ancestor,
            // include it
            if (currNode.isModule()) {
                ModuleNode parentNode = currNode;
                boolean found = false;
                while (parentNode.getParent() != null) {
                    if (parentNode == node) {
                        found = true;
                        break;
                    }
                    parentNode = parentNode.getParent();
                }
                if (!found) {
                    otherModules.add(currNode);
                }
            }
        }
        return otherModules;
    }

    //Returns true if the aspect and the class are in the same module path
    //i.e. if the aspect is in a module that is an ancestor of the class.
    //Also true if both the aspect and the class are not in modules.
    //Note that aspectNode can be null, meaning that the aspect is not in a
    //module
    public boolean isInSameModuleSet(ModuleNode aspectNode, PCNode classNode) {
        if (aspectNode != null && !aspectNode.isAspect()) {
            throw new InternalCompilerError(
                    "Expecting a ModuleNode of type TYPE_ASPECT");
        }
        ModuleNode classOwner = getOwner(classNode);

        //if the aspect is not in a module, and so is the class, then return
        // true
        if (aspectNode == null && classOwner == null) {
            return true;
        }
        //if the aspect is not in a module but the class is, return false
        if (aspectNode == null && classOwner != null) {
            return false;
        }

        ModuleNode aspectOwner = aspectNode.getParent();
        //if both unconstrained by modules, return true
        if (classOwner == null && aspectOwner == null) {
            return true;
        }
        //if the class is not in a module, but the aspect is, return true
        //TODO: This decision means that aspects in modules are _can_ access
        // classes
        //that are not in modules
        if (classOwner == null && aspectOwner != null) {
            return true;
        }
        //if the class is in a module an the aspect is not, return false\
        //this should already be handled by another case above
        if (classOwner != null && aspectOwner == null) {
            assert(false);
            return false;
        }
        //if both are in a module, see if the aspect belongs to a module that
        //is the same as the owner of the class or an ancestor thereof
        while (classOwner != null) {
            if (classOwner == aspectOwner) {
                return true;
            }
            classOwner = classOwner.getParent();
        }

        return false;
    }

    /**
     * Returns true of two classes if they belong to the same module set.
     * Classes in the same module set are considered to be in the same module
     * for the purposes of determining external calls. TODO: This is not as well
     * defined as in aspects. There are two possible conditions for two classes
     * to be in the same module set: (1) One is the root of the subtree to which
     * the other belongs or (2) they have a common ancestor.
     * 
     * Choosing 1 will lead to a less restrictive system (as the members of the
     * topmost modules will not necessarily be considered as internal to those
     * below. Choosing 2 will lead to a more restrictive system in that all
     * classes are considered internal as long as they are in the same subtree.
     * This does have the advantage of enforcing the signature guarantee of the
     * top modules (i.e. that only external matches to the joinpoints in the
     * signature are advised).
     * 
     * The implementation below is choice 2
     */
    public boolean isInSameModuleSet(PCNode node1, PCNode node2) {
        ModuleNode owner1 = getOwner(node1);
        ModuleNode owner2 = getOwner(node2);
        if (owner1 == null || owner2 == null) {
            return false;
        }
        //check for owner1
        ModuleNode root1 = owner1;
        while (root1.getParent() != null) {
            root1 = root1.getParent();
        }
        //check for owner2
        ModuleNode root2 = owner2;
        while (root2.getParent() != null) {
            root2 = root2.getParent();
        }
        return root1 == root2;
    }

    /**
     * Returns the module list of the given node. For a module, the module list is
     * the module itself and its ancestors, starting from the module itself. 
     * For aspects and classes, the module list is the module list of its parent.
     */
    public List getModuleList(ModuleNode n) {
        ArrayList ret = new ArrayList();
        if (n.isModule()) {
            ret.add(n);
        }
        while (n.getParent() != null) {
            n = n.getParent();
            ret.add(n);
        }
        return ret;
    }

    /**
     * Returns the pointcut that represents the signatures that apply to the
     * class
     *  
     */
    public Pointcut getApplicableSignature(PCNode classNode) {
        Pointcut ret = null;
        ModuleNodeModule owner = (ModuleNodeModule)getOwner(classNode);
        if (owner == null) {
            return ret;
        }
        
        //get the private signature for the owning module
        ret = owner.getPrivateSigAIPointcut();
        
        boolean prevIsConstrained = false;
        //get the non-private signatures from the modules in the modulelist
        List /* ModuleNode */moduleList = getModuleList(owner);
        for (Iterator iter = moduleList.iterator(); iter.hasNext();) {
            ModuleNodeModule module = (ModuleNodeModule) iter.next();
            if (prevIsConstrained) {
                ret = AndPointcut.construct(ret, module.getSigAIPointcut(),
                        AbcExtension.generated);
            } else {
                ret = OrPointcut.construct(ret, module.getSigAIPointcut(),
                        AbcExtension.generated);
            }
            prevIsConstrained = module.isConstrained();
        }

        return ret;
    }

    /**
     * Checks result of the match taking the effect of signatures into account 
     * 
     * @author Neil Ongkingco
     *  
     */
    public Residue openModMatchesAt(Pointcut pc, ShadowMatch sm,
            Aspect currAspect, WeavingEnv weaveEnv, SootClass cls,
            SootMethod method, AbstractAdviceDecl ad) throws SemanticException {

        Residue ret = pc.matchesAt(weaveEnv, cls, method, sm);

        //if openmod is not loaded, just return ret
        if (!AbcExtension.isLoaded()) {
            return ret;
        }
        //if it doesn't match, return immediately
        if (ret == NeverMatch.v()) {
            return ret;
        }
        //get the class the method belongs to
        //note: Used to be a method getOwningClass() of ShadowMatch+, 
        //but moved here to avoid contamination of the base code. And yes, it is ugly. 
        SootClass sootOwningClass = null;
        if (sm instanceof MethodCallShadowMatch) {
            sootOwningClass = ((MethodCallShadowMatch)sm).getMethodRef().declaringClass();
        } else if (sm instanceof ConstructorCallShadowMatch) {
            sootOwningClass = ((ConstructorCallShadowMatch)sm).getMethodRef().declaringClass();
        } else if (sm instanceof GetFieldShadowMatch) {
            sootOwningClass = ((GetFieldShadowMatch)sm).getFieldRef().declaringClass();
        } else if (sm instanceof SetFieldShadowMatch) {
            sootOwningClass = ((SetFieldShadowMatch)sm).getFieldRef().declaringClass();
        } else {
            sootOwningClass = sm.getContainer().getDeclaringClass();
        }
        
        PCNode owningClass = PCStructure.v().getClass(sootOwningClass);

        //get the class that contains this statement
        SootClass sootContainingClass = sm.getContainer().getDeclaringClass();
        PCNode containingClass = PCStructure.v().getClass(sootContainingClass);

        //debug
        AbcExtension.debPrintln("ModuleStructure.matchesAt: aspect "
                + currAspect.getName() + "; owning class " + owningClass.toString() 
                + "; containing class " + containingClass.toString() 
                + "; pc " + pc.toString());

        //if the aspect and the class belong to the same moduleset, return ret
        //i.e. it is matching in with an internal class/aspect, so signatures are
        //not applied
        ModuleStructure ms = ModuleStructure.v();
        ModuleNode aspectNode = ms.getNode(currAspect.getName(),
                ModuleNode.TYPE_ASPECT);
        if (ms.isInSameModuleSet(aspectNode, owningClass)) {
            return ret;
        } else {
            //else

            //check if any of the signatures match this shadow
            Pointcut sigPointcut = ms.getApplicableSignature(owningClass);
            Residue sigMatch;

            //if there are no matching signatures, return nevermatch
            if (sigPointcut == null) {
                return NeverMatch.v();
            }
            
            try {
                sigMatch = sigPointcut.matchesAt(weaveEnv, sm.getContainer()
                        .getDeclaringClass(), sm.getContainer(), sm);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Error matching signature pc",
                        e);
            }
            if (sigMatch != NeverMatch.v()) {
                Residue retResidue;
                //special case for cflowsetup, as cflow pointcuts should not
                //apply to the cflowsetups, otherwise the counter increment/decrement
                //would never be called
                if (ad instanceof CflowSetup) {
                    retResidue = ret; 
                } else {
                    retResidue = AndResidue.construct(sigMatch, ret);
                }
                AbcExtension.debPrintln("sigMatch = " + sigMatch);
                AbcExtension.debPrintln("ret = " + ret);
                AbcExtension.debPrintln("retResidue = " + retResidue);
                return retResidue;
            } else {
                AbcExtension.debPrintln(
                        "No matching signature in class " + 
                        containingClass + 
                        " of advice in aspect " + 
                        currAspect.getName());

                ModuleNode ownerModule = ms.getOwner(owningClass);
                String msg = "An advice in aspect " + 
                		currAspect.getName() + 
                		" would normally apply here, " +
                        "but does not match any of the signatures of module " +
                        ownerModule.name();

                addWarning(msg, sm);

                return NeverMatch.v();
            }
        }
    }
    
    private static void addWarning(String msg, ShadowMatch sm) {
        abc.main.Main.v().error_queue.enqueue(ErrorInfoFactory
                .newErrorInfo(ErrorInfo.WARNING, msg,
                        sm.getContainer(), sm.getHost()));
    }

    public Collection /* ModuleNodes */getModules() {
        return moduleNodes.values();
    }
    
    public void normalizeSigPointcuts() {
        for (Iterator iter = moduleNodes.values().iterator(); iter.hasNext(); ) {
            ModuleNode currNode = (ModuleNode) iter.next();
            if (currNode.isModule()) {
                ((ModuleNodeModule)currNode).normalizeSigPointcut();
            }
        }
    }
}