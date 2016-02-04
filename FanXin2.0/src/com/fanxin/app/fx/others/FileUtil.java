package com.fanxin.app.fx.others;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

 
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private String local_image_path;
 
    public FileUtil(Context context,String local_image_path) {
        this.local_image_path = local_image_path;
    }
 
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片到制定路径
     * 
     * @param filepath
     * @param bitmap
     */
    public void saveBitmap(String filename, Bitmap bitmap) {
        if (!isExternalStorageWritable()) {
            Log.i(TAG, "SD卡不可用，保存失败");
            return;
        }

        if (bitmap == null) {
            return;
        }
     
        try {
            
            File file = new File(local_image_path,filename);
            FileOutputStream outputstream = new FileOutputStream(file);
            if((filename.indexOf("png") != -1)||(filename.indexOf("PNG") != -1))  
            {  
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputstream);
            }  else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            }
            
            outputstream.flush();
            outputstream.close();
            
        } catch (FileNotFoundException e) {
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        } 
    }

    /**
     * 返回当前应用 SD 卡的绝对路径 like
     * /storage/sdcard0/Android/data/com.example.test/files
     */
    @SuppressLint("SdCardPath")
    public String getAbsolutePath() {
        File root = new File(local_image_path);
        if(!root.exists()){
            root.mkdirs();
            
        }
         
            
     return local_image_path;
         

    }

    @SuppressLint("SdCardPath")
    public boolean isBitmapExists(String filename) {
        File dir =new File(local_image_path);
         if(!dir.exists()){
             dir.mkdirs();
            }
                //context.getExternalFilesDir(null);
        File file = new File(dir, filename);

        return file.exists();
    }

  
}
