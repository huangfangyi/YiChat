package com.htmessage.fanxinht.acitivity.chat.file.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 是否有扩展
     * @param filename
     * @return
     */
    public static boolean hasExtentsion(String filename) {
        int dot = filename.lastIndexOf('.');
        if ((dot > -1) && (dot < (filename.length() - 1))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取文件扩展名
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }
    /**
     *  获取文件名
     * @param filepath
     * @return
     */
    public static String getFileNameFromPath(String filepath) {
        if ((filepath != null) && (filepath.length() > 0)) {
            int sep = filepath.lastIndexOf('/');
            if ((sep > -1) && (sep < filepath.length() - 1)) {
                return filepath.substring(sep + 1);
            }
        }
        return filepath;
    }

    /**
     * 获取不带扩展名的文件名
     * @param filename
     * @return
     */

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 打开文件
     * @param file 文件
     * @param activity 文件要打开的界面
     */
    public static void openFile(File file, Activity activity){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        String mimeType = getMimeType(file.getAbsolutePath());
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        try {
            activity.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(activity, "Can\'t find proper app to open this file", 1).show();
        }
    }

    /**
     * 获取文件类型
     * @param filePath
     * @return
     */
    public static String getMimeType(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String type = null;
        String extension = getExtensionName(filePath.toLowerCase());
        if (!TextUtils.isEmpty(extension)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        Log.i(TAG, "url:" + filePath + " " + "type:" + type);

        // FIXME
        if (StringUtil.isEmpty(type) && filePath.endsWith("aac")) {
            type = "audio/aac";
        }

        return type;
    }

    public enum SizeUnit {
        Byte,
        KB,
        MB,
        GB,
        TB,
        Auto,
    }

    public static String formatFileSize(long size) {
        return formatFileSize(size, SizeUnit.Auto);
    }

    /**
     *  获取文件的大小
     * @param size
     * @param unit
     * @return
     */
    public static String formatFileSize(long size, SizeUnit unit) {
        if (size < 0) {
            return "未知大小";
        }

        final double KB = 1024;
        final double MB = KB * 1024;
        final double GB = MB * 1024;
        final double TB = GB * 1024;
        if (unit == SizeUnit.Auto) {
            if (size < KB) {
                unit = SizeUnit.Byte;
            } else if (size < MB) {
                unit = SizeUnit.KB;
            } else if (size < GB) {
                unit = SizeUnit.MB;
            } else if (size < TB) {
                unit = SizeUnit.GB;
            } else {
                unit = SizeUnit.TB;
            }
        }
        switch (unit) {
            case Byte:
                return size + "B";
            case KB:
                return String.format(Locale.US, "%.2fKB", size / KB);
            case MB:
                return String.format(Locale.US, "%.2fMB", size / MB);
            case GB:
                return String.format(Locale.US, "%.2fGB", size / GB);
            case TB:
                return String.format(Locale.US, "%.2fPB", size / TB);
            default:
                return size + "B";
        }
    }
    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean 是否copy文件成功
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean isok = true;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();//刷新流
                fs.close();//关闭
                inStream.close();//关闭输入流
            } else {
                isok = false;
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }
    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean 是否copy整个文件夹成功
     */
    public static boolean copyFolder(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                } else {
                    temp=new File(oldPath+File.separator+file[i]);
                }
                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();//刷新流数据
                    output.close();//关闭输出流
                    input.close();//关闭输入流
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); //递归调用
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }
}
