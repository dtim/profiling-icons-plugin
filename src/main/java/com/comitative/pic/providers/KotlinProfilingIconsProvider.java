package com.comitative.pic.providers;

import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClassInitializer;
import org.jetbrains.kotlin.psi.KtNamedFunction;

public class KotlinProfilingIconsProvider extends BaseProfilingIconsProvider {

    private static final Logger LOG = Logger.getInstance(KotlinProfilingIconsProvider.class);

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo<?> lineMarkerInfo = null;

        if (element instanceof LeafPsiElement) {
            PsiElement parent = element.getParent();
            if ((parent instanceof KtNamedFunction && element.equals(((KtNamedFunction) parent).getIdentifyingElement()))
                    || (parent instanceof KtClassInitializer && (element.getText().matches("^init$")))) {
                StatisticsService statisticsService = getStatisticsService(element.getProject());
                if (statisticsService != null) {
                    lineMarkerInfo = new LineMarkerInfo<>(
                            element,
                            element.getTextRange(),
                            getImpactIcon(0.1),
                            elt -> elt.getText() + ": " + "10% " + element.getClass(),
                            null,
                            GutterIconRenderer.Alignment.CENTER);
                }
            }
        }

        return lineMarkerInfo;
    }
}
