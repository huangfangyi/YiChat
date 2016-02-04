package com.fanxin.app.comments;

 

import com.fanxin.app.R;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ClickTextView extends ClickableSpan {
	private String clickString;
	private Context context;

	public ClickTextView(Context context, String clickString) {
		this.clickString = clickString;
		this.context = context;
	}

	@Override
	public void updateDrawState(TextPaint ds) {

		ds.setColor(context.getResources().getColor(
				R.color.text_color));

		ds.setUnderlineText(false); // 去掉下划线
	}

	@Override
	public void onClick(View widget) {

		if (widget instanceof TextView) {
			((TextView) widget).setHighlightColor(context.getResources().getColor(
					android.R.color.transparent));
		}
		 
		 
		Toast.makeText(context, clickString, Toast.LENGTH_LONG).show();

	}

}