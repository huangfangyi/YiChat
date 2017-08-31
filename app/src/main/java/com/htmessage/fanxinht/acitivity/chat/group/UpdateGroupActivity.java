package com.htmessage.fanxinht.acitivity.chat.group;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;

/**
 * Created by huangfangyi on 2017/3/5.
 * qq 84543217
 */

public class UpdateGroupActivity extends BaseActivity {

    public static final int TYPE_GROUP_NAME=1;
    public static final int TYPE_GROUP_DESC=2;

    private String groupId;
    private HTGroup htGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        int type = getIntent().getIntExtra("type", 1);
        groupId = getIntent().getStringExtra("groupId");
        RelativeLayout title = (RelativeLayout) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        TextView titleTV = (TextView) findViewById(R.id.tv_title);
        TextView saveTV = (TextView) findViewById(R.id.tv_save);
        EditText infoET = (EditText) findViewById(R.id.et_info);
        htGroup= HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup == null) {
           finish();
            return;
        }
        initView(type, titleTV, saveTV, infoET);
    }

    private void initView(final int type, TextView titleTV, TextView saveTV, final EditText infoET) {
        String title = "";
        String key = "";

        switch (type) {
            case TYPE_GROUP_NAME:
                title =getString(R.string.change_groupName);
                infoET.setHint(htGroup.getGroupName());
                 break;
            case TYPE_GROUP_DESC:
                title =getString(R.string.change_group_desc);
                infoET.setHint(htGroup.getGroupDesc());

                break;
        }
        titleTV.setText(title);
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateInServer(type, infoET.getText().toString().trim());
            }
        });

    }

    private void updateInServer(int type, final String value) {

        if (  TextUtils.isEmpty(value) ) {
            return;
        }

        if(type==TYPE_GROUP_NAME){
          if(!value.equals(htGroup.getGroupName())){
              if (value.length() > 18) {
                  Toast.makeText(getApplicationContext(), R.string.group_groupname_dot_18, Toast.LENGTH_SHORT).show();
                  return;
              }
              final Dialog progressDialog =  HTApp.getInstance().createLoadingDialog(this,getString(R.string.are_uploading));
              progressDialog.show();
              HTClient.getInstance().groupManager().updateGroupName(htGroup.getGroupId(), value, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK), new GroupManager.CallBack() {
                  @Override
                  public void onSuccess(String s) {
                      progressDialog.dismiss();
                      Toast.makeText(UpdateGroupActivity.this,R.string.Modify_the_group_name_successful,Toast.LENGTH_SHORT).show();
                      setResult(RESULT_OK,new Intent().putExtra("value",value));
                      finish();
                  }

                  @Override
                  public void onFailure() {
                      progressDialog.dismiss();
                      Toast.makeText(UpdateGroupActivity.this,R.string.change_groupName_failed,Toast.LENGTH_SHORT).show();
                  }
              });
          }

            return;
        }
        if(type==TYPE_GROUP_DESC){
            if (value.length() > 100) {
                Toast.makeText(getApplicationContext(), R.string.group_groupdesc_dot_100, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!value.equals(htGroup.getGroupDesc())){
                final Dialog progressDialog =  HTApp.getInstance().createLoadingDialog(this,getString(R.string.are_uploading));
                progressDialog.show();
                HTClient.getInstance().groupManager().updateGroupDesc(htGroup.getGroupId(), value, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateGroupActivity.this,R.string.update_groups_success,Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK,new Intent().putExtra("value",value));
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateGroupActivity.this,R.string.update_groups_failed,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return;
        }






        //本地用户资料
     //   final JSONObject userJson = HTApp.getInstance().getUserJson();
//        List<Param> params = new ArrayList<Param>();
//        params.add(new Param(HTConstant.JSON_KEY_SESSION, userJson.getString(HTConstant.JSON_KEY_SESSION)));
//        params.add(new Param(key, value));
//        List<File> files = new ArrayList<File>();
//        if (key == HTConstant.JSON_KEY_AVATAR) {
//            File file = new File(Environment.getExternalStorageDirectory(), value);
//            if (file.exists()) {
//                files.add(file);
//            }
//        }
//        new OkHttpUtils(this).post(params, files, HTConstant.URL_UPDATE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                progressDialog.dismiss();
//                int code = jsonObject.getIntValue("code");
//                if (code == 1) {
//                    userJson.put(key, value);
//                    HTApp.getInstance().setUserJson(userJson);
//                    setResult(RESULT_OK, new Intent().putExtra("value", value));
//                    finish();
//
//                } else {
//
//                    Toast.makeText(getApplicationContext(), getString(R.string.upload_failed) + code, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                progressDialog.dismiss();
//            }
//        });

    }
}
