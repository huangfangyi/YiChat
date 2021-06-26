package com.htmessage.sdk.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/2/14.
 * qq 84543217
 */

public class HTMessageImageBody  extends HTMessageBody{
    private String size;
    private String localPath;
    private String fileName;
    private String remotePath;
    private String thumbnailRemotePath;
    public HTMessageImageBody(){

    }
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
        bodyJson.put("localPath",localPath);
    }

    public void setSize(String size) {
        this.size = size;
        bodyJson.put("size",size);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        bodyJson.put("fileName",fileName);
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        bodyJson.put("remotePath",remotePath);
    }

    public void setThumbnailRemotePath(String thumbnailRemotePath) {
        this.thumbnailRemotePath = thumbnailRemotePath;
        bodyJson.put("thumbnailRemotePath",thumbnailRemotePath);
    }

    public String getSize() {
        if(size==null){
            size=bodyJson.getString("size");
        }
        return size;
    }

    public String getLocalPath() {
        if(localPath==null){
            localPath=bodyJson.getString("localPath");
        }

        return localPath;
    }

    public String getFileName() {
        if(fileName==null){
            fileName=bodyJson.getString("fileName");
        }
        return fileName;
    }

    public String getRemotePath() {
        if(remotePath==null){
            remotePath=bodyJson.getString("remotePath");
        }
        return remotePath;
    }

    public String getThumbnailRemotePath() {
        if(thumbnailRemotePath==null){
            thumbnailRemotePath=bodyJson.getString("thumbnailRemotePath");
        }
        return thumbnailRemotePath;
    }

    public HTMessageImageBody(JSONObject bodyJson) {
        super(bodyJson);
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
//
//        if (bodyJson.containsKey("thumbnailRemotePath")) {
//            thumbnailRemotePath = bodyJson.getString("thumbnailRemotePath");
//        }
    }

    public HTMessageImageBody(String body) {
        super(body);
        if (bodyJson.containsKey("size")) {
            size = bodyJson.getString("size");

        }
        if (bodyJson.containsKey("localPath")) {
            localPath = bodyJson.getString("localPath");

        }
        if (bodyJson.containsKey("fileName")) {
            fileName = bodyJson.getString("fileName");

        }
        if (bodyJson.containsKey("remotePath")) {
            remotePath = bodyJson.getString("remotePath");

        }

        if (bodyJson.containsKey("thumbnailRemotePath")) {
            thumbnailRemotePath = bodyJson.getString("thumbnailRemotePath");
        }
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
