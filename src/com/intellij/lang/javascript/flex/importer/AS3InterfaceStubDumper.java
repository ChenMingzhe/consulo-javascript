package com.intellij.lang.javascript.flex.importer;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.psi.JSAttributeList;
import com.intellij.lang.javascript.psi.stubs.impl.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.SmartList;

/**
 * Created by IntelliJ IDEA.
 * User: Maxim.Mossienko
 * Date: 05.03.2009
 * Time: 0:43:37
 * To change this template use File | Settings | File Templates.
 */
class AS3InterfaceStubDumper extends AS3InterfaceDumper
{
	final LinkedList<StubElement> parents;

	public AS3InterfaceStubDumper(StubElement parent)
	{
		parents = new LinkedList<StubElement>();
		parents.add(parent);
	}

	@Override
	public void processMetadata(MetaData metaData)
	{
		parents.addLast(new JSAttributeStubImpl(metaData.name, parents.getLast()));
		super.processMetadata(metaData);
		parents.removeLast();
	}

	@Override
	public void addMetaDataValue(String s, String s1)
	{
		new JSAttributeNameValuePairStubImpl(s, StringUtil.stripQuotesAroundValue(s1), parents.getLast());
	}

	@Override
	protected void processArgumentList(MethodInfo methodInfo, String parentName)
	{
		parents.add(new JSParameterListStubImpl(parents.getLast()));
		super.processArgumentList(methodInfo, parentName);
		parents.removeLast();
	}

	@Override
	public void processParameter(@NotNull String name, @Nullable Multiname type, String parentName, @Nullable Multiname value, boolean rest)
	{
		new JSParameterStubImpl(name, rest ? JSParameterStubImpl.REST_MASK : 0, getMultinameAsPackageName(type, parentName, true), getValueRepr(value),
				parents.getLast());
	}

	@Override
	public void append(@NotNull @NonNls String str)
	{
	}

	@Override
	public void processFunction(MethodInfo methodInfo, boolean referenceNameRequested, Abc abc, String indent, String attr)
	{
		parents.add(new JSFunctionStubImpl(methodInfo.name.name, methodInfo.isGetMethod() ? JSFunctionStubImpl.GET_PROPERTY_MASK : methodInfo.isSetMethod
				() ? JSFunctionStubImpl.SET_PROPERTY_MASK : methodInfo.parentTraits != null && methodInfo.parentTraits.name == methodInfo.name ?
				JSFunctionStubImpl.CONSTRUCTOR_MASK : 0, getMultinameAsPackageName(methodInfo.name, methodInfo.parentTraits != null ? methodInfo.parentTraits
				.getClassName() : null, referenceNameRequested), getMultinameAsPackageName(methodInfo.returnType, methodInfo.getParentName(),
				referenceNameRequested), parents.getLast()));
		super.processFunction(methodInfo, referenceNameRequested, abc, indent, attr);
		parents.removeLast();
	}

	@Override
	public void processVariable(SlotInfo info, String indent, String attr)
	{
		parents.add(new JSVarStatementStubImpl(parents.getLast()));
		super.processVariable(info, indent, attr);
		String parentName = info.getParentName();
		String qName = getMultinameAsPackageName(info.name, parentName, false);
		new JSVariableStubImpl(qName.substring(qName.lastIndexOf('.') + 1), info.isConst() ? JSVariableStubImpl.CONST_MASK : 0,
				getMultinameAsPackageName(info.type, parentName, false), getValueRepr(info.value), qName, parents.getLast());
		parents.removeLast();
	}

	@Override
	public void processClass(SlotInfo slotInfo, Abc abc, String attr, String indent)
	{
		parents.add(new JSClassStubImpl(slotInfo.name.name, slotInfo.isInterfaceClass() ? JSClassStubImpl.INTERFACE_MASK : 0,
				getMultinameAsPackageName(slotInfo.name, null, false), parents.getLast()));
		super.processClass(slotInfo, abc, attr, indent);
		parents.removeLast();
	}

	@Override
	protected void processModifierList(MemberInfo memberInfo, String attr, String indent)
	{
		StringTokenizer tokenizer = new StringTokenizer(attr, " ");
		List<JSAttributeList.ModifierType> modifiers = new SmartList<JSAttributeList.ModifierType>();
		JSAttributeList.AccessType accessType = null;
		String ns = null;

		while(tokenizer.hasMoreTokens())
		{
			String next = tokenizer.nextToken();
			boolean foundModifier = false;

			for(JSAttributeList.AccessType type : JSAttributeList.AccessType.values())
			{
				if(next.equalsIgnoreCase(type.name()))
				{
					accessType = type;
					foundModifier = true;
					break;
				}
			}

			if(!foundModifier)
			{
				for(JSAttributeList.ModifierType type : JSAttributeList.ModifierType.values())
				{
					if(next.equalsIgnoreCase(type.name()))
					{
						modifiers.add(type);
						foundModifier = true;
						break;
					}
				}
			}

			if(!foundModifier)
			{
				ns = next;
			}
		}
		parents.add(new JSAttributeListStubImpl(parents.getLast(), ns, accessType, modifiers.toArray(new JSAttributeList.ModifierType[modifiers.size()])));
		super.processModifierList(memberInfo, attr, indent);
		parents.removeLast();
	}

	@Override
	protected void dumpExtendsList(Traits it)
	{
		if(!it.base.isStarReference())
		{
			new JSReferenceListStubImpl(new String[]{getMultinameAsPackageName(it.base, null, true)}, parents.getLast(), JSElementTypes.EXTENDS_LIST);
		}
	}

	@Override
	protected void dumpInterfacesList(String indent, Traits it, boolean anInterface)
	{
		String[] interfaces;
		if(it.interfaces.length > 0)
		{
			interfaces = new String[it.interfaces.length];

			int i = 0;
			for(Multiname name : it.interfaces)
			{
				interfaces[i++] = getMultinameAsPackageName(name, null, true);
			}
			new JSReferenceListStubImpl(interfaces, parents.getLast(), anInterface ? JSElementTypes.EXTENDS_LIST : JSElementTypes.IMPLEMENTS_LIST);
		}
	}
}
