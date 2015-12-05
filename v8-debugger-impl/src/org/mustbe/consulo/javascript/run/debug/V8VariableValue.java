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

package org.mustbe.consulo.javascript.run.debug;

import java.util.Collection;

import javax.swing.Icon;

import org.chromium.sdk.JsArray;
import org.chromium.sdk.JsDeclarativeVariable;
import org.chromium.sdk.JsEvaluateContext;
import org.chromium.sdk.JsFunction;
import org.chromium.sdk.JsObject;
import org.chromium.sdk.JsValue;
import org.chromium.sdk.JsVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;

/**
 * @author VISTALL
 * @since 20.03.14
 */
public class V8VariableValue extends XNamedValue
{
	public static void addValue(@NotNull XValueChildrenList valueChildrenList, @NotNull JsEvaluateContext debugContext, @NotNull JsVariable jsVariable)
	{
		JsValue value = jsVariable.getValue();
		if(value instanceof JsFunction)
		{
			return;
		}
		valueChildrenList.add(new V8VariableValue(debugContext, jsVariable));
	}

	@NotNull
	private final JsEvaluateContext myEvaluateContext;
	@NotNull
	private final JsVariable myJsVariable;

	private V8VariableValue(@NotNull JsEvaluateContext evaluateContext, @NotNull JsVariable jsVariable)
	{
		super(jsVariable.getName());
		myEvaluateContext = evaluateContext;
		myJsVariable = jsVariable;
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		final JsDeclarativeVariable declarativeVariable = myJsVariable.asDeclarativeVariable();
		if(declarativeVariable == null)
		{
			return null;
		}
		final JsValue value = myJsVariable.getValue();
		if(!declarativeVariable.isMutable())
		{
			return null;
		}
		final JsValue.Type valueType = value.getType();
		switch(valueType)
		{
			case TYPE_NUMBER:
			case TYPE_NULL:
			case TYPE_STRING:
			case TYPE_BOOLEAN:
				return new XValueModifier()
				{
					@Override
					public void setValue(@NotNull String expression, @NotNull final XModificationCallback callback)
					{
						JsEvaluateContext.PrimitiveValueFactory valueFactory = myEvaluateContext.getValueFactory();
						JsValue value = null;
						try
						{
							if(expression.equals("null"))
							{
								value = valueFactory.getNull();
							}
							else if(expression.equals("undefined"))
							{
								value = valueFactory.getUndefined();
							}
							else
							{
								switch(valueType)
								{
									case TYPE_NUMBER:
										value = valueFactory.createNumber(expression);
										break;
									case TYPE_STRING:
										value = valueFactory.createString(expression);
										break;
									case TYPE_BOOLEAN:
										value = valueFactory.createBoolean(Boolean.valueOf(expression));
										break;
									case TYPE_ERROR:
										break;
									case TYPE_REGEXP:
										break;
									case TYPE_DATE:
										break;
									case TYPE_ARRAY:
										break;
									case TYPE_UNDEFINED:
										break;
									case TYPE_NULL:
										break;
								}
							}
						}
						catch(Exception e)
						{
							callback.errorOccurred("Bad value");
							return;
						}

						declarativeVariable.setValue(value, new JsDeclarativeVariable.SetValueCallback()
						{
							@Override
							public void success()
							{
								callback.valueModified();
							}

							@Override
							public void failure(Exception e)
							{
								callback.errorOccurred(e.getMessage());
							}
						}, null);
					}

					@Override
					public void calculateInitialValueEditorText(XInitialValueCallback callback)
					{
						callback.setValue(value.getValueString());
					}
				};
			default:
				return null;
		}
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		XValueChildrenList valueChildrenList = new XValueChildrenList();

		JsValue value = myJsVariable.getValue();
		if(value instanceof JsArray)
		{
			long length = ((JsArray) value).getLength();
			for(int i = 0; i < length; i++)
			{
				V8VariableValue.addValue(valueChildrenList, myEvaluateContext, ((JsArray) value).get(i));
			}
		}
		else if(value instanceof JsObject)
		{
			Collection<? extends JsVariable> properties = ((JsObject) value).getProperties();
			for(JsVariable property : properties)
			{
				V8VariableValue.addValue(valueChildrenList, myEvaluateContext, property);
			}
		}
		node.addChildren(valueChildrenList, true);
	}

	@NotNull
	private static Icon getIconForValue(JsValue value, JsValue.Type valueType)
	{
		if(value instanceof JsArray)
		{
			return AllIcons.Debugger.Db_array;
		}
		switch(valueType)
		{
			case TYPE_NUMBER:
			case TYPE_NULL:
			case TYPE_REGEXP:
			case TYPE_UNDEFINED:
			case TYPE_BOOLEAN:
			case TYPE_STRING:
				return AllIcons.Debugger.Db_primitive;
		}
		return AllIcons.Debugger.Value;
	}

	private static boolean canHaveChildren(JsValue value, JsValue.Type valueType)
	{
		if(value instanceof JsArray)
		{
			return ((JsArray) value).getLength() > 0;
		}
		switch(valueType)
		{
			case TYPE_NUMBER:
			case TYPE_NULL:
			case TYPE_REGEXP:
			case TYPE_UNDEFINED:
			case TYPE_STRING:
			case TYPE_BOOLEAN:
				return false;
		}
		return true;
	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		final JsValue value = myJsVariable.getValue();
		final JsValue.Type valueType = value.getType();

		xValueNode.setPresentation(getIconForValue(value, valueType), new XValuePresentation()
		{
			@Nullable
			@Override
			public String getType()
			{
				switch(valueType)
				{
					case TYPE_NUMBER:
					case TYPE_STRING:
					case TYPE_NULL:
					case TYPE_DATE:
					case TYPE_REGEXP:
					case TYPE_UNDEFINED:
					case TYPE_BOOLEAN:
						return null;
					default:
						if(value instanceof JsArray)
						{
							return StringUtil.decapitalize(((JsArray) value).getClassName()) + "[" + ((JsArray) value).getLength() + "]";
						}
						else if(value instanceof JsObject)
						{
							return StringUtil.decapitalize(((JsObject) value).getClassName());
						}
						return null;
				}
			}

			@Override
			public void renderValue(@NotNull XValueTextRenderer textRenderer)
			{
				switch(myJsVariable.getValue().getType())
				{
					case TYPE_NUMBER:
						textRenderer.renderValue(myJsVariable.getValue().getValueString());
						break;
					case TYPE_STRING:
						textRenderer.renderStringValue(myJsVariable.getValue().getValueString());
						break;
					case TYPE_FUNCTION:
						break;
					case TYPE_BOOLEAN:
						textRenderer.renderValue(myJsVariable.getValue().getValueString());
						break;
					case TYPE_ERROR:
						break;
					case TYPE_REGEXP:
						textRenderer.renderStringValue(myJsVariable.getValue().getValueString());
						break;
					case TYPE_DATE:
						textRenderer.renderValue(myJsVariable.getValue().getValueString());
						break;
					case TYPE_UNDEFINED:
						textRenderer.renderValue("undefined");
						break;
					case TYPE_NULL:
						textRenderer.renderValue("null");
						break;
				}
			}
		}, canHaveChildren(value, valueType));
	}
}
