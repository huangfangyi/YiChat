package com.htmessage.sdk.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.ProgressConnection;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.client.HTAction;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.listener.HTConnectionListener;
import com.htmessage.sdk.manager.HTPreferenceManager;
import com.htmessage.sdk.model.CurrentUser;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.Logger;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.sdk.utils.NetWorkUtil;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.SASLAnonymous;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import static com.htmessage.sdk.utils.MessageUtils.getTimeStamp;

/**
 * @author huangfangyi
 * @since 2016-05-05
 */
public class MessageService extends Service {
    private static final String TAG = MessageService.class.getSimpleName().toString();
    private static XMPPTCPConnection xmppConnection;


    private MyBinder myBinder;
    private MyServiceConnection myServiceConnection;
    private LocalBroadcastManager localBroadcastManager;
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_REGISTER = 2;
    public static final int TYPE_CHAT = 4;
    public static final int TYPE_INIT = 5;
    public static final int TYPE_LOGOUT = 6;
    public static final int TYPE_CHAT_CMD = 7;
    public static final int TYPE_AWAKE = 8;
    public static final int TYPE_NTIFICATION = 9;
    public static final int TYPE_NTIFICATION_CANCEL = 10;

    private long firstLoginMsgTime;
    private String loginUsername;
    private String loginPassword;
    private boolean isLogining = false;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;

    private NotificationManager manager = null;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private Handler xmppHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    //登录成功
                    final XMPPTCPConnection xmppConnectionNew = (XMPPTCPConnection) msg.obj;
                    Log.d("xmppConnectionNew-->",xmppConnectionNew.getHost());
//                    if (xmppConnection != null) {
//                        if (!xmppConnection.getStreamId().equals(xmppConnectionNew.getStreamId())) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    xmppConnection.disconnect();
//                                    xmppConnection=xmppConnectionNew;
//                                    android.os.Message message=xmppHandler.obtainMessage();
//                                    message.what=1001;
//                                    message.sendToTarget();
//                                }
//                            }).start();
//                        }
//
//                    } else {
                    if(xmppConnectionNew==null){
                        return;
                    }

                    if (xmppConnection == null || xmppConnectionNew.getStreamId()==null|| !xmppConnectionNew.getStreamId().equals(xmppConnection.getStreamId())) {
                        if (xmppConnection != null) {
                            xmppConnection.removeAsyncStanzaListener(stanzaListener);
                            xmppConnection.removeConnectionListener(connectionListener);
                        }
                        xmppConnection = xmppConnectionNew;
                        addLisenter(xmppConnection);
                    }

                    // }
                    timerHandler.removeCallbacks(runnable);
                    timerHandler.postDelayed(runnable, 30000);
                    scheduleJob();
                    break;

//                case 1001:
//                     addLisenter(xmppConnection);
//                    break;

                case 1003:
                    //断线重连
                    if (currentUser != null) {
                        login(currentUser.getUsername(), currentUser.getPassword(), true);
                    }

