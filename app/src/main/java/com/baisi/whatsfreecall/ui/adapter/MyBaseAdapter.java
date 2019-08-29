package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 * @author mny
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected LayoutInflater inflater;
    protected Context context;

    public MyBaseAdapter(Context context) {
        super();
        this.context = context;
        list = new ArrayList<T>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView2(position, convertView, parent);
    }

    public abstract View getView2(int position, View convertView, ViewGroup parent);

    public List<T> getData() {
        return list;
    }

    //获取数据
    public int getCount(List<T> data) {
        return data.size();
    }

    //向底部添加一条
    public void addBottom(T t, boolean isClearOld) {
        if (isClearOld) {
            list.clear();
        }
        list.add(t);
        notifyDataSetChanged();
    }

    //向底部添加多条
    public void addBottom(List<T> data, boolean isClearOld) {
        if (isClearOld) {
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    //向顶部添加一条数据
    public void addTop(T t, boolean isClearOld) {
        if (isClearOld) {
            list.clear();
        }
        list.add(0, t);
        notifyDataSetChanged();
    }

    //向顶部添加多条数据
    public void addTop(List<T> data, boolean isClearOld) {
        if (isClearOld) {
            list.clear();
        }
        list.addAll(0, data);
        notifyDataSetChanged();
    }
}
