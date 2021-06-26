package com.htmessage.sdk.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/2/15.
 * qq 84543217
 */

public class HTMessageLocationBody extends HTMessageBody {
    private double latitude = 0;
    private double longitude = 0;
    private String address;

    private String localPath;
    private String remotePath;
    private String fileName;
    public HTMessageLocationBody(){

    }
    public HTMessageLocationBody(JSONObject bodyJson) {
        super(bodyJson);
    }

    public HTMessageLocationBody(String body) {
        super(body);
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
        bodyJson.put("latitude", latitude);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        bodyJson.put("longitude", longitude);
    }

    public void setAddress(String address) {
        this.address = address;
        bodyJson.put("address", address);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        bodyJson.put("fileName", fileName);
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        bodyJson.put("remotePath", remotePath);

    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
        bodyJson.put("localPath", localPath);
    }

    public double getLatitude() {
        if (latitude == 0) {
            latitude = bodyJson.getDouble("latitude");
        }
        return latitude;
    }

    public double getLongitude() {
        if (longitude == 0) {
            longitude = bodyJson.getDouble("longitude");
        }
        return longitude;
    }

    public String getAddress() {
        if (address == null) {
            address = bodyJson.getString("address");
        }
        return address;
    }

    public String getLocalPath() {
        if (localPath == null) {
            localPath = bodyJson.getString("localPath");
        }
        return localPath;
    }

    public String getRemotePath() {
        if (remotePath == null) {
            remotePath = bodyJson.getString("remotePath");
        }
        return remotePath;
    }

    public String getFileName() {
        if (fileName == null) {
            fileName = bodyJson.getString("fileName");
        }
        return fileName;
    }
}
