package com.htmessage.sdk.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTPreferenceManager;
import com.htmessage.sdk.manager.MmvkManger;

import org.jivesoftware.smack.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.measite.minidns.record.A;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by huangfangyi on 2016/12/8.
 * qq 84543217
 */

public class HTHttpUtils {
    private Context context;
    private OkHttpClient okHttpClient;
    private static final int RESULT_ERROR = 1000;
    private static final int RESULT_SUCESS = 2000;
    private HttpCallBack httpCallBack;
    private String baseUrl = "";
    private String DEVICE_URL_GET;
    private String DEVICE_URL_UPDATE;


    public HTHttpUtils(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        if (SDKConstant.IS_LIMITLESS) {
            baseUrl = "http://" + HTPreferenceManager.getInstance().getIMServer() + ":19080/rest/adhoc/muc@muc.app.im";
            DEVICE_URL_GET = HTPreferenceManager.getInstance().getDeviceGet();
            DEVICE_URL_UPDATE = HTPreferenceManager.getInstance().getDeviceUpdate();
        } else {
            baseUrl = "http://" + SDKConstant.HOST + ":19080/rest/adhoc/muc@muc.app.im";
//            DEVICE_URL_GET = SDKConstant.DEVICE_URL_GET;
//            DEVICE_URL_UPDATE = SDKConstant.DEVICE_URL_UPDATE;
        }

    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //纯粹键值对post请求
    @SuppressLint("CheckResult")
    public void creatGroup(final List<String> data, final String groupName, final String groupDesc, final String imgUrl, final String username, final HttpCallBack httpCallBack) {


        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {


                JSONArray members = new JSONArray();
                for (String member : data) {
                    members.add(member);
                }
                JSONObject memberJson = new JSONObject();

                memberJson.put("var", "members");
                memberJson.put("value", members);
                JSONArray fields = new JSONArray();

                fields.add(memberJson);
                JSONObject imgurlJson = new JSONObject();
                imgurlJson.put("var", "imgurl");
                imgurlJson.put("value", imgUrl);
                fields.add(imgurlJson);

                JSONObject descJson = new JSONObject();
                descJson.put("var", "desc");
                descJson.put("value", groupDesc);
                fields.add(descJson);
                JSONObject name = new JSONObject();
                name.put("var", "name");
                name.put("value", groupName);
                fields.add(name);

                JSONObject uid = new JSONObject();
                uid.put("var", "uid");
                uid.put("value", username);
                fields.add(uid);

                JSONObject command = new JSONObject();
                command.put("fields", fields);
                command.put("node", "group-add");

                JSONObject finalData = new JSONObject();
                finalData.put("command", command);

                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);

                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });


    }

    //纯粹键值对post请求
    @SuppressLint("CheckResult")
    public void deleteGroup(final String groupId, final String username, final HttpCallBack httpCallBack) {


        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {

                String node = "group-del";
                JSONArray fields = new JSONArray();
                JSONObject groupIdJson = new JSONObject();
                groupIdJson.put("var", "gid");
                groupIdJson.put("value", groupId);
                fields.add(groupIdJson);
                JSONObject uid = new JSONObject();
                uid.put("var", "uid");
                uid.put("value", username);
                fields.add(uid);

                JSONObject command = new JSONObject();
                command.put("fields", fields);
                command.put("node", node);

                JSONObject finalData = new JSONObject();
                finalData.put("command", command);
                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);

                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });


    }


    @SuppressLint("CheckResult")
    public void leaveGroup(final String groupId, final String username, final String nickname, final HttpCallBack httpCallBack) {


        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {

                String node = "group-quit";
                JSONArray fields = new JSONArray();
                JSONObject groupIdJson = new JSONObject();
                groupIdJson.put("var", "gid");
                groupIdJson.put("value", groupId);
                fields.add(groupIdJson);
                JSONObject uid = new JSONObject();
                uid.put("var", "uid");
                uid.put("value", username);
                fields.add(uid);

                JSONObject userNickJson = new JSONObject();
                userNickJson.put("var", "nickname");
                userNickJson.put("value", nickname);
                fields.add(userNickJson);
                JSONObject command = new JSONObject();
                command.put("fields", fields);
                command.put("node", node);

                JSONObject finalData = new JSONObject();
                finalData.put("command", command);


                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);

                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });

    }


    @SuppressLint("CheckResult")
    public void updateGroupInfo(final String groupId, final String username, final String groupName, final String groupDesc, final String imageUrl, final HttpCallBack httpCallBack) {


        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {

                String node = "group-update";
                JSONArray fields = new JSONArray();
                JSONObject groupIdJson = new JSONObject();
                groupIdJson.put("var", "gid");
                groupIdJson.put("value", groupId);
                fields.add(groupIdJson);
                JSONObject uid = new JSONObject();
                uid.put("var", "uid");
                uid.put("value", username);
                fields.add(uid);

                JSONObject groupNameJSON = new JSONObject();
                groupNameJSON.put("var", "name");
                groupNameJSON.put("value", groupName);
                fields.add(groupNameJSON);
                JSONObject desJSON = new JSONObject();
                desJSON.put("var", "desc");
                desJSON.put("value", groupDesc);
                fields.add(desJSON);
                JSONObject imgurlJSON = new JSONObject();
                imgurlJSON.put("var", "imgurl");
                imgurlJSON.put("value", imageUrl);
                fields.add(imgurlJSON);

                JSONObject command = new JSONObject();
                command.put("fields", fields);
                command.put("node", node);

                JSONObject finalData = new JSONObject();
                finalData.put("command", command);


                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);
                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });

    }


