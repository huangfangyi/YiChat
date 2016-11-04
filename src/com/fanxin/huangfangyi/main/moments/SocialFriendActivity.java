package com.fanxin.huangfangyi.main.moments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.main.utils.PathUtils;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.domain.EaseUser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SocialFriendActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    private String imageName;
    private PullToRefreshListView pull_refresh_list;
    private List<JSONObject> articles = new ArrayList<JSONObject>();

    // private JSONArray datas = new JSONArray();
    private SocialFriendAdapter adapter;
    private ListView actualListView;
    private int page = 0;
    
    String userID;
    List<String> sIDs = new ArrayList<String>();
    String friendID;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        setContentView(R.layout.fx_activity_moments_me);
        userID = DemoHelper.getInstance().getCurrentUsernName();

        System.out.println("上传数据------->>>>>>>>" + "userID" + ":" + userID);

        friendID = this.getIntent().getStringExtra("friendID");
        // if(friendID==null){
        // finish();
        // return;
        // }
        TextView tv_title = (TextView) this.findViewById(R.id.tv_title);
        // 此处应该换成昵称

        String nick_temp = friendID;
        if (friendID.equals(userID)) {
            nick_temp = "我";

        } else {

            EaseUser user = DemoHelper.getInstance().getContactList()
                    .get(friendID);
            if (user != null) {
                nick_temp = user.getNick();
            }
        }

        tv_title.setText(nick_temp + " 的个人相册");
        initView();
    }

    private void initView() {

        pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pull_refresh_list.setMode(PullToRefreshBase.Mode.BOTH);

        pull_refresh_list
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                SocialFriendActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        // Update the LastUpdatedLabel
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);

                        // Do work to refresh the list here.

                        if (pull_refresh_list.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                            page = 0;

                        } else if (pull_refresh_list.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                            page++;

                        }

                        getData(page);
                    }
                });

        actualListView = pull_refresh_list.getRefreshableView();
        adapter = new SocialFriendAdapter(SocialFriendActivity.this, articles);
        actualListView.setAdapter(adapter);
        actualListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position != 0) {
                    Log.e("position----->>", String.valueOf(position));
                    JSONObject json = adapter.getJSONs().get(position - 1);
                    startActivity(new Intent(SocialFriendActivity.this,
                            SocialDetailActivity.class).putExtra("json",
                            json.toJSONString()));
                }
            }

        });
        getData(0);
        pull_refresh_list.setRefreshing(false);
        ImageView iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }

        });

    }


    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.fx_dialog_social_main);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("拍照");
        tv_paizhao.setOnClickListener(new OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {

                imageName = getNowTime() + ".jpg";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File("/sdcard/bizchat/", imageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("相册");
        tv_xiangce.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                getNowTime();
                imageName = getNowTime() + ".jpg";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                dlg.cancel();
            }
        });

    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            String path = null;

            switch (requestCode) {

                case PHOTO_REQUEST_TAKEPHOTO:
                    path = "/sdcard/bizchat/" + imageName;
                    break;

                case PHOTO_REQUEST_GALLERY:

                    if (data != null) {
                        path = PathUtils.getPath(SocialFriendActivity.this,data.getData());
                        System.out.println(path);
                    }

                    break;

            }

            Intent intent = new Intent();
            intent.putExtra("imagePath", path);

            intent.setClass(SocialFriendActivity.this,
                    MomentsPublishActivity.class);
            startActivity(intent);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void getData(final int page_num) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("userID", userID));
        params.add(new Param("friendID", friendID));
        params.add(new Param("num", page_num + ""));
        OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_FRIEND, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                pull_refresh_list.onRefreshComplete();
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    JSONArray users_temp = jsonObject.getJSONArray("data");
                    String time = jsonObject.getString("time");
                    DemoApplication.getInstance().setTime(time);
                    if (page_num == 0) {

                        // datas = users_temp;
                        articles.clear();
                        sIDs.clear();
                        for (int i = 0; i < users_temp.size(); i++) {
                            JSONObject json = users_temp.getJSONObject(i);
                            String sID = json.getString("sID");
                            sIDs.add(sID);
                            articles.add(json);
                        }

                    } else {

                        Map<String, JSONObject> map = new HashMap<String, JSONObject>();

                        for (int i = 0; i < users_temp.size(); i++) {
                            JSONObject json = users_temp.getJSONObject(i);
                            String sID = json.getString("sID");
                            if (!sIDs.contains(sID)) {
                                sIDs.add(sID);
                                articles.add(json);
                            }
                        }

                    }
                    // adapter = new
                    // SocialFriendAdapter(SocialFriendActivity.this,
                    // datas, time);
                    // actualListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // ACache.get(getActivity()).put("last_login", users);

                } else {
                    // ToastUtil.showMessage("服务器出错...");
                }

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getData(0);
    }
    
}
