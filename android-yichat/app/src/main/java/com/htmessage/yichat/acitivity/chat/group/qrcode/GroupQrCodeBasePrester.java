package com.htmessage.yichat.acitivity.chat.group.qrcode;

import com.htmessage.yichat.acitivity.BasePresenter;

/**
 * 项目名称：fanxinht
 * 类描述：GroupQrCodeBasePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:36
 * 邮箱:814326663@qq.com
 */
public interface GroupQrCodeBasePrester extends BasePresenter {
    void onDestory();
    void CreateQrCode();
    void saveQrCode();
}