//    //纯粹键值对post请求
//    @SuppressLint("CheckResult")
//    public void deleteMember(final String groupId, final String username, final String deleteUserId, final HttpCallBack httpCallBack) {
//        String node = "member-del";
//        JSONArray fields = new JSONArray();
//
//        JSONObject groupIdJson = new JSONObject();
//        groupIdJson.put("var", "gid");
//        groupIdJson.put("value", groupId);
//        fields.add(groupIdJson);
//
//        JSONObject deleteUser = new JSONObject();
//        deleteUser.put("var", "oid");
//        deleteUser.put("value", deleteUserId);
//        fields.add(deleteUser);
//
//        JSONObject uid = new JSONObject();
//        uid.put("var", "uid");
//        uid.put("value", username);
//        fields.add(uid);
//
//        JSONObject command = new JSONObject();
//        command.put("fields", fields);
//        command.put("node", node);
//
//        JSONObject finalData = new JSONObject();
//        finalData.put("command", command);
//
//
//        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {
//
//                String node = "member-del";
//                JSONArray fields = new JSONArray();
//
//                JSONObject groupIdJson = new JSONObject();
//                groupIdJson.put("var", "gid");
//                groupIdJson.put("value", groupId);
//                fields.add(groupIdJson);
//
//                JSONObject deleteUser = new JSONObject();
//                deleteUser.put("var", "oid");
//                deleteUser.put("value", deleteUserId);
//                fields.add(deleteUser);
//
//                JSONObject uid = new JSONObject();
//                uid.put("var", "uid");
//                uid.put("value", username);
//                fields.add(uid);
//
//                JSONObject command = new JSONObject();
//                command.put("fields", fields);
//                command.put("node", node);
//
//                JSONObject finalData = new JSONObject();
//                finalData.put("command", command);
//
//
//                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
//                e.onNext(response);
//
//
//            }
//
//
//        }).map(new Function<Response, JSONObject>() {
//            @Override
//            public JSONObject apply(Response response) throws Exception {
//
//                if (response.isSuccessful()) {
//                    ResponseBody body = response.body();
//                    if (body != null) {
//                        String result = body.string();
//                        try {
//                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);
//
//                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
//                            return jsonObject;
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//                    }
//                }
//                return null;
//
//
//            }
//        })
//
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<JSONObject>() {
//                    @Override
//                    public void accept(JSONObject jsonObject) throws Exception {
//
//                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
//                            return;
//
//                        }
//                        if (jsonObject != null) {
//                            httpCallBack.onResponse(jsonObject);
//                        } else {
//                            httpCallBack.onFailure("network connection is failed");
//                        }
//
//                    } // 第三步：订阅
//
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
//                            return;
//
//                        }
//                        httpCallBack.onFailure("network connection is failed");
//                    }
//                });
//
//    }

    //纯粹键值对post请求
    @SuppressLint("CheckResult")
    public void deleteMember(final String groupId, final String username, final Map<String,String> membersMap, final HttpCallBack httpCallBack) {



        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {

                String node = "member-del";
                JSONArray fields = new JSONArray();

                JSONObject groupIdJson = new JSONObject();
                groupIdJson.put("var", "gid");
                groupIdJson.put("value", groupId);
                fields.add(groupIdJson);

                JSONObject deleteUser = new JSONObject();
                deleteUser.put("var", "oid");
                List<String> membersIds=new ArrayList<>(membersMap.keySet());
                JSONArray oidAarry=new JSONArray();
                for(String userId:membersIds){
                    oidAarry.add(userId);
                }
                deleteUser.put("value", oidAarry);
                fields.add(deleteUser);

                JSONObject uid = new JSONObject();
                uid.put("var", "uid");
                uid.put("value", username);
                fields.add(uid);

                JSONObject command = new JSONObject();
                command.put("fields", fields);
                command.put("node", node);

                JSONObject finalData = new JSONObject();
                finalData.put("command", command);


                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);
                             Log.d("resultDe---",resultDe);
                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });

    }

    @SuppressLint("CheckResult")
    public void addMembers(final Map<String, String> membersMap, final String groupId, final String username, final HttpCallBack httpCallBack) {


        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {

                List<String> data = new ArrayList<>(membersMap.keySet());
                String node = "member-add";
                JSONArray members = new JSONArray();
                for (String member : data) {
                    members.add(member);
                }
                JSONObject memberJson = new JSONObject();

                memberJson.put("var", "oid");
                memberJson.put("value", members);
                JSONArray fields = new JSONArray();
                fields.add(memberJson);
                JSONObject gidJson = new JSONObject();
                gidJson.put("var", "gid");
                gidJson.put("value", groupId);
                fields.add(gidJson);
                JSONObject uid = new JSONObject();
                uid.put("var", "uid");
                uid.put("value", username);
                fields.add(uid);
                JSONObject command = new JSONObject();
                command.put("fields", fields);
                command.put("node", node);
                JSONObject finalData = new JSONObject();
                finalData.put("command", command);


                Response response = okHttpClient.newCall(getRequest(finalData)).execute();
                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY, result);

                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });
    }


    private Request getRequest(JSONObject finalData) {
        String aes = AESUtils.encryptText(AESUtils.KEY, finalData.toJSONString());
        Log.d("finalData", finalData.toJSONString());
        RequestBody requestBody = RequestBody.create(JSON, URLEncoder.encode(aes));
        Request request = new Request.Builder()
                .addHeader("Authorization", URLEncoder.encode(AESUtils.encryptText(AESUtils.KEY, "Basic YWRtaW5AYXBwLmltOjEyMzQ1NkBhcHA=")))
                .addHeader("Content-Type", "application/json")
                .url(baseUrl)
                .post(requestBody)
                .build();
        return request;

    }

    @SuppressLint("CheckResult")
    public void getGroupList(final String username, final HttpCallBack httpCallBack) {


        Observable.create(new ObservableOnSubscribe<Response>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {

                Request.Builder builer = new Request.Builder()
                        .url(SDKConstant.URL_GROUP_LIST)
                        .addHeader("Content-Type", "application/json");

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = null;


                String encryptData = URLEncoder.encode(AESUtils.encryptText(AESUtils.KEY_API, new JSONObject().toJSONString()), "utf-8");
                body = RequestBody.create(JSON, encryptData);


                builer.post(body);


                String token = MmvkManger.getIntance().getAsString("KEY_LOGIN_USER_TOKEN");
                Log.d("groupList---server---", token);
                if (TextUtils.isEmpty(token)) {
                    httpCallBack.onFailure("errpor");
                    return;


                }
                builer.addHeader("zf-token",token);
                builer.addHeader("None-AES", "1");
                Request request = builer.build();


                Response response = okHttpClient.newCall(request).execute();

                e.onNext(response);


            }


        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {

                if (response != null && response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                         try {
                            String resultDe = AESUtils.decryptText(AESUtils.KEY_API, result);
                            Log.d("groupList---server---2", resultDe);
                            JSONObject jsonObject = JSONObject.parseObject(resultDe);
                            return jsonObject;

                        } catch (Exception e) {
                             e.printStackTrace();

                        }

                    }
                }
                return null;


            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {

                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        if (jsonObject != null) {
                            httpCallBack.onResponse(jsonObject);
                        } else {
                            httpCallBack.onFailure("network connection is failed");
                        }

                    } // 第三步：订阅

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
                            return;

                        }
                        httpCallBack.onFailure("network connection is failed");
                    }
                });

    }


    public interface HttpCallBack {

        void onResponse(JSONObject jsonObject);

        void onFailure(String errorMsg);

    }

    public interface DownloadCallBack {

        void onSuccess();

        void onFailure();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    public void loadFile(String url, final String savePath, final DownloadCallBack callBack) {
        Request request = new Request.Builder()
                //下载地址
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);

                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                callBack.onSuccess();

            }
        });
    }


