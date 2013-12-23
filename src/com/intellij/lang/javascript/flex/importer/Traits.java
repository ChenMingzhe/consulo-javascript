package com.intellij.lang.javascript.flex.importer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maxim.Mossienko
 *         Date: Oct 20, 2008
 *         Time: 7:01:09 PM
 */
class Traits
{
	Object name;
	MethodInfo init;
	Traits itraits;
	Multiname base;
	int flags;
	String protectedNs;
	Multiname interfaces[];
	Map<String, MemberInfo> names = new LinkedHashMap<String, MemberInfo>();
	Map<Integer, SlotInfo> slots = new LinkedHashMap<Integer, SlotInfo>();
	Map<Integer, MethodInfo> methods = new LinkedHashMap<Integer, MethodInfo>();
	Map<Integer, MemberInfo> members = new LinkedHashMap<Integer, MemberInfo>();

	@Override
	public String toString()
	{
		return name.toString();
	}

	public void dump(Abc abc, String indent, String attr, final FlexByteCodeInformationProcessor processor)
	{
		for(MemberInfo m : members.values())
		{
			m.dump(abc, indent, attr, processor);
		}
	}

	String getClassName()
	{
		final String s = name.toString();
		if(s.endsWith(Abc.$))
		{
			return s.substring(0, s.length() - 1);
		}
		return s;
	}
}
