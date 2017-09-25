package com.htmessage.fanxinht.acitivity.main.about;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.CommonUtils;

/**
 * 项目名称：FanXinHT0831
 * 类描述：AboutUsFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/25 13:54
 * 邮箱:814326663@qq.com
 */
public class AboutUsFragment extends Fragment implements AboutUsView, OnLongClickListener, OnClickListener {
    private AboutUsPresenter presenter;
    private TextView tv_phone, tv_qq, tv_email, tv_title;
    private Button btn_copy_phone, btn_copy_qq, btn_copy_email;
    private LinearLayout ll_phone, ll_qq, ll_email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }

    private void initView() {
        tv_title = (TextView) getView().findViewById(R.id.tv_title);
        tv_phone = (TextView) getView().findViewById(R.id.tv_phone);
        tv_qq = (TextView) getView().findViewById(R.id.tv_qq);
        tv_email = (TextView) getView().findViewById(R.id.tv_email);
        btn_copy_phone = (Button) getView().findViewById(R.id.btn_copy_phone);
        btn_copy_qq = (Button) getView().findViewById(R.id.btn_copy_qq);
        btn_copy_email = (Button) getView().findViewById(R.id.btn_copy_email);
        ll_phone = (LinearLayout) getView().findViewById(R.id.ll_phone);
        ll_qq = (LinearLayout) getView().findViewById(R.id.ll_qq);
        ll_email = (LinearLayout) getView().findViewById(R.id.ll_email);
    }

    private void setListener() {
        btn_copy_phone.setOnClickListener(this);
        btn_copy_qq.setOnClickListener(this);
        btn_copy_email.setOnClickListener(this);

        ll_phone.setOnClickListener(this);
        ll_qq.setOnClickListener(this);
//        ll_email.setOnClickListener(this);

//        ll_phone.setOnLongClickListener(this);
//        ll_qq.setOnLongClickListener(this);
        ll_email.setOnLongClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter = new AboutUsPresenter(this);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.ll_phone:
                presenter.showCopyDialog(getMobile());
                break;
            case R.id.ll_qq:
                presenter.showCopyDialog(getQQ());
                break;
            case R.id.ll_email:
                presenter.showCopyDialog(getEmail());
                break;
        }
        return true;
    }

    @Override
    public void showToast(int msgId) {
        CommonUtils.showToastShort(getActivity(), msgId);
    }

    @Override
    public void setPresenter(AboutUsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    private String getMobile() {
        return tv_phone.getText().toString().trim();
    }

    private String getQQ() {
        return tv_qq.getText().toString().trim();
    }

    private String getEmail() {
        return tv_email.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy_phone:
                presenter.showCopyDialog(getMobile());
                break;
            case R.id.btn_copy_qq:
                presenter.showCopyDialog(getQQ());
                break;
            case R.id.btn_copy_email:
                presenter.showCopyDialog(getEmail());
                break;
            case R.id.ll_phone:
                presenter.startCall(getMobile());
                break;
            case R.id.ll_qq:
                presenter.startQQ(getQQ());
                break;
            case R.id.ll_email:
                presenter.showCopyDialog(getEmail());
                break;
        }
    }
}
