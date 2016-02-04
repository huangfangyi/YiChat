package com.fanxin.app.comments;



import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.MYApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class SendTask {

	private String url;
	private List<Uri> images;
	private String content;// 朋友圈文字
	private String location;// 位置
	private String userID;// 
    private String imageStr="0";
	public SendTask(Context context, String url, List<Uri> images,
			String content, String location) {

		this.url = url;
		this.images = images;
		this.content = content;
		this.location = location;
		userID=MYApplication.getInstance().getUserName();
		 
	}

	@SuppressLint("HandlerLeak")
	public void getData(final DataCallBack dataCallBack) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 111 && dataCallBack != null) {
					JSONObject jsonObject = (JSONObject) msg.obj;

					dataCallBack.onDataCallBack(jsonObject);

				} else {
					dataCallBack.onDataCallBack(null);
					Log.e("APIerrorCode:", String.valueOf(msg.what));

				}
			}
		};

		new Thread() {

			@SuppressLint("SdCardPath")
			public void run() {
				HttpClient client = new DefaultHttpClient();

				MultipartEntity entity = new MultipartEntity();
				int num = images.size();
				for (int i = 0; i < num; i++) {

					String imageUrl = images.get(i).getPath();
					String filename = imageUrl.substring(imageUrl
							.lastIndexOf("/") + 1);
				    
					File file = new File("/sdcard/bizchat/"+filename);
					
					File file_big = new File("/sdcard/bizchat/"+"big_"+filename);
					
					if(file.exists()&&file_big.exists()){
						Log.e("imageStr_ok---->>>>>>.", "ffffff");
					}else{
						Log.e("imageStr_ok---->>>>>>.", "ggggggg");
					}
					// 小图
					entity.addPart("file_" + String.valueOf(i), new FileBody(
							file));
					// 大图
					
					entity.addPart("file_" + String.valueOf(i) + "_big",
							new FileBody(file_big));
					if(i==0){
						imageStr=filename;
					}else{
						imageStr=imageStr+"split"+filename;
						Log.e("imageStr---->>>>>>.", imageStr);
					}
				  
				}
				try {
					// 图片总数量
					entity.addPart("num", new StringBody(String.valueOf(num),
							Charset.forName("UTF-8")));
					Log.e("num---->>>>>>.",String.valueOf(num));
					// 文章内容
					entity.addPart("content",
							new StringBody(content, Charset.forName("UTF-8")));
					Log.e("content---->>>>>>.",content);
					// 位置
					if(!TextUtils.isEmpty(location)){
						entity.addPart("location",
								new StringBody(location, Charset.forName("UTF-8")));
					}else{
 
						entity.addPart("location",
								new StringBody("0", Charset.forName("UTF-8")));
					}
					//Log.e("location---->>>>>>.",location);
					//发布者id
					entity.addPart("userID",
							new StringBody(userID, Charset.forName("UTF-8")));
					//拼接的图片
					entity.addPart("imageStr",
							new StringBody(imageStr, Charset.forName("UTF-8")));
					Log.e("imageStr---->>>>>>.",imageStr);
				} catch (UnsupportedEncodingException e1) {

					e1.printStackTrace();
				}
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);

				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 30000);
				HttpPost post = new HttpPost(url);
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
						}
						String builder_BOM = jsonTokener(builder.toString());
						System.out.println("返回数据是------->>>>>>>>"
								+ builder.toString());
						try {
							JSONObject jsonObject = new JSONObject();
							jsonObject = JSONObject.parseObject(builder_BOM);
							Message msg = handler.obtainMessage();
							msg.what = 111;
							msg.obj = jsonObject;
							handler.sendMessage(msg);
						} catch (JSONException e) {
							Message msg = handler.obtainMessage();
							msg.what = 222;
							msg.obj = null;
							handler.sendMessage(msg);
						}

					} else {
						Log.e("response.getStatusLine().getStatusCode() ----》》",
								String.valueOf(response.getStatusLine()
										.getStatusCode()));
						Message msg = handler.obtainMessage();
						msg.what = 333;
						msg.obj = null;
						handler.sendMessage(msg);
					}

				} catch (ClientProtocolException e) {
					Message msg = handler.obtainMessage();
					msg.what = 444;
					msg.obj = null;
					handler.sendMessage(msg);

				} catch (IOException e) {
					Message msg = handler.obtainMessage();
					msg.what = 555;
					msg.obj = null;
					handler.sendMessage(msg);
				}

			}
		}.start();

	}

	private String jsonTokener(String in) {

		if (in != null && in.startsWith("\ufeff")) {
			in = in.substring(1);
		}
		return in;
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface DataCallBack {
		void onDataCallBack(JSONObject data);
	}

}
