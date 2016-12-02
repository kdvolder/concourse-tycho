/*******************************************************************************
 * Copyright (c) 2012, 2016 Pivotal Software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * and the Apache License v2.0 is available at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * You may elect to redistribute this code under either of these licenses.
 *
 *  Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 ********************************************************************************/
package org.eclipse.cft.server.tests.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ConcurrentModificationException;

import org.eclipse.cft.server.core.internal.CloudFoundryPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;

import junit.framework.TestCase;

public class CloudUtilTest extends TestCase {

	@Override
	protected void setUp() throws Exception {

	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testCreateProject() throws Exception {
		IProject project = createPredefinedProject("testProject", CloudFoundryPlugin.PLUGIN_ID);
		assertNotNull(project);
	}

	static String getSourceWorkspacePath(String bundleName) {
		return getPluginDirectoryPath(bundleName) + java.io.File.separator + "workspace"; //$NON-NLS-1$
	}

	private static String getPluginDirectoryPath(String bundleName) {
		try {
			URL platformURL = Platform.getBundle(bundleName).getEntry("/"); //$NON-NLS-1$
			return new File(FileLocator.toFileURL(platformURL).getFile()).getAbsolutePath();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static IProject createPredefinedProject(final String projectName, String bundleName)
			throws CoreException, IOException {
		IJavaProject jp = setUpJavaProject(projectName, bundleName);
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
		return jp.getProject();
	}

	public static File createTempDirectory() throws IOException {
		return createTempDirectory("sts", null);
	}

	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.delete();
		file.mkdirs();
		return file;
	}

	private static IJavaProject setUpJavaProject(final String projectName, String bundleName)
			throws CoreException, IOException {
		return setUpJavaProject(projectName, "1.4", getSourceWorkspacePath(bundleName)); //$NON-NLS-1$
	}

	public static IJavaProject setUpJavaProject(final String projectName, String compliance, String sourceWorkspacePath)
			throws CoreException, IOException {
		IProject project = setUpProject(projectName, compliance, sourceWorkspacePath);
		IJavaProject javaProject = JavaCore.create(project);
		return javaProject;
	}

	/**
	 * Copy file from src (path to the original file) to dest (path to the
	 * destination file).
	 */
	private static void copy(File src, File dest) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			OutputStream out = new FileOutputStream(dest);
			try {
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			finally {
				out.close();
			}
		}
		finally {
			in.close();
		}
	}

	/**
	 * Copy the given source directory (and all its contents) to the given
	 * target directory.
	 */
	private static void copyDirectory(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdirs();
		}
		File[] files = source.listFiles();
		if (files == null) {
			return;
		}
		for (File sourceChild : files) {
			String name = sourceChild.getName();
			if (name.equals(".svn")) {
				continue;
			}
			File targetChild = new File(target, name);
			if (sourceChild.isDirectory()) {
				copyDirectory(sourceChild, targetChild);
			}
			else {
				copy(sourceChild, targetChild);
			}
		}
	}

	public static IProject setUpProject(final String projectName, String compliance, String sourceWorkspacePath)
			throws CoreException, IOException {
		// copy files in project from source workspace to target workspace
		String targetWorkspacePath = getWorkspaceRoot().getLocation().toFile().getCanonicalPath();
		copyDirectory(new File(sourceWorkspacePath, projectName), new File(targetWorkspacePath, projectName));

		// create project
		final IProject project = getWorkspaceRoot().getProject(projectName);
		IWorkspaceRunnable populate = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(null);
				try {
					project.open(null);
				}
				catch (ConcurrentModificationException e) {
					// wait and try again to work-around
					// ConcurrentModificationException (bug 280488)
					try {
						Thread.sleep(500);
						project.open(null);
						project.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}
				}
			}
		};
		getWorkspace().run(populate, null);
		return project;
	}

	/**
	 * Wait for autobuild notification to occur
	 */
	public static void waitForAutoBuild() {
		waitForJobFamily(ResourcesPlugin.FAMILY_AUTO_BUILD);
	}

	/**
	 * Allows Display to process events, so UI can make progress. Tests running
	 * in the UI thread may need to call this to avoid UI deadlocks.
	 * <p>
	 * For convenience, it is allowed to call this method from a non UI thread,
	 * but such calls have no effect.
	 */
	public static void waitForDisplay() {
		if (inUIThread()) {
			while (Display.getDefault().readAndDispatch()) {
				// do nothing
			}
		}
	}

	public static boolean inUIThread() {
		return Display.getDefault().getThread() == Thread.currentThread();
	}

	// public static void waitForEditor(IEditorPart editor) throws CoreException
	// {
	// IFileEditorInput editorInput = (IFileEditorInput)
	// editor.getEditorInput();
	// IFile file = editorInput.getFile();
	// waitForResource(file);
	// }

	public static void waitForJobFamily(Object jobFamily) {
		boolean wasInterrupted = false;
		do {
			try {
				Job.getJobManager().join(jobFamily, null);
				wasInterrupted = false;
			}
			catch (OperationCanceledException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				wasInterrupted = true;
			}
		} while (wasInterrupted);
	}

	public static void waitForManualBuild() {
		waitForJobFamily(ResourcesPlugin.FAMILY_MANUAL_BUILD);
	}

	/**
	 * Returns the IWorkspace this test suite is running on.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}

	public static void waitForResource(IResource resource) throws CoreException {
		waitForAutoBuild();
		waitForManualBuild();
		waitForJobFamily(ResourcesPlugin.FAMILY_AUTO_REFRESH);
		waitForJobFamily(ResourcesPlugin.FAMILY_MANUAL_REFRESH);
		resource.refreshLocal(IResource.DEPTH_ONE, null);
	}

	public static void setAutoBuilding(boolean enabled) throws CoreException {
		IWorkspaceDescription wsd = getWorkspace().getDescription();
		if (!wsd.isAutoBuilding() == enabled) {
			wsd.setAutoBuilding(enabled);
			getWorkspace().setDescription(wsd);
		}
	}

}
