package com.baisi.whatsfreecall;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.utils.httputils.WifiUtils;
import com.baisi.whatsfreecall.utils.utilscalarrequest.ScalarsConverterFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class SignInActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private TextView mTvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        System.out.println("SignInActivity.onCreate" + WifiUtils.getMacAddress(getApplicationContext()));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.btn_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mTvNumber = (TextView) findViewById(R.id.tv_shownumber);
        String format = getString(R.string.notification_number, "+86", "18733700412");
        mTvNumber.setText(Html.fromHtml(format));
        String str="1232";
        switch (str){
            case "1232":
                System.out.println("1232");
                break;
        }
    }

    AlertDialog dialog;

    private void showDiallog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Login");
        dialog.setMessage("login progress");
        dialog.show();

    }

    private void dismisDialog() {
        if (dialog != null) {
//            dialog.dismiss();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
       /* showDiallog();*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            System.out.println("007Query>>>IDtoken" + account.getIdToken());
            createRetorfit(account.getIdToken());
        } catch (ApiException e) {
            if (e.getStatusCode() == 7) {
                Toast.makeText(SignInActivity.this, "翻墙失败影响测试", Toast.LENGTH_SHORT).show();
            }
            Log.w("SignActivity", "signInResult:failed code=" + e.getStatusCode() + e.toString());
        }
    }

    public interface PostRequest_Interface {
        @POST(UrlConfig.LOGIN)
        Call<String> getCall(@Header("Authorization") String userToken, @Body String values);
    }

    public void createRetorfit(String token) {
        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(ScalarsConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        // 步骤5:创建 网络请求接口 的实例
        PostRequest_Interface request = retrofit.create(PostRequest_Interface.class);
        //对 发送请求 进行封装(设置需要翻译的内容)
        Gson gson = new Gson();
        Sign sign = new Sign(token);
        String body = gson.toJson(sign);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<String> call = request.getCall("Bearer " + userToken, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("SignInActivity.onResponse>>>" + response.body().toString());
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("请求失败");
                System.out.println(t.getMessage());
            }
        });
    }

    class Sign {
        public String gt;

        public Sign(String gt) {
            this.gt = gt;
        }
    }
}
