/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Dmitry Stalnov (dstalnov@fusionone.com) - contributed fix for
 *       bug Encapuslate field can fail when two variables in one variable declaration (see
 *       https://bugs.eclipse.org/bugs/show_bug.cgi?id=51540).
 *******************************************************************************/
package tests.eclipse.SelfEncapsulateField;

/* Note: our setters always return a value; the result files have been adapted to adjust that. */

import junit.framework.TestCase;
import tests.CompileHelper;
import AST.FieldDeclaration;
import AST.Program;
import AST.RefactoringException;

public class SelfEncapsulateFieldTests extends TestCase {

	public SelfEncapsulateFieldTests(String name) {
		super(name);
	}

	protected String getResourceLocation() {
		return "tests/eclipse/SelfEncapsulateField/tests";
	}

	protected String adaptName(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1) + ".java";
	}

	protected void performTest(String id, String folder, String fieldName) throws Exception {
		Program in = CompileHelper.compile(getResourceLocation() + "/" + folder + "_in/" + adaptName(id));
		Program out = CompileHelper.compile(getResourceLocation() + "/" + folder + "_out/" + adaptName(id));
		assertNotNull(in);
		assertNotNull(out);
		try {
			FieldDeclaration fd = in.findField(fieldName);
			assertNotNull(fd);
			fd.doSelfEncapsulate();
			assertEquals(out.toString(), in.toString());
		} catch(RefactoringException rfe) {
			assertEquals(out.toString(), rfe.getMessage());
		}
	}

	protected void performInvalidTest(String id, String folder, String fieldName) throws Exception {
		Program in = CompileHelper.compile(getResourceLocation() + "/" + folder + "/" + adaptName(id));
		assertNotNull(in);
		try {
			FieldDeclaration fd = in.findField(fieldName);
			assertNotNull(fd);
			fd.doSelfEncapsulate();
			assertEquals("<failure>", in.toString());
		} catch(RefactoringException rfe) {
		}
	}

	private void objectTest(String fieldName) throws Exception {
		performTest(getName(), "object", fieldName);
	}

	private void baseTest(String fieldName) throws Exception {
		performTest(getName(), "base", fieldName);
	}

	private void invalidTest(String fieldName) throws Exception {
		performInvalidTest(getName(), "invalid", fieldName);
	}

	private void existingTest(String fieldName) throws Exception {
		performTest(getName(), "existingmethods", fieldName);
	}
	//=====================================================================================
	// Invalid
	//=====================================================================================

	public void testPostfixExpression() throws Exception {
		invalidTest("field");
	}

	public void testInvalidOverwrite() throws Exception {
		invalidTest("field");
	}

	public void testAnnotation() throws Exception {
		invalidTest("field");
	}

	//=====================================================================================
	// Primitiv Data Test
	//=====================================================================================

	public void testPrefixInt() throws Exception {
		baseTest("field");
	}

	public void testPrefixBoolean() throws Exception {
		baseTest("field");
	}

	public void testPostfixInt() throws Exception {
		baseTest("field");
	}

	public void testThisExpression() throws Exception {
		baseTest("field");
	}

	public void testThisExpressionInner() throws Exception {
		baseTest("field");
	}

	public void testFinal() throws Exception {
		baseTest("field");
	}

	public void testTwoFragments() throws Exception {
		baseTest("field");
	}

	//=====================================================================================
	// Basic Object Test
	//=====================================================================================

	public void testSimpleRead() throws Exception {
		objectTest("field");
	}

	public void testSimpleWrite() throws Exception {
		objectTest("field");
	}

	public void testSimpleReadWrite() throws Exception {
		objectTest("field");
	}

	public void testEnumRead() throws Exception {
		/* disabled: works, but enums pretty-print in a strange way
		objectTest("field");*/
	}

	public void testEnumReadWrite() throws Exception {
		/* disabled: works, but enums pretty-print in a strange way
		objectTest("field");*/
	}

	public void testNestedRead() throws Exception {
		objectTest("field");
	}

	public void testArrayRead() throws Exception {
		objectTest("field");
	}

    public void testCStyleArrayRead() throws Exception {
      objectTest("field");
    }


	public void testSetterInAssignment() throws Exception {
		objectTest("field");
	}

	public void testSetterInExpression() throws Exception {
		objectTest("field");
	}

	public void testSetterInInitialization() throws Exception {
		objectTest("field");
	}

	public void testSetterAsReceiver() throws Exception {
		objectTest("field");
	}

	public void testCompoundWrite() throws Exception {
		objectTest("field");
	}

	public void testCompoundWrite2() throws Exception {
		objectTest("field");
	}

	public void testCompoundWrite3() throws Exception {
		objectTest("field");
	}

	public void testFinalField() throws Exception {
		objectTest("field");
	}

	public void testGenericRead() throws Exception {
		objectTest("field");
	}

	public void testGenericRead2() throws Exception {
		objectTest("field");
	}

	public void testGenericReadWrite() throws Exception {
		objectTest("field");
	}

	//=====================================================================================
	// static import tests
	//=====================================================================================

	/* disabled: TODO
	private void performStaticImportTest(String referenceName) throws Exception {
		Program in = CompileHelper.compile(getResourceLocation() + "/static_ref_in/" + adaptName(referenceName));
		Program out = CompileHelper.compile(getResourceLocation() + "/static_ref_out/" + adaptName(referenceName));
		assertNotNull(in);
		assertNotNull(out);
		try {
			FieldDeclaration fd = in.findField(fieldName);
			assertNotNull(fd);
			fd.selfEncapsulate();
			assertEquals(out.toString(), in.toString());
		} catch(RefactoringException rfe) {
			assertEquals(out.toString(), rfe.getMessage());
		}
	}

	public void testStaticImportRead() throws Exception {
		performStaticImportTest("StaticImportReadReference");
	}

	public void testStaticImportWrite() throws Exception {
		performStaticImportTest("StaticImportWriteReference");
	}

	public void testStaticImportReadWrite() throws Exception {
		performStaticImportTest("StaticImportReadWriteReference");
	}

	public void testStaticImportNone() throws Exception {
		performStaticImportTest("StaticImportNoReference");
	}*/

	//=====================================================================================
	// existing getter/setter
	//=====================================================================================

	/* disabled: no support for reusing existing getters/setters
	public void testThisExpressionInnerWithSetter() throws Exception {
		existingTest("field");
	}

	public void testThisExpressionWithGetterSetter() throws Exception {
		existingTest("field");
	}

	public void testTwoFragmentsWithSetter() throws Exception {
		existingTest("field");
	}*/
}
