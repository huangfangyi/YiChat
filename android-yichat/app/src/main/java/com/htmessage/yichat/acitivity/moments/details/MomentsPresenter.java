package com.htmessage.yichat.acitivity.moments.details;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.R;

/**
 * Created by huangfangyi on 2017/7/22.
 * qq 84543217
 */

public class MomentsPresenter implements MomentsContract.Presenter {

    private JSONObject data;
    private MomentsContract.View detailView;

    public MomentsPresenter(MomentsContract.View detailView) {
        this.detailView = detailView;
        detailView.setPresenter(this);
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (detailView == null) {
                return;
            }
            switch (msg.what) {
                case 1000:
                    //点赞或者取消点赞成功
                    JSONArray praises = (JSONArray) msg.obj;
                    detailView.updateGoodView(praises);
                    break;

                case 1002:

                    break;
                case 1003:
                    //删除评论成功或者//评论成功
                    JSONArray comments = (JSONArray) msg.obj;
                    detailView.updateCommentView(comments);


                    break;
                case 1004:
                    //删除动态成功
                    detailView.finish();
                    break;

                case 1005:
                    //接口返回错误
                    int resId = msg.arg1;
                    Toast.makeText(detailView.getBaseContext(), resId, Toast.LENGTH_SHORT).show();

                    break;
                case 1006:
                    //获取一个动态详情
                    JSONObject data = (JSONObject) msg.obj;
                    //

                    detailView.initMomentView(data);
//                    }

                    break;

            }
        }
    };

    @Override
    public void start() {

    }

    public void initData(Bundle bundle) {

        String dataStr = bundle.getString("data");
        if (dataStr == null) {
            String mid = bundle.getString("mid");
            getDataFromServer(mid);
        } else {
            data = JSONObject.parseObject(dataStr);
            detailView.initMomentView(data);
        }

    }

    @Override
    public void setGood(String tid) {


        JSONObject body = new JSONObject();
        body.put("trendId", tid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_praise, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//        // 更新后台
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("tid", aid));
//        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GOOD, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray praises = jsonObject.getJSONArray("praises");
//                        detailView.updateGoodView( praises);
//                        break;
//                    case -1:
//                        Toast.makeText(detailView.getBaseContext(), R.string.praise_failed, Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        Toast.makeText(detailView.getBaseContext(), R.string.praise_failed, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(detailView.getBaseContext(), R.string.praise_failed_msg + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    @Override
    public void cancelGood(String tid) {

        JSONObject body = new JSONObject();
        body.put("trendId", tid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_praise_cancel, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("pid", gid));
//        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GOOD_CANCEL, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray praises = jsonObject.getJSONArray("praises");
//                        detailView.updateGoodView( praises);
//                        break;
//
//                    default:
//                        Toast.makeText(detailView.getBaseContext(), R.string.praise_cancle_fail, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(detailView.getBaseContext(), R.string.praise_cancle_fail_msg + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void deleteComment(String cid) {

        JSONObject body = new JSONObject();
        body.put("commentId", cid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_comment_delete, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1003;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//        List<Param> params = new ArrayList<>();
//        params.add(new Param("cid", cid));
//        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_DELETE_COMMENT, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray comments = jsonObject.getJSONArray("comments");
//                        detailView.updateCommentView( comments);
//                        break;
//                    default:
//                        Toast.makeText(detailView.getBaseContext(), R.string.delete_comment_failed, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(detailView.getBaseContext(), R.string.delete_comment_failed_msg + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void delete(String aid) {
        JSONObject body = new JSONObject();
        body.put("trendId", aid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_delete, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1004;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//
//        final List<Param> params = new ArrayList<>();
//        params.add(new Param("tid", aid));
//        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_DELETE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        detailView.finish();
//
//                        break;
//
//                    default:
//                        Toast.makeText(detailView.getBaseContext(), R.string.delete_dynamic_failed, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(detailView.getBaseContext(), R.string.delete_dynamic_failed_msg + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public void comment(String content) {

        String aid = data.getString("trendId");
        JSONObject body = new JSONObject();
        body.put("content", content);
        body.put("trendId", aid);

        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_comment, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1003;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//
//        String aid=data.getString("id");
//        params.add(new Param("tid", aid));
//        List<Param> params = new ArrayList<>();
//
//        params.add(new Param("content", content));
//        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_COMMENT, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray comments = jsonObject.getJSONArray("comments");
//                        detailView.updateCommentView( comments);
//                        break;
//                    case -1:
//                        Toast.makeText(detailView.getBaseContext(), R.string.service_not_response, Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        Toast.makeText(detailView.getBaseContext(), R.string.service_not_response, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(detailView.getBaseContext(), R.string.service_not_response, Toast.LENGTH_SHORT).show();
//            }
//        });
//

    }


    private void getDataFromServer(String mid) {

        JSONObject body = new JSONObject();
        body.put("trendId", mid);

        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_detail, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1006;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//
//        List<Param> params=new ArrayList<>();
//        params.add(new Param("tid",mid));
//        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GET_DETAIL, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code=jsonObject.getInteger("code");
//                if(code==1){
//                    JSONObject dataTemp=jsonObject.getJSONObject("data");
//                    if(dataTemp!=null){
//                        String serverTime=dataTemp.getString("serverTime");
//                        data=dataTemp;
//                        detailView.initMomentView(data,serverTime);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });

    }
}
