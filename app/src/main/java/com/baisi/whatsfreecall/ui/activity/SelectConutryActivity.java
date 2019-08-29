package com.baisi.whatsfreecall.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.interfaces.UpdateIndexUIListener;
import com.baisi.whatsfreecall.ui.adapter.CountryAdapter;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.sahooz.library.CountryEntity;

import java.util.ArrayList;
import java.util.List;

import static com.baisi.whatsfreecall.ui.activity.DialActivity.RESULT_CODE;


public class SelectConutryActivity extends AppCompatActivity implements ShowAdFilter{

    private Toolbar mChargeToolbar;
    private AppBarLayout mAppBarLayout;
    private CardView mSearchEditFrame;
    private ListView mLvContactCountry;
    private TextView mTvContactShowlaeblCountry;
    private CountryAdapter mCountryAdapter;
    List<CountryEntity> models = new ArrayList<>();
    List<CountryEntity> queryCountrys = new ArrayList<>();
    private SearchView mScView;
    private CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_conutry);
        initView();
    }
    private void initView() {
        mChargeToolbar = (Toolbar) findViewById(R.id.select_toolbar);
        initToolbar(mChargeToolbar);
        cardView= (CardView) findViewById(R.id.search_edit_frame);
        mLvContactCountry = (ListView) findViewById(R.id.lv_contact_country);
        mTvContactShowlaeblCountry = (TextView) findViewById(R.id.tv_contact_showlaebl_country);
        mCountryAdapter = new CountryAdapter(this);
        mCountryAdapter.setUpdateIndexUIListener(updateIndexUIListener);
        mLvContactCountry.setAdapter(mCountryAdapter);
        models.addAll(CountryEntity.getAll(this,null));
        mCountryAdapter.addBottom(models, true);
        mLvContactCountry.setOnScrollListener(mCountryAdapter);
        mScView = (SearchView) findViewById(R.id.sc_view);
        mScView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryCountrys.clear();
                for (int i = 0; i < models.size(); i++) {
                    CountryEntity countryEntity=models.get(i);
                    if (countryEntity.getName().contains(newText)||String.valueOf(countryEntity.getCode()).contains(newText)) {
                        queryCountrys.add(models.get(i));
                    }
                }
                mCountryAdapter.addBottom(queryCountrys, true);
                mCountryAdapter.notifyDataSetChanged();
                return true;
            }
        });
        mLvContactCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                CountryEntity countryEntity= mCountryAdapter.getItem(position);
                intent.putExtra("entity",countryEntity);
                setResult(RESULT_CODE,intent);
                finish();
            }
        });
    }
    private void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(getResources().getString(R.string.select_country_title));
        setSupportActionBar(toolbar);
        //设置toolbar后调用setDisplayHomeAsUpEnabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    UpdateIndexUIListener updateIndexUIListener = new UpdateIndexUIListener() {
        @Override
        public void onUpdatePosition(int position) {
            ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) mTvContactShowlaeblCountry.getLayoutParams();
            mp.topMargin = position/* + dip2px(WhatsFreeCallApplication.getInstance().getApplicationContext(), 10)*/;
            mTvContactShowlaeblCountry.setLayoutParams(mp);
        }

        @Override
        public void onUpdateText(String mtext) {
            mTvContactShowlaeblCountry.setText(mtext);
        }
    };

    @Override
    public boolean allowShowAd() {
        return false;
    }
}
