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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private static final Logger LOG = Logger.getInstance(StatisticsService.class);

    ReadWriteLock statisticsLock = new ReentrantReadWriteLock();
    Lock readLock = statisticsLock.readLock();
    Lock writeLock = statisticsLock.writeLock();

    private static final ExtensionPointName<SnapshotParser> EP_NAME =
            ExtensionPointName.create("com.comitative.pic.snapshotParser");

    private SnapshotParser getParser(@NotNull String formatKey) {
        for (SnapshotParser extensionParser: EP_NAME.getExtensionList()) {
            if (formatKey.equals(extensionParser.getKey())) {
                return extensionParser;
            }
        }

        return null;
    }

    private volatile List<TimeRecord> statistics = new ArrayList<>();

    public @NotNull List<SnapshotParser> getParserList() {
        return Collections.unmodifiableList(
                EP_NAME.getExtensionList()
                        .stream()
                        .sorted(Comparator.comparing(SnapshotParser::getName))
                        .collect(Collectors.toList()));
    }

    public boolean loadFile(@NotNull File file, @NotNull SnapshotParser parser) {
        try (InputStream inputStream = new FileInputStream(file)) {
            List<TimeRecord> records = new ArrayList<>(parser.parseStream(inputStream));
            writeLock.lock();
            statistics = records;
            writeLock.unlock();
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + file);
            return false;
        } catch (IOException e) {
            LOG.error("Input/output error while reading " + file + ": " + e.getMessage());
            return false;
        }

        return true;
    }

    public @NotNull List<TimeRecord> getTimeRecords(@NotNull CodeReference codeReference) {
        LOG.info("Requested time records for " + codeReference);
        readLock.lock();
        try {
            return new ArrayList<>();
        } finally {
            readLock.unlock();
        }
    }

}
