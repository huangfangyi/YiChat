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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.htmessage.sdk.utils.SystemUtils;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ping.PingManager;


import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An adaptive ping manager using {@link AlarmManager}.
 * @author Daniele Ricci
 */
public class AndroidAdaptiveServerPingManager extends AbstractAdaptiveServerPingManager {

    private static final Logger LOGGER = Logger.getLogger(AndroidAdaptiveServerPingManager.class.getName());

    private static final String PING_ALARM_ACTION = "org.igniterealtime.smackx.ping.ACTION";

    private static final Map<XMPPConnection, AndroidAdaptiveServerPingManager> INSTANCES =
        new WeakHashMap<XMPPConnection, AndroidAdaptiveServerPingManager>();

    private static AlarmManager sAlarmManager;

    // we don't use the onConnectionCreated static initializer because we need an Android system context

    private static void ensureAlarmManager(Context context) {
        sAlarmManager = (AlarmManager) context.getApplicationContext()
            .getSystemService(Context.ALARM_SERVICE);
    }

    public static AndroidAdaptiveServerPingManager getInstanceFor(XMPPConnection connection, Context context) {
        PingManager pingManager= PingManager.getInstanceFor(connection);


        synchronized (INSTANCES) {
            AndroidAdaptiveServerPingManager serverPingWithAlarmManager = INSTANCES.get(connection);
            if (serverPingWithAlarmManager == null) {
                serverPingWithAlarmManager = new AndroidAdaptiveServerPingManager(connection, context);
                INSTANCES.put(connection, serverPingWithAlarmManager);
            }
            return serverPingWithAlarmManager;
        }



     }

    private AndroidAdaptiveServerPingManager(XMPPConnection connection, Context context) {
        super(connection);
        mContext = context;
        enable();
        onConnectionCompleted();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGGER.fine("Ping Alarm broadcast received");
            if (isEnabled()) {
               // MessageService2.ping(context);
            }
        }
    };

    private static final int MIN_ALARM_INTERVAL = 90 * 1000;

    private Context mContext;
    private PendingIntent mPendingIntent;

    private void setupOnConnectionCompleted() {
        if (mContext != null) {


            // setup first alarm using last value from preference
            setupPing(HTPreferenceManager.getInstance().getPingAlarmInterval(mContext, AlarmManager.INTERVAL_HALF_HOUR));
            // next increase can happen at least at next interval
            mNextIncrease = HTPreferenceManager.getInstance().getPingAlarmBackoff(mContext, mInterval);
            // reset internal variables
            mLastSuccess = 0;
            mLastSuccessInterval = 0;
        }
    }

    @Override
    public void onConnectionCompleted() {
        setupOnConnectionCompleted();
        mPingStreak = 0;
    }

    @Override
    public void onConnectivityChanged() {
        setupOnConnectionCompleted();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled && !isEnabled()) {
            enable();
            onConnectionCompleted();
        }
        else if (!enabled && isEnabled()) {
            disable();
        }

        super.setEnabled(enabled);
    }

    private synchronized void enable() {
        if (mPendingIntent == null) {
            mContext.registerReceiver(mReceiver, new IntentFilter(PING_ALARM_ACTION));
            ensureAlarmManager(mContext);
            mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(PING_ALARM_ACTION), 0);
        }
    }

    private synchronized void disable() {
        if (mPendingIntent != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            }
            catch (IllegalArgumentException e) {
                // for some strange reason, this can happen once in a while.
                // I can't see how it's possible given the protection of
                // mPendingIntent != null and the synchronized clause.
                // Could it be Android unregistering on its own?
                // Whatever, report the exception as non-fatal
            //    ReportingManager.logException(e);
                LOGGER.log(Level.WARNING, "Unable to unregister broadcast receiver", e);
            }
            ensureAlarmManager(mContext);
            sAlarmManager.cancel(mPendingIntent);
            mPendingIntent = null;
        }
    }

    @Override
    protected long getElapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    @Override
    protected synchronized void setupPing(long intervalMillis) {
        if (mPendingIntent != null) {
            sAlarmManager.cancel(mPendingIntent);
            mInterval = intervalMillis;

            // do not go beyond 30 minutes...
            if (mInterval > AlarmManager.INTERVAL_HALF_HOUR) {
                mInterval = AlarmManager.INTERVAL_HALF_HOUR;
            }
            // ...or less than 90 seconds
            else if (mInterval < MIN_ALARM_INTERVAL) {
                mInterval = MIN_ALARM_INTERVAL;
            }

            // save value to preference for later retrieval
            HTPreferenceManager.getInstance().setPingAlarmInterval(mContext, mInterval);

            // remove difference from last received stanza
            long interval = mInterval;
            XMPPConnection connection = connection();
            if (connection != null) {
                long now = System.currentTimeMillis();
                long lastStanza = connection.getLastStanzaReceived();
                if (lastStanza > 0)
                    interval -= (now - lastStanza);
            }

            LOGGER.log(Level.WARNING, "Setting alarm for next ping to " + mInterval + " ms (real " + interval + " ms)");

            if (SystemUtils.isOnWifi(mContext)) {
                // when on WiFi we can afford an inexact ping (carrier will not destroy our connection)
                sAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    interval, mPendingIntent);
            }
            else {
                // when on mobile network, we need exact ping timings
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    sAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval,
                        mPendingIntent);
                } else {
                    sAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval,
                        mPendingIntent);
                }
            }
        }
    }

    @Override
    protected void setNextIncreaseInterval(long interval) {
        super.setNextIncreaseInterval(interval);
        HTPreferenceManager.getInstance().setPingAlarmBackoff(mContext, mNextIncrease);
    }

    public static void onConnected() {
        synchronized (INSTANCES) {
            Iterator<Map.Entry<XMPPConnection, AndroidAdaptiveServerPingManager>> it = INSTANCES.entrySet().iterator();
            while (it.hasNext()) {
                AndroidAdaptiveServerPingManager instance = it.next().getValue();
                instance.onConnectivityChanged();
            }
        }
    }

    /**
     * Unregister the alarm broadcast receiver and cancel the alarm.
     */
    public static void onDestroy() {
        synchronized (INSTANCES) {
            Iterator<Map.Entry<XMPPConnection, AndroidAdaptiveServerPingManager>> it = INSTANCES.entrySet().iterator();
            while (it.hasNext()) {
                AndroidAdaptiveServerPingManager instance = it.next().getValue();
                instance.disable();
            }
        }
    }

}
