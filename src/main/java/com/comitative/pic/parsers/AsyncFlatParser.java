package com.comitative.pic.parsers;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import java.util.function.Function;
import java.util.regex.*;

/**
 * A simple parser for the flat report format as generated by the Async Profiler.
 *
 * The parser currently skips stack traces part and only collects information from the summary block.
 * This behavior may change in the future versions.
 */
public final class AsyncFlatParser extends SnapshotParser {

    private static final Logger LOG = Logger.getInstance(AsyncFlatParser.class);
    private static final String PARSER_NAME = "Async Profiler flat snapshot";

    /**
     * Return a human-readable parser name.
     *
     * @return String representation of the parser name
     * @see SnapshotParser#getName()
     */
    @Override
    public @NotNull String getName() {
        return PARSER_NAME;
    }

    /**
     * Parse an input stream matching the Async Profiler flat report format.
     * This version of the parser only reads cumulative (summary) records and skips any line it can't parse.
     * This behavior may change in the future.
     *
     * @param inputStream a input stream to parse
     * @return a list of time records from the input stream
     * @throws IOException if the stream can't be read
     * @see SnapshotParser#parseStream(InputStream)
     */
    @Override
    public @NotNull List<TimeRecord> parseStream(@NotNull InputStream inputStream) throws IOException {
        final List<TimeRecord> statistics = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            parseSummaryLine(line).ifPresent(statistics::add);
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
    Optional<TimeRecord> parseSummaryLine(String line) {
        Matcher matcher = SUMMARY_LINE_PATTERN.matcher(line);
        if (matcher.matches()) {
            try {
                long absoluteTime = Long.parseLong(matcher.group(1));
                double relativeTime = normalizePercent(Double.parseDouble(matcher.group(2)));
                long sampleCount = Long.parseLong(matcher.group(3));
                return parseMethodName(matcher.group(4)).flatMap(methodRef -> {
                    TimeRecord timeRecord = new TimeRecord(methodRef, relativeTime);
                    timeRecord.setAbsoluteTime(absoluteTime);
                    timeRecord.setSampleCount(sampleCount);
                    return Optional.of(timeRecord);
                });
            } catch (NumberFormatException e) {
                LOG.warn("invalid numeric value: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    /**
     * Parse name representations in the snapshot file and produce code references.
     *
     * @param name a name to parse.
     * @return an optional code reference, empty when the name is incorrect or should be ignored
     *
     * Notes:
     *   - This implementation of the parser uses a very simple string processing algorithm:
     *     split the name components by known separators and ignore the rest.
     *     This approach is very simple but not general enough to handle native names (e.g., JVM GC functions
     *     or system calls), method signatures, lambda abstractions etc.
     *   - This simple parsing algorithm will be replaced with a more sophisticated parser based on Grammar Kit
     *     upon the completion of a better PSI/snapshot name matching algorithm.
     */
    @NotNull Optional<CodeReference> parseMethodName(@NotNull String name) {
        ArrayList<String> components = new ArrayList<>();
        int len = name.length();
        if (name.endsWith(JAVA_METHOD_MARKER)) {
            len -= JAVA_METHOD_MARKER_LENGTH;
        }

        // A simple FSM to extract and store prefixes that correspond to the separate components of package,
        // class or method names. The processing terminates when:
        //   * the FSM reaches the end of the string,
        //   * the FSM finds an opening parenthesis (it starts the optional method signature that follows the name),
        //   * the FSM finds a character that can't occur in a Java/Kotlin name (it's probably the C/C++ name).
        int start = 0;
        while (start < len) {
            int sepIndex = takeUntil(name, start, len,
                    c -> c == '.' || c == '$' || c == '(' || NATIVE_NAME_MARKERS.contains(c));
            if (sepIndex < len) {
                char sep = name.charAt(sepIndex);
                if (NATIVE_NAME_MARKERS.contains(sep)) {
                    break;
                } else {
                    components.add(name.substring(start, sepIndex));
                    start = sepIndex + 1;
                    if (sep == '(') {
                        break;
                    }
                }
            } else {
                components.add(name.substring(start, len));
                break;
            }
        }

        // A "good" Java/Kotlin name should contain at least the class name (may be qualified) and the method name.
        // We currently ignore the names that don't correspond this pattern.
        //
        // Note: As an heuristic, we currently threat the last name component as a method name, and all preceding
        // components are joined together as a qualified class name. This approach is very simple but can't handle
        // lambdas and possibly other kinds of javac-generated names. As now the plugin does not support markers
        // inside methods anyway, I decided to choose simplicity over genericity.
        int numComponents = components.size();
        if (numComponents >= 2) {
            CodeReference ref = CodeReference.builder()
                    .setMethodName(components.get(numComponents - 1))
                    .setFqClassName(String.join(".", components.subList(0, numComponents - 1)))
                    .build();
            return Optional.of(ref);
        }

        return Optional.empty();
    }

    /**
     * Skip the string prefix till the first character that satisfies the predicate.
     * @param text the string to process
     * @param start initial position in the string (inclusive): all characters before it are ignored
     * @param end end position in the string (exclusive): the processing stops here (typically it is the text length)
     * @param predicate the condition to stop
     * @return the end position of the prefix
     */
    private int takeUntil(@NotNull String text, int start, int end, Function<Character, Boolean> predicate) {
        int i = start;
        int last = Math.min(end, text.length());
        while (i < last && !predicate.apply(text.charAt(i))) {
            i += 1;
        }
        return i;
    }

    /**
     * Convert the relative time in percent to the interval of [0.0 .. 1.0] so the other code can deal
     * with the uniform representation. The method truncates negative or too large values.
     *
     * @param percentValue a relative time value
     * @return truncated and normalized value
     */
    private static double normalizePercent(double percentValue) {
        if (percentValue < 0.0) {
            return 0.0;
        } else if (percentValue > 100.0) {
            return 1.0;
        } else {
            return percentValue / 100.0;
        }
    }

    // A regex to match summary lines in the report
    private static final Pattern SUMMARY_LINE_PATTERN =
            Pattern.compile("^\\s*(\\d+)\\s+(\\d+\\.\\d+)%\\s+(\\d+)\\s+(.+)\\s*$");

    // Characters that mark that the method name correspond to a native name (an internal JVM method,
    // a system call name, or a name from a C/C++ library). We currently just ignore these names
    // as there is no Java/Kotlin code where we could place a marker anyway.
    private static final Set<Character> NATIVE_NAME_MARKERS = new TreeSet<>(Arrays.asList(':', '/'));

    // The optional suffix that Async Profiler may add (with some command line options) to the method names
    // corresponding to the user code or Java/Kotlin library methods, and its length. We strip this suffix
    // during the processing.
    private static final String JAVA_METHOD_MARKER = "_[j]";
    private static final int JAVA_METHOD_MARKER_LENGTH = JAVA_METHOD_MARKER.length();
}
