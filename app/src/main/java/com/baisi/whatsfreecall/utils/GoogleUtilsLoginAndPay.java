package com.baisi.whatsfreecall.utils;

import android.content.Context;

import com.baisi.whatsfreecall.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by MnyZhao on 2017/12/8.
 */

public class GoogleUtilsLoginAndPay {
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 9001;
    public Context mContext;
    public GoogleUtilsLoginAndPay() {

    }

    public GoogleUtilsLoginAndPay(Context context) {
        this.mContext = context;
    }

    /**
     * 获取google登陆的客户端
     *
     * @return
     */
    public  GoogleSignInClient getGoogleSignClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        return mGoogleSignInClient;
    }

}
