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

package consulo.json.validation.descriptionByAnotherPsiElement;

import javax.annotation.Nonnull;
import consulo.annotation.access.RequiredReadAction;
import consulo.json.validation.JsonFileDescriptorProvider;
import consulo.json.validation.descriptor.JsonObjectDescriptor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;

/**
 * @author VISTALL
 * @since 12.11.2015
 */
public class DescriptionByAnotherPsiElementJsonDescriptionProvider implements JsonFileDescriptorProvider
{
	@RequiredReadAction
	@Override
	public boolean isMyFile(@Nonnull PsiFile file)
	{
		VirtualFile virtualFile = PsiUtilCore.getVirtualFile(file);
		if(virtualFile == null)
		{
			return false;
		}
		return DescriptionByAnotherPsiElementService.getInstance(file.getProject()).getRegisteredPsiElementId(virtualFile) != null;
	}

	@RequiredReadAction
	@Override
	public void fillRootObject(@Nonnull JsonObjectDescriptor root, @Nonnull PsiFile file)
	{
		VirtualFile virtualFile = PsiUtilCore.getVirtualFile(file);
		if(virtualFile == null)
		{
			return;
		}

		Pair<DescriptionByAnotherPsiElementProvider<PsiElement>, PsiElement> pair = DescriptionByAnotherPsiElementService.getInstance(file.getProject())
				.getRegisteredPsiElementInfo(virtualFile);

		PsiElement psiElement = pair.getSecond();
		if(psiElement == null)
		{
			return;
		}

		pair.getFirst().fillRootObject(psiElement, root);
	}
}
