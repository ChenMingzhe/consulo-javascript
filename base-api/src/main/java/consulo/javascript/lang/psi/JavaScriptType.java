/*
 * Copyright 2013-2015 must-be.org
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

package consulo.javascript.lang.psi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.annotation.access.RequiredReadAction;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 13.12.2015
 */
public interface JavaScriptType
{
	JavaScriptType UNKNOWN = new JavaScriptType()
	{
		@RequiredReadAction
		@Nonnull
		@Override
		public String getPresentableText()
		{
			return "?";
		}

		@Nullable
		@Override
		public PsiElement getTargetElement()
		{
			return null;
		}
	};

	@Nonnull
	@RequiredReadAction
	String getPresentableText();

	@Nullable
	PsiElement getTargetElement();
}