                    break;
            }
        }
    };

     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      boolean result= bindService(new Intent(this, RemoteService.class), myServiceConnection, Context.BIND_IMPORTANT);
 //     Logger.d("result----->",String.valueOf(result));

        if (intent != null) {
            int type = intent.getIntExtra("TYPE", 0);
            Logger.d("type----->", type + "");
            switch (type) {
                case TYPE_INIT:
//                    Logger.d(TAG, "onStartCommand:" + "TYPE_INIT");
//                    if (currentUser != null && currentUser.getUsername() != null && currentUser.getPassword() != null ) {
//                        timerHandler.removeCallbacks(runnable);
//                        timerHandler.postDelayed(runnable,30000);
////                        connectXmpp();
//                     }

                    break;
                case TYPE_LOGIN:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_LOGIN");

                    loginUsername = intent.getStringExtra("username");
                    loginPassword = intent.getStringExtra("password");
                    if (loginUsername != null && loginPassword != null) {
                        isLogining = true;
                        login(loginUsername, loginPassword, false);
                    } else {
                        sendLoginResult(false);
                    }
                    break;
                case TYPE_REGISTER:
//                    Logger.d(TAG, "onStartCommand:" + "TYPE_REGISTER");
//                    String username1 = intent.getStringExtra("username");
//                    String password1 = intent.getStringExtra("password");
//                    if (username1 != null && password1 != null) {
//                        register(username1, password1);
//                    }

                    break;
                case TYPE_LOGOUT:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_LOGOUT");
                    logoutXmpp();
                    break;
//                case TYPE_CHECK:
//                    Logger.d(TAG, "onStartCommand:" + "TYPE_CHECK");
//                    checkConnection();
//                   // timerHandler.post(runnable);
//                    break;
                case TYPE_CHAT:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_CHAT");
                    String chatTo = intent.getStringExtra("chatTo");
                    String body = intent.getStringExtra("body");
                    String msgId = intent.getStringExtra("msgId");
                    int chatType = intent.getIntExtra("chatType", 1);
                    if (chatTo != null && body != null) {
                        sendMessage(chatTo, body, msgId, chatType, false);
                    }

                    break;
                case TYPE_CHAT_CMD:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_CHAT_CMD");
                    String chatTo1 = intent.getStringExtra("chatTo");
                    String body1 = intent.getStringExtra("body");
                    String msgId1 = intent.getStringExtra("msgId");
                    int chatType1 = intent.getIntExtra("chatType", 1);
                    if (chatTo1 != null && body1 != null) {
                        sendMessage(chatTo1, body1, msgId1, chatType1, true);
                    }
                    break;
                case TYPE_AWAKE:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_AWAKE");
                    timerHandler.removeCallbacks(runnable);
                    timerHandler.post(runnable);
////                    if(getApplicationContext()==null){
//                        this.getApplication().onCreate();
////                    }

                    break;
                case TYPE_NTIFICATION:
                  //  setForeground(startId);
                    break;
                case TYPE_NTIFICATION_CANCEL:
                    cancelNotification();
                    break;


            }

        }

         setForeground(startId);
        return START_STICKY;
    }


    int notifyId = 30;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
     @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        Logger.d("MessageService32", "onCreate()");


        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        myServiceConnection = new MyServiceConnection();
        HTPreferenceManager.init(MessageService.this.getApplication());
        currentUser = HTPreferenceManager.getInstance().getUser();
        if (currentUser != null && currentUser.getUsername() != null && currentUser.getPassword() != null) {

//                        connectXmpp();
            login(currentUser.getUsername(), currentUser.getPassword(), true);
        }

        //   initConnection();
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());
        //  initBroadCastReceiver();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(1, new Notification());
//        }
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             networkCallback = new NetworkCallbackImpl();
             NetworkRequest.Builder builder = new NetworkRequest.Builder();
             NetworkRequest request = builder.build();
             connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
             connectivityManager.registerNetworkCallback(request, networkCallback);
         }

    }

     @TargetApi(Build.VERSION_CODES.LOLLIPOP)
     private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Logger.d(TAG, "NetworkCallbackImpl--->onAvailable");
            timerHandler.removeCallbacks(runnable);
            timerHandler.postDelayed(runnable, 5000);
            //  Toast.makeText(getBaseContext(), "onAvailable", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Logger.d(TAG, "NetworkCallbackImpl--->onLosing");

            //  Toast.makeText(getBaseContext(), "onLosing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d(TAG, "NetworkCallbackImpl--->onLost");
            //   Toast.makeText(getBaseContext(), "onLost", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            // Toast.makeText(getBaseContext(), "onCapabilitiesChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            //Toast.makeText(getBaseContext(), "onLinkPropertiesChanged", Toast.LENGTH_SHORT).show();
        }
    }

    //发送连续连接状态,至监听
    private void sendConnectionConState(boolean isConnected, int code) {
        Intent intent = new Intent(HTAction.ACTION_CONNECTION);
        intent.putExtra("state", isConnected);
        intent.putExtra("code", code);
        localBroadcastManager.sendBroadcast(intent);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeground(int startId) {
//
//        if (!HTPreferenceManager.getInstance().getNotificationShow()) {
//            return;
//        }
        manager = (NotificationManager)  getSystemService(Context.NOTIFICATION_SERVICE);
        //为了版本兼容  选择V4包下的NotificationCompat进行构造
        builder = new NotificationCompat.Builder(this);
        String title = "保持消息通知-APP";
        String content = "前台进程保活";
        if (!isZh()) {
            title = "Keep alive for Notification";
            content = "From -app";
        }

        Intent intentService=new Intent(this, MessageService.class).putExtra("TYPE", MessageService.TYPE_NTIFICATION_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getService(this,1,intentService,0);

       //  PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Integer.valueOf(userId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(content)
                .setSound(null)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(getApplicationInfo().icon);
        notification = builder.build();

        notification.icon = getApplicationInfo().icon;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        String id = "channel_03";
        String name = "APP";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mChannel.setSound(null, null);
            //     Toast.makeText(mContext, mChannel.toString(), Toast.LENGTH_SHORT).show();
            //  Log.i(TAG, mChannel.toString());
            manager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(content)

                     .setSmallIcon( getApplicationInfo().icon).build();



        }




       // manager.notify(Integer.valueOf(userId), notification);//发送通知

        startForeground(notifyId, notification);





















//        Notification.Builder builder = new Notification.Builder(this)
//                .setOngoing(true)
//                .setAutoCancel(false)
//                //设置图标
//                .setSmallIcon(this.getApplicationInfo().icon)
//
//                //设置标题
//                .setContentTitle(title)
//                //设置内容
//                .setContentText(contentText);
//
//        startForeground(startId, builder.build());
 //        if (HTPreferenceManager.getInstance().isDualProcess()) {

            Intent intent = new Intent(MessageService.this, RemoteService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }

       // }
    }

    private void cancelNotification() {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (locale.getDefault().toString().contains("zh"))

            return true;
        else
            return false;
    }

    private void login(final String username, final String password, boolean isAutoLogin) {
        if (isAutoLogin && xmppConnection != null && xmppConnection.isAuthenticated() && pingResult) {
            return;
        }

        final XMPPTCPConnection xmpptcpConnection = new XMPPTCPConnection(getConfig());
        xmpptcpConnection.addConnectionListener(connectionListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    xmpptcpConnection.connect().login(username,password);
                    //        xmpptcpConnection.login();
                    if (isLogining) {
                        sendLoginResult(true);
                    }
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                    if (isLogining) {
                        sendLoginResult(false);
                    }
//                    Logger.d(TAG, "connectXmpp" + "connect()--->e--->" + e.getMessage().toString());
//                    if ("Client is already connected".equals(e.getMessage().toString()) || "Client is already logged in".equals(e.getMessage().toString())) {
//                        if (isLogining) {
//                            login();
//                        }
//
//                    } else {
//                        if (isLogining) {
//                            sendLoginResult(false);
//                        }
//                    }
                }
            }
        }).start();

    }

