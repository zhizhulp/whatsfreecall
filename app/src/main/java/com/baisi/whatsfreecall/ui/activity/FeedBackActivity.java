package com.baisi.whatsfreecall.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;

public class FeedBackActivity extends BaseActivity implements ShowAdFilter{
    private Toolbar mToolBar;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        initView();
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.feedback_toolbar);
        initToolbar(mToolBar, R.string.feedback);
        mWebView = (WebView) findViewById(R.id.wv_view);
        mProgressBar= (ProgressBar) findViewById(R.id.pb_feedback);
        setWebView(mWebView, UrlConfig.FEEDBACK_URL,mProgressBar);
    }


    @Override
    public boolean allowShowAd() {
        return false;
    }
}
