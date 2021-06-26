package com.htmessage.yichat.acitivity.moments.publish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.video.VideoPlayActivity;
import com.htmessage.yichat.acitivity.moments.BigImageActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.HTPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.Luban;


public class MomentsPublishActivity extends BaseActivity {
    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_LOCAL = 1;


    private File cameraFile;
    private GridView gridview;
     private List<String> pathList = new ArrayList<>();
    private ImageAdapter adapter;
    // 显示位置的TextView
     // 发送按钮
    private Button btn_send;
    private int temp=0;
    // 文本输入
    private EditText et_content;
     private List<String> nameList = new ArrayList<>();
    private boolean isShowed = false;
     private SimpleDraweeView sdv_image;
    private RelativeLayout rl_videothum;
    private boolean isVideo = false;
    private String videoPath,thumPath ="";

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    temp++;
                    if (temp == pathList.size()) {
                        String content = et_content.getText().toString().trim();
                        sendSocial(content,videoPath);
                    }
                    break;
                case 1:
                    temp = 0;
                    CommonUtils.cencelDialog();
                    CommonUtils.showToastShort(MomentsPublishActivity.this, R.string.update_failed);
                    break;

                case 1000:

                    JSONObject data = (JSONObject) msg.obj;
                    Intent intent = new Intent();
                        intent.putExtra("data", data.toJSONString());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                     break;

                case 1001:
                    int resId=msg.arg1;
                    CommonUtils.showToastShort(MomentsPublishActivity.this,resId);

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_publish_moments);
        super.onCreate(arg0);
        getData();
        initView();
        initData();
        setListener();


    }
    private void setListener() {

        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    CommonUtils.showToastShort(getApplicationContext(), R.string.input_text);
                    return;

                }
//				if (lists.size() == 0) {
//                CommonUtils.showToastShort(getApplicationContext(), "请选择图片....");
//					return;
//				}
                send(content);
            }

        });
        rl_videothum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1);
                Intent intent = new Intent(MomentsPublishActivity.this, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.VIDEO_NAME, videoName);
                intent.putExtra(VideoPlayActivity.VIDEO_PATH, videoPath);
                startActivity(intent);
            }
        });

    }

    private void initData() {
        if (isVideo){
            gridview.setVisibility(View.GONE);
            rl_videothum.setVisibility(View.VISIBLE);
            Glide.with(this).load(thumPath).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_chat_image).error(R.drawable.default_chat_image).into(sdv_image);

         }else{
            gridview.setVisibility(View.VISIBLE);
            rl_videothum.setVisibility(View.GONE);
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
        }

    }





    private void getData() {
        isVideo = this.getIntent().getBooleanExtra("isVideo",false);
        if (isVideo){
            videoPath = this.getIntent().getStringExtra("pathList");
            thumPath = this.getIntent().getStringExtra("thumPath");
        }else{
            pathList.clear();
            ArrayList<String> imagePath = this.getIntent().getStringArrayListExtra("pathList");
            pathList.addAll(imagePath);
        }

    }

    private void initView() {

        sdv_image = (SimpleDraweeView) this.findViewById(R.id.sdv_image);
        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        gridview = (GridView) this.findViewById(R.id.gridview);
        rl_videothum = (RelativeLayout) this.findViewById(R.id.rl_videothum);
     }

    // 发送
    private void send(String content) {
        CommonUtils.showDialogNumal(this,getString(R.string.publishing));

        if (isVideo){
             uploadVideo(thumPath,content,videoPath);
        }else if(pathList!=null&&!pathList.isEmpty()){
            compressMore(pathList);
        }else {
            sendSocial(content,null);
        }

    }

    private void uploadVideo(final String thumPath, final String content, final String path) {
        final String suffix = path.substring(path.lastIndexOf(".") + 1);
        String fileName=HTApp.getInstance().getUsername()+System.currentTimeMillis()+suffix;
        new UploadFileUtils(MomentsPublishActivity.this, fileName, path).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                int body = result.getStatusCode();
                if (body == 200) {
                   final String url = HTConstant.baseImgUrl + fileName;
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           sendVideoMent(url,content);

                       }
                   });
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      CommonUtils.cencelDialog();
                  }
              });
            }
        });
    }

