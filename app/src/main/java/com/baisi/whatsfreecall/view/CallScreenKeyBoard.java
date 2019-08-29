package com.baisi.whatsfreecall.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.baisi.whatsfreecall.R;

/**
 * Created by MnyZhao on 2018/1/2.
 */

public class CallScreenKeyBoard extends LinearLayout {
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

    public CallScreenKeyBoard(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public CallScreenKeyBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public CallScreenKeyBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.keyboard_calling, this, true);
        mLl1 = (LinearLayout) view.findViewById(R.id.lls_1);
        mLl1.setOnClickListener(itemClickListener);
        mLl2 = (LinearLayout) view.findViewById(R.id.lls_2);
        mLl2.setOnClickListener(itemClickListener);
        mLl3 = (LinearLayout) view.findViewById(R.id.lls_3);
        mLl3.setOnClickListener(itemClickListener);
        mLl4 = (LinearLayout) view.findViewById(R.id.lls_4);
        mLl4.setOnClickListener(itemClickListener);
        mLl5 = (LinearLayout) view.findViewById(R.id.lls_5);
        mLl5.setOnClickListener(itemClickListener);
        mLl6 = (LinearLayout) view.findViewById(R.id.lls_6);
        mLl6.setOnClickListener(itemClickListener);
        mLl7 = (LinearLayout) view.findViewById(R.id.lls_7);
        mLl7.setOnClickListener(itemClickListener);
        mLl8 = (LinearLayout) view.findViewById(R.id.lls_8);
        mLl8.setOnClickListener(itemClickListener);
        mLl9 = (LinearLayout) view.findViewById(R.id.lls_9);
        mLl9.setOnClickListener(itemClickListener);
        mLlX = (LinearLayout) view.findViewById(R.id.lls_x);
        mLlX.setOnClickListener(itemClickListener);
        mLl0 = (LinearLayout) view.findViewById(R.id.lls_0);
        mLl0.setOnClickListener(itemClickListener);
        mLlJ = (LinearLayout) view.findViewById(R.id.lls_j);
        mLlJ.setOnClickListener(itemClickListener);
    }

    private View.OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                default:
                    break;
                case R.id.lls_1:
                    setNumber("1");
                    break;
                case R.id.lls_2:
                    setNumber("2");
                    break;
                case R.id.lls_3:
                    setNumber("3");
                    break;
                case R.id.lls_4:
                    setNumber("4");
                    break;
                case R.id.lls_5:
                    setNumber("5");
                    break;
                case R.id.lls_6:
                    setNumber("6");
                    break;
                case R.id.lls_7:
                    setNumber("7");
                    break;
                case R.id.lls_8:
                    setNumber("8");
                    break;
                case R.id.lls_9:
                    setNumber("9");
                    break;
                case R.id.lls_0:
                    setNumber("0");
                    break;
                case R.id.lls_x:
                    setNumber("*");
                    break;
                case R.id.lls_j:
                    setNumber("#");
                    break;
            }
        }
    };
    public interface OnNumberClickListener {
        void setNumber(String number);
    }
    OnNumberClickListener numberClickListener;

    public void setOnNumberClickListener(OnNumberClickListener numberClickListener) {
        this.numberClickListener = numberClickListener;
    }
    private void setNumber(String number) {
        if (numberClickListener != null) {
            numberClickListener.setNumber(number);
        }
    }
}
