package com.fanxin.app.fx;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
 
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
 




import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.fx.others.AutoListView;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.AutoListView.OnLoadListener;
import com.fanxin.app.fx.others.AutoListView.OnRefreshListener;
import com.fanxin.app.fx.others.LastLoginAdapter;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class LasterLoginUserActivity extends BaseActivity implements OnRefreshListener,
OnLoadListener {
 
  
  
    AutoListView autoListView;
    LastLoginAdapter adapter;
 
    String time ="0";
    List<JSONObject> list= new ArrayList<JSONObject>();
    
    int page=0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            @SuppressWarnings("unchecked")
            List<JSONObject> result = (List<JSONObject>) msg.obj;
            switch (msg.what) {
            case AutoListView.REFRESH:
                autoListView.onRefreshComplete();
                list.clear();
                list.addAll(result);
         
             
                break;
            case AutoListView.LOAD:
                autoListView.onLoadComplete();
                list.addAll(result);
            
                break;
            }
             
            autoListView.setResultSize(result.size());
            adapter.setTime(time);
            MYApplication.last_time=time;
            MYApplication.page=page; 
            adapter.notifyDataSetChanged();
        };
    };
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lasterloginuser);
       
        autoListView=(AutoListView)findViewById(R.id.listview);
        
        list= MYApplication.getInstance().list;
        page=MYApplication.page;
        time=MYApplication.last_time;
        adapter=new LastLoginAdapter(list,LasterLoginUserActivity.this,time);     
        autoListView.setAdapter(adapter);
        autoListView.setOnRefreshListener(this);
        autoListView.setOnLoadListener(this);
        
        if(list==null||list.size()==0){
            initData();
                
        }
        autoListView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                  if(view.getTag()=="HEADER"||view.getTag().equals("HEADER")||view.getTag()=="FOOTER"||view.getTag().equals("FOOTER")){
                        return;
                    }
                  JSONObject json = list.get(position-1);
                  
                    String hxid=json.getString("hxid");
                    String nick=json.getString("nick");
                    String avatar=json.getString("avatar");
                    
                    String sex=json.getString("sex");
                    
                    Intent intent =new Intent();
                    intent.putExtra("hxid", hxid);
                    intent.putExtra("nick", nick);
                    intent.putExtra("avatar", avatar);
                    intent.putExtra("sex", sex);
            
                    
                    intent.setClass(LasterLoginUserActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                    
                    
                    
               
                  
                  
                  
                  
                  
            }
            
            
        });
        
        
         
    }
    
 
    
    private void initData() {
        
       
        loadData(AutoListView.REFRESH);
        
         
         
    }
    public List<JSONObject> getData(){
        List<JSONObject> jsonList=new ArrayList<JSONObject>();
        HttpClient client = new DefaultHttpClient();
        StringBody pageBody = null;
        try {
            pageBody=new StringBody(String.valueOf(page));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("上传的数据是------->>>>>>>>"
                +String.valueOf(page));
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("num", pageBody);
        client.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                30000);
        HttpPost post = new HttpPost(Constant.URL_LASTERLOGIN);
        post.setEntity(entity);
        StringBuilder builder = new StringBuilder();
        try {
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity()
                                .getContent(), Charset.forName("UTF-8")));
                for (String s = reader.readLine(); s != null; s = reader
                        .readLine()) {
                    builder.append(s);
                    System.out.println("返回数据是------->>>>>>>>"
                            + builder.toString());
                    String builder_BOM = jsonTokener(builder.toString());
                    
                    
 
                    
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = JSONObject.parseObject(builder_BOM);
                        time= jsonObject.getString("time");
                     
                        JSONArray jsonArray = jsonObject.getJSONArray("users");
                        if(jsonObject!=null)
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject2 = (JSONObject)  jsonArray.getJSONObject(i);
                   
                                jsonList.add(jsonObject2);
                         
                            
                        }
                        System.out.println("看返回的json数组----->>>>>"+list);
                 
                    
                }
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }
        
        
        
        
        
        return jsonList;
        
  
        
        
    }
    private String jsonTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
    

    private void loadData(final int what) {
 
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj =getData();
                handler.sendMessage(msg);

            }
        }).start();
    }
    
   

    @Override
    public void onLoad() {
        loadData(AutoListView.LOAD);
        page=page+1;
        
    
    }


    @Override
    public void onRefresh() {
        loadData(AutoListView.REFRESH);
        page=0;
    }
 
     
}
