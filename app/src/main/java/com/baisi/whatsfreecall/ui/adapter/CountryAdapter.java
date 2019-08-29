package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.interfaces.UpdateIndexUIListener;
import com.sahooz.library.CountryEntity;

/**
 * Created by MnyZhao on 2017/12/23.
 */

public class CountryAdapter extends MyBaseAdapter<CountryEntity> implements AbsListView.OnScrollListener {
    private UpdateIndexUIListener listener;
    private int mCurrentFirstPosition = 0;
    private int lastFirstPosition = -1;

    public CountryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView2(int position, View convertView, ViewGroup parent) {
        HolderView holderView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.country_item, parent, false);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }
        holderView = (HolderView) convertView.getTag();
        CountryEntity countryEntity = list.get(position);
        holderView.mIvCountryIcon.setImageDrawable(countryEntity.flag ==0? null : context.getResources().getDrawable(countryEntity.flag));
        if (!TextUtils.isEmpty(countryEntity.getName())) {
            holderView.mTvCountryName.setText(countryEntity.getName());
        }
        if (!TextUtils.isEmpty(String.valueOf(countryEntity.getCode()))) {
            holderView.mTvCountryNumber.setText("+"+countryEntity.getCode());
        }
        if (position > 0 && !list.get(position).getLabel().equals(list.get(position - 1).getLabel())) {
            holderView.mTvGroupIndex.setText(list.get(position).getLabel());

        } else if (position == 0) {
            holderView.mTvGroupIndex.setText(list.get(position).getLabel());
        } else {
            holderView.mTvGroupIndex.setText("");
        }
        return convertView;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mCurrentFirstPosition = firstVisibleItem;
        if (list.size() > 0) {
            if (listener != null) {
                listener.onUpdateText(list.get(mCurrentFirstPosition).getLabel());
            }
            if (firstVisibleItem != lastFirstPosition) {
                if (listener != null) {
                    listener.onUpdatePosition(0);
                }
            }
            if (list.size() > 1) {
                if (lastFirstPosition != -1 && !list.get(firstVisibleItem).getLabel()
                        .equals(list.get(firstVisibleItem + 1).getLabel())) {
                    View childView = view.getChildAt(0);
                    int bottom = childView.getBottom();
                    int height = childView.getHeight();
                    int distance = bottom - height;
                    if (distance < 0) {//如果新的section
                        listener.onUpdatePosition(distance);
                    } else {
                        listener.onUpdatePosition(0);
                    }
                }
            }
            lastFirstPosition = firstVisibleItem;
        }else{
            listener.onUpdateText("");
        }
    }


    public void setUpdateIndexUIListener(UpdateIndexUIListener listener) {
        this.listener = listener;
    }

    class HolderView {
        public TextView mTvCountryName;
        public TextView mTvGroupIndex;
        public TextView mTvCountryNumber;
        public ImageView mIvCountryIcon;
        public HolderView(View convertView) {
            mTvCountryName = (TextView) convertView.findViewById(R.id.tv_contry_name);
            mTvGroupIndex = (TextView) convertView.findViewById(R.id.tv_group_index_contry);
            mTvCountryNumber = (TextView) convertView.findViewById(R.id.tv_country_number);
            mIvCountryIcon= (ImageView) convertView.findViewById(R.id.iv_country_icon);;
        }
    }
}
