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

/*
 * @author max
 */
package com.intellij.lang.javascript.psi.stubs;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.javascript.psi.JSQualifiedNamedElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import consulo.javascript.lang.psi.stubs.JavaScriptIndexKeys;

public class JSQualifiedElementIndex extends StringStubIndexExtension<JSQualifiedNamedElement>
{
	@NotNull
	@Override
	public StubIndexKey<String, JSQualifiedNamedElement> getKey()
	{
		return JavaScriptIndexKeys.ELEMENTS_BY_QNAME;
	}
}