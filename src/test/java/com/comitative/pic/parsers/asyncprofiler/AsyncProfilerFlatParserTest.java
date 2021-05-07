package com.comitative.pic.parsers.asyncprofiler;

import com.comitative.pic.MethodReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.parsers.SnapshotParser;
import com.comitative.pic.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AsyncProfilerFlatParserTest {

    @Test
    void parseSummaryLine_shouldReturnLineComponents() {
        AsyncFlatParser parser = new AsyncFlatParser();
        Optional<TimeRecord> result = parser.parseSummaryLine(SAMPLE_SUMMARY_LINE);
        assertTrue(result.isPresent(), "Parse result should be present");
        result.ifPresent(timeRecord -> {
            MethodReference method = timeRecord.getMethodReference();
            assertEquals("com.comitative.pt.MainKt.main_[j]", method.getMethodName());
            checkTimeRecordValues(timeRecord, 0.0965, 8420496037L, 842L);
        });
    }

    @Test
    void withSampleFile_shouldParseAllSummaryLines() {
        try {
            SnapshotParser parser = new AsyncFlatParser();
            try (InputStream report = openResourceFile("async_flat_sample_01.txt")) {
                List<TimeRecord> stats = parser.parseStream(report);
                assertNotNull(stats, "Method call time statistics should not be null");
                assertEquals(73, stats.size());

                MethodReference last = MethodReference.builder().setMethodName("java.util.Arrays.copyOf_[j]").build();
                checkTimeRecordValues(
                        stats.get(stats.size() - 1),
                        0.0001, 9997228L, 1L);
            }
        } catch (IOException e) {
            fail("Input/output error while reading the report");
        }
    }

    private @NotNull InputStream openResourceFile(String resourceName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(resourceName);
        if (resource != null) {
            File file = new File(resource.getFile());
            return new FileInputStream(file);
        } else {
            throw new IOException("Test resource " + resourceName + " not found");
        }
    }

    private void checkTimeRecordValues(TimeRecord record, double relativeTime, long absoluteTime, long sampleCount) {
        assertNotNull(record, "Time record should not be null");
        assertEquals(relativeTime, record.getRelativeTime(), "Relative time mismatch");
        assertEquals(absoluteTime, record.getAbsoluteTime(), "Absolute time mismatch");
        assertEquals(sampleCount, record.getSampleCount(), "Sample count mismatch");
    }

    private static final String SAMPLE_SUMMARY_LINE =
            "  8420496037    9.65%      842  com.comitative.pt.MainKt.main_[j]";
}