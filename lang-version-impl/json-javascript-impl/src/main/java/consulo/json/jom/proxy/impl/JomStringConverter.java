package consulo.json.jom.proxy.impl;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import consulo.javascript.psi.JSSimpleLiteralExpression;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import consulo.annotation.access.RequiredReadAction;
import consulo.javascript.lang.JavaScriptTokenSets;
import consulo.json.jom.proxy.JomBadValueExpressionException;
import consulo.json.jom.proxy.JomValueConverter;

/**
 * @author VISTALL
 * @since 13.11.2015
 */
public class JomStringConverter implements JomValueConverter.Converter<String>
{
	@Override
	public String getDefaultValue()
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public String parseValue(@Nonnull Class type, @Nonnull Type genericType, @Nonnull PsiElement value) throws JomBadValueExpressionException
	{
		if(value instanceof JSSimpleLiteralExpression)
		{
			IElementType elementType = ((JSSimpleLiteralExpression) value).getLiteralElementType();
			if(JavaScriptTokenSets.STRING_LITERALS.contains(elementType))
			{
				return StringUtil.unquoteString(value.getText());
			}
		}

		throw new JomBadValueExpressionException();
	}
}
