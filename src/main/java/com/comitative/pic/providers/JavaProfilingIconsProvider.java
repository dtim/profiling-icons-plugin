package com.comitative.pic.providers;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class JavaProfilingIconsProvider extends BaseProfilingIconsProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            PsiElement identifier = method.getIdentifyingElement();
            PsiClass containingClass = method.getContainingClass();
            if (identifier != null && containingClass != null) {
                String className = containingClass.getQualifiedName();
                if (className == null) {
                    className = containingClass.getName();
                }

                if (className != null) {
                    return createMarker(identifier, className,
                            method.isConstructor() ? CONSTRUCTOR_METHOD_NAME : method.getName());
                }
            }
        }

        return null;
    }
}
