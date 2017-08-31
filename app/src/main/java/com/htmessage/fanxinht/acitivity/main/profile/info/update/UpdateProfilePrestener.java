package com.htmessage.fanxinht.acitivity.main.profile.info.update;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.fanxinht.utils.Validator;

import java.util.ArrayList;
import java.util.List;

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
    private static final int TYPE_SIGN = 2;

    public UpdateProfilePrestener(UpdateProfileView updateProfileView) {
        this.updateProfileView = updateProfileView;
        this.updateProfileView.setPresenter(this);
    }

    @Override
    public void update() {
        updateInfo(getKey(updateProfileView.getType()),updateProfileView.getInputString(),updateProfileView.getDefultString());
    }

    @Override
    public void updateInfo(final String key, final String value , String defaultStr) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value) || ((defaultStr != null) && value.equals(defaultStr))) {
            return;
        }
        if(HTConstant.JSON_KEY_FXID.equals(key)){
            if(Validator.isChinese(value)){
                updateProfileView.onUpdateFailed(updateProfileView.getBaseActivity().getString(R.string.mixin_can_not_has_chinese));
                return;
            }
        }
        if (value.length() > 30) {
            updateProfileView.onUpdateFailed(updateProfileView.getBaseActivity().getString(R.string.string_not_30));
            return;
        }
        final Dialog progressDialog =  HTApp.getInstance().createLoadingDialog(updateProfileView.getBaseActivity(),updateProfileView.getBaseActivity().getString(R.string.are_uploading));
        progressDialog.show();
        //本地用户资料
        final JSONObject userJson = HTApp.getInstance().getUserJson();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param(key, value));
        new OkHttpUtils(updateProfileView.getBaseActivity()).post(params, HTConstant.URL_UPDATE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                if (code == 1) {
                    userJson.put(key, value);
                    HTApp.getInstance().setUserJson(userJson);
                    LocalBroadcastManager.getInstance(updateProfileView.getBaseActivity()).sendBroadcast(new Intent(IMAction.ACTION_UPDATE_INFO).putExtra(HTConstant.KEY_CHANGE_TYPE,key).putExtra(key,value));
                    updateProfileView.onUpdateSuccess(updateProfileView.getBaseActivity().getString(R.string.update_success));
                } else {
                    updateProfileView.onUpdateFailed(updateProfileView.getBaseActivity().getString(R.string.upload_failed)+code);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                updateProfileView.onUpdateFailed(errorMsg);
                progressDialog.dismiss();
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
                title = updateProfileView.getBaseActivity().getString(R.string.change_mixin);
                break;
            case TYPE_SIGN:
                title = updateProfileView.getBaseActivity().getString(R.string.change_personalized_signature);
                break;
        }
        return title;
    }

    @Override
    public String getKey(int type) {
        String key = "";
        switch (type) {
            case TYPE_NICK:
                key = HTConstant.JSON_KEY_NICK;
                break;
            case TYPE_FXID:
                key = HTConstant.JSON_KEY_FXID;
                break;
            case TYPE_SIGN:
                key = HTConstant.JSON_KEY_SIGN;
                break;
        }
        return key;
    }


    @Override
    public void onDestory() {
        updateProfileView = null;
    }
}
