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
        if (element instanceof KtNamedFunction) {
            KtNamedFunction function = (KtNamedFunction) element;
            PsiElement identifier = function.getNameIdentifier();
            KtClassOrObject ktClass = getKtClassOrObject(function);
            String functionName = function.getName();
            String qualifiedClassName = null;
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
        }

        return null;
    }

    private @Nullable KtClassOrObject getKtClassOrObject(@NotNull KtNamedFunction function) {
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
