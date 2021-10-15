package com.htmessage.sdk.client;

/**
 * Created by huangfangyi on 2017/7/7.
 * qq 84543217
 */

public class HTOptions {

    private boolean isDualProcess;

    public boolean isDebug() {
        return debug;
    }
    public void setDebug(boolean debug) {
        debug = debug;
    }

    private boolean debug;
    public boolean isDualProcess() {
        return isDualProcess;
    }

    public void setDualProcess(boolean isDualProcess) {
        isDualProcess = isDualProcess;
    }

//    private String host;
//    private String deviceUpdate;
//    private String deviceGet;
//    private String endpoint = " ";
//    private String accessKeyId = " ";
//    private String accessKeySecret = " ";
//    private String bucket = " ";
//    private String baseOssUrl = "http://" + bucket + "." + endpoint + "/";
//    private boolean isKeepAlive;
//
//    public boolean isDebug() {
//        return isDebug;
//    }
//
//    public void setDebug(boolean debug) {
//        isDebug = debug;
//    }
//
//    private boolean isDebug;
//    public boolean isKeepAlive() {
//        return isKeepAlive;
//    }
//
//    public void setKeepAlive(boolean keepAlive) {
//        isKeepAlive = keepAlive;
//    }
//
//
//    public String getHost() {
//        return host;
//    }
//
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public void setSinglePointUrl(String deviceUpdate, String deviceGet) {
//        this.deviceUpdate = deviceUpdate;
//        this.deviceGet = deviceGet;
//    }
//
//    public String getDeviceUpdate() {
//        return deviceUpdate;
//    }
//
//    public String getDeviceGet() {
//        return deviceGet;
//    }
//
//
//    public void setOssInfo(String endpoint, String bucket, String accessKeyId, String accessKeySecret) {
//        this.endpoint = endpoint;
//        this.bucket = bucket;
//        this.accessKeyId = accessKeyId;
//        this.accessKeySecret = accessKeySecret;
//        this.baseOssUrl = "http://" + bucket + "." + endpoint + "/";
//    }
//
//    public String getAccessKeyId() {
//        return accessKeyId;
//    }
//
//    public String getEndpoint() {
//        return endpoint;
//    }
//
//    public String getAccessKeySecret() {
//        return accessKeySecret;
//    }
//
//
//    public String getBucket() {
//        return bucket;
//    }
//
//
//    public String getBaseOssUrl() {
//        return baseOssUrl;
//    }


}
