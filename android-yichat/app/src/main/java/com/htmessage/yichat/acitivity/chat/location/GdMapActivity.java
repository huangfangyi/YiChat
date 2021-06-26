package com.htmessage.yichat.acitivity.chat.location;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

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
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：fanxinht
 * 类描述：GdMapActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/29 11:34
 * 邮箱:814326663@qq.com
 */
public class GdMapActivity extends BaseActivity implements AMapLocationListener, LocationSource, PoiSearch.OnPoiSearchListener, AMap.OnCameraChangeListener {
    private MapView amapView;
    private AMap aMap;//地图对象
    private Button sendButton = null;
    static AMapLocation lastLocation = null;
    private EditText et_search;
    //根据中心位显示的热点
    private List<LocationBean> beanList = new ArrayList<>();
    //根据关键字显示的热点
    private List<LocationBean> beanListKeywords = new ArrayList<>();
    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器
    private LocationAdapter adapter;
    private LocationSearchAdapter searchAdapter;
    private ListView lwKeywords;
    private ListView lwPoi;
    private boolean isMove = true;
    private LinearLayout ll_search;
    private int currentPage = 0;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_gd_map);
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

    private void refreshList(List<LocationBean> beanList) {
        adapter = new LocationAdapter(beanList, this);
        lwPoi.setAdapter(adapter);
    }

    private void refreshSearchList(List<LocationBean> beanList) {
        searchAdapter = new LocationSearchAdapter(beanList, this);
        lwKeywords.setAdapter(searchAdapter);
    }

    private void initPoi(String keyword, String cityCode, double latitudePoi, double longitudePoi) {
        String type = "商务住宅|餐饮服务|生活服务";
        if (!TextUtils.isEmpty(keyword)) {
            type = "";
        }
        PoiSearch.Query query = new PoiSearch.Query(keyword, type, cityCode);
//keyWord表示搜索字符串，
//第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
//cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(40);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        if (longitudePoi != 0 && latitudePoi != 0) {
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitudePoi, longitudePoi), 1000));//设置周边搜索的中心点以及半径
        }
        poiSearch.searchPOIAsyn();
    }

    private void initView(Bundle arg0) {
        //显示地图
        amapView = (MapView) findViewById(R.id.amapView);
        //必须要写
        amapView.onCreate(arg0);
        sendButton = (Button) findViewById(R.id.btn_location_send);
        et_search = (EditText) findViewById(R.id.et_search);
        lwKeywords = (ListView) findViewById(R.id.lw_keywords);
        lwPoi = (ListView) findViewById(R.id.lv_poi);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_search.getVisibility() != View.VISIBLE) {
                    et_search.getText().clear();
                    beanListKeywords.clear();
                    searchAdapter.notifyDataSetChanged();
                    ll_search.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keyWord) {
        String type = "商务住宅|餐饮服务|生活服务";
        if (!TextUtils.isEmpty(keyWord)) {
            type = "";
        }
        query = new PoiSearch.Query(keyWord, type, null);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);//设置不限制城市
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    private void initData() {
        //获取地图对象
        aMap = amapView.getMap();
        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(false);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);

        //地图移动监听
        aMap.setOnCameraChangeListener(this);
        //地图触摸监听
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                isMove = true;
            }
        });

        //  aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
////        //定位的小图标 默认是蓝点 这里自定义一团火，其实就是一张图片
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
        showProgross();
        startLocation();
        refreshList(beanList);
        refreshSearchList(beanListKeywords);
    }

    private void setListener() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !TextUtils.isEmpty(s)) {
                    String newText = s.toString().trim();
                    doSearchQuery(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lwKeywords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationBean item = searchAdapter.getItem(position);
                lastLocation.setAddress(item.getAddress());
                lastLocation.setLatitude(item.getLatitude());
                lastLocation.setLongitude(item.getLongtitude());
                if (ll_search.getVisibility() == View.VISIBLE) {
                    ll_search.setVisibility(View.GONE);
                }
                isMove = true;
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(item.getLatitude(), item.getLongtitude())));
                et_search.getText().clear();
            }
        });
        lwPoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isMove = false;
                RadioButton mRB = (RadioButton) view.findViewById(R.id.rb_check);
