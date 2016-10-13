package com.fanxin.huangfangyi.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.db.InviteMessgeDao;
import com.fanxin.huangfangyi.db.UserDao;
import com.fanxin.huangfangyi.domain.InviteMessage;
import com.fanxin.huangfangyi.domain.InviteMessage.InviteMesageStatus;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.JSONUtil;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class NewFriendsAdapter extends BaseAdapter {
    Context context;
    List<InviteMessage> msgs;
    private InviteMessgeDao messgeDao;
    int total = 0;

    public NewFriendsAdapter(Context context, List<InviteMessage> msgs) {
        this.context = context;
        this.msgs = msgs;
        messgeDao = new InviteMessgeDao(context);
        total = msgs.size();
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public InviteMessage getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.fx_item_newfriend_msg, null);

        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
            holder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.btn_refused = (Button) convertView.findViewById(R.id.btn_refused);
            holder.tv_note = (TextView) convertView.findViewById(R.id.tv_note);

            convertView.setTag(holder);
        }

        final InviteMessage msg = getItem(total - 1 - position);
        String reason = "理由： ";
        String nick = msg.getFrom();
        try {
            JSONObject jsonObject = JSONObject.parseObject(msg.getReason());
            if (jsonObject != null) {
                nick = jsonObject.getString("nick");
                String avatar = jsonObject.getString("avatar");
                Glide.with(context).load(FXConstant.URL_AVATAR + avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar);
                //TODO  在申请消息的jsonobject里面是传有申请理由的，后续开发者可以按照需求处理这个申请理由
                String reasonTemp = jsonObject.getString(FXConstant.CMD_ADD_REASON);
                if (!TextUtils.isEmpty(reasonTemp)) {
                    reason = reason + reasonTemp;
                }
            }
        } catch (JSONException e) {

        }

        holder.tv_reason.setText(reason);
        holder.tv_name.setText(nick);
        if (msg.getStatus() == InviteMesageStatus.AGREED) {

            holder.tv_note.setText(" 申请加为好友");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_refused.setVisibility(View.GONE);
        } else if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
            holder.tv_note.setText(" 同意了你的好友请求");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_refused.setVisibility(View.GONE);

        } else if (msg.getStatus() == InviteMesageStatus.REFUSED) {
            holder.tv_note.setText(" 申请加为好友");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.tv_added.setText("已拒绝");
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_refused.setVisibility(View.GONE);
        } else if (msg.getStatus() == InviteMesageStatus.BEREFUSED) {
            holder.tv_note.setText(" 拒绝了你的好友请求");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.tv_added.setText("被拒绝");
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_refused.setVisibility(View.GONE);
        } else {
            holder.tv_note.setText(" 申请加为好友");
            holder.tv_added.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_add.setTag(msg);
            final ViewHolder finalHolder = holder;
            holder.btn_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptInvitation(finalHolder.btn_add, msg, finalHolder.tv_added, finalHolder.btn_refused);
                }

            });
            holder.btn_refused.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuseInvitation(finalHolder.btn_add, msg, finalHolder.tv_added, finalHolder.btn_refused);
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_avatar;
        TextView tv_name;
        TextView tv_reason;
        TextView tv_added;
        Button btn_add;
        Button btn_refused;

        TextView tv_note;

    }

    /**
     * 同意好友请求
     *
     * @param button
     * @param
     */
    private void acceptInvitation(final Button button, final InviteMessage msg,
                                  final TextView textview, final Button buttonRefused) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在同意...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        List<Param> params = new ArrayList<>();
        params.add(new Param("user", DemoHelper.getInstance().getCurrentUsernName()));
        params.add(new Param("friend", msg.getFrom()));
        OkHttpManager.getInstance().post(params, FXConstant.URL_ADD_FRIEND, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    JSONObject json = jsonObject.getJSONObject("user");
                    if (json != null && json.size() != 0) {
                        EaseUser user = JSONUtil.Json2User(json);
                        // 存入内存
                        DemoHelper.getInstance().getContactList().put(user.getUsername(), user);
                        // 存入db
                        UserDao dao = new UserDao(context);
                        dao.saveContact(user);
                        sendCmdAgreeMsg(button, msg, textview, pd, buttonRefused);
                    }
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                pd.dismiss();
            }
        });
    }


    private void sendCmdAgreeMsg(final Button button, final InviteMessage msg,
                                 final TextView textview, final ProgressDialog pd, final Button buttonRefused) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        //支持单聊和群聊，默认单聊，
        cmdMsg.setChatType(EMMessage.ChatType.Chat);
        //action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(FXConstant.CMD_AGREE_FRIEND);
        cmdMsg.setReceipt(msg.getFrom());
        cmdMsg.addBody(cmdBody);
        JSONObject jsonObject = DemoApplication.getInstance().getUserJson();
        //传递申请者的资料
        cmdMsg.setAttribute(FXConstant.KEY_USER_INFO, jsonObject.toJSONString());
        cmdMsg.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @SuppressLint("ShowToast")
                    public void run() {
                        pd.dismiss();
                        textview.setVisibility(View.VISIBLE);
                        button.setEnabled(false);
                        button.setVisibility(View.GONE);
                        buttonRefused.setVisibility(View.GONE);
                        msg.setStatus(InviteMesageStatus.AGREED);
                        // 更新db
                        ContentValues values = new ContentValues();
                        values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                .getStatus().ordinal());
                        messgeDao.updateMessage(msg.getId(), values);

                    }
                });
            }

            @Override
            public void onError(int i, final String s) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(context,
                                "同意失败:" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);


    }


    private void refuseInvitation(final Button button, final InviteMessage msg,
                                  final TextView textview, final Button buttonRefused) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在拒绝...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        //支持单聊和群聊，默认单聊，
        cmdMsg.setChatType(EMMessage.ChatType.Chat);
        //action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(FXConstant.CMD_REFUSE_FRIEND);
        cmdMsg.setReceipt(msg.getFrom());
        cmdMsg.addBody(cmdBody);
        JSONObject jsonObject = DemoApplication.getInstance().getUserJson();
        //传递申请者的资料
        cmdMsg.setAttribute(FXConstant.KEY_USER_INFO, jsonObject.toJSONString());
        cmdMsg.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @SuppressLint("ShowToast")
                    public void run() {
                        pd.dismiss();
                        textview.setVisibility(View.VISIBLE);
                        textview.setText("已拒绝");
                        button.setEnabled(false);
                        button.setVisibility(View.GONE);
                        buttonRefused.setVisibility(View.GONE);
                        msg.setStatus(InviteMesageStatus.REFUSED);
                        // 更新db
                        ContentValues values = new ContentValues();
                        values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                .getStatus().ordinal());
                        messgeDao.updateMessage(msg.getId(), values);

                    }
                });
            }

            @Override
            public void onError(int i, final String s) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(context,
                                "拒绝失败:" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);


    }


}
