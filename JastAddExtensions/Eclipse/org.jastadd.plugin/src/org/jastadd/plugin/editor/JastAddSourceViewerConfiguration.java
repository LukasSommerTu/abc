package org.jastadd.plugin.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jastadd.plugin.model.JastAddModel;

/**
 * Connects various JastAdd features to the text editor.
 */
public class JastAddSourceViewerConfiguration extends SourceViewerConfiguration {
	
	private JastAddModel model;
	
	public JastAddSourceViewerConfiguration(JastAddModel model) {
		this.model = model;
	}
	
	/**
	 * Annotation hover showing marker messages in the vertical bar on the left
	 * in the editor
	 */
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover();
	}
	
	/**
	 * Text hover showing appearing when ever the mouse pointer hovers over some text.
	 */
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if(model != null)
			return model.getEditorConfiguration().getTextHover();
		return super.getTextHover(sourceViewer, contentType);
	}
	
	/**
	 * Provides syntax highlighting via the JastAddScanner and JastAddColors classes
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		if(model != null) {
			PresentationReconciler reconciler = new PresentationReconciler();
			ITokenScanner scanner = model.getEditorConfiguration().getScanner();
			if (scanner != null) {
				DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
				reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
				reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
			}
			return reconciler;
		}
		return super.getPresentationReconciler(sourceViewer);
	}
	
	/**
	 * Provides auto indentation via the JastAddAutoIndentStrategy class
	 */
	@Override 
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		if(model != null)
			return new IAutoEditStrategy[] { model.getEditorConfiguration().getAutoIndentStrategy() };
		return super.getAutoEditStrategies(sourceViewer, contentType);
	}
		
	/**
	 * Provides a content assistant providing name completion via the JastAddCompletionProcessor class
	 */
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if(model != null) {
			IContentAssistProcessor completionProcessor = model.getEditorConfiguration().getCompletionProcessor();
			if (completionProcessor != null) {
				ContentAssistant assistant= new ContentAssistant();
				assistant.enableAutoActivation(true);
				assistant.setAutoActivationDelay(500);
				assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
				assistant.setContentAssistProcessor(completionProcessor, IDocument.DEFAULT_CONTENT_TYPE);
				assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
				assistant.setContextInformationPopupBackground(new Color(null, 255, 255, 255));
				assistant.setProposalSelectorBackground(new Color(null, 255, 255, 255));
				assistant.setContextSelectorBackground(new Color(null, 255, 255, 255));	
				return assistant;
			}
		}
		return super.getContentAssistant(sourceViewer);
	}
	
	/**
	 * Provides a ControlCreator, used to create annotation hover controls 
	 */
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new JastAddTextPresenter(true));
			}
		};
	}
		
	/**
	 * Provides a reconciling strategy via the JastAddReconcilingStrategy class
	 */
	@Override 
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if(model != null) {
			JastAddReconcilingStrategy strategy = new JastAddReconcilingStrategy(model);
			MonoReconciler reconciler = new MonoReconciler(strategy, false);
			return reconciler;
		}
		return super.getReconciler(sourceViewer);
    }

	
	private class JastAddTextPresenter implements DefaultInformationControl.IInformationPresenterExtension, IInformationPresenter {
		
		private final String LINE_DELIM= System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		private int fCounter;
		private boolean fEnforceUpperLineLimit;

		public JastAddTextPresenter(boolean enforceUpperLineLimit) {
			super();
			fEnforceUpperLineLimit= enforceUpperLineLimit;
		}

		public JastAddTextPresenter() {
			this(true);
		}
		
		public String updatePresentation(Drawable drawable, String hoverInfo,
				TextPresentation presentation, int maxWidth, int maxHeight) {

			if (hoverInfo == null)
				return null;

			GC gc = new GC(drawable);
			try {

				StringBuffer buffer = new StringBuffer();
				int maxNumberOfLines = Math.round(maxHeight / gc.getFontMetrics().getHeight());

				fCounter = 0;
				BufferedReader reader = new BufferedReader(new StringReader(hoverInfo));

				//boolean lastLineFormatted = false;
				//String lastLineIndent = null;

				String line = reader.readLine();
				//boolean lineFormatted = reader.isFormattedLine();
				boolean firstLineProcessed = false;

				while (line != null) {

					if (fEnforceUpperLineLimit && maxNumberOfLines <= 0)
						break;

					if (firstLineProcessed) {
						/*
						if (!lastLineFormatted)
							append(buffer, LINE_DELIM, null);
						else {
							append(buffer, LINE_DELIM, presentation);
							if (lastLineIndent != null)
								append(buffer, lastLineIndent, presentation);
						}
						*/
						append(buffer, LINE_DELIM, presentation);
					}

					append(buffer, line, null);
					firstLineProcessed = true;

					/*
					lastLineFormatted= lineFormatted;
					if (!lineFormatted)
						lastLineIndent= null;
					else if (lastLineIndent == null)
						lastLineIndent= getIndent(line);
					*/

					line = reader.readLine();
					//lineFormatted= reader.isFormattedLine();
		

					maxNumberOfLines--;
				}

				/*
				if (line != null) {
					append(buffer, LINE_DELIM, lineFormatted ? presentation : null);
					append(buffer, HTMLMessages.getString("HTMLTextPresenter.ellipse"), presentation); //$NON-NLS-1$
				}
				*/

				return trim(buffer, presentation);

			} catch (IOException e) {

				// ignore TODO do something else?
				return null;

			} finally {
				gc.dispose();
			}
		}
				
		protected void adaptTextPresentation(TextPresentation presentation, int offset, int insertLength) {

			int yoursStart= offset;
			int yoursEnd=   offset + insertLength -1;
			yoursEnd= Math.max(yoursStart, yoursEnd);

			Iterator e= presentation.getAllStyleRangeIterator();
			while (e.hasNext()) {

				StyleRange range= (StyleRange) e.next();

				int myStart= range.start;
				int myEnd=   range.start + range.length -1;
				myEnd= Math.max(myStart, myEnd);

				if (myEnd < yoursStart)
					continue;

				if (myStart < yoursStart)
					range.length += insertLength;
				else
					range.start += insertLength;
			}
		}

		private void append(StringBuffer buffer, String string, TextPresentation presentation) {

			int length= string.length();
			buffer.append(string);

			if (presentation != null)
				adaptTextPresentation(presentation, fCounter, length);

			fCounter += length;
		}
		
		private String getIndent(String line) {
			int length= line.length();

			int i= 0;
			while (i < length && Character.isWhitespace(line.charAt(i))) ++i;

			return (i == length ? line : line.substring(0, i)) + " "; //$NON-NLS-1$
		}

		private String trim(StringBuffer buffer, TextPresentation presentation) {

			int length= buffer.length();

			int end= length -1;
			while (end >= 0 && Character.isWhitespace(buffer.charAt(end)))
				-- end;

			if (end == -1)
				return ""; //$NON-NLS-1$

			if (end < length -1)
				buffer.delete(end + 1, length);
			else
				end= length;

			int start= 0;
			while (start < end && Character.isWhitespace(buffer.charAt(start)))
				++ start;

			buffer.delete(0, start);
			presentation.setResultWindow(new Region(start, buffer.length()));
			return buffer.toString();
		}

		public String updatePresentation(Display display, String hoverInfo,
				TextPresentation presentation, int maxWidth, int maxHeight) {
			return updatePresentation((Drawable)display, hoverInfo, presentation, maxWidth, maxHeight);
		}

	}
}
