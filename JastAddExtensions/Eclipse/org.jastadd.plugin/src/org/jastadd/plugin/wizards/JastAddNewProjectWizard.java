package org.jastadd.plugin.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.jastadd.plugin.builder.JastAddBuilder;
import org.jastadd.plugin.resources.JastAddNature;

public abstract class JastAddNewProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage projectPage;
	private IProject newProject;
	private IConfigurationElement fConfigElement;

	@Override
	public boolean performFinish() {
		createNewProject();

		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);

		if (newProject == null)
			return false;
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//System.out.println("Initializing new project wizard JastAddJ");
	}

	@Override
	public void addPages() {
		super.addPages();
		projectPage = new WizardNewProjectCreationPage("JastAddNewProjectPage");
		projectPage.setTitle(createProjectPageTitle());
		projectPage.setDescription(createProjectPageDescription());
		this.addPage(projectPage);
	}

	protected abstract String createProjectPageTitle();
	protected abstract String createProjectPageDescription();
	protected abstract String getNatureID();
	
	private IProject createNewProject() {
		if (newProject != null) {
			return newProject;
		}

		final IProject newProjectHandle = projectPage.getProjectHandle();
		IPath defaultPath = Platform.getLocation();
		IPath newPath = projectPage.getLocationPath();
		if (defaultPath.equals(newPath))
			newPath = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description =
			workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocation(newPath);
		description.setNatureIds(new String[] { getNatureID() });

		// create the new project operation
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
			throws CoreException {
				createProject(description, newProjectHandle, monitor);
			}
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			// ie.- one of the steps resulted in a core exception	
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				if (((CoreException) t).getStatus().getCode()
						== IResourceStatus.CASE_VARIANT_EXISTS) {
					MessageDialog.openError(getShell(), "Error", "error1");
				} else {
					ErrorDialog.openError(getShell(), "Error", null,
							// no special message
							((CoreException) t).getStatus());
				}
			} else {
				// CoreExceptions are handled above, but unexpected runtime exceptions and errors may still occur.
				Platform.getLog(Platform.getBundle(PlatformUI.PLUGIN_ID)).log(
						new Status(
								Status.ERROR,
								PlatformUI.PLUGIN_ID,
								0,
								t.toString(),
								t));
				MessageDialog.openError(getShell(), "Error", "Error2");
			}
			return null;
		}

		newProject = newProjectHandle;

		try {
			IProjectDescription desc = newProject.getDescription();
			ICommand[] commands = desc.getBuildSpec();
			boolean found  = false;
			for(int i = 0; i < commands.length; i++) {
				if(commands[i].getBuilderName().equals(JastAddBuilder.BUILDER_ID)) {
					found = true;
					break;
				}
			}
			if(!found) {
				ICommand command = desc.newCommand();
				command.setBuilderName(JastAddBuilder.BUILDER_ID);
				ICommand[] newCommands = new ICommand[commands.length + 1];
				System.arraycopy(commands, 0, newCommands, 1, commands.length);
				newCommands[0] = command;
				desc.setBuildSpec(newCommands);
				newProject.setDescription(desc, null);
			}
		} catch(CoreException c) {
		}
		return newProject;
	}

	private void createProject(
			IProjectDescription description,
			IProject projectHandle,
			IProgressMonitor monitor)
	throws CoreException, OperationCanceledException {
		try {
			monitor.beginTask("", 2000); //$NON-NLS-1$

			projectHandle.create(
					description,
					new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled())
				throw new OperationCanceledException();

			projectHandle.open(new SubProgressMonitor(monitor, 1000));

		} finally {
			monitor.done();
		}
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
	    fConfigElement = config;
	}
	
}
