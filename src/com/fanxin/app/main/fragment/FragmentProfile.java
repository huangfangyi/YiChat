package com.fanxin.app.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanxin.app.R;
import com.fanxin.app.main.activity.ProfileActivity;
import com.fanxin.app.main.activity.SettingsActivity;

public class FragmentProfile extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.re_myinfo).setOnClickListener(this);
        getView().findViewById(R.id.re_setting).setOnClickListener(this);
    }




    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.re_myinfo:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;

            case R.id.re_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;

        }
    }
}