//    public void getDeviceId(String userId, final HttpCallBack callBack) {
//        FormBody.Builder bodyBulder = new FormBody.Builder();
//        bodyBulder.add("userId", userId);
//        RequestBody requestBody = bodyBulder.build();
//        Request request = new Request.Builder()
//                .url(DEVICE_URL_GET)
//                .post(requestBody)
//                .build();
//
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            String result = response.body().string();
//            try {
//                JSONObject jsonObject = JSONObject.parseObject(result);
//                callBack.onResponse(jsonObject);
//            } catch (JSONException e) {
//                callBack.onFailure("data prase error");
//            }
//        } catch (IOException e) {
//            callBack.onFailure("error");
//            e.printStackTrace();
//        }
//
//    }

//    public void updateDeviceId(String userId, String deviceId, final HttpCallBack callBack) {
//        FormBody.Builder bodyBulder = new FormBody.Builder();
//        bodyBulder.add("userId", userId);
//        bodyBulder.add("deviceId", deviceId);
//        RequestBody requestBody = bodyBulder.build();
//        Request request = new Request.Builder()
//                .url(DEVICE_URL_UPDATE)
//                .post(requestBody)
//                .build();
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//
//            String result = response.body().string();
//            try {
//                JSONObject jsonObject = JSONObject.parseObject(result);
//                callBack.onResponse(jsonObject);
//            } catch (JSONException e) {
//                callBack.onFailure("data prase error");
//            }
//        } catch (IOException e) {
//            callBack.onFailure("error");
//            e.printStackTrace();
//        }
//
//
//    }
}
