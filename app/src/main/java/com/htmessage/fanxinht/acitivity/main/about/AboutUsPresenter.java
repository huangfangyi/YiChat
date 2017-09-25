package com.htmessage.fanxinht.acitivity.main.about;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.baidu.platform.comapi.map.E;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.widget.HTAlertDialog;


/**
 * 项目名称：FanXinHT0831
 * 类描述：AboutUsPresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/25 13:58
 * 邮箱:814326663@qq.com
 */
public class AboutUsPresenter implements AboutUsBasePresenter {

    private AboutUsView aboutUsView;
    //剪切板管理工具类
    private ClipboardManager mClipboardManager;
    //剪切板Data对象
    private ClipData mClipData;

    public AboutUsPresenter(AboutUsView aboutUsView) {
        this.aboutUsView = aboutUsView;
        this.aboutUsView.setPresenter(this);
        mClipboardManager = (ClipboardManager) aboutUsView.getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void destory() {
        aboutUsView = null;
    }

    @Override
    public void showCopyDialog(final String msg) {
        HTAlertDialog alert = new HTAlertDialog(aboutUsView.getBaseActivity(), null, new String[]{aboutUsView.getBaseActivity().getString(R.string.copy)});
        alert.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    //创建一个新的文本clip对象
                    mClipData = ClipData.newPlainText(aboutUsView.getBaseActivity().getString(R.string.call_us), msg);
                    //把clip对象放在剪贴板中
                    mClipboardManager.setPrimaryClip(mClipData);
                    aboutUsView.showToast(R.string.copy_success);
                }
            }
        });
    }

    @Override
    public void startQQ(String QQ) {
        try {
//            aboutUsView.showToast(R.string.open_qq);
            //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + QQ;//uin是发送过去的qq号码
            aboutUsView.getBaseActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            aboutUsView.showToast(R.string.open_qq_failed);
        }
    }

    @Override
    public void startCall(String mobile) {
        if (ActivityCompat.checkSelfPermission(aboutUsView.getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            //用intent启动拨打电话
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
            aboutUsView.getBaseActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            aboutUsView.showToast(R.string.start_call_failed);
        }
    }

    @Override
    public void start() {

    }
}
