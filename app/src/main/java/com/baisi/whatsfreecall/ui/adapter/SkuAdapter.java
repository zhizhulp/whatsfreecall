package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.utilpay.SkuDetails;

import java.util.List;

/**
 * Created by MnyZhao on 2017/12/7.
 */

public class SkuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context context;
    public List<SkuDetails> skuDetailses;

    public SkuAdapter(Context context, List<SkuDetails> skuDetailses) {
        this.context = context;
        this.skuDetailses = skuDetailses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.sku_item, parent, false);
        HolderView holderView = new HolderView(itemView);
        return holderView;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SkuDetails skuDetailses = this.skuDetailses.get(position);
        if (holder instanceof HolderView) {
            StringBuffer stringBuffer = new StringBuffer(skuDetailses.getTitle());
            int index = stringBuffer.indexOf("(");
            try {
                ((HolderView) holder).tvPrice.setText(stringBuffer.substring(0, index));
            } catch (StringIndexOutOfBoundsException e){
                Firebase.getInstance(context).logEvent(StatisticalConfig.SKU_ADAPTER,skuDetailses.getTitle());
            }

            ((HolderView) holder).tvState.setText(skuDetailses.getDescription());
            ((HolderView) holder).ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemClick(((HolderView) holder).ll_group, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return skuDetailses.size();
    }

    class HolderView extends RecyclerView.ViewHolder {
        public LinearLayout ll_group;
        public TextView tvPrice;
        public TextView tvState;

        public HolderView(View itemView) {
            super(itemView);
            ll_group = (LinearLayout) itemView.findViewById(R.id.ll_chare);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_charge_show_price);
            tvState = (TextView) itemView.findViewById(R.id.tv_charge_show_satate);
        }
    }

    /*点击事件*/
    public interface OnItemClickListener {
        public void OnItemClick(View view, int position);
    }

    OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
