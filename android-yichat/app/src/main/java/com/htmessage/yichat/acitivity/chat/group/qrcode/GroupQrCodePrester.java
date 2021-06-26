package com.htmessage.yichat.acitivity.chat.group.qrcode;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.ImageUtils;
import com.yzq.zxinglibrary.encode.CodeCreator;

/**
 * 项目名称：fanxinht
 * 类描述：GroupQrCodePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:44
 * 邮箱:814326663@qq.com
 */
public class GroupQrCodePrester implements GroupQrCodeBasePrester {

    private GroupQrCodeView codeView;

    public GroupQrCodePrester(GroupQrCodeView codeView) {
        this.codeView = codeView;
        this.codeView.setPresenter(this);
    }

    @Override
    public void onDestory() {
        codeView = null;
    }

    @Override
    public void CreateQrCode() {
        JSONObject userJson = codeView.getIntenetData();
        String groupId = userJson.getString("groupId");
        String groupAvatar = userJson.getString("groupAvatar");
        String groupName = userJson.getString("groupName");
        String creator = userJson.getString("creator");
        JSONObject allobj = new JSONObject();
        allobj.put("codeType", 3);
        JSONObject object = new JSONObject();
        object.put("gid", groupId);
        object.put("name", groupName);
        object.put("creator", creator);
        object.put("imgurlde", TextUtils.isEmpty(groupAvatar) ? "false" : groupAvatar);
        allobj.put("data", object.toJSONString());
        try {
            Bitmap bitmap = CodeCreator.createQRCode(allobj.toJSONString(), 500, 500,null);
            codeView.showQrCode(bitmap);
        } catch (Exception e) {
            codeView.showError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void saveQrCode() {
        CommonUtils.showDialogNumal(codeView.getBaseActivity(),codeView.getBaseActivity().getString(R.string.saving));
        new Thread(){
            @Override
            public void run() {
                super.run();
                View layout = codeView.getFrameLayout();
                Bitmap bitmap = ImageUtils.makeView2Bitmap(layout);
                ImageUtils.saveViewBitMap(codeView.getBaseContext(), bitmap, new ImageUtils.OnSaveViewListener() {
                    @Override
                    public void success(String path) {
                        showToast(R.string.saving_successful);
                    }

                    @Override
                    public void error(String error) {
                        showToast(R.string.saving_failed);
                    }
                });
            }
        }.start();
    }

    @Override
    public void start() {

    }
    private void showToast(final Object msg){
        codeView.getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(codeView.getBaseActivity(),msg);
            }
        });
    }
}
