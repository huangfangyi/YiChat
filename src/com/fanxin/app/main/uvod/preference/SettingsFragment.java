package com.fanxin.app.main.uvod.preference;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.fanxin.app.R;


public class SettingsFragment extends PreferenceFragmentCompat{
    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }
}
