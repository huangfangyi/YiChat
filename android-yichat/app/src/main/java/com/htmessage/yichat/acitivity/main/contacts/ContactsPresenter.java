package com.htmessage.yichat.acitivity.main.contacts;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.MsgUtils;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class ContactsPresenter implements BaseContactsPresenter {

    private ContactsView contactsView;
    private List<User> users = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (contactsView == null) {
                return;

            }
            switch (msg.what) {

                case 1000:
                    //网络获取了好友列表
                    List<User> userList = (List<User>) msg.obj;
                    users.clear();
                    users.addAll(userList);
                    contactsView.refreshALL();

                    break;
                case 1001:

                    final User user = (User) msg.obj;
                    users.remove(user);
                    contactsView.refreshALL();
                    //考虑到在好友查找页面会删除数据。
                    if (UserManager.get().getFriends().contains(user.getUserId())) {
                        //通知本地其他地方关联处删除---目前只有会话如果有关于此人的会话需要删掉
                        LocalBroadcastManager.getInstance(contactsView.getBaseContext()).sendBroadcast(new Intent(IMAction.DELETE_FRIEND_LOCAL).putExtra("userId", user.getUserId()));

                        //发送CMD消息，告诉好友关系解除了

                        MsgUtils.getInstance().sendDeleteCMD(user.getUserId());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserManager.get().deleteMyFriends(user.getUserId());
                            }
                        }).start();
                    }
                    break;
                case 1002:
                    int resId = msg.arg1;
                    Toast.makeText(contactsView.getBaseContext(), resId, Toast.LENGTH_SHORT).show();
                    break;
                case 1003:
                    int index = msg.arg1;

                    //todo 头部有几个view
                    contactsView.refreshItem(index + 1);

                    break;

                case 1004:
                    int unread=msg.arg1;
                    contactsView.showInvitionCount(unread);
                    break;

                case 1005:
                    //接收到删除好友的透传

                    final User user1 = (User) msg.obj;
                    users.remove(user1);
                    contactsView.refreshALL();
                    //考虑到在好友查找页面会删除数据。
                    if (UserManager.get().getFriends().contains(user1.getUserId())) {
                        //通知本地其他地方关联处删除---目前只有会话如果有关于此人的会话需要删掉
                       // LocalBroadcastManager.getInstance(contactsView.getBaseContext()).sendBroadcast(new Intent(IMAction.DELETE_FRIEND_LOCAL).putExtra("userId", user1.getUserId()));
                        //发送CMD消息，告诉好友关系解除了MsgUtils.getInstance().sendDeleteCMD(user1.getUserId());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserManager.get().deleteMyFriends(user1.getUserId());
                            }
                        }).start();
                    }
                    break;


            }
        }
    };

    public ContactsPresenter(ContactsView view) {
        contactsView = view;
        contactsView.setPresenter(this);
    }


    @Override
    public List<User> getContactsListLocal() {


        return users;
    }

    //删除好友
    @Override
    public void deleteContacts(final User user) {
        JSONObject data = new JSONObject();
        data.put("friendId", user.getUserId());
        ApiUtis.getInstance().postJSON(data, Constant.URL_FRIEND_DELETE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = user;
                    message.sendToTarget();
                   // LocalBroadcastManager.getInstance(contactsView.getBaseContext()).sendBroadcast(new Intent(IMAction.DELETE_FRIEND_LOCAL).putExtra("userId", user.getUserId()));

                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.arg1 = R.string.delete_failed;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

    }


    private List<User> sortList(List<User> users) {

        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(users, comparator);
        return users;
    }

    public class PinyinComparator implements Comparator<User> {

        @Override
        public int compare(User o1, User o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();
            if (py1.equals(py2)) {
                return o1.getNick().compareTo(o2.getNick());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }
        }
    }


    @Override
    public void refreshContactsInServer() {
//        JSONArray userList=UserManager.get().getMyFrindsJsonArray();
//        if(userList!=null){
//            List<User> userListTemp=new ArrayList<>();
//            for (int i=0;i<userList.size();i++){
//                JSONObject userJson=userList.getJSONObject(i);
//                User user=new User(userJson);
//                userListTemp.add
//            }
//        }
        JSONObject data = new JSONObject();
        data.put("pageNo", 1);
        data.put("pageSize", 2000);
        ApiUtis.getInstance().postJSON(data, Constant.URL_FRIEND_LIST, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    if (jsonArray != null && jsonArray.size() > 0) {
                        List<User> users = new ArrayList<User>();
                        Set<String> friendList = new HashSet<>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject friend = jsonArray.getJSONObject(i);
                            String userId = friend.getString("userId");
                            //用户头像昵称缓存到本地
                            UserManager.get().saveUserInfo(friend);
                            friendList.add(userId);
                            users.add(new User(friend));
                        }
                        UserManager.get().saveFriends(friendList);
                        UserManager.get().saveMyFrindsJsonArray(jsonArray);

                        Message message = handler.obtainMessage();
                        message.what = 1000;
                        message.obj = sortList(users);
                        message.sendToTarget();
                    }

                }
            }

            @Override
            public void onFailure(int errorCode) {
                JSONArray jsonArray= UserManager.get().getMyFrindsJsonArray();
                if (jsonArray != null && jsonArray.size() > 0) {
                    List<User> users = new ArrayList<User>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject friend = jsonArray.getJSONObject(i);
                        //用户头像昵称缓存到本地
                        users.add(new User(friend));
                    }

                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = sortList(users);
                    message.sendToTarget();
                }
            }
        });


    }


    @Override
    public int getContactsCount() {
        return users.size();
    }

    @Override
    public void clearInvitionCount() {
        contactsView.showInvitionCount(0);
    }

    @Override
    public void refreshContactsInLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArray = UserManager.get().getMyFrindsJsonArray();
                if (jsonArray != null) {
                    List<User> users = new ArrayList<User>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject friend = jsonArray.getJSONObject(i);
                         users.add(new User(friend));
                    }
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = sortList(users);
                    message.sendToTarget();
                }
            }
        }).start();