//
//    private void uploadVideoThum(String thumPath, final String content,  final String videoUrl) {
//       final String fileName = thumPath.substring(thumPath.lastIndexOf("/") + 1);
//        new UploadFileUtils(MomentsPublishActivity.this, fileName, thumPath).asyncUploadFile(new UploadFileUtils.a() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//            }
//
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                int body = result.getStatusCode();
//                if (body == 200) {
//                    final String thumUrl = HTConstant.baseImgUrl + fileName;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            sendVideoMent(thumUrl,videoUrl,content);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonUtils.cencelDialog();
//                    }
//                });
//            }
//        });
//    }

    private void sendVideoMent(String videoUrl,String content){
        sendSocial(content,videoUrl);
    }

    private void uploadImages(String filePath,int index) {
        final String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        String fileName=HTApp.getInstance().getUsername()+index+System.currentTimeMillis()+suffix;
        nameList.add(fileName);
        new UploadFileUtils(MomentsPublishActivity.this, fileName, filePath).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                int body = result.getStatusCode();
                if (body == 200) {
                    if (handler == null) {
                        return;
                    }
                    handler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (handler == null) {
                    return;
                }
                handler.sendEmptyMessage(1);
            }
        });
    }

    private void sendSocial(final String content,String videPath) {


        JSONObject body=new JSONObject();
        String imagePath = "";
        if (nameList!=null && nameList.size()!=0){
            if ( nameList.size() > 1) {
                for (int i = 0; i < nameList.size() ; i++) {
                    imagePath += HTConstant.baseImgUrl+nameList.get(i) + ",";
                }
                //把最后一个逗号去掉
                imagePath=imagePath.substring(0,imagePath.length()-1);
                Log.d("imagePath---->",imagePath);
            } else {
                imagePath = HTConstant.baseImgUrl+nameList.get(0);
            }
            body.put("imgs",imagePath);
        }
        if (!TextUtils.isEmpty(videPath) ){
             body.put("videos",videPath);
        }
        if(!TextUtils.isEmpty(content)){
            body.put("content",content);
        }
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_publish, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }

                String code=jsonObject.getString("code");
                if("0".equals(code)){

                    JSONObject data=jsonObject.getJSONObject("data");
                    Message message=handler.obtainMessage();
                    message.what=1000;
                    message.obj=data;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.obj=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if (handler == null) {
                    return;
                }
                Message message=handler.obtainMessage();
                message.what=1001;
                message.obj=errorCode;
                message.sendToTarget();
            }
        });

















