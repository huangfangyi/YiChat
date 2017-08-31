package com.htmessage.fanxinht.anyrtc.Utils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

/**
 * Created by Skyline on 2016/5/24.
 */
public class LogUtil {
    private static final int METHOD_COUNT = 2;
    private static final int METHOD_OFFSET = 2;
    private static final LogLevel LOG_LEVEL = LogLevel.FULL;

    // 基本数据类型
    private final static String[] types = {"int", "java.lang.String", "boolean", "char",
            "float", "double", "long", "short", "byte"};

    public static void e(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.e(message);
    }

    public static void w(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.w(message);
    }

    public static void d(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.d(message);
    }

    public static void v(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.v(message);
    }

    public static void i(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.i(message);
    }

    public static void wtf(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.wtf(message);
    }

    public static void json(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.json(message);
    }

    public static void xml(String tag, String message) {
        Logger.init(tag)
                .methodCount(METHOD_COUNT)
                .logLevel(LOG_LEVEL)
                .methodOffset(METHOD_OFFSET);
        Logger.xml(message);
    }


    /**
     * 将对象转化为String
     *
     * @param object
     * @return
     */
    public static <T> String objectToString(T object) {
        if (object == null) {
            return "Object{object is null}";
        }
        if (object.toString().startsWith(object.getClass().getName() + "@")) {
            StringBuilder builder = new StringBuilder(object.getClass().getSimpleName() + "{");
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                boolean flag = false;
                for (String type : types) {
                    if (field.getType().getName().equalsIgnoreCase(type)) {
                        flag = true;
                        Object value = null;
                        try {
                            value = field.get(object);
                        } catch (IllegalAccessException e) {
                            value = e;
                        } finally {
                            builder.append(String.format("%s=%s, ", field.getName(),
                                    value == null ? "null" : value.toString()));
                            break;
                        }
                    }
                }
                if (!flag) {
                    builder.append(String.format("%s=%s, ", field.getName(), "Object"));
                }
            }
            return builder.replace(builder.length() - 2, builder.length() - 1, "}").toString();
        } else {
            return object.toString();
        }
    }

}
