package com.htmessage.yichat.acitivity.main.profile.info.update;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;

/**
 * 项目名称：HTOpen
 * 类描述：UpdateProfilePrestener 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 13:38
 * 邮箱:814326663@qq.com
 */
public class UpdateProfilePrestener implements UpdateProfileBasePrester {
    private UpdateProfileView updateProfileView;
    private static final int TYPE_NICK = 0;
    private static final int TYPE_FXID = 1;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (updateProfileView == null) {
                return;
            }

            switch (msg.what) {
                case 1000:
                    CommonUtils.cencelDialog();
                    Bundle bundle=msg.getData();
                    updateProfileView.show(R.string.update_success);
                    if(bundle!=null){
                        updateProfileView.onUpdateSuccess(bundle.getString("key"),bundle.getString("value"));
                    }




                    break;
                case 1001:
                    //更新失败
                    CommonUtils.cencelDialog();

                    int resId = msg.arg1;
                    updateProfileView.show(resId);


                    break;
            }
        }
    };


    public UpdateProfilePrestener(UpdateProfileView updateProfileView) {
        this.updateProfileView = updateProfileView;
        this.updateProfileView.setPresenter(this);
    }

    @Override
    public void update() {
        String inputString = updateProfileView.getInputString();
        if(updateProfileView.getType() == 1){
            if(Validator.isChineseStr(inputString)){
                updateProfileView.onUpdateFailed(updateProfileView.getBaseActivity().getString(R.string.mixin_can_not_has_chinese));
                return;
            }
        }
        updateInfo(getKey(updateProfileView.getType()),inputString,updateProfileView.getDefultString());
    }

    @Override
    public void updateInfo(final String key, final String value , String defaultStr) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value) || ((defaultStr != null) && value.equals(defaultStr))) {
            return;
        }
        if("appId".equals(key)){
            if(Validator.isChineseStr(value)){
                updateProfileView.onUpdateFailed(updateProfileView.getBaseActivity().getString(R.string.mixin_can_not_has_chinese));
                return;
            }
        }
        if (value.length() > 30) {
            updateProfileView.onUpdateFailed(updateProfileView.getBaseActivity().getString(R.string.string_not_30));
            return;
        }
        CommonUtils.showDialogNumal(updateProfileView.getBaseActivity(),updateProfileView.getBaseActivity().getString(R.string.are_uploading));
        //本地用户资料


        JSONObject data=new JSONObject();
        data.put(key,value);
        ApiUtis.getInstance().postJSON(data, Constant.URL_INFO_UPDATE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject userJSON = UserManager.get().getMyUser();
                    userJSON.put(key, value);
                    UserManager.get().saveMyUser(userJSON);
                    Message message = handler.obtainMessage();
                    Bundle bundle=new Bundle();
                    bundle.putString("key",key);
                    bundle.putString("value",value);
                    message.what = 1000;
                    message.setData(bundle);
                     message.sendToTarget();

                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.update_fail;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


    }

    @Override
    public String getTitle(int type) {
        String title = "";
        switch (type) {
            case TYPE_NICK:
                title = updateProfileView.getBaseActivity().getString(R.string.change_nick);
                break;
            case TYPE_FXID:
                title = updateProfileView.getBaseActivity().getString(R.string.change_appId);
                break;

        }
        return title;
    }

    @Override
    public String getKey(int type) {
        String key = "";
        switch (type) {
            case TYPE_NICK:
                key = "nick";
                break;
            case TYPE_FXID:
                key = "appId";
                break;

        }
        return key;
    }


    @Override
    public void onDestory() {
        updateProfileView = null;
    }
}
