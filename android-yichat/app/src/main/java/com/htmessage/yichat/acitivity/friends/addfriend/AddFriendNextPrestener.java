package com.htmessage.yichat.acitivity.friends.addfriend;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;

public class AddFriendNextPrestener implements AddFriendNextBasePrestener {
    private AddFriendNextView nextView;


    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(nextView==null){
                return;
            }

            switch (msg.what){

                case 1000:
                    //搜索到用户
                    JSONObject userJson= (JSONObject) msg.obj;
                    nextView.onSearchSuccess(userJson);

                    break;
                case 1001:
                    //用户不存在或者接口调用失败
                    int resId=msg.arg1;
                    nextView.onSearchFailed(resId);
                    break;
            }
        }
    };

    public AddFriendNextPrestener(AddFriendNextView nextView) {
        this.nextView = nextView;
        this.nextView.setPresenter(this);
    }

    @Override
    public void searchUser(String content) {

       // CommonUtils.showDialogNumal(nextView.getBaseContext(), nextView.getBaseContext().getString(R.string.are_finding_contact));
        JSONObject data=new JSONObject();
        data.put("content",content);
        ApiUtis.getInstance().postJSON(data, Constant.URL_USER_SEARCH, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                   if(jsonArray!=null&&jsonArray.size()>0){
                      Message message=handler.obtainMessage();
                      message.what=1000;
                      message.obj=jsonArray.getJSONObject(0);
                      message.sendToTarget();
                  }else {
                      Message message=handler.obtainMessage();
                      message.what=1001;
                      message.arg1= R.string.user_not_exit;
                      message.sendToTarget();
                  }

                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1= errorCode;
                message.sendToTarget();
            }
        });



//        List<Param> paramList = new ArrayList<>();
//        paramList.add(new Param("userId", nextView.getInputString()));
//        paramList.add(new Param("uid", HTApp.get().getUserId()));
//        new OkHttpUtils(nextView.getBaseContext()).post(paramList, HTConstant.URL_Search_User, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONObject json = jsonObject.getJSONObject("user");
//                        nextView.onSearchSuccess(json);
//                        break;
//                    case -1:
//                        nextView.onSearchFailed(nextView.getBaseContext().getString(R.string.User_does_not_exis));
//                        break;
//                    default:
//                        nextView.onSearchFailed(nextView.getBaseContext().getString(R.string.server_is_busy_try_again));
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                nextView.onSearchFailed(nextView.getBaseContext().getString(R.string.server_is_busy_try_again));
//            }
//        });
    }

    @Override
    public void start() {

    }
}