//        contacts.clear();
//        contacts.addAll(sortList(new ArrayList<User>(ContactsManager.get().getContactList().values())));
//        contactsView.refreshALL();
    }

    @Override
    public void deleteContactsFromCMD(final String userId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (User user : users) {
                    if (userId.equals(user.getUserId())) {
                        Message message = handler.obtainMessage();
                        message.what = 1005;
                        message.obj = user;
                        message.sendToTarget();

                        break;
                    }
                }
            }
        }).start();

    }

    @Override
    public void groupSend(String msg) {
        CommonUtils.showDialogNumal(contactsView.getBaseActivity(), "");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            HTMessage htMessage = HTMessage.createTextSendMessage(user.getUserId(), msg);
            HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
                @Override
                public void onProgress() {

                }

                @Override
                public void onSuccess(long timeStamp) {

                }

                @Override
                public void onFailure() {

                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtils.cencelDialog();

            }
        }, 3000);

    }

    @Override
    public void updateUser(String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (User user : users) {
                    if (user.getUserId().equals(userId)) {
                        user.setRemark(UserManager.get().getUserRemark(userId));
                        Message message = handler.obtainMessage();
                        message.arg1 = users.indexOf(user);
                        message.what = 1003;
                        message.sendToTarget();
                        break;
                    }
                }
            }
        }).start();

    }

    @Override
    public void getApplyUnread() {
        ApiUtis.getInstance().postJSON(new JSONObject(), Constant.URL_APPLY_UNREAD, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    int unread=jsonObject.getInteger("data");

                    Message message=handler.obtainMessage();
                    message.what=1004;
                    message.arg1=unread;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });
    }

    @Override
    public void start() {

    }


}
