package com.htmessage.yichat.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * 可伸展的textview
 */
public class StretchTextView extends TextView implements View.OnClickListener {
    private int maxLines = 2;
    private boolean isSpread;//伸展状态
    private OnClickListener listener;
    private int spreadHeight;//展开时的高度


    public StretchTextView(Context context) {
        super(context);
        init(context);
    }

    public StretchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public StretchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setEllipsize(TextUtils.TruncateAt.END);//多余的变成...
        setMaxLines(maxLines);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getLineCount() >= maxLines) {
                            if (oPenCLoseListener != null) {
                                oPenCLoseListener.show();
                            }
                        } else {
                            if (oPenCLoseListener != null) {
                                oPenCLoseListener.hind();
                            }
                        }
                    }
                }, 100);
            }
        });

        super.setOnClickListener(this);
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }


    @Override
    public void onClick(View view) {
        if (getLineCount() < maxLines) {
            return;
        }
        isSpread = !isSpread;
        if (isSpread) {
            this.setMaxLines(Integer.MAX_VALUE);
            this.requestLayout();

            if (spreadHeight == 0) {
                if (oPenCLoseListener != null) {
                    oPenCLoseListener.Open();
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spreadHeight = getMeasuredHeight();
                        if (oPenCLoseListener != null) {
                            oPenCLoseListener.close();
                        }
                    }
                }, 100);
            } else {
                if (oPenCLoseListener != null) {
                    oPenCLoseListener.close();
                }
            }
        } else {
            this.setMaxLines(maxLines);
            this.requestLayout();
            spreadHeight = getMeasuredHeight();
            if (oPenCLoseListener != null) {
                oPenCLoseListener.Open();
            }
        }
        if (listener != null) {
            listener.onClick(view);
        }
    }

    private OnOPenCLoseListener oPenCLoseListener;

    public void setoPenCLoseListener(OnOPenCLoseListener oPenCLoseListener) {
        this.oPenCLoseListener = oPenCLoseListener;
    }

    public interface OnOPenCLoseListener {
        void Open();

        void close();

        void show();

        void hind();
    }
}