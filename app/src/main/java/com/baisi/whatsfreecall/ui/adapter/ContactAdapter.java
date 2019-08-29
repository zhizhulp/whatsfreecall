package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.entity.ContactsEntity;
import com.baisi.whatsfreecall.interfaces.UpdateIndexUIListener;
import com.baisi.whatsfreecall.view.CircleImageView;
import com.bumptech.glide.Glide;


/**
 * Created by MnyZhao on 2017/12/6.
 */

public class ContactAdapter extends MyBaseAdapter<ContactsEntity> implements AbsListView.OnScrollListener {
    private UpdateIndexUIListener listener;
    private int mCurrentFirstPosition = 0;
    private int lastFirstPosition = -1;

    public ContactAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView2(int position, View convertView, ViewGroup parent) {
        HolderView holderView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.conotact_item, null);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }
        holderView = (HolderView) convertView.getTag();
        ContactsEntity contactsEntity = list.get(position);
        if (TextUtils.isEmpty(contactsEntity.getIconUri())) {
            Glide.with(context).load(R.drawable.avatar).into(holderView.circleImageView);
        } else {
            Glide.with(context).load(contactsEntity.getIconUri()).into(holderView.circleImageView);
        }
        if (!TextUtils.isEmpty(contactsEntity.getName())) {
            holderView.tvName.setText(contactsEntity.getName());
        } else {
            holderView.tvName.setText(contactsEntity.getPhone());
        }
        if (position > 0 && !list.get(position).getFirstLater().equals(list.get(position - 1).getFirstLater())) {
            holderView.tvGroup.setText(list.get(position).getFirstLater());

        } else if (position == 0) {
            holderView.tvGroup.setText(list.get(position).getFirstLater());
        } else {
            holderView.tvGroup.setText("");
        }
        if (position + 1 < list.size()) {
            if (position > 1 && !list.get(position).getFirstLater().equals(list.get(position + 1).getFirstLater())) {
                holderView.tvLine.setVisibility(View.VISIBLE);
            } else {
                holderView.tvLine.setVisibility(View.GONE);
            }
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
                listener.onUpdateText(list.get(mCurrentFirstPosition).getFirstLater());
            }
            if (firstVisibleItem != lastFirstPosition) {
                if (listener != null) {
                    listener.onUpdatePosition(0);
                }
            }
            if (list.size() > 1) {
                if (lastFirstPosition != -1 && !list.get(firstVisibleItem).getFirstLater()
                        .equals(list.get(firstVisibleItem + 1).getFirstLater())) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
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
            }
            lastFirstPosition = firstVisibleItem;
        }
    }

    public void setUpdateIndexUIListener(UpdateIndexUIListener listener) {
        this.listener = listener;
    }

    class HolderView {
        private CircleImageView circleImageView;
        private TextView tvName;
        private TextView tvGroup;
        private TextView tvLine;

        public HolderView(View convertView) {
            circleImageView = (CircleImageView) convertView.findViewById(R.id.iv_contact_item_photo);
            tvName = (TextView) convertView.findViewById(R.id.tv_contact_item_name);
            tvGroup = (TextView) convertView.findViewById(R.id.tv_group_index);
            tvLine = (TextView) convertView.findViewById(R.id.tv_contact_line);
        }
    }
}
