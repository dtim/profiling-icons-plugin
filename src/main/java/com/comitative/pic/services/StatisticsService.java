package com.comitative.pic.services;

import com.comitative.pic.MethodReference;
import com.comitative.pic.TimeRecord;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class StatisticsService {
    private static final Logger log = Logger.getInstance(StatisticsService.class);

    private volatile HashMap<MethodReference, TimeRecord> records = new HashMap<>();

    public void loadFromFile(File file) {
    }

    public Optional<TimeRecord> getTimeRecord(@NotNull MethodReference methodReference) {
        return Optional.empty();
    }

}
