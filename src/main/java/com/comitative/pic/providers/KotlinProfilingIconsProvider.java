package com.comitative.pic.providers;

import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

import java.util.Collection;
import java.util.List;

public class KotlinProfilingIconsProvider extends BaseProfilingIconsProvider {

    private static final Logger LOG = Logger.getInstance(KotlinProfilingIconsProvider.class);

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(
            @NotNull List<? extends PsiElement> elements,
            @NotNull Collection<? super LineMarkerInfo<?>> result) {
        StatisticsService statistics = null;
        if (!elements.isEmpty()) {
            PsiElement firstElement = elements.get(0);
            if (firstElement != null) {
                statistics = firstElement.getProject().getService(StatisticsService.class);
            }
        }

        if (statistics != null) {
            for (PsiElement element : elements) {
                if (element instanceof KtNamedFunction) {
                    KtNamedFunction function = (KtNamedFunction) element;
                    KtClass functionClass = getKtClass(function);
                    KtFile file = function.getContainingKtFile();

                    StringBuilder sb = new StringBuilder();
                    if (functionClass != null) {
                        FqName functionClassFqName = functionClass.getFqName();
                        if (functionClassFqName != null) {
                            sb
                                    .append("{")
                                    .append(functionClassFqName)
                                    .append("/")
                                    .append(functionClassFqName.shortName())
                                    .append("}");
                        }
                    }

                    sb.append("[").append(file.getName()).append("]");

                    FqName functionFqName = function.getFqName();
                    if (functionFqName != null) {
                        sb
                                .append("(")
                                .append(functionFqName.asString())
                                .append("/")
                                .append(functionFqName.shortName().asString())
                                .append(")");
                    }

                    final String components = sb.toString();
                    PsiElement identifier = function.getIdentifyingElement();
                    if (identifier != null) {
                        String methodName = identifier.getText();
                        result.add(new LineMarkerInfo<>(
                                identifier,
                                identifier.getTextRange(),
                                getImpactIcon(0.1),
                                elt -> components,
                                null,
                                GutterIconRenderer.Alignment.LEFT));
                    }
                }
            }
        }
    }

    private @Nullable KtClass getKtClass(@NotNull KtNamedFunction function) {
        PsiElement parent = function.getParent();
        if (parent instanceof KtClassBody) {
            PsiElement grandParent = parent.getParent();
            if (grandParent instanceof KtClass) {
                return (KtClass) grandParent;
            }
        }

        return null;
    }

    /*
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo<?> lineMarkerInfo = null;

        if (element instanceof LeafPsiElement) {
            PsiElement parent = element.getParent();
            if ((parent instanceof KtNamedFunction && element.equals(((KtNamedFunction) parent).getIdentifyingElement()))
                        || (parent instanceof KtClassInitializer && (element.getText().matches("^init$")))) {
                    StatisticsService statisticsService = getStatisticsService(element.getProject());
                    if (statisticsService != null) {
                        String methodName = element.getText();
                        lineMarkerInfo = new LineMarkerInfo<>(
                                element,
                                element.getTextRange(),
                                getImpactIcon(0.1),
                                elt -> String.format("[%s] ::%s", "unknown", methodName),
                                null,
                                GutterIconRenderer.Alignment.CENTER);
                    }
                }
            }

        return lineMarkerInfo;
    }
     */
}
