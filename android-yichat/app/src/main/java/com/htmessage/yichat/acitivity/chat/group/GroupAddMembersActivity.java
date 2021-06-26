package com.htmessage.yichat.acitivity.chat.group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.ChatActivity;
import com.htmessage.yichat.acitivity.chat.PickContactAdapter;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GroupAddMembersActivity extends BaseActivity {
    private ImageView iv_search;
    private TextView tv_checked, tv_check_all;
    private ListView listView;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;

    private PickContactAdapter contactAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<String> exitingMembers = new ArrayList<String>();
    // 可滑动的显示选中用户的View
  //  private LinearLayout menuLinerLayout, ll_check_all;

    // 选中用户总数,右上角显示

    private String userId = null;
    private String groupId = null;

    // 添加的列表
    private List<String> addList = new ArrayList<String>();


    private List<String> friendList;
    private HorizontalScrollView horizonMenu;
    private HTGroup htGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        initView();
        groupId = getIntent().getStringExtra("groupId");
        userId = getIntent().getStringExtra("userId");
        if (groupId != null) {
            isCreatingNewGroup = false;
            htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
            Set<String> groupUserIds=GroupInfoManager.getInstance().getAllMemberUserIdFromLocal(groupId);
            if(groupUserIds!=null){

                exitingMembers.addAll(new ArrayList<>(groupUserIds));
            }
        } else if (userId != null) {
            checkCreatePermission();
            isCreatingNewGroup = true;
            addList.add(userId);
            exitingMembers.add(userId);
        } else {
            checkCreatePermission();
            isCreatingNewGroup = true;
        }


        // 获取好友列表
        friendList = new ArrayList<>(UserManager.get().getFriends());
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {
        });
        refreshList(friendList);
    }

    private void    checkCreatePermission(){

        if(!SettingsManager.getInstance().getCreateGroupAuthStatus()){

            AlertDialog.Builder exceptionBuilder = new AlertDialog.Builder(GroupAddMembersActivity.this);

            exceptionBuilder.setTitle("无建群权限");
            exceptionBuilder.setMessage("管理员设置为只有指定用户方可建群");
            exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    finish();

                }
            });
            exceptionBuilder.setCancelable(false);
            exceptionBuilder.show();
        }




    }


    // private EditText etGroupName;

    private void initView() {
        tv_checked = (TextView) this.findViewById(R.id.btn_rtc);
        tv_checked.setVisibility(View.VISIBLE);
        tv_checked.setText(R.string.ok);
        listView = (ListView) findViewById(R.id.list);
        iv_search = (ImageView) this.findViewById(R.id.iv_search);
       // ll_check_all = (LinearLayout) findViewById(R.id.ll_check_all);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        // View headerView = layoutInflater.inflate(R.layout.item_group_create_header, null);

        //  etGroupName = (EditText) headerView.findViewById(R.id.et_header);
        // CommonUtils.setEditTextInhibitInputSpeChat(etGroupName);
        horizonMenu = (HorizontalScrollView) this.findViewById(R.id.horizonMenu);

        //TODO 屏蔽headview
        // listView.addHeaderView(headerView);
     //   menuLinerLayout = (LinearLayout) this.findViewById(R.id.linearLayoutMenu);
        //设置监听
        setTextChangedListener((EditText) this.findViewById(R.id.et_search));
        tv_checked.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        setTitle(R.string.Initiate_group_chat);
//        ll_check_all.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonUtils.showAlertDialog(GroupAddMembersActivity.this, null, "确定邀请所有好友进群?", new CommonUtils.OnDialogClickListener() {
//                    @Override
//                    public void onPriformClock() {
//                        JSONArray jsonArray =  MmvkManger.getIntance().getJSONArray(HTApp.get().getUserId() + groupId);
//                        arrayToList(jsonArray, friendList);
//                        //已有群加人
//                        existsGroupAddMembers(addList);
//                    }
//
//                    @Override
//                    public void onCancleClock() {
//
//                    }
//                });
//            }
//        });
    }


    private void setTextChangedListener(final EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    String str_s = etSearch.getText().toString().trim();
                    List<String> usersTemp = new ArrayList<>();
                    for (String userId : friendList) {
                        String nick = UserManager.get().getUserNick(userId);
                        if (nick.contains(str_s)) {
                            usersTemp.add(userId);
                        }
                    }
                    refreshList(usersTemp);
                } else {
                    refreshList(friendList);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void refreshList(List<String> users) {
        contactAdapter = new PickContactAdapter(GroupAddMembersActivity.this, users, exitingMembers);
        listView.setAdapter(contactAdapter);
    }

    // 即时显示被选中用户的头像和昵称。
    public void addTolist(String userId) {
        if (exitingMembers.contains(userId) && groupId != null) {
            return;
        }
        if (addList.contains(userId)) {
            return;
        }
        addList.add(userId);
        if (addList.size() == 0) {
            //UserManager.get().loadUserAvatar(GroupAddMembersActivity.this, R.drawable.icon_search_gray, iv_search);
            tv_checked.setText(getString(R.string.ok));
        } else {
            tv_checked.setText(getString(R.string.ok_xiaokuohao) + addList.size() + getString(R.string.xiaokuohao));

        }
    }

    public void removeFromList(String userId) {
        addList.remove(userId);
        tv_checked.setText(getString(R.string.ok_xiaokuohao) + addList.size() + getString(R.string.xiaokuohao));
        if (addList.size() < 1) {
            tv_checked.setText(R.string.ok);
        }
    }

    /**
     * 确认选择的members
     */
    public void save() {


        if (addList.size() == 0) {
            CommonUtils.showToastShort(getBaseContext(), R.string.check_friend);
            return;
        }
        if (addList.size() +exitingMembers.size()> Constant.MaxGroupCount) {
            CommonUtils.showToastShort(getBaseContext(), "群人数不可超过"+Constant.MaxGroupCount);
            return;
        }
        if (isCreatingNewGroup) {
            //只有一個人，直接进入聊天界面
            if (addList.size() == 1) {
                String userId = addList.get(0);
                startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra("userId", userId));
                finish();
                return;
            }
            //否则进入创建群组
            creatGroupNew(addList);
        } else {
            //已有群加人
            existsGroupAddMembers(addList);
        }
    }

    /**
     * 创建新群组
     */
    private void creatGroupNew(final List<String> members) {
        String groupName = HTApp.getInstance().getUserNick() + "、" + UserManager.get().getUserRealNick(members.get(0)) + "、" + UserManager.get().getUserRealNick(members.get(1));
        if (groupName.length() > 20) {
            groupName = groupName.substring(0, 19);
        }

        CommonUtils.showDialogNumal(GroupAddMembersActivity.this, getString(R.string.Is_to_create_a_group_chat));
        HTClient.getInstance().groupManager().createGroup(members, groupName, "", "", new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(getBaseContext(), R.string.success_to_create_groups);
                startActivity(new Intent(GroupAddMembersActivity.this, ChatActivity.class).putExtra("userId", data).putExtra("chatType", MessageUtils.CHAT_GROUP));
                finish();
            }

            @Override
            public void onFailure() {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(getBaseContext(), R.string.Failed_to_create_groups);
            }

            @Override
            public void onHTMessageSend(HTMessage htMessage) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.upLoadMessage(htMessage);
                        LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

                    }
                });
            }
        });
    }


    public class PinyinComparator implements Comparator<String> {
        @Override
        public int compare(String userId1, String userId2) {
            String nick1 = UserManager.get().getUserNick(userId1);
            String nick2 = UserManager.get().getUserNick(userId2);


            String py1 = User.getInitialLetter(nick1);
            String py2 = User.getInitialLetter(nick2);
            if (py1.equals(py2)) {
                return nick1.compareTo(nick2);
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


    private void existsGroupAddMembers(final List<String> addMembers) {
        if (addMembers.size() == 0) {
            return;
        }
        if (htGroup == null) {
            return;
        }
        //我是群主或者管理员
        if (GroupInfoManager.getInstance().isManager(groupId)) {
            CommonUtils.showDialogNumal(GroupAddMembersActivity.this, getString(R.string.joining_group));
            final Map<String, String> map = new HashMap<>();
            String membersListString = "";
            for (String userId : addMembers) {
                String nick = UserManager.get().getUserRealNick(userId);
                map.put(userId, nick);
                membersListString = membersListString + nick + " ";
            }
            String showMsg = HTApp.getInstance().getUserNick() + "邀请了" + membersListString + "进群";

            HTClient.getInstance().groupManager().addMembersWithContent(map, htGroup.getOwner(), groupId, showMsg, new GroupManager.CallBack() {
                @Override
                public void onSuccess(String data) {
                    GroupInfoManager.getInstance().addMembers(groupId, addMembers);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(getBaseContext(), R.string.joining_group_success);

                            setResult(RESULT_OK);
                            finish();
                        }
                    });

                }

                @Override
                public void onFailure() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(getBaseContext(), R.string.joining_group_failed);
//                          setResult(RESULT_OK);
                            finish();
                        }
                    });
                }

                @Override
                public void onHTMessageSend(HTMessage htMessage) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.upLoadMessage(htMessage);
                            LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

                        }
                    });
                }
            });
        } else {
            CommonUtils.showToastShort(getBaseContext(), "该群群成员无权限邀请人进群");
        }
    }

}

