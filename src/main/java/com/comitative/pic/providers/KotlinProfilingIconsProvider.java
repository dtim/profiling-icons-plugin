package com.comitative.pic.providers;


import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

public class KotlinProfilingIconsProvider extends BaseProfilingIconsProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        PsiElement identifier = null;
        String functionName = null;
        String qualifiedClassName = null;

        if (element instanceof KtNamedFunction) {
            KtNamedFunction function = (KtNamedFunction) element;
            identifier = function.getNameIdentifier();
            functionName = function.getName();
            KtClassOrObject ktClass = getKtClassOrObject(function);
            if (ktClass == null) {
                // It is a file scope function, we can obtain the qualified name from the containing Kotlin file
                KtFile containingFile = function.getContainingKtFile();
                String packageName = containingFile.getPackageFqName().asString();
                String fileName = containingFile.getName();
                if (fileName.endsWith(".kt")) {
                    qualifiedClassName = packageName + "." + fileName.substring(0, fileName.length() - 3) + "Kt";
                }
            } else {
                // Otherwise we should get a fully qualified name of a parent class/object
                FqName classFqName = ktClass.getFqName();
                if (classFqName != null) {
                    qualifiedClassName = classFqName.asString();
                }
            }
        } else if (element instanceof KtPrimaryConstructor){
            KtClassOrObject ktClass = ((KtPrimaryConstructor) element).getContainingClassOrObject();
            identifier = ktClass.getNameIdentifier();
            functionName = CONSTRUCTOR_METHOD_NAME;
            FqName classFqName = ktClass.getFqName();
            if (classFqName != null) {
                qualifiedClassName = classFqName.asString();
            }
        } else if (element instanceof KtSecondaryConstructor) {
            KtSecondaryConstructor constructor = (KtSecondaryConstructor) element;
            KtClassOrObject ktClass = constructor.getContainingClassOrObject();
            identifier = ktClass.getNameIdentifier();
            functionName = CONSTRUCTOR_METHOD_NAME;
            FqName classFqName = ktClass.getFqName();
            if (classFqName != null) {
                qualifiedClassName = classFqName.asString();
            }
        }

        if (identifier != null && qualifiedClassName != null && functionName != null) {
            return createMarker(identifier, qualifiedClassName, functionName);
        }

        return null;
    }

    private @Nullable
    KtClassOrObject getKtClassOrObject(@NotNull KtNamedFunction function) {
        PsiElement parent = function.getParent();
        if (parent instanceof KtClassBody) {
            PsiElement grandParent = parent.getParent();
            if (grandParent instanceof KtClassOrObject) {
                return (KtClassOrObject) grandParent;
            }
        }

        return null;
    }
}
