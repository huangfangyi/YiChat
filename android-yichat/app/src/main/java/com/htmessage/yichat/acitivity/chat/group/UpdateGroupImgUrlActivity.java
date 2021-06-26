package com.htmessage.yichat.acitivity.chat.group;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.data.GroupInfoManager;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by huangfangyi on 2017/1/13.
 * qq 84543217
 */

public class UpdateGroupImgUrlActivity extends BaseActivity {
    private ImageView ivGroup;
    private String imageName;
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
        if (GroupInfoManager.getInstance().isManager(groupId)) {
            tvSave.setVisibility(View.VISIBLE);
        } else {
            tvSave.setVisibility(View.GONE);
        }
    }

    private void initData() {
        String imgUrl = htGroup.getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)) {
            if (!imgUrl.startsWith("http") || !imgUrl.contains(HTConstant.baseImgUrl)) {
                imgUrl = HTConstant.baseImgUrl + imgUrl;
            }
        }
        CommonUtils.loadGroupAvatar(getBaseContext(), imgUrl, ivGroup);
        ivGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!GroupInfoManager.getInstance().isManager(groupId)) {
                         CommonUtils.showToastShort(getBaseContext(), R.string.change_group_info_just_owner);
                        return;

                }
                showUpdateDialog();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(HTApp.getInstance().getImageDirFilePath() + "/" + imageName);
                //如果头像没有更新,昵称也没有更新,ruturn
                if ((TextUtils.isEmpty(imageName) || !file.exists())) {
                    return;
                }
                CommonUtils.showDialogNumal(UpdateGroupImgUrlActivity.this, getString(R.string.are_uploading));
                HTClient.getInstance().groupManager().updateGroupImgUrlLocal(groupId, file.getAbsolutePath(), HTApp.getInstance().getUserNick(), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.update_success);
                                finish();

                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.update_groups_failed);

                            }
                        });
                    }

                    @Override
                    public void onHTMessageSend(HTMessage htMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.upLoadMessage(htMessage);
                                LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

                            }
                        });
                    }
                });
            }
        });
    }


    private void showUpdateDialog() {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(UpdateGroupImgUrlActivity.this, null, new String[]{getString(R.string.attach_take_pic), getString(R.string.image_manager)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        imageName = System.currentTimeMillis() + ".png";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(HTApp.getInstance().getImageDirFilePath(), imageName)));
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
                    beginCrop(Uri.fromFile(new File(HTApp.getInstance().getImageDirFilePath(), imageName)));
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
        Uri destination = Uri.fromFile(new File(HTApp.getInstance().getImageDirFilePath(), imageName));
        Crop.of(source, destination).asSquare().start(UpdateGroupImgUrlActivity.this, PHOTO_REQUEST_CUT);
    }
}
