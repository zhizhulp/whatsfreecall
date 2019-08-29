package com.baisi.whatsfreecall.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.BaseFragment;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.entity.ContactsEntity;
import com.baisi.whatsfreecall.entity.NumberEntity;
import com.baisi.whatsfreecall.interfaces.UpdateIndexUIListener;
import com.baisi.whatsfreecall.ui.activity.DialActivity;
import com.baisi.whatsfreecall.ui.activity.LoginActivity;
import com.baisi.whatsfreecall.ui.adapter.ContactAdapter;
import com.baisi.whatsfreecall.ui.adapter.ContactDialogAdapter;
import com.baisi.whatsfreecall.utils.CheckToken;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by MnyZhao on 2017/12/5.
 */

public class ContactFragment extends BaseFragment {
    private ListView mlv;
    private TextView mTvLabel;
    private CardView mCdView;
    private ImageView mIvClose;
    List<ContactsEntity> contactsEntities;
    ContactAdapter mAdapter;
    AlertDialog mDialog;
    ListView mLvDialog;
    ContactDialogAdapter mContactDialogAdapter;
    List<NumberEntity> numberList;
    ContactsEntity contactsEntity;
    /*记录选中的号码若有多个号码 避免改变原集合中的数据*/
    ContactsEntity changeContactEntity;

    public static ContactFragment Instance(String str) {
        ContactFragment uf = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("info", str);
        uf.setArguments(args);
        return uf;
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragemnt, container, false);
        mlv = (ListView) view.findViewById(R.id.lv_contact);
        /*Test*/
        mCdView = (CardView) view.findViewById(R.id.cv_test);
        mIvClose = (ImageView) view.findViewById(R.id.iv_close);
        if (SpUtils.getBoolean(getContext(), SpConfig.SHOW_TEST_NUMBER, true)) {
            mCdView.setVisibility(View.VISIBLE);
        } else {
            mCdView.setVisibility(View.GONE);
        }
        mIvClose.setOnClickListener(clickListener);
        mCdView.setOnClickListener(clickListener);
        /*Test*/
        if (Build.VERSION.SDK_INT > 9) {
            mlv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        mTvLabel = (TextView) view.findViewById(R.id.tv_contact_showlaebl);
        mAdapter = new ContactAdapter(WhatsFreeCallApplication.getInstance().getApplicationContext());
        mAdapter.setUpdateIndexUIListener(updateIndexUIListener);
        if (contactsEntities != null && contactsEntities.size() > 0) {
            mlv.setAdapter(mAdapter);
            mAdapter.addBottom(contactsEntities, true);
            mlv.setOnScrollListener(mAdapter);
            mlv.setOnItemClickListener(itemListener);
        }
        return view;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close:
                    mCdView.setVisibility(View.GONE);
                    SpUtils.putBoolean(getContext(), SpConfig.SHOW_TEST_NUMBER, false);
                    break;
                case R.id.cv_test:
                    if (testInterface != null) {
                        testInterface.setTestListener("00000000010", 46);
                    }
                    break;
            }
        }
    };
    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CONTACT_CLICK, StatisticalConfig.RESULT_CODE_CLICK);
            contactsEntity = contactsEntities.get(position);
            setChangeEntityDetault(contactsEntity);
            if (CheckToken.checkToken()) {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_CONTACT, StatisticalConfig.LOGIN_SHOW);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtras(BundleUtils.getBundleUtils().getLoginBundle(false, BundleUtils.ENTER_CALL, "", null));
                getActivity().startActivity(intent);
            } else {
                showDialog(contactsEntity);
            }
        }
    };
    UpdateIndexUIListener updateIndexUIListener = new UpdateIndexUIListener() {
        @Override
        public void onUpdatePosition(int position) {
            ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) mTvLabel.getLayoutParams();
            mp.topMargin = position + dip2px(WhatsFreeCallApplication.getInstance().getApplicationContext(), 10);
            mTvLabel.setLayoutParams(mp);
        }

        @Override
        public void onUpdateText(String mtext) {
            mTvLabel.setText(mtext);
        }
    };

    public static int dip2px(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setDefaultFragmentTitle(String title) {

    }

    public void setData(List<ContactsEntity> contactsEntities) {
        this.contactsEntities = contactsEntities;
        if (mlv != null) {
            mAdapter = new ContactAdapter(WhatsFreeCallApplication.getInstance().getApplicationContext());
            mAdapter.setUpdateIndexUIListener(updateIndexUIListener);
            mlv.setAdapter(mAdapter);
            mAdapter.addBottom(contactsEntities, true);
            mlv.setOnScrollListener(mAdapter);
            mlv.setOnItemClickListener(itemListener);
        }

    }

    private void showDialog(ContactsEntity contactsEntity) {
        if (contactsEntity != null && contactsEntity.getPhone() != null&&contactsEntity.getType()!=null) {
            if (!(contactsEntity.getPhone().contains(";") && contactsEntity.getType().contains(";"))) {
                skipDial(contactsEntity);
            } else {
                String[] type = contactsEntity.getType().split(";");
                String[] strNumber = contactsEntity.getPhone().split(";");
                mContactDialogAdapter = new ContactDialogAdapter(getActivity());
                numberList = new ArrayList<>();
                for (int i = 0; i < type.length; i++) {
                    numberList.add(new NumberEntity(type[i], strNumber[i]));
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout, null);
                builder.setView(view);
                builder.setTitle(contactsEntity.getName());
                mDialog = builder.create();
                mLvDialog = (ListView) view.findViewById(R.id.lv_dialog);
                mLvDialog.setAdapter(mContactDialogAdapter);
                mContactDialogAdapter.addBottom(numberList, true);
                mLvDialog.setOnItemClickListener(itemDialogClickListener);
                mDialog.show();
            }
        }
    }

    AdapterView.OnItemClickListener itemDialogClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NumberEntity numberEntity = numberList.get(position);
            changeContactEntity.setPhone(numberEntity.getNumber());
            changeContactEntity.setType(numberEntity.getNumberType());
            skipDial(changeContactEntity);
            mDialog.dismiss();
        }
    };

    private void skipDial(ContactsEntity contactsEntity) {
        Intent intent = new Intent(getActivity(), DialActivity.class);
        intent.putExtras(BundleUtils.getBundleUtils().getContactBundle(contactsEntity));
        /*if (GeoUtil.getCountryCode(WhatsFreeCallApplication.getInstance().getApplicationContext(), contactsEntity.getPhone()) == 0) {
            Toast.makeText(getContext(), "Please check the correctness of the phone number", Toast.LENGTH_SHORT).show();
            return;
        }*/
        getActivity().startActivity(intent);
    }

    private void setChangeEntityDetault(ContactsEntity contactsEntity) {
        changeContactEntity = new ContactsEntity();
        changeContactEntity.setId(contactsEntity.getId());
        changeContactEntity.setName(contactsEntity.getName());
        changeContactEntity.setFirstLater(contactsEntity.getFirstLater());
        changeContactEntity.setIconUri(contactsEntity.getIconUri());
        changeContactEntity.setPinyin(contactsEntity.getPinyin());
    }

    /*Test*/
    public interface TestInterface {
        void setTestListener(String number, int counterCode);
    }

    TestInterface testInterface;

    public void setTestInterface(TestInterface testInterface) {
        this.testInterface = testInterface;
    }
    /*Test*/
}
