package com.htmessage.fanxinht.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.htmessage.fanxinht.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：MapTest
 * 类描述：MapUtils 描述:
 * 创建人：songlijie
 * 创建时间：2017/4/10 10:32
 * 邮箱:814326663@qq.com
 */
public class MapUtils {
    private static String TAG = MapUtils.class.getSimpleName();
    /**
     * 打开高德地图并导航
     *
     * @param context 上下文的对象
     * @param lat     到达的经度
     * @param lng     到达的维度
     */
    public static void openGDMap(Context context, String lat, String lng, String toAddress) {
        Log.e(TAG,"打开高德地图传过来的经纬度:lat:"+lat +"--lng:"+lng+"--地址:"+toAddress);
        Intent intent;
        if (isAvilible(context, "com.autonavi.minimap")) {
            try {
                String appName = context.getString(R.string.app_name);
                intent = Intent.getIntent("androidamap://navi?sourceApplication=" + appName + "&poiname=" + toAddress + "&lat=" + lat + "&lon=" + lng + "&dev=0");
                context.startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "您尚未安装高德地图,请下载后再开启导航!", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    /**
     * 打开google地图并导航
     *
     * @param context 上下文对象
     * @param lat     目的地经度
     * @param lng     目的地维度
     */
    public static void openGoogleMap(Context context, String lat, String lng, String toAddress) {
        if (isAvilible(context, "com.google.android.apps.maps")) {
//            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng + ","+toAddress);
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
            double[] toDoubles = map_bd2hx(Double.valueOf(lat), Double.valueOf(lng));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=" + toDoubles[0] + "," + toDoubles[1] + "(" + toAddress + ")"));
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "您尚未安装谷歌地图,请下载后再开启导航!", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.google.android.apps.maps");
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    /**
     * 打开google地图并导航
     *
     * @param context 上下文对象
     * @param formlat 起点经度
     * @param fromlng 起点维度
     * @param tolat   目的地经度
     * @param tolng   目的地维度
     */
    public static void openGoogleMap(Context context, String formlat, String fromlng, String tolat, String tolng) {
        Log.e(TAG,"打开谷歌地图传过来的经纬度:formlat:"+formlat +"--fromlng:"+fromlng+"--目的地tolat:"+tolat+"---tolng:"+tolng);
        if (isAvilible(context, "com.google.android.apps.maps")) {
            double[] fromDoubles = map_bd2hx(Double.valueOf(formlat), Double.valueOf(fromlng));
            double[] toDoubles = map_bd2hx(Double.valueOf(tolat), Double.valueOf(tolng));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + fromDoubles[0] + "," + fromDoubles[1] + "&daddr=" + toDoubles[0] + "," + toDoubles[1]));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您尚未安装谷歌地图,请下载后再开启导航!", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.google.android.apps.maps");
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    /**
     * 打开百度地图并导航
     *
     * @param context 上下文对象
     * @param lat     目的地经度
     * @param lng     目的地维度
     */
    public static void openBaiduMap(Context context, String lat, String lng, String toAddress) {
        Log.e(TAG,"打开百度地图传过来的经纬度:lat:"+lat +"--lng:"+lng+"--地址:"+toAddress);
        Intent intent;
        if (isAvilible(context, "com.baidu.BaiduMap")) {//传入指定应用包名
            try {
                String appName = context.getString(R.string.app_name);
                intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:" + lat + "," + lng + "|name:" + toAddress +        //终点
                        //mode 为导航方式
                        "&mode=driving&region=&src=" + appName + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                context.startActivity(intent); //启动调用
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
        } else {//未安装

            //market为路径，id为包名
            //显示手机上所有的market商店
            Toast.makeText(context, "您尚未安装百度地图,请下载后再开启导航!", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    /**
     * 打开腾讯地图并导航
     *
     * @param context   上下文的对象
     * @param fromlat   起点经度
     * @param fromlng   起点维度
     * @param toAddress 重点地址
     */
    public static void openTencentMap(Context context, String fromlat, String fromlng, String toAddress) {
        Intent intent;
        if (isAvilible(context, "com.tencent.map")) {//传入指定应用包名
            try {
                intent = new Intent();
                String appName = context.getString(R.string.app_name);
                String url = "http://apis.map.qq.com/uri/v1/routeplan?type=drive&from=&fromcoord="
                        + fromlat + "," + fromlng
                        + "&to=" + toAddress + "&tocoord=&policy=0&referer=" + appName;
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                context.startActivity(intent); //启动调用
            } catch (Exception e) {
                Log.e("intent", e.getMessage());
            }
        } else {//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            Toast.makeText(context, "您尚未安装腾讯地图,请下载后再开启导航!", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.tencent.map");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }

    /**
     * 打开腾讯地图并导航
     *
     * @param context   上下文的对象
     * @param fromAddress 起点位置
     * @param fromlat   起点经度
     * @param fromlng   起点维度
     * @param tolat   终点经度
     * @param tolng   终点维度
     * @param toAddress 终点地址
     */
    public static void openTencentMap(Context context, String fromAddress, String fromlat, String fromlng, String tolat, String tolng, String toAddress) {
        Log.e(TAG,"打开腾讯地图传过来的经纬度:formlat:"+fromlat +"--fromlng:"+fromlng+"----起点:"+fromAddress+"--目的地tolat:"+tolat+"---tolng:"+tolng+"---终点:"+toAddress);
        Intent intent;
        if (isAvilible(context, "com.tencent.map")) {//传入指定应用包名
            try {
                intent = new Intent();
                double[] fromDoubles = map_bd2hx(Double.valueOf(fromlat), Double.valueOf(fromlng));
                double[] toDoubles = map_bd2hx(Double.valueOf(tolat), Double.valueOf(tolng));
                String appName = context.getString(R.string.app_name);
                String url = "qqmap://map/routeplan?type=drive&from=" + fromAddress + "&fromcoord="
                        + fromDoubles[0] + "," + fromDoubles[1] + "&to=" + toAddress + "&tocoord=" + toDoubles[0] + "," + toDoubles[1] + "&policy=0&referer=" + appName;
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                context.startActivity(intent); //启动调用
            } catch (Exception e) {
                Log.e("intent", e.getMessage());
            }
        } else {//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            Toast.makeText(context, "您尚未安装腾讯地图,请下载后再开启导航!", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("market://details?id=com.tencent.map");
//            intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }
    }
    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 坐标转换，腾讯地图/google/高德（火星坐标）转换成百度地图坐标
     *
     * @param lat 腾讯纬度
     * @param lon 腾讯经度
     * @return 返回结果：经度,纬度
     */
    public static double[] map_hx2bd(double lat, double lon) {
        double bd_lat;
        double bd_lon;
        double x_pi = 3.141592653589793 * 3000.0 / 180.0;//3.14159265358979324
        double x = lon, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        bd_lon = z * Math.cos(theta) + 0.0065;
        bd_lat = z * Math.sin(theta) + 0.006;
        double[] doubles = new double[]{bd_lat, bd_lon};
        return doubles;
    }


    /**
     * 坐标转换，百度地图坐标转换成腾讯/google/高德地图坐标
     *
     * @param lat 百度坐标纬度
     * @param lon 百度坐标经度
     * @return 返回结果：纬度,经度
     */
    public static double[] map_bd2hx(double lat, double lon) {
        double tx_lat;
        double tx_lon;
        double x_pi = 3.141592653589793 * 3000.0 / 180.0;//3.14159265358979324
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        tx_lon = z * Math.cos(theta);
        tx_lat = z * Math.sin(theta);
        double[] doubles = new double[]{tx_lat, tx_lon};
        return doubles;
    }
}
