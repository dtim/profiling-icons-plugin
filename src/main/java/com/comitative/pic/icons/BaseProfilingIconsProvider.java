package com.comitative.pic.icons;

import com.comitative.pic.services.StatisticsService;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class BaseProfilingIconsProvider implements LineMarkerProvider {
    private static final double LOW_IMPACT_THRESHOLD = 0.1;
    private static final double MEDIUM_IMPACT_THRESHOLD = 0.4;

    protected Icon getImpactIcon(double relativeTime) {
        if (Double.compare(relativeTime, LOW_IMPACT_THRESHOLD) <= 0) {
            return AllIcons.Actions.ProfileBlue;
        } else if (Double.compare(relativeTime, MEDIUM_IMPACT_THRESHOLD) <= 0) {
            return AllIcons.Actions.ProfileYellow;
        } else {
            return AllIcons.Actions.ProfileRed;
        }
    }

    protected StatisticsService getStatisticsService(@NotNull Project project) {
        return project.getService(StatisticsService.class);
    }
}