//    private void initConnection() {
//        Log.d(TAG, "initConnection");
//
//        if (xmppConnection != null) {
//            if (xmppConnection.isConnected() && xmppConnection.isAuthenticated() && pingResult) {
//                return;
//            }
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    xmppConnection.disconnect();
//                    if (!xmppConnection.isSocketClosed() && xmppConnection.isConnected()) {
//                        xmppConnection.instantShutdown();
//                        xmppConnection = null;
//                    }
//                }
//            }).start();
//        }
//        xmppConnection = new XMPPTCPConnection(getConfig());
//        Roster roster = Roster.getInstanceFor(xmppConnection);
//        roster.setRosterLoadedAtLogin(false);
//
//        xmppConnection.addConnectionListener(connectionListener);
//        addLisenter(xmppConnection);
//    }


    boolean isFinished = true;

//    private void connectXmpp() {
//        if (!isFinished) {
//            if (isLogining) {
//                sendLoginResult(false);
//            }
//            Logger.d(TAG, "connectXmpp" + "isFinished--->" + isFinished + "-->isLogining--->" + isLogining);
//
//            return;
//        }
//        if (!NetWorkUtil.isNetworkConnected(this)) {
//            Logger.d(TAG, "connectXmpp" + "isNetworkConnected is error");
//            if (isLogining) {
//                sendLoginResult(false);
//            }
//
//            return;
//        }
//
//        if (xmppConnection != null) {
//            isFinished = false;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//
//                        xmppConnection.connect();
//                        Logger.d(TAG, "connectXmpp" + "-->connect()");
//                    } catch (SmackException | IOException | XMPPException e) {
//
//                        Logger.d(TAG, "connectXmpp" + "connect()--->e--->" + e.getMessage().toString());
//                        if ("Client is already connected".equals(e.getMessage().toString()) || "Client is already logged in".equals(e.getMessage().toString())) {
//                            if (isLogining) {
//                                login();
//                            }
//
//                        } else {
//                            if (isLogining) {
//                                sendLoginResult(false);
//                            }
//                        }
//                        e.printStackTrace();
//                    }
//                    isFinished = true;
//
//                }
//            }).start();
//
//        } else {
//            isFinished = true;
//            sendLoginResult(false);
//        }
//
//    }

    //初始化一个连接
    private XMPPTCPConnectionConfiguration getConfig() {
        String HOST = "";
        if (SDKConstant.IS_LIMITLESS) {

            HOST = HTPreferenceManager.getInstance().getIMServer();
        } else {
            HOST = SDKConstant.HOST;
        }

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(HOST)  // Name of your Host
                .setPort(SDKConstant.PORT)
                .setDebuggerEnabled(true)

                .setResource("mobile")
                //.setUsernameAndPassword(username, password)
                .setServiceName(SDKConstant.SERVER_NAME);
        XMPPTCPConnectionConfiguration configuration = builder.build();
        return configuration;
    }

