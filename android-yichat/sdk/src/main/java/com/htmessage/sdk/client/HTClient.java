package com.htmessage.sdk.client;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.listener.HTConnectionListener;
import com.htmessage.sdk.manager.ConversationManager;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.manager.HTDatabaseManager;
import com.htmessage.sdk.manager.HTPreferenceManager;
import com.htmessage.sdk.manager.MessageManager;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.CurrentUser;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.service.MessageService;
import com.htmessage.sdk.service.RemoteService;

import java.util.Iterator;
import java.util.List;


/**
 * Created by huangfangyi on 2016/12/9.
 * qq 84543217
 */

public class HTClient {

    /**
     * application context
     */
    private static Context appContext = null;
    private static final String TAG = HTClient.class.getSimpleName();
    private HTConnectionListener htConnectionListener;
    private MessageLisenter messageLisenter;
    private GroupLisenter groupLisenter;
    private static MessageManager messageManager;
    private static ConversationManager conversationManager;
    private static HTChatManager htChatManager;
    private static GroupManager groupManager;
    private static HTDatabaseManager HTDatabaseManager;
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private static HTClient htClient;


    public static HTClient getInstance() {
        if (htClient == null) {
            throw new RuntimeException("init it first");
        }
        return htClient;
    }

    public boolean isLogined() {
        CurrentUser currentUser = HTPreferenceManager.getInstance().getUser();
        if (currentUser != null && currentUser.getUsername() != null && currentUser.getPassword() != null) {
            return true;
        } else {
            return false;
        }


    }

    public void setMessageLisenter(MessageLisenter messageLisenter) {
        this.messageLisenter = messageLisenter;


    }

    public void setGroupLisenter(GroupLisenter groupLisenter) {
        this.groupLisenter = groupLisenter;
    }


    private ConnectionBroadcastReceiver connectionBroadcastReceiver;

    public HTClient(Context context) {

        HTOptions options = new HTOptions();
        options.setDebug(true);
        options.setDualProcess(true);

        appContext = context;
        initAllManager(options);
        initReceiver(appContext);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        Log.d(TAG, "process app name : " + processAppName);
        if (processAppName != null) {
            Log.d("processAppName-->", "not null");
        } else {
            Log.d("processAppName-->", " null");
        }
        Log.d(TAG, "getPackageName : " + appContext.getPackageName());
        if (processAppName != null && processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            //接收连接状态的广播
            Intent intent1=new Intent(appContext, MessageService.class).putExtra("TYPE", MessageService.TYPE_INIT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext. startForegroundService(intent1);
            } else {
                appContext. startService(intent1);
            }
            if (options.isDualProcess()) {
                Intent intent11= new Intent(appContext, RemoteService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appContext. startForegroundService(intent11);
                } else {
                    appContext. startService(intent11);
                }
            }
        }


    }

    public HTClient(Context context, HTOptions options) {
        appContext = context;
        initAllManager(options);
        initReceiver(appContext);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        Log.d(TAG, "process app name : " + processAppName);
        if (processAppName != null) {
            Log.d("processAppName-->", "not null");
        } else {
            Log.d("processAppName-->", " null");
        }
        Log.d(TAG, "getPackageName : " + appContext.getPackageName());
        if (processAppName != null && processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            //接收连接状态的广播
            Intent intent1=new Intent(appContext, MessageService.class).putExtra("TYPE", MessageService.TYPE_INIT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext. startForegroundService(intent1);
            } else {
                appContext. startService(intent1);
            }
            if (options.isDualProcess()) {
                Intent intent11= new Intent(appContext, RemoteService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appContext. startForegroundService(intent11);
                } else {
                    appContext. startService(intent11);
                }
            }


        }

    }


    private void initAllManager(HTOptions options) {
        HTPreferenceManager.init(appContext);
        if (options != null) {
            HTPreferenceManager.getInstance().setDualProcess(options.isDualProcess());
            HTPreferenceManager.getInstance().setDebug(options.isDebug());
        }


        if (HTPreferenceManager.getInstance().getUser() != null) {
            HTDatabaseManager.init(appContext);
            messageManager = new MessageManager(appContext);
            groupManager = new GroupManager(appContext);
            conversationManager = new ConversationManager(appContext);
            htChatManager = new HTChatManager(appContext);
        }

    }


