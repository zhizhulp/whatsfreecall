package com.baisi.whatsfreecall;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baisi.whatsfreecall.utils.utilpay.IabBroadcastReceiver;
import com.baisi.whatsfreecall.utils.utilpay.IabHelper;
import com.baisi.whatsfreecall.utils.utilpay.IabResult;
import com.baisi.whatsfreecall.utils.utilpay.Inventory;
import com.baisi.whatsfreecall.utils.utilpay.Purchase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    public static String TAG = "WhatsFreeCall";
    public static String SKU_1990000 = "credit_1990000";
    public static String SKU_5000000 = "credit_5000000";
    public static String SKU_10000000 = "credit_10000000";
    public static String SKU_30000000 = "credit_30000000";
    public static String SKU_test = "test_001_0.5";
    public String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp+52dSDHIsNA0B9McBHJgkbafkgp3CIZS+86WbxDUnS/ypmTGl4iYKQVkiO+yTxCcaFvI1yn1jNI4qimLC1iZF/C9AdK4kSe0IUcnqmLaSMnl1gAywWJUHgpzRXO0W7Al1RdZvNDoYuqfGQVdJcl05PlqLawsxYg+YHdYMYdaLa1/BhpuTYk4b7EU2cMtVC0IJaSd8EQ4F//4LVzAD4HMLlsf2N3l6xaZ/Fv72TYo2QfxpeqiYHd/coOKn2AEEfKS0JAxqmM3WCC6ZT7fbVCdURcLF6Pouk0Gj72NbudsFfaKujpqXhETJR2quznrDISXU4n3Y0OStYBgSFpi9mO4wIDAQAB";
    // The helper object
    IabHelper mHelper;
    //提供购买通知
    IabBroadcastReceiver mBroadcastReceiver;
    List<String> skuList = new ArrayList<>();
    boolean isConsume = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        skuList.add(SKU_1990000);
        skuList.add(SKU_5000000);
        skuList.add(SKU_10000000);
        skuList.add(SKU_30000000);
        skuList.add(SKU_test);
        skuList.add("creadit_100000000");
        mHelper = new IabHelper(this, getResources().getString(R.string.base64EncodedPublicKey));
        /*测试阶段查看log*/
        mHelper.enableDebugLogging(true);
        //开始设置 异步的监听
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(true,skuList,null,mGotInventoryListener);
                   /* mHelper.queryInventoryAsync(mGotInventoryListener);*/
                    /*mHelper.launchPurchaseFlow(MainActivity.this, SKU_1990000, 1000, MainActivity.this);*/
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (inventory != null) {
                List<String> list = inventory.getAllOwnedSkus();
                for (int i = 0; i < list.size(); i++) {
                   Log.e("MainActivity.onQueryInventoryFinished>>>",inventory.getSkuDetails(list.get(i)).toString());
                }
                if (inventory.getPurchase(SKU_1990000) != null) {
                    tvShowMsg.setText(inventory.getPurchase(SKU_1990000).toString());
                    System.out.println("007Query>>>" + inventory.getPurchase(SKU_1990000).toString());
                } else {
                    tvShowMsg.setText("没有库存");
                }
            }
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_1990000);
//            try {
//                mHelper.launchPurchaseFlow(MainActivity.this, SKU_1990000, 1000, onIabPurchaseFinishedListener);
//            } catch (IabHelper.IabAsyncInProgressException e) {
//                e.printStackTrace();
//            }
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_1990000), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);

        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
                mHelper = null;
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
    }

    IabHelper.OnIabPurchaseFinishedListener onIabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            System.out.println("MainActivity.onIabPurchaseFinished>>>>>>" + "Purchase finished: " + result + ", purchase: " + info);
            tvShowMsg.setText("purchase:" + info);
            if (mHelper == null) {
                return;
            }
            if (result.isFailure()) {
                complain("Erroe purchasing:" + result);
                return;
            }
            if (!verifyDeveloperPayload(info)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }
            if (result.isSuccess()) {
                if (isConsume) {
                    try {
                        mHelper.consumeAsync(info, mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }
            //需要检查消耗的是哪个东西
            if (result.isSuccess()) {
                tvShowMsg.setText("消耗成功");
            } else {
                complain("Error while consuming: " + result);
            }
        }
    };


    private TextView tvShowMsg;
    private Button btnBuy;
    private Button btnConsume;
    private Button btnInv;


    private void init() {

        tvShowMsg = (TextView) findViewById(R.id.tv_showMsg);
        btnBuy = (Button) findViewById(R.id.btn_buy);
        btnBuy.setOnClickListener(listener);
        btnConsume = (Button) findViewById(R.id.btn_consume);
        btnConsume.setOnClickListener(listener);
        btnInv = (Button) findViewById(R.id.btn_inv);
        btnInv.setOnClickListener(listener);
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_buy:
                    tvShowMsg.setText("buy");
                    try {
                        mHelper.launchPurchaseFlow(MainActivity.this, SKU_1990000, 1000, onIabPurchaseFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                        System.out.println("MainActivity.onClick>>>>>>>>>>>>" + e.toString());
                    }

                    break;
                case R.id.btn_consume:
                    if (isConsume) {
                        isConsume = false;
                        btnConsume.setText("设置购买完成后不消耗");
                    } else {
                        isConsume = true;
                        btnConsume.setText("设置购买完成后消耗");
                    }
                    break;
                case R.id.btn_inv:
                    tvShowMsg.setText("query inv");
                    try {
                        /*mHelper.queryInventoryAsync(mGotInventoryListener);*/
                        mHelper.queryInventoryAsync(true,skuList,null,mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                        System.out.println("MainActivity.onClick>>>>>>>>>>>>" + e.toString());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
