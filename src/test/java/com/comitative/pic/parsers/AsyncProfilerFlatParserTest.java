package com.comitative.pic.parsers;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
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
            CodeReference method = timeRecord.getCodeReference();
            assertEquals("com.comitative.pt.MainKt", method.getFqClassName());
            assertEquals("main", method.getMethodName());
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

                // The sample file contains 73 summary lines, but only 21 of them refer to Java/Kotlin names
                assertEquals(21, stats.size());

                CodeReference last = CodeReference.builder().setMethodName("copyOf").build();
                checkTimeRecordValues(
                        stats.get(stats.size() - 1),
                        0.0001, 9997228L, 1L);
            }
        } catch (IOException e) {
            fail("Input/output error while reading the report");
        }
    }

    @Test
    void nameParser_shouldParseJavaNames() {
        AsyncFlatParser parser = new AsyncFlatParser();
        int testSetSize = SAMPLE_JAVA_NAMES.length;
        for (int i = 0; i < testSetSize; i++) {
            Optional<CodeReference> parseResult = parser.parseMethodName(SAMPLE_JAVA_NAMES[i]);
            assertTrue(parseResult.isPresent(), SAMPLE_JAVA_NAMES[i]);
            final String expectedClassName = SAMPLE_JAVA_NAME_EXPECTED_CLASSES[i];
            final String expectedMethodName = SAMPLE_JAVA_NAME_EXPECTED_METHODS[i];
            parseResult.ifPresent(ref -> {
                assertEquals(expectedClassName, ref.getFqClassName(), "Incorrect class name");
                assertEquals(expectedMethodName, ref.getMethodName(), "Incorrect method name");
            });
        }
    }

    @Test
    void nameParser_shouldIgnoreNativeNames() {
        AsyncFlatParser parser = new AsyncFlatParser();
        for (String name: SAMPLE_NATIVE_NAMES) {
            Optional<CodeReference> parseResult = parser.parseMethodName(name);
            assertFalse(parseResult.isPresent());
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

    // Note: if you modify this array, make sure to update SAMPLE_JAVA_NAME_CLASSES and SAMPLE_JAVA_NAME_METHODS below
    private static final String[] SAMPLE_JAVA_NAMES = {
            "MyClass.myMethod",
            "java.lang.Double.valueOf",
            "com.comitative.pt.MainKt.main_[j]",
            "Matrix$Wrapper.createRandom$1.apply",
            "com.baeldung.jni.HelloWorldJNI.sayHello()Ljava/lang/String;_[j]",
            "com.baeldung.jni.HelloWorldJNI.main([Ljava/lang/String;)V_[j]",
            "Matrix.<init>",
            "edu.coursera.parallel.OneDimAveragingPhaserTest.lambda$runParallelBarrier$0([D[DIIIILjava/util/concurrent/Phaser;)V_[j]"
    };

    // Note: this array should contain the same number of elements as SAMPLE_JAVA_NAMES
    private static final String[] SAMPLE_JAVA_NAME_EXPECTED_CLASSES = {
            "MyClass",
            "java.lang.Double",
            "com.comitative.pt.MainKt",
            "Matrix.Wrapper.createRandom.1",
            "com.baeldung.jni.HelloWorldJNI",
            "com.baeldung.jni.HelloWorldJNI",
            "Matrix",
            "edu.coursera.parallel.OneDimAveragingPhaserTest.lambda.runParallelBarrier"
    };

    // Note: this array should contain the same number of elements as SAMPLE_JAVA_NAMES
    private static final String[] SAMPLE_JAVA_NAME_EXPECTED_METHODS = {
            "myMethod",
            "valueOf",
            "main",
            "apply",
            "sayHello",
            "main",
            "<init>",
            "0"
    };

    private static final String[] SAMPLE_NATIVE_NAMES = {
            "Monitor::IUnlock(bool)",
            "/usr/lib/x86_64-linux-gnu/libc-2.31.so",
            "PhaseIdealLoop::is_dominator(Node*, Node*) [clone .part.0]",
            "clear_page_erms_[k]",
            "CardTableExtension::scavenge_contents_parallel(ObjectStartArray*, MutableSpace*, HeapWord*, PSPromotionManager*, unsigned int, unsigned int)",
            "SpinPause",
            "operator new(unsigned long)",
            "[unknown]"
    };

}