package com.htmessage.yichat.acitivity.moments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.GifSizeFilter;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.video.CaptureVideoActivity;
import com.htmessage.yichat.acitivity.moments.publish.MomentsPublishActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.sdk.manager.MmvkManger;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.io.FileOutputStream;
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

import static com.htmessage.update.uitls.WidgetUtils.getPhotoPathFromContentUri;

/**
 * Created by huangfangyi on 2017/7/11.
 * qq 84543217
 */

public class MomentsPresenter implements MomentsContract.Presenter {
    private MomentsContract.View monmentsView;

    private static final String isFriend = "1";//默认经过好友关系查询
    private static final String isFold = "1";//默认返回
    private static final String category = "0";//默认没有经纬度0
    private List<JSONObject> data = new ArrayList<>();
     private File cameraFile;
    private String backgroudMoment = "";
    private String cacheKeyBg;
    private String cacheKeyTime;
    private String cacheKey;

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_LOCAL = 1;
    private static final int REQUEST_CODE_PUBLISH = 2;
    private static final int REQUEST_CODE_CAMERA_FOR_BG = 3;
    private static final int REQUEST_CODE_LOCAL_FOR_BG = 4;
    private static final int REQUEST_CODE_VIDEO = 5;
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(monmentsView==null){
                return;
            }
            switch (msg.what){
                case 1000:
                    //点赞或者取消点赞成功
                    JSONArray praises = (JSONArray) msg.obj;
                    int positionPraises=msg.arg1;
                    monmentsView.updateGoodView(positionPraises, praises);
                    break;

                case 1002:

                    break;
                case 1003:
                    //删除评论成功或者//评论成功
                    JSONArray comments = (JSONArray) msg.obj;
                    int positonComments=msg.arg1;

                    monmentsView.updateCommentView(positonComments, comments);



                    break;
                case 1004:
                    //删除动态成功
                    int position=msg.arg1;
                    data.remove(position);
                    monmentsView.refreshListView(null);
                    break;

                case 1005:
                    //接口返回错误
                    CommonUtils.cencelDialog();

                    int resId=msg.arg1;
                    Toast.makeText(monmentsView.getBaseContext(),resId,Toast.LENGTH_SHORT).show();
                  //  monmentsView.onRefreshComplete();
                    break;
                case 1006:
                    //获取一个动态详情
                     //
                    int pageIndex=msg.arg1;
                    JSONObject data1= (JSONObject) msg.obj;
                    JSONArray jsonArray = data1.getJSONArray("list");

                    backgroudMoment = data1.getString("background");
                    List<JSONObject> list = JSONArray.parseArray(jsonArray.toJSONString(), JSONObject.class);
                    if (pageIndex == 1) {
                        data.clear();

                    }
                    data.addAll(list);
                    monmentsView.refreshListView(null);
                    monmentsView.onRefreshComplete();

                    break;
                case 1007:
                    //更新背景图
                    String url= (String) msg.obj;
                    CommonUtils.cencelDialog();
                    monmentsView.showBackground(url);
                    backgroudMoment = url;
                    MmvkManger.getIntance().putString(cacheKeyBg, url);


                    break;


            }
        }
    };


    public MomentsPresenter(MomentsContract.View view) {
        monmentsView = view;
        monmentsView.setPresenter(this);
         cacheKey = "moments" + HTApp.getInstance().getUsername();
        cacheKeyBg = HTApp.getInstance().getUsername() + "momentBg";
        cacheKeyTime = HTApp.getInstance().getUsername() + "time";
    }

    @Override
    public void start() {

    }


    @Override
    public List<JSONObject> getData() {
        JSONArray jsonArray = MmvkManger.getIntance().getJSONArray(cacheKey);
        if (jsonArray != null) {
            List<JSONObject> list = JSONArray.parseArray(jsonArray.toJSONString(), JSONObject.class);
            data.addAll(list);
        }
        return data;
    }

    public String getBackgroudMoment() {
        String backgroud = MmvkManger.getIntance().getAsString(cacheKeyBg);
        if (!TextUtils.isEmpty(backgroud)) {
            backgroudMoment = backgroud;
        }
        return backgroudMoment;
    }



    @Override
    public void loadeData(final int pageIndex) {
//        if (data.size() < 20) {
//            pageIndex = 1;
//        }


        JSONObject body=new JSONObject();
        body.put("pageNo", pageIndex);
        body.put("pageSize", 10);

        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_friend_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONObject data=jsonObject.getJSONObject("data");
                    Message message=handler.obtainMessage();
                    message.what=1006;
                    message.obj=data;
                    message.arg1=pageIndex;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1005;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1005;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });



//
//
//        final Context context = monmentsView.getBaseActivity();
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("isFriend", isFriend));
//        params.add(new Param("category", category));
//        params.add(new Param("currentPage", pageIndex + ""));
//        params.add(new Param("pageSize", 20 + ""));
//        params.add(new Param("isFold", isFold));
//        final int finalPageIndex = pageIndex;
//        new OkHttpUtils(context).post(params, HTConstant.URL_SOCIAL, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getInteger("code");
//                switch (code) {
//                    case 1:
//
//                        break;
//                    case -1:
//                        CommonUtils.showToastShort(context, R.string.just_nothing);
//                        break;
//                }
//                monmentsView.onRefreshComplete();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                monmentsView.onRefreshComplete();
//                CommonUtils.showToastShort(context, monmentsView.getBaseContext().getResources().getString(R.string.request_failed_msg) + errorMsg);
//            }
//        });

    }

    @Override
    public void setGood(final int position, final String aid) {


        JSONObject body=new JSONObject();
        body.put("trendId",aid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_praise, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONArray data=jsonObject.getJSONArray("data");
                    Message message=handler.obtainMessage();
                    message.what=1000;
                    message.obj=data;
                    message.arg1=position;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1005;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1005;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });








//
//
//        // 更新后台
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("tid", aid));
//        new OkHttpUtils(monmentsView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GOOD, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONObject tempData = data.get(position);
//
//                        String userId = tempData.getString("userId");
//                        String imagestr = tempData.getString("imagestr");
//
//                        String imageUrl = "";
//                        if (!TextUtils.isEmpty(imagestr)) {
//                            String[] images = imagestr.split(",");
//                            if (images != null) {
//                                imageUrl = images[0];
//                            }
//                        }
//                        JSONArray praises = jsonObject.getJSONArray("praises");
//                        monmentsView.updateGoodView(position, praises);
//                         break;
//                    case -1:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.praise_failed);
//                        break;
//                    default:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.praise_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.praise_failed);
//            }
//        });
    }

    @Override
    public void comment(final int position, final String aid, final String content) {

        JSONObject body=new JSONObject();
        body.put("content",content);
        body.put("trendId",aid);

        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_comment, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONArray data=jsonObject.getJSONArray("data");
                    Message message=handler.obtainMessage();
                    message.what=1003;
                    message.arg1=position;
                    message.obj=data;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1005;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1005;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });



//
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("tid", aid));
//        params.add(new Param("content", content));
//        new OkHttpUtils(monmentsView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_COMMENT, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray comments = jsonObject.getJSONArray("comments");
//                        monmentsView.updateCommentView(position, comments);
//                        JSONObject tempData = data.get(position);
//                        String userId = tempData.getString("userId");
//                        String imagestr = tempData.getString("imagestr");
//                        String imageUrl = "";
//                        if (!TextUtils.isEmpty(imagestr)) {
//                            String[] images = imagestr.split(",");
//                            if (images != null) {
//                                imageUrl = images[0];
//                            }
//                        }
//                         break;
//                    case -1:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.service_not_response);
//                        break;
//                    default:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.service_not_response);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.service_not_response);
//            }
//        });
    }

    @Override
    public void cancelGood(final int position, String aid) {



        JSONObject body=new JSONObject();
        body.put("trendId",aid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_praise_cancel, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONArray data=jsonObject.getJSONArray("data");
                    Message message=handler.obtainMessage();
                    message.what=1000;
                    message.obj=data;
                    message.arg1=position;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1005;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1005;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });








//
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("pid", gid));
//        new OkHttpUtils(monmentsView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GOOD_CANCEL, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray praises = jsonObject.getJSONArray("praises");
//                        monmentsView.updateGoodView(position, praises);
//                        break;
//
//                    default:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.praise_cancle_fail);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.praise_cancle_fail);
//            }
//        });
    }

    @Override
    public void deleteComment(final int position, String cid) {

        JSONObject body=new JSONObject();
        body.put("commentId",cid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_comment_delete, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONArray data=jsonObject.getJSONArray("data");
                    Message message=handler.obtainMessage();
                    message.what=1003;
                    message.obj=data;
                    message.arg1=position;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1005;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1005;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });





//        List<Param> params = new ArrayList<>();
//        params.add(new Param("cid", cid));
//        new OkHttpUtils(monmentsView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_DELETE_COMMENT, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray comments = jsonObject.getJSONArray("comments");
//                        monmentsView.updateCommentView(position, comments);
//                        break;
//                    default:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.delete_comment_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.delete_comment_failed);
//            }
//        });
    }

    @Override
    public void onBarRightViewClicked() {
     monmentsView.showPicDialog(1, monmentsView.getBaseContext().getString(R.string.push_moment));
    //    monmentsView.showBackGroundPicDialog(1, monmentsView.getBaseContext().getString(R.string.push_moment));
    }

    @Override
    public void deleteItem(final int position, String aid) {


        JSONObject body=new JSONObject();
        body.put("trendId",aid);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_delete, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                     Message message=handler.obtainMessage();
                    message.what=1004;
                    message.arg1=position;
                    message.sendToTarget();
                }else {

                    Message message=handler.obtainMessage();
                    message.what=1005;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1005;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });



//        final List<Param> params = new ArrayList<>();
//        params.add(new Param("tid", aid));
//        new OkHttpUtils(monmentsView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_DELETE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        data.remove(position);
//                        monmentsView.refreshListView(null);
//                        break;
//
//                    default:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.delete_dynamic_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.delete_dynamic_failed);
//            }
//        });
    }


    @Override
    public void startToPhoto(int type) {
        Activity activity = monmentsView.getBaseActivity();
        if (!CommonUtils.isSdcardExist()) {
            CommonUtils.showToastShort(monmentsView.getBaseContext(), "内存卡不存在");
            return;
        }
        if (!CommonUtils.checkPermission(activity, Manifest.permission.CAMERA)) {
            CommonUtils.showToastShort(activity, R.string.open_camera_permission);
            return;
        }
        String filePath = HTApp.getInstance().getImageDirFilePath();
        cameraFile = new File(filePath + DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png");
        Uri imgUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            //兼容android7.0 使用共享文件的形式
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            imgUri = activity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            imgUri = Uri.fromFile(cameraFile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        if (type == 1) {
            activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } else {
            activity.startActivityForResult(intent, REQUEST_CODE_CAMERA_FOR_BG);
        }
    }

    @Override
    public void startToAlbum(int type) {
        Activity activity = monmentsView.getBaseActivity();
        if (!CommonUtils.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || !CommonUtils.checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            CommonUtils.showToastShort(activity, R.string.open_sd_permission);
            return;
        }
        if (type == 1) {
            Matisse.from(monmentsView.getBaseActivity())
                    .choose(MimeType.ofAll())
                    .countable(true)
                    .maxSelectable(9)
                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize(monmentsView.getBaseActivity().getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .forResult(REQUEST_CODE_LOCAL);
//
//            PhotoPicker.builder()
//                    .setPhotoCount(9)
//                    .setShowCamera(true)
//                    .setShowGif(true)
//                    .setPreviewEnabled(false)
//                    .start(activity, REQUEST_CODE_LOCAL);
        } else {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowCamera(false)
                    .setShowGif(false)
                    .setPreviewEnabled(false)
                    .start(activity, REQUEST_CODE_LOCAL_FOR_BG);
        }
    }

    @Override
    public void startToVideo(int type) {
        Intent intent = new Intent(monmentsView.getBaseActivity(), CaptureVideoActivity.class);
        String dirPath = HTApp.getInstance().getVideoPath();
        String filePath = dirPath + "/" + DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".mp4";
        intent.putExtra("EXTRA_DATA_FILE_NAME", filePath);
        intent.putExtra(HTConstant.SMALL_FROM_MOMENT, true);
        monmentsView.getBaseActivity().startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists()) {
                    List<String> list = new ArrayList<>();
                    list.add(cameraFile.getAbsolutePath());
                    startToPublishActivity((ArrayList<String>) list);
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (intent != null) {

                    // ArrayList<String> list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    List<Uri> list = Matisse.obtainResult(intent);
                    ArrayList<String> images=new ArrayList<>();
                    final List<String> videos=new ArrayList<>();
                    for(Uri uri:list){
                        if(uri.toString().contains("images")){
                            images.add( getPhotoPathFromContentUri(monmentsView.getBaseContext(),uri));

                        }else if(uri.toString().contains("video")){

                            videos.add( getPhotoPathFromContentUri(monmentsView.getBaseContext(),uri));

                        }
                    }
                    if(images.size()!=0){
                        startToPublishActivity(images);
                    }else if(videos.size()!=0) {
                        String videoPath = videos.get(0);
                        String filePath = HTApp.getInstance().getImageDirFilePath();
                        File file = new File(filePath, "th_video_" + DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png");
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                            ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            startToPublishActivity(videoPath, file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }






//
//                    ArrayList<String> list = intent.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
//                    if (list != null) {
//                        startToPublishActivity(list);
//                    }
                }
            } else if (requestCode == REQUEST_CODE_PUBLISH) { // send local image
                if (intent != null) {
                    String jsonStr = intent.getStringExtra("data");
                    if (jsonStr != null) {
                        data.add(0, JSONObject.parseObject(jsonStr));
                        monmentsView.refreshListView(null);
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA_FOR_BG) { //更新志工圈背景
                if (cameraFile != null && cameraFile.exists()) {
                    String path = cameraFile.getAbsolutePath();
                    updateMomentBackGround(path);
                }
            } else if (requestCode == REQUEST_CODE_LOCAL_FOR_BG) { //相册选择背景 更新志工圈背景
                if (intent != null) {
                    ArrayList<String> list = intent.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (list != null && list.size() != 0) {
                        String path = list.get(0);
                        updateMomentBackGround(path);
                    }
                }
            } else if (requestCode == REQUEST_CODE_VIDEO) {//选择发布小视频
                if (intent != null) {
                    String videoPath = intent.getStringExtra("path");
                    String filePath = HTApp.getInstance().getImageDirFilePath();
                    File file = new File(filePath, "th_video_" + DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png");
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                        ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                        startToPublishActivity(videoPath, file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onBarRightViewLongClicked() {


        Intent intent = new Intent();
        intent.setClass(monmentsView.getBaseContext(), MomentsPublishActivity.class);
        intent.putStringArrayListExtra("pathList", new ArrayList<>());
        monmentsView.getBaseActivity().startActivityForResult(intent, REQUEST_CODE_PUBLISH);

    }

    /**
     * 发布图片
     *
     * @param urls
     */
    private void startToPublishActivity(ArrayList<String> urls) {
        Intent intent = new Intent();
        intent.setClass(monmentsView.getBaseContext(), MomentsPublishActivity.class);
        intent.putStringArrayListExtra("pathList", urls);
        monmentsView.getBaseActivity().startActivityForResult(intent, REQUEST_CODE_PUBLISH);
    }

    /**
     * 发布小视频
     *
     * @param videoPath
     * @param thumPath
     */
    private void startToPublishActivity(String videoPath, String thumPath) {
        Intent intent = new Intent();
        intent.setClass(monmentsView.getBaseContext(), MomentsPublishActivity.class);
        intent.putExtra("pathList", videoPath);
        intent.putExtra("thumPath", thumPath);
        intent.putExtra("isVideo", true);
        monmentsView.getBaseActivity().startActivityForResult(intent, REQUEST_CODE_PUBLISH);
    }




    @SuppressLint("CheckResult")
    private void updateMomentBackGround(final String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        CommonUtils.showDialogNumal(monmentsView.getBaseContext(), monmentsView.getBaseContext().getString(R.string.are_uploading));
        final String fileName = value.substring(value.lastIndexOf("/") + 1);
        Log.d("updateMomentBackGround",fileName);

        List<String> pathList = new ArrayList<>();
        pathList.add(value);

        Flowable.just(pathList)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull List<String> list) throws Exception {
                        // 同步方法直接返回压缩后的文件
                        return Luban.with(monmentsView.getBaseContext()).load(list).get();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        Log.d("updateMomentBackGround",files.size()+"");
                        for (File file : files) {
                            uploadImageback(fileName, file.getAbsolutePath());
                        }
                    }
                });


//
//        Luban.get(monmentsView.getBaseContext()).load(new File(value)).putGear(Luban.THIRD_GEAR).setCompressListener(new OnCompressListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onSuccess(final File file) {
//                monmentsView.getBaseActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                monmentsView.getBaseActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                    }
//                });
//            }
//        }).launch();
    }

    private void uploadImageback(final String fileName, String value) {
        new UploadFileUtils(monmentsView.getBaseContext(), fileName, value).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String url = HTConstant.baseImgUrl + fileName;
                monmentsView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadBackground(url);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                monmentsView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                    }
                });
            }
        });
    }


    private void uploadBackground(final String url) {


        JSONObject body = new JSONObject();
        body.put("img", url);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_background, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                     Message message = handler.obtainMessage();
                    message.what = 1007;
                     message.obj = url;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1005;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


//        List<Param> params = new ArrayList<Param>();
//        params.add(new Param("background", url));
//        new OkHttpUtils(monmentsView.getBaseContext()).post(params, HTConstant.URL_UPLOAD_MOMENT_BACKGROUND, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//
//                        break;
//                    default:
//                        CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.update_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(monmentsView.getBaseContext(), R.string.update_failed);
//                CommonUtils.cencelDialog();
//            }
//        });
    }
}
