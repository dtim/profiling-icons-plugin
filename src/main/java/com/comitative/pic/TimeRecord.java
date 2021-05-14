package com.comitative.pic;

import org.jetbrains.annotations.NotNull;

public class TimeRecord {
    private final @NotNull
    CodeReference codeReference;
    private final double relativeTime;
    private long absoluteTime;
    private long sampleCount;

    public TimeRecord(@NotNull CodeReference codeReference, double relativeTime) {
        this.codeReference = codeReference;
        this.relativeTime = relativeTime;
    }

    public @NotNull
    CodeReference getCodeReference() {
        return codeReference;
    }

    public double getRelativeTime() {
        return relativeTime;
    }

    public long getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(long absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public long getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(long sampleCount) {
        this.sampleCount = sampleCount;
    }
}
