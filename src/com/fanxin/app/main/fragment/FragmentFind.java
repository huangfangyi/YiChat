package com.fanxin.app.main.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.R;
import com.fanxin.app.main.FXConstant;
import com.fanxin.app.main.activity.ChatActivity;
import com.fanxin.app.main.activity.ScanCaptureActivity;
import com.fanxin.app.main.moments.SocialMainActivity;
import com.fanxin.app.main.service.GroupService;
import com.fanxin.app.main.utils.OkHttpManager;
import com.fanxin.app.main.utils.Param;
import com.fanxin.easeui.EaseConstant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class FragmentFind extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.re_friends).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String userID = DemoHelper.getInstance().getCurrentUsernName();
                if (!TextUtils.isEmpty(userID)) {

                    startActivity(new Intent(getActivity(), SocialMainActivity.class).putExtra("userID", userID));

                }
            }


        });
        getView().findViewById(R.id.re_qrcode).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), ScanCaptureActivity.class));
            }

        });
        getView().findViewById(R.id.re_rp_group).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {



                List<EMGroup> groupList= EMClient.getInstance().groupManager().getAllGroups();
                 boolean isJoined=false;

                for(EMGroup group:groupList){
                    if(group.getGroupId().equals(FXConstant.REDPACKET_GROUP_ID)){
                        isJoined=true;
                    }

                }
                if(isJoined){

                    intoRPGroupChat();
                }else{

                    joinGroupInServer();
                }

            }

        });
    }

    private void intoRPGroupChat(){
              startActivity(new Intent(getContext(), ChatActivity.class).putExtra("chatType", EaseConstant.CHATTYPE_GROUP).putExtra("userId",FXConstant.REDPACKET_GROUP_ID));
    }

    private  void  joinGroupInServer(){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("正在加入红包群");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        List<Param> params=new ArrayList<>();
        params.add(new Param("groupId",FXConstant.REDPACKET_GROUP_ID));
        params.add(new Param("members",DemoHelper.getInstance().getCurrentUsernName()));
        OkHttpManager.getInstance().post(params, FXConstant.URL_GROUP_ADD_MEMBERS, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code=jsonObject.getIntValue("code");
                if(code==1000){
                    JSONObject data=jsonObject.getJSONObject("data");
                    if(data.containsKey("data")&&data.get("data") instanceof JSONObject&&data.getJSONObject("data").containsKey("newmembers")&&data.getJSONObject("data").get("newmembers") instanceof JSONArray){

                        JSONArray jsonArray=data.getJSONObject("data").getJSONArray("newmembers");
                        if(jsonArray!=null&&jsonArray.size()!=0){

                            Toast.makeText(getContext(),"加群成功",Toast.LENGTH_SHORT).show();
                            intoRPGroupChat();

                            getActivity().startService(new Intent(getContext(), GroupService.class));
                            return;

                        }
                    }

                }
                Toast.makeText(getContext(),"加群失败 ...",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),"加群失败 ...",Toast.LENGTH_SHORT).show();
            }
        });


    }


}
