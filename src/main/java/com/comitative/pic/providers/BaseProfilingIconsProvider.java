package com.comitative.pic.providers;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.statistics.StatisticsService;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for line marker providers.
 * Contains the common code that Java and Kotlin line marker providers both use.
 * The set of methods and their implementation will probably change in the future.
 */
abstract class BaseProfilingIconsProvider implements LineMarkerProvider {

    // Gutter icon cache
    private final Map<String, ImpactIcon> iconCache = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Generate a gutter icon for a time record, or restore it from the cache.
     * @param timeRecord a time record to represent with a gutter icon
     * @return an icon
     */
    protected @NotNull Icon getImpactIcon(@NotNull TimeRecord timeRecord) {
        long impactClass = Math.round(timeRecord.getRelativeTime() * 100.0);
        if (impactClass < 1) {
            impactClass = 1;
        } else if (impactClass > 99) {
            impactClass = 99;
        }

        String iconText = String.format("%02d", impactClass);
        return iconCache.computeIfAbsent(iconText, text -> new ImpactIcon(text, JBColor.DARK_GRAY));
    }

    /**
     * Generate a tooltip text for a time record.
     *
     * @param timeRecord a time record to extract the tooltip information
     * @return a tooltip representation
     */
    protected @NotNull String getTooltipText(@NotNull TimeRecord timeRecord) {
        double scaledRelativeTime = timeRecord.getRelativeTime() * 100.0;
        long sampleCount = timeRecord.getSampleCount();
        return String.format(
                "%.02f%% (%d %s)",
                scaledRelativeTime,
                sampleCount,
                sampleCount == 1 ? "sample" : "samples");
    }

    /**
     * Produce a line marker for a method with a given identifying PSI element.
     *
     * The method implements a common name resolution algorithm for both Java and Kotlin programs.
     * The algorithm just takes the first time record that matches the qualified class name and method name.
     * It currently does not try to select the best matching record (in particular, we don't try to resolve
     * overloaded methods that differ in signature), nor does it resolve nested functions or lambda abstractions.
     *
     * The implementation is expected to change in the future.
     *
     * @param identifier a PSI element corresponding to the method identifier
     * @param className a fully qualified class name that will be matched with the statistics
     * @param methodName a short method name
     * @param <T> a technical type parameter for LineMarkerInfo
     * @return a LineMarkerInfo for the line marker or null if there is no statistical information for the method
     */
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
            final TimeRecord timeRecord = records.get(0);
            final Icon gutterIcon = getImpactIcon(timeRecord);
            final String tooltipText = getTooltipText(timeRecord);
            return new LineMarkerInfo<>(
                    identifier,
                    identifier.getTextRange(),
                    gutterIcon,
                    elt -> tooltipText,
                    null,
                    GutterIconRenderer.Alignment.CENTER);
        }

        return null;
    }

    // A common way to represent constructors in JVM names.
    // We use this constant for both Java and Kotlin code, so it is defined in their common base class.
    protected static final String CONSTRUCTOR_METHOD_NAME = "<init>";
}
