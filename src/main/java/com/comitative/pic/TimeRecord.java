package com.comitative.pic;

import org.jetbrains.annotations.NotNull;

public class TimeRecord {
    private final @NotNull MethodReference methodReference;
    private final double relativeTime;
    private long absoluteTime;
    private long sampleCount;

    public TimeRecord(@NotNull MethodReference methodReference, double relativeTime) {
        this.methodReference = methodReference;
        this.relativeTime = relativeTime;
    }

    public @NotNull MethodReference getMethodReference() {
        return methodReference;
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
