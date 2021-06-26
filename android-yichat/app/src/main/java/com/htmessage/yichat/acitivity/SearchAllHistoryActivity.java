package com.htmessage.yichat.acitivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.MsgUtils;
import com.htmessage.yichat.acitivity.chat.ChatActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangfangyi on 2019/5/21.
 * qq 84543217
 */
public class SearchAllHistoryActivity extends BaseActivity {

    private EditText etSearch;
    private TextView tvSearch;
    private ListView listView, listview_contacts;
    // private List<HTMessage> htMessages=new ArrayList<>();
    private MyAdapter adapter;
    private List<User> users = new ArrayList<>();
    private MyAdapterContacts myAdapterContacts;
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    //检索完本地
                    String content = (String) msg.obj;
                    if (!TextUtils.isEmpty(groupIds)) {
                        searchInserver(content, groupIds);
                    } else {
                        CommonUtils.cencelDialog();
                        if(msgMap.size()>0){
                            findViewById(R.id.tv_record).setVisibility(View.VISIBLE);
                        }
                        adapter = new MyAdapter(msgMap, SearchAllHistoryActivity.this);
                        listView.setAdapter(adapter);


                    }


                    break;
                case 2000:
                    CommonUtils.cencelDialog();
                    if(msgMap.size()>0){
                        findViewById(R.id.tv_record).setVisibility(View.VISIBLE);
                    }
                    adapter = new MyAdapter(msgMap, SearchAllHistoryActivity.this);
                    listView.setAdapter(adapter);
                    break;
                case 3000:
                    CommonUtils.cencelDialog();
                    break;

                case 4000:
                    String contentMsg = (String) msg.obj;
                    myAdapterContacts.notifyDataSetChanged();
                    searchInLocal(contentMsg);
                    break;
            }
        }
    };
    String groupIds = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_search_history);
        etSearch = findViewById(R.id.et_search);
        tvSearch = findViewById(R.id.tv_search);
        listView = findViewById(R.id.listview);


        List<HTGroup> htGroups = HTClient.getInstance().groupManager().getAllGroups();
        for (int i = 0; i < htGroups.size(); i++) {
            HTGroup htGroup = htGroups.get(i);
            groupIds = htGroup.getGroupId() + "," + groupIds;

        }
        if (!TextUtils.isEmpty(groupIds)) {
            groupIds = groupIds.substring(0, groupIds.length() - 1);
        }
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showDialogNumal(SearchAllHistoryActivity.this, "正在搜索");
                String content = etSearch.getText().toString().trim();
                searchUserAndGroup(content);


                //  searchInserver(content,groupIds);


            }
        });
        adapter = new MyAdapter(msgMap, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = adapter.getItem(position);
                startActivity(new Intent(SearchAllHistoryActivity.this, HistoryDetailsActivity.class)
                        .putParcelableArrayListExtra("data", new ArrayList<Parcelable>(msgMap.get(userId))));

            }
        });

        listview_contacts = findViewById(R.id.listview_contacts);

        myAdapterContacts = new MyAdapterContacts(users, SearchAllHistoryActivity.this);
        listview_contacts.setAdapter(myAdapterContacts);
        listview_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user=myAdapterContacts.getItem(position);
             startActivity(new Intent(SearchAllHistoryActivity.this, ChatActivity.class).putExtra("chatType",user.getType()).putExtra("userId",user.getUserId()));
              finish();
            }
        });
    }

    private void searchUserAndGroup(String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                JSONArray jsonArray = UserManager.get().getMyFrindsJsonArray();
                if (jsonArray != null) {
                    boolean isFirst=true;
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String userId = jsonObject.getString("userId");
                        String nick = UserManager.get().getUserNick(userId);
                        String avatar = jsonObject.getString("avatar");

                        if (nick.contains(content)) {
                            User user = new User();
                            user.setUsername(userId);
                            user.setNick(nick);
                            user.setType(1);
                            user.setAvatar(avatar);
                            if (isFirst) {
                                user.setInitialLetter("好友");
                                isFirst=false;
                            }
                            users.add(user);

                        }

                    }
                }

                List<HTGroup> htGroups = HTClient.getInstance().groupManager().getAllGroups();
                boolean isFirst=true;
                for (int i = 0; i < htGroups.size(); i++) {
                    HTGroup htGroup = htGroups.get(i);
                    if (htGroup.getGroupName().contains(content)) {
                        User user = new User();
                        user.setNick(htGroup.getGroupName());
                        user.setUsername(htGroup.getGroupId());
                        user.setAvatar(htGroup.getImgUrl());
                        if (isFirst) {
                            user.setInitialLetter("群聊");
                            isFirst=false;
                        }
                        user.setType(2);
                        users.add(user);
                    }
                }


                Message message = handler.obtainMessage();
                message.what = 4000;
                message.obj = content;
                message.sendToTarget();
            }
        }).start();
    }

    private void searchInserver(String content, String groupIds) {
        JSONObject data = new JSONObject();
        data.put("searchContent", content);
        data.put("groupIds", groupIds);
        data.put("pageNo", 1);
        data.put("pageSize", 1000);
        ApiUtis.getInstance().postJSON(data, Constant.URL_MESSAGE_SEARCH, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (data != null) {
                        for (int i = 0; i < data.size(); i++) {
                            JSONObject msgJson = data.getJSONObject(i);
                            String msgString = msgJson.getString("content");
                            long time = msgJson.getLong("time");
                            HTMessage htMessage = MsgUtils.getInstance().stringToMessage(msgString, time);
                            if (htMessage != null) {
                                List<HTMessage> htMessageList = msgMap.get(htMessage.getUsername());
                                if (htMessageList == null) {
                                    htMessageList = new ArrayList<>();
                                }
                                htMessageList.add(htMessage);
                                msgMap.put(htMessage.getUsername(), htMessageList);
                            }


                        }
                        Message message = handler.obtainMessage();
                        message.what = 2000;
                        message.sendToTarget();

                    }

                }

            }

            @Override
            public void onFailure(int errorCode) {

                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 3000;
                message.sendToTarget();

            }
        });
