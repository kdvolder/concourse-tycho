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
package concourse.eclipse.tests;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import concourse.eclipse.tests.util.ACondition;
import junit.framework.TestCase;

public class CreateProjectTest extends TestCase {

	@Override
	protected void setUp() throws Exception {

	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testCreateProject() throws Exception {

		ACondition.waitFor("workbench", 60000, () -> {
			IWorkbenchWindow workenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			assertNotNull(workenchWindow);
		});

	}

}
