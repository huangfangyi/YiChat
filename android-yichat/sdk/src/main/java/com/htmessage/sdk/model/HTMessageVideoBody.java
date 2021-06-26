package com.htmessage.sdk.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/2/14.
 * qq 84543217
 */

public class HTMessageVideoBody extends HTMessageBody {
    public HTMessageVideoBody(){

    }

    public void setLocalPath(String localPath) {

        this.localPath = localPath;
        bodyJson.put("localPath",localPath);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        bodyJson.put("fileName",fileName);

    }

    public  void setSize(String size){
        this.size=size;
        bodyJson.put("size",size);
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        bodyJson.put("remotePath",remotePath);

    }

    public void setThumbnailRemotePath(String thumbnailRemotePath) {
        this.thumbnailRemotePath = thumbnailRemotePath;
        bodyJson.put("thumbnailRemotePath",thumbnailRemotePath);

    }

    public void setLocalPathThumbnail(String localPathThumbnail) {
        this.localPathThumbnail = localPathThumbnail;
        bodyJson.put("localPathThumbnail",localPathThumbnail);

    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
        bodyJson.put("videoDuration",videoDuration);

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

    public int getVideoDuration() {
        if(videoDuration==0){
            videoDuration=bodyJson.getInteger("videoDuration");
        }
        return videoDuration;
    }

    public String getLocalPathThumbnail() {
        if(localPathThumbnail==null){
            localPathThumbnail=bodyJson.getString("localPathThumbnail");
        }
        return localPathThumbnail;
    }

    private String localPath;
    private String fileName;
    private String remotePath;
    private String size;
    //远程缩略图
    private String thumbnailRemotePath;
    //本地缩略图地址
    private String localPathThumbnail;
    private int videoDuration=0;

    public HTMessageVideoBody(JSONObject bodyJson) {
        super(bodyJson);
    }

    public HTMessageVideoBody(String body) {
        super(body);
    }
}
