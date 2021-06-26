package com.htmessage.yichat.acitivity.main;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;

/**
 * Created by huangfangyi on 2017/6/25.
 * qq 84543217
 */

public class MainPrestener implements MainBasePrester {
    private MainView mainView;
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    JSONObject data= (JSONObject) msg.obj;
                    int  updateStatus=data.getInteger("updateStatus");
                    String memo=data.getString("memo");
                    String url=data.getString("downloadUrl");
                    mainView.showUpdateDialog(memo,url,(updateStatus==1)?true:false);

                    break;
                case 1001:

                     break;
            }


        }
    };
    public MainPrestener(MainView _mainView) {
        this.mainView = _mainView;
        this.mainView.setPresenter(this);
     }

    @Override
    public void start() {

    }


//    /**
//     * 获取VersionCode
//     *
//     * @return 当前应用的VersionCode
//     */
//    public String getVersionCode() {
//        try {
//            PackageManager manager = context.getPackageManager();
//            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//            String version = String.valueOf(info.versionCode);
//            return version;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


    @Override
    public void checkVersion(final int currentVserion) {

//        JSONObject jsonObject= SettingsManager.getInstance().getVersionStatus();
//        if(jsonObject!=null){
//            int version=jsonObject.getInteger("version");
//

            //if(version>getVersionCode()){

//                    data": {
//                    "version": "123123131",
//                            "memo": "12313131",
//                            "updateStatus": 0,
//                            "downloadUrl": "123131"
//                },
//        }
        JSONObject data=new JSONObject();
        data.put("type",0);
        data.put("currentVersion",currentVserion);

        ApiUtis.getInstance().postJSON(data, Constant.URL_VERSION, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null)
                    return;

                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONObject data=jsonObject.getJSONObject("data");

                    int version=data.getInteger("version");
                    if(version>currentVserion){
                        //int  updateStatus=jsonObject.getInteger("updateStatus");


//                    data": {
//                    "version": "123123131",
//                            "memo": "12313131",
//                            "updateStatus": 0,
//                            "downloadUrl": "123131"
//                },

                        Message message=handler.obtainMessage();
                        message.what=1000;
                        message.obj=data;
                        message.sendToTarget();
                    }else {
                        Message message=handler.obtainMessage();
                        message.what=1001;
                        message.arg1= R.string.just_new_version;
                        message.sendToTarget();
                    }

                }else {
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();

                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=R.string.api_error_5;
                message.sendToTarget();

            }
        });


//        final String version = getVersionCode();
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("system", "0"));
//        params.add(new Param("vid", version));
//        new OkHttpUtils(context).post(params, HTConstant.URL_CHECK_UPDATE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e("-----更新检查:"+jsonObject.toJSONString());
//                if (jsonObject != null) {
//                    String serviceVersion = jsonObject.getString("newVersion");
//                    if (TextUtils.isEmpty(serviceVersion)) {
//                        return;
//                    }
//                    if (!"null".equals(serviceVersion) && jsonObject.containsKey("url") && jsonObject.containsKey("info") && jsonObject.containsKey("status")) {
//                        String url = jsonObject.getString("url");
//                        String info = jsonObject.getString("info");
//                        String statue = jsonObject.getString("status");
//                        if (!version.equals(serviceVersion) && (Integer.valueOf(version) < Integer.valueOf(serviceVersion))) {
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//            }
//        });

    }


}
