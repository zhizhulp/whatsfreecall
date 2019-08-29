package com.baisi.whatsfreecall.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.baisi.whatsfreecall.R;

/**
 * Created by nick on 2018/2/6.
 */

public class TimeLineMarker extends View {

    private int mMarkerSize = 50;
    private int mLineSize = 12;
    private Drawable mBeginLine;
    private Drawable mEndLine;
    private Drawable mMarkerDrawable;

    public TimeLineMarker(Context context) {
        this(context,null);
    }

    public TimeLineMarker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimeLineMarker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeLineMarker);
        //mMarkerSize = a.getDimensionPixelSize(R.styleable.TimeLineMarker_markerSize,mMarkerSize);
        mLineSize = a.getDimensionPixelSize(R.styleable.TimeLineMarker_lineSize,mLineSize);
        mBeginLine = a.getDrawable(R.styleable.TimeLineMarker_beginLine);
        mEndLine = a.getDrawable(R.styleable.TimeLineMarker_endLine);
        mMarkerDrawable = a.getDrawable(R.styleable.TimeLineMarker_marker);
        a.recycle();

        if(mMarkerDrawable != null){
            mMarkerDrawable.setCallback(this);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(mBeginLine != null){
            mBeginLine.draw(canvas);
        }
        if(mEndLine != null){
            mEndLine.draw(canvas);
        }
        if(mMarkerDrawable != null){
            mMarkerDrawable.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getPaddingLeft() + getPaddingRight();
        int h = getPaddingTop() + getPaddingBottom();

        if(mMarkerDrawable != null){
            w += mMarkerSize;
            h += 50;
        }

        w = Math.max(w,getMeasuredWidth());
        h = Math.max(h,getMeasuredHeight());

        int widthSize = resolveSizeAndState(w,widthMeasureSpec,0);
        int heightSize = resolveSizeAndState(h,heightMeasureSpec,0);

        setMeasuredDimension(widthSize,heightSize);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawableSize();
    }

    private void initDrawableSize() {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int width = getWidth();
        int height = getHeight();

        int cWidth = width - pLeft - pRight;
        int cHeight = height - pTop - pBottom;

        Rect bounds;

        if (mMarkerDrawable != null) {
            // Size
            pLeft = width/2 - mMarkerSize/2;
            pTop = height/2 - mMarkerSize/2;
            mMarkerDrawable.setBounds(pLeft, pTop, pLeft + mMarkerSize, pTop + mMarkerSize);
            bounds = mMarkerDrawable.getBounds();
        } else {
            bounds = new Rect(pLeft, pTop, pLeft + cWidth, pTop + cHeight);
        }

        int halfLineSize = mLineSize >> 1;
        int lineLeft = bounds.centerX() - halfLineSize;

        if (mBeginLine != null) {
            mBeginLine.setBounds(lineLeft, 0, lineLeft + mLineSize, bounds.top);
        }

        if (mEndLine != null) {
            mEndLine.setBounds(lineLeft, bounds.bottom, lineLeft + mLineSize, height);
        }
    }

    //设置时间线的宽度
    public void setLineSize(int lineSize){
        if(this.mLineSize != lineSize){
            this.mLineSize = lineSize;
            initDrawableSize();
            invalidate();
        }
    }



    public void setBeginLine(Drawable beginLine) {
        if (this.mBeginLine != beginLine) {
            this.mBeginLine = beginLine;
            if (mBeginLine != null) {
                mBeginLine.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setEndLine(Drawable endLine) {
        if (this.mEndLine != endLine) {
            this.mEndLine = endLine;
            if (mEndLine != null) {
                mEndLine.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setMarkerDrawable(Drawable markerDrawable) {
        if (this.mMarkerDrawable != markerDrawable) {
            this.mMarkerDrawable = markerDrawable;
            if (mMarkerDrawable != null) {
                mMarkerDrawable.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setMarkerSize(int markerSize) {
        if (this.mMarkerSize != markerSize) {
            mMarkerSize = markerSize;
            initDrawableSize();
            invalidate();
        }
    }

    public void setMarkerDrawableAndSize(Drawable markerDrawable,int markerSize) {
        if (this.mMarkerDrawable != markerDrawable) {
            this.mMarkerDrawable = markerDrawable;
            if (mMarkerDrawable != null) {
                mMarkerDrawable.setCallback(this);
            }
            this.mMarkerSize = markerSize;
            initDrawableSize();
            invalidate();
        }
    }


}
