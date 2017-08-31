package com.htmessage.fanxinht.acitivity.moments.publish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.moments.BigImageActivity;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.HTPathUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class MomentsPublishActivity extends BaseActivity {
    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_LOCAL = 1;
    private File cameraFile;
    private GridView gridview;
    private LinearLayout ll_location;
    private List<String> pathList = new ArrayList<>();
    private ImageAdapter adapter;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";
    // 显示位置的TextView
    private TextView tv_location;
    private TextView tv_cancel;
    private String mylocation;
    private String category = "0";
    private String coordinate = "0";
    // 发送按钮
    private Button btn_send;
    // 文本输入
    private EditText et_content;
    private int temp = 0;//次数
    private List<String> nameList = new ArrayList<>();
    private ProgressDialog dialog;
    OSSClient oss;
    private Handler hanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    temp++;
                    if (temp == pathList.size()) {
                        String content = et_content.getText().toString().trim();
                        sendSocial(content);
                    }
                    break;
                case 1:
                    temp = 0;
                    dialog.dismiss();
                    CommonUtils.showToastShort(MomentsPublishActivity.this, R.string.update_failed);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_publish_moments);
        super.onCreate(arg0);
        ArrayList<String> imagePath = this.getIntent().getStringArrayListExtra("pathList");
        initView(imagePath);

        // 位置相关
        mLocationClient = new LocationClient(this); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
    }

    private void initView(ArrayList<String> images) {
        pathList.addAll(images);
        gridview = (GridView) this.findViewById(R.id.gridview);

        adapter = new ImageAdapter(MomentsPublishActivity.this, pathList);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (pathList.size() < 9 && position == pathList.size()) {
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
                tv_location.setText(R.string.getting_location);
            }

        });

        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    CommonUtils.showToastShort(getApplicationContext(), R.string.input_text);
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
        dialog.setMessage(getString(R.string.publishing));
        dialog.show();
        if (TextUtils.isEmpty(mylocation)) {
            mylocation = "0";
        }
        compressMore(pathList);

    }

    private void uploadImages(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        nameList.add(fileName);
        new UploadFileUtils(MomentsPublishActivity.this, fileName, filePath).asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                int body = result.getStatusCode();
                if (body == 200) {
                    hanler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                hanler.sendEmptyMessage(1);
                Log.d("---->", "链接错误:" + clientExcepion.getMessage() + "====服务错误:" + serviceException.getMessage());
            }
        });
    }

    private void sendSocial(final String content) {
        List<Param> params = new ArrayList<>();
        JSONObject userJson = HTApp.getInstance().getUserJson();
        String userId = userJson.getString(HTConstant.JSON_KEY_HXID);
        params.add(new Param("userId", userId));
        params.add(new Param("category", category));
        params.add(new Param("coordinate", coordinate));
        params.add(new Param("content", content));
        params.add(new Param("location", mylocation));
        String imagePath = "";
        if (nameList.size() != 1 || nameList.size() > 1) {
            for (int i = 1; i < nameList.size() - 1; i++) {
                imagePath += nameList.get(i) + ",";
            }
            imagePath = nameList.get(0) + "," + imagePath + nameList.get(nameList.size() - 1);
        } else {
            imagePath = nameList.get(0);
        }
        params.add(new Param("imagestr", imagePath));
        new OkHttpUtils(this).post(params, HTConstant.URL_PUBLISH, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        JSONObject data = jsonObject.getJSONObject("data");
                        Intent intent = new Intent();
                        intent.putExtra("data", data.toJSONString());
                        setResult(Activity.RESULT_OK, intent);

                        finish();
                        break;
                    default:
                        CommonUtils.showToastShort(getApplicationContext(), getString(R.string.server_wrong) + code);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                dialog.dismiss();
                CommonUtils.showToastShort(getApplicationContext(), errorMsg);
            }
        });
    }

    class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;
        private List<String> list;

        public ImageAdapter(Context context, List<String> list) {
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
        public String getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_gridview_image, null);
            SimpleDraweeView sdv_image = (SimpleDraweeView) convertView
                    .findViewById(R.id.sdv_image);
            if (position == list.size() && list.size() < 9) {
                GenericDraweeHierarchy hierarchy = sdv_image.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.icon_tag_add);
            } else {

                String url = getItem(position);

                Glide.with(MomentsPublishActivity.this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_image).into(sdv_image);

            }
            return convertView;
        }

    }

    private void showPhotoDialog() {


        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText(R.string.attach_take_pic);
        tv_paizhao.setOnClickListener(new OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!CommonUtils.isSdcardExist()) {
                    Toast.makeText(MomentsPublishActivity.this, R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
                    return;
                }

                cameraFile = new File(new HTPathUtils(null, MomentsPublishActivity.this).getImagePath() + "/" + HTApp.getInstance().getUsername()
                        + System.currentTimeMillis() + ".png");
                //   cameraFile.getParentFile().mkdirs();
                startActivityForResult(
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                        REQUEST_CODE_CAMERA);

                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText(R.string.image_manager);
        tv_xiangce.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9 - pathList.size())
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(MomentsPublishActivity.this, REQUEST_CODE_LOCAL);
                dlg.cancel();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists()) {
                    pathList.add(cameraFile.getAbsolutePath());
                    adapter.notifyDataSetChanged();

                }


            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (list != null) {
                        pathList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void checkDialog(final int position) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("看大图");
        tv_paizhao.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MomentsPublishActivity.this, BigImageActivity.class);
                intent.putExtra("images", pathList.toArray(new String[pathList.size()]));
                intent.putExtra("page", position);
                intent.putExtra("isNetUrl", false);//表示非网络图片,而是本地图片
                startActivity(intent);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText(R.string.delete);
        tv_xiangce.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pathList.remove(position);
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
                coordinate = location.getLatitude() + "," + location.getLongitude();
                tv_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        tv_location.setText("所在位置");
                        tv_cancel.setVisibility(View.GONE);
                        mylocation = "0";
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
        option.setScanSpan(span);//
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


//    p void onDestroy() {
//        super.onDestroy();
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//    }

    /**
     * 压缩多图
     *
     * @param pathList 传入的为图片原始路径
     */
    private void compressMore(final List<String> pathList) {
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final ArrayList<String> newList = new ArrayList<>();//压缩后的图片路径
        final Handler handler = new Handler();
        class Task implements Runnable {
            String path;

            Task(String path) {
                this.path = path;
            }

            @Override
            public void run() {
                Luban.get(MomentsPublishActivity.this)
                        .load(new File(path))                     //传人要压缩的图片
                        .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {
                                //  AppManager.I().currentActivity().showDialog("加载中...");
                            }

                            @Override
                            public void onSuccess(final File file) {
                                uploadImages(file.getAbsolutePath());
                                if (!taskList.isEmpty()) {
                                    Runnable runnable = taskList.pop();
                                    handler.post(runnable);
                                } else {
                                    //全部压缩结束
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();    //启动压缩
            }
        }
        //循环遍历原始路径 添加至linklist中
        for (String path : pathList) {
            taskList.add(new Task(path));
        }
        handler.post(taskList.pop());
    }

    @Override
    public void back(View view) {
        showExitDialog();
    }

    private void showExitDialog(){
        CommonUtils.showAlertDialog(MomentsPublishActivity.this, getString(R.string.prompt), getString(R.string.cancle_edit), new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {
                MomentsPublishActivity.this.finish();
            }

            @Override
            public void onCancleClock() {

            }
        });
    }
}
