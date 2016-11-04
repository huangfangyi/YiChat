package com.fanxin.huangfangyi.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.ui.BaseActivity;

public class AddFriendsPreActivity extends BaseActivity implements OnClickListener{
    private    TextView tv_search;
    private RelativeLayout rl_leida,rl_jianqun,rl_sacn,rl_lianxiren,rl_ggh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_addfriends_pre);
        initUI();
       setOnClick();
    }

    private void setOnClick() {
        tv_search.setOnClickListener(this);
        rl_leida.setOnClickListener(this);
        rl_jianqun.setOnClickListener(this);
        rl_sacn.setOnClickListener(this);
        rl_lianxiren.setOnClickListener(this);
        rl_ggh.setOnClickListener(this);
    }

    private void initUI() {
        tv_search = (TextView) findViewById(R.id.tv_search);
        rl_leida = (RelativeLayout) findViewById(R.id.rl_leida);
        rl_jianqun = (RelativeLayout) findViewById(R.id.rl_jianqun);
        rl_sacn = (RelativeLayout) findViewById(R.id.rl_sacn);
        rl_lianxiren = (RelativeLayout) findViewById(R.id.rl_lianxiren);
        rl_ggh = (RelativeLayout) findViewById(R.id.rl_ggh);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_search:
                startActivity(new Intent(AddFriendsPreActivity.this,AddFriendsNextActivity.class));
            break;
            case R.id.rl_leida:
                startActivity(new Intent(AddFriendsPreActivity.this,RadarActivity.class));
                break;
            case R.id.rl_jianqun:

                break;
            case R.id.rl_sacn:
                startActivity(new Intent(AddFriendsPreActivity.this,ScanCaptureActivity.class));
                break;
            case R.id.rl_lianxiren:

                break;
            case R.id.rl_ggh:

                break;
        }
    }
}
