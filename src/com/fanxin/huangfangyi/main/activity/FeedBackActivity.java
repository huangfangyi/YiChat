package com.fanxin.huangfangyi.main.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.ui.BaseActivity;

/**
 * 项目名称：QianShanDoctor
 * 类描述：FeedBackActivity 反馈
 * 创建人：slj
 * 创建时间：2016-6-28 14:45
 * 修改人：slj
 * 修改时间：2016-6-28 14:45
 * 修改备注：
 * 邮箱:slj@bjlingzhuo.com
 */
public class FeedBackActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{
    private Context mContext = null;//上下文对象
    private RadioGroup radioTypeId;
    private RadioButton radioTypeId1;
    private RadioButton radioTypeId2;
    private RadioButton radioTypeId3;
    private EditText fb_et_content;
    private Button btnSubmit;
    private String content;//反馈内容
    private int typeId = 1;//默认为1
    private ImageView iv_back,iv_camera;
    private TextView tv_title;
    private RelativeLayout titleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        DemoApplication.getInstance().saveActivity(this);
        mContext = this;
        getData();
        initView();
        initData();
        setOnClick();
    }
    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        tv_title = (TextView) findViewById(R.id.tv_title);
        titleBar = (RelativeLayout) findViewById(R.id.title);

        radioTypeId = (RadioGroup) findViewById(R.id.radioTypeId);
        radioTypeId1 = (RadioButton) findViewById(R.id.radioTypeId1);
        radioTypeId2 = (RadioButton) findViewById(R.id.radioTypeId2);
        radioTypeId3 = (RadioButton) findViewById(R.id.radioTypeId3);
        fb_et_content = (EditText) findViewById(R.id.fb_et_content);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
    }

    public void initData() {
        iv_camera.setVisibility(View.GONE);
        this.tv_title.setText(R.string.feeBack);
    }

    public void getData() {

    }

    public void setOnClick() {
// 判断radiobutton的点击事件
        radioTypeId.setOnCheckedChangeListener(this);
        btnSubmit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.iv_back:
                back(v);
                break;
            case  R.id.btnSubmit:
                content = fb_et_content.getText().toString().trim();
                // 判断输入内容是否为空
                if (TextUtils.isEmpty(content)) {
                    // 说明输入内容为空
                    Toast.makeText(mContext, getString(R.string.fb_message_not_be_null), Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 功能已做,缺少网络请求 需要在此添加网路请求 然后判断.
                    Toast.makeText(mContext, getString(R.string.fb_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int buttonId = group.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) FeedBackActivity.this
                .findViewById(buttonId);
        switch (rb.getId()) {
            case R.id.radioTypeId1:
                typeId = 1;
                break;
            case R.id.radioTypeId2:
                typeId = 2;
                break;
            case R.id.radioTypeId3:
                typeId = 3;
                break;
        }
    }
}
