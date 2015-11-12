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

package org.mustbe.consulo.json.jom;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.json.validation.JsonFileDescriptorProvider;
import org.mustbe.consulo.json.validation.descriptor.JsonObjectDescriptor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.ObjectUtil;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 10.11.2015
 */
public class JomModeAsJsonFileDescriptorProvider implements JsonFileDescriptorProvider
{
	@RequiredReadAction
	@Override
	public boolean isMyFile(@NotNull PsiFile file)
	{
		return JomManager.getInstance(file.getProject()).getFileElement(file) != null;
	}

	@RequiredReadAction
	@Override
	public void fillRootObject(@NotNull JsonObjectDescriptor root, @NotNull PsiFile file)
	{
		JomFileElement<JomElement> fileElement = JomManager.getInstance(file.getProject()).getFileElement(file);
		if(fileElement == null)
		{
			return;
		}

		fillDescriptor(root, fileElement.getFileDescriptor().getDefinitionClass());
	}

	private static void fillDescriptor(JsonObjectDescriptor objectDescriptor, Class<?> clazz)
	{
		Set<Method> methods = new HashSet<Method>();
		collectMethods(clazz, methods, new HashSet<Class<?>>());

		for(Method method : methods)
		{
			JomPropertyGetter jomProperty = method.getAnnotation(JomPropertyGetter.class);
			if(jomProperty == null)
			{
				continue;
			}

			String propertyName = StringUtil.getPropertyName(method.getName());
			propertyName = ObjectUtil.notNull(propertyName, method.getName());
			if(!StringUtil.isEmpty(jomProperty.value()))
			{
				propertyName = jomProperty.value();
			}

			fillObjectDescriptor(objectDescriptor, method.getReturnType(), method.getGenericReturnType(), propertyName);
		}
	}

	private static void fillObjectDescriptor(JsonObjectDescriptor objectDescriptor, @NotNull Class<?> classType, @NotNull Type genericType, @Nullable String propertyName)
	{
		if(classType.isArray())
		{
			objectDescriptor.addProperty(propertyName, classType);
		}
		else if(classType == Collection.class || classType == Set.class || classType == List.class)
		{
			if(!(genericType instanceof ParameterizedType))
			{
				throw new IllegalArgumentException();
			}

			Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
			Object o = Array.newInstance((Class<?>) actualTypeArguments[0], 0);

			objectDescriptor.addProperty(propertyName, o.getClass());
		}
		else if(classType == Null.class)
		{
			objectDescriptor.addProperty(propertyName, Null.class);
		}
		else if(classType == boolean.class || classType == Boolean.class)
		{
			objectDescriptor.addProperty(propertyName, Boolean.class);
		}
		else if(classType == String.class)
		{
			objectDescriptor.addProperty(propertyName, String.class);
		}
		else if(classType == byte.class ||
				classType == short.class ||
				classType == int.class ||
				classType == long.class ||
				classType == float.class ||
				classType == double.class ||
				classType == BigInteger.class ||
				classType == Byte.class ||
				classType == Short.class ||
				classType == Integer.class ||
				classType == Long.class ||
				classType == Float.class ||
				classType == Double.class ||
				classType == BigInteger.class)
		{
			objectDescriptor.addProperty(propertyName, Number.class);
		}
		else if(classType == Map.class)
		{
			if(!(genericType instanceof ParameterizedType))
			{
				throw new IllegalArgumentException();
			}

			Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();

			Class rawType = null;
			Type actualTypeArgument = actualTypeArguments[1];
			if(actualTypeArgument instanceof ParameterizedType)
			{
				rawType = (Class) ((ParameterizedType) actualTypeArgument).getRawType();
			}
			else
			{
				rawType = (Class) actualTypeArgument;
			}

			JsonObjectDescriptor child = new JsonObjectDescriptor();
			fillObjectDescriptor(child, rawType, actualTypeArguments[1], null);

			objectDescriptor.addProperty(propertyName, child);
		}
		else
		{
			JsonObjectDescriptor another = new JsonObjectDescriptor();
			fillDescriptor(another, classType);
			objectDescriptor.addProperty(propertyName, another);
		}
	}

	private static void collectMethods(Class<?> clazz, Set<Method> methods, Set<Class<?>> processClasses)
	{
		if(processClasses.contains(clazz))
		{
			return;
		}

		processClasses.add(clazz);

		Method[] declaredMethods = clazz.getDeclaredMethods();
		ContainerUtil.addAllNotNull(methods, declaredMethods);

		Class<?>[] interfaces = clazz.getInterfaces();
		for(Class<?> anInterface : interfaces)
		{
			collectMethods(anInterface, methods, processClasses);
		}
	}
}
