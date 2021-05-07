package com.comitative.pic.parsers;

import com.comitative.pic.TimeRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Parser interface.
 *
 * Each specific parser should implement this interface. The ParserFactory
 * abstract factory creates a matching parser instance by request when the user
 * loads a profiling report file.
 */
public interface SnapshotParser {
    @NotNull String getFormatKey();


    @NotNull String getFormatName();

    /**
     * Parse a stream that contains the profiling data and return the data
     * as a mapping from identifiers of code blocks (e.g., methods) to their execution statistics.
     *
     * @param inputStream a input stream to parse
     * @return a mapping from code block identifiers to their profiling statistics
     */
    @NotNull List<TimeRecord> parseStream(@NotNull InputStream inputStream) throws IOException;
}
