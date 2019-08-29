package com.baisi.whatsfreecall.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;


public class ChargeSuccessActivity extends BaseActivity implements View.OnClickListener ,ShowAdFilter{

    /**
     * 显示余额
     */
    private TextView mTvSuccessBalance;
    /**
     * OK
     */
    private Button mBtnSuccessOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_success);
        initView();
    }

    private void initView() {
        mTvSuccessBalance = (TextView) findViewById(R.id.tv_success_balance);
        mTvSuccessBalance.setText(SpUtils.getString(getApplicationContext(),SpConfig.USER_STRING_BALANCE,"$0.00"));
        mBtnSuccessOk = (Button) findViewById(R.id.btn_success_ok);
        mBtnSuccessOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_success_ok:
                skip(HomeActivity.class);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean allowShowAd() {
        return false;
    }
}
