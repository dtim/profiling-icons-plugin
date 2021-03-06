package com.comitative.pic.statistics;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.parsers.SnapshotParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Profiler statistics service.
 *
 * Provides a project-level storage for the profiling data and a snapshot parser repository.
 */
@Service
public class StatisticsService {
    private static final Logger LOG = Logger.getInstance(StatisticsService.class);
    private static final ExtensionPointName<SnapshotParser> EP_NAME =
            ExtensionPointName.create("com.comitative.pic.snapshotParser");

    private final AtomicReference<StatisticsDictionary> statistics = new AtomicReference<>(new StatisticsDictionary());

    /**
     * Retrieve a name-ordered list of registered parsers (snapshot file formats).
     * @return an immutable list of snapshot parsers
     */
    public @NotNull List<SnapshotParser> getParserList() {
        return Collections.unmodifiableList(
                EP_NAME.getExtensionList()
                        .stream()
                        .sorted(Comparator.comparing(SnapshotParser::getName))
                        .collect(Collectors.toList()));
    }

    /**
     * Load a snapshot from an input file and replace the old statistics with the new data.
     * @param file the file to load
     * @param parser the parser that will be used for processing the input file
     * @return true if the file is successfully loaded, false if there was an unrecoverable error during the processing
     */
    public boolean loadFile(@NotNull File file, @NotNull SnapshotParser parser) {
        try (InputStream inputStream = new FileInputStream(file)) {
            StatisticsDictionary dictionary = new StatisticsDictionary();
            dictionary.addAll(parser.parseStream(inputStream));
            // There is no need to check for versions or synchronize with possible other updater threads,
            // we just need to atomically replace the collection.
            statistics.set(dictionary);
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + file);
            return false;
        } catch (IOException e) {
            LOG.error("Input/output error while reading " + file + ": " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Retrieves the list of time records that correspond to the given code reference.
     * @param codeReference a code reference to search
     * @return a list of found time records (empty if no records were found)
     * @see TimeRecord
     * @see CodeReference
     */
    public @NotNull List<TimeRecord> getTimeRecords(@NotNull CodeReference codeReference) {
        LOG.trace("Requested time records for " + codeReference);
        return statistics.get().getTimeRecords(codeReference);
    }
}
