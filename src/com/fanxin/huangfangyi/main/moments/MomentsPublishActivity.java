package com.fanxin.huangfangyi.main.moments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
 
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.main.utils.PathUtils;
import com.fanxin.huangfangyi.ui.BaseActivity;


public class MomentsPublishActivity extends BaseActivity {

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private String imagePath = null;
	private GridView gridview;
	private LinearLayout ll_location;
	private List<Uri> lists = new ArrayList<Uri>();
	private ImageAdapter adapter;
	private String imageName;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";
	// 显示位置的TextView
	private TextView tv_location;
	private TextView tv_cancel;
	private String mylocation;
	// 发送按钮
	private Button btn_send;
	// 文本输入

	private EditText et_content;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.fx_activity_publish_moments);
		super.onCreate(arg0);
		imagePath = this.getIntent().getStringExtra("imagePath");
		Log.e("imagePath---->>>>.", imagePath);
		if (TextUtils.isEmpty(imagePath)) {
			finish();
			return;
		}
		Uri uri_temp = Uri.fromFile(new File(imagePath));

		// 第一张图片要特别注意一下，是传过来的....
		getTwoImage(uri_temp, true);
		lists.add(uri_temp);
		initView();

		// 位置相关
		mLocationClient = new LocationClient(this); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
	}

	private void initView() {

		gridview = (GridView) this.findViewById(R.id.gridview);

		adapter = new ImageAdapter(MomentsPublishActivity.this, lists);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (lists.size() < 9 && position == lists.size()) {
					showPhotoDialog();
				} else {

					checkDialog(position);

				}

			}

		});
		// 获取位置
		tv_location = (TextView) this.findViewById(R.id.tv_location);
		tv_cancel = (TextView) this.findViewById(R.id.tv_cancel);
		ll_location = (LinearLayout) this.findViewById(R.id.ll_location);

		ll_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InitLocation();
				mLocationClient.start();
				tv_location.setText("正在获取位置...");
			}

		});

		et_content = (EditText) this.findViewById(R.id.et_content);
		btn_send = (Button) this.findViewById(R.id.btn_send);
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = et_content.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {

					Toast.makeText(getApplicationContext(), "请输入文字内容....",
							Toast.LENGTH_SHORT).show();
					return;

				}