//    //连接到服务器,1--仅仅连接不登录,给注册用的.
//    private void register(final String username, final String password) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (xmppConnection == null) {
//                        initConnection();
//                    }
//                    xmppConnection.connect();
//                    AccountManager accountManager = AccountManager.getInstance(xmppConnection);
//                    accountManager.sensitiveOperationOverInsecureConnection(true);
//                    accountManager.createAccount(username, password);
//                    xmppConnection.disconnect();
//                    sendRgisterResult(true);
//                } catch (SmackException | XMPPException | IOException e) {
//                    e.printStackTrace();
//                    Logger.d("e--->", e.getMessage().toString());
//                    if (e.getMessage().toString().equals("Client is already connected")) {
//                        AccountManager accountManager = AccountManager.getInstance(xmppConnection);
//                        accountManager.sensitiveOperationOverInsecureConnection(true);
//                        try {
//                            accountManager.createAccount(username, password);
//                            xmppConnection.disconnect();
//                            sendRgisterResult(true);
//                        } catch (SmackException.NoResponseException e1) {
//                            e1.printStackTrace();
//                            sendRgisterResult(false);
//                        } catch (XMPPException.XMPPErrorException e1) {
//                            e1.printStackTrace();
//                            sendRgisterResult(false);
//                        } catch (SmackException.NotConnectedException e1) {
//                            sendRgisterResult(false);
//                            e1.printStackTrace();
//                        }
//                        return;
//                    }
//                    sendRgisterResult(false);
//                }
//            }
//        }).start();
//    }


    //检查连接
    private synchronized void checkConnection() {

        if (!NetWorkUtil.isNetworkConnected(this)) {
            Logger.d(TAG, "checkConnection:" + "noNetworkConnected");
            return;
        }
        if (currentUser == null) {
            Logger.d(TAG, "checkConnection:" + "no user info");
            return;
        }

        if (xmppConnection == null) {
            login(currentUser.getUsername(), currentUser.getPassword(), true);

            return;
        }
        pingTest(new CallBack() {
            @Override
            public void onSuccess() {
                pingResult = true;


//                if (localBroadcastManager == null || broadcastReceiver == null) {
//                    initBroadCastReceiver();
//                    Logger.d(TAG, "checkConnection:broadcastReceiver error");
//
//                }


                //     sendState("ping  is success");
                Logger.d(TAG, "checkConnection:" + "pingTest onSuccess");
//                Logger.d(TAG, "checkConnection:isConnected" + String.valueOf(xmppConnection.isConnected()));
//                Logger.d(TAG, "checkConnection:isSocketClosed" + String.valueOf(xmppConnection.isSocketClosed()));
//                Logger.d(TAG, "checkConnection:isAuthenticated" + String.valueOf(xmppConnection.isAuthenticated()));
                // Logger.d(TAG, "checkConnection:" + String.valueOf(xmppConnection.isAuthenticated()));
            }

            @Override
            public void onFailure(String errorMessage) {
                pingResult = false;
                Logger.d(TAG, "checkConnection:" + "pingTest onFailure errorMessage: " + errorMessage);
//                if (errorMessage.equals("1000")) {
//                    if (xmppConnection != null) {
//                        xmppConnection.disconnect();
//                    }
//                }else {
//
//                }
                timerHandler.removeCallbacks(runnable);
                android.os.Message message = xmppHandler.obtainMessage();
                message.what = 1003;
                message.sendToTarget();


            }
        });


    }

    private boolean pingResult = true;

    private synchronized void pingTest(final CallBack callBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                PingManager pingManager = PingManager.getInstanceFor(xmppConnection);
                try {
                    boolean isOk = pingManager.pingMyServer();
                    if (isOk) {

                        Logger.d(TAG, "pingTest():" + "pingMyServer: isOk");
                        callBack.onSuccess();
                    } else {
                        Logger.d(TAG, "pingTest():" + "pingMyServer: isFailure");
                        callBack.onFailure("1000");
                    }

                } catch (SmackException.NotConnectedException e) {
                    Logger.d(TAG, "pingTest():" + "NotConnectedException: " + e.getMessage().toString());

                    callBack.onFailure("2000");
                }
            }
        }).start();
    }

    private Handler timerHandler = new Handler();
    private static final int TIME = 30000;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                checkConnection();
                timerHandler.postDelayed(this, TIME);
                Logger.d(TAG, "runnable:" + "to checkConnection() ");
            } catch (Exception e) {
                Logger.d(TAG, "runnable:" + "Exception:" + e.getMessage().toString());
                e.printStackTrace();
            }
        }
    };

    StanzaListener stanzaListener;

    private void addLisenter(final XMPPTCPConnection mXmpp) {

        Logger.d(TAG, "addLisenter:");
        ReconnectionManager.getInstanceFor(mXmpp).enableAutomaticReconnection();

        mXmpp.removeConnectionListener(connectionListener);
        mXmpp.addConnectionListener(connectionListener);
        Roster roster = Roster.getInstanceFor(mXmpp);
        roster.setRosterLoadedAtLogin(false);
        if (firstLoginMsgTime == 0) {
            firstLoginMsgTime = System.currentTimeMillis();
        }
        stanzaListener = new StanzaListener() {
            @Override
            public void processPacket(final Stanza packet) throws SmackException.NotConnectedException {

                Logger.d(TAG, "addLisenter: " + "processPacket-->" + packet.toString());
                //发送回执
                if (SDKConstant.IS_GROUP_OFFLINE) {
                    if (((Message) packet).getType() == Message.Type.groupchat) {
                        ExtensionElement time = packet.getExtension("delay", "urn:xmpp:delay");
                        final long timeStamp = getTimeStamp(time.toXML().toString());

                        Stanza stanza = new Stanza() {
                            @Override
                            public CharSequence toXML() {
                                return "<iq type='set' to='muc.app.im' id='" + System.currentTimeMillis() + "'><ack xmlns='http://jabber.org/protocol/muc#timestamp'>" + timeStamp + "</ack></iq>";
                            }
                        };
                        try {
                            mXmpp.sendStanza(stanza);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        Logger.d(TAG, "addLisenter: " + "send groupChat callback");
                    }

                }

                if (((Message) packet).getType() == Message.Type.chat) {
                    Stanza stanza = new Stanza() {
                        @Override
                        public CharSequence toXML() {
                            return "<message to='app.im'id='" + System.currentTimeMillis() + "'type='chat'> <received xmlns = 'urn:xmpp:receipts' id='" + packet.getStanzaId() + "'/> </message>";
                        }
                    };
                    mXmpp.sendStanza(stanza);
                    Logger.d(TAG, "addLisenter: " + "send sigleChat callback");
                }
                if (!packet.getFrom().equals("app.im") && !XmppStringUtils.parseBareJid(packet.getFrom()).equals(XmppStringUtils.parseBareJid(mXmpp.getUser()))) {

                    //离线消息
                    Logger.d(TAG, "addLisenter: " + "offline message");
                    long duration = System.currentTimeMillis() - firstLoginMsgTime;

                    Logger.d(TAG, "addLisenter: " + "duration---->" + duration);
                    if ((duration / 1000) < 2) {


                        MessageUtils.handleReceiveMessage((Message) packet, MessageService.this, true);


//                        //处理离线消息
                        Logger.d(TAG, "addLisenter: " + "handleReceiveMessage offline");
                    } else {
                        //直接处理
                        MessageUtils.handleReceiveMessage((Message) packet, MessageService.this, false);
                        Logger.d(TAG, "addLisenter: " + "handleReceiveMessage online");

                    }

                }
            }
        };

        StanzaFilter packetFilter = new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {

                if (stanza instanceof Message) {
                    return true;
                }
                return false;
            }
        };
        mXmpp.removeAsyncStanzaListener(stanzaListener);
        mXmpp.addAsyncStanzaListener(stanzaListener, packetFilter);
    }


