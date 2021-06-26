package com.htmessage.yichat.acitivity.main.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.Sidebar;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.MsgUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SearchContactsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher, AdapterView.OnItemLongClickListener, Sidebar.OnTouchingLetterChangedListener {
    private TextView tv_title, floating_header;
    private ListView list;
    private Sidebar sideBar;
    private ContactsSearchAdapter adapter;
    private EditText edt_search;
    private ImageView iv_clear;
    private List<User> users = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1000:
                    //网络获取了好友列表
                    List<User> userList = (List<User>) msg.obj;
                    users.clear();
                    users.addAll(userList);

                    adapter.notifyDataSetChanged();
                    break;
                case 1001:

                    final User user = (User) msg.obj;
                    users.remove(user);
                    adapter.notifyDataSetChanged();

                    //通知本地其他地方关联处删除---目前只有会话如果有关于此人的会话需要删掉
                    LocalBroadcastManager.getInstance(SearchContactsActivity.this).sendBroadcast(new Intent(IMAction.DELETE_FRIEND_LOCAL).putExtra("userId", user.getUserId()));
                    //发送CMD消息，告诉好友关系解除了
                    MsgUtils.getInstance().sendDeleteCMD(user.getUserId());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserManager.get().deleteMyFriends(user.getUserId());
                        }
                    }).start();

                    setResult(RESULT_OK);
                    break;
                case 1002:
                    int resId = msg.arg1;
                    Toast.makeText(SearchContactsActivity.this, resId, Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_search_contacts);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArray = UserManager.get().getMyFrindsJsonArray();
                if (jsonArray != null) {
                    List<User> usersTemp = new ArrayList<User>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject friend = jsonArray.getJSONObject(i);
                        UserManager.get().saveUserInfo(friend);
                        usersTemp.add(new User(friend));
                    }
                    if (handler == null) {
                        return;
                    }
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = sortList(usersTemp);
                    message.sendToTarget();
                }
            }
        }).start();

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        list = (ListView) findViewById(R.id.list);
        sideBar = (Sidebar) findViewById(R.id.sidebar);
        edt_search = (EditText) findViewById(R.id.edt_search);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        floating_header = (TextView) findViewById(R.id.floating_header);

    }

    private void initData() {
        tv_title.setText(R.string.address_book);
        sideBar.setVisibility(View.VISIBLE);
        sideBar.setTextView(floating_header);
        refreshList(users);
    }

    private void setListener() {
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);
        iv_clear.setOnClickListener(this);
        edt_search.addTextChangedListener(this);
        sideBar.setOnTouchingLetterChangedListener(this);
    }

    private void refreshList(List<User> userList) {
        sortList(userList);
        adapter = new ContactsSearchAdapter(this, userList);
        list.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user=adapter.getItem(position);
        startActivity(new Intent(SearchContactsActivity.this, UserDetailActivity.class).putExtra("userId", user.getUserId()));
        finish();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear:
                edt_search.getText().clear();
                iv_clear.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            iv_clear.setVisibility(View.VISIBLE);
            String newText = edt_search.getText().toString();
            List<User> usersTemp = new ArrayList<User>();
            for (User user : users) {
                String nick = user.getNick();
                String username = user.getUserId();
                if (TextUtils.isEmpty(nick)) {
                    nick = username;
                }
                if (nick.contains(newText)) {
                    usersTemp.add(user);
                }
            }
            refreshList(usersTemp);
        } else {
            iv_clear.setVisibility(View.GONE);
            refreshList(users);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        User user = adapter.getItem(position);
        showItemDialog(user);
        return true;
    }

    private void showItemDialog(final User user) {
        HTAlertDialog dialog = new HTAlertDialog(this, null, new String[]{getResources().getString(R.string.delete)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        deleteContacts(user);
                        break;

                }
            }
        });
    }

    //删除好友
    private void deleteContacts(final User user) {
        JSONObject data = new JSONObject();
        data.put("friendId", user.getUserId());
        ApiUtis.getInstance().postJSON(data, Constant.URL_FRIEND_DELETE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = user;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.arg1 = R.string.delete_failed;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

    }

    /**
     * 删除用户
     *
     * @param user
     */
    private void deleteContact(final User user) {
//        CommonUtils.showDialogNumal(this, getString(R.string.deleting));
//        List<Param> params = new ArrayList<>();
//        params.add(new Param(HTConstant.JSON_KEY_USERID, user.getUserId()));
//        new OkHttpUtils(getBaseContext()).post(params, HTConstant.URL_DELETE_FRIEND, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                         UserDao userDao = new UserDao(getBaseContext());
//                        userDao.deleteContact(user.getUserId());
//                        HTClient.get().conversationManager().deleteConversationAndMessage(user.getUserId());
//                        sendDeleteCmd(user);
//                        refreshList(users);
//                        edt_search.getText().clear();
//                        iv_clear.setVisibility(View.GONE);
//                        LocalBroadcastManager.get(getBaseContext()).sendBroadcast(new Intent(IMAction.REFRESH_CONTACTS_LIST));
//                        CommonUtils.showToastShort(getBaseContext(), R.string.delete_sucess);
//                        break;
//                    default:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.Delete_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(getBaseContext(), R.string.Delete_failed);
//            }
//        });
    }

    private void sendDeleteCmd(User user) {
//        JSONObject userJson = HTApp.get().getUserJson();
//        CmdMessage customMessage = new CmdMessage();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("action", 1003);
//        JSONObject data = new JSONObject();
//        data.put("userId", userJson.getString("userId"));
//        data.put("nick", userJson.getString("nick"));
//        data.put("avatar", userJson.getString("avatar"));
//        data.put("role", userJson.getString(HTConstant.JSON_KEY_ROLE));
//        data.put("teamId", userJson.getString("teamId"));
//        jsonObject.put("data", data);
//        customMessage.setMsgId(UUID.randomUUID().toString());
//        customMessage.setFrom(HTApp.get().getUserId());
//        customMessage.setTime(System.currentTimeMillis());
//        customMessage.setTo(user.getUserId());
//        customMessage.setBody(jsonObject.toJSONString());
//        customMessage.setChatType(ChatType.singleChat);
//        HTClient.get().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
//            @Override
//            public void onProgress() {
//
//            }
//
//            @Override
//            public void onSuccess(long timeStamp) {
//
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//        });
    }

    private List<User> sortList(List<User> users) {
        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(users, comparator);
        return users;
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            list.setSelection(position);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
