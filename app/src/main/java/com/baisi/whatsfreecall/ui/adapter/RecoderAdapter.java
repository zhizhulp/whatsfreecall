package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.configs.MyCallType;
import com.baisi.whatsfreecall.entity.RecoderEntity;
import com.baisi.whatsfreecall.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MnyZhao on 2017/12/6.
 */

public class RecoderAdapter extends MyBaseAdapter<RecoderEntity> {

    public RecoderAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView2(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recoder_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        RecoderEntity recoderEntity = list.get(position);
        if (TextUtils.isEmpty(recoderEntity.getPhotoUri())) {
            Glide.with(context).load(R.drawable.avatar).into(holder.ivPhoto);
        } else {
            Glide.with(context).load(recoderEntity.getPhotoUri()).into(holder.ivPhoto);
        }
        if (TextUtils.isEmpty(recoderEntity.getName())) {
            holder.tvName.setText(recoderEntity.getPhoneNumber());
        } else {
            holder.tvName.setText(recoderEntity.getName());
        }
        holder.tvAdress.setText(recoderEntity.getCounryName());
        Date date;
        try {
            date = new Date(recoderEntity.getDate());

        } catch (NumberFormatException e) {
            date = new Date();
        }
        switch (recoderEntity.getType()) {
            case MyCallType.CALL_OUT_SUCCESS:
                Glide.with(context).load(R.drawable.callout_green).into(holder.ivType);
                break;
            case MyCallType.CALL_OUT_ERROR:
                Glide.with(context).load(R.drawable.callout_red).into(holder.ivType);
                break;
            default:
                break;
           /* //呼入接通
            case CallLog.Calls.INCOMING_TYPE:
                Glide.with(context).load(R.drawable.callin_green).into(holder.ivType);
                break;
            //呼入未接
            case CallLog.Calls.MISSED_TYPE:
            //呼入拒接
            case CallLog.Calls.REJECTED_TYPE:
                Glide.with(context).load(R.drawable.callin_red).into(holder.ivType);
                break;
            //呼出
            case CallLog.Calls.OUTGOING_TYPE:
                Glide.with(context).load(R.drawable.callout).into(holder.ivType);
                break;*/
        }
       /* if (DateFormatter.isToday(date)) {
            holder.tvDate.setText("Now");
        } else if (DateFormatter.isYesterday(date)) {
            holder.tvDate.setText("Yestoday");
        } else {*/
            SimpleDateFormat format = new SimpleDateFormat("MMM d, HH:mm", Locale.ENGLISH);
            holder.tvDate.setText(format.format(date));
       /* }*/

        return convertView;
    }

    class ViewHolder {
        CircleImageView ivPhoto;
        TextView tvName;
        TextView tvAdress;
        ImageView ivType;
        TextView tvDate;

        public ViewHolder(View convertView) {
            ivPhoto = (CircleImageView) convertView.findViewById(R.id.iv_recoder_icon);
            tvName = (TextView) convertView.findViewById(R.id.tv_recoder_name);
            tvAdress = (TextView) convertView.findViewById(R.id.tv_recoder_adress);
            ivType = (ImageView) convertView.findViewById(R.id.iv_recoder_call_type);
            tvDate = (TextView) convertView.findViewById(R.id.tv_recoder_date);
        }
    }
}
