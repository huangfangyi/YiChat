package com.htmessage.yichat.acitivity.showbigimage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.BitmapUtils;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.ChatFileManager;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.yichat.utils.HTPathUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.PhotoViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JzvdStd;
import uk.co.senab.photoview.PhotoView;

public class ShowBigImageFragment extends Fragment {

    private List<HTMessage> htMessageList = new ArrayList<>();
    private PhotoViewPager photoViewPager;
    private int indexPage = 0;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    //  CommonUtils.cencelDialog();
//                    String localPath=msg.getData().getString("localPath");
//                    JzvdStd jzvdStd= (JzvdStd) msg.obj;
//                    jzvdStd.setUp(localPath, "");
//
//                    jzvdStd.startVideo();
                    break;
                case 1001:
//                    CommonUtils.cencelDialog();
//                    Toast.makeText(getActivity(),"下载失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show_big_image, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ChatFileManager.get().getImageOrVideoMessage();这个指针对应数据会变化，初始化加载需要采用复制的方式
        List<HTMessage> htMessageListTemp = ChatFileManager.get().getImageOrVideoMessage();
        for(HTMessage htMessage:htMessageListTemp){
            htMessageList.add(htMessage);
        }
        indexPage = getActivity().getIntent().getIntExtra("indexPage", 0);
        if (htMessageList == null||htMessageList.size()==0) {
            getActivity().finish();
            return;
        }


        initView();
        initData();
    }


    private void initData() {

    }

    private void initView() {
        photoViewPager = getView().findViewById(R.id.viewpager_image);
        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        photoViewPager.setAdapter(new MyAdapter());
        photoViewPager.setCurrentItem(indexPage);
        ImageView iv_downloade = getView().findViewById(R.id.iv_download);
        iv_downloade.setImageResource(R.drawable.icon_download);
        iv_downloade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savaFile(htMessageList.get(photoViewPager.getCurrentItem()));
            }
        });
    }

    private class MyAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return htMessageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            HTMessage htMessage = htMessageList.get(position);
            if (htMessage.getType() == HTMessage.Type.IMAGE) {
                HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
                //创建一个photoview
                PhotoView photoView = new PhotoView(getActivity());
                String localPath = ChatFileManager.get().getLocalPath(htMessage.getMsgId(), htMessage.getType());
                if (localPath != null) {
                    //看下是否有本地路劲
                    photoView.setImageURI(Uri.parse(localPath));
                } else {
                    String localPath2=htMessageImageBody.getLocalPath();
                    if(localPath2!=null){
                        photoView.setImageURI(Uri.parse(localPath2));
                    }else {
                        //如果没有
                        CommonUtils.loadImage(getActivity(), htMessageImageBody.getRemotePath(), photoView);
                        downLoadFile(htMessage);
                    }

                }
                container.addView(photoView);

                return photoView;
            } else if (htMessage.getType() == HTMessage.Type.VIDEO) {
                //对于视频消息，视频截图不存文件，只存bitmap
                HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) htMessage.getBody();

                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_video, container, false);
                JzvdStd myVideoView = view.findViewById(R.id.jzcdStd);
                myVideoView.startButton.setVisibility(View.VISIBLE);
                Bitmap bitmap = ChatFileManager.get().getMsgImageBitmap(htMessage.getMsgId());
                if (bitmap != null) {
                    myVideoView.thumbImageView.setImageBitmap(bitmap);
                }

                myVideoView.backButton.setVisibility(View.GONE);
                myVideoView.tinyBackImageView.setVisibility(View.GONE);
                myVideoView.batteryLevel.setVisibility(View.GONE);
                myVideoView.batteryTimeLayout.setVisibility(View.GONE);

                myVideoView.startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myVideoView.startVideo();
                    }
                });

                String localPath = ChatFileManager.get().getLocalPath(htMessage.getMsgId(), htMessage.getType());
                if (localPath != null) {
                    Log.d("localPath---", localPath);
                    myVideoView.setUp(localPath, "");


                } else {

                    String localPath2=htMessageVideoBody.getLocalPath();
                    if(localPath2!=null){
                        myVideoView.setUp(localPath2, "");

                    }else {
                        myVideoView.setUp(htMessageVideoBody.getRemotePath(), "");
                        downLoadFile(htMessage);
                    }



                }


                container.addView(view);
                return view;
            }


