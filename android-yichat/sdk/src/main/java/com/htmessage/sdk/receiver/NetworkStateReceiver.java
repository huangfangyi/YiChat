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

package com.htmessage.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.htmessage.sdk.manager.AndroidAdaptiveServerPingManager;
import com.htmessage.sdk.manager.HTPreferenceManager;
import com.htmessage.sdk.service.MessageService;


/**
 * Receives changes of the network state to start/stop the message service.
 * @author Daniele Ricci
 * @version 1.0
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkStateReceiver.class.getSimpleName();

    private static final int ACTION_START = 1;
    private static final int ACTION_STOP = 2;
    private static final int ACTION_TEST = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        int serviceAction = 0;

        final ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);

        // background data setting has changed
        if (ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED.equals(action)) {

            // if background data gets deactivated, just stop the service now
            if (!cm.getBackgroundDataSetting()) {
                Log.w(TAG, "background data disabled!");
                serviceAction = ACTION_STOP;
            }
            else {
                Log.w(TAG, "background data enabled!");
                // start message center
                serviceAction = ACTION_START;
                // notify ping manager that connection type has changed
                AndroidAdaptiveServerPingManager.onConnected();
            }
        }

        // connectivity status has changed
        else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            // TODO handle FAILOVER_CONNECTION

            final NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                Log.w(TAG, "network state changed!");

                if (info.getType() == ConnectivityManager.TYPE_MOBILE &&
                        !shouldReconnect(context)) {
                    Log.w(TAG, "throttling on mobile network");
                    return;
                }

                switch (info.getState()) {
                    case CONNECTED:
                        // test connection or reconnect
                        serviceAction = ACTION_TEST;
                        // notify ping manager that connection type has changed
                        AndroidAdaptiveServerPingManager.onConnected();
                        break;
                    case SUSPENDED:
                        Log.v(TAG, "suspending network traffic");
                        break;
                    default:
                        serviceAction = ACTION_STOP;
                        break;
                }
            }
            else {
                // no network info available
                serviceAction = ACTION_STOP;
            }
        }

        switch (serviceAction) {
            case ACTION_START:
                // start message center
                Intent intent1=new  Intent(context, MessageService.class).putExtra("TYPE", MessageService.TYPE_AWAKE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent1);
                } else {
                    context. startService(intent1);
                }
                break;
            case ACTION_STOP:

                // stop message center
               // MessageService2.stop(context);
                break;
            case ACTION_TEST:
                // connection test
                Intent intent2=new  Intent(context, MessageService.class).putExtra("TYPE", MessageService.TYPE_AWAKE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent2);
                } else {
                    context. startService(intent2);
                }
                break;
        }
    }

    private boolean shouldReconnect(Context context) {
        // check if some activity is holding to the message center
        // or there is a pending push notification
        if (HTPreferenceManager.getInstance().getUser()!=null)
            return true;

        long lastConnect = HTPreferenceManager.getInstance().getLastConnection(context);

        // no last connection registered
        if (lastConnect < 0)
            return true;

        long now = System.currentTimeMillis();
        long diff = 3000;

        return (now - lastConnect) >= diff;
    }

}
