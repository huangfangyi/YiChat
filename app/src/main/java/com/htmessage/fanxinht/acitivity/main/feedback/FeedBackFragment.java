package com.htmessage.fanxinht.acitivity.main.feedback;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.CommonUtils;

/**
 * 项目名称：PersonalTailor
 * 类描述：FeedBackFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/31 16:27
 * 邮箱:814326663@qq.com
 */
public class FeedBackFragment extends Fragment implements FeedBackView, View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    //    private RadioGroup radioTypeId;
//    private RadioButton radioTypeId1;
//    private RadioButton radioTypeId2;
//    private RadioButton radioTypeId3;
    private EditText fb_et_content;
    private Button btnSubmit;
    private int typeId = 1;//默认为1
    private FeedBackPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View feedview = inflater.inflate(R.layout.fragment_feedback, container, false);
        initView(feedview);
        setListener();
        return feedview;
    }

    private void setListener() {
        btnSubmit.setOnClickListener(this);
        // 判断radiobutton的点击事件
//        radioTypeId.setOnCheckedChangeListener(this);
    }

    private void initView(View feedview) {
//        radioTypeId = (RadioGroup) feedview.findViewById(R.id.radioTypeId);
//        radioTypeId1 = (RadioButton) feedview.findViewById(R.id.radioTypeId1);
//        radioTypeId2 = (RadioButton) feedview.findViewById(R.id.radioTypeId2);
//        radioTypeId3 = (RadioButton) feedview.findViewById(R.id.radioTypeId3);
        fb_et_content = (EditText) feedview.findViewById(R.id.fb_et_content);
        btnSubmit = (Button) feedview.findViewById(R.id.btnSubmit);
    }

    @Override
    public void showToast(int resId) {
        CommonUtils.showToastShort(getActivity(), resId);
    }

    @Override
    public void setPresenter(FeedBackPresenter presenter) {
        this.presenter = presenter;
    }

    private String getContent() {
        return fb_et_content.getText().toString().trim();
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                // 判断输入内容是否为空
                if (TextUtils.isEmpty(getContent())) {
                    // 说明输入内容为空
                    showToast(R.string.fb_content_dont_empty);
                    return;
                }
                presenter.sendFeedBack(getContent());
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
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
