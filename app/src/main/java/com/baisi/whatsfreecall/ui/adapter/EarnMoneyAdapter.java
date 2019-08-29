package com.baisi.whatsfreecall.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.entity.EarnListEntity;
import com.baisi.whatsfreecall.utils.DateFormatter;
import com.baisi.whatsfreecall.view.progress_button.AnimDownloadProgressButton;

import java.util.Date;
import java.util.List;

public class EarnMoneyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private String TAG = "EarnMoneyAdapter";
    private List<EarnListEntity.EarnEntity> datas;
    private OnItemChildClick clickListener;

    public void setClickListener(OnItemChildClick clickListener) {
        this.clickListener = clickListener;
    }

    public EarnMoneyAdapter(List<EarnListEntity.EarnEntity> datas) {
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_earn_item, parent, false);
        return new EarnMoneyHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        EarnListEntity.EarnEntity data = datas.get(position);
        EarnMoneyHolder holder1 = (EarnMoneyHolder) holder;
        holder1.icon.setImageResource(data.resId);
        holder1.desc.setText(data.desc);
        holder1.money.setText(data.reward);
        holder1.cardView.setCardBackgroundColor(data.prtResColor);
        holder1.leftTime.setProgressBtnBackgroundColor(data.color);
        holder1.leftTime.setProgressBtnBackgroundSecondColor(data.rightColor);
        holder1.leftTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemChildClick(v, holder.getAdapterPosition());
                }
            }
        });
        if (data.totalTime == -1) {//没有奖励了
            holder1.leftTime.setState(AnimDownloadProgressButton.DOWNLOADING);
            holder1.leftTime.setCurrentText("No more reward");
            holder1.leftTime.setEnabled(false);
        } else {
            float pro;
            if (data.totalTime == 0) pro = 100;
            else pro = (1 - ((float) data.leftTime / (float) data.totalTime)) * 100;
            if (pro == 100) {
                holder1.leftTime.setState(AnimDownloadProgressButton.NORMAL);
                holder1.leftTime.setCurrentText("Go");
                holder1.leftTime.setEnabled(true);
            } else {
                holder1.leftTime.setState(AnimDownloadProgressButton.DOWNLOADING);
                holder1.leftTime.setProgress(pro);
                holder1.leftTime.setCurrentText(DateFormatter.format(new Date(data.leftTime*1000), "mm:ss"));
                holder1.leftTime.setEnabled(false);
            }
        }
        if (data.type == 1007) holder1.leftTime.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    static class EarnMoneyHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView desc;
        TextView money;
        AnimDownloadProgressButton leftTime;
        CardView cardView;

        EarnMoneyHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            icon = (ImageView) itemView.findViewById(R.id.im_icon);
            desc = (TextView) itemView.findViewById(R.id.tv_desc);
            money = (TextView) itemView.findViewById(R.id.tv_money);
            leftTime = (AnimDownloadProgressButton) itemView.findViewById(R.id.tv_left_time);
        }
    }

    public interface OnItemChildClick {
        void onItemChildClick(View view, int position);
    }
}
