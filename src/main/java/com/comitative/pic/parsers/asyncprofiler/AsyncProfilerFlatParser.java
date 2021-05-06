package com.comitative.pic.parsers.asyncprofiler;

import com.comitative.pic.MethodReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.parsers.ProfilerSnapshotParser;
import com.comitative.pic.utils.Pair;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import java.util.Optional;
import java.util.regex.*;

/**
 * A simple parser for the flat report format as generated by the Async Profiler.
 *
 * The parser currently skips stack traces part and only collects information from the summary block.
 * This behavior may change in the future versions.
 */
public final class AsyncProfilerFlatParser implements ProfilerSnapshotParser {

    private static final Logger log = Logger.getInstance(AsyncProfilerFlatParser.class);

    @Override
    public @NotNull Map<MethodReference, TimeRecord> parseStream(@NotNull InputStream inputStream) throws IOException {
        final Map<MethodReference, TimeRecord> statistics = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            parseSummaryLine(line).ifPresent(entry -> statistics.put(entry.getFirst(), entry.getSecond()));
            line = reader.readLine();
        }
        return statistics;
    }

    /**
     * Parse a summary line.
     *
     * This method is not a part of a public API and should be considered an implementation detail.
     * It has package visibility for testing purposes
     *
     * Expected format:
     * [absolute time: long, ns] [relative time:  double, %%]% [sample count: long] [method name, string]
     *
     * @param line line to parse
     * @return Optional pair (method reference, statistics), Optional.empty() if the line does not match the format
     */
    Optional<Pair<MethodReference, TimeRecord>> parseSummaryLine(String line) {
        Matcher matcher = SUMMARY_LINE_PATTERN.matcher(line);
        if (matcher.matches()) {
            try {
                MethodReference methodReference = MethodReference.builder().setMethodName(matcher.group(4)).build();
                TimeRecord timeRecord = new TimeRecord();
                timeRecord.setAbsoluteTime(Long.parseLong(matcher.group(1)));
                timeRecord.setRelativeTime(normalizePercent(Double.parseDouble(matcher.group(2))));
                timeRecord.setSampleCount(Long.parseLong(matcher.group(3)));
                return Optional.of(new Pair<>(methodReference, timeRecord));
            } catch (NumberFormatException e) {
                log.warn("invalid numeric value: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    private static double normalizePercent(double percentValue) {
        if (percentValue < 0.0) {
            return 0.0;
        } else if (percentValue > 100.0) {
            return 1.0;
        } else {
            return percentValue / 100.0;
        }
    }

    private static final Pattern SUMMARY_LINE_PATTERN =
            Pattern.compile("^\\s*(\\d+)\\s+(\\d+\\.\\d+)%\\s+(\\d+)\\s+(.+)\\s*$");

}
