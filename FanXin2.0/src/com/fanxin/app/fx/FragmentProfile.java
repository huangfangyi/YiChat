package com.fanxin.app.fx;

import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.comments.SocialFriendActivity;
import com.fanxin.app.comments.SocialMainActivity;
import com.fanxin.app.fx.others.LoadUserAvatar;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SdCardPath")
public class FragmentProfile extends Fragment {

    private LoadUserAvatar avatarLoader;
    private String avatar = "";
    private ImageView iv_avatar;
    private TextView tv_name;
    TextView tv_fxid;
    String fxid;
    String nick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        avatarLoader = new LoadUserAvatar(getActivity(), "/sdcard/fanxin/");
        RelativeLayout re_myinfo = (RelativeLayout) getView().findViewById(
                R.id.re_myinfo);
        re_myinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),
                        MyUserInfoActivity.class));
            }

        });
        RelativeLayout re_setting = (RelativeLayout) getView().findViewById(
                R.id.re_setting);
        re_setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }

        });
        
        RelativeLayout re_money_bag = (RelativeLayout) getView().findViewById(
                R.id.re_money_bag);
        re_money_bag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WalletActivity.class));
            }

        });
        nick = LocalUserInfo.getInstance(getActivity()).getUserInfo("nick");
        fxid = LocalUserInfo.getInstance(getActivity()).getUserInfo("fxid");

        avatar = LocalUserInfo.getInstance(getActivity()).getUserInfo("avatar");
        iv_avatar = (ImageView) re_myinfo.findViewById(R.id.iv_avatar);
        tv_name = (TextView) re_myinfo.findViewById(R.id.tv_name);
        tv_fxid = (TextView) re_myinfo.findViewById(R.id.tv_fxid);
        tv_name.setText(nick);
        if (fxid.equals("0")) {
            tv_fxid.setText("微信号：未设置");
        } else {
            tv_fxid.setText("微信号:" + fxid);
        }
        showUserAvatar(iv_avatar, avatar);
        
        
        getView().findViewById(R.id.re_xiangce).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                      String userID=MYApplication.getInstance().getUserName(); 
                      if(!TextUtils.isEmpty(userID)){
                       
                          startActivity(new Intent(getActivity(),SocialFriendActivity.class).putExtra("friendID", userID));
                        
                      }
            }
            
            
        });
        getView().findViewById(R.id.re_rewards).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                   
                          startActivity(new Intent(getActivity(),AlipayMeActivity.class));
                        
                  
            }
            
            
        });
        
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

    @Override
    public void onResume() {
        super.onResume();
        String vatar_temp = LocalUserInfo.getInstance(getActivity())
                .getUserInfo("avatar");
        if (!vatar_temp.equals(avatar)) {
            showUserAvatar(iv_avatar, avatar);
        }

        String nick_temp = LocalUserInfo.getInstance(getActivity())
                .getUserInfo("nick");
        String fxid_temp = LocalUserInfo.getInstance(getActivity())
                .getUserInfo("fxid");
        if (!nick_temp.equals(nick)) {
            tv_name.setText(nick_temp);
        }
        if (!fxid_temp.equals(fxid)) {
            if (fxid_temp.equals("0")) {
                tv_fxid.setText("微信号：未设置");
            } else {
                tv_fxid.setText("微信号:" + fxid_temp);
            }
        }
    }

}
