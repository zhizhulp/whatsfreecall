package com.baisi.whatsfreecall.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.baisi.whatsfreecall.R;

/**
 * Created by MnyZhao on 2017/12/14.
 */

public class KeyBoardView extends LinearLayout {
    public static final int CLICK=1;
    public static final int LONG_CLICK=2;
    private Context mContext;
    private LinearLayout mLl1;
    private LinearLayout mLl2;
    private LinearLayout mLl3;
    private LinearLayout mLl4;
    private LinearLayout mLl5;
    private LinearLayout mLl6;
    private LinearLayout mLl7;
    private LinearLayout mLl8;
    private LinearLayout mLl9;
    private LinearLayout mLlX;
    private LinearLayout mLl0;
    private LinearLayout mLlJ;
    private LinearLayout mLlHide;
    private LinearLayout mLlCall;
    private LinearLayout mLlDelete;

    public KeyBoardView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public KeyBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public KeyBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.keyboard, this, true);
        mLl1 = (LinearLayout) view.findViewById(R.id.ll_1);
        mLl1.setOnClickListener(itemClickListener);
        mLl2 = (LinearLayout) view.findViewById(R.id.ll_2);
        mLl2.setOnClickListener(itemClickListener);
        mLl3 = (LinearLayout) view.findViewById(R.id.ll_3);
        mLl3.setOnClickListener(itemClickListener);
        mLl4 = (LinearLayout) view.findViewById(R.id.ll_4);
        mLl4.setOnClickListener(itemClickListener);
        mLl5 = (LinearLayout) view.findViewById(R.id.ll_5);
        mLl5.setOnClickListener(itemClickListener);
        mLl6 = (LinearLayout) view.findViewById(R.id.ll_6);
        mLl6.setOnClickListener(itemClickListener);
        mLl7 = (LinearLayout) view.findViewById(R.id.ll_7);
        mLl7.setOnClickListener(itemClickListener);
        mLl8 = (LinearLayout) view.findViewById(R.id.ll_8);
        mLl8.setOnClickListener(itemClickListener);
        mLl9 = (LinearLayout) view.findViewById(R.id.ll_9);
        mLl9.setOnClickListener(itemClickListener);
        mLlX = (LinearLayout) view.findViewById(R.id.ll_x);
        mLlX.setOnClickListener(itemClickListener);
        mLl0 = (LinearLayout) view.findViewById(R.id.ll_0);
        mLl0.setOnClickListener(itemClickListener);
        mLlJ = (LinearLayout) view.findViewById(R.id.ll_j);
        mLlJ.setOnClickListener(itemClickListener);
        mLlHide = (LinearLayout) view.findViewById(R.id.ll_hide);
        mLlHide.setOnClickListener(itemClickListener);
        mLlCall = (LinearLayout) view.findViewById(R.id.ll_call);
        mLlCall.setOnClickListener(itemClickListener);
        mLlDelete = (LinearLayout) view.findViewById(R.id.ll_delete);
        mLlDelete.setOnClickListener(itemClickListener);
        mLlDelete.setOnLongClickListener(longclick);

    }
    private View.OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_1:
                    setNumber("1");
                    break;
                case R.id.ll_2:
                    setNumber("2");
                    break;
                case R.id.ll_3:
                    setNumber("3");
                    break;
                case R.id.ll_4:
                    setNumber("4");
                    break;
                case R.id.ll_5:
                    setNumber("5");
                    break;
                case R.id.ll_6:
                    setNumber("6");
                    break;
                case R.id.ll_7:
                    setNumber("7");
                    break;
                case R.id.ll_8:
                    setNumber("8");
                    break;
                case R.id.ll_9:
                    setNumber("9");
                    break;
                case R.id.ll_0:
                    setNumber("0");
                    break;
                case R.id.ll_x:
                    setNumber("*");
                    break;
                case R.id.ll_j:
                    setNumber("#");
                    break;
                case R.id.ll_hide:
                    if(onHideClickListener!=null){
                        onHideClickListener.setHide(true);
                    }
                    break;
                case R.id.ll_call:
                    if(onCallClickListener!=null){
                        onCallClickListener.setCallListener();
                    }
                    break;
                case R.id.ll_delete:
                    if(onDeleteClickListener!=null){
                        onDeleteClickListener.setDeleteListener(CLICK);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    View.OnLongClickListener longclick= new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()){
                case R.id.ll_delete:
                    if(onDeleteClickListener!=null){
                        onDeleteClickListener.setDeleteListener(LONG_CLICK);
                    }
                    break;
            }
            return false;
        }
    };

    public interface OnNumberClickListener {
        void setNumber(String number);
    }

    public interface OnCallClickListener {
        void setCallListener();
    }

    public interface OnDeleteClickListener {
        /**
         * 1 表示正常按下删除一个 2 表示长按全部清空
         *
         * @param mode
         */
        void setDeleteListener(int mode);
    }

    OnNumberClickListener numberClickListener;

    public void setOnNumberClickListener(OnNumberClickListener numberClickListener) {
        this.numberClickListener = numberClickListener;
    }

    OnCallClickListener onCallClickListener;

    public void setOnCallClickListener(OnCallClickListener onCallClickListener) {
        this.onCallClickListener = onCallClickListener;
    }

    OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }
    public interface  OnHideClickListener{
       void setHide(boolean isHide);
    }
    OnHideClickListener onHideClickListener;
    public void setOnHideClickListener(OnHideClickListener onHideClickListener){
        this.onHideClickListener=onHideClickListener;
    }
    private void setNumber(String number) {
        if (numberClickListener != null) {
            numberClickListener.setNumber(number);
        }
    }
}
