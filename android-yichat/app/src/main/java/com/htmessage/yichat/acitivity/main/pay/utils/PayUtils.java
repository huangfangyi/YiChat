package com.htmessage.yichat.acitivity.main.pay.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.main.pay.aliutils.PayResult;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.List;
import java.util.Map;

public class PayUtils {

    public static void getTnFromUnionPayService(final Activity context,String money){
//        new OkHttpUtils(context).requestFromGet(HTConstant.TN_URL_01, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e("----银联支付:"+jsonObject.toJSONString());
//                if (jsonObject.containsKey("result")){
//                    String result = jsonObject.getString("result");
//                    payByUnionPay(result,context);
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });
    }

    /**
     * 银联支付  如果安装有APK则APK支付 如果未安装APK则JAR支付
     * @param tn
     * @param context
     */
    private static void payByUnionPay(String tn, Activity context) {
//        if (context ==null){
//            return;
//        }
//        boolean installed = UPPayAssistEx.checkInstalled(context);
//        if (installed){
//            UPPayAssistEx.startPay(context, null, null, tn, HTConstant.mMode);
//        }else{
//            UPPayAssistEx.startPayByJAR(context, PayActivity.class,null,null,tn, HTConstant.mMode);
//        }
    }

    /**
     * 处理支付返回的结果
     * @param data
     */
    public static void onPayResult(Intent data,onPayResultListenr listenr){
//        /*************************************************
//         * 步骤3：处理银联手机支付控件返回的支付结果
//         ************************************************/
//        if (data == null) {
//            listenr.faile();
//            return;
//        }
//        /*
//         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
//         */
//        String str = data.getExtras().getString("pay_result");
//        if (str.equalsIgnoreCase("success")) {
//            // 如果想对结果数据验签，可使用下面这段代码，但建议不验签，直接去商户后台查询交易结果
//            // result_data结构见c）result_data参数说明
//            if (data.hasExtra("result_data")) {
//                String result = data.getExtras().getString("result_data");
//                JSONObject resultJson = JSONObject.parseObject(result);
//                LoggerUtils.e("----银联支付结果:"+result);
//                String sign = resultJson.getString("sign");
//                String dataOrg = resultJson.getString("data");
//                // 此处的verify建议送去商户后台做验签
//                // 如要放在手机端验，则代码必须支持更新证书
//                boolean ret = verify(dataOrg, sign, HTConstant.mMode);
//                if (ret) {
//                    // 验签成功，显示支付结果
//                    listenr.success();
//                } else {
//                    // 验签失败
//                    listenr.faile();
//                }
//            }
//            // 结果result_data为成功时，去商户后台查询一下再展示成功
//        } else if (str.equalsIgnoreCase("fail")) {
//            listenr.faile();
//        } else if (str.equalsIgnoreCase("cancel")) {
//            listenr.cancle();
//        }
    }

    private static boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;

    }
    public interface onPayResultListenr{
        void success();
        void faile();
        void cancle();
    }

    /**
     * 微信支付网络支付调用接口 获取到服务器返回的订单号
     *
     * @param aty    activity
     * @param object 从服务器请求下来的数据
     */
    private static void pay(Activity aty, JSONObject object) {
        PayReq req = new PayReq();
        req.appId = HTConstant.WX_APP_ID;//APPID
        req.partnerId = object.getString("partnerid");//商户号
        req.prepayId = object.getString("prepayid");
        req.packageValue = object.getString("package");
        req.nonceStr = object.getString("noncestr");
        req.timeStamp = object.getString("timestamp");
     //   req.extData = object.getString("out_trade_no"); // optional
        req.sign = object.getString("sign");
        sendPayReq(aty, req,HTConstant.WX_APP_ID);
    }

    /**
     * 请求微信支付
     *
     * @param aty 当前的activity
     * @param amount 钱数
     * @param type 1--充值唤起，2-微信直接发红
     */
    public static void payByWeChat(final Activity aty, final String amount,int type) {
        if (isWeixinAvilible(aty)) {//判断是否安装了微信
            CommonUtils.showDialogNumal(aty, aty.getString(R.string.are_authing));

            JSONObject body = new JSONObject();
            body.put("money", amount);
            body.put("type", type);
            ApiUtis.getInstance().postJSON(body, Constant.PAY_BY_WECHAT, new ApiUtis.HttpCallBack() {
                @Override
                public void onResponse(final JSONObject jsonObject) {
                    if (aty != null && !aty.isDestroyed()) {
                        CommonUtils.cencelDialog();
                        String code = jsonObject.getString("code");
                        if ("0".equals(code)) {
                            aty.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    pay(aty, data);
                                }
                            });
                        } else {

                            aty.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showToastShort(aty, R.string.pay_sending_error);

                                }
                            });
                        }


                    }
                }

                @Override
                public void onFailure(int errorCode) {
                    if (aty != null && !aty.isDestroyed()) {
                        aty.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showToastShort(aty, R.string.pay_sending_error);

                            }
                        });
                    }
                }
            });
        }
