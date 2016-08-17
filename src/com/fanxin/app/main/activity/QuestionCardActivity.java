package com.fanxin.app.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.fanxin.app.R;
import com.fanxin.app.main.FXConstant;
import com.fanxin.app.ui.BaseActivity;

/**
 * Created by huangfangyi on 2016/8/12.\
 * QQ:84543217
 */
public class QuestionCardActivity extends BaseActivity {
    private final static  int T0_REDPACKET=1000;
    private String answer=null;
    private String toChatUsername;
    private int chatType;
    private String jsonArray;
    private String cardData;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fx_activity_question_card);
        toChatUsername=getIntent().getStringExtra("toChatUsername");
        chatType=getIntent().getIntExtra("chatType",0);
        jsonArray=getIntent().getStringExtra("jsonArray");
        if(toChatUsername==null&&jsonArray==null||chatType==0){
            finish();
            return;
        }
        initViews();



    }
    private void initViews(){
        final EditText etQuestion= (EditText) this.findViewById(R.id.et_question);
        final EditText etItemA= (EditText) this.findViewById(R.id.et_item_a);
        final EditText etItemB= (EditText) this.findViewById(R.id.et_item_b);
        final EditText etItemC= (EditText) this.findViewById(R.id.et_item_c);
        final EditText etItemD= (EditText) this.findViewById(R.id.et_item_d);
        RadioGroup radioGroup= (RadioGroup) findViewById(R.id.rg_items);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                 switch (checkedId){
                     case R.id.rb_a:
                         answer="A";
                         break;
                     case R.id.rb_b:
                         answer="B";
                         break;
                     case R.id.rb_c:
                         answer="C";
                         break;
                     case R.id.rb_d:
                         answer="D";
                         break;
                 }
            }
        });
        this.findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String question=etQuestion.getText().toString().trim();
                String itemA=etItemA.getText().toString().trim();
                String itemB=etItemB.getText().toString().trim();
                String itemC=etItemC.getText().toString().trim();
                String itemD=etItemD.getText().toString().trim();

                if(TextUtils.isEmpty(question)||TextUtils.isEmpty(itemA)||TextUtils.isEmpty(itemB)||TextUtils.isEmpty(itemC)||TextUtils.isEmpty(itemD)||TextUtils.isEmpty(answer)){
                    Toast.makeText(getApplicationContext(),"请设置完全...",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(question.length()>60){

                    Toast.makeText(getApplicationContext(),"问题不能超过60字",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(itemA.length()>30||itemB.length()>30||itemC.length()>30||itemD.length()>30){

                    Toast.makeText(getApplicationContext(),"选项不能超过30字",Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(FXConstant.JSON_QA_QUESTION,question);
                jsonObject.put(FXConstant.JSON_QA_A,itemA);
                jsonObject.put(FXConstant.JSON_QA_B,itemB);
                jsonObject.put(FXConstant.JSON_QA_C,itemC);
                jsonObject.put(FXConstant.JSON_QA_D,itemD);
                jsonObject.put(FXConstant.JSON_QA_ANSWER,answer);
                saveData(jsonObject.toJSONString());
            }
        });
    }



    //保存问题数据
    private void saveData(String data){
        cardData=data;
        RedPacketUtil.startRedPacketActivityForResult(this, chatType, toChatUsername, T0_REDPACKET, JSONArray.parseArray(jsonArray));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==T0_REDPACKET&&resultCode==RESULT_OK&&data!=null){
            data.putExtra("cardData",cardData);
            setResult(RESULT_OK,data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
