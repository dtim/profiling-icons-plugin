package com.comitative.pic.parsers.asyncprofiler;

import com.comitative.pic.MethodReference;
import org.jetbrains.annotations.NotNull;

public final class AsyncProfilerMethodParser {
    public MethodReference parse(@NotNull String name) {
        return MethodReference.builder()
                .setMethodName(name)
                .build();
    }
}
