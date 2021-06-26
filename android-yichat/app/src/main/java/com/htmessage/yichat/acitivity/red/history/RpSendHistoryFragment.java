package com.htmessage.yichat.acitivity.red.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.red.RedDetailActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;

/**
 * 项目名称：CityBz
 * 类描述：RpSendHistoryFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/10 9:06
 * 邮箱:814326663@qq.com
 */
public class RpSendHistoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private TextView tv_nick, tv_money, tv_send_total;
    private ImageView iv_avatar;
    private ListView lv_history;
    private RpHistoryAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    //获取到发出红包的汇总
                    JSONObject sendData = (JSONObject) msg.obj;
                    tv_money.setText(Validator.formatMoney(sendData.getString("money")));
                    tv_send_total.setText(sendData.getString("count"));
                    break;
                case 1001:
                    int resId = msg.arg1;
                    Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
                    break;
                case 1002:
                    JSONArray sendList = (JSONArray) msg.obj;
                    refreshListView(sendList);

                    break;
                case 1003:
                    CommonUtils.cencelDialog();
                    JSONObject reData = (JSONObject) msg.obj;
                    int type=msg.arg1;
                    //type 0是单聊 1是群里，本地 1是单聊 2是群聊
                    startActivity(new Intent(getActivity(), RedDetailActivity.class).putExtra("data",reData.toJSONString())
                            .putExtra("chatType",type+1));

                    break;


            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_send_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
    }


    private void setListener() {
        lv_history.setOnItemClickListener(this);
    }

    private void initData() {
        tv_nick.setText(HTApp.getInstance().getUserNick());
        UserManager.get().loadUserAvatar(getActivity(), UserManager.get().getMyAvatar(), iv_avatar);
        showSendHistory();
        getSendList();
    }

    private void initView() {
        tv_nick = (TextView) getView().findViewById(R.id.tv_nick);
        tv_money = (TextView) getView().findViewById(R.id.tv_money);
        tv_send_total = (TextView) getView().findViewById(R.id.tv_send_total);
        iv_avatar = (ImageView) getView().findViewById(R.id.iv_avatar);
        lv_history = (ListView) getView().findViewById(R.id.lv_history);
    }

    private void getData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject data=adapter.getItem(position);
        String packetId=data.getString("packetId");
        int type=data.getInteger("type");

        getRedPacketDetails(packetId,type);


    }


    private void refreshListView(JSONArray  jsonObjectList) {
        adapter = new RpHistoryAdapter(jsonObjectList, getActivity(), false);
        lv_history.setAdapter(adapter);
    }

    private void showSendHistory() {
        //发出红包汇总

        ApiUtis.getInstance().postJSON(new JSONObject(), Constant.URL_packet_send_info, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = data;
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        params.add(new Param("isGroupRed", isGroupRed));
//        new OkHttpUtils(getActivity()).post(params, HTConstant.SEND_RP_LIST, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e("----发送红包的历史记录:" + jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        String total = jsonObject.getString("total");
//                        String receivedMoney = jsonObject.getString("sendMoney");
//                        JSONArray array = jsonObject.getJSONArray("data");
//                        List<JSONObject> jsonObjects = arrayToList(array);
//                        tv_money.setText(Validator.formatMoney(receivedMoney));
//                        tv_send_total.setText(total);
//                        refreshListView(jsonObjects);
//                        break;
//                    default:
//                        tv_money.setText(Validator.formatMoney(0));
//                        tv_send_total.setText("0");
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                tv_money.setText(Validator.formatMoney(0));
//                tv_send_total.setText("0");
//            }
//        });
    }

    private void getSendList() {

        //发出红包汇总
        JSONObject body = new JSONObject();
        body.put("pageNo", 1);
        body.put("pageSize", 1000);
        ApiUtis.getInstance().postJSON(body, Constant.URL_packet_send_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = data;
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
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
    public void onDestroy() {
        super.onDestroy();
        handler = null;
    }


    private void  getRedPacketDetails(String packetId,int type){
        CommonUtils.showDialogNumal(getActivity(),"查看红包");
        JSONObject body=new JSONObject();
        body.put("packetId",packetId);
        ApiUtis.getInstance().postJSON(body, Constant.URL_RedPacket_Detail, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code=jsonObject.getString("code");
                if("0".equals(code)) {

                    JSONObject data = jsonObject.getJSONObject("data");
                    Message message=handler.obtainMessage();
                    message.what=1003;
                    message.obj=data;
                    message.arg1=type;
                    message.sendToTarget();
                }else {
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();

                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();

            }
        });
    }
}
