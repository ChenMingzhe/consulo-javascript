package com.sixrr.inspectjs.style;

import javax.annotation.Nonnull;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.inspectjs.*;

public class NonBlockStatementBodyJSInspection extends JavaScriptInspection {
    private InspectionJSFix fix = new WrapBodyFix();

    @Override
	@Nonnull
    public String getDisplayName() {
        return InspectionJSBundle.message("non.block.statement.body.display.name");
    }

    @Override
	@Nonnull
    public String getGroupDisplayName() {
        return JSGroupNames.STYLE_GROUP_NAME;
    }

    @Override
	public boolean isEnabledByDefault() {
        return false;
    }

    @Override
	public String buildErrorString(Object... args) {
        if (args[0] instanceof JSIfStatement) {
            return InspectionJSBundle.message("non.block.branch.error.string");
        } else {
            return InspectionJSBundle.message("non.block.body.error.string");
        }
    }

    @Override
	public InspectionJSFix buildFix(PsiElement location) {
        return fix;
    }

    private static class WrapBodyFix extends InspectionJSFix {
        @Override
		@Nonnull
        public String getName() {
            return InspectionJSBundle.message("wrap.statement.body.fix");
        }

        @Override
		public void doFix(Project project, ProblemDescriptor descriptor)
                throws IncorrectOperationException {
            final PsiElement statementIdentifier = descriptor.getPsiElement();
            final JSStatement statement = (JSStatement) statementIdentifier.getParent();
            if (statement instanceof JSLoopStatement) {
                final JSStatement body = ((JSLoopStatement) statement).getBody();
                wrapStatement(body);
            } else {
                final JSIfStatement ifStatement = (JSIfStatement) statement;
                final JSStatement thenBranch = ifStatement.getThen();
                if (thenBranch != null && !(thenBranch instanceof JSBlockStatement)) {
                    wrapStatement(thenBranch);
                }
                final JSStatement elseBranch = ifStatement.getElse();
                if (elseBranch != null && !(elseBranch instanceof JSBlockStatement)) {
                    wrapStatement(elseBranch);
                }
            }
        }

        private static void wrapStatement(JSStatement statement) throws IncorrectOperationException {
            final String text = statement.getText();
            replaceStatement(statement, '{' + text + '}');
        }
    }

    @Override
	public BaseInspectionVisitor buildVisitor() {
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor {

        @Override public void visitJSDoWhileStatement(@Nonnull JSDoWhileStatement statement) {
            super.visitJSDoWhileStatement(statement);

            final JSStatement body = statement.getBody();
            if (body == null) {
                return;
            }

            if (!!(body instanceof JSBlockStatement)) {
                return;
            }
            registerStatementError(statement, statement);
        }

        @Override public void visitJSWhileStatement(@Nonnull JSWhileStatement statement) {
            super.visitJSWhileStatement(statement);

            final JSStatement body = statement.getBody();
            if (body == null) {
                return;
            }

            if (body instanceof JSBlockStatement) {
                return;
            }
            registerStatementError(statement, statement);
        }

        @Override public void visitJSForStatement(@Nonnull JSForStatement statement) {
            super.visitJSForStatement(statement);

            final JSStatement body = statement.getBody();
            if (body == null) {
                return;
            }

            if (body instanceof JSBlockStatement) {
                return;
            }
            registerStatementError(statement, statement);
        }

        @Override public void visitJSForInStatement(@Nonnull JSForInStatement statement) {
            super.visitJSForInStatement(statement);

            final JSStatement body = statement.getBody();
            if (body == null) {
                return;
            }

            if (body instanceof JSBlockStatement) {
                return;
            }
            registerStatementError(statement, statement);
        }

        @Override public void visitJSIfStatement(@Nonnull JSIfStatement statement) {
            super.visitJSIfStatement(statement);

            final JSStatement thenBranch = statement.getThen();
            if (thenBranch != null) {
                if (!(thenBranch instanceof JSBlockStatement)) {
                    registerStatementError(statement, statement);
                    return;
                }
            }
            final JSStatement elseBranch = statement.getElse();

            if (elseBranch != null) {
                if (!(elseBranch instanceof JSBlockStatement) &&
                        !(elseBranch instanceof JSIfStatement)) {
                    registerStatementError(statement, statement);
                }
            }
        }
    }
}

