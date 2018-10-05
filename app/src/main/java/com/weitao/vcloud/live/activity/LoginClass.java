package com.weitao.vcloud.live.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.weitao.vcloud.live.DemoCache;

import com.weitao.vcloud.live.nim.config.perference.Preferences;

import com.weitao.vcloud.live.util.file.AssetCopyer;

import com.netease.nim.uikit.common.ui.dialog.DialogMaker;


import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.io.IOException;

/**
 * 登录/注册界面
 */
public class LoginClass{

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String KICK_OUT = "KICK_OUT";
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    private String AppKey;
    private String Token;

    private Context context;
    // data
    private AbortableFuture<LoginInfo> loginRequest;


    public LoginClass(Context context) {
        this.context = context;
    }

    /**
     * 基本权限管理
     */
    private void requestBasicPermission(Activity activity) {
        MPermission.with(activity)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();
    }

    public void onRequestPermissionsResult(Activity activity,int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
    }

    private boolean bWritePermission;

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        //Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        bWritePermission = true;
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        //Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
        bWritePermission = false;
    }


    public void getkey(String string){
        int size=string.length();
    }

    /**
     * ***************************************** 登录 **************************************
     */

    /**
     * 登录应用服务器
     */
    public void login(final String account, final String token,final String appkey) {
        /*if (!NetworkUtil.isNetAvailable(context)) {
            Toast.makeText(context, R.string.net_broken, Toast.LENGTH_SHORT).show();
            return;
        }
        DialogMaker.showProgressDialog(context, null, "登录中", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);*/

        // 账号密码登录
        //Toast.makeText(context,"登录中", Toast.LENGTH_SHORT).show();
        DemoCache.setAccount(account);
        saveLoginInfo(account, token, appkey);
        Log.e(TAG,"token="+ token+"|||account="+account);
        loginNim(account,token);

        /*DemoServerHttpClient.getInstance().login(account, token, new DemoServerHttpClient.DemoServerHttpCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DemoCache.setAccount(account);
                saveLoginInfo(account, token);

                loginNim(account, MD5.getStringMD5(token));
            }
            @Override
            public void onFailed(int code, String errorMsg) {
                onLoginDone();
                Toast.makeText(context, "登录失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void loginNim(final String account, final String token) {
        NIMClient.getService(AuthService.class).login(new LoginInfo(account, token)).setCallback(new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                onLoginDone();
            }
            @Override
            public void onFailed(int i) {
                onLoginDone();
                Toast.makeText(context, "IM登录失败: " + i, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onException(Throwable throwable) {
                onLoginDone();
            }
        });
    }



    private boolean checkWritePermission() {
        if (bWritePermission) {
            return true;
        } else {
            Toast.makeText(context, "授权读写存储卡权限后,才能正常使用", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void initAsset() {
        AssetCopyer assetCopyer = new AssetCopyer(context);
        try {
            assetCopyer.copy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLoginInfo(final String account, final String token,final String appKey) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
        Preferences.saveUserappkey(appKey);
        Preferences.saveVodToken(DemoCache.getVodtoken());
        Preferences.saveRememberAccountToken(true);
        Preferences.saveLoginState(true);
    }
}
