package com.baisi.whatsfreecall.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.configs.UrlConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by MnyZhao on 2017/12/7.
 */

public class BaseActivity extends AppCompatActivity {
    protected String TAG= getClass().getSimpleName();
    /**
     * 获取本Activity对象
     */
    public static Activity currentACtivity;
    /**
     * 获取屏幕的宽高
     */
    public static int screenW, screenH;
    /**
     * 吐司对象
     */
    private Toast toast;
    /**
     * 意图对象--跳转
     */
    private Intent intent;
    /******************* 吐司 ********************************/
    /**
     * 短吐司提示
     *
     * @param msg 显示的信息
     */
    protected void showShortToast(String msg) {
        if (toast == null) {// 判断第一次是不是空 是空就新建一个不是就直接设置值使用
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    /**
     * 短吐司提示
     *
     * @param id 字符串对应的编号
     */
    protected void showShortToast(int id) {
        String msg = getString(id);// 获取当前Id所对应的字符
        showShortToast(msg);// 设置并显示
    }

    /**
     * 长吐司提示
     *
     * @param msg 显示信息
     */
    protected void showLongToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }

    /**
     * 长吐司提示
     *
     * @param id 信息对应的编号
     */
    protected void showLongToast(int id) {
        String msg = getString(id);
        showLongToast(msg);
    }

    /******************** Skip跳转 *************************************/
    /**
     * 跳转传值
     *
     * @param class1 下一个界面
     * @param bundle 传递的值
     */
    protected void skip(Class<?> class1, Bundle bundle) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(this, class1);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 不传值的跳转
     *
     * @param class1 下一个界面
     */
    protected void skip(Class<?> class1) {
        skip(class1, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentACtivity = this;
        screenW = getWindowManager().getDefaultDisplay().getWidth();// 获取宽
        screenH = getWindowManager().getDefaultDisplay().getHeight();// 获取高
    }

    /**************************initToolbar**************************************/
    protected void initToolbar(Toolbar toolbar, String title) {
        if (!TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
        }
        setSupportActionBar(toolbar);
        //设置toolbar后调用setDisplayHomeAsUpEnabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void initToolbar(Toolbar toolbar, int title) {
        toolbar.setTitle(getResources().getString(title));
        setSupportActionBar(toolbar);
        //设置toolbar后调用setDisplayHomeAsUpEnabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /*隐藏键盘显示光标*/
    public void hideSoftInputMethod(EditText ed) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 调用系统邮件
     * 必须明确使用mailto前缀来修饰邮件地址,
     *
     * @param callId
     * @param body
     */
    protected boolean startEmail(String callId, String body) {
        try {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:" + UrlConfig.EMAIL));
            data.putExtra(Intent.EXTRA_SUBJECT, callId);
            data.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(data);
        } catch (RuntimeException e) {
            System.out.println("BaseActivity.startEmail>>>>"+e.getMessage().toString());
            return false;
        }
        return true;
    }


    /**
     * 显示加载布局
     *
     * @param llProgress  父布局 LinearLayout
     * @param tvShowMsg   子内容textView 显示加载信息
     * @param progressBar progressBar
     * @param colorId     颜色id
     * @param msg         加载内容提示
     */
    protected void showProgress(LinearLayout llProgress, ProgressBar progressBar, TextView tvShowMsg, String msg, int colorId) {
        tvShowMsg.setText(msg);
        setProgressBarColor(colorId, progressBar);
        llProgress.setVisibility(View.VISIBLE);
    }

    /**
     * 显示加载布局
     *
     * @param llProgress  父布局 LinearLayout id
     * @param tvShowMsg   子内容textView 显示加载信息 id
     * @param progressBar 进度条
     * @param colorId     颜色id
     * @param msg         加载内容提示
     */
    protected void showProgress(int llProgress, int progressBar, int tvShowMsg, String msg, int colorId) {
        LinearLayout llProgressBar = (LinearLayout) this.findViewById(llProgress);
        TextView tvMsg = (TextView) this.findViewById(tvShowMsg);
        ProgressBar progressBar1 = (ProgressBar) this.findViewById(progressBar);
        setProgressBarColor(colorId, progressBar1);
        tvMsg.setText(msg);
        llProgressBar.setVisibility(View.VISIBLE);
    }

    protected void showDefaultProgress(){
        showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.wait), R.color.colorPrimary);
    }
    protected void hideDefaultProgress(){
        hideProgress((R.id.ll_progress));
    }

    /**
     * 隐藏加载布局
     *
     * @param llProgress 父布局
     */
    protected void hideProgress(LinearLayout llProgress) {

        llProgress.setVisibility(View.GONE);
    }

    /**
     * 隐藏加载布局
     *
     * @param llProgress 父布局 id
     */
    protected void hideProgress(int llProgress) {
        LinearLayout llProgressBar = (LinearLayout) this.findViewById(llProgress);
        llProgressBar.setVisibility(View.GONE);
    }

    /**
     * 是否显示progressbar布局
     *
     * @param llProgress
     * @return
     */
    protected boolean isProgressShow(int llProgress) {
        LinearLayout llProgressBar = (LinearLayout) this.findViewById(llProgress);
        return llProgressBar.isShown();
    }

    /**
     * 是否显示progressbar布局
     *
     * @param llProgress
     * @return
     */
    protected boolean isProgressShow(LinearLayout llProgress) {
        return llProgress.isShown();
    }

    /**
     * 设置ProgressBar 的颜色
     *
     * @param colorId      color 在色值中的颜色
     * @param mProgressBar progressBar控件
     */
    protected void setProgressBarColor(int colorId, ProgressBar mProgressBar) {
        // fixes pre-Lollipop progressBar indeterminateDrawable tinting
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable wrapDrawable = DrawableCompat.wrap(mProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, colorId));
            mProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, colorId), PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * 设置webView
     *
     * @param mWebView
     * @param url          加载路径
     * @param mProgressBar 进度
     */
    protected void setWebView(final WebView mWebView, String url, final ProgressBar mProgressBar) {
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //支持插件
        /*webSettings.setPluginsEnabled(true);被弃用方法 在18中删除*/
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //webview中缓存模式
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebView.loadUrl(url);
        if (mProgressBar != null) {
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    mProgressBar.setProgress(newProgress);
                }
            });
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * 系统分享
     *
     * @param msg 分享内容
     */
    protected void share(String msg) {
        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.putExtra(Intent.EXTRA_TEXT, msg);
        intent1.setType("text/plain");
        startActivity(Intent.createChooser(intent1, "share"));
    }

}
