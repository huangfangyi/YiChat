package com.fanxin.app.main.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.R;
import com.fanxin.app.main.FXConstant;
import com.fanxin.app.ui.BaseActivity;

/**
 * Created by huangfangyi on 2016/8/13.\
 * QQ:84543217
 */
public class AnswerCardActivity  extends BaseActivity{
    private  String answer=null;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fx_activity_answercard);
        String cardData=getIntent().getStringExtra("cardData");
        if(cardData==null){
           finish();
            return;

        }

        initViews(JSONObject.parseObject(cardData));
    }

    private void initViews(JSONObject jsonObject){
        TextView tvQuestion= (TextView) this.findViewById(R.id.tv_question);

        RadioButton rb_a= (RadioButton) this.findViewById(R.id.rb_a);
        RadioButton rb_b= (RadioButton) this.findViewById(R.id.rb_b);
        RadioButton rb_c= (RadioButton) this.findViewById(R.id.rb_c);
        RadioButton rb_d= (RadioButton) this.findViewById(R.id.rb_d);
        rb_a.setText("A. "+jsonObject.getString(FXConstant.JSON_QA_A));
        rb_b.setText("B. "+jsonObject.getString(FXConstant.JSON_QA_B));
        rb_c.setText("C. "+jsonObject.getString(FXConstant.JSON_QA_C));
        rb_d.setText("D. "+jsonObject.getString(FXConstant.JSON_QA_D));
        tvQuestion.setText(jsonObject.getString(FXConstant.JSON_QA_QUESTION));
        RadioGroup radioGroup= (RadioGroup) findViewById(R.id.rg_items);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case  R.id.rb_a:
                        answer="A";
                        break;
                    case  R.id.rb_b:
                        answer="B";
                        break;

                    case  R.id.rb_c:
                        answer="C";
                        break;
                    case  R.id.rb_d:
                        answer="D";
                        break;
                }
            }
        });
        final String defaultAnswer=jsonObject.getString(FXConstant.JSON_QA_ANSWER);
        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(answer)){
                    Toast.makeText(getApplicationContext(),"请选择答案..",Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if(!answer.equals(defaultAnswer)){
                    Toast.makeText(getApplicationContext(),"回答错误,请重试",Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Log.d("answer---->>",answer);
                Log.d("defaultAnswer---->>",defaultAnswer);

                setResult(RESULT_OK);
                finish();
            }
        });





    }
}
