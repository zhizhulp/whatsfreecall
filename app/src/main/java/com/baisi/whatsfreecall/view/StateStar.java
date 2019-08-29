package com.baisi.whatsfreecall.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baisi.whatsfreecall.R;

/**
 * 带状态的签到图标
 */
public class StateStar extends RelativeLayout {

    private ImageView imStar;
    private View rootView;
    private ImageView imCoin;
    private int starEnablePic;
    private int starDisablePic;
    private int coinEnablePic;
    private int coinDisablePic;
    private volatile boolean isFlashing;

    public StateStar(Context context) {
        super(context);
        init(context, null);
    }

    public StateStar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StateStar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateStar);
        starEnablePic = typedArray.getResourceId(R.styleable.StateStar_star_enable_pic, R.mipmap.star1);
        starDisablePic = typedArray.getResourceId(R.styleable.StateStar_star_dis_enable_pic, R.mipmap.star01);
        coinEnablePic = typedArray.getResourceId(R.styleable.StateStar_icon_enable_pic, R.mipmap.coin1);
        coinDisablePic = typedArray.getResourceId(R.styleable.StateStar_icon_dis_enable_pic, R.mipmap.coin01);
        typedArray.recycle();
        initLayout(context);
    }

    private void initLayout(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.state_star, this, true);
        imStar = (ImageView) rootView.findViewById(R.id.im_star);
        imCoin = (ImageView) rootView.findViewById(R.id.im_coin);

        StateListDrawable starDrawable = new StateListDrawable();
        Drawable starEnablePic = context.getResources().getDrawable(this.starEnablePic);
        Drawable starDisablePic = context.getResources().getDrawable(this.starDisablePic);
        starDrawable.addState(new int[]{android.R.attr.state_enabled}, starEnablePic);
        starDrawable.addState(new int[]{-android.R.attr.state_enabled}, starDisablePic);
        imStar.setImageDrawable(starDrawable);

        StateListDrawable coinDrawable = new StateListDrawable();
        Drawable coinEnablePic = context.getResources().getDrawable(this.coinEnablePic);
        Drawable coinDisablePic = context.getResources().getDrawable(this.coinDisablePic);
        coinDrawable.addState(new int[]{android.R.attr.state_enabled}, coinEnablePic);
        coinDrawable.addState(new int[]{-android.R.attr.state_enabled}, coinDisablePic);
        imCoin.setImageDrawable(coinDrawable);
    }

    public void setEEnable(boolean enable) {
        imStar.setEnabled(enable);
        imCoin.setEnabled(enable);
    }

    public void startFlash() {
        if (isFlashing) stopFlash();
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation1.setDuration(500);
        alphaAnimation1.setRepeatCount(Animation.INFINITE);
        alphaAnimation1.setRepeatMode(Animation.REVERSE);
        imStar.startAnimation(alphaAnimation1);
        imCoin.startAnimation(alphaAnimation1);
        isFlashing = true;
    }

    public void stopFlash() {
        if (!isFlashing) return;
        imCoin.clearAnimation();
        imStar.clearAnimation();
        isFlashing = false;
    }

    public boolean isFlashing() {
        return isFlashing;
    }
}