//        List<Param> list=new ArrayList<>();
//        list.add(new Param("content",content));
//        list.add(new Param("groupId",groupIds));
//        new OkHttpUtils(this).post(list, HTConstant.URL_SEARCH_GROUP_HISTORY, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code=jsonObject.getInteger("code");
//
//                if(code==1){
//
//                 JSONObject data=jsonObject.getJSONObject("data");
//                    Iterator<String> it = data.keySet().iterator();
//                    while(it.hasNext()){
//// 获得key
//                        String key = it.next();
//                        JSONArray value = data.getJSONArray(key);
//                        List<HTMessage> htMessageList=arrayToList(value);
//                        if(msgMap.get(key)==null){
//                            List<HTMessage> htMessages=new ArrayList<>();
//                            htMessages.addAll(htMessageList);
//                            msgMap.put(key,htMessages);
//                        }else {
//
//                            msgMap.get(key).addAll(htMessageList);
//                        }
//
//
//
//                     //   System.out.println("key: "+key+",value:"+value.toJSONString());
//                    }
//
//
//                }
//                adapter=new MyAdapter(msgMap,SearchAllHistoryActivity.this);
//                listView.setAdapter(adapter);
//
//
////                if(code==1){
////                    htMessages.clear();
////                    JSONArray jsonArray=jsonObject.getJSONArray("data");
////                    for(int i=0;i<jsonArray.size();i++){
////
////                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
////                        String msgString64=jsonObject1.getString("message");
////                        String timeStamp = jsonObject1.getString("timeStamp");
////                        if (!TextUtils.isEmpty(msgString64) && !TextUtils.isEmpty(timeStamp)) {
////                            try {
////                                String msgString = Base64.decodeToString(msgString64);
////                                JSONObject messageJSON = JSONObject.parseObject(msgString);
////                                HTMessage htMessage = ChatPresenter.creatFromJSON(messageJSON, timeStamp);
////                                if (htMessage != null&&htMessage.getType()== HTMessage.Type.TEXT) {
////                                    htMessages.add(0, htMessage);
////                                }
////                            } catch (Exception e) {
////                                try {
////                                    JSONObject messageJSON = JSONObject.parseObject(msgString64);
////                                    HTMessage htMessage = ChatPresenter.creatFromJSON(messageJSON, timeStamp);
////                                    if (htMessage != null&&htMessage.getType()== HTMessage.Type.TEXT) {
////                                        htMessages.add(0, htMessage);
////                                    }
////                                } catch (Exception e1) {
////
////                                }
////
////                                e.printStackTrace();
////                            }
////                        }
////
////                    }
////                    adapter.notifyDataSetChanged();
////
////                }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });


    }


    private Map<String, List<HTMessage>> msgMap = new HashMap<>();


    private void searchInLocal(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HTMessage> htMessageList = HTClient.getInstance().messageManager().searchMsgFromDB(null, content);

                for (HTMessage htMessage : htMessageList) {
                    String userId = htMessage.getUsername();
                    if (msgMap.get(userId) == null) {
                        List<HTMessage> htMessages = new ArrayList<>();
                        htMessages.add(htMessage);
                        msgMap.put(userId, htMessages);
                    } else {

                        msgMap.get(userId).add(htMessage);
                    }
                }

                Message message = handler.obtainMessage();
                message.what = 1000;
                message.obj = content;
                message.sendToTarget();

            }
        }).start();


