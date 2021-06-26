package com.htmessage.yichat.acitivity.main.wallet;

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
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.widget.HTAlertDialog;

/**
 * Created by huangfangyi on 2019/6/4.
 * qq 84543217
 */
public class PreAddBandCardActivity extends BaseActivity {

    private ListView listView;
    private Myadapter myadapter;
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    JSONArray data= (JSONArray) msg.obj;
                    setAapter(data);
                    break;
                case 1001:
                    int resId=msg.arg1;
                    Toast.makeText(PreAddBandCardActivity.this,resId,Toast.LENGTH_SHORT).show();
                    break;

                case 1002:
                    JSONArray data1= (JSONArray) msg.obj;
                    setAapter(data1);
                    Toast.makeText(PreAddBandCardActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pre_add_bankcard);
        setTitle("添加银行卡");
        findViewById(R.id.ll_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreAddBandCardActivity.this,NextAddBandCardActivity.class));

            }
        });
        listView=findViewById(R.id.listView);
        JSONArray data= WalletUtils.getInstance().getBankCardList();
        if(data!=null){
            setAapter(data);
        }
    }

    private void  setAapter(JSONArray data){
        myadapter=new Myadapter(PreAddBandCardActivity.this,data);
        listView.setAdapter(myadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               new HTAlertDialog(PreAddBandCardActivity.this,null,new String[]{"删除银行卡"}).init(new HTAlertDialog.OnItemClickListner() {
                   @Override
                   public void onClick(int position) {
                       switch (position){
                           case 0:
                               String cardId=myadapter.getItem(position).getString("id");
                               deleteCard(cardId);
                               break;
                       }
                   }
               });

            }
        });
    }

    class  Myadapter extends BaseAdapter{

        private Context context;
        private JSONArray jsonArray;


        public Myadapter(Context context, JSONArray jsonArray){
            this.context=context;
            this.jsonArray=jsonArray;
        }


        @Override
        public int getCount() {
            return jsonArray.size();
        }

        @Override
        public JSONObject  getItem(int position) {
            return jsonArray.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.item_bankcard,parent,false);
            }

            TextView tvBankName=convertView.findViewById(R.id.tv_bankname);

            TextView tvBankNumber=convertView.findViewById(R.id.tv_bankNumber);
            JSONObject jsonObject=getItem(position);
            String bankName=jsonObject.getString("bankName");
            String bankNumber=jsonObject.getString("bankNumber");
            if(bankNumber.length()>4){
                bankNumber=bankNumber.substring(bankNumber.length()-4);
            }
            tvBankNumber.setText("* * * *   * * * *   * * * *   "+bankNumber);
            tvBankName.setText(bankName);
            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCardListFromServer();
    }

    private void getCardListFromServer(){

        ApiUtis.getInstance().postJSON(new JSONObject(), Constant.URL_bank_card_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                   JSONArray data=jsonObject.getJSONArray("data");
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
                if(handler==null){
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });
    }

    private void deleteCard(String cardId){
        JSONObject body=new JSONObject();
        body.put("cardId",cardId);

        ApiUtis.getInstance().postJSON(body, Constant.URL_bank_card_delete, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data=jsonObject.getJSONArray("data");
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
                if(handler==null){
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;

    }
}