    private void initAllManager() {
        HTPreferenceManager.init(appContext);

        if (HTPreferenceManager.getInstance().getUser() != null) {
            HTDatabaseManager.init(appContext);
            messageManager = new MessageManager(appContext);
            groupManager = new GroupManager(appContext);
            conversationManager = new ConversationManager(appContext);
            htChatManager = new HTChatManager(appContext);
        }


    }

    public ConversationManager conversationManager() {
        if (conversationManager == null) {
            conversationManager = new ConversationManager(appContext);
        }
        return conversationManager;
    }

    public MessageManager messageManager() {
        if (messageManager == null) {
            messageManager = new MessageManager(appContext);
        }
        return messageManager;
    }

    public GroupManager groupManager() {
        if (groupManager == null) {
            groupManager = new GroupManager(appContext);
        }
        return groupManager;
    }

    public HTChatManager chatManager() {
        if (htChatManager == null) {
            htChatManager = new HTChatManager(appContext);
        }
        return htChatManager;
    }

    private void initReceiver(Context context) {
//        mhanderThread=new HandlerThread("Message Thread");
//        mhanderThread.start();
//       handler =new Handler(mhanderThread.getLooper()){
//
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                HTMessage htMessage= (HTMessage) msg.obj;
//                messageManager().saveMessage(  htMessage, true);
//                if(messageLisenter!=null){
//                    messageLisenter.onHTMessage(htMessage);
//                }else {
//                    Intent htmessageInten=new Intent(SDKAction.HTMESSAGE);
//                    htmessageInten.putExtra("data",htMessage);
//                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(htmessageInten);
//                }
//
//            }
//        };


        connectionBroadcastReceiver = new ConnectionBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HTAction.ACTION_CONNECTION);
        intentFilter.addAction(HTAction.ACTION_LOGIN);
        intentFilter.addAction(HTAction.ACTION_LOGOUT);
        intentFilter.addAction(HTAction.ACTION_MESSAGE_CMD);
        intentFilter.addAction(HTAction.ACTION_MESSAGE_CALL);
        intentFilter.addAction(HTAction.ACTION_MESSAGE);
        intentFilter.addAction(HTAction.ACTION_REGISTER);
        intentFilter.addAction(HTAction.ACTION_GROUPLIST_LOADED);
        intentFilter.addAction(HTAction.ACTION_GROUP_DELETED);
        intentFilter.addAction(HTAction.ACTION_GROUP_LEAVED);


