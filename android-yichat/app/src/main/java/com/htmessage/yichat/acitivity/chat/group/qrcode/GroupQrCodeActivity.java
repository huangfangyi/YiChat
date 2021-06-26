package com.htmessage.yichat.acitivity.chat.group.qrcode;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：fanxinht
 * 类描述：GroupQrCodeActivity 描述:
 * 创建人：songlijie
 * 创建时间：2018/3/23 9:16
 * 邮箱:814326663@qq.com
 */
public class GroupQrCodeActivity extends BaseActivity {
    private GroupQrCodePrester codePrester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.group_qrcode);
        GroupQrCodeFragment fragment = (GroupQrCodeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new GroupQrCodeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
        codePrester = new GroupQrCodePrester(fragment);
//        showRightTextView(R.string.save, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (codePrester != null) {
//                    codePrester.saveQrCode();
//                }
//            }
//        });
    }

}
