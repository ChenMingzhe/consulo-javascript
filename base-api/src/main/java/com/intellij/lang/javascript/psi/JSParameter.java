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

package com.intellij.lang.javascript.psi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayFactory;
import consulo.annotation.access.RequiredReadAction;

/**
 * User: max
 * Date: Jan 30, 2005
 * Time: 6:43:02 PM
 */
public interface JSParameter extends JSVariable
{
	public static final JSParameter[] EMPTY_ARRAY = new JSParameter[0];

	public static ArrayFactory<JSParameter> ARRAY_FACTORY = new ArrayFactory<JSParameter>()
	{
		@Nonnull
		@Override
		public JSParameter[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new JSParameter[count];
		}
	};

	JSFunction getDeclaringFunction();

	@RequiredReadAction
	boolean isRest();

	@Nullable
	@RequiredReadAction
	PsiElement getRestElement();

	boolean isOptional();
}
