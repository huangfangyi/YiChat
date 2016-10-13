package com.fanxin.huangfangyi.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.activity.PasswordResetActivity;
import com.fanxin.huangfangyi.main.activity.ProfileActivity;
import com.fanxin.huangfangyi.main.activity.SettingsActivity;
import com.fanxin.huangfangyi.main.moments.SocialFriendActivity;

public class FragmentProfile extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }
    private void initView(){
        ImageView ivAvatar= (ImageView) getView().findViewById(R.id.iv_avatar);
        TextView tvNick= (TextView) getView().findViewById(R.id.tv_name);
        TextView tvFxid= (TextView) getView().findViewById(R.id.tv_fxid);

        JSONObject jsonObject=DemoApplication.getInstance().getUserJson();
        Glide.with(this).load(FXConstant.URL_AVATAR+ jsonObject.getString(FXConstant.JSON_KEY_AVATAR)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(ivAvatar);
        tvNick.setText(jsonObject.getString(FXConstant.JSON_KEY_NICK));
        String fxid=jsonObject.getString(FXConstant.JSON_KEY_FXID);
        if(TextUtils.isEmpty(fxid)){
            fxid="未设置";
        }
        fxid="微信号:"+fxid;
        tvFxid.setText(fxid);
    }
    private void setListener(){

        getView().findViewById(R.id.re_myinfo).setOnClickListener(this);
        getView().findViewById(R.id.re_setting).setOnClickListener(this);
        getView().findViewById(R.id.re_wallet).setOnClickListener(this);
        getView().findViewById(R.id.re_xiangce).setOnClickListener(this);
        getView().findViewById(R.id.re_fanxin).setOnClickListener(this);
        getView().findViewById(R.id.re_xiangce).setOnClickListener(this);
        getView().findViewById(R.id.re_yunzhanghu).setOnClickListener(this);
        getView().findViewById(R.id.re_find_password).setOnClickListener(this);

     }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.re_myinfo:
                startActivityForResult(new Intent(getActivity(), ProfileActivity.class),0);
                break;

            case R.id.re_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.re_wallet:
                RedPacketUtil.startChangeActivity(getActivity());
                break;

            case R.id.re_xiangce:
                startActivity(new Intent(getActivity(), SocialFriendActivity.class).putExtra("friendID", DemoHelper.getInstance().getCurrentUsernName()));
                break;
            case R.id.re_yunzhanghu:
                joinQQGroup("ycxd0w_eXmTbKIjyDdHb5Dy_-ZhY8E7t");
                break;
            case R.id.re_fanxin:
                joinQQGroup("5QH7bwWtFt5dCwIlIp__y4nuVF1rggp1");
                break;
            case R.id.re_find_password:
                startActivity(new Intent(getActivity(), PasswordResetActivity.class).putExtra("isReset",true));
                break;

        }
    }


    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(getContext(),"本设备未安装手机QQ",Toast.LENGTH_LONG).show();
            return false;
        }
    }


}
