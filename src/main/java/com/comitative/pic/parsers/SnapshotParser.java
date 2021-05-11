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
public abstract class SnapshotParser {
    public abstract @NotNull String getKey();


    public abstract @NotNull String getName();

    /**
     * Parse a stream that contains the profiling data and return the data
     * as a mapping from identifiers of code blocks (e.g., methods) to their execution statistics.
     *
     * @param inputStream a input stream to parse
     * @return a mapping from code block identifiers to their profiling statistics
     */
    public abstract @NotNull List<TimeRecord> parseStream(@NotNull InputStream inputStream) throws IOException;

    /**
     * Produce a human-readable representation of the parser to use in combo boxes etc.
     * It is an override for Object.toString().
     *
     * This abstract base class just calls getName().
     *
     * @return String representation of the class in human-readable form (e.g., the parser/format name)
     */
    @Override
    public String toString() {
        return getName();
    }
}
