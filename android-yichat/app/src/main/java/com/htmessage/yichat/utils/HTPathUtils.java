package com.htmessage.yichat.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.htmessage.yichat.HTApp;
import com.htmessage.sdk.model.HTMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by huangfangyi on 2016/12/4.
 * qq 84543217
 */

public class HTPathUtils {
    private static HTPathUtils instance;
    private static File storageDir = null;
    private File voicePath = null;
    private File imagePath = null;
    private File videoPath = null;
    private File filePath;
 //    public static final String imagePathName = "/image/";
//    public static final String voicePathName = "/voice/";
//    public static final String filePathName = "/file/";
//    public static final String videoPathName = "/video/";
   private static   String pathPrefix;

    public  HTPathUtils(String chatTo, Context context) {
        String   username= HTApp.getInstance().getUsername();
        String packageName = context.getPackageName();
        pathPrefix = "/Android/data/" + packageName + "/";

        this.voicePath = generateVoicePath(username,chatTo,context);
        if(!this.voicePath.exists()) {
            this.voicePath.mkdirs();
        }
        this.imagePath = generateImagePath(username, chatTo, context);
        if(!this.imagePath.exists()) {
            this.imagePath.mkdirs();
        }

        this.videoPath = generateVideoPath(username, chatTo, context);
        if(!this.videoPath.exists()) {
            this.videoPath.mkdirs();
        }

        this.filePath = generateFiePath(username, chatTo, context);
        if(!this.filePath.exists()) {
            this.filePath.mkdirs();
        }

    }

    public File getImagePath() {
        return this.imagePath;
    }

    public File getVoicePath() {
        return this.voicePath;
    }

    public File getFilePath() {
        return this.filePath;
    }

    public File getVideoPath() {
        return this.videoPath;
    }


    private static File getStorageDir(Context context) {
        if(storageDir == null) {
            File file = Environment.getExternalStorageDirectory();
            if(file.exists()) {
                return file;
            }
            storageDir = context.getFilesDir();
        }
        return storageDir;
    }


    private static File generateImagePath(String username, String chatTo, Context context) {
        String filePath = null;

        if(username == null) {
            filePath = pathPrefix + chatTo + "/image/";
        } else {
            filePath = pathPrefix + username + "/" + chatTo + "/image/";
        }


        return new File(getStorageDir(context), filePath);
    }

    private static File generateVoicePath(String username, String chatTo, Context context) {
        String filePath = null;
        if(username == null) {
            filePath = pathPrefix + chatTo + "/voice/";
        } else {
            filePath = pathPrefix + username + "/" + chatTo + "/voice/";
        }

        return new File(getStorageDir(context), filePath);
    }

    private static File generateFiePath(String username, String chatTo, Context context) {
        String filePath = null;
        if(username == null) {
            filePath = pathPrefix + chatTo + "/file/";
        } else {
            filePath = pathPrefix + username + "/" + chatTo + "/file/";
        }

        return new File(getStorageDir(context), filePath);
    }

    private static File generateVideoPath(String username, String chatTo, Context context) {
        String filePath = null;
        if(username == null) {
            filePath = pathPrefix + chatTo + "/video/";
        } else {
            filePath = pathPrefix + username + "/" + chatTo + "/video/";
        }

        return new File(getStorageDir(context), filePath);
    }


    public void saveSendFileInDisk(final String filePath, final HTMessage.Type type, final NewFilePathCallBack newFilePathCallBack){
        final String fileName=filePath.substring(filePath.lastIndexOf("/")+1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String finalPath=null;
                if(type== HTMessage.Type.IMAGE){
                      finalPath=getImagePath().getAbsolutePath()+fileName;

                }else if(type== HTMessage.Type.VOICE){
                      finalPath=getVoicePath().getAbsolutePath()+fileName;

                }
                else if(type== HTMessage.Type.VIDEO){
                    finalPath=getVideoPath().getAbsolutePath()+fileName;

                }
                else if(type== HTMessage.Type.FILE){
                    finalPath=getFilePath().getAbsolutePath()+fileName;

                }
                Log.d("filePath--->",filePath);
                Log.d("finalPath--->",finalPath);
                copyFile(filePath,finalPath,newFilePathCallBack);


            }
        }).start();


    }

    interface NewFilePathCallBack{

        void  onSuccess(String filePath);
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath,NewFilePathCallBack newFilePathCallBack) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            newFilePathCallBack.onSuccess(newPath);

        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }






}
