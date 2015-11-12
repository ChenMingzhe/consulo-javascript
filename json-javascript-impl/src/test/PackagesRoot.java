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

package test;

import java.util.Map;

import org.mustbe.consulo.json.jom.JomElement;
import org.mustbe.consulo.json.jom.JomPropertyGetter;

/**
 * @author VISTALL
 * @since 10.11.2015
 */
public interface PackagesRoot extends JomElement
{
	@JomPropertyGetter
	String getUrl();

	@JomPropertyGetter
	String[] getAuthors();

	@JomPropertyGetter
	PackagesObject getObject();

	@JomPropertyGetter
	Map<String, String> getDependencies();
}