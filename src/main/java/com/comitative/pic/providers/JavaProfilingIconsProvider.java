package com.comitative.pic.providers;

import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Random;

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
        for(PsiElement element: elements) {
            if (element instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) element;
                for (PsiMethod psiMethod: psiClass.getMethods()) {
                    PsiElement identifier = psiMethod.getIdentifyingElement();
                    if (identifier != null) {
                        result.add(new LineMarkerInfo<>(
                                identifier,
                                identifier.getTextRange(),
                                getImpactIcon(0.1),
                                elt -> "[" +
                                        psiClass.getQualifiedName() +
                                        " / " +
                                        psiClass.getName() +
                                        "] " + psiMethod.getName(),
                                null,
                                GutterIconRenderer.Alignment.CENTER));
                    }
                }

            }
        }
    }

    /*
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo<?> lineMarkerInfo = null;
        PsiElement parent;

        if (element instanceof PsiIdentifier
                && (parent = element.getParent()) instanceof PsiMethod
                && element.equals(((PsiMethod) parent).getIdentifyingElement())) {
            StatisticsService statisticsService = getStatisticsService(element.getProject());
            if (statisticsService != null) {
                PsiClass methodClass = PsiTreeUtil.getParentOfType(parent, PsiClass.class);
                if (methodClass != null) {
                    LOG.info("Got a class: " + methodClass.getQualifiedName());
                    String className = methodClass.getQualifiedName();

                    String methodName = element.getText();
                    String fileName = element.getContainingFile().getVirtualFile().getPresentableName();

                    double relativeTime = rng.nextDouble();
                    final String tooltipText = String.format("%s: [%s] %s %4.1f%%",
                            fileName, className, methodName, relativeTime * 100.0);
                    lineMarkerInfo = new LineMarkerInfo<>(
                            (PsiIdentifier) element,
                            element.getTextRange(),
                            getImpactIcon(relativeTime),
                            elt -> elt.getText() + ": " + tooltipText,
                            null,
                            GutterIconRenderer.Alignment.CENTER);
                }
            }
        }

        return lineMarkerInfo;
    }
    */
}
