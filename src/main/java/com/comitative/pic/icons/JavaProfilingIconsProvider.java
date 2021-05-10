package com.comitative.pic.icons;

import com.comitative.pic.TimeRecord;
import com.comitative.pic.services.StatisticsService;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class JavaProfilingIconsProvider extends BaseProfilingIconsProvider {

    private final Random rng = new Random();
    private static final Logger LOG = Logger.getInstance(JavaProfilingIconsProvider.class);

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
                    String methodName = element.getText(); // ((PsiMethod) parent).getName();

                    double relativeTime = rng.nextDouble();
                    final String tooltipText = String.format("%4.1f%%", relativeTime * 100.0);
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
}