//        if(htMessageList!=null){
//            htMessages.clear();
//            htMessages.addAll(htMessageList);
//            adapter.notifyDataSetChanged();
//        }
    }


    class MyAdapter extends BaseAdapter {
        private Map<String, List<HTMessage>> data;
        private Context context;
        List<String> userIds;

        public MyAdapter(Map<String, List<HTMessage>> data, Context context) {

            this.data = data;
            this.context = context;
            userIds = new ArrayList<>(data.keySet());
        }


        @Override
        public int getCount() {
            return userIds.size();
        }

        @Override
        public String getItem(int position) {
            return userIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_message_search, parent, false);

            }

            ImageView avatarView = convertView.findViewById(R.id.avatar);
            TextView nameView = convertView.findViewById(R.id.name);
            TextView contentView = convertView.findViewById(R.id.message);
            TextView timeView = convertView.findViewById(R.id.time);
            String userId = getItem(position);

            List<HTMessage> htMessageList = msgMap.get(userId);
            String nick = "";
            String avatar = "";

            if (UserManager.get().getFriends().contains(userId)) {
                nick = UserManager.get().getUserNick(userId);
                avatar = UserManager.get().getUserAvatar(userId);
            } else {

                HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
                if (htGroup != null) {

                    nick = htGroup.getGroupName();
                    avatar = htGroup.getImgUrl();
                }


            }
            String content = htMessageList.size() + "条相关的聊天记录";

            //long time=getItem(position).getTime();
            UserManager.get().loadUserAvatar(SearchAllHistoryActivity.this, avatar, avatarView);
            nameView.setText(nick);
            contentView.setText(content);
            //timeView.setText(DateUtils.getTimestampString(new Date(time)));
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }


    class MyAdapterContacts extends BaseAdapter {
        private List<User> data;
        private Context context;

        public MyAdapterContacts(List<User> data, Context context) {

            this.data = data;
            this.context = context;
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public User getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_contact_list, parent, false);

            }
            TextView headerView = convertView.findViewById(R.id.header);
            ImageView avatarView = convertView.findViewById(R.id.iv_avatar);
            TextView nameView = convertView.findViewById(R.id.tv_name);
            String header = getItem(position).getInitialLetter();
            String nick = getItem(position).getNick();
            String avatar = getItem(position).getAvatar();
            UserManager.get().loadUserAvatar(SearchAllHistoryActivity.this, avatar, avatarView);
            nameView.setText(nick);
            if (TextUtils.isEmpty(header)) {
                headerView.setVisibility(View.GONE);

            } else {
                headerView.setVisibility(View.VISIBLE);
                headerView.setText(header);
            }
            return convertView;
        }
    }

}
