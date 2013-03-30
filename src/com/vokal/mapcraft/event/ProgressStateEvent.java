package com.vokal.mapcraft.event;

public class ProgressStateEvent {
    private static int sOnCount;

    private ProgressStateEvent() {

    }

    public synchronized boolean isRunning() {
        return sOnCount > 0;
    }

    public synchronized static ProgressStateEvent start() {
        sOnCount++;
        return new ProgressStateEvent();
    }

    public synchronized static ProgressStateEvent end() {
        sOnCount--;
        if (sOnCount < 0) {
            sOnCount = 0;
        }
        return new ProgressStateEvent();
    }
}