//            List<Param> params = new ArrayList<>();
//            params.add(new Param("userId", HTApp.getInstance().getUsername()));
//            params.add(new Param("amount", amount));
////            params.add(new Param("phoneIp", CommonUtils.getHostIP()));
//            new OkHttpUtils(aty).post(params, HTConstant.PAY_BY_WECHAT, new OkHttpUtils.HttpCallBack() {
//                @Override
//                public void onResponse(JSONObject jsonObject) {
//                    CommonUtils.cencelDialog();
//                    int code = jsonObject.getIntValue("code");
//                    switch (code) {
//                        case 1:
//                            JSONObject data = jsonObject.getJSONObject("data");
//                            pay(aty, data);
//                            break;
//                        default:
//                            CommonUtils.showToastShort(aty, R.string.pay_sending_error);
//                            break;
//
//                    }
//                }
//
//                @Override
//                public void onFailure(String errorMsg) {
//                    CommonUtils.cencelDialog();
//                    CommonUtils.showToastShort(aty, R.string.pay_sending_error);
//                }
//            });
//        } else {
//            CommonUtils.showToastShort(aty, R.string.has_no_wechat);
//        }
    }
    /**
     * 发起请求
     *
     * @param activity 发起的activity
     * @param req      payReq
     */
    private static void sendPayReq(Activity activity, PayReq req,String appId) {
        if (TextUtils.isEmpty(appId)){
            appId = HTConstant.WX_APP_ID;
        }
        IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, null);
        msgApi.registerApp(appId);
        msgApi.sendReq(req);
     }
    /**
     * 发起请求
     *
     * @param activity 发起的activity
     * @param req      payReq
     */
//    private static void sendPayReq(Activity activity, PayReq req) {
//        IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, null);
//        msgApi.registerApp(HTConstant.WX_APP_ID);
//        msgApi.sendReq(req);
//    }
    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    private static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

//    /**
//     * 查询是否微信支付成功
//     *
//     * @param context
//     */
//    public static void checkWeChatPayResult(final Context context) {
//        if (TextUtils.isEmpty(outTradeNo)) {
//            return;
//        }
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("out_trade_no", outTradeNo));
//        new OkHttpUtils(context).postJSON(params, HTConstant.CHECK_PAY_RESULT, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        break;
//                    default:
//                        CommonUtils.showToastShort(context, R.string.pay_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(context, R.string.pay_failed);
//            }
//        });
//    }

    /**
     * 支付宝支付
     *
     * @param activity 当前的activity
     * @param listener 回调
     * @param amount 钱数
     */
    public static void payByAliPay(final Activity activity, String amount,int type, final PayBackListener listener) {
        CommonUtils.showDialogNumal(activity,activity.getString(R.string.are_authing));

        JSONObject body=new JSONObject();
        body.put("money",amount);
        body.put("type",type);
        ApiUtis.getInstance().postJSON(body, Constant.PAY_BY_ALIPAY, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    final String orderString = jsonObject.getString("data");
                    PayTask alipay = new PayTask(activity);
                    Map<String, String> result = alipay.payV2(orderString, true);
                    PayResult payResult = new PayResult(result);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    final String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    LoggerUtils.e("---支付宝支付结果:"+resultInfo);
                    final String resultCode = payResult.getResultStatus();
                    if(activity!=null&&!activity.isDestroyed()){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                if ("9000".equals(resultCode)) {
                                    listener.paySuccess();
                                } else if ("6001".equals(resultCode)) {
                                    listener.payCancled();
                                } else {
                                    listener.payFailed();
                                }
                            }
                        });
                    }

                }else {
                    if(activity!=null&&!activity.isDestroyed()){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(activity, R.string.pay_sending_error);
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(activity!=null&&!activity.isDestroyed()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(activity, R.string.pay_sending_error);
                        }
                    });
                }
            }
        });
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        params.add(new Param("amount", amount));
//        new OkHttpUtils(activity).post(params, HTConstant.PAY_BY_ALIPAY, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        final String orderString = jsonObject.getString("data");
//                        LoggerUtils.e("---支付宝支付:"+orderString);
//                        Runnable payRunnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                PayTask alipay = new PayTask(activity);
//                                Map<String, String> result = alipay.payV2(orderString, true);
//                                PayResult payResult = new PayResult(result);
//                                /**
//                                 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                                 */
//                                final String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                                LoggerUtils.e("---支付宝支付结果:"+resultInfo);
//                                final String resultCode = payResult.getResultStatus();
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if ("9000".equals(resultCode)) {
//                                            listener.paySuccess();
//                                        } else if ("6001".equals(resultCode)) {
//                                            listener.payCancled();
//                                        } else {
//                                            listener.payFailed();
//                                        }
//                                    }
//                                });
//                            }
//                        };
//                        Thread payThread = new Thread(payRunnable);
//                        payThread.start();
//                        break;
//                    default:
//                        CommonUtils.showToastShort(activity, R.string.pay_sending_error);
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(activity, R.string.pay_sending_error);
//            }
//        });
    }

    public interface PayBackListener {
        void paySuccess();

        void payFailed();

        void payCancled();
    }


}