//            String image = images[position];
//            final PhotoView photoView = new PhotoView(BigImageActivity.this);
//            if (!isNetUrl) {
//                CommonUtils.loadImage(BigImageActivity.this, image, photoView);
//            }
//            if (isNetUrl && !image.contains("http")) {
//                image = HTConstant.baseImgUrl + image;
//            }
//            CommonUtils.loadImage(BigImageActivity.this, image, photoView);
//            photoView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    showDialog(photoView);
//                    return true;
//                }
//            });
//            container.addView(photoView);
            return new ImageView(getActivity());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//            JzvdStd myVideoView = ((View)object).findViewById(R.id.jzcdStd);
//            if(myVideoView!=null){
//                myVideoView.re
//            }
        }
    }

    public static ContentValues getImageContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "image/jpeg");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("orientation", Integer.valueOf(0));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }


    private void savaFile(HTMessage htMessage) {
        new HTAlertDialog(getActivity(), null, new String[]{"保存"}).init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        String localPath = ChatFileManager.get().getLocalPath(htMessage.getMsgId(), htMessage.getType());
                        if (localPath != null) {
                            if (htMessage.getType() == HTMessage.Type.IMAGE) {
                                //通知图库更新

                                    Bitmap bitmap= BitmapUtils.getDiskBitmap(localPath);
                                  File file = BitmapUtils.saveImgToDisk(System.currentTimeMillis()+HTApp.getInstance().getUsername()+".png",bitmap);
                                    Uri uri=null;
                                    if(file==null){
                                        uri = Uri.fromFile(new File(localPath));
                                        try {
                                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), localPath, System.currentTimeMillis() + "", null);
                                        } catch (Exception e) {
                                        }
                                    }else {
                                        uri = Uri.fromFile(file);
                                        try {
                                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getPath(), System.currentTimeMillis() + "", null);
                                        } catch (Exception e) {
                                        }
                                    }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本

                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                } else {

                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                                }

                            } else {
//                                MediaScannerConnection mMediaScanner = new MediaScannerConnection(getActivity(), null);
//                                mMediaScanner.connect();
//                                if (mMediaScanner !=null && mMediaScanner.isConnected()) {
//                                    mMediaScanner.scanFile(localPath,"video/mp4");
//                                }

                                insertIntoMediaStore(getActivity(),true,new File(localPath),System.currentTimeMillis());
                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
                                    Uri uri = Uri.parse(localPath);
                                   // LoggerUtils.e("localPath----"+localPath);
                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                } else {
                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                                }
//
//                                ContentResolver localContentResolver = getActivity().getContentResolver();
//                                ContentValues localContentValues = getVideoContentValues(getActivity(), new File(localPath), System.currentTimeMillis());
//                                Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
//                                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
                            }
                            Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
