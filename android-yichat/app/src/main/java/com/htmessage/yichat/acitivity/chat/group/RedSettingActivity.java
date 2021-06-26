package com.htmessage.yichat.acitivity.chat.group;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;
import com.htmessage.yichat.utils.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2019/1/31.
 * qq 84543217
 */
public class RedSettingActivity extends BaseActivity {
    private EditText et_number;
    private Button btn_ok;
    private String groupId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_redsettings);
        groupId=this.getIntent().getStringExtra("groupId");
        et_number=this.findViewById(R.id.et_number);
        btn_ok=this.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=et_number.getText().toString();
                if(!Validator.isNumber(number)&&!Validator.isDouble(number)){
                    Toast.makeText(RedSettingActivity.this,"请输入正确的数字",Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadServer(number);


            }
        });



    }

    private void uploadServer(String number){
        List<Param> params=new ArrayList<>();
        params.add(new Param("gid",groupId));

        params.add(new Param("money",number));
        new OkHttpUtils(RedSettingActivity.this).post(params, HTConstant.SET_APPEAR, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code=jsonObject.getInteger("code");
                if(code==1){
                    Toast.makeText(RedSettingActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(RedSettingActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(RedSettingActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();


            }
        });

    }
}
