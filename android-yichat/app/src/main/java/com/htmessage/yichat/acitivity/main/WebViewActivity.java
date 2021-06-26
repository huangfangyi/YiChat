package com.htmessage.yichat.acitivity.main;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.LoggerUtils;

/**
 * 项目名称：YiChat
 * 类描述：WebViewActivity 描述: 协议页面
 * 创建人：songlijie
 * 创建时间：2016/11/25 10:57
 * 邮箱:814326663@qq.com
 */
public class WebViewActivity extends BaseActivity {
    private WebView webView_userInfor;//显示协议
    private String url ,title= null;
    private ImageView iv_back;
    private TextView tv_title;
    private ProgressBar webView_ProgressBar;//进度条显示进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        getData();
        initView();
        initData();
        setOnClick();
    }

    public void initView() {
        webView_ProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        webView_userInfor = (WebView) findViewById(R.id.webView);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    public void initData() {
        tv_title.setText(title);
        // 设置JS交互数据 //支持javascript
        webView_userInfor.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView_userInfor.getSettings().setSupportZoom(true);
        //隐藏缩放工具
        webView_userInfor.getSettings().setBuiltInZoomControls(true);
        // 设置不出现缩放工具
        webView_userInfor.getSettings().setDisplayZoomControls(false);
        //自适应屏幕
        webView_userInfor.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView_userInfor.getSettings().setLoadWithOverviewMode(true);
        webView_userInfor.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); // Android默认的处理方式
                handler.proceed();  // 接受所有网站的证书
                //handleMessage(Message msg); // 进行其他处理
            }
        });
        webView_userInfor.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    webView_ProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == webView_ProgressBar.getVisibility()) {
                        webView_ProgressBar.setVisibility(View.VISIBLE);
                    }
                    webView_ProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        // 加载web资源
        webView_userInfor.loadUrl(url);
    }

    public void getData() {
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        LoggerUtils.e("-----url:" + url);
    }

    public void setOnClick() {
        // 设置webview的点击事件
        webView_userInfor.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView_userInfor.canGoBack()) {
//            if (mWebView.getUrl().equals(HTConstant.BASE_BAOXIANCHAOSHI_URL)){
//                super.onBackPressed();
//            }else{
            webView_userInfor.goBack();
//            }
        } else {
            super.onBackPressed();
        }
    }
}
