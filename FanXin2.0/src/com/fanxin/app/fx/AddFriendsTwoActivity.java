package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

 










import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

public class AddFriendsTwoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_two);

        final RelativeLayout re_search = (RelativeLayout) this
                .findViewById(R.id.re_search);
        final TextView tv_search = (TextView) re_search
                .findViewById(R.id.tv_search);
        final EditText et_search = (EditText) this.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > 0) {
                    re_search.setVisibility(View.VISIBLE);
                    tv_search.setText(et_search.getText().toString().trim());
                } else {

                    re_search.setVisibility(View.GONE);
                    tv_search.setText("");

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        re_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String uid = et_search.getText().toString().trim();
                if (uid == null || uid.equals("")) {
                    return;
                }
                searchUser(uid);

            }

        });

    }

    private void searchUser(String uid) {
        final ProgressDialog dialog = new ProgressDialog(
                AddFriendsTwoActivity.this);
        dialog.setMessage("正在查找联系人...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        
        
        
      
        
        
        Map<String, String> map = new HashMap<String, String>();

        map.put("uid", uid);

        LoadDataFromServer task = new LoadDataFromServer(
                AddFriendsTwoActivity.this, Constant.URL_Search_User, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    dialog.dismiss();
                    int code = data.getInteger("code");
                    if (code == 1) {

                        JSONObject json = data.getJSONObject("user");
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");
                        String sex = json.getString("sex");

                        String hxid = json.getString("hxid");

                        Intent intent = new Intent();
                        intent.putExtra("hxid", hxid);
                        intent.putExtra("avatar", avatar);
                        intent.putExtra("nick", nick);

                        intent.putExtra("sex", sex);
                        intent.setClass(AddFriendsTwoActivity.this,
                                UserInfoActivity.class);
                        startActivity(intent);
                    } else if (code == 2) {

                        Toast.makeText(AddFriendsTwoActivity.this, "用户不存在",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {

                        Toast.makeText(AddFriendsTwoActivity.this,
                                "服务器查询错误...", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(AddFriendsTwoActivity.this,
                                "服务器繁忙请重试...", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    dialog.dismiss();
                    Toast.makeText(AddFriendsTwoActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void back(View view) {
        finish();
    }

}