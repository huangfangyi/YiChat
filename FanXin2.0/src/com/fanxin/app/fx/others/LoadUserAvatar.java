package com.fanxin.app.fx.others;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * 
 */
public class LoadUserAvatar {
    // 最大线程数
    private static final int MAX_THREAD_NUM = 5;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;
 
    // 线程池
    private ExecutorService threadPools = null;

    public LoadUserAvatar(Context context, String local_image_path) {
        bitmapCache = new BitmapCache();
        fileUtil = new FileUtil(context, local_image_path);
        threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM); 
    }
    

    @SuppressLint("HandlerLeak")
    public Bitmap loadImage(final ImageView imageView, final String imageUrl,
            final ImageDownloadedCallBack imageDownloadedCallBack) {
        final String filename = imageUrl
                .substring(imageUrl.lastIndexOf("/") + 1);
        final String filepath = fileUtil.getAbsolutePath() + filename;

        // 先从内存中拿
        Bitmap bitmap = bitmapCache.getBitmap(imageUrl);

        if (bitmap != null) {
            Log.i("aaaa", "image exists in memory");

            return bitmap;
        }

        // 从文件中找
        if (fileUtil.isBitmapExists(filename)) {
            Log.i("aaaa", "image exists in file" + filename);

            bitmap = BitmapFactory.decodeFile(filepath);
            // 先缓存到内存
            bitmapCache.putBitmap(imageUrl, bitmap);
            return bitmap;

        }

        // 内存和文件中都没有再从网络下载
        if (imageUrl != null && !imageUrl.equals("")) {
            final Handler handler = new Handler() {
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 111 && imageDownloadedCallBack != null) {
                        Bitmap bitmap = (Bitmap) msg.obj;
                        imageDownloadedCallBack.onImageDownloaded(imageView,
                                bitmap);
                    }
                }
            };

            Thread thread = new Thread() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    Log.i("aaaa", Thread.currentThread().getName()
                            + " is running");
                    InputStream inputStream = HTTPService.getInstance()
                            .getStream(imageUrl);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 5; // width，hight设为原来的十分一
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream,
                            null, options);

                    // 图片下载成功后缓存并执行回调刷新界面
                    if (bitmap != null) {

                        // 先缓存到内存
                        bitmapCache.putBitmap(imageUrl, bitmap);
                        // 缓存到文件系统
                        fileUtil.saveBitmap(filename, bitmap);

                        Message msg = new Message();
                        msg.what = 111;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);

                    }
                }
            };

            threadPools.execute(thread);
        }

        return null;
    }

 
    /**
     * 图片下载完成回调接口
     * 
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);
    }

   
 
}