//				if (lists.size() == 0) {
//					Toast.makeText(getApplicationContext(), "请选择图片....",
//							Toast.LENGTH_SHORT).show();
//					return;
//				}
				send(content);
			}

		});

	}
	// 发送
	private void send(String content) {
		dialog = new ProgressDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("正在发布...");

		dialog.show();
		if(TextUtils.isEmpty(mylocation)){
			mylocation="0";

		}
		List<Param> params=new ArrayList<>();
		params.add(new Param("content",content));
		params.add(new Param("location",mylocation));
		OkHttpManager.getInstance().postMoments(params, lists, FXConstant.URL_PUBLISH, new OkHttpManager.HttpCallBack() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				int code = jsonObject.getInteger("code");
				if (code == 1000) {

					Toast.makeText(getApplicationContext(), "发布成功...",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"服务器端错误:" + String.valueOf(code),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(String errorMsg) {

			}
		});

	}

	class ImageAdapter extends BaseAdapter {
		LayoutInflater inflater;
		Context context;
		List<Uri> list;

		public ImageAdapter(Context context, List<Uri> list) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			int total = list.size();
			if (total < 9)
				total++;
			return total;
		}

		@Override
		public Uri getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.fx_item_gridview_image, null);
			SimpleDraweeView sdv_image = (SimpleDraweeView) convertView
					.findViewById(R.id.sdv_image);
			if (position == list.size() && list.size() < 9) {
				GenericDraweeHierarchy hierarchy = sdv_image.getHierarchy();
				hierarchy.setPlaceholderImage(R.drawable.fx_icon_tag_add);
			} else {

				Uri uri_temp = getItem(position);
				sdv_image.setImageURI(uri_temp);
			}
			return convertView;
		}

	}

	private void showPhotoDialog() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.fx_dialog_social_main);
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("拍照");
		tv_paizhao.setOnClickListener(new OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {

				imageName = getNowTime() + ".jpg";
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File( Environment.getExternalStorageDirectory().toString()+"/bizchat/" , imageName)));
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
				path = Environment.getExternalStorageDirectory().toString()+"/bizchat/" + imageName;
				System.out.println(path);
				break;

			case PHOTO_REQUEST_GALLERY:

				if (data != null) {
					path = PathUtils.getPath(MomentsPublishActivity.this,data.getData());
					System.out.println(path);
				}

				break;

			}
			getTwoImage(Uri.fromFile(new File(path)), false);

			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	// 压缩处理生成一大一小两张图片

	@SuppressLint("SdCardPath")
	private void getTwoImage(Uri uri, boolean is_first) {
		if (uri == null) {
			Toast.makeText(getApplicationContext(), "添加图片失败,请重试...",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String imageUrl = uri.getPath();
		String imageName_temp = imageUrl
				.substring(imageUrl.lastIndexOf("/") + 1);
		// 生成大图
		save(imageUrl, 200, "big_" + imageName_temp);
		// 生成小图
		save(imageUrl, 60, imageName_temp);
		Log.e("imageUrl---->>>>", imageUrl);
		Log.e("imageName_temp---->>>>", imageName_temp);

		if ((new File( Environment.getExternalStorageDirectory().toString()+"/bizchat/"  + imageName_temp)).exists()
				&& (new File( Environment.getExternalStorageDirectory().toString()+"/bizchat/"  + "big_" + imageName_temp))
						.exists()) {
			if (!is_first) {
				lists.add(uri);
				adapter.notifyDataSetChanged();
			}

		} else {

			Toast.makeText(getApplicationContext(), "添加图片失败,请重试",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void save(String path, int size, String saveName) {

		try {
			// File f = new File(path);

			Bitmap bm = PictureUtil.getSmallBitmap(path);
			int degree = readPictureDegree(path);

			if (degree != 0) {// 旋转照片角度
				bm = rotateBitmap(bm, degree);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

			FileOutputStream fos = new FileOutputStream(new File(
					PictureUtil.getAlbumDir(), saveName));

			int options = 100;
			// 如果大于80kb则再次压缩,最多压缩三次
			while (baos.toByteArray().length / 1024 > size && options > 10) {
				// 清空baos
				baos.reset();
				// 这里压缩options%，把压缩后的数据存放到baos中
				bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
				options -= 30;
			}

			fos.write(baos.toByteArray());
			fos.close();
			baos.close();
			// bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);

			// Toast.makeText(this, "Compress OK!", Toast.LENGTH_SHORT).show();

		} catch (Exception e) {

		}

	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	private void checkDialog(final int position) {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.fx_dialog_social_main);
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("看大图");
		tv_paizhao.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent it = new Intent(Intent.ACTION_VIEW);

				it.setDataAndType(lists.get(position), "image/*");
				startActivity(it);
				dlg.cancel();
			}
		});
		TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
		tv_xiangce.setText("删除");
		tv_xiangce.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				lists.remove(position);
				adapter.notifyDataSetChanged();
				dlg.cancel();
			}
		});

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			String str_addr = location.getAddrStr();
			if (!TextUtils.isEmpty(str_addr)) {
				mLocationClient.stop();
				tv_location.setText(str_addr);
				tv_cancel.setVisibility(View.VISIBLE);
				mylocation = str_addr;
				tv_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						tv_location.setText("所在位置");
						tv_cancel.setVisibility(View.GONE);
						mylocation = "";
					}

				});
			}
 		}

	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//
		option.setCoorType(tempcoor);//
		int span = 1000;
		//
		option.setScanSpan(span);//
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