//
//    boolean isTimerStarted=false;
//    Timer timer = new Timer();
//
//    TimerTask task = new TimerTask(){
//        @Override
//        public void run() {
//            Stanza stanza = new Stanza() {
//                @Override
//                public CharSequence toXML() {
//                    return "<iq type='set' to='muc.app.im' id='" + System.currentTimeMillis() + "'><ack xmlns='http://jabber.org/protocol/muc#timestamp'>" + pushGroupTime + "</ack></iq>";
//                }
//            };
//            try {
//                xmppConnection.sendStanza(stanza);
//                Logger.d(TAG, "addLisenter: " + "send groupChat callback");
//
//
//            } catch (SmackException.NotConnectedException e) {
//                e.printStackTrace();
//                Logger.d(TAG, "addLisenter: error " + "send groupChat callback ");
//
//            }
//
//        }
//    };


    private CurrentUser currentUser;


    private void logoutXmpp() {
        Logger.d(TAG, "logoutXmpp");

        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = null;
                timerHandler.removeCallbacks(runnable);
                HTPreferenceManager.getInstance().logout();

                if (xmppConnection != null) {
                    xmppConnection.removeConnectionListener(connectionListener);
                    xmppConnection.removeAsyncStanzaListener(stanzaListener);

                    if (xmppConnection.isConnected()) {
                        xmppConnection.disconnect();
                        xmppConnection.instantShutdown();
                    }
                    xmppConnection = null;
                }
                sendLogoutResult(true);
               // stopSelf();

            }
        }).start();

    }


    private void sendMessage(final String chatTo, final String body, final String msgId, final int chatType, final boolean isCustom) {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            sendMessageResult(false, msgId, isCustom, 0);
            Logger.d(TAG, "sendMessage" + " network is error");
            return;
        }
        if (xmppConnection != null && xmppConnection.isConnected() && xmppConnection.isAuthenticated()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendStanza(chatTo, body, msgId, chatType, isCustom);
                }
            }).start();

        } else {

            sendMessageResult(false, msgId, isCustom, 0);


        }
    }


    private void sendStanza(final String chatTo, final String body, final String msgId, final int chatType, final boolean isCustom) {

        String chatTypeString = "chat";
        final String chatToJid;
        if (chatType == 2) {
            chatTypeString = "groupchat";
            chatToJid = XmppStringUtils.parseBareJid(chatTo + SDKConstant.SERVER_DOMAIN_MUC);
        } else {
            chatToJid = XmppStringUtils.parseBareJid(chatTo + SDKConstant.SERVER_DOMAIN);
        }
        final String finalChatTypeString = chatTypeString;
        Stanza stanza = new Stanza() {
            @Override
            public CharSequence toXML() {


                try {
                    return "<message to='" + chatToJid
                            + "' id='" + msgId
                            + "' type='" + finalChatTypeString +
                            "'><body>" + URLEncoder.encode(body, "utf-8") + "</body><request xmlns='urn:xmpp:receipts'/></message>";
                } catch (UnsupportedEncodingException e) {
                    return "<message to='" + chatToJid
                            + "' id='" + msgId
                            + "' type='" + finalChatTypeString +
                            "'><body>" + URLEncoder.encode(body) + "</body><request xmlns='urn:xmpp:receipts'/></message>";
                }

            }
        };

        StanzaFilter replyFilter = new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                String str = stanza.toXML().toString();
                if (str.contains(msgId) && stanza.getFrom().equals("app.im")) {
                    return true;
                }
                return false;
            }
        };
        try {

            xmppConnection.sendStanzaWithResponseCallback(stanza, replyFilter, new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Logger.d(TAG, "sendStanza" + " sendStanzaWithResponseCallback--->" + packet.toXML().toString());

                    ExtensionElement time = ((Message) packet).getExtension("delay", "urn:xmpp:delay");
                    long timeStamp = System.currentTimeMillis();
                    if (time != null) {
                        timeStamp = getTimeStamp(time.toXML().toString());
                    }

                    sendMessageResult(true, msgId, isCustom, timeStamp);
                }
            }, new ExceptionCallback() {
                @Override
                public void processException(Exception exception) {
                    Logger.d(TAG, "sendStanza" + "sendStanzaWithResponseCallback exception--->" + exception.getMessage().toString());
                    sendMessageResult(true, msgId, isCustom, System.currentTimeMillis());
                }
            }, 3000);

        } catch (SmackException.NotConnectedException e) {
            sendMessageResult(false, msgId, isCustom, 0);
            Logger.d(TAG, "sendStanza" + "sendStanzaWithResponseCallback NotConnectedException--->" + e.getMessage().toString());
            e.printStackTrace();

        }
    }


    private void sendMessageResult(boolean success, String msgId, boolean isCustom, long timeStamp) {

        Intent intent = new Intent();
        if (isCustom) {

            intent.setAction(HTAction.ACTION_RESULT_MESSAGE_CMD);

        } else {
            intent.setAction(HTAction.ACTION_RESULT_MESSAGE);
        }
        intent.putExtra("time", timeStamp);
        intent.putExtra("data", msgId);
        intent.putExtra("result", success);
        localBroadcastManager.sendBroadcast(intent);

        if (!success) {
            checkConnection();
        }
    }

    private ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void connected(XMPPConnection connection) {
//            Logger.d(TAG, "connectionListener:" + "--->connected");
//            if(xmppConnection!=null){
//
//
//                Logger.d(TAG,"CURRENT_XMPP"+xmppConnection.getStreamId());
//                Logger.d(TAG,"CURRENT_XMPP1"+connection.getStreamId());
//            }else {
//                Logger.d(TAG,"CURRENT_XMPP2"+connection.getStreamId());
//             }
//            //如果是登录页面执行登陆操作
//            login();
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            android.os.Message message = xmppHandler.obtainMessage();
            message.obj = connection;
            message.what = 1000;
            message.sendToTarget();
            Logger.d(TAG, "connectionListener:" + "--->authenticated--->" + connection.toString() + "--->resumed--->" + resumed);
            sendConnectionConState(true, 0);
            timerHandler.removeCallbacks(runnable);
            timerHandler.post(runnable);
            if (isLogining) {
                sendLoginResult(true);
            }


        }

        @Override
        public void connectionClosed() {
            sendConnectionConState(false, 0);
            Logger.d(TAG, "connectionListener:" + "--->connectionClosed--->");
            if (isLogining) {
                sendLoginResult(false);
            }


        }

        @Override
        public void connectionClosedOnError(Exception e) {
            if (e.getMessage().contains("<stream:error><conflict xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>")) {

//                //如果有登录用户信息,并考虑自动重连机制
//                if (currentUser != null) {
//                    checkDeviceId(true);
//
//                }
                sendConnectionConState(false, HTConnectionListener.CONFLICT);
                //   logoutXmpp();

            } else {
                sendConnectionConState(false, HTConnectionListener.NUMORL);
            }
            if (isLogining) {
                sendLoginResult(false);
            }

            Logger.d(TAG, "connectionListener:" + "--->connectionClosedOnError--->e---->" + e.getMessage().toString());


        }

        @Override
        public void reconnectionSuccessful() {
            sendConnectionConState(true, 1);
            Logger.d(TAG, "connectionListener:" + "--->reconnectionSuccessful--->");
        }

        @Override
        public void reconnectingIn(int seconds) {

        }

        @Override
        public void reconnectionFailed(Exception e) {
            sendConnectionConState(false, HTConnectionListener.RECONNECT_ERROR);
            //   sendState("reconnectionFailed" + e.getMessage());
            Logger.d(TAG, "connectionListener:" + "--->reconnectionFailed--->e--->" + e.getMessage().toString());
        }
    };