//        List<Param> params = new ArrayList<>();
//        String userId = HTApp.getInstance().getUsername();
//        params.add(new Param("userId", userId));
//        params.add(new Param("category", category));
//        params.add(new Param("coordinate", coordinate));
//        params.add(new Param("content", content));
//        params.add(new Param("location", mylocation));
//      //  params.add(new Param("restrict", String.valueOf(seeType)));
//
//        String imagePath = "";
//        if (nameList!=null && nameList.size()!=0){
//            if (nameList.size() != 1 || nameList.size() > 1) {
//                for (int i = 1; i < nameList.size() - 1; i++) {
//                    imagePath += nameList.get(i) + ",";
//                }
//                imagePath = nameList.get(0) + "," + imagePath + nameList.get(nameList.size() - 1);
//            } else {
//                imagePath = nameList.get(0);
//            }
//            params.add(new Param("imagestr", imagePath));
//        }
//       if (!TextUtils.isEmpty(videPath) ){
//           params.add(new Param("videoUrl", videPath));
//
//       }
//        new OkHttpUtils(this).post(params, HTConstant.URL_PUBLISH, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getInteger("code");
//                switch (code) {
//                    case 1:
//                        JSONObject data = jsonObject.getJSONObject("data");
//                        Intent intent = new Intent();
//                        intent.putExtra("data", data.toJSONString());
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
//                        break;
//                    default:
//                        CommonUtils.showToastShort(getApplicationContext(), getString(R.string.server_wrong) + code);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(getApplicationContext(), errorMsg);
//            }
//        });
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
                    CommonUtils.showToastShort(MomentsPublishActivity.this, R.string.sd_card_does_not_exist);
                    return;
                }

                cameraFile = new File(new HTPathUtils("images", MomentsPublishActivity.this).getImagePath() + "/" + HTApp.getInstance().getUsername()
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
        tv_paizhao.setText(R.string.look_big_image);
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
//    private class LocationChangedListener extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (IMAction.ACTION_LOCATION_HAS.equals(intent.getAction())) {
//               String lat = intent.getStringExtra("lat");
//                String lng = intent.getStringExtra("lng");
//                String str_addr = intent.getStringExtra("address");
//                if (!TextUtils.isEmpty(str_addr)) {
//                    tv_location.setText(str_addr);
//                    tv_cancel.setVisibility(View.VISIBLE);
//                    mylocation = str_addr;
//                    coordinate = lat + "," +lng;
//                    tv_cancel.setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            tv_location.setText(R.string.location_info_here);
//                            tv_cancel.setVisibility(View.GONE);
//                            mylocation = "0";
//                            isShowed = false;
//                        }
//                    });
//                }
//            } else if (IMAction.ACTION_LOCATION_FAILED.equals(intent.getAction())) {
//                if (!isShowed) {
//                    tv_location.setText(R.string.location_failed);
//                    mylocation = "0";
//                    isShowed = true;
//                }
//            }
//        }
//    }

    /**
     * 压缩多图
     *
     * @param pathList 传入的为图片原始路径
     */

    @SuppressLint("CheckResult")
    private void compressMore(final List<String> pathList) {
        Flowable.just(pathList)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override public List<File> apply(@NonNull List<String> list) throws Exception {
                        // 同步方法直接返回压缩后的文件
                        return Luban.with(MomentsPublishActivity.this).load(list).get();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        for(int i=0;i< files.size();i++){
                            File file=files.get(i);
                            uploadImages(file.getAbsolutePath(),i);
                        }
                    }
                });





//
//        final LinkedList<Runnable> taskList = new LinkedList<>();
//        final Handler handler = new Handler();
//        class Task implements Runnable {
//            String path;
//
//            Task(String path) {
//                this.path = path;
//            }
//
//            @Override
//            public void run()
//            {
//                Luban.get(MomentsPublishActivity.this)
//                        .load(new File(path))                     //传人要压缩的图片
//                        .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
//                        .setCompressListener(new OnCompressListener() { //设置回调
//                            @Override
//                            public void onStart() {
//                            }
//
//                            @Override
//                            public void onSuccess(final File file) {
//                                uploadImages(file.getAbsolutePath());
//                                if (!taskList.isEmpty()) {
//                                    Runnable runnable = taskList.pop();
//                                    handler.post(runnable);
//                                } else {
//                                    //全部压缩结束
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                            }
//                        }).launch();    //启动压缩
//            }
//        }
//        //循环遍历原始路径 添加至linklist中
//        for (String path : pathList) {
//            taskList.add(new Task(path));
//        }
//        handler.post(taskList.pop());
    }

    @Override
    public void back(View view) {
         showExitDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_BACK){
             showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitDialog(){
        CommonUtils.showAlertDialog(MomentsPublishActivity.this, getString(R.string.prompt), getString(R.string.cancle_edit), new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {
                if (isVideo){
                    isVideo = false;
                }
                MomentsPublishActivity.this.finish();
            }

            @Override
            public void onCancleClock() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        handler=null;
        super.onDestroy();
    }
}
