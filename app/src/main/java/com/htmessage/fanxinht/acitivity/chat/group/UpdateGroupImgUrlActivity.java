package com.htmessage.fanxinht.acitivity.chat.group;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.widget.HTAlertDialog;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by huangfangyi on 2017/1/13.
 * qq 84543217
 */

public class UpdateGroupImgUrlActivity extends BaseActivity {
    private ImageView ivGroup;


    private String groupId;
    private TextView tvSave;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private HTGroup htGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_update);
        groupId = this.getIntent().getStringExtra("groupId");

        if (groupId == null || HTClient.getInstance().groupManager().getGroup(groupId) == null) {
            finish();
            return;
        }
        htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        initView();
        initData();
    }

    private void initView() {
        ivGroup = (ImageView) this.findViewById(R.id.iv_group);

        tvSave = (TextView) this.findViewById(R.id.tv_save);
        tvSave.setEnabled(false);

    }

    private void initData() {

        Glide.with(getApplicationContext()).load(htGroup.getImgUrl()).placeholder(R.drawable.default_group).into(ivGroup);


        ivGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String groupNewName = etGroupName.getText().toString().trim();
//                String groupNewDesc = etGroupDesc.getText().toString().trim();
                File file = new File(Environment.getExternalStorageDirectory() + "/" + imageName);
                //如果头像没有更新,昵称也没有更新,ruturn
                if ((TextUtils.isEmpty(imageName) || !file.exists())) {
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(UpdateGroupImgUrlActivity.this);
                progressDialog.setMessage(getString(R.string.are_uploading));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                HTClient.getInstance().groupManager().updateGroupImgUrlLocal(groupId, file.getAbsolutePath(), HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.update_groups_failed, Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });

//                if (!TextUtils.isEmpty(imageName) && new File(Environment.getExternalStorageDirectory() + "/" + imageName).exists()) {
//                 uploadAvatar  (Environment.getExternalStorageDirectory() + "/" + imageName, progressDialog,groupNewName,groupNewDesc);
//                } else {
//                    updateGroupInfo(groupNewName,groupNewDesc,htGroup.getImgUrl(),progressDialog);
//                }

            }
        });
    }

//    private void uploadAvatar(String filePath, final ProgressDialog pd, final String groupName, final String groupDesc) {
//
//        final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
//        final OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(OSSConstant.accessKeyId, OSSConstant.accessKeySecret);
//        final ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
//        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
//        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
//        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//        OSSLog.enableLog();
//        OSSClient oss = new OSSClient(getApplicationContext(), OSSConstant.endpoint, credentialProvider, conf);
//
////        new UploadFileUtils(oss, OSSConstant.bucket, fileName, filePath).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
////            @Override
////            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
////
////            }
////
////            @Override
////            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
////                final String url = OSSConstant.baseUrl + fileName;
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                    //    pd.dismiss();
////                        updateGroupInfo(groupName,groupDesc,url,pd);
////                    }
////                });
////            }
////
////            @Override
////            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        pd.dismiss();
////                        Toast.makeText(getApplicationContext(),"上传图片失败",Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
////        });
//    }

    private String imageName;

    private void showUpdateDialog() {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(UpdateGroupImgUrlActivity.this, null, new String[]{"拍照", "相册"});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        imageName = System.currentTimeMillis() + ".png";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName)));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        break;
                    case 1:
                        imageName = System.currentTimeMillis() + ".png";
                        Crop.pickImage(UpdateGroupImgUrlActivity.this, PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:
                    beginCrop(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName)));
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        beginCrop(data.getData());
                    break;

                case PHOTO_REQUEST_CUT:
                    Uri output = Crop.getOutput(data);
                    Glide.with(getApplicationContext()).load(output.getPath()).into(ivGroup);
                    tvSave.setEnabled(true);
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName));
        Crop.of(source, destination).asSquare().start(UpdateGroupImgUrlActivity.this, PHOTO_REQUEST_CUT);
    }


//    private void updateGroupInfo(String groupNewName, String groupDesc, String imageUrl, final ProgressDialog progressDialog) {
//
//        HTClient.getInstance().groupManager().updateGroupInfo(groupId, groupNewName, groupDesc, imageUrl, new GroupManager.CallBack() {
//            @Override
//            public void onSuccess(String data) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "更新群资料成功", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure() {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "更新群资料失败", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }


}
