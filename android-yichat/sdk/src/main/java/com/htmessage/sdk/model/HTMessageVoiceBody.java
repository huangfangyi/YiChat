package com.htmessage.sdk.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/2/14.
 * qq 84543217
 */

public class HTMessageVoiceBody extends HTMessageBody {
    private String localPath;
    private String fileName;
    private String remotePath;
     private int audioDuration=0;

    public HTMessageVoiceBody(){}


    public void setLocalPath(String localPath) {
        this.localPath = localPath;
        bodyJson.put("localPath",localPath);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        bodyJson.put("fileName",fileName);
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        bodyJson.put("remotePath",remotePath);

    }

    public void setAudioDuration(int audioDuration) {
        this.audioDuration = audioDuration;
        bodyJson.put("audioDuration",audioDuration);
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

    public int getAudioDuration() {
        if(audioDuration==0){
            audioDuration=bodyJson.getInteger("audioDuration");
        }
        return audioDuration;
    }

    public HTMessageVoiceBody(JSONObject bodyJson) {
        super(bodyJson);
//        if (bodyJson.containsKey("audioDuration")) {
//            audioDuration = bodyJson.getInteger("audioDuration");
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

    }

    public HTMessageVoiceBody(String body){
        super(body);
//        if (bodyJson.containsKey("audioDuration")) {
//            audioDuration = bodyJson.getInteger("audioDuration");
//        }
//        if (bodyJson.containsKey("localPath")) {
//            localPath = bodyJson.getString("localPath");
//        }
//        if (bodyJson.containsKey("fileName")) {
//            fileName = bodyJson.getString("fileName");
//        }
//        if (bodyJson.containsKey("remotePath")) {
//            remotePath = bodyJson.getString("remotePath");
//        }

    }


//    @Override
//    public String getLocalBody() {
//
//        if (audioDuration!=0) {
//            bodyJson.put("audioDuration", audioDuration);
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
//
//
//        return bodyJson.toJSONString();
//    }

}
