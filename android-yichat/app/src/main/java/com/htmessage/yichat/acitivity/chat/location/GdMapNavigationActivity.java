package com.htmessage.yichat.acitivity.chat.location;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.MapUtils;
import com.htmessage.yichat.widget.HTAlertDialog;

/**
 * 项目名称：fanxinht
 * 类描述：GdMapNavigationActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/12/12 11:53
 * 邮箱:814326663@qq.com
 */
public class GdMapNavigationActivity extends BaseActivity implements AMapLocationListener, LocationSource {
    private MapView amapView;
    private AMap aMap;//地图对象
    static AMapLocation lastLocation = null;

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器

    private String fromLat, fromLng;
    private String fromAddress;
    private String type;
    private double latitude = 0;
    private TextView tv_title, tv_address_title, tv_address;
    private ImageView iv_navigation;
    private double longtitude;
    private String address;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_gdmap_navigation);
        getData();
        initView(arg0);
        initData();
        setListener();
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    private void getData() {
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longtitude = getIntent().getDoubleExtra("longitude", 0);
        address = getIntent().getStringExtra("address");
        type = getIntent().getStringExtra("type");
    }

    private void initView(Bundle arg0) {
        //显示地图
        amapView = (MapView) findViewById(R.id.amapView);
        //必须要写
        amapView.onCreate(arg0);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.location_message);
        tv_address_title = (TextView) findViewById(R.id.tv_address_title);
        tv_address = (TextView) findViewById(R.id.tv_address);
        iv_navigation = (ImageView) findViewById(R.id.iv_navigation);
    }

    private void initData() {
        tv_address_title.setText(R.string.location_message);
        tv_address.setText(address);
        //获取地图对象
        aMap = amapView.getMap();
        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        settings.setZoomControlsEnabled(false);
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(false);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
//        //定位的小图标 默认是蓝点 这里自定义一团火，其实就是一张图片
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
        //初始化定位
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        if (latitude == 0 || longtitude == 0 || TextUtils.isEmpty(address)) {
            finish();
            return;
        } else {
            //设置缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            //将地图移动到定位点
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longtitude)));
            //添加图钉
            MarkerOptions options = getMarkerOptions(latitude, longtitude, address);
            aMap.addMarker(options);
        }
    }

    private void setListener() {
        iv_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation();
                showCityPup(fromLat, fromLng, fromAddress, String.valueOf(latitude), String.valueOf(longtitude), address);
            }
        });

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation == null) {
            return;
        }
        if (lastLocation != null) {
            if (lastLocation.getLatitude() == amapLocation.getLatitude() && lastLocation.getLongitude() == amapLocation.getLongitude()) {
                Log.d("map", "same location, skip refreshALL");
                // mMapView.refreshALL(); //need this refreshALL?
                return;
            }
        }
        lastLocation = amapLocation;
        if (amapLocation.getErrorCode() == 0) {
            //定位成功回调信息，设置相关消息
            amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
            amapLocation.getAccuracy();//获取精度信息
            amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
            amapLocation.getCountry();//国家信息
            amapLocation.getProvince();//省信息
            amapLocation.getCity();//城市信息
            amapLocation.getDistrict();//城区信息
            amapLocation.getStreet();//街道信息
            amapLocation.getStreetNum();//街道门牌号信息
            amapLocation.getCityCode();//城市编码
            amapLocation.getAdCode();//地区编码
            if (latitude != 0 || longtitude != 0 || !TextUtils.isEmpty(address)) {
                fromLat = String.valueOf(amapLocation.getLatitude());//获取纬度
                fromLng = String.valueOf(amapLocation.getLongitude());//获取经度
                fromAddress = amapLocation.getAddress();
            } else {
                //设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                //将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(amapLocation);
            }
            stopLocation();
            //获取定位信息
            StringBuffer buffer = new StringBuffer();
            buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
            Log.d("AmapError", "AmapError:" + buffer.toString());
        } else {
            if (latitude == 0) {
                CommonUtils.showToastShort(GdMapNavigationActivity.this, R.string.location_failed);
            }
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + amapLocation.getErrorCode() + ", errInfo:"
                    + amapLocation.getErrorInfo());

        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        amapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        amapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        amapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
        destroyLocation();
        amapView.onDestroy();
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(double Latitude, double Longitude, String address) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka));
        //位置
        options.position(new LatLng(Latitude, Longitude));
        StringBuffer buffer = new StringBuffer();
        buffer.append(address);
        //标题
        options.title(buffer.toString());
        //子标题
//        options.snippet("这里好火");
        //设置多少帧刷新一次图片资源
        options.period(60);
        return options;
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) {
                // 停止定位
                mLocationClient.stopLocation();
            }
        }
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    private void showCityPup(final String fromLat, final String fromLng, final String fromAddress, final String activityLat, final String activityLng, final String address) {
        HTAlertDialog dialog = new HTAlertDialog(GdMapNavigationActivity.this, getString(R.string.navigation), new String[]{getString(R.string.bd_map), getString(R.string.gd_map), getString(R.string.gg_map), getString(R.string.tc_map), getString(R.string.cancel)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        MapUtils.openBaiduMap(GdMapNavigationActivity.this, activityLat, activityLng, address);
                        break;
                    case 1:
                        MapUtils.openGDMap(GdMapNavigationActivity.this, activityLat, activityLng, address);
                        break;
                    case 2:
                        MapUtils.openGoogleMap(GdMapNavigationActivity.this, fromLat, fromLng, activityLat, activityLng);
                        break;
                    case 3:
                        MapUtils.openTencentMap(GdMapNavigationActivity.this, fromAddress, fromLat, fromLng, activityLat, activityLng, address);
                        break;
                    case 4:

                        break;
                    default:

                        break;
                }
            }
        });
    }
}
