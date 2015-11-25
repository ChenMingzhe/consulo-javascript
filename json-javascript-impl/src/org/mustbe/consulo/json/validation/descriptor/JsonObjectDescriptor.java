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

package org.mustbe.consulo.json.validation.descriptor;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.json.validation.NativeArray;
import com.intellij.openapi.util.Factory;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 10.11.2015
 */
public class JsonObjectDescriptor
{
	private Map<String, JsonPropertyDescriptor> myProperties = new HashMap<String, JsonPropertyDescriptor>();

	@NotNull
	public JsonPropertyDescriptor addProperty(@Nullable final String propertyName, @NotNull final Class<?> value)
	{
		if(value == Object.class)
		{
			throw new IllegalArgumentException("We cant add object type, use JsonObjectDescriptor as parameter");
		}

		return ContainerUtil.getOrCreate(myProperties, propertyName, new Factory<JsonPropertyDescriptor>()
		{
			@Override
			public JsonPropertyDescriptor create()
			{
				return new JsonPropertyDescriptor(propertyName, value);
			}
		});
	}

	@NotNull
	public JsonPropertyDescriptor addProperty(@Nullable final String propertyName, @NotNull final JsonObjectDescriptor value)
	{
		return ContainerUtil.getOrCreate(myProperties, propertyName, new Factory<JsonPropertyDescriptor>()
		{
			@Override
			public JsonPropertyDescriptor create()
			{
				return new JsonPropertyDescriptor(propertyName, value);
			}
		});
	}

	@NotNull
	public JsonPropertyDescriptor addProperty(@Nullable final String propertyName, @NotNull final NativeArray value)
	{
		return ContainerUtil.getOrCreate(myProperties, propertyName, new Factory<JsonPropertyDescriptor>()
		{
			@Override
			public JsonPropertyDescriptor create()
			{
				return new JsonPropertyDescriptor(propertyName, value);
			}
		});
	}

	@Nullable
	public JsonPropertyDescriptor getProperty(@NotNull final String propertyName)
	{
		JsonPropertyDescriptor propertyDescriptor = myProperties.get(propertyName);
		if(propertyDescriptor != null)
		{
			return propertyDescriptor;
		}

		return myProperties.get(null);
	}

	public Map<String, JsonPropertyDescriptor> getProperties()
	{
		return myProperties;
	}
}
