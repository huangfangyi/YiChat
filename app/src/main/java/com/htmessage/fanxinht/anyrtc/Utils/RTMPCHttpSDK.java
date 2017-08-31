package com.htmessage.fanxinht.anyrtc.Utils;

import android.content.Context;

import com.htmessage.fanxinht.HTConstant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by Eric on 2016/7/27.
 */
public class RTMPCHttpSDK {
//    public final static String gHttpLiveListUrl = "http://%s/anyapi/V1/livelist?AppID=%s&DeveloperID=%s";
//    public final static String gHttpRecordUrl = "http://%s/anyapi/V1/recordrtmp?AppID=%s&DeveloperID=%s&AnyrtcID=%s&Url=%s&ResID=%s";
//    public final static String gHttpCloseRecUrl = "http://%s/anyapi/V1/closerecrtmp?AppID=%s&DeveloperID=%s&VodSvrID=%s&VodResTag=%s";

    /**
     * RTMPC http 回调接口
     */
    public static interface RTMPCHttpCallback {
        public void OnRTMPCHttpOK(String strContent);

        public void OnRTMPCHttpFailed(int code);
    }

    /**
     * 获取直播服务器直播大厅列表
     * @param ctx 上下文环境
     * @param strAddr 直播大厅服务器地址
     * @param strDeveloperId AnyRTC平台开发者ID
     * @param strAppId AnyRTC平台申请的应用的appId
     * @param strToken AnyRTC平台申请的应用的appToken
     * @param callback 获取直播列表的响应回调
     */
    public static void GetLiveList(final Context ctx, String strAddr, String strDeveloperId, final String strAppId, final String strToken, final RTMPCHttpCallback callback) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        final String httpUrl = String.format(HTConstant.gHttpLiveListUrl, strAddr, strAppId, strDeveloperId);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            int auth_times = 0;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String strContent = new String(responseBody);
                callback.OnRTMPCHttpOK(strContent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401 && auth_times == 0) {
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].getName().equals("WWW-Authenticate")) {
                            HttpAuthHeader httpAuthHeader = new HttpAuthHeader(
                                    headers[i].getValue());

                            String realm = httpAuthHeader.getRealm();
                            String uri = httpUrl;
                            String nonce = httpAuthHeader.getNonce();
                            String cnonce = getRandomString(16);

                            String response = authDigest("GET", strAppId, realm, uri, strToken, nonce, cnonce,
                                    httpAuthHeader.getScheme(), httpAuthHeader.getQop().length() > 0);

                            Header[] reqHdr = new Header[1];
                            reqHdr[0] = new BasicHeader("Authorization", response);
                            httpClient.get(ctx, httpUrl, reqHdr, null, this);
                            auth_times++;
                            return;
                        }
                    }
                }
                callback.OnRTMPCHttpFailed(statusCode);
            }
        };
        httpClient.get(ctx, httpUrl, handler);
    }

    /**
     * 停止录像
     * @param ctx 上下文环境
     * @param strAddr 直播大厅服务器地址
     * @param strDeveloperId AnyRTC平台开发者ID
     * @param strAppId AnyRTC平台申请的应用的appId
     * @param strToken AnyRTC平台申请的应用的appToken
     * @param strVodSvrId 录像成功时响应的VodSvrId
     * @param strVodResTag 录像成功时响应的VodResTag
     */
    public static void CloseRecRtmpStream(final Context ctx, String strAddr, String strDeveloperId, final String strAppId,
                                          final String strToken, final String strVodSvrId, final String strVodResTag) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        final String httpUrl = String.format(HTConstant.gHttpCloseRecUrl, strAddr, strAppId, strDeveloperId, strVodSvrId, strVodResTag);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            int auth_times = 0;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401 && auth_times == 0) {
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].getName().equals("WWW-Authenticate")) {
                            HttpAuthHeader httpAuthHeader = new HttpAuthHeader(
                                    headers[i].getValue());

                            String realm = httpAuthHeader.getRealm();
                            String uri = httpUrl;
                            String nonce = httpAuthHeader.getNonce();
                            String cnonce = getRandomString(16);

                            String response = authDigest("GET", strAppId, realm, uri, strToken, nonce, cnonce,
                                    httpAuthHeader.getScheme(), httpAuthHeader.getQop().length() > 0);

                            Header[] reqHdr = new Header[1];
                            reqHdr[0] = new BasicHeader("Authorization", response);
                            httpClient.get(ctx, httpUrl, reqHdr, null, this);
                            auth_times++;
                            return;
                        }
                    }
                }
            }
        };
        httpClient.get(ctx, httpUrl, handler);
    }

    /**
     *
     * @param ctx 上下文环境
     * @param strAddr 直播大厅服务器地址
     * @param strDeveloperId AnyRTC平台开发者ID
     * @param strAppId AnyRTC平台申请的应用的appId
     * @param strToken AnyRTC平台申请的应用的appToken
     * @param strAnyrtcId AnyRTC平台的anyrtcId
     * @param rtmpurl 录像的rtmpurl
     * @param resId 系统直播的id，录播完成后会异步通知到AnyRTC平台设置的App录像异步通知URL中
     * @param callback 获取录像的响应回调
     */
    public static void RecordRtmpStream(final Context ctx, String strAddr, String strDeveloperId, final String strAppId,
                                        final String strToken, final String strAnyrtcId, final String rtmpurl, final String resId, final RTMPCHttpCallback callback) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        final String httpUrl = String.format(HTConstant.gHttpRecordUrl, strAddr, strAppId, strDeveloperId, strAnyrtcId, rtmpurl, resId);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            int auth_times = 0;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String strContent = new String(responseBody);
                callback.OnRTMPCHttpOK(strContent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401 && auth_times == 0) {
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].getName().equals("WWW-Authenticate")) {
                            HttpAuthHeader httpAuthHeader = new HttpAuthHeader(
                                    headers[i].getValue());

                            String realm = httpAuthHeader.getRealm();
                            String uri = httpUrl;
                            String nonce = httpAuthHeader.getNonce();
                            String cnonce = getRandomString(16);

                            String response = authDigest("GET", strAppId, realm, uri, strToken, nonce, cnonce,
                                    httpAuthHeader.getScheme(), httpAuthHeader.getQop().length() > 0);

                            Header[] reqHdr = new Header[1];
                            reqHdr[0] = new BasicHeader("Authorization", response);
                            httpClient.get(ctx, httpUrl, reqHdr, null, this);
                            auth_times++;
                            return;
                        }
                    }
                }
                callback.OnRTMPCHttpFailed(statusCode);
            }
        };
        httpClient.get(ctx, httpUrl, handler);
    }

    public static String authDigest(String method, String username,
                                    String realm, String uri, String password, String nonce,
                                    String cnonce, int authMethod, boolean has_qop) {
        if (authMethod != HttpAuthHeader.DIGEST)
            return "";
        String ncount = "00000001";
        if (cnonce == null || cnonce.length() == 0)
            cnonce = md5(String.format("%d", System.currentTimeMillis()));
        String sentive = String.format("%s:%s:%s", username, realm, password);
        String A2 = method + ":" + uri;
        String HA1 = md5(sentive);
        String HA2 = md5(A2);
        String middle;
        String qop = "";
        if (has_qop) {
            qop = "auth";
            middle = nonce + ":" + ncount + ":" + cnonce + ":" + qop;
        } else {
            middle = nonce;
        }
        String response = md5(HA1 + ":" + middle + ":" + HA2);

        String strAuthResp = String.format(
                "Digest username=\"%s\", realm=\"%s\", nonce=\"%s\", "
                        + "uri=\"%s\", response=\"%s\"", username, realm,
                nonce, uri, response);
        if (has_qop) {
            strAuthResp += ", ";
            strAuthResp += String.format("qop=%s, nc=%s, cnonce=\"%s\"", qop,
                    ncount, cnonce);
        }
        return strAuthResp;
    }

    public static String getRandomString(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);//0~61
            sf.append(str.charAt(number));
        }
        return sf.toString();
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
