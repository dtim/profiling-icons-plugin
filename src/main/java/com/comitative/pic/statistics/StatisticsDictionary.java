package com.comitative.pic.statistics;

import com.comitative.pic.CodeReference;
import com.comitative.pic.TimeRecord;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class StatisticsDictionary {
    private final HashMap<CodeReference, ArrayList<TimeRecord>> records;

    StatisticsDictionary() {
        this.records = new HashMap<>();
    }

    void add(@NotNull TimeRecord timeRecord) {
        CodeReference codeReference = timeRecord.getCodeReference();
        ArrayList<TimeRecord> matchingRecords = records.computeIfAbsent(codeReference, k -> new ArrayList<>());
        matchingRecords.add(timeRecord);
    }

    void addAll(@NotNull Collection<TimeRecord> timeRecords) {
        for (TimeRecord record: timeRecords) {
            add(record);
        }
    }

    List<TimeRecord> getTimeRecords(@NotNull CodeReference codeReference) {
        ArrayList<TimeRecord> matchingRecords = records.get(codeReference);
        if (matchingRecords == null) {
            CodeReference shortReference = CodeReference.builder()
                    .setFqClassName(codeReference.getShortClassName())
                    .setMethodName(codeReference.getMethodName())
                    .build();
            matchingRecords = records.get(shortReference);
        }

        if (matchingRecords != null) {
            return matchingRecords;
        } else {
            return Collections.emptyList();
        }
    }
}
