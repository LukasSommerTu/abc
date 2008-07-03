package org.jastadd.plugin.jastadd.debugger.attributes;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.jastadd.plugin.jastadd.Activator;
import org.jastadd.plugin.jastadd.generated.AST.AttributeDecl;
import org.jastadd.plugin.jastadd.generated.AST.ParameterDeclaration;

/**
 * Represents an attribute with its value, if computed.
 * 
 * The state represents the state of the variable.
 * @author luke
 *
 */
public class AttributeEvaluationNode implements AttributeNode {

	public enum AttributeState {
		/**
		 * A value is already held for this attribute, but it can be recalculated.
		 */
		CALCULATED,
		/**
		 * This attribute is currently being evaluating somewhere, and therefore cannot
		 * be displayed or calculated.
		 */
		BEING_CALCULATED,
		/**
		 * This attribute has no value, but can be evaluated to calculate one.
		 */
		EMPTY,
		/**
		 * This attribute has a pre-calculated value, which cannot be changed due to
		 * non-primitive arguments.
		 */
		PRE_CALCULATED,
		/**
		 * This attribute has no value, and cannot be evaluated to calculate one due to
		 * non-primitive arguments.
		 */
		NOT_CALCULABLE
	}
	
	private AttributeDecl attribute;
	private IJavaValue parent;
	private AttributeState state = AttributeState.EMPTY;
	private IJavaValue result;
	private IJavaThread thread;
	private Shell shell;

	public AttributeEvaluationNode(IJavaValue parentValue, AttributeDecl attribute, IJavaThread thread, Shell shell) {
		this.attribute = attribute;
		this.parent = parentValue;
		this.thread = thread;

		// We want to discover the initial state of the attribute
		try {
			if (alreadyRunning(parent)) {
				state = AttributeState.BEING_CALCULATED;
			} else {
				// Scan the local variables of the parent, to see if this attribute has been pre-calculated
				for (IVariable child : parent.getVariables()) {
					if (child.getName().startsWith(attribute.name() + "$computed")) {
						state = child.getValue().getValueString().equals("true") ? AttributeState.CALCULATED : AttributeState.EMPTY;
					}
					if (child.getName().startsWith(attribute.name() + "$value")) {
						result = ((IJavaValue) ((IJavaVariable) child).getValue());
					}
				}
			}
			
			// Check that each parameter is a primitive type, otherwise set the state to be uncomputable, for now.
			// (unless it has already been calculated)
			for (ParameterDeclaration param : attribute.getParameterList()){
				// We can only deal with primitive types or strings
				if (!param.type().isPrimitive() && !param.type().isString()) {
					state = (state == AttributeState.CALCULATED) ? AttributeState.PRE_CALCULATED : AttributeState.NOT_CALCULABLE;
				}
			}
			
			
		} catch (DebugException e) {
			ILog log = Platform.getLog(Activator.getInstance().getBundle());
			log.log(new Status(IStatus.ERROR, Activator.JASTADD_PLUGIN_ID, e.getLocalizedMessage(), e));
		}
	}

	private boolean alreadyRunning(IJavaValue object) throws DebugException {
		// Check to see whether this method is currently executing on the stack
		for (IThread newThread : thread.getDebugTarget().getThreads()) {
			for (IStackFrame stackFrame : newThread.getStackFrames()) {
				IJavaStackFrame javaStackFrame = (IJavaStackFrame) stackFrame;
				if 		(javaStackFrame.getMethodName().equals(attribute.name()) &&
						javaStackFrame.getReferenceType().equals(object.getJavaType()) &&
						javaStackFrame.getThis().equals(object)) {
					// We're executing this method, so set calculating switch
					return true;
				}
			}
		}
		return false;
	}
	
	public AttributeState getState() {
		return state;
	}

	/**
	 * Returns the result of the evaluation.
	 * @return variable, iff getState().equals(AttributeState.CALCULATED) || getState().equals(AttributeState.PRE_CALCULATED)
	 */
	public IJavaValue getResult() {
		// If it's already been computed, we want to return the computed value
		if (getState().equals(AttributeState.CALCULATED) || getState().equals(AttributeState.PRE_CALCULATED)) {
			return result;
		} else {
			// Return nothing if we've not got a calculated result
			return null;
		}
	}

