package com.design.circutebreaker.service;

public class CircuitBreaker {

    enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private State state = State.CLOSED;

    private final int failureThreshold;
    private final long openDurationMillis;
    private final int halfOpenSuccessThreshold;
    private final int halfOpenMaxTrialRequests;

    private int consecutiveFailures = 0;
    private int halfOpenSuccessCount = 0;
    private int halfOpenTrialCount = 0;
    private long openUntilTimestamp = 0L;

    public CircuitBreaker(int failureThreshold,
                          long openDurationMillis,
                          int halfOpenSuccessThreshold,
                          int halfOpenMaxTrialRequests) {
        this.failureThreshold = failureThreshold;
        this.openDurationMillis = openDurationMillis;
        this.halfOpenSuccessThreshold = halfOpenSuccessThreshold;
        this.halfOpenMaxTrialRequests = halfOpenMaxTrialRequests;
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        if (state == State.OPEN) {
            if (now >= openUntilTimestamp) {
                moveToHalfOpen();
            } else {
                return false;
            }
        }

        if (state == State.HALF_OPEN) {
            if (halfOpenTrialCount >= halfOpenMaxTrialRequests) {
                return false;
            }
            halfOpenTrialCount++;
            return true;
        }

        return true;
    }

    public synchronized void recordSuccess() {
        switch (state) {
            case CLOSED:
                consecutiveFailures = 0;
                break;

            case HALF_OPEN:
                halfOpenSuccessCount++;
                if (halfOpenSuccessCount >= halfOpenSuccessThreshold) {
                    moveToClosed();
                }
                break;

            case OPEN:
                // Normally success should not be recorded in OPEN state
                break;
        }
    }

    public synchronized void recordFailure() {
        switch (state) {
            case CLOSED:
                consecutiveFailures++;
                if (consecutiveFailures >= failureThreshold) {
                    moveToOpen();
                }
                break;

            case HALF_OPEN:
                moveToOpen();
                break;

            case OPEN:
                // Already open, nothing more to do
                break;
        }
    }

    public synchronized State getState() {
        return state;
    }

    private void moveToClosed() {
        state = State.CLOSED;
        consecutiveFailures = 0;
        halfOpenSuccessCount = 0;
        halfOpenTrialCount = 0;
        openUntilTimestamp = 0L;
    }

    private void moveToOpen() {
        state = State.OPEN;
        openUntilTimestamp = System.currentTimeMillis() + openDurationMillis;
        consecutiveFailures = 0;
        halfOpenSuccessCount = 0;
        halfOpenTrialCount = 0;
    }

    private void moveToHalfOpen() {
        state = State.HALF_OPEN;
        halfOpenSuccessCount = 0;
        halfOpenTrialCount = 0;
    }
}
