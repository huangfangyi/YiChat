package com.htmessage.fanxinht.widget.zxing.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by HDL on 2017/6/27.
 */

public class SelectAlbumUtils {

    /**
     * 获取选择图片之后的路径
     *
     * @param context 上下文对象
     * @param data    返回的结果
     * @return
     */
    public static String getPicPath(Context context, Intent data) {
        String picPath = "";
        if (Build.VERSION.SDK_INT >= 19) {
            picPath = getPicOnKitKatAfter(context, data);
        } else {
            picPath = getPicOnKitKatBefore(context, data);
        }
        return picPath;
    }


    /**
     * 4.4以上的处理
     *
     * @param data
     * @return
     */
    @TargetApi(19)
    private static String getPicOnKitKatAfter(Context context, Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.provider.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /**
     * 4.4一下的处理
     *
     * @param data
     * @return
     */
    private static String getPicOnKitKatBefore(Context context, Intent data) {
        Uri uri = data.getData();
        return getImagePath(context, uri, null);
    }

    private static String getImagePath(Context context, Uri uri, String selection) {
        String Path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return Path;
    }
}
