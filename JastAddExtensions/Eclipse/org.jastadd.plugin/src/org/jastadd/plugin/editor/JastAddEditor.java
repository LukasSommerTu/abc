package org.jastadd.plugin.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jastadd.plugin.editor.folding.JastAddEditorFolder;
import org.jastadd.plugin.editor.hover.JastAddSourceInformationControl;
import org.jastadd.plugin.model.JastAddModel;
import org.jastadd.plugin.model.JastAddModelProvider;
import org.jastadd.plugin.outline.JastAddContentOutlinePage;
import org.jastadd.plugin.resources.JastAddDocumentProvider;

/**
 * JastAdd editor providing various JastAdd related editor features
 */
public abstract class JastAddEditor extends TextEditor {
		
	private JastAddContentOutlinePage fOutlinePage;
	private ProjectionSupport projectionSupport;
	private JastAddEditorFolder folder;
	private IContextActivation contextActivation;
	
	private JastAddModel model;
	
	public JastAddModel getModel() {
		return this.model;
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput)input;
			IFile file = fileInput.getFile();
			model = JastAddModelProvider.getModel(file);
			setSourceViewerConfiguration(new JastAddSourceViewerConfiguration(model));		
		}
		super.doSetInput(input);
	}

	/** 
	 * Overriden method from TextEditor which adds a JastAdd specific SourceViewerConfiguration
	 * and DocumentProvider.
	 */
	@Override
	protected void initializeEditor() {
		super.initializeEditor();		
		setDocumentProvider(new JastAddDocumentProvider());
	}
	
	/**
	 * Overriden method from TextEditor which adds a JastAdd specific ContentOutline and
	 * BreakpointAdapter.
	 */
	@Override
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage =  new JastAddContentOutlinePage(this, model);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getEditorInput());
			}
			return fOutlinePage;
		} 
		return super.getAdapter(required);
	}
	
	/**
	 * Overriden method from AbstractDecoratedTextEditor. Adds projection support
	 * which provides folding in the editor. Activates the JastAdd editor context which activates
	 * commands and keybindings related to the context.
	 */
	@Override
	public void createPartControl(Composite parent) {
		setEditorContextMenuId(getEditorSite().getId());

		super.createPartControl(parent);
	    
	    ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
	    projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
	    if (model != null) {
	    	projectionSupport.addSummarizableAnnotationType(model.getEditorConfiguration().getErrorMarkerID()); //$NON-NLS-1$
	    	projectionSupport.addSummarizableAnnotationType(model.getEditorConfiguration().getWarningMarkerID()); //$NON-NLS-1$
	    }
	    projectionSupport.setHoverControlCreator(new JastAddControlCreator());
	    projectionSupport.install();
	    getSourceViewerConfiguration();
	    viewer.doOperation(ProjectionViewer.TOGGLE);
	    
	    folder = new JastAddEditorFolder(viewer.getProjectionAnnotationModel(), this);
	    if (model != null)
	    	model.addListener(folder);
	    
	    if (model != null) {
	    	IContextService contextService = (IContextService) getSite().getService(IContextService.class);
	    	contextActivation = contextService.activateContext(model.getEditorConfiguration().getEditorContextID());
	    }
	}
	
	/**
	 * Overriden method from TextEditor which removes listeners and contexts. 
	 */
	@Override 
	public void dispose() {
		super.dispose();
		
		IEditorInput input = getEditorInput();
		if(input instanceof IFileEditorInput && model != null) {
			IFileEditorInput fileInput = (IFileEditorInput)input;
			IFile file = fileInput.getFile();
			final JastAddModel model = JastAddModelProvider.getModel(file);
			if (model != null && this.model == model) {
				model.releaseFile(file);
			}
		}
		if (model != null)
			model.removeListener(folder);
	    IContextService contextService = (IContextService) getSite().getService(IContextService.class);
	    contextService.deactivateContext(contextActivation);
	}
	
	public void installInformationPresenter(InformationPresenter presenter) {
		presenter.install(getSourceViewer());
	}
	

	/**
	 * Overriden from AbstractDecoratedTextEditor. Adds a projection viewer which provides
	 * support for folding.
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}
	
	
	/**
	 * ControlCreator class used when creating the hover window for collapsed folding markers 
	 */
	private class JastAddControlCreator implements IInformationControlCreator {
	 	   public IInformationControl createInformationControl(Shell shell) {
	  	     return new JastAddSourceInformationControl(shell, model);
	  	   }
	}
	
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		
		if (model == null) return;
		
		model.getEditorConfiguration().populateContextMenu(menu, this);
	}
}
