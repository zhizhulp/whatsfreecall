package com.bestgo.adsplugin.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.bestgo.adsplugin.animation.AbstractAnimator;

public class NativeAdContainer extends FrameLayout {
    private long mUseTime;
    private long mLastActiveTime;
    private String name;

    private int mScreenVisibility = INVISIBLE;

    private int placementIndex;

    private AbstractAnimator mAnimator;

    public NativeAdContainer(Context context) {
        super(context);
    }

    public NativeAdContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NativeAdContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            super.onLayout(changed, left, top, right, bottom);
        } catch (Exception ex) {
//            setVisibility(GONE);
            ex.printStackTrace();
            Log.e("MyFrameLayout", ex.getMessage());
        }
    }

    public void setPlacementIndex(int index) {
        this.placementIndex = index;
    }

    public int getPlacementIndex() {
        return placementIndex;
    }

    public void setUseTime() {
        mUseTime = System.currentTimeMillis();
    }

    public void setLastActiveTime() {
        mLastActiveTime = System.currentTimeMillis();
    }

    public boolean isOld() {
        return System.currentTimeMillis() - mLastActiveTime > 1000 * 5;
    }

    public boolean canReused() {
        return Math.abs(System.currentTimeMillis() - mUseTime) >= 1000;
    }

    protected void onWindowVisibilityChanged(final int visibility) {
        mScreenVisibility = visibility;
    }

    public boolean isScreenVisible() {
        return mScreenVisibility == VISIBLE;
    }

    public void setAnimator(AbstractAnimator animator) {
        this.mAnimator = animator;
    }

    public AbstractAnimator getAnimator() {
        return mAnimator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
