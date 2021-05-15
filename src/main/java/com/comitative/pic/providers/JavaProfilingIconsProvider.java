package com.comitative.pic.providers;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JavaProfilingIconsProvider extends BaseProfilingIconsProvider {

    private final Random rng = new Random();
    private static final Logger LOG = Logger.getInstance(JavaProfilingIconsProvider.class);

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(
            @NotNull List<? extends PsiElement> elements,
            @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if (!elements.isEmpty()) {
            Project currentProject = elements.get(0).getProject();
            StatisticsService statisticsService = currentProject.getService(StatisticsService.class);

            for (PsiElement element : elements) {
                if (element instanceof PsiClass) {
                    PsiClass psiClass = (PsiClass) element;
                    for (PsiMethod psiMethod : psiClass.getMethods()) {
                        PsiElement identifier = psiMethod.getIdentifyingElement();
                        if (identifier != null) {
                            String className = psiClass.getQualifiedName();
                            if (className == null) {
                                className = psiClass.getName();
                            }

                            if (className != null) {
                                CodeReference codeReference = CodeReference.builder()
                                        .setFqClassName(className)
                                        .setMethodName(psiMethod.getName())
                                        .build();

                                final List<TimeRecord> records = statisticsService.getTimeRecords(codeReference);

                                // A bit of corner-cutting: let's pretend that the first record is the correct one.
                                // It will really be the case in almost all cases except code blocks
                                // lesser than a method (lambda etc).
                                if (!records.isEmpty()) {
                                    final TimeRecord timeRecord = records.get(0);
                                    final long sampleCount = timeRecord.getSampleCount();
                                    final String tooltip = String.format(
                                            "%.02f%% (%d %s)",
                                            timeRecord.getRelativeTime() * 100,
                                            sampleCount,
                                            sampleCount == 1 ? "sample" : "samples");
                                    result.add(new LineMarkerInfo<>(
                                            identifier,
                                            identifier.getTextRange(),
                                            getImpactIcon(timeRecord.getRelativeTime()),
                                            elt -> tooltip,
                                            null,
                                            GutterIconRenderer.Alignment.CENTER));

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Return a qualified class name starting from the outermost class but without the package.
     * For example, if a fully qualified name for a class is "my.package.name.MyOutermostClass.MyInnerClass",
     * the method will return "MyOutermostClass.MyInnerClass".
     *
     * @param psiClass a PSI node of the class
     * @return a string representation of the class name
     *
     * Note: this method is not currently used but it may be needed in the future.
     */
    private String combinedClassName(@NotNull PsiClass psiClass) {
        Stack<String> components = new Stack<>();
        PsiClass currentClass = psiClass;
        while (currentClass != null) {
            components.push(currentClass.getName());
            currentClass = currentClass.getContainingClass();
        }

        ArrayList<String> orderedComponents = new ArrayList<>(components.size());
        while (!components.empty()) {
            orderedComponents.add(components.pop());
        }

        return String.join(".", orderedComponents);
    }
}
