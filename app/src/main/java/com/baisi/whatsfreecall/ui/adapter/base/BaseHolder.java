package com.baisi.whatsfreecall.ui.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 基类 RecyclerView.ViewHolder
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected OnViewClickListener mOnViewClickListener = null;
    public BaseHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);//点击事件
    }

    /**
     * 设置数据
     */
    public abstract void setData(T data, int position);


    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getPosition());
        }
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }

}
