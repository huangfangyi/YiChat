# 环信红包集成文档


## 1. redpacketlibrary简介

**redpacketlibrary**，在环信**sdk3.0**的基础上提供了收发红包和零钱页的功能。


## 2. redpacketlibrary目录说明

* libs ：包含了集成红包功能所依赖的jar包。(红包使用了glide库做图片加载，由于已经依赖了easeui这里不重复添加)
* res ：包含了红包SDK和聊天页面中的资源文件。（红包SDK相关以rp开头，聊天页面相关以em开头）
* utils ： 封装了收发红包的相关方法。
* widget ：聊天界面中的红包以及领取红包后的chatrow。
* RedPacketConstant.java 红包功能需要的常量。
* **注意: 由于RedPacketUtil类中使用了环信SDK中相关方法，redpacketlibrary依赖了easeui。**。

## 3. 集成步骤

###3.1 添加对红包工程的依赖
* ChatDemo的build.gradle中

```
 dependencies {
    //增加对redpacketlibrary的依赖
    compile project(':redpacketlibrary')
    compile project(':EaseUI')
    compile fileTree(dir: 'libs', include: '*.jar', exclude: 'android-support-multidex.jar')
}

```

* ChatDemo的setting.gradle中


```
include ':EaseUI', ':redpacketlibrary'

```
###3.2 ChatDemo清单文件中注册红包相关组件
```

        <uses-sdk
            android:minSdkVersion="9"
            android:targetSdkVersion="19"
            tools:overrideLibrary="com.easemob.redpacketui"
        />
        
    <!--红包相关界面start-->
        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPRedPacketActivity"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustPan|stateVisible"
            />

        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPDetailActivity"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustPan"
            />

        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPRecordActivity"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustPan"
            />

        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPWebViewActivity"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustResize|stateHidden"
            />
        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPChangeActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustResize|stateHidden"
            />

        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPBankCardActivity"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustPan|stateHidden"
            />

        <activity
            android:name="com.easemob.redpacketui.ui.activity.RPGroupMemberActivity"
            android:screenOrientation="portrait"
            android:theme="@style/horizontal_slide"
            android:windowSoftInputMode="adjustPan|stateHidden"
            />

        <activity
            android:name="com.alipay.sdk.app.H5PayActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:screenOrientation="behind"
            android:windowSoftInputMode="adjustResize|stateHidden"
            />
        <!--红包相关界面end-->    
```

###3.3 初始化红包上下文和token
* DemoApplication中初始化红包上下文。

```
    import com.easemob.redpacketsdk.RedPacket;
    
    @Override
    public void onCreate() {
        super.onCreate();
        RedPacket.getInstance().initContext(applicationContext);
        //打开Log开关 正式发布时请关闭
        RedPacket.getInstance().setDebugMode(true);
    }
```

### 3.4 ChatFragment中增加收发红包的功能 
* 需要导入的包

```
   import com.easemob.redpacketui.RedPacketConstant;
   import com.easemob.redpacketui.utils.RedPacketUtil;
   import com.easemob.redpacketui.widget.ChatRowRedPacket;
   import com.easemob.redpacketui.widget.ChatRowRedPacketAck;
```

* 添加红包相关常量


```
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
        
        private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
        
        private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
        
        private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
        
        private static final int ITEM_RED_PACKET = 16;
        
        private static final int REQUEST_CODE_SEND_RED_PACKET = 16;

```
* 添加红包入口


```
    @Override
    protected void registerExtendMenuItem() {
        //demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
        super.registerExtendMenuItem();
        //聊天室暂时不支持红包功能
        if (chatType != Constant.CHATTYPE_CHATROOM) {
            inputMenu.registerExtendMenuItem(R.string.attach_red_packet, R.drawable.em_chat_red_packet_selector, ITEM_RED_PACKET, extendMenuItemClickListener);
        }
    }
```

* 添加自定义chatrow到CustomChatRowProvider，详见ChatFragment中的CustomChatRowProvider。

