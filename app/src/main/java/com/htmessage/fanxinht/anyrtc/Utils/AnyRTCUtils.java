package com.htmessage.fanxinht.anyrtc.Utils;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;

/**
 * Created by Skyline on 2016/11/1.
 */

public enum AnyRTCUtils {
    AnyRTC_OK(0),               //正常
    AnyRTC_UNKNOW(1),           // 未知错误
    AnyRTC_EXCEPTION(2),	    // SDK调用异常
    AnyRTC_NET_ERR(100),		// 网络错误
    AnyRTC_LIVE_ERR(101),		// 直播出错
    AnyRTC_BAD_REQ(201),		// 服务不支持的错误请求
    AnyRTC_AUTH_FAIL(202),		// 认证失败
    AnyRTC_NO_USER(203),		// 此开发者信息不存在
    AnyRTC_SQL_ERR(204),		// 服务器内部数据库错误
    AnyRTC_ARREARS(205),		// 账号欠费
    AnyRTC_LOCKED(206),		    // 账号被锁定
    AnyRTC_FORCE_EXIT(207);	    // 强制离开

    private int value;

    AnyRTCUtils(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 根据错误码获取文字描述
     * @param value
     * @return
     */
    public static String getErrString(int value) {
        if (value == AnyRTC_OK.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_ok);
        } else if (value == AnyRTC_UNKNOW.getValue()) {
            return HTApp.getContext().getString(R.string.str_unknow_exception);
        } else if (value == AnyRTC_EXCEPTION.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_exception);
        } else if (value == AnyRTC_NET_ERR.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_net_err);
        } else if (value == AnyRTC_LIVE_ERR.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_live_err);
        } else if (value == AnyRTC_BAD_REQ.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_bad_req);
        } else if (value == AnyRTC_AUTH_FAIL.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_auth_fail);
        } else if (value == AnyRTC_NO_USER.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_no_user);
        } else if (value == AnyRTC_SQL_ERR.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_sql_err);
        } else if (value == AnyRTC_ARREARS.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_arrears);
        } else if (value == AnyRTC_LOCKED.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_locked);
        } else if (value == AnyRTC_FORCE_EXIT.getValue()) {
            return HTApp.getContext().getString(R.string.str_anyrtc_force_exit);
        } else {
            return "";
        }
    }
}
