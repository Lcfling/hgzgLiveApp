package com.weitao.vcloud.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.weitao.vcloud.live.R;
import com.weitao.vcloud.live.nim.config.perference.Preferences;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * Created by hzxuwen on 2016/6/12.
 */
public class WelcomeActivity extends Activity{

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private static boolean firstEnter = true; // 首次进入

    private boolean customSplash = false;
    public static final String DATABASE = "Database";
    public static final String PATH = "/data/data/code.sharedpreferences/shared_prefs/Database.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        if (!firstEnter) {
            onIntent();

        } else {
            showSplashView();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    onIntent();
                }
            };
            if (customSplash) {
                new Handler().postDelayed(runnable, 1000);
            } else {
                runnable.run();
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        onIntent();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        LogUtil.i(TAG, "onIntent...");

        /*if (TextUtils.isEmpty(DemoCache.getAccount()) || TextUtils.isEmpty(DemoCache.getVodtoken())) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
                LoginActivity.start(this);
            }
            finish();
        } else {*/
        canAutoLogin();
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
        SharedPreferences sp = getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(sp.getString("local","").equals("")){
            editor.putString("local", "1");
            editor.commit();
            showGuideActivity();
        }else{
            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }

       /* }*/
    }

    /**
     * 已经登陆过，自动登陆
     */
    private boolean canAutoLogin() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();
        String appkey = Preferences.getUserappkey();
        boolean loginState = Preferences.getLoginState();
        LoginClass loginClass=new LoginClass(this);
        loginClass.login(account,token,appkey);

        return loginState && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }


    /**
     * 首次进入，打开欢迎界面
     */
    private void showSplashView() {
        getWindow().setBackgroundDrawableResource(R.drawable.ic_welcome);
        customSplash = true;
    }
    private void showGuideActivity() {
        //Toast.makeText(this, " showGuide: ", Toast.LENGTH_SHORT).show();
        showGuideActivity(null);
    }

    private void showGuideActivity(Intent intent) {
        startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
        finish();
    }

    private void showMainActivity() {
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