//                //每次选择一个item时都要清除所有的状态，防止出现多个被选中
                adapter.clearStates(position);
                mRB.setChecked(adapter.getStates(position));
                LocationBean item = adapter.getItem(position);
                //刷新数据，调用getView刷新ListView
                adapter.notifyDataSetChanged();
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(item.getLatitude(), item.getLongtitude())));

                lastLocation.setAddress(item.getAddress());
                lastLocation.setLatitude(item.getLatitude());
                lastLocation.setLongitude(item.getLongtitude());
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation == null) {
            return;
        }
        sendButton.setEnabled(true);
        if (lastLocation != null) {
            if (lastLocation.getLatitude() == amapLocation.getLatitude() && lastLocation.getLongitude() == amapLocation.getLongitude()) {
                Log.d("map", "same location, skip refreshALL");
                // mMapView.refreshALL(); //need this refreshALL?
                return;
            }
        }

        dismissDialog();
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
//          aMap.clear();
//          //设置缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            //将地图移动到定位点
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
            //点击定位按钮 能够将地图的中心移动到定位点
            mListener.onLocationChanged(amapLocation);
            initPoi(amapLocation.getPoiName(), amapLocation.getCityCode(), amapLocation.getLatitude(), amapLocation.getLongitude());
            //获取定位信息
            StringBuffer buffer = new StringBuffer();
            buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
            Log.d("AmapError", "AmapError:" + buffer.toString());
            //
            stopLocation();
        } else {
            CommonUtils.showToastShort(GdMapActivity.this, R.string.location_failed);
            stopLocation();
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

    private void showProgross() {
        CommonUtils.showDialogNumal(this, getString(R.string.Making_sure_your_location));
    }

    private void dismissDialog() {
        CommonUtils.cencelDialog();
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
        isMove = true;
        stopLocation();
        destroyLocation();
        dismissDialog();
        amapView.onDestroy();
    }

    public void sendLocation(View view) {
        CommonUtils.showDialogNumal(GdMapActivity.this, getString(R.string.are_doing));
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {

            @Override
            public void onMapScreenShot(Bitmap bitmap) {
                bitmap = cropBitmap(bitmap);
                File file1 = new File(HTApp.getInstance().getDirFilePath()+"/location/");
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File file = new File(file1.getAbsolutePath().toString() + "/" + DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                    GdMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                        }
                    });
                    Intent intent = GdMapActivity.this.getIntent();
                    intent.putExtra("latitude", lastLocation.getLatitude());
                    intent.putExtra("longitude", lastLocation.getLongitude());
                    intent.putExtra("address", lastLocation.getAddress());
                    intent.putExtra("thumbnailPath", file.getAbsolutePath());
                    GdMapActivity.this.setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } catch (FileNotFoundException e) {
                    GdMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                        }
                    });
                    e.printStackTrace();
                } catch (IOException e) {
                    GdMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
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

    /**
     * 按长方形裁切图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int nw, nh, retX, retY;

        nw = w;
        nh = w / 2;
        retX = 0;
        retY = (h / 2) - (w / 4);


        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (poiResult != null && !poiResult.getPois().isEmpty()) {
                ArrayList<PoiItem> poiItems = poiResult.getPois();
                List<LocationBean> locationBeanList = new ArrayList<>();
                for (PoiItem poitem : poiItems) {
                    poitem.getTitle();//标识
                    poitem.getSnippet();//地址位置
                    LocationBean bean = new LocationBean();
                    bean.setTitle(poitem.getTitle());
                    bean.setAddress(poitem.getSnippet());
                    bean.setLatitude(poitem.getLatLonPoint().getLatitude());
                    bean.setLongtitude(poitem.getLatLonPoint().getLongitude());
                    if (!locationBeanList.contains(bean)) {
                        locationBeanList.add(bean);
                    }
                }
                if (ll_search.getVisibility() == View.VISIBLE) {
                    beanListKeywords.clear();
                    beanListKeywords.addAll(locationBeanList);
                    searchAdapter.notifyDataSetChanged();
                } else {
                    if (isMove) {
                        beanList.clear();
                        beanList.addAll(locationBeanList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        Log.d("PoiResult---->22", poiItem.toString());
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("PoiResult---->33", cameraPosition.toString());
    }

    Marker marker;

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (marker != null) {
            aMap.clear();
            marker.destroy();
        }
        MarkerOptions options = getMarkerOptions(cameraPosition.target.latitude, cameraPosition.target.longitude, "");
        marker = aMap.addMarker(options);
        initPoi("", "", cameraPosition.target.latitude, cameraPosition.target.longitude);
    }

    public void back2(View view) {
        if (ll_search.getVisibility() != View.GONE) {
            et_search.getText().clear();
            ll_search.setVisibility(View.GONE);
            beanListKeywords.clear();
            searchAdapter.notifyDataSetChanged();
        }
    }
}
