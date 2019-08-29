package com.baisi.whatsfreecall.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.entity.NumberEntity;


/**
 * Created by MnyZhao on 2018/1/3.
 */

public class ContactDialogAdapter extends MyBaseAdapter<NumberEntity> {

    public ContactDialogAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView2(int position, View convertView, ViewGroup parent) {
        HolderView holderView;
        if (convertView==null) {
            convertView=inflater.inflate(R.layout.number_item,null);
            holderView=new HolderView(convertView);
            convertView.setTag(holderView);
        }
        holderView= (HolderView) convertView.getTag();
        NumberEntity numberEntity=list.get(position);
        if(TextUtils.isEmpty(numberEntity.getNumberType())){
            holderView.mTvType.setText("");
        }else{
            holderView.mTvType.setText(numberEntity.getNumberType());
        }
        if(TextUtils.isEmpty(numberEntity.getNumber())){
            holderView.mTvNumber.setText("");
        }else{
            holderView.mTvNumber.setText(numberEntity.getNumber());
        }
        return convertView;
    }
    class HolderView{
        TextView mTvType;
        TextView mTvNumber;
        public HolderView(View convertView){
            mTvType= (TextView) convertView.findViewById(R.id.tv_number_type);
            mTvNumber= (TextView) convertView.findViewById(R.id.tv_number);
        }
    }
}
