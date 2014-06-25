/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.lang.javascript.psi.resolve;

import com.intellij.lang.javascript.psi.JSImportStatement;
import com.intellij.psi.PsiElement;

/**
 * @author Maxim.Mossienko
 *         Date: Jul 29, 2008
 *         Time: 8:04:55 PM
 */
public class JSImportedElementResolveResult
{
	public final String qualifiedName;
	public final PsiElement resolvedElement;
	public final JSImportStatement importStatement;
	public static final JSImportedElementResolveResult EMPTY_RESULT = new JSImportedElementResolveResult(null);

	public JSImportedElementResolveResult(String _qualifiedName)
	{
		this(_qualifiedName, null, null);
	}

	public JSImportedElementResolveResult(String _qualifiedName, PsiElement _resolvedElement, JSImportStatement _importString)
	{
		qualifiedName = _qualifiedName;
		resolvedElement = _resolvedElement;
		importStatement = _importString;
	}

	public JSImportedElementResolveResult appendSignature(final String s)
	{
		return new JSImportedElementResolveResult(qualifiedName + s, resolvedElement, importStatement);
	}
}
