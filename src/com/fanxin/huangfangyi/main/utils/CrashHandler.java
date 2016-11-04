package com.fanxin.huangfangyi.main.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.main.FXConstant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Crash
 * 异常拦截器
 * create by Zhiqiang_H 2016-03-23
 */
public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    /** 是否开启日志输出,在Debug状态下开启,
     * 在Release状态下关闭以提示程序性能
     * */
    public static final boolean DEBUG = true;

    private static CrashHandler instance;
    private Context context;
    private String errorLogDir = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    /** 错误报告文件的扩展名 */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    private final int expirationDate = 7;//设置过期时间(单位：天)
    private File file;//保存的文件路径
    private CrashHandler(){
    }

    public synchronized static CrashHandler getInstance(){
        if(instance == null){
            instance = new CrashHandler();
        }
        return instance;
    }
    public void init(Context context){
       init(context,getDefaultSaveRootPath(context));
    }
    public void init(Context context, String errorLogDir){
        this.context = context;
        this.errorLogDir = errorLogDir;
        Thread.setDefaultUncaughtExceptionHandler(this);
        //删除过期文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteExpirationFile();
            }
        }).start();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(ex != null) {
            //收集设备信息
            disposeThrowable(ex);
            ex.printStackTrace();
        }
    }

    private void disposeThrowable(Throwable ex){
            getInfo();
            String mtype = android.os.Build.MODEL; // 手机型号
            String mtyb= android.os.Build.BRAND;//手机品牌
            String cpuAbi = android.os.Build.CPU_ABI;//cpu架构
            String sdk = android.os.Build.VERSION.SDK;//sdk版本号
            String release = android.os.Build.VERSION.RELEASE;//依赖的系统版本号
            String manufacturer = android.os.Build.MANUFACTURER;//手机品牌
         StringBuffer sb = new StringBuffer();
        sb.append("手机系统信息:"+"\n"+"手机品牌:"+mtyb+"\n" +"手机型号:"+mtype+"\n"+"制造商:"+manufacturer+"\n"+"手机系统版本号:"+release+"\n"+"系统SDK版本号:"+sdk+"\n"+"cpu架构:"+cpuAbi+"\n");
        sb.append("错误信息:"+"\n"+ex.toString()+"\n");
        sb.append("错误所在:"+"\n");
        StackTraceElement[] steArray = ex.getStackTrace();
        for(StackTraceElement ste : steArray){
            sb.append("System.err at " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n");
        }
        if(errorLogDir != null){
            try {
                file = new File(errorLogDir+File.separator + "Error" + File.separator+ sdf.format(System.currentTimeMillis()) + CRASH_REPORTER_EXTENSION);
                if(!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    printLog(TAG,"chuangjianfile");
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes("utf-8"));
                fos.flush();
                fos.close();
                printLog(TAG,"xie如成功");
                fos = null;
            }catch (Exception e){
                e.printStackTrace();
                printLog(TAG,"errorLogDir:"+errorLogDir +"Exception:" +e.getMessage());
            }
        }
        Process.killProcess(Process.myPid());
        System.exit(10);
    }
    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        Log.d(TAG,"走了发送异常报告");
        sendCrashReportsToServer();
    }
    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    private void sendCrashReportsToServer() {
        String[] crFiles = getCrashReportFiles();
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            for (String fileName : sortedFiles) {
                File cr = new File(errorLogDir+File.separator + "Error", fileName);
                printLog(TAG,"fileName:"+fileName);
                postReport(cr);
                cr.delete();// 删除已发送的报告
            }
        }
    }
    private void postReport(File file) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("crash","crash"));
        List<File> files = new ArrayList<File>();
        if (file.exists()){
            files.add(file);
        }
        Log.d(TAG,"走了发送异常报告:"+file.toString());
        // TODO 发送错误报告到服务器
        new OKHttpUtils(context).post(params, files, FXConstant.HOST, new OKHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                printLog(TAG,"走了发送异常报告:"+jsonObject);
                printLog(TAG,"crashjsonObject:"+jsonObject);
            }

            @Override
            public void onFailure(String errorMsg) {
                printLog(TAG,"crasherrorMsg:"+errorMsg);
                printLog(TAG,"没走:"+errorMsg);
            }
        });
    }
    /**
     * 获取错误报告文件名
     * @return
     */
    private String[] getCrashReportFiles() {
        File filesDir = new File(errorLogDir+File.separator + "Error");
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    private void deleteExpirationFile(){
        if(errorLogDir == null){
            return;
        }
        long expirationTime = expirationDate * 24 * 3600 * 1000;
        long nowTime = System.currentTimeMillis();
        List<File> deleteList = new ArrayList<File>();
        File dir = new File(errorLogDir);
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            for(File f : files){
                if(f.exists()){
                    long time = f.lastModified();
                    if(nowTime - time >= expirationTime){
                        deleteList.add(f);
                    }
                }
            }
            for(File f : deleteList){
                f.delete();
            }
        }
    }
    /**
     * 获取IMEI号，IESI号，手机型号
     */
    private String getInfo() {
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb= android.os.Build.BRAND;//手机品牌
        String cpuAbi = android.os.Build.CPU_ABI;//cpu架构
        String sdk = android.os.Build.VERSION.SDK;//sdk版本号
        String release = android.os.Build.VERSION.RELEASE;//依赖的系统版本号
        String device = android.os.Build.DEVICE;//设备
        String manufacturer = android.os.Build.MANUFACTURER;//手机品牌
        String mobileInfo = "手机品牌："+mtyb+",手机型号："+mtype+",cpu架构:"+cpuAbi+",系统SDK版本号:"+sdk+",手机系统版本号:"+release+",制造商:"+manufacturer;
        printLog(TAG,"mobile:"+mobileInfo);
        return mobileInfo;
    }
    /**
     * .获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    private String getMacAddress(Context context){
        String result = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return result;
    }
    /**
     * 手机CPU信息
     */
    private String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (Exception e) {
        }
        printLog(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);
        return cpuInfo;
    }
    /**
     * 获取APP文件目录下的cache文件家
     * @return
     */
    private String getDefaultSaveRootPath(Context applicion) {

        if (applicion.getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            //noinspection ConstantConditions
            return applicion.getExternalCacheDir().getAbsolutePath();
        }
    }
    private void printLog(String tag ,String logMessage){
        if (DEBUG){
            Log.d(tag,logMessage);
        }
    }
}