	/**
	 * Evaluates the attribute.
	 * 
	 * If it has already been evaluated, it is evaluated again.
	 * 
	 * Will not be evaluated if we're already executing this method
	 */
	public void eval() {
		try {
			// Ensure we're not trying to invoke an already running method
			if (state.equals(AttributeState.EMPTY) || state.equals(AttributeState.CALCULATED)) {
				if (parent instanceof IJavaObject) {
					IJavaObject object = (IJavaObject) parent;
					
					ArrayList<IJavaValue> args = new ArrayList<IJavaValue>();
					
					// Deal with arguments
					if (attribute.getNumParameter() > 0) {

						IJavaDebugTarget javaDebugTarget = ((IJavaDebugTarget) object.getDebugTarget());
						
						ParameterDialog dialog = new ParameterDialog(shell, "Enter the parameters for evaluating this attribute", javaDebugTarget);
						for (ParameterDeclaration param : attribute.getParameterList()) {
							dialog.addField(param.name(), param.type().name(), "", false);
						}
						if (dialog.open() == IDialogConstants.CANCEL_ID) {
							// Cancel execution
							return;
						}

						for (ParameterDeclaration param : attribute.getParameterList()) {
							String stringValue = dialog.getValue(param.name());
							
							IJavaValue arg = newPrimitiveValue(javaDebugTarget, stringValue, param.type().name());

							args.add(arg);
						}
						
					}
					
					result = object.sendMessage(attribute.name(), attribute.descName(), args.toArray(new IJavaValue[0]), thread, null);
					state = AttributeState.CALCULATED;
				} else {
					// TODO error, we've tried to do this on a primitive
				}
			} else {
				// We're not in a state to calculate the value
			}
		} catch (CoreException e) {
			ILog log = Platform.getLog(Activator.getInstance().getBundle());
			log.log(new Status(IStatus.ERROR, Activator.JASTADD_PLUGIN_ID, e.getLocalizedMessage(), e));
		} catch (NonPrimitiveTypeException e) {
			ILog log = Platform.getLog(Activator.getInstance().getBundle());
			log.log(new Status(IStatus.ERROR, Activator.JASTADD_PLUGIN_ID, e.getLocalizedMessage(), e));
		}		
	}

	/**
	 * Converts a string representing the value to the type specified by the second string.
	 * @param target
	 * @param value
	 * @param type
	 * @return
	 * @throws NonPrimitiveTypeException
	 */
	protected static IJavaValue newPrimitiveValue(IJavaDebugTarget target, String value, String type) throws NonPrimitiveTypeException {

		if (type.equals("int")) {
			return target.newValue(Integer.valueOf(value));
		} else if (type.equals("double")) {
			return target.newValue(Double.valueOf(value));
		} else if (type.equals("float")) {
			return target.newValue(Float.valueOf(value));
		} else if (type.equals("long")) {
			return target.newValue(Long.valueOf(value));
		} else if (type.equals("short")) {
			return target.newValue(Short.valueOf(value));
		} else if (type.equals("boolean")) {
			return target.newValue(Boolean.valueOf(value));
		} else if (type.equals("byte")) {
			return target.newValue(Byte.valueOf(value));
		} else if (type.equals("String")) {
			return target.newValue(value);
		} else if (type.equals("char")) {
			return target.newValue(value.charAt(0));
		}
		
		throw new NonPrimitiveTypeException(type);
	}
	
	protected static class NonPrimitiveTypeException extends Exception {
		private static final long serialVersionUID = -8114268132764373368L;
		
		public NonPrimitiveTypeException(String type) {
			super(type + " is not a primitive or a string and therefore cannot be instantiated.");
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.jastadd.plugin.jastadd.debugger.attributes.AttributeNode#getParentValue()
	 */
	public IJavaValue getParentValue() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.jastadd.plugin.jastadd.debugger.attributes.AttributeNode#getAttributeName()
	 */
	public String getAttributeName() {
		return attribute.name();
	}

	public static class AttributeEvaluationNested extends AttributeEvaluationNode {
		private AttributeNode parent;

		/**
		 * 
		 * @param parent - must have been calculated
		 * @param attribute
		 * @param viewer
		 */
		public AttributeEvaluationNested(AttributeNode parent, AttributeDecl attribute, IJavaThread thread, Shell shell) {
			super(parent.getResult(), attribute, thread, shell);
			this.parent = parent;
		}

		public AttributeNode getParent() {
			return parent;
		}
	}
}
