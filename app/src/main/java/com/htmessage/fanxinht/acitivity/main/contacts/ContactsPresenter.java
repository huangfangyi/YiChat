package com.htmessage.fanxinht.acitivity.main.contacts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.domain.InviteMessgeDao;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.domain.UserDao;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class ContactsPresenter implements BaseContactsPresenter {



    private ContactsView contactsView;
    private List<User> contacts;
    private InviteMessgeDao inviteMessgeDao;
    public ContactsPresenter (ContactsView view){
        contactsView=view;
        contactsView.setPresenter(this);
        contacts=new ArrayList<>(ContactsManager.getInstance().getContactList().values());
        inviteMessgeDao=new InviteMessgeDao(contactsView.getBaseContext());
    }


    @Override
    public List<User> getContactsListInDb() {
        contacts= sortList(new ArrayList<User>(ContactsManager.getInstance().getContactList().values()));
        return contacts;
    }
    //删除好友
    @Override
    public void deleteContacts(String userId) {
        Map<String, User> users = ContactsManager.getInstance().getContactList();
        User user = users.get(userId);
        users.remove(user.getUsername());
        contacts.remove(user);
        deleteContact(user);
    }
    //移入黑名单
    @Override
    public void moveUserToBlack(final String userId) {
        View diaglogView = View.inflate(contactsView.getBaseContext(), R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_title = (TextView) diaglogView.findViewById(R.id.tv_delete_title);
        TextView tv_delete_people = (TextView) diaglogView.findViewById(R.id.tv_delete_people);
        TextView tv_cancle = (TextView) diaglogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) diaglogView.findViewById(R.id.tv_ok);
        tv_delete_title.setText(R.string.prompt);
        tv_delete_people.setText(R.string.Into_the_blacklist);
        AlertDialog.Builder builder = new AlertDialog.Builder(contactsView.getBaseActivity());
        builder.setView(diaglogView);
        final AlertDialog dialog = builder.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showMoveToBlackDialog(userId);
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //移除用户
    public void deleteContactOnBroast(String id) {
        Map<String, User> users = ContactsManager.getInstance().getContactList();
        User user = users.get(id);
        users.remove(id);
        InviteMessgeDao dao = new InviteMessgeDao(contactsView.getBaseActivity());
        dao.deleteMessage(id);
        UserDao userDao = new UserDao(contactsView.getBaseActivity());
        userDao.deleteContact(id);
        contacts.remove(user);
        HTClient.getInstance().conversationManager().deleteConversationAndMessage(id);
    }

    /**
     * 删除用户
     *
     * @param user
     */
    private void deleteContact(final User user) {
        final ProgressDialog pd = new ProgressDialog(contactsView.getBaseActivity());
        pd.setMessage(contactsView.getBaseContext().getResources().getString(R.string.deleting));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        List<Param> params = new ArrayList<>();
        params.add(new Param(HTConstant.JSON_KEY_HXID, user.getUsername()));
        new OkHttpUtils(contactsView.getBaseActivity()).post(params, HTConstant.URL_DELETE_FRIEND, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                pd.dismiss();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        InviteMessgeDao dao = new InviteMessgeDao(contactsView.getBaseActivity());
                        dao.deleteMessage(user.getUsername());
                        UserDao userDao = new UserDao(contactsView.getBaseActivity());
                        userDao.deleteContact(user.getUsername());
                        HTClient.getInstance().conversationManager().deleteConversationAndMessage(user.getUsername());
                        sendDeleteCmd(user);
                        Toast.makeText(contactsView.getBaseActivity(), R.string.delete_sucess, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(contactsView.getBaseActivity(), R.string.Delete_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                pd.dismiss();
                Toast.makeText(contactsView.getBaseActivity(), R.string.Delete_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDeleteCmd(User user) {
        JSONObject userJson = HTApp.getInstance().getUserJson();
        CmdMessage customMessage = new CmdMessage();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", 1003);
        JSONObject data = new JSONObject();
        data.put("userId", userJson.getString("userId"));
        data.put("nick", userJson.getString("nick"));
        data.put("avatar", userJson.getString("avatar"));
        data.put("role", userJson.getString(HTConstant.JSON_KEY_ROLE));
        data.put("teamId", userJson.getString("teamId"));
        jsonObject.put("data", data);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(user.getUsername());
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    
    
    
    
    @Override
    public List<User> sortList(List<User> users) {
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
        List<Param> params = new ArrayList();
        new OkHttpUtils(contactsView.getBaseContext()).post(params, HTConstant.URL_FriendList, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray friends = jsonObject.getJSONArray("user");
                        if (friends != null || friends.size() != 0) {
                            List<User> users = new ArrayList<User>();
                            for (int i = 0; i < friends.size(); i++) {
                                JSONObject friend = friends.getJSONObject(i);
                                User user = CommonUtils.Json2User(friend);
                                users.add(user);
                            }
                            ContactsManager.getInstance().saveContactList(users);
                            contacts.clear();
                            contacts.addAll(sortList(users));
                            contactsView.refresh();
                        }
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });

    }

    @Override
    public int getInvitionCount() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }

    @Override
    public int getContactsCount() {
        return contacts.size();
    }

    @Override
    public void clearInvitionCount() {
        inviteMessgeDao.saveUnreadMessageCount(0);
        contactsView.showInvitionCount(0);
    }

    @Override
    public void refreshContactsInLocal() {

        contacts.clear();
        contacts.addAll(sortList(new ArrayList<User>(ContactsManager.getInstance().getContactList().values())));
        contactsView.refresh();
    }

    @Override
    public void start() {

    }

    /**
     * 移入黑名单
     * @param userId
     */
    private void showMoveToBlackDialog(final String userId) {
        Map<String, User> users = ContactsManager.getInstance().getContactList();
       final User user = users.get(userId);
        users.remove(userId);
        getContactsListInDb().remove(user);
       final ProgressDialog dialog = new ProgressDialog(contactsView.getBaseContext());
        dialog.setMessage(contactsView.getBaseContext().getString(R.string.Is_moved_into_blacklist));
        dialog.show();
        List<Param> params = new ArrayList<>();
        params.add(new Param(HTConstant.JSON_KEY_HXID,userId));
        new OkHttpUtils(contactsView.getBaseContext()).post(params, HTConstant.URL_ADD_BLACKLIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                int code = jsonObject.getIntValue("code");
                switch (code){
                    case 1:
                        InviteMessgeDao dao = new InviteMessgeDao(contactsView.getBaseActivity());
                        dao.deleteMessage(userId);
                        UserDao userDao = new UserDao(contactsView.getBaseActivity());
                        userDao.deleteContact(userId);
                        HTClient.getInstance().conversationManager().deleteConversationAndMessage(userId);
                        Toast.makeText(contactsView.getBaseContext(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(contactsView.getBaseContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                dialog.dismiss();
                Toast.makeText(contactsView.getBaseContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
