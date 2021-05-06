package com.comitative.pic;

public class TimeRecord {
    private double relativeTime;
    private long absoluteTime;
    private long sampleCount;

    public double getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(double relativeTime) {
        this.relativeTime = relativeTime;
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
