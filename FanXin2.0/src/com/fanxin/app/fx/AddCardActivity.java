package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.ACache;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

public class AddCardActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        initView();

    }

    private void initView() {

        final EditText et_cardID = (EditText) findViewById(R.id.et_cardID);
        final EditText et_cardPSW = (EditText) findViewById(R.id.et_cardPSW);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String cardID = et_cardID.getText().toString().trim();
                String cardPSW = et_cardPSW.getText().toString().trim();
                if (TextUtils.isEmpty(cardID) || TextUtils.isEmpty(cardPSW)) {
                    Toast.makeText(getApplicationContext(), "卡号或密码不能为空",
                            Toast.LENGTH_SHORT).show();

                    return;

                }

                updateServer(cardID, cardPSW);
            }

        });
    }

    private void updateServer(String cardID, String cardPSW) {
        final ProgressDialog dialog = new ProgressDialog(AddCardActivity.this);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在加载...");
        dialog.show();
        Map<String, String> map = new HashMap<String, String>();
        map.put("hxid", MYApplication.getInstance().getUserName());
        map.put("cardID", cardID);
        map.put("cardPSW", cardPSW);
        LoadDataFromServer task = new LoadDataFromServer(AddCardActivity.this,
                Constant.URL_ADD_CARD, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (data == null) {

                    Toast.makeText(getApplicationContext(), "访问服务器错误,更新失敗...",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                int code = data.getInteger("code");
                if (code == 1) {
                    JSONObject json = data.getJSONObject("data");
                    if (json != null) {
                        JSONArray jsons = ACache.get(getApplicationContext())
                                .getAsJSONArray(Constant.CARDLIST);
                        if (jsons == null) {
                            jsons = new JSONArray();
                        }

                        jsons.add(json);
                        ACache.get(getApplicationContext()).put(Constant.CARDLIST, jsons);
                    }
                    
                    Toast.makeText(getApplicationContext(), "添加银行卡成功!",
                            Toast.LENGTH_SHORT).show();
                    // datas = data.getJSONArray("data");
                    // adapter = new MyAdapter(AddCardActivity.this, datas);
                    // listView.setAdapter(adapter);
                    // ACache.get(getApplicationContext()).put(Constant.CARDLIST,
                    // datas);
                } else if (code == 2) {

                    Toast.makeText(getApplicationContext(), "账户和密码错误",
                            Toast.LENGTH_SHORT).show();
                }else if(code == 3){
                    
                    Toast.makeText(getApplicationContext(), "访问服务器出错",
                            Toast.LENGTH_SHORT).show();
                }
                
                
                else {

                    Toast.makeText(getApplicationContext(), "银行卡重复或者添加失败...",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
