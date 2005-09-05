/*
 * Created on Jun 18, 2005
 *
 */
package abc.om.visit;

import abc.om.AbcExtension;
import abc.om.ExtensionInfo;
import abc.om.ast.DummyAspectDecl_c;
import abc.om.ast.ModuleDecl;
import abc.om.ast.OpenModNodeFactory;
import abc.weaving.aspectinfo.AbcFactory;
import abc.weaving.aspectinfo.Aspect;
import abc.weaving.aspectinfo.GlobalAspectInfo;
import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * @author Neil Ongkingco
 *
 */
public class CollectModuleAspects extends ContextVisitor {
    ExtensionInfo ext = null;
    
    public CollectModuleAspects(Job job, TypeSystem ts, OpenModNodeFactory nf, ExtensionInfo ext) {
        super(job, ts, nf);
        this.ext = ext;
    }

    protected NodeVisitor enterCall(Node parent, Node n)
            throws SemanticException {
        //attaches an Aspect to a moduleNode, which is used in generating the
        //cflow counter initializations
        if (n instanceof ModuleDecl) {
            ModuleDecl decl = (ModuleDecl) n;
            DummyAspectDecl_c dummyAspectDecl = (DummyAspectDecl_c) parent;
            Aspect dummyAspect = ext.getAbcExtension().getGlobalAspectInfo().getAspect(
                    				AbcFactory.AbcClass(dummyAspectDecl.type())
                    				);
            AbcExtension.debPrintln(dummyAspect.toString());
            ModuleNodeModule module = (ModuleNodeModule)ext.moduleStruct.getNode(decl.name(), 
                    				ModuleNode.TYPE_MODULE);
            module.setDummyAspect(dummyAspect);
        }
        return super.enterCall(parent, n);
    }

}
