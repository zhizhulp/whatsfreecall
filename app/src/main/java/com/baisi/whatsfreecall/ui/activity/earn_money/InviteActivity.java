package com.baisi.whatsfreecall.ui.activity.earn_money;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.net.BaseNetActivity;
import com.baisi.whatsfreecall.base.net.ResponseHandlerImpl;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.entity.InviteCode;
import com.baisi.whatsfreecall.entity.httpbackentity.HttpEntity;
import com.baisi.whatsfreecall.retrofit_services.NetService;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;

import retrofit2.Retrofit;

public class InviteActivity extends BaseNetActivity implements View.OnClickListener {


    private TextView tvEnter;
    private TextView tvCode;
    private EditText etCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initViews();

        getInviteCode();
    }

    private void getInviteCode() {
        String userToken = "Bearer " +
                SpUtils.getString(WhatsFreeCallApplication.getInstance(), SpConfig.USER_TOKEN, "");
        doNetwork(HttpClentUtils.getRetorfit().create(NetService.class).getInviteCode(userToken),
                new ResponseHandlerImpl<HttpEntity<InviteCode>>() {
                    @Override
                    public void handle200(HttpEntity<InviteCode> result) {
                        if (result.getErr_code() == 0) {
                            tvCode.setText(result.getData().code);
                        } else {
                            showShortToast(result.getErr_msg());
                        }
                    }
                }
        );
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar, "Invite Friend");
        tvEnter = (TextView) findViewById(R.id.tv_enter_code);
        tvEnter.setOnClickListener(this);
        View tvCopy = findViewById(R.id.tv_copy);
        tvCopy.setOnClickListener(this);
        tvCode = (TextView) findViewById(R.id.tv_invite_code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_enter_code:
                showEnterCodeDialog();
                break;
            case R.id.tv_copy:
                copyText();
                break;
            case R.id.tv_ok:
                if (TextUtils.isEmpty(etCode.getText())) {
                    showLongToast("Please input invite code");
                } else {
                    postInviteCode();
                }

                break;
        }
    }

    private void postInviteCode() {
        String userToken = "Bearer " +
                SpUtils.getString(WhatsFreeCallApplication.getInstance(), SpConfig.USER_TOKEN, "");
        doNetwork(HttpClentUtils.getRetorfit().create(NetService.class).postInviteCode(userToken, new InviteCode(etCode.getText().toString())),
                new ResponseHandlerImpl<HttpEntity<InviteCode>>() {
                    @Override
                    public void handle200(HttpEntity<InviteCode> result) {
                        showShortToast(result.getErr_msg());
                    }
                }
        );
    }

    private void showEnterCodeDialog() {
        Dialog dialog = new Dialog(this, R.style.transpanrent_theme);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.enter_code_dialog);
        etCode = (EditText) dialog.findViewById(R.id.et_code);
        dialog.findViewById(R.id.tv_ok).setOnClickListener(this);
        dialog.show();
    }

    private void copyText() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, tvCode.getText()));
        }

        showShortToast("The invite code has already copy to clipboard");
    }
}
