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

package com.intellij.lang.javascript.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: max
 * Date: Jan 30, 2005
 * Time: 11:20:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSForInStatementImpl extends JSStatementImpl implements JSForInStatement
{
	public JSForInStatementImpl(final ASTNode node)
	{
		super(node);
	}

	@Override
	public JSVarStatement getDeclarationStatement()
	{
		final ASTNode childNode = getNode().findChildByType(JSElementTypes.VAR_STATEMENT);
		return childNode == null ? null : (JSVarStatement) childNode.getPsi();
	}

	@Override
	public JSExpression getVariableExpression()
	{
		ASTNode child = getNode().getFirstChildNode();
		while(child != null)
		{
			if(child.getElementType() == JSTokenTypes.IN_KEYWORD)
			{
				return null;
			}
			if(JSElementTypes.EXPRESSIONS.contains(child.getElementType()))
			{
				return (JSExpression) child.getPsi();
			}
			child = child.getTreeNext();
		}
		return null;
	}

	@Override
	public JSExpression getCollectionExpression()
	{
		ASTNode child = getNode().getFirstChildNode();
		boolean inPassed = false;
		while(child != null)
		{
			if(child.getElementType() == JSTokenTypes.IN_KEYWORD)
			{
				inPassed = true;
			}
			if(inPassed && JSElementTypes.EXPRESSIONS.contains(child.getElementType()))
			{
				return (JSExpression) child.getPsi();
			}
			child = child.getTreeNext();
		}

		return null;
	}

	@Override
	public boolean isForEach()
	{
		return getNode().findChildByType(JSTokenTypes.EACH_KEYWORD) != null;
	}

	@Override
	public JSStatement getBody()
	{
		ASTNode child = getNode().getFirstChildNode();
		boolean passedRParen = false;
		while(child != null)
		{
			if(child.getElementType() == JSTokenTypes.RPAR)
			{
				passedRParen = true;
			}
			else if(passedRParen && JSElementTypes.STATEMENTS.contains(child.getElementType()))
			{
				return (JSStatement) child.getPsi();
			}
			child = child.getTreeNext();
		}

		return null;
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent,
			@Nonnull PsiElement place)
	{
		if(lastParent != null)
		{
			final JSVarStatement statement = getDeclarationStatement();
			if(statement != null)
			{
				return statement.processDeclarations(processor, state, lastParent, place);
			}
			else
			{
				final JSExpression expression = getVariableExpression();
				if(expression != null && !processor.execute(expression, null))
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void accept(@Nonnull JSElementVisitor visitor)
	{
		visitor.visitJSForInStatement(this);
	}
}
