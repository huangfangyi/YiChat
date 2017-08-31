package com.htmessage.fanxinht.acitivity.addfriends.newfriend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.domain.InviteMessgeDao;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.domain.UserDao;
import com.htmessage.fanxinht.domain.InviteMessage;
import com.htmessage.fanxinht.domain.InviteMessage.Status;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.fanxinht.widget.HTAlertDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewFriendsAdapter extends BaseAdapter {
    private Context context;
    private List<InviteMessage> msgs;
    private InviteMessgeDao messgeDao;
    int total = 0;

    public NewFriendsAdapter(Context _context, List<InviteMessage> msgs) {
        this.context = _context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_newfriend_msg, null);

        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
            holder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.rl_item_add = (RelativeLayout) convertView.findViewById(R.id.rl_item_add);
            convertView.setTag(holder);
        }


//        final InviteMessage msg = getItem(total - 1 - position);
        final InviteMessage msg = getItem(position);
//        String reason = context.getString(R.string.request_add_friend);
        String reason = context.getString(R.string.Reasons);
        String nick = msg.getFrom();
        JSONObject userInfo = null;
        try {
            userInfo = JSONObject.parseObject(msg.getReason());
            Log.d("slj", "----获取好友信息:" + userInfo.toJSONString());
            if (userInfo != null) {
                nick = userInfo.getString("nick");
                String avatar = userInfo.getString("avatar");
                if (!TextUtils.isEmpty(avatar)) {
                    if (!avatar.contains("http")) {
                        avatar = HTConstant.URL_AVATAR + avatar;
                    }
                }
                Glide.with(context).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(holder.iv_avatar);
                //TODO  在申请消息的jsonobject里面是传有申请理由的，后续开发者可以按照需求处理这个申请理由
                String reasonTemp = userInfo.getString(HTConstant.CMD_ADD_REASON);
                if (!TextUtils.isEmpty(reasonTemp)) {
                    reason = reason + reasonTemp;
                } else {
                    reason = context.getString(R.string.Request_to_add_you_as_a_friend);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.tv_name.setText(nick);
        holder.tv_reason.setText(reason);
        if (msg.getStatus() == InviteMessage.Status.AGREED) {
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
        } else if (msg.getStatus() == InviteMessage.Status.REFUSED) {
            holder.tv_added.setText(context.getString(R.string.Refused));
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
        } else if (msg.getStatus() == InviteMessage.Status.BEREFUSED) {
            holder.tv_added.setText(context.getString(R.string.has_rejected));
            holder.tv_reason.setText(context.getString(R.string.already_rejected));
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
        } else if (msg.getStatus() == InviteMessage.Status.BEAGREED) {
            holder.tv_reason.setText(context.getString(R.string.Has_agreed_to_your_friend_request));
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
        } else {
            holder.tv_added.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_add.setTag(msg);
            final ViewHolder finalHolder = holder;
            final JSONObject finalUserInfo = userInfo;
            if (finalUserInfo != null) {
                holder.btn_add.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptInvitation(finalHolder.btn_add, msg, finalHolder.tv_added, finalUserInfo);
                    }

                });
            }
        }
        holder.rl_item_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = msg.getFrom();
                context.startActivity(new Intent(context, UserDetailsActivity.class).putExtra(HTConstant.JSON_KEY_HXID,from));
            }
        });
        holder.rl_item_add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteInviteMessge(msg);
                return true;
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_avatar;
        TextView tv_name;
        TextView tv_reason;
        TextView tv_added;
        Button btn_add;
        RelativeLayout rl_item_add;
    }

    /**
     * 同意好友请求
     *
     * @param button
     * @param
     */
    private void acceptInvitation(final Button button, final InviteMessage msg,
                                  final TextView textview, final JSONObject userInfo) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(context.getString(R.string.Are_agree_with));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", msg.getFrom()));
        new OkHttpUtils(context).post(params, HTConstant.URL_ADD_FRIEND, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                switch (code){
                    case 1:
                        User user = CommonUtils.Json2User(userInfo);
                        // 存入内存
                        ContactsManager.getInstance().getContactList().put(user.getUsername(), user);
                        // 存入db
                        UserDao dao = new UserDao(context);
                        dao.saveContact(user);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.ACTION_CONTACT_CHANAGED));
                        sendCmdAgreeMsg(button, msg, textview, pd);
                        break;
                    default:
                        pd.dismiss();
                        Toast.makeText(context,
                                context.getString(R.string.add_friend_failed), Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                pd.dismiss();
            }
        });
    }


    private void sendCmdAgreeMsg(final Button button, final InviteMessage msg,
                                 final TextView textview, final ProgressDialog pd) {
        JSONObject userJson = HTApp.getInstance().getUserJson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", 1001);
        JSONObject data = new JSONObject();
        data.put("userId", userJson.getString("userId"));
        data.put("nick", userJson.getString("nick"));
        data.put("avatar",userJson.getString("avatar"));
        data.put("role",userJson.getString(HTConstant.JSON_KEY_ROLE));
        data.put("teamId",userJson.getString("teamId"));
        jsonObject.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(msg.getFrom());
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(context, context.getString(R.string.add_friend_success), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }

            @Override
            public void onFailure() {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        Toast.makeText(context,
                              context.getString(R.string.add_friend_failed), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }
        });

        textview.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        button.setVisibility(View.GONE);
        msg.setStatus(Status.AGREED);
        // 更新db
        ContentValues values = new ContentValues();
        values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                .getStatus().ordinal());
        messgeDao.updateMessage(msg.getId(), values);

    }

    /**
     * 删除透传消息
     * @param msg
     */
    private void deleteInviteMessge(final InviteMessage msg){
        HTAlertDialog dialog = new HTAlertDialog(context,null,new String[]{context.getString(R.string.delete)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position){
                    case 0:
                        msgs.remove(msg);
                        messgeDao.deleteMessage(msg.getFrom());
                        notifyDataSetChanged();
                        break;
                }
            }
        });
    }
}
