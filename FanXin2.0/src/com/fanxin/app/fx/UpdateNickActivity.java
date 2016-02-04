package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;
 







import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateNickActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);
        final String nick=LocalUserInfo.getInstance(UpdateNickActivity.this).getUserInfo("nick");
        
      final EditText  et_nick= (EditText) this.findViewById(R.id.et_nick);
       et_nick.setText(nick);
      TextView  tv_save= (TextView) this.findViewById(R.id.tv_save);
      tv_save.setOnClickListener(new OnClickListener(){

        @Override
        public void onClick(View v) {
            String newNick=et_nick.getText().toString().trim();
            if(nick.equals(newNick)||newNick.equals("")||newNick.equals("0")) {
                return;
            }  
            updateIvnServer(newNick);
        }
          
      });
          

    }
    
    private  void updateIvnServer(final String newNick){
        Map<String, String> map = new HashMap<String, String>();
         String  hxid=LocalUserInfo.getInstance(UpdateNickActivity.this).getUserInfo("hxid");
        map.put("newNick", newNick);
        map.put("hxid", hxid);
        final ProgressDialog dialog = new ProgressDialog(UpdateNickActivity.this);
        dialog.setMessage("正在更新...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        LoadDataFromServer registerTask = new LoadDataFromServer(
                UpdateNickActivity.this, Constant.URL_UPDATE_Nick, map);

        registerTask.getData(new DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                dialog.dismiss();
                try {
                    int code = data.getInteger("code");
                    if (code == 1) {
                        LocalUserInfo.getInstance(UpdateNickActivity.this).setUserInfo("nick", newNick);
                       finish();
                       
                    } 
                      else {
                        
                        Toast.makeText(UpdateNickActivity.this,
                                "更新失败...", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (JSONException e) {
                   
                    Toast.makeText(UpdateNickActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });
    }

    public void back(View view) {

        finish();
    }
}