//
//    private void login() {
//
//        if (!TextUtils.isEmpty(loginPassword) && !TextUtils.isEmpty(loginUsername)) {
//            try {
//                xmppConnection.login(loginUsername, loginPassword);
//                //登录后,启动自动断线重连
//                //reconnectionManager.enableAutomaticReconnection();
//                if (isLogining) {
//                    sendLoginResult(true);
//
//                }
//            } catch (XMPPException | SmackException | IOException e) {
//                Logger.d("e--->", e.getMessage().toString());
//                if (e.getMessage().toString().equals("Client is already logged in")) {
//                    if (isLogining) {
//                        sendLoginResult(true);
//
//                    }
//                    return;
//                }
//                if (isLogining) {
//                    Logger.d("false---->", "4444");
//
//                    sendLoginResult(false);
//                }
//                Logger.d("login--->", e.getMessage().toString());
//                stopSelf();
//                e.printStackTrace();
//            }
//
//            return;
//        } else {
//
//            //如果有登录用户信息,并考虑自动重连机制
//            if (currentUser != null) {
//                try {
//                    xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                    //登录后,启动自动断线重连
//                    //  reconnectionManager.enableAutomaticReconnection();
//                } catch (XMPPException | SmackException | IOException e) {
//                    if (e.getMessage().toString().equals("Client is already logged in")) {
//                        return;
//                    }
//                    e.printStackTrace();
//                }
//
//            }
//
//        }
//        scheduleJob();
//
//    }