//
                        } else {
                            Toast.makeText(getActivity(), "待下载完成再试", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

    }

    public static void insertIntoMediaStore(Context context, boolean isVideo, File saveFile, long createTime) {
        ContentResolver mContentResolver = context.getContentResolver();
        if (createTime == 0)
            createTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        //值一样，但是还是用常量区分对待
        values.put(isVideo ? MediaStore.Video.VideoColumns.DATE_TAKEN
                : MediaStore.Images.ImageColumns.DATE_TAKEN, createTime);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        if (!isVideo)
            values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, isVideo ? getVideoMimeType(saveFile.getAbsolutePath()) : "image/jpeg");
        //插入
        mContentResolver.insert(isVideo
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private static String getVideoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("mp4") || lowerPath.endsWith("mpeg4")) {
            return "video/mp4";
        } else if (lowerPath.endsWith("3gp")) {
            return "video/3gp";
        }
        return "video/mp4";
    }

    private void downLoadFile(HTMessage htMessage) {
        String fileName;
        String remotePath;
        String tempPath ;
        HTPathUtils htPathUtils = new HTPathUtils(htMessage.getUsername(), getActivity());

        HTMessageBody htMessageBody;
        if (htMessage.getType() == HTMessage.Type.IMAGE) {
            htMessageBody = (HTMessageImageBody) htMessage.getBody();
            fileName = ((HTMessageImageBody) htMessageBody).getFileName();
            remotePath = ((HTMessageImageBody) htMessageBody).getRemotePath();
            tempPath=htPathUtils.getImagePath() + "/" + fileName;;
        } else {
            htMessageBody = (HTMessageVideoBody) htMessage.getBody();
            fileName = ((HTMessageVideoBody) htMessageBody).getFileName();
            remotePath = ((HTMessageVideoBody) htMessageBody).getRemotePath();
            tempPath=htPathUtils.getVideoPath() + "/" + fileName;;
        }



        ApiUtis.getInstance().loadFile(remotePath, tempPath, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ChatFileManager.get().setLocalPath(htMessage.getMsgId(), tempPath, htMessage.getType());
//                            Bundle bundle=new Bundle();
//                            bundle.putString("localPath",tempPath);
//                            Message message=handler.obtainMessage();
//                            message.obj=myVideoView;
//                            message.what=1000;
//                            message.setData(bundle);
//                            message.sendToTarget();
            }

            @Override
            public void onFailure(int errorCode) {
//
//                            Message message=handler.obtainMessage();
//                             message.what=1001;
//                            message.sendToTarget();
            }
        });

    }


    /**
     * 保存图片
     *
     * @param path
     */
    private void showSaveImageDialog(final String path) {
        HTAlertDialog dialog = new HTAlertDialog(getActivity(), null, new String[]{getString(R.string.save)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        saveImage(path);
                        break;
                }
            }
        });
    }


    @NonNull
    private String getSaveImagePath() {
        String dirFilePath = HTApp.getInstance().getImageDirFilePath();
        String fileName = DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png";
        return dirFilePath + "/" + fileName;
    }

    /**
     * 保存图片
     *
     * @param filePath
     */
    private void saveImage(String filePath) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showToast(R.string.need_sdcard_permission);
            return;
        }
        CommonUtils.showDialogNumal(getActivity(), getString(R.string.saving));
//        if (TextUtils.isEmpty(filePath)) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    showToast(R.string.saving_failed);
//                }
//            }, 500);
//            return;
//        }
        String savePath = getSaveImagePath();
        if (filePath.contains("http://") || filePath.startsWith("http") || filePath.contains("https://")) {
            saveNetImage(filePath, savePath);
        } else {
            saveLocalImage(filePath, 0);
        }
    }

    /**
     * 保存图片
     *
     * @param filePath 网络地址
     * @param savePath 保存的地址
     */
    private void saveNetImage(String filePath, final String savePath) {
        if (TextUtils.isEmpty(savePath)) {
            showToast(R.string.saving_failed);
            return;
        }
        new OkHttpUtils(getActivity()).loadFile(filePath, savePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {
                saveLocalImage(savePath, 1);
            }

            @Override
            public void onFailure(String message) {
                showToast(R.string.saving_failed);
            }
        });
    }

    /**
     * 吐司
     *
     * @param resId
     */
    private void showToast(final int resId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(getActivity(), resId);
            }
        });
    }

    /**
     * 保存图片
     *
     * @param filePath
     */
    private void saveLocalImage(final String filePath, final int type) {
        new Thread() {
            @Override
            public void run() {
                boolean success = CommonUtils.saveImageToAlubm(getActivity(), filePath);
                if (success) {
                    if (type == 1) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    //    showToast(R.string.saving_successful);
                } else {
                    ///  showToast(R.string.saving_failed);
                }
            }
        }.start();
    }


}
