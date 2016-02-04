package com.fanxin.app.fx.others;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.db.InviteMessgeDao;
import com.fanxin.app.db.UserDao;
import com.fanxin.app.domain.InviteMessage;
import com.fanxin.app.domain.User;
import com.fanxin.app.domain.InviteMessage.InviteMesageStatus;
import com.fanxin.app.fx.AddFriendsFinalActivity;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;
import com.easemob.util.HanziToPinyin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewHolder")
public class NewFriendsAdapter extends BaseAdapter {
    Context context;
    List<InviteMessage> msgs;
    private InviteMessgeDao messgeDao;
    int total = 0;
    private LoadUserAvatar avatarLoader;

    @SuppressLint("SdCardPath")
    public NewFriendsAdapter(Context context, List<InviteMessage> msgs) {
        this.context = context;
        this.msgs = msgs;
        messgeDao = new InviteMessgeDao(context);
        avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
        total = msgs.size();
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public InviteMessage getItem(int position) {
        // TODO Auto-generated method stub
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        holder = new ViewHolder();
        final InviteMessage msg = getItem(total - 1 - position);
        // int msg_id = msg.getId();
        // String userUid = msg.getFrom();
        String reason_total = msg.getReason();
        String[] sourceStrArray = reason_total.split("66split88");
        // 先附初值
        String name = msg.getFrom();
        String avatar = msg.getFrom();
        String reason = "请求加好友";
        if (sourceStrArray.length == 4) {
            name = sourceStrArray[0];
            avatar = sourceStrArray[1];
            reason = sourceStrArray[3];
        }
        convertView = View.inflate(context, R.layout.item_newfriendsmsag, null);
        holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
        holder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
        holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
        holder.tv_name.setText(name);
        holder.tv_reason.setText(reason);
        if (msg.getStatus() == InviteMesageStatus.AGREED
                || msg.getStatus() == InviteMesageStatus.BEAGREED) {

            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
        } else {
            holder.tv_added.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_add.setTag(msg);
            holder.btn_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    acceptInvitation(holder.btn_add, msg, holder.tv_added);
                }

            });

        }
        showUserAvatar(holder.iv_avatar, avatar);
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_avatar;
        TextView tv_name;
        TextView tv_reason;
        TextView tv_added;
        Button btn_add;

    }

    private void showUserAvatar(ImageView iamgeView, String avatar) {
        if (avatar == null || avatar.equals("")) {
            return;
        }
        final String url_avatar = Constant.URL_Avatar + avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new ImageDownloadedCallBack() {

                        @Override
                        public void onImageDownloaded(ImageView imageView,
                                Bitmap bitmap) {
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);

        }
    }

    /**
     * 同意好友请求或者群申请
     * 
     * @param button
     * @param username
     */
    private void acceptInvitation(final Button button, final InviteMessage msg,
            final TextView textview) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在同意...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        // Map<String, String> map_uf = new HashMap<String, String>();
        // map_uf.put("user", MYApplication.getInstance().getUserName());
        // map_uf.put("friend", msg.getFrom());
        // LoadDataFromServer task = new LoadDataFromServer(context,
        // Constant.URL_ADD_FRIEND, map_uf);
        //
        // task.getData(new DataCallBack() {
        //
        // @Override
        // public void onDataCallBack(JSONObject data) {
        // if (data == null) {
        // pd.dismiss();
        // return;
        // }
        //
        // try {
        // int code = data.getInteger("code");
        // if (code == 1000) {
        // // 支持单聊和群聊，默认单聊，如果是群聊添加下面这行
        // // cmdMsg.setChatType(ChatType.GroupChat);
        // EMMessage cmdMsg = EMMessage
        // .createSendMessage(EMMessage.Type.CMD);
        // String action = Constant.CMD_AGREE_FRIEND;// action可以自定义，在广播接收时可以收到
        // CmdMessageBody cmdBody = new CmdMessageBody(action);
        //
        // cmdMsg.setReceipt(msg.getFrom());
        //
        // cmdMsg.addBody(cmdBody);
        // EMChatManager.getInstance().sendMessage(cmdMsg,
        // new EMCallBack() {
        //
        // @Override
        // public void onError(int arg0,
        // final String arg1) {
        // ((Activity) context)
        // .runOnUiThread(new Runnable() {
        // public void run() {
        // pd.dismiss();
        //
        // Toast.makeText(
        // context,
        // "同意失败" + arg1,
        // Toast.LENGTH_SHORT)
        // .show();
        // }
        // });
        // }
        //
        // @Override
        // public void onProgress(int arg0, String arg1) {
        //
        // }
        //
        // @Override
        // public void onSuccess() {
        // ((Activity) context)
        // .runOnUiThread(new Runnable() {
        // public void run() {
        // pd.dismiss();
        // textview.setVisibility(View.VISIBLE);
        // button.setEnabled(false);
        // button.setVisibility(View.GONE);
        // msg.setStatus(InviteMesageStatus.AGREED);
        // // 更新db
        // ContentValues values = new ContentValues();
        // values.put(
        // InviteMessgeDao.COLUMN_NAME_STATUS,
        // msg.getStatus()
        // .ordinal());
        // messgeDao.updateMessage(
        // msg.getId(),
        // values);
        // // 巩固程序,即时将该好友存入好友列表
        // addFriendToList(msg
        // .getFrom());
        //
        // }
        // });
        // }
        //
        // });
        //
        // } else {
        // pd.dismiss();
        // showError("服务器端数据访问错误");
        //
        // }
        // } catch (JSONException e) {
        // showError("服务器端数据解析错误");
        // pd.dismiss();
        // e.printStackTrace();
        // }
        //
        // }
        //
        // });

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getGroupId() == null) // 同意好友请求
                        EMChatManager.getInstance().acceptInvitation(
                                msg.getFrom());
                    else
                        // 同意加群申请
                        EMGroupManager.getInstance().acceptApplication(
                                msg.getFrom(), msg.getGroupId());
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            textview.setVisibility(View.VISIBLE);
                            button.setEnabled(false);
                            button.setVisibility(View.GONE);
                            msg.setStatus(InviteMesageStatus.AGREED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                    .getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);

                            // 巩固程序,即时将该好友存入好友列表

                            addFriendToList(msg.getFrom());

                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, "同意失败: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    private void showError(String strE) {
        Toast.makeText(context, strE, Toast.LENGTH_SHORT).show();

    }
    
    
    private void addFriendToList(final String hxid) {
        Map<String, String> map_uf = new HashMap<String, String>();
        map_uf.put("user", MYApplication.getInstance().getUserName());
        map_uf.put("friend", hxid);
        LoadDataFromServer task = new LoadDataFromServer(null,
                Constant.URL_ADD_FRIEND, map_uf);
        task.getData(new DataCallBack() {
            public void onDataCallBack(JSONObject data) {
                try {

                    int code = data.getInteger("code");
                    if (code == 1000) {

                        JSONObject json = data.getJSONObject("user");
                        if (json != null && json.size() != 0) {

                        }
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");

                        String hxid = json.getString("hxid");
                        String fxid = json.getString("fxid");
                        String region = json.getString("region");
                        String sex = json.getString("sex");
                        String sign = json.getString("sign");
                        String tel = json.getString("tel");
                        User user = new User();

                        user.setUsername(hxid);
                        user.setNick(nick);
                        user.setAvatar(avatar);
                        user.setFxid(fxid);
                        user.setRegion(region);
                        user.setSex(sex);
                        user.setSign(sign);
                        user.setTel(tel);
                        setUserHearder(hxid, user);
                        Map<String, User> userlist = MYApplication
                                .getInstance().getContactList();
                        Map<String, User> map_temp = new HashMap<String, User>();
                        map_temp.put(hxid, user);
                        userlist.putAll(map_temp);
                        // 存入内存
                        MYApplication.getInstance().setContactList(userlist);
                        // 存入db
                        UserDao dao = new UserDao(context);

                        dao.saveContact(user);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

        });

    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     * 
     * @param username
     * @param user
     */
    @SuppressLint("DefaultLocale")
    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        headerName = headerName.trim();
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                    1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }
}
