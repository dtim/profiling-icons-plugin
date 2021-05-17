package com.comitative.pic.providers;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

import java.util.List;

public class KotlinProfilingIconsProvider extends BaseProfilingIconsProvider {

    private static final Logger LOG = Logger.getInstance(KotlinProfilingIconsProvider.class);

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        Project currentProject = element.getProject();
        StatisticsService statisticsService = currentProject.getService(StatisticsService.class);

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
            CodeReference codeReference = CodeReference.builder()
                    .setFqClassName(qualifiedClassName)
                    .setMethodName(functionName)
                    .build();

            List<TimeRecord> records = statisticsService.getTimeRecords(codeReference);
            if (!records.isEmpty()) {
                final GutterMark gutterMark = getImpactGutterMark(records.get(0));
                return new LineMarkerInfo<>(
                        identifier,
                        identifier.getTextRange(),
                        gutterMark.getIcon(),
                        elt -> gutterMark.getTooltipText(),
                        null,
                        GutterIconRenderer.Alignment.CENTER);
            }
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
