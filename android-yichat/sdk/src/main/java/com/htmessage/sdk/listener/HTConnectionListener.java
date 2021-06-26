package com.htmessage.sdk.listener;

/**
 * Created by huangfangyi on 2017/2/13.
 * qq 84543217
 */

public interface HTConnectionListener {
     public static int CONFLICT=1;
     public static int NUMORL=0;
     public static int RECONNECT_ERROR=2;
     public void onConnected(int type) ;
     public void onDisconnected();
     public void onConflict();


}
