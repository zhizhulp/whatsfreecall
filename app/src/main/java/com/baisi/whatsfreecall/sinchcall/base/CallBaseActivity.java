package com.baisi.whatsfreecall.sinchcall.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.sinchcall.service.SinchService;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;


/**
 * Created by MnyZhao on 2017/12/12.
 */

public class CallBaseActivity extends BaseActivity implements ServiceConnection {
    private SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    /**
     * 启动Sinch
     */
    protected void signSinch() {
        System.out.println("CallBaseActivity.signSinch>>>>" + "start");
        if(getSinchServiceInterface()!=null){
            if (!getSinchServiceInterface().isStarted()) {
           /* 启动sinch客户端*/
                getSinchServiceInterface().startClient(SpUtils.getString(getApplicationContext(), SpConfig.USER_NAME, "unlogin"));
            }
        }
    }

    /**
     * 检测sinch客户端是否启动若启动则执行操作未启动则再次启动
     * @return
     */
    protected boolean checkSinchisStartAtNull(){
        if(getSinchServiceInterface()!=null&&getSinchServiceInterface().isStarted()){
            return true;
        }
        return false;
    }
}
