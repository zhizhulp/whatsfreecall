package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.entity.checkin.CheckInEntity;
import com.baisi.whatsfreecall.view.TimeLineMarker;

import java.util.List;

/**
 * Created by nick on 2018/2/6.
 */

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder> {

    private Context context;
    private List<CheckInEntity.tasks> mList;

    public CheckInAdapter(Context context, List<CheckInEntity.tasks> mList){
        this.context = context;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_every_day_check_in,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == 0){
            holder.mMarker.setBeginLine(null);
        }else if(position == mList.size()-1){
            holder.mMarker.setEndLine(null);
        }else{
            holder.mMarker.setBeginLine(context.getResources().getDrawable(R.color.colorPrimary));
            holder.mMarker.setEndLine(context.getResources().getDrawable(R.color.colorPrimary));
        }
        if(mList.get(position).getStatus() == 1) {
            holder.mMarker.setMarkerDrawableAndSize(context.getResources().getDrawable(R.drawable.shape_ring),dip2px(context,14));
            holder.mDay.getPaint().setFakeBoldText(false);
            holder.mDay.setTextColor(ContextCompat.getColor(context,R.color.text_light));
        }else if(mList.get(position).getStatus() == 2) {
            holder.mMarker.setMarkerDrawableAndSize(context.getResources().getDrawable(R.drawable.done),dip2px(context,21));
            holder.mDay.setTextColor(ContextCompat.getColor(context,R.color.text_fold));
            holder.mDay.getPaint().setFakeBoldText(true);
        }
        holder.mDay.setText("Day "+(position+1));
        holder.mReward.setText(mList.get(position).reward);
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TimeLineMarker mMarker;
        private TextView mDay;
        private TextView mReward;

        public ViewHolder(View itemView) {
            super(itemView);
            mMarker = (TimeLineMarker) itemView.findViewById(R.id.tlm_item_check_in);
            mDay = (TextView) itemView.findViewById(R.id.tv_item_check_in_day);
            mReward = (TextView) itemView.findViewById(R.id.tv_item_check_in_reward);
            //字体加粗
            mReward.getPaint().setFakeBoldText(true);
        }
    }
}