//
//    private void checkDeviceId(boolean isConflict) {
//
//
//        new HTHttpUtils(this).getDeviceId(currentUser.getUsername(), new HTHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                if (jsonObject != null) {
//                    int code = jsonObject.getInteger("code");
//                    if (code == 1) {
//                        String deviceIdServer = jsonObject.getString("deviceId");
//                        if (!TextUtils.isEmpty(deviceIdServer) && !TextUtils.isEmpty(DEVICE_ID) && !DEVICE_ID.equals(deviceIdServer)) {
//                            Log.d(TAG, "deviceIdServer---"+deviceIdServer+"----"+DEVICE_ID);
//                            sendConnectionConState(false, HTConnectionListener.CONFLICT);
//                           Intent intent=  new Intent(MessageService32.this, MessageService32.class).putExtra("TYPE", TYPE_LOGOUT);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                 startForegroundService(intent);
//                            } else {
//                               startService(intent);
//                            }
//
//
//                            return;
//                        }
//                    }
//                    try {
//                        xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                        //登录后,启动自动断线重连
//                        //  reconnectionManager.enableAutomaticReconnection();
//                    } catch (XMPPException | SmackException | IOException e) {
//                        if (e.getMessage().toString().equals("Client is already logged in")) {
//                            return;
//                        }
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.d(TAG, "login getDeviceId json is null");
//                    try {
//                        xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                        //登录后,启动自动断线重连
//                        //  reconnectionManager.enableAutomaticReconnection();
//                    } catch (XMPPException | SmackException | IOException e) {
//                        if (e.getMessage().toString().equals("Client is already logged in")) {
//                            return;
//                        }
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//                Log.d(TAG, "login getDeviceId error"+SDKConstant.DEVICE_URL_GET);
//                try {
//                    xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                    //登录后,启动自动断线重连
//                    //  reconnectionManager.enableAutomaticReconnection();
//                } catch (XMPPException | SmackException | IOException e) {
//                    if (e.getMessage().toString().equals("Client is already logged in")) {
//                        return;
//                    }
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//    }

    /**
     * Executed when user clicks on SCHEDULE JOB.
     */
    private void scheduleJob() {
        Logger.d("KeepAliveService", "scheduleJob");
        if (HTPreferenceManager.getInstance().isDualProcess()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                JobInfo jobInfo = (new JobInfo.Builder(1, new ComponentName(this.getPackageName(), KeepAliveService.class.getName()))).setPeriodic(2000L).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
                jobScheduler.schedule(jobInfo);
            }
        }
    }


    private void sendLoginResult(boolean success) {
        if (success) {
            android.os.Message message = handler.obtainMessage();
            message.obj = success;
            message.what = 1000;
            handler.sendMessage(message);
        } else {
            Intent intent = new Intent(HTAction.ACTION_LOGIN);
            intent.putExtra("state", success);

            localBroadcastManager.sendBroadcast(intent);
            isLogining = false;
        }


    }

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    //登录成功
                    boolean success = (boolean) msg.obj;

                    if (isLogining) {
                        HTPreferenceManager.getInstance().setUser(loginUsername, loginPassword);
                        currentUser = HTPreferenceManager.getInstance().getUser();
                        loginUsername = null;
                        loginPassword = null;
                    }
                    // MainManager.initManagerList(getApplicationContext());

                    Intent intent = new Intent(HTAction.ACTION_LOGIN);
                    intent.putExtra("state", success);
                    localBroadcastManager.sendBroadcast(intent);
                    isLogining = false;


                    break;
              /*  case 2000:
                    //群消息离线接收完之后，向服务器告知最近的推送时间戳
                    timer.schedule(task,3000);
*/

            }

        }

    };

    private void sendRgisterResult(boolean success) {
        Intent intent = new Intent(HTAction.ACTION_REGISTER);
        intent.putExtra("data", success);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendLogoutResult(boolean success) {
        Intent intent = new Intent(HTAction.ACTION_LOGOUT);
        //  intent.putExtra("TYPE", ReceiverConstant.TYPE_LOGOUT_RESULT);
        intent.putExtra("state", success);
        localBroadcastManager.sendBroadcast(intent);
    }


    interface CallBack {
        void onSuccess();

        void onFailure(String errorMessage);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        if (myServiceConnection != null) {
            try {
                unbindService(myServiceConnection);
            } catch ( IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        }

//        if(localBroadcastManager!=null ){
//            localBroadcastManager.unregisterReceiver(broadcastReceiver);
//        }


        stopConnection();

        stopForeground(true);
        //  HTClient.getInstance().destroy();
        // getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        super.onDestroy();
    }


    private void stopConnection() {
        if (runnable != null) {
            timerHandler.removeCallbacks(runnable);
        }
        if (xmppConnection != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    xmppConnection.disconnect();
                    xmppConnection.instantShutdown();
                }
            }).start();

        }

    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Logger.d("castiel", "本地服务连接成功");
//            ProgressConnection iMyAidlInterface = ProgressConnection.Stub.asInterface(arg1);
//            try {
//                Log.i("LocalService", "connected with " + iMyAidlInterface.getProName());
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {


            // 连接出现了异常断开了，LocalCastielService被杀死了
            Logger.d("two service---->", "本地服务Local被干掉");
            //     Toast.makeText(ConService.this, "本地服务Local被干掉", Toast.LENGTH_LONG).show();
            // 启动LocalCastielService
            if (HTPreferenceManager.getInstance().isDualProcess()) {
                Intent intent = new Intent(MessageService.this, RemoteService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
                 bindService(new Intent(MessageService.this, RemoteService.class), myServiceConnection, Context.BIND_IMPORTANT);
            }
        }

    }

    class MyBinder extends ProgressConnection.Stub {

        @Override
        public String getProName() throws RemoteException {
            return  MessageService.class.getName();
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        myBinder = new MyBinder();
        return myBinder;

    }


}