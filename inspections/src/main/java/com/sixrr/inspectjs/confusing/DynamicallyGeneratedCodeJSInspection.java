package com.sixrr.inspectjs.confusing;

import javax.annotation.Nonnull;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSNewExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.sixrr.inspectjs.BaseInspectionVisitor;
import com.sixrr.inspectjs.InspectionJSBundle;
import com.sixrr.inspectjs.JSGroupNames;
import com.sixrr.inspectjs.JavaScriptInspection;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;

public class DynamicallyGeneratedCodeJSInspection extends JavaScriptInspection {

    @Override
	@Nonnull
    public String getDisplayName() {
        return InspectionJSBundle.message("dynamically.generated.code.display.name");
    }

    @Override
	@Nonnull
    public String getGroupDisplayName() {
        return JSGroupNames.CONFUSING_GROUP_NAME;
    }

    @Override
	@Nullable
    protected String buildErrorString(Object... args) {
        return InspectionJSBundle.message("dynamically.generated.code.error.string");
    }

    @Override
	public BaseInspectionVisitor buildVisitor() {
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor {

        @Override public void visitJSCallExpression(JSCallExpression jsCallExpression) {
            super.visitJSCallExpression(jsCallExpression);
            final JSExpression methodExpression;
            try {
                methodExpression = jsCallExpression.getMethodExpression();
            } catch (Exception e) {
                return; //catching an intelliJ CCE
            }
            if (!(methodExpression instanceof JSReferenceExpression)) {
                return;
            }
            final JSReferenceExpression referenceExpression = (JSReferenceExpression) methodExpression;
            final JSExpression qualifier = referenceExpression.getQualifier();

            @NonNls final String methodName = referenceExpression.getReferencedName();
            if (!"eval".equals(methodName) && !"setTimeout".equals(methodName) && !"setInterval".equals(methodName)) {
                return;
            }
            registerError(methodExpression);
        }

        @Override public void visitJSNewExpression(JSNewExpression jsNewExpression) {
            super.visitJSNewExpression(
                    jsNewExpression);
            final JSExpression methodExpression;
            try {
                methodExpression = jsNewExpression.getMethodExpression();
            } catch (Exception e) {
                return; //catching an intelliJ CCE
            }
            if (!(methodExpression instanceof JSReferenceExpression)) {
                return;
            }
            final JSReferenceExpression referenceExpression = (JSReferenceExpression) methodExpression;
            final JSExpression qualifier = referenceExpression.getQualifier();

            if (qualifier != null) {
                return;
            }
            @NonNls final String methodName = referenceExpression.getReferencedName();
            if (!"Function".equals(methodName)) {
                return;
            }
            registerError(methodExpression);
        }
    }
}
