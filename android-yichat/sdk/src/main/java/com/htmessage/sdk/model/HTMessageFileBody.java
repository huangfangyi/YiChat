package com.htmessage.sdk.model;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/4/6.
 * qq 84543217
 */

public class HTMessageFileBody extends HTMessageBody {

    private String localPath;
    private String fileName;
    private String remotePath;
    private long fileSize;

    public HTMessageFileBody() {

    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
        bodyJson.put("localPath", localPath);
    }

    public void setSize(long fileSize) {
        this.fileSize = fileSize;
        bodyJson.put("fileSize", String.valueOf(fileSize));
    }

    public   long getSize(){
        fileSize=0;
        String fileSizeTemp=bodyJson.getString("fileSize");


        if(fileSizeTemp!=null){
            if(fileSizeTemp.contains(".")){
                fileSizeTemp=  fileSizeTemp.substring(0,fileSizeTemp.indexOf("."));
            }

            Log.d("fileSizeTemp--->",fileSizeTemp);

            try {
                fileSize=Long.parseLong(fileSizeTemp);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }

        }
        return fileSize;

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        bodyJson.put("fileName", fileName);
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        bodyJson.put("remotePath", remotePath);
    }




    public String getLocalPath() {
        if (localPath == null) {
            localPath = bodyJson.getString("localPath");
        }

        return localPath;
    }

    public String getFileName() {
        if (fileName == null) {
            fileName = bodyJson.getString("fileName");
        }
        return fileName;
    }

    public String getRemotePath() {
        if (remotePath == null) {
            remotePath = bodyJson.getString("remotePath");
        }
        return remotePath;
    }



    public HTMessageFileBody(JSONObject bodyJson) {
        super(bodyJson);
//        if (bodyJson.containsKey("fileSize")) {
//            fileSize = bodyJson.getString("size");
//
//        }
//        if (bodyJson.containsKey("localPath")) {
//            localPath = bodyJson.getString("localPath");
//
//        }
//        if (bodyJson.containsKey("fileName")) {
//            fileName = bodyJson.getString("fileName");
//
//        }
//        if (bodyJson.containsKey("remotePath")) {
//            remotePath = bodyJson.getString("remotePath");
//
//        }
//
//        if (bodyJson.containsKey("thumbnailRemotePath")) {
//            thumbnailRemotePath = bodyJson.getString("thumbnailRemotePath");
//        }
    }

    public HTMessageFileBody(String body) {
        super(body);
//        if (bodyJson.containsKey("size")) {
//            size = bodyJson.getString("size");
//
//        }
//        if (bodyJson.containsKey("localPath")) {
//            localPath = bodyJson.getString("localPath");
//
//        }
//        if (bodyJson.containsKey("fileName")) {
//            fileName = bodyJson.getString("fileName");
//
//        }
//        if (bodyJson.containsKey("remotePath")) {
//            remotePath = bodyJson.getString("remotePath");
//
//        }


    }


    @Override
    public String getLocalBody() {
//
//        if (size != null) {
//
//            bodyJson.put("size", size);
//        }
//        if (localPath != null) {
//
//            bodyJson.put("localPath", localPath);
//        }
//        if (fileName != null) {
//
//            bodyJson.put("fileName", fileName);
//        }
//        if (remotePath != null) {
//
//            bodyJson.put("remotePath", remotePath);
//        }
//        if (thumbnailRemotePath != null) {
//
//            bodyJson.put("thumbnailRemotePath", thumbnailRemotePath);
//        }

        return bodyJson.toJSONString();
    }
}