package com.fanxin.huangfangyi.main.uvod.ui.base;


import android.content.Context;


import com.fanxin.huangfangyi.R;
import com.ucloud.player.widget.v2.UVideoView;

import java.util.List;

/**
 * Created by leewen on 2015/8/11.
 */
public class UMenuItemHelper {
    private static final String TAG = "UMenuItemHelper";
    private static UMenuItem mMainMenuItem;
    private static UMenuItemHelper instance;
    private static Context mContext;
    private UMenuItemHelper(Context context) {
        mContext = context;
        mMainMenuItem = new UMenuItem.Builder().title(mContext.getResources().getString(R.string.menu_main_title))
                .index(0)
                .builder();
    }

    public static UMenuItemHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (UMenuItemHelper.class) {
                if (instance == null) {
                    instance = new UMenuItemHelper(context);
                }
            }
        }
        return instance;
    }

    private String get(String[] names, String types[], String type) {
        for(int i =0; i < types.length; i++) {
            if (type.equalsIgnoreCase(types[i])) {
                return names[i];
            }
        }
        return type;
    }

   public UMenuItem buildVideoDefinitationMenuItem(List<UVideoView.DefinitionType> definitions, int index) {
        UMenuItem menuItem = new UMenuItem.Builder().title(mContext.getResources().getString(R.string.menu_item_title_definition)).index(index).builder();
        String[] retNames = mContext.getResources().getStringArray(R.array.pref_definition_names);
        String[] retValues = mContext.getResources().getStringArray(R.array.pref_definition_values);
        for(UVideoView.DefinitionType definition: definitions) {
            String title = get(retNames, retValues, definition.name());
            menuItem.childs.add(new UMenuItem.Builder().title(title).type(title).parent(menuItem).builder());
        }
        return menuItem;
    }

    public UMenuItem buildVideoDecoderMenuItem(int defaultSelect) {
       return buildVideoMenuItem(
               mContext.getResources().getString(R.string.menu_item_title_decoder),
               R.array.pref_decoder_names,
               R.array.pref_decoder_values,
               defaultSelect);
    }

    public UMenuItem buildVideoRatioMenuItem(int defaultSelect) {
        return buildVideoMenuItem(
                mContext.getResources().getString(R.string.menu_item_title_ratio),
                R.array.pref_screen_ratio_names,
                R.array.pref_screen_ratio_values,
                defaultSelect);
    }

    public UMenuItem buildVideoMenuItem(String title, int resNameId, int resValueId, int defaultSelect) {
        UMenuItem menuItem = new UMenuItem.Builder().title(title).index(defaultSelect).builder();
        String[] retNames = mContext.getResources().getStringArray(resNameId);
        String[] types = mContext.getResources().getStringArray(resValueId);
        for(int i = 0; i < retNames.length; i++) {
            menuItem.childs.add(new UMenuItem.Builder().title(retNames[i]).type(types[i] + "").parent(menuItem).builder());
        }
        return menuItem;
    }

    public UMenuItem register(UMenuItem child) {
        if (mMainMenuItem != null && !mMainMenuItem.childs.contains(child)) {
            mMainMenuItem.childs.add(child);
        }
        return mMainMenuItem;
    }

    public UMenuItem register(UMenuItem child, int location) {
        if (mMainMenuItem != null && !mMainMenuItem.childs.contains(child)) {
            mMainMenuItem.childs.add(location, child);
        }
        return mMainMenuItem;
    }

    public UMenuItem unRegister(UMenuItem child) {
        if (mMainMenuItem != null && mMainMenuItem.childs.contains(child)) {
            mMainMenuItem.childs.remove(child);
        }
        return mMainMenuItem;
    }

    public UMenuItem getMainMenu() {
        return mMainMenuItem;
    }

    public void release() {
        if (mMainMenuItem != null && mMainMenuItem.childs != null) {
            mMainMenuItem.childs.clear();
            instance = null;
            mMainMenuItem = null;
        }
    }
}
