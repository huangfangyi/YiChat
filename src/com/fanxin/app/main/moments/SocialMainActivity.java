package com.fanxin.app.main.moments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yinglouquan.app.Constant;
import com.yinglouquan.app.MYApplication;
import com.yinglouquan.app.R;
import com.yinglouquan.app.activity.BaseActivity;
import com.yinglouquan.app.comments.SocialApiTask.DataCallBack;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SocialMainActivity extends BaseActivity {

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

	private String imageName;
	private PullToRefreshListView pull_refresh_list;
	private List<JSONObject> articles=new ArrayList<JSONObject>();
	
//	private JSONArray datas = new JSONArray();
	private SocialMainAdapter adapter;
	private ListView actualListView;
	private int page = 0;

	String userID;
	List<String> sIDs = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_social_main);
		super.onCreate(arg0);
		userID = this.getIntent().getStringExtra("userID");
		initFile();
		initView();
	}

	@SuppressLint("SdCardPath")
	public void initFile() {

		File dir = new File("/sdcard/bizchat");

		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private void initView() {
		
		pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pull_refresh_list.setMode(Mode.BOTH);

		pull_refresh_list
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								SocialMainActivity.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.

						if (pull_refresh_list.getCurrentMode() == Mode.PULL_FROM_START) {
							page = 0;

						} else if (pull_refresh_list.getCurrentMode() == Mode.PULL_FROM_END) {
							page++;

						}

						getData(page);
					}
				});

		actualListView = pull_refresh_list.getRefreshableView();
		adapter = new SocialMainAdapter(SocialMainActivity.this, articles);
		actualListView.setAdapter(adapter);
		actualListView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				adapter.hideCommentEditText();
				return false;
			}

		});
		getData(0);
		pull_refresh_list.setRefreshing(false);
		ImageView iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
		iv_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPhotoDialog();
			}

		});
	}


	private void showPhotoDialog() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.dialog_social_main);
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("拍照");
		tv_paizhao.setOnClickListener(new OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {

				imageName = getNowTime() + ".jpg";
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File("/sdcard/bizchat/", imageName)));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
				dlg.cancel();
			}
		});
		TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
		tv_xiangce.setText("相册");
		tv_xiangce.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				getNowTime();
				imageName = getNowTime() + ".jpg";
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

				dlg.cancel();
			}
		});

	}

	@SuppressLint("SimpleDateFormat")
	private String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
	}

	@SuppressLint("SdCardPath")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			String path = null;

			switch (requestCode) {

			case PHOTO_REQUEST_TAKEPHOTO:
				path = "/sdcard/bizchat/" + imageName;
				break;

			case PHOTO_REQUEST_GALLERY:

				if (data != null) {
					Uri imageFilePath = data.getData();

					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(imageFilePath,
							proj, null, null, null);
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					// 获取图片真实地址
					path = cursor.getString(column_index);
					System.out.println(path);

				}

				break;

			}

			Intent intent = new Intent();
			intent.putExtra("imagePath", path);

			intent.setClass(SocialMainActivity.this,
					SocialPublishActivity.class);
			startActivity(intent);
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void getData(final int page_num) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userID", userID);
		map.put("num", String.valueOf(page_num));
		SocialApiTask task = new SocialApiTask(SocialMainActivity.this,
				Constant.URL_SOCIAL, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				pull_refresh_list.onRefreshComplete();
				if (data == null) {

					return;

				}
				int code = data.getInteger("code");
				if (code == 1000) {
					JSONArray users_temp = data.getJSONArray("data");
					String time = data.getString("time");
					MYApplication.getInstance().setTime(time);
					if (page_num == 0) {

					//	datas = users_temp;
						articles.clear();
						sIDs.clear();
						for (int i = 0; i < users_temp.size(); i++) {
							JSONObject json = users_temp.getJSONObject(i);
							String sID = json.getString("sID");
							sIDs.add(sID);
							articles.add(json);
						}

					} else {

						Map<String, JSONObject> map = new HashMap<String, JSONObject>();

						for (int i = 0; i < users_temp.size(); i++) {
							JSONObject json = users_temp.getJSONObject(i);
							String sID = json.getString("sID");
							if (!sIDs.contains(sID)) {
								sIDs.add(sID);
								articles.add(json);
							}
						}

					}
//					adapter = new SocialMainAdapter(SocialMainActivity.this,
//							datas, time);
				//	actualListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					// ACache.get(getActivity()).put("last_login", users);

				} else {
					// ToastUtil.showMessage("服务器出错...");
				}
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		getData(0);
	}

}