        LocalBroadcastManager.getInstance(context).registerReceiver(connectionBroadcastReceiver, intentFilter);
    }

    public static void init(Context context) {
        if (htClient == null) {
            htClient = new HTClient(context);
        }
    }

    public static void init(Context context, HTOptions htOptions) {
        if (htClient == null) {
            htClient = new HTClient(context, htOptions);
        }
    }

    private HTCallBack htCallBack;

    public void login(String username, String password, HTCallBack htCallBack) {
        this.htCallBack = htCallBack;
        Intent intent = new Intent(appContext, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_LOGIN);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext. startForegroundService(intent);
        } else {
            appContext. startService(intent);
        }

    }

    private HTCallBack logoutCallBack;

    public void logout(HTCallBack htCallBack) {
        this.logoutCallBack = htCallBack;
        Intent intent = new Intent(appContext, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_LOGOUT);
      //  appContext.startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext. startForegroundService(intent);
         } else {
            appContext. startService(intent);
         }
    }

    private HTCallBack registerCallBack;

    public void register(String username, String password, HTCallBack htCallBack) {
        this.registerCallBack = htCallBack;
        Intent intent = new Intent(appContext, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_REGISTER);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext. startForegroundService(intent);
        } else {
            appContext. startService(intent);
        }
    }

    public interface HTCallBack {
        void onSuccess();

        void onError();
    }

    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(connectionBroadcastReceiver);

    }

    public void addConnectionListener(HTConnectionListener htConnectionListener) {
        this.htConnectionListener = htConnectionListener;

    }

    class ConnectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            String action = intent.getAction();
            if (action.equals(HTAction.ACTION_CONNECTION)) {
                boolean isConnected = intent.getBooleanExtra("state", true);
                int code = intent.getIntExtra("code", HTConnectionListener.NUMORL);
                 if (htConnectionListener != null) {
                     if (isConnected) {
                        htConnectionListener.onConnected(code);
                    } else {

                        if (code == HTConnectionListener.CONFLICT) {

                            htConnectionListener.onConflict();
                        } else {
                            htConnectionListener.onDisconnected();

                        }


                    }
                }


            } else if (action.equals(HTAction.ACTION_LOGIN)) {
                boolean loginResult = intent.getBooleanExtra("state", false);
                if (loginResult) {
                    initAllManager();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//                                @SuppressLint("MissingPermission") String DEVICE_ID = tm.getDeviceId();
//                                new HTHttpUtils(context).updateDeviceId(HTPreferenceManager.getInstance().getUser().getUsername(), DEVICE_ID, new HTHttpUtils.HttpCallBack() {
//                                    @Override
//                                    public void onResponse(JSONObject jsonObject) {
//                                        if(jsonObject!=null){
//                                            Log.d(TAG, "updateDeviceId--->"+jsonObject.toJSONString());
//
//                                        }
////                                        String result = "";
////                                        if (jsonObject != null) {
////                                            result = jsonObject.toJSONString();
////                                        }
//                                     }
//
//                                    @Override
//                                    public void onFailure(String errorMsg) {
//                                        Log.d(TAG, "updateDeviceId--->no errorMsg");
//                                     }
//
//
//                                });
//                            } catch (RuntimeException e) {
//
//                                Log.d(TAG, "updateDeviceId--->no perssion");
//                            }
//
//
//                        }
//                    }).start();

                }
                if (htCallBack != null) {
                    if (loginResult) {
                        htCallBack.onSuccess();


                    } else {
                        htCallBack.onError();
                    }
                }

            } else if (action.equals(HTAction.ACTION_LOGOUT)) {
                boolean logoutResult = intent.getBooleanExtra("state", false);
                if (logoutCallBack != null) {
                    if (logoutResult) {
                        logoutCallBack.onSuccess();
                    } else {
                        logoutCallBack.onError();
                    }
                }
            } else if (action.equals(HTAction.ACTION_MESSAGE)) {


                final HTMessage htMessage = intent.getParcelableExtra("data");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (messageLisenter != null) {
                            messageLisenter.onHTMessage(htMessage);
                        } else {
                            messageManager().saveMessage(htMessage, true);
                            Intent htmessageInten = new Intent(SDKAction.HTMESSAGE);
                            htmessageInten.putExtra("data", htMessage);
                            LocalBroadcastManager.getInstance(appContext).sendBroadcast(htmessageInten);
                        }
//
                    }
                }).start();

//
//                //   Log.d("HTClient:","Received HTMessage:"+htMessage.toXmppMessageBody());
//                Message message = handler.obtainMessage();
//                message.obj = htMessage;
//                handler.sendMessage(message);


            } else if (action.equals(HTAction.ACTION_MESSAGE_CMD)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CmdMessage cmdMessage = intent.getParcelableExtra("data");
                        Log.d("HTClient:", "Received cmdMessage:" + cmdMessage.toXmppMessage());
                        if (messageLisenter != null) {
                            messageLisenter.onCMDMessgae(cmdMessage);
                        } else {
                            Intent htmessageInten = new Intent(SDKAction.HTMESSAGE_CMD);
                            htmessageInten.putExtra("data", cmdMessage);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }


                    }
                }).start();
            } else if (action.equals(HTAction.ACTION_MESSAGE_CALL)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CallMessage callMessage = intent.getParcelableExtra("data");
                        Log.d("HTClient:", "Received cmdMessage:" + callMessage.toXmppMessage());
                        if (messageLisenter != null) {
                            messageLisenter.onCallMessgae(callMessage);
                        } else {
                            Intent htmessageInten = new Intent(SDKAction.HTMESSAGE_CMD);
                            htmessageInten.putExtra("data", callMessage);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                }).start();
            } else if (action.equals(HTAction.ACTION_REGISTER)) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = intent.getBooleanExtra("data", false);
                        if (registerCallBack != null) {
                            if (result) {
                                registerCallBack.onSuccess();
                            } else {
                                registerCallBack.onError();
                            }
                        }
                    }
                }).start();
            } else if (action.equals(HTAction.ACTION_GROUPLIST_LOADED)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (groupLisenter != null) {

                            groupLisenter.onGroupListLoaded();
                        }
                    }
                }).start();

            } else if (action.equals(HTAction.ACTION_GROUP_DELETED)) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        if (groupLisenter != null) {
                            String groupId = intent.getStringExtra("groupId");
                            groupLisenter.onGroupDestroyed(groupId);
                        }
                    }
                }).start();

            } else if (action.equals(HTAction.ACTION_GROUP_LEAVED)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (groupLisenter != null) {
                            String userId = intent.getStringExtra("userId");
                            String userNick = intent.getStringExtra("userNick");
                            String groupId = intent.getStringExtra("groupId");
                            groupLisenter.onGroupMemberLeaved(groupId,userId, userNick);
                        }
                    }
                }).start();

            }
        }
    }

