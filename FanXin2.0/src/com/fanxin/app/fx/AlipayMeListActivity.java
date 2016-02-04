package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

public class AlipayMeListActivity extends BaseActivity {
    private ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipaymelist);
        listView=(ListView) this.findViewById(R.id.listView);
        getData();
    }
     private void getData(){
         
         Map<String, String> map = new HashMap<String, String>();
         
        final ProgressDialog dialog = new ProgressDialog(AlipayMeListActivity.this);
        dialog.setMessage("正在获取数据...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        LoadDataFromServer registerTask = new LoadDataFromServer(
                AlipayMeListActivity.this, Constant.URL_ALIPAYMELIST, map);

        registerTask.getData(new DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                dialog.dismiss();
                try {
                    int code = data.getInteger("code");
                    if (code == 1) {
                        JSONArray lists=data.getJSONArray("list");
                        if(lists!=null&&lists.size()!=0){
                            
                            MyAdapter adapter=new MyAdapter(AlipayMeListActivity.this,lists);
                            listView.setAdapter(adapter);
                        }
                     
                       
                    } 
                      else {
                        
                        Toast.makeText(AlipayMeListActivity.this,
                                "访问数据库失败...", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (JSONException e) {
                   
                    Toast.makeText(AlipayMeListActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });
    }
     
     class MyAdapter extends BaseAdapter {
         private Context context;
         private JSONArray jsons;
         private LayoutInflater inflater;

         public MyAdapter(Context context, JSONArray jsons) {

             this.context = context;
             this.jsons = jsons;
             inflater = LayoutInflater.from(context);
         }

         @Override
         public int getCount() {
             // TODO Auto-generated method stub
             return jsons.size();
         }

         @Override
         public JSONObject getItem(int position) {
             // TODO Auto-generated method stub
             return jsons.getJSONObject(position);
         }

         @Override
         public long getItemId(int position) {
             // TODO Auto-generated method stub
             return position;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             if (convertView == null) {

                 convertView = inflater.inflate(R.layout.item_alipayme, parent,
                         false);
             }
             ViewHolder holder = (ViewHolder) convertView.getTag();
             if (holder == null) {
                 holder = new ViewHolder();
                 holder.tv_aid = (TextView) convertView
                         .findViewById(R.id.tv_aid);
                 holder.tv_content = (TextView) convertView
                         .findViewById(R.id.tv_content);
                 holder.tv_name = (TextView) convertView
                         .findViewById(R.id.tv_name);
                 holder.tv_money = (TextView) convertView
                         .findViewById(R.id.tv_money);
                 convertView.setTag(holder);
             }
             JSONObject json = getItem(position);
             String name = json.getString("name");
             String aid = json.getString("aid");

             String content = json.getString("content");

             String money = json.getString("money");
             holder.tv_aid.setText("编号:" + aid);
             holder.tv_content.setText(content);

             holder.tv_name.setText(name);

             holder.tv_money.setText(money + "元");

             return convertView;
         }

     }

     class ViewHolder {
         TextView tv_name;
         TextView tv_content;
         TextView tv_aid;
         TextView tv_money;

     }


         
     }
   