* ContextMenuActivity的onCreate(）中屏蔽红包消息的转发和撤回功能。


```
   if (type == EMMessage.Type.TXT.ordinal()) {
            if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false) ||
                    message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                    || message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_MONEY_MESSAGE, false)){
                setContentView(R.layout.em_context_menu_for_location);
            }
        }
        
        
    if (isChatroom
                //red packet code : 屏蔽红包消息的撤回功能
                || message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
                //end of red packet code
            View v = (View) findViewById(R.id.forward);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }

```

* 进入发红包页面


```
    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
        ...
        case ITEM_RED_PACKET:
            RedPacketUtils.startRedPacketActivityForResult(this, chatType, toChatUsername, REQUEST_CODE_SEND_MONEY);
            break;
        default:
            break;
        }
        //不覆盖已有的点击事件
        return false;
    }
```

* 发送红包消息到聊天窗口

```
   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ...
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
            ...
            case REQUEST_CODE_SEND_MONEY:
                if (data != null){
                    sendMessage(RedPacketUtils.createRPMessage(getActivity(), data, toChatUsername));
                }
                break;
            default:
                break;
            }
        }     
    }
    
```

* 领取红包并发送回执消息到聊天窗口

```
    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)){
            RedPacketUtils.openRedPacket(getActivity(), chatType, message, toChatUsername, messageList);
            return true;
        }
        return false;
    }
```
* ChatFragment中群红包领取回执的处理(聊天页面)

```
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//获取自定义action
            if (action.equals(RedPacketConstant.REFRESH_GROUP_MONEY_ACTION)){
                RedPacketUtils.receiveRedPacketAckMessage(message);
                messageList.refresh();
            }
        }
        super.onCmdMessageReceived(messages);
    }
```

* MainActivity中群红包领取回执的处理(导航页面)

```
    import com.easemob.redpacketui.RedPacketConstant;
    import com.easemob.redpacketui.utils.RedPacketUtils;
    
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//获取自定义action
            if (action.equals(RedPacketConstant.REFRESH_GROUP_MONEY_ACTION) ){
                RedPacketUtils.receiveRedPacketAckMessage(message);
            }
        }
        refreshUIWithMessage();
    }
```
###3.5 群红包领取回执的全局处理


* DemoHelper中

```
    import com.easemob.redpacketui.RedPacketConstant;
    import com.easemob.redpacketui.utils.RedPacketUtils;
    
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            EMLog.d(TAG, "收到透传消息");
            //获取消息body
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            final String action = cmdMsgBody.action();//获取自定义action
            if(!easeUI.hasForegroundActivies()){
                if (action.equals(RedPacketConstant.REFRESH_GROUP_MONEY_ACTION)){
                    RedPacketUtils.receiveRedPacketAckMessage(message);
                    broadcastManager.sendBroadcast(new Intent(RedPacketConstant.REFRESH_GROUP_MONEY_ACTION));
                }
            }
        }
    }
```

* MainActivity中

```
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        intentFilter.addAction(RedPacketConstant.REFRESH_GROUP_MONEY_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
            ...
            if (action.equals(RedPacketConstant.REFRESH_GROUP_MONEY_ACTION)){
                if (conversationListFragment != null){
                    conversationListFragment.refresh();
                    }
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
```

###3.6 ConversationListFragment中对红包回执消息的处理

```
   import com.easemob.redpacketui.RedPacketConstant;

   @Override
    protected void setUpView() {
       ...
            conversationListView.setConversationListHelper(new EaseConversationListHelper() {
                    @Override
                    public String onSetItemSecondaryText(EMMessage lastMessage) {
                        if (lastMessage.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
                            String sendNick = lastMessage.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, "");
                            String receiveNick = lastMessage.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, "");
                            String msg;
                            if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                                msg = String.format(getResources().getString(R.string.msg_someone_take_red_packet), receiveNick);
                            } else {
                                if (sendNick.equals(receiveNick)) {
                                    msg = getResources().getString(R.string.msg_take_red_packet);
                                } else {
                                    msg = String.format(getResources().getString(R.string.msg_take_someone_red_packet), sendNick);
                                }
                            }
                            return msg;
                        }
                        return null;
                    }
                });
                super.setUpView();
    }
```

###3.7 添加零钱页的入口

* 在需要添加零钱的页面调用下面的方法


```
    import com.easemob.redpacketui.utils.RedPacketUtils;

    RedPacketUtils.startChangeActivity(getActivity());

```

* **提示: 如果不需要红包相关功能可全局搜索关键字red packet去掉红包相关的代码以及redpacketlibray**。










