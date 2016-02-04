package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;
 










import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.db.UserDao;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadUserAvatar;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.util.HanziToPinyin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {
    private LoadUserAvatar avatarLoader;
    boolean is_friend = false;
     String hxid;
    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        avatarLoader = new LoadUserAvatar(this, "/sdcard/fanxin/");
        Button btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
        final String nick = this.getIntent().getStringExtra("nick");
        final String avatar = this.getIntent().getStringExtra("avatar");
        String sex = this.getIntent().getStringExtra("sex");
        hxid = this.getIntent().getStringExtra("hxid");
        if (nick != null && avatar != null && sex != null && hxid != null) {
            tv_name.setText(nick);
            if (sex.equals("1")) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (sex.equals("2")) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            } else {
                iv_sex.setVisibility(View.GONE);
            }
            if (MYApplication.getInstance().getContactList()
                    .containsKey(hxid)) {
                is_friend = true;
                btn_sendmsg.setText("发消息");
            }

            showUserAvatar(iv_avatar, avatar);
        }

        btn_sendmsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(hxid.equals(LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("hxid"))){
                    Toast.makeText(getApplicationContext(), "不能和自己聊天。。", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (is_friend) {
                    Intent intent = new Intent();
                    intent.putExtra("userId", hxid);
                    intent.putExtra("userAvatar", avatar);
                    intent.putExtra("userNick", nick);

                    intent.setClass(UserInfoActivity.this, ChatActivity.class);
                    startActivity(intent);
                } else {

                    Intent intent = new Intent();
                    intent.putExtra("hxid", hxid);
                    // intent.putExtra("avatar", avatar);
                    // intent.putExtra("nick", nick);

                    intent.setClass(UserInfoActivity.this,
                            AddFriendsFinalActivity.class);
                    startActivity(intent);

                }
            }

        });
        
        Button btn_new= (Button) this.findViewById(R.id.btn_new);
        btn_new.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                     if(hxid.equals(LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("hxid"))){
                         Toast.makeText(getApplicationContext(), "不能和自己聊天。。", Toast.LENGTH_SHORT).show();
                         return ;
                     }
                    Intent intent = new Intent();
                    intent.putExtra("userId", hxid);
           
                    intent.putExtra("userNick", nick);
                    intent.putExtra("userAvatar", avatar);
                    intent.setClass(UserInfoActivity.this, ChatActivity.class);
                    startActivity(intent);
             
            }

        });
       refresh();
    }

    private void showUserAvatar(ImageView iamgeView, String avatar) {
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

    public void back(View view) {

        finish();
    }
   
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
    
    
    
    private void refresh(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("uid", hxid);

        LoadDataFromServer task = new LoadDataFromServer(
                UserInfoActivity.this, Constant.URL_Search_User, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    
                    int code = data.getInteger("code");
                    if (code == 1) {

                        JSONObject json = data.getJSONObject("user");
                        String hxid = json.getString("hxid");
                        String fxid = json.getString("fxid");
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");
                        String sex = json.getString("sex");
                        String region = json.getString("region");
                        String sign = json.getString("sign");
                        String tel = json.getString("tel");

                        User user = new User();
                        user.setFxid(fxid);
                        user.setUsername(hxid);
                        user.setBeizhu("");
                        user.setNick(nick);
                        user.setRegion(region);
                        user.setSex(sex);
                        user.setTel(tel);
                        user.setSign(sign);
                        user.setAvatar(avatar);
                        setUserHearder(hxid, user);
                        
                       
                        UserDao dao = new UserDao(UserInfoActivity.this);
                        dao.saveContact(user);
                        MYApplication.getInstance().getContactList().put(hxid, user);
                        
                    } 

                } catch (JSONException e) {
                     
                    e.printStackTrace();
                }
            }
        });
    }
    
}
