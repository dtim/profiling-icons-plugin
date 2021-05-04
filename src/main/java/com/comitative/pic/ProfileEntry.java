package com.comitative.pic;

public final class ProfileEntry {
    private final double relativeTime;
    private final long absoluteTime;

    public ProfileEntry(long absoluteTime, double relativeTime) {
        this.relativeTime = relativeTime;
        this.absoluteTime = absoluteTime;
    }

    public double getRelativeTime() {
        return relativeTime;
    }

    public long getAbsoluteTime() {
        return absoluteTime;
    }
}
