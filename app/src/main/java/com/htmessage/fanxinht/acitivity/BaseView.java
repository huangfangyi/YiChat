package com.htmessage.fanxinht.acitivity;

import android.app.Activity;
import android.content.Context;

/**
 * Created by huangfangyi on 2017/6/21.
 * qq 84543217
 */

public interface BaseView<T> {
    void setPresenter(T presenter);
    Context getBaseContext();
    Activity getBaseActivity();
}
