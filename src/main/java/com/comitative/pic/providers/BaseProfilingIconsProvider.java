package com.comitative.pic.providers;

import com.comitative.pic.TimeRecord;
import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseProfilingIconsProvider implements LineMarkerProvider {
    private static final double LOW_IMPACT_THRESHOLD = 0.1;
    private static final double MEDIUM_IMPACT_THRESHOLD = 0.4;

    private final ConcurrentHashMap<Long, ImpactGutterMark> iconCache = new ConcurrentHashMap<>();

    protected GutterMark getImpactGutterMark(TimeRecord timeRecord) {
        long impactClass = Math.round(timeRecord.getRelativeTime() * 100.0);
        if (impactClass < 0) {
            impactClass = 0;
        } else if (impactClass > 99) {
            impactClass = 99;
        }
        return iconCache.computeIfAbsent(impactClass, time -> new ImpactGutterMark(time, timeRecord));
    }

    protected StatisticsService getStatisticsService(@NotNull Project project) {
        return project.getService(StatisticsService.class);
    }
}
