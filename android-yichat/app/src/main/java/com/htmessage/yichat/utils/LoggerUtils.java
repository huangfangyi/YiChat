package com.htmessage.yichat.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 项目名称：yichatDemo
 * 类描述：LoggerUtils 描述:
 * 创建人：songlijie
 * 创建时间：2017/10/27 13:48
 * 邮箱:814326663@qq.com
 */
public class LoggerUtils {
    private static String customTagPrefix = LoggerUtils.class.getPackage().getName(); //自定义tag 项目名字
    private static boolean isbug = true;

    private LoggerUtils() {
    }

    public static void isDebug(boolean isDebug) {
        isbug = isDebug;
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void d(String content) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.d(tag, content);
    }

    public static void v(String content) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.v(tag, content);
    }

    public static void e(String content) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.e(tag, content);
    }

    public static void i(String content) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.i(tag, content);
    }

    public static void w(String content) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.w(tag, content);
    }

    public static void e(String content, Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.e(tag, content, throwable);
    }

    public static void d(String content, Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.d(tag, content, throwable);
    }

    public static void i(String content, Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.i(tag, content, throwable);
    }

    public static void v(String content, Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.v(tag, content, throwable);
    }

    public static void w(String content, Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.w(tag, content, throwable);
    }

    public static void wtf(String content, Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.wtf(tag, content, throwable);
    }

    public static void wtf(String content) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.wtf(tag, content);
    }

    public static void wtf(Throwable throwable) {
        if (!isbug) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.wtf(tag, throwable);
    }
}
