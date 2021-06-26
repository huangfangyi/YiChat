package com.htmessage.wetalk.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI wxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flash_activity);
        //		微信登录API
        wxapi = WXAPIFactory.createWXAPI(this, HTConstant.WX_APP_ID_LOGIN);

        wxapi.registerApp(HTConstant.WX_APP_ID_LOGIN);
        //如果没回调onResp，八成是这句没有写
        wxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxapi.handleIntent(intent, this);
    }

    // 微信发送消息给app，app接受并处理的回调函数
    @Override
    public void onReq(BaseReq baseReq) {

    }

    // app发送消息给微信，微信返回的消息回调函数,根据不同的返回码来判断操作是否成功
    @Override
    public void onResp(BaseResp resp) {
        Log.d("resp---", resp.errCode + "----" + resp.toString());
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.PAY_BY_WECHAT_RESULT).putExtra(HTConstant.KEY_PAY_WECHAT, String.valueOf(resp.errCode)));
            finish();
        }
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    CommonUtils.showToastShort(this, R.string.login_cancle);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_OK:
                    // 获取到code
                    String code = ((SendAuth.Resp) resp).code;
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(IMAction.LOGIN_BY_WECHAT_RESULT).putExtra("WX_RESULT_CODE", code));
                    finish();
                    break;

                default:
                    CommonUtils.showToastShort(this, R.string.login_cancle);

                    finish();
                    break;

            }
        } else  {
                finish();
        }
    }
}