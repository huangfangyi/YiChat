/*
 * Kontalk Android client
 * Copyright (C) 2016 Kontalk Devteam <devteam@kontalk.org>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.htmessage.sdk.manager;

import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;

import java.util.logging.Logger;


/**
 * An adaptive ping manager.
 * @author Daniele Ricci
 */
public abstract class AbstractAdaptiveServerPingManager extends Manager {

    private static final Logger LOGGER = Logger.getLogger(AbstractAdaptiveServerPingManager.class.getName());

    protected boolean mEnabled = true;


    // All values are in milliseconds.

    /**
     * Current ping interval.
     */
    protected long mInterval;
    /**
     * Timestamp of last ping success.
     */
    protected long mLastSuccess;
    /**
     * Last successful ping interval.
     */
    protected long mLastSuccessInterval;
    /**
     * Interval for the next increase.
     */
    protected long mNextIncrease;
    /**
     * Successful ping streak. Used for computing when the next increase attempt
     * will be made.
     */
    protected long mPingStreak;

    protected AbstractAdaptiveServerPingManager(XMPPConnection connection) {
        super(connection);
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public abstract void onConnectionCompleted();

    public abstract void onConnectivityChanged();

    /**
     * Called by the ping failed listener.
     * It will half the interval for the next alarm.
     */
    public void pingFailed() {
        long interval;

        // ping has failed, reset ping streak
        mPingStreak = 0;

        if (mLastSuccessInterval > 0) {
            // we were trying an increase, go back to previous value
            interval = mLastSuccessInterval;
            // better use the previous value for a longer time :)
            setNextIncreaseInterval((long) (mNextIncrease * 1.5));
        }
        else {
            // half interval
            interval = mInterval / 2;
        }
        setupPing(interval);
    }

    /**
     * Called when a ping has succeeded.
     * In order to avoid a too much optimistic approach, we wait for at least
     * the supposed ping interval to pass before incrementing back the interval
     * for the next ping.
     */
    public void pingSuccess() {
        long nextAlarm = mInterval;
        long now = getElapsedRealtime();

        // we got a successful ping, increase ping streak
        mPingStreak += mInterval;

        if (mLastSuccessInterval > 0) {
            // interval increase was successful, reset backoff
            setNextIncreaseInterval(mInterval);

            // interval increase was successful
        }
        // try an increase only if we previously had a successful ping
        else if (mLastSuccess > 0) {
            if (mPingStreak >= mNextIncrease) {
                // we are trying an increase, store the last successful interval
                mLastSuccessInterval = mInterval;

                nextAlarm = (long) (mInterval * 1.5);
            }

            // do not increase interval for now
        }

        // remember last success
        mLastSuccess = now;

        setupPing(nextAlarm);
    }

    protected abstract void setupPing(long intervalMillis);

    protected abstract long getElapsedRealtime();

    protected void setNextIncreaseInterval(long interval) {
        // reset last successful interval
        mLastSuccessInterval = 0;
        // set and save next increase
        mNextIncrease = interval;
    }

}
