package com.htmessage.fanxinht.acitivity.chat.file.util;

import com.htmessage.fanxinht.R;

import java.util.HashMap;
import java.util.Map;


public class FileIcons {

    private static final Map<String, Integer> smallIconMap = new HashMap<String, Integer>();
    static {
        smallIconMap.put("xls", R.drawable.file_ic_session_excel);
        smallIconMap.put("ppt", R.drawable.file_ic_session_ppt);
        smallIconMap.put("doc", R.drawable.file_ic_session_word);
        smallIconMap.put("xlsx", R.drawable.file_ic_session_excel);
        smallIconMap.put("pptx", R.drawable.file_ic_session_ppt);
        smallIconMap.put("docx", R.drawable.file_ic_session_word);
        smallIconMap.put("pdf", R.drawable.file_ic_session_pdf);
        smallIconMap.put("html", R.drawable.file_ic_session_html);
        smallIconMap.put("htm", R.drawable.file_ic_session_html);
        smallIconMap.put("txt", R.drawable.file_ic_session_txt);
        smallIconMap.put("rar", R.drawable.file_ic_session_rar);
        smallIconMap.put("zip", R.drawable.file_ic_session_zip);
        smallIconMap.put("7z", R.drawable.file_ic_session_zip);
        smallIconMap.put("mp4", R.drawable.file_ic_session_mp4);
        smallIconMap.put("mp3", R.drawable.file_ic_session_mp3);
        smallIconMap.put("amr", R.drawable.file_ic_session_mp3);
        smallIconMap.put("png", R.drawable.file_ic_session_png);
        smallIconMap.put("gif", R.drawable.file_ic_session_gif);
        smallIconMap.put("jpg", R.drawable.file_ic_session_jpg);
        smallIconMap.put("jpeg", R.drawable.file_ic_session_jpg);
        smallIconMap.put("apk", R.mipmap.ic_launcher);
    }

    private static final Map<String, Integer> bigIconMap = new HashMap<String, Integer>();
    static {
        bigIconMap.put("xls", R.drawable.file_ic_detail_excel);
        bigIconMap.put("ppt", R.drawable.file_ic_detail_ppt);
        bigIconMap.put("doc", R.drawable.file_ic_detail_word);
        bigIconMap.put("xlsx", R.drawable.file_ic_detail_excel);
        bigIconMap.put("pptx", R.drawable.file_ic_detail_ppt);
        bigIconMap.put("docx", R.drawable.file_ic_detail_word);
        bigIconMap.put("pdf", R.drawable.file_ic_detail_pdf);
        bigIconMap.put("html", R.drawable.file_ic_detail_html);
        bigIconMap.put("htm", R.drawable.file_ic_detail_html);
        bigIconMap.put("txt", R.drawable.file_ic_detail_txt);
        bigIconMap.put("rar", R.drawable.file_ic_detail_rar);
        bigIconMap.put("zip", R.drawable.file_ic_detail_zip);
        bigIconMap.put("7z", R.drawable.file_ic_detail_zip);
        bigIconMap.put("mp4", R.drawable.file_ic_detail_mp4);
        bigIconMap.put("mp3", R.drawable.file_ic_detail_mp3);
        bigIconMap.put("amr", R.drawable.file_ic_detail_mp3);
        bigIconMap.put("png", R.drawable.file_ic_detail_png);
        bigIconMap.put("gif", R.drawable.file_ic_detail_gif);
        bigIconMap.put("jpg", R.drawable.file_ic_detail_jpg);
        bigIconMap.put("jpeg", R.drawable.file_ic_detail_jpg);
        bigIconMap.put("apk", R.mipmap.ic_launcher);
    }

    public static int smallIcon(String fileName) {
        String ext = FileUtil.getExtensionName(fileName).toLowerCase();
        Integer resId = smallIconMap.get(ext);
        if (resId == null) {
            return R.drawable.file;
        }

        return resId.intValue();
    }

    public static int bigIcon(String fileName) {
        String ext = FileUtil.getExtensionName(fileName).toLowerCase();
        Integer resId = bigIconMap.get(ext);
        if (resId == null) {
            return R.drawable.file;
        }

        return resId.intValue();
    }
}
