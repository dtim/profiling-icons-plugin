package com.comitative.pic;

import org.jetbrains.annotations.NotNull;

/**
 * A time usage record for a method or another code fragment.
 * Each time record contains a reference to the code fragment and a relative time (in normalized percent, [0.0 .. 1.0]).
 * Optionally the absolute time (in nanoseconds) and the sample count (in samples) can be added
 * (they are zero by default).
 *
 * @see CodeReference
 */
public class TimeRecord {
    private final @NotNull CodeReference codeReference;
    private final double relativeTime;
    private long absoluteTime;
    private long sampleCount;

    /**
     * A constructor that initializes the required fields.
     * @param codeReference a code reference for which the statistics is collected
     * @param relativeTime a relative time ([0.0 .. 1.0]) of the code fragment during the profiling
     */
    public TimeRecord(@NotNull CodeReference codeReference, double relativeTime) {
        this.codeReference = codeReference;
        this.relativeTime = relativeTime;
    }

    /**
     * A getter for the code reference.
     * @return the code fragment reference
     */
    public @NotNull
    CodeReference getCodeReference() {
        return codeReference;
    }

    /**
     * A getter for the relative time.
     * @return the relative time value
     */
    public double getRelativeTime() {
        return relativeTime;
    }

    /**
     * A getter for the absolute time (ns).
     * @return the absolute time value
     */
    public long getAbsoluteTime() {
        return absoluteTime;
    }

    /**
     * A setter for the absolute time (ns).
     *
     * @param absoluteTime the absolute time value
     */
    public void setAbsoluteTime(long absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    /**
     * A getter for the sample count.
     *
     * @return the sample count value
     */
    public long getSampleCount() {
        return sampleCount;
    }

    /**
     * A setter for the sample count.
     * @param sampleCount sample count value
     */
    public void setSampleCount(long sampleCount) {
        this.sampleCount = sampleCount;
    }
}
