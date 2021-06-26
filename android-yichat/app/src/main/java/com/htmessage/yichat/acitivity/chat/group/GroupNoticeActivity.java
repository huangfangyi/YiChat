package com.htmessage.yichat.acitivity.chat.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;
import com.htmessage.yichat.widget.HTAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2019/7/17.
 * qq 84543217
 */
public class GroupNoticeActivity extends BaseActivity {
    private String groupId;
    private ListView listView;
    private JSONArray jsonArray=new JSONArray();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    JSONArray data = (JSONArray) msg.obj;
                    if(data!=null&&data.size()!=0){
                        JSONObject dataJson=data.getJSONObject(data.size()-1);
                         jsonArray.clear();
                        jsonArray.add(dataJson);
                        myadapter = new Myadapter(GroupNoticeActivity.this, jsonArray);
                        listView.setAdapter(myadapter);
                    }
                    break;
                case 1001:
                    CommonUtils.cencelDialog();
                    int resId=msg.arg1;
                    Toast.makeText(GroupNoticeActivity.this,resId,Toast.LENGTH_SHORT).show();
                    break;

                case 1002:
                    jsonArray.clear();
                    myadapter.notifyDataSetChanged();
                    CommonUtils.cencelDialog();


                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_group_notice);
        groupId = this.getIntent().getStringExtra("groupId");
        listView = this.findViewById(R.id.listView);
        setTitle("群公告");
        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup != null) {
            if (GroupInfoManager.getInstance().isManager(groupId)) {
                showRightTextView("发布新公告", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GroupNoticeActivity.this, GroupNoticePublishActivity.class).putExtra("groupId", groupId));
                    }
                });


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (myadapter == null) {
                            return;
                        }
                        if(GroupInfoManager.getInstance().isManager(groupId)){
                            JSONObject jsonObject = myadapter.getItem(position);
                            deleteNotice(jsonObject);
                        }

                    }
                });

            }
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    class Myadapter extends BaseAdapter {
        private Context context;
        private JSONArray jsonArray;

        public Myadapter(Context context, JSONArray jsonArray) {
            this.context = context;
            this.jsonArray = jsonArray;

        }

        @Override
        public int getCount() {
            return jsonArray.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return jsonArray.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.item_group_notice, parent, false);
            }
            TextView tvTime = convertView.findViewById(R.id.tv_time);
            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            TextView tvUser = convertView.findViewById(R.id.tv_user);
            TextView tvContent = convertView.findViewById(R.id.tv_content);
            JSONObject data = getItem(position);
            tvTime.setText(data.getString("timeDesc"));
            tvTitle.setText(data.getString("title"));
            tvContent.setText(data.getString("content"));
            tvUser.setText(data.getString("nick"));

            return convertView;
        }
    }

    Myadapter myadapter;

    private void getData() {

        JSONObject body = new JSONObject();
        body.put("groupId", groupId);

        ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_NOTICE_LIST, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                     Message message = handler.obtainMessage();
                     message.obj=data;
                    message.what = 1000;
                    message.sendToTarget();
                }  else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });



//

    }

    private void deleteNotice(final JSONObject data) {
        HTAlertDialog htAlertDialog = new HTAlertDialog(GroupNoticeActivity.this, "是否删除该条公告？", new String[]{"删除"});
        htAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    CommonUtils.showDialogNumal(GroupNoticeActivity.this, "正在提交");
                    JSONObject body=new JSONObject();
                    body.put("noticeId",data.getString("noticeId"));
                    ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_NOTICE_DELETE, new ApiUtis.HttpCallBack() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            if(handler==null){
                                return;
                            }
                            String code=jsonObject.getString("code");
                            if("0".equals(code)){
                                Message message = handler.obtainMessage();

                                message.what = 1002;
                                message.sendToTarget();
                            }else {
                                Message message = handler.obtainMessage();
                                message.what = 1001;
                                message.arg1 = R.string.api_error_5;
                                message.sendToTarget();
                            }
                        }

                        @Override
                        public void onFailure(int errorCode) {
                            if(handler==null){
                                return;
                            }
                            Message message = handler.obtainMessage();
                            message.what = 1001;
                            message.arg1 = errorCode;
                            message.sendToTarget();
                        }
                    });
//                    List<Param> params = new ArrayList<>();
//                    params.add(new Param("id", data.getString("id")));
//                    params.add(new Param("userid", data.getString("user_id")));
//                    params.add(new Param("groupid", groupId));
//                    new OkHttpUtils(GroupNoticeActivity.this).post(params, HTConstant.URL_GROUP_NOTICE_DEL, new OkHttpUtils.HttpCallBack() {
//                        @Override
//                        public void onResponse(JSONObject jsonObject) {
//                            CommonUtils.cencelDialog();
//                            int code = jsonObject.getInteger("code");
//                            if (code == 1) {
//                                CommonUtils.showToastShort(GroupNoticeActivity.this, "删除成功");
//                                getData();
//
//                            } else {
//                                CommonUtils.showToastShort(GroupNoticeActivity.this, "删除失败");
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(String errorMsg) {
//                            CommonUtils.cencelDialog();
//                            CommonUtils.showToastShort(GroupNoticeActivity.this, "删除失败");
//
//                        }
//                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;

    }
}
