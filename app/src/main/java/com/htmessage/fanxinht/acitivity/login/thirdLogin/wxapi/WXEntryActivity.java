package com.htmessage.fanxinht.acitivity.login.thirdLogin.wxapi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.main.MainActivity;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.sdk.client.HTClient;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	private static final String TAG = WXEntryActivity.class.getSimpleName();
	private IWXAPI wxapi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_activity);
		//		微信登录API
		wxapi = WXAPIFactory.createWXAPI(getApplicationContext(), HTConstant.WX_APP_ID);
//		wxapi.registerApp(HTConstant.WX_APP_ID);
		//如果没回调onResp，八成是这句没有写
		wxapi.handleIntent(getIntent(), WXEntryActivity.this);
	}

	// 微信发送消息给app，app接受并处理的回调函数
	@Override
	public void onReq(BaseReq baseReq) {

	}

	// app发送消息给微信，微信返回的消息回调函数,根据不同的返回码来判断操作是否成功
	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(this, R.string.login_cancle, Toast.LENGTH_SHORT).show();
				finish();
				break;
			case BaseResp.ErrCode.ERR_OK:
						// 获取到code
					String code = ((SendAuth.Resp) resp).code;
					List<Param> params = new ArrayList<>();
					params.add(new Param("appid",HTConstant.WX_APP_ID));
					params.add(new Param("secret",HTConstant.WX_APP_SECRET));
					params.add(new Param("code",code));
					params.add(new Param("grant_type","authorization_code"));
					new OkHttpUtils(WXEntryActivity.this).post(params, HTConstant.WX_APP_OAUTH2_URL, new OkHttpUtils.HttpCallBack() {
						@Override
						public void onResponse(com.alibaba.fastjson.JSONObject jsonObject) {
							if (jsonObject !=null){
										Log.d(TAG,jsonObject.toJSONString());
										String unionid = jsonObject.getString("unionid");
										final String openid = jsonObject.getString("openid");
										String refresh_token = jsonObject.getString("refresh_token");
										String expires_in = jsonObject.getString("expires_in");
										String access_token = jsonObject.getString("access_token");
								Log.d(TAG,unionid + "===" + openid + "===" + refresh_token + "===" + expires_in + "===" + access_token);
										List<Param> paramsList = new ArrayList<Param>();
										paramsList.add(new Param("access_token",access_token));
										paramsList.add(new Param("openid",openid));
								new OkHttpUtils(WXEntryActivity.this).post(paramsList, HTConstant.WX_APP_USERINFO_URL, new OkHttpUtils.HttpCallBack() {
									@Override
									public void onResponse(com.alibaba.fastjson.JSONObject response) {
										if (response != null) {
											try {
												String nickname = response.getString("nickname");
												String sex = response.getString("sex");
												String province = response.getString("province");
												String city = response.getString("city");
												String country = response.getString("country");
												String headimgurl = response.getString("headimgurl");
												Log.d(TAG, "nickname===" + nickname + "sex===" + sex + "province===" +province+"city==="+city+"country==="+country+"headimgurl==="+headimgurl);
												loginByThird(openid, headimgurl, nickname, "weixin");
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}

									@Override
									public void onFailure(String errorMsg) {

									}
								});
							}
						}

						@Override
						public void onFailure(String errorMsg) {

						}
					});
				break;
		}
	}


	private void loginIm(final JSONObject userJson,final Dialog progressDialog) {

		String userId=userJson.getString(HTConstant.JSON_KEY_HXID);
		String password= userJson.getString(HTConstant.JSON_KEY_PASSWORD);
		if(TextUtils.isEmpty(password)){
			password=userJson.getString("password");
		}
		HTClient.getInstance().login(userId, password, new HTClient.HTCallBack() {
			@Override
			public void onSuccess() {
				if (userJson == null) {
					return;
				}
				saveFriends(userJson);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(getApplicationContext(), R.string.login_success,
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(WXEntryActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					}
				});
			}

			@Override
			public void onError() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}


	private void saveFriends(final JSONObject jsonObject) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONArray friends = jsonObject.getJSONArray("friend");
				if (jsonObject.containsKey("friend")) {
					jsonObject.remove("friend");
				}
				String sex = jsonObject.getString(HTConstant.JSON_KEY_SEX);
				if (TextUtils.isEmpty(sex)){
					jsonObject.put(HTConstant.JSON_KEY_SEX,"1");
				}
				HTApp.getInstance().setUserJson(jsonObject);
				Map<String, User> userlist = new HashMap<String, User>();
				if (friends != null) {
					for (int i = 0; i < friends.size(); i++) {
						JSONObject friend = friends.getJSONObject(i);
						User user = CommonUtils.Json2User(friend);
						userlist.put(user.getUsername(), user);
					}
					Log.d("friends1-->", userlist.size() + "");
					List<User> users = new ArrayList<User>(userlist.values());
					ContactsManager.getInstance().saveContactList(users);
				}
			}
		}).start();

	}
	private void loginByThird(String thirdOpenId,String thirdAvatar,String thirdNickname,String thirdType) {

		final Dialog pd = HTApp.getInstance().createLoadingDialog(WXEntryActivity.this, getString(R.string.Is_landing));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		List<Param> params = new ArrayList<Param>();
		params.add(new Param("openID", thirdOpenId));
		params.add(new Param("avatar", thirdAvatar));
		params.add(new Param("nickname", thirdNickname));
		params.add(new Param("type", thirdType));
		new OkHttpUtils(WXEntryActivity.this).post(params, HTConstant.URL_THIRDLOGIN, new OkHttpUtils.HttpCallBack() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				String info = null;
				int status = jsonObject.getInteger("code");
				switch(status){
					case 1:
						JSONObject json = jsonObject.getJSONObject("user");
						loginIm(json, pd);
						break;
					case -1:
						pd.dismiss();
						info = getString(R.string.Account_does_not_exist);
						Toast.makeText(WXEntryActivity.this, info, Toast.LENGTH_SHORT).show();
						break;
					case -2:
						pd.dismiss();
						info = getString(R.string.Incorrect_password);
						Toast.makeText(WXEntryActivity.this, info, Toast.LENGTH_SHORT).show();
						break;
					case -3:
						pd.dismiss();
						info = getString(R.string.Account_has_been_disabled);
						Toast.makeText(WXEntryActivity.this, info, Toast.LENGTH_SHORT).show();
						break;
					default:
						info = getString(R.string.Server_busy);
						Toast.makeText(WXEntryActivity.this, info, Toast.LENGTH_SHORT).show();
						break;
				}
			}

			@Override
			public void onFailure(String errorMsg) {

			}
		});
	}
}