//
//    private HandlerThread mhanderThread;
//
//    public void destroy() {
//
//        if (mhanderThread != null) {
//            mhanderThread.quit();
//        }
//    }
//
//    private Handler handler;


    public Context getContext() {

        return appContext;
    }


    public void sendConfict(){
         Intent intent = new Intent(HTAction.ACTION_CONNECTION);
        intent.putExtra("state", false);
        intent.putExtra("code", HTConnectionListener.CONFLICT);
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }
    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    private static String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        if (l == null) {
            //部分机型中getRunningAppProcesses方法失效
            return null;
        }
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }


    public void setOnNewMessageReceived() {


    }


    public interface MessageLisenter {
        void onHTMessage(HTMessage htMessage);

        void onCMDMessgae(CmdMessage cmdMessage);

        void onCallMessgae(CallMessage callMessage);


    }

    public interface GroupLisenter {
        void onGroupListLoaded();

        void onGroupDestroyed(String groupId);

        void onGroupMemberLeaved(String groupId, String userId, String userNick);
    }


    public void setNotificationShow(boolean show) {
        HTPreferenceManager.getInstance().setNotificationShow(show);
        if (show) {

            Intent intent1=new Intent(appContext, MessageService.class).putExtra("TYPE", MessageService.TYPE_NTIFICATION);

          Intent intent11=   new Intent(appContext, RemoteService.class).putExtra("TYPE", RemoteService.TYPE_NOTIFICATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               appContext. startForegroundService(intent1);
                appContext. startForegroundService(intent11);
            } else {
                appContext. startService(intent1);
                appContext. startService(intent11);
            }

        } else {

            Intent intent1=new Intent(appContext, MessageService.class).putExtra("TYPE", MessageService.TYPE_NTIFICATION_CANCEL);

            Intent intent11=   new Intent(appContext, RemoteService.class).putExtra("TYPE", RemoteService.TYPE_NOTIFICATION_CANCEL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext. startForegroundService(intent1);
                appContext. startForegroundService(intent11);
            } else {
                appContext. startService(intent1);
                appContext. startService(intent11);
            }

         }
    }

    private OSSClient oss;

    public OSSClient ossClient() {
        if (oss == null) {

            String endpoint = "";
            String accessKeyId = "";
            String accessKeySecret = "";
            if (SDKConstant.IS_LIMITLESS) {
                endpoint = HTPreferenceManager.getInstance().getEndpoint();
                accessKeyId = HTPreferenceManager.getInstance().getAccessKeyId();
                accessKeySecret = HTPreferenceManager.getInstance().getAccessKeySecret();

            } else {
                endpoint = SDKConstant.endpoint;
                accessKeyId = SDKConstant.accessKeyId;
                accessKeySecret = SDKConstant.accessKeySecret;
            }

            OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

            OSSLog.enableLog();

            oss = new OSSClient(appContext, endpoint, credentialProvider, conf);

        }

        return oss;
    }

    public void refreshIMConnection(){
Intent intent1=new Intent(appContext, MessageService.class).putExtra("TYPE", MessageService.TYPE_INIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent1);
        } else {
            appContext.startService(intent1);
        }

     }

}
