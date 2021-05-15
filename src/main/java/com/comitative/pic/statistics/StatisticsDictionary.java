package com.comitative.pic.statistics;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class StatisticsDictionary {
    private final HashMap<String, ArrayList<CodeReference>> classAliases;
    private final HashMap<CodeReference, ArrayList<TimeRecord>> records;

    StatisticsDictionary() {
        this.classAliases = new HashMap<>();
        this.records = new HashMap<>();
    }

    void add(@NotNull TimeRecord timeRecord) {
        CodeReference codeReference = timeRecord.getCodeReference();
    }

    List<TimeRecord> getTimeRecords(@NotNull CodeReference codeReference) {
        return Collections.emptyList();
    }
}
