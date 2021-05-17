package com.comitative.pic.providers;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseProfilingIconsProvider implements LineMarkerProvider {
    private final ConcurrentHashMap<Long, ImpactGutterMark> iconCache = new ConcurrentHashMap<>();

    protected GutterMark getImpactGutterMark(TimeRecord timeRecord) {
        long impactClass = Math.round(timeRecord.getRelativeTime() * 100.0);
        if (impactClass < 1) {
            impactClass = 1;
        } else if (impactClass > 99) {
            impactClass = 99;
        }
        return iconCache.computeIfAbsent(impactClass, time -> new ImpactGutterMark(time, timeRecord));
    }

    protected @Nullable <T extends PsiElement> LineMarkerInfo<T> createMarker(
            @NotNull T identifier,
            @NotNull String className,
            @NotNull String methodName) {

        CodeReference codeReference = CodeReference.builder()
                .setFqClassName(className)
                .setMethodName(methodName)
                .build();

        List<TimeRecord> records = identifier
                .getProject()
                .getService(StatisticsService.class)
                .getTimeRecords(codeReference);

        if (!records.isEmpty()) {
            // A bit of corner-cutting: let's pretend that the first record is the correct one.
            // It will really be the case in almost all cases except code blocks
            // lesser than a method (lambda etc).
            final GutterMark gutterMark = getImpactGutterMark(records.get(0));
            return new LineMarkerInfo<>(
                    identifier,
                    identifier.getTextRange(),
                    gutterMark.getIcon(),
                    elt -> gutterMark.getTooltipText(),
                    null,
                    GutterIconRenderer.Alignment.CENTER);
        }

        return null;
    }

    protected StatisticsService getStatisticsService(@NotNull Project project) {
        return project.getService(StatisticsService.class);
    }

    protected static final String CONSTRUCTOR_METHOD_NAME = "<init>";
}
