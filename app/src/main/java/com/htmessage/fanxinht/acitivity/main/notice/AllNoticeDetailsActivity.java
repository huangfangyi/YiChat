package com.htmessage.fanxinht.acitivity.main.notice;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：YiChat
 * 类描述：AllNoticeDetailsActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/3 14:53
 * 邮箱:814326663@qq.com
 */
public class AllNoticeDetailsActivity extends BaseActivity {
    private TextView tv_notice_title, tv_notice_time, tv_notice_cotent,tv_title;
    private JSONObject object;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_notice_detail);
        getData();
        initView();
        iniData();
        setListener();

    }

    private void getData() {
        String notice = getIntent().getStringExtra("notice");
        if (TextUtils.isEmpty(notice)) {
            finish();
            return;
        }
        object = JSONObject.parseObject(notice);
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.notice_detail);
        tv_notice_title = (TextView) findViewById(R.id.tv_notice_title);
        tv_notice_time = (TextView) findViewById(R.id.tv_notice_time);
        tv_notice_cotent = (TextView) findViewById(R.id.tv_notice_cotent);
    }

    private void iniData() {
        tv_notice_title.setText(object.getString("title"));
        tv_notice_time.setText(object.getString("time"));
        String content = object.getString("content");
        tv_notice_cotent.setText(Html.fromHtml(content));
    }

    private void setListener() {

    }
}
