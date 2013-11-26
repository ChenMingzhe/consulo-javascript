package com.intellij.lang.javascript.generation;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.MemberChooserObject;
import com.intellij.codeInsight.generation.PsiElementMemberChooserObject;
import com.intellij.javascript.JSParameterInfoHandler;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.RowIcon;
import com.intellij.util.Icons;

import javax.swing.*;

/**
 * @author Maxim.Mossienko
*         Date: Jul 17, 2008
*         Time: 8:55:57 PM
*/
public class JSNamedElementNode extends PsiElementMemberChooserObject implements ClassMember {
  public JSNamedElementNode(JSNamedElement node) {
    super(node, buildTextFor(node), buildIcon(node));
  }

  private static Icon buildIcon(final JSNamedElement node) {
    Icon icon = node.getIcon(0);

    if (node instanceof JSFunction) {
      final JSFunction function = (JSFunction)node;
      final Icon accessIcon;

      if(function.isGetProperty()) {
        accessIcon = Icons.VARIABLE_READ_ACCESS;
      } else if(function.isSetProperty()) {
        accessIcon = Icons.VARIABLE_WRITE_ACCESS;
      } else {
        accessIcon = null;
      }

      if (accessIcon != null) {
        final LayeredIcon layeredIcon = new LayeredIcon(1);
        layeredIcon.setIcon(accessIcon, 0, 1, 3);
        RowIcon rowIcon = new RowIcon(2);
        rowIcon.setIcon(layeredIcon, 1);
        rowIcon.setIcon(icon, 0);
        icon = rowIcon;
      }
    }
    return icon;
  }

  private static String buildTextFor(final JSNamedElement node) {
    String text = node.getName();

    if (node instanceof JSFunction) {
      final JSFunction function = (JSFunction)node;
      text += "(";
      final JSParameterList parameterList = function.getParameterList();

      if (parameterList != null) {
        boolean first = true;
        for(JSParameter p:parameterList.getParameters()) {
          if (!first) text += ", ";
          first = false;
          text += JSParameterInfoHandler.getSignatureForParameter(p, false);
        }
      }

      text += ")";
      final String typeString = function.getReturnTypeString();
      if (typeString != null) {
        text += ":" + typeString;
      }
    } else if (node instanceof JSVariable) {
      final JSVariable var = (JSVariable)node;
      final String typeString = var.getTypeString();
      if (typeString != null) text += ":" + typeString;
    }
    return text;
  }

  public MemberChooserObject getParentNodeDelegate() {
    final PsiElement element = getPsiElement();
    PsiElement parent = JSResolveUtil.findParent(element);
    return new JSNamedElementNode((JSNamedElement)parent);
  }
}
