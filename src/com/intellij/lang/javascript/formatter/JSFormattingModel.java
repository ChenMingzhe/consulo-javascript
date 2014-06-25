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

package com.intellij.lang.javascript.formatter;

import org.jetbrains.annotations.NotNull;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingDocumentModel;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * @author ven
 */
public class JSFormattingModel implements FormattingModel
{
	private FormattingModel myModel;

	public JSFormattingModel(final PsiFile file, CodeStyleSettings settings, final Block rootBlock)
	{
		myModel = FormattingModelProvider.createFormattingModelForPsiFile(file, rootBlock, settings);
	}

	@Override
	@NotNull
	public Block getRootBlock()
	{
		return myModel.getRootBlock();
	}

	@Override
	@NotNull
	public FormattingDocumentModel getDocumentModel()
	{
		return myModel.getDocumentModel();
	}

	@Override
	public TextRange replaceWhiteSpace(TextRange textRange, String whiteSpace)
	{
		return myModel.replaceWhiteSpace(textRange, whiteSpace);
	}

	@Override
	public TextRange shiftIndentInsideRange(TextRange range, int indent)
	{
		return myModel.shiftIndentInsideRange(range, indent);
	}

	@Override
	public void commitChanges()
	{
		myModel.commitChanges();
	}
}
