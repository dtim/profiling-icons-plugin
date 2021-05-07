package com.comitative.pic.services;

import com.comitative.pic.MethodReference;
import com.comitative.pic.TimeRecord;
import com.comitative.pic.parsers.SnapshotParser;
import com.comitative.pic.parsers.asyncprofiler.AsyncFlatParser;
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

@Service
public class StatisticsService {
    private static final Logger log = Logger.getInstance(StatisticsService.class);

    ReadWriteLock statisticsLock = new ReentrantReadWriteLock();
    Lock readLock = statisticsLock.readLock();
    Lock writeLock = statisticsLock.writeLock();

    private static final ExtensionPointName<SnapshotParser> EP_NAME =
            ExtensionPointName.create("com.comitative.pic.snapshotParser");

    private static final HashMap<String, SnapshotParser> PREDEFINED_PARSERS = new HashMap<>();
    static {
        AsyncFlatParser asyncFlatParser = new AsyncFlatParser();
        PREDEFINED_PARSERS.put(asyncFlatParser.getFormatKey(), asyncFlatParser);
    }

    private SnapshotParser getParser(@NotNull String formatKey) {
        SnapshotParser parser = PREDEFINED_PARSERS.get(formatKey);
        if (parser != null) {
            return parser;
        }

        for (SnapshotParser extensionParser: EP_NAME.getExtensionList()) {
            if (formatKey.equals(extensionParser.getFormatKey())) {
                return extensionParser;
            }
        }

        return null;
    }

    private volatile List<TimeRecord> statistics = new ArrayList<>();

    public boolean loadFile(@NotNull File file, @NotNull String formatKey) {
        SnapshotParser parser = getParser(formatKey);
        if (parser == null) {
            log.error("No parser found for the snapshot format: " + formatKey);
            return false;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            List<TimeRecord> records = new ArrayList<>(parser.parseStream(inputStream));
            writeLock.lock();
            statistics = records;
            writeLock.unlock();
        } catch (FileNotFoundException e) {
            log.error("File not found: " + file);
            return false;
        } catch (IOException e) {
            log.error("Input/output error while reading " + file + ": " + e.getMessage());
            return false;
        }

        return true;
    }

    public List<TimeRecord> getTimeRecords(@NotNull String className) {
        readLock.lock();
        try {
            return new ArrayList<>();
        } finally {
            readLock.unlock();
        }
    }

    public List<TimeRecord> getTimeRecords(@NotNull String className, @NotNull String methodName) {
        readLock.lock();
        try {
            return new ArrayList<>();
        } finally {
            readLock.unlock();
        }
    }

}
