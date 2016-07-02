package com.fanxin.app.main.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fanxin.app.R;

public class FragmentFind  extends Fragment{
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_find, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	 
		getView().findViewById(R.id.re_friends).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
//                      String userID=MYApplication.getInstance().getUserName();
//                      if(!TextUtils.isEmpty(userID)){
//
//                          startActivity(new Intent(getActivity(),SocialMainActivity.class).putExtra("userID", userID));
//
//                      }
            }
            
            
        });
    
		}
	
	
	 
}
