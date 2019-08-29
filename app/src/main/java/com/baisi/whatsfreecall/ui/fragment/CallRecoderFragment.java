package com.baisi.whatsfreecall.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.BaseFragment;
import com.baisi.whatsfreecall.entity.RecoderEntity;
import com.baisi.whatsfreecall.ui.adapter.RecoderAdapter;

import java.util.List;

/**
 * Created by MnyZhao on 2017/12/5.
 */

public class CallRecoderFragment extends BaseFragment {
    private ListView mLvRecoder;
    private List<RecoderEntity> recoderEntities;
    private RecoderAdapter mRecoderAdapter;

    public static CallRecoderFragment Instance(String str) {
        CallRecoderFragment uf = new CallRecoderFragment();
        Bundle args = new Bundle();
        args.putString("info", str);
        uf.setArguments(args);
        return uf;
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recoder_fragment, container, false);
        mLvRecoder= (ListView) view.findViewById(R.id.lv_recoder);
        if(recoderEntities!=null&&recoderEntities.size()>0){
            mRecoderAdapter = new RecoderAdapter(WhatsFreeCallApplication.getInstance().getApplicationContext());
            mLvRecoder.setAdapter(mRecoderAdapter);
            mRecoderAdapter.addBottom(recoderEntities, true);
            mLvRecoder.setOnItemClickListener(itemClickListener);
        }
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setDefaultFragmentTitle(String title) {

    }

    public void setDate(List<RecoderEntity> recoderEntities) {
        this.recoderEntities = recoderEntities;
        if (mLvRecoder != null) {
            mRecoderAdapter = new RecoderAdapter(WhatsFreeCallApplication.getInstance().getApplicationContext());
            mLvRecoder.setAdapter(mRecoderAdapter);
            mRecoderAdapter.addBottom(recoderEntities, true);
            mLvRecoder.setOnItemClickListener(itemClickListener);
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RecoderEntity recoderEntity = recoderEntities.get(position);
            if (recoderItemClickListener != null) {
                recoderItemClickListener.setRecoderEntity(recoderEntity);
            }
        }
    };
    /*提供接口出去拨打电话*/
    public OnRecoderItemClickListener recoderItemClickListener;

    public void setOnRecoderItemClickListener(OnRecoderItemClickListener recoderItemClickListener) {
        this.recoderItemClickListener = recoderItemClickListener;
    }

    public interface OnRecoderItemClickListener {
        void setRecoderEntity(RecoderEntity recoderEntity);
    }
}
