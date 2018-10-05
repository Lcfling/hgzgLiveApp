package com.weitao.vcloud.live.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import android.view.WindowManager;
import android.view.ViewGroup;
import android.content.pm.ActivityInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.view.MotionEvent;
import android.text.TextUtils;
import android.widget.Toast;

import com.weitao.vcloud.live.R;
import com.weitao.vcloud.live.config.DemoServers;
import com.weitao.vcloud.live.server.entity.RoomInfoEntity;
import com.weitao.vcloud.live.DemoCache;

import com.weitao.vcloud.live.base.BaseActivity;
import com.weitao.vcloud.live.fragment.LiveEnterFragment;
import com.weitao.vcloud.live.fragment.ShortVideoMainFragment;
import com.weitao.vcloud.live.fragment.VideoMainFragment;
import com.weitao.vcloud.live.server.DemoServerHttpClient;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.weitao.vcloud.live.widget.MyWebChromeClient;

import com.wang.avi.AVLoadingIndicatorView;

import cn.sharesdk.onekeyshare.OnekeyShare;


public class MainActivity extends BaseActivity {
    public static final String TAG_LIVE_FRAGMENT = LiveEnterFragment.class.getSimpleName();
    public static final String TAG_VIDEO_FRAGMENT = VideoMainFragment.class.getSimpleName();
    public static final String TAG_SHORT_VIDEO_FRAGMENT = ShortVideoMainFragment.class.getSimpleName();
    public static final String EXTRA_FROM_UPLOAD_NOTIFY = "extra_from_upload_notify"; //由上传通知启动

    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    public static WebView myweb;
    private FrameLayout fullscreenContainer;
    public String currentFragment = TAG_LIVE_FRAGMENT;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private MyWebChromeClient mMyWebChromeClient=new MyWebChromeClient(this);
    private long mExitTime;

    View tab_live, tab_upload;
    TextView tabShortVideo;
    View btn_login_out;
    private AVLoadingIndicatorView avi;
    RelativeLayout errorPage;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //switchFragment();



    }

    boolean isFromUploadNotify;
    boolean saveBackStack;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //若用户点击 通知栏显示的上传通知,启动MainActivity, 则切换至视频上传Fragment
        isFromUploadNotify = intent.getBooleanExtra(EXTRA_FROM_UPLOAD_NOTIFY, false);
        if (isFromUploadNotify) {
            saveBackStack = true;
            currentFragment = TAG_VIDEO_FRAGMENT;
            //switchFragment();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator("BallPulseIndicator");
        errorPage=(RelativeLayout) findViewById(R.id.errorPage);
        Button refresh=(Button) findViewById(R.id.webrefresh);


        myweb = (WebView) findViewById(R.id.myweb);
        myweb.getSettings().setAppCacheMaxSize(1024*1024*20);//设置缓冲大小
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        myweb.getSettings().setAppCachePath(appCacheDir);
        myweb.getSettings().setJavaScriptEnabled(true);
        myweb.getSettings().setDomStorageEnabled(true);

        //CookieManager.getInstance().setAcceptThirdPartyCookies(myweb,true);
        myweb.getSettings().setAppCacheEnabled(true);
        myweb.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //myweb.getSettings().setAllowFileAccess(true);
        myweb.getSettings().setAllowUniversalAccessFromFileURLs(true);
        //myweb.getSettings().setAllowFileAccessFromFileURLs(true);
        myweb.addJavascriptInterface(MainActivity.this,"android");
        //myweb.loadUrl("http:live.taoleyizhan.com/sui/index.html");
        myweb.clearCache(false);
        myweb.loadUrl("http://fx.taoleyizhan.com/mobile");


        myweb.setWebViewClient(new WebViewClient(){
            /*@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器

                view.loadUrl(url);
                return true;
            }*/
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                avi.show();
                Log.d(TAG,"onPageStarted");
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                avi.hide();
                myweb.setVisibility(View.VISIBLE);
                Log.d(TAG,"onPageFinished");
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                showErrorPage();
                Log.d(TAG,"errorCode="+errorCode);
            }
        });

        myweb.setWebChromeClient(mMyWebChromeClient);
        /*myweb.setWebChromeClient(new WebChromeClient() {
            *//*** 视频播放相关的方法 **//*

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(MainActivity.this);
                frameLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }
        });*/
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myweb.reload();
                hideErrorPage();
            }
        });

    }

    protected void hideErrorPage() {
        errorPage.setVisibility(View.GONE);
        myweb.setVisibility(View.GONE);
    }
    protected void showErrorPage(){
        errorPage.setVisibility(View.VISIBLE);

    }

    public void webToUser(){
        LogUtil.e(TAG,"登录跳转");
        myweb.post(new Runnable() {
            @Override
            public void run() {
                myweb.loadUrl(DemoServers.apiServer() +"/mobile/user.php");
            }
        });
        //myweb.loadUrl(DemoServers.apiServer()+"/mobile/user.php");
    }
    @JavascriptInterface
    public void showShare(String title,String url,String src) {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(title);
        // titleUrl QQ和QQ空间跳转链接
        //oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(src);//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl(url);
        // 启动分享GUI
        oks.show(MainActivity.this);
    }
    @JavascriptInterface
    public void JsEnterLive(){
        if (TextUtils.isEmpty(DemoCache.getAccount())) {
            LogUtil.e(TAG,"数据"+DemoCache.getAccount());
            webToUser();
        } else {
            EnterLiveActivity.start(MainActivity.this);
        }

    }
    @JavascriptInterface
    public void JsEnterAudience(int roomid){
        LogUtil.e(TAG, "roomid="+roomid);
        if (TextUtils.isEmpty(DemoCache.getAccount())) {
            // 判断当前app是否正在运行
            /*if (!SysInfoUtil.stackResumed(this)) {

            }*/
            webToUser();
        }else{
            DemoServerHttpClient.getInstance().getRoomInfo(0, String.valueOf(roomid), new DemoServerHttpClient.DemoServerHttpCallback<RoomInfoEntity>() {
                @Override
                public void onSuccess(RoomInfoEntity roomInfoEntity) {
                    DialogMaker.dismissProgressDialog();
                    DemoCache.setRoomInfoEntity(roomInfoEntity);
                    if(roomInfoEntity.getStatus() !=1 && roomInfoEntity.getStatus()!=3){
                        showToast("当前房间, 不在直播中");
                        return;
                    }
                    LogUtil.e(TAG, "直接进去了");
                    LiveRoomActivity.startAudience(MainActivity.this, roomInfoEntity.getRoomid() + "", roomInfoEntity.getRtmpPullUrl(), true);
                }

                @Override
                public void onFailed(int code, String errorMsg) {
                    showToast(errorMsg);
                    DialogMaker.dismissProgressDialog();
                }
            });
        }

    }
    @JavascriptInterface
    public void login(String s){
        LogUtil.e(TAG,"登录触发成功");
        final String account=s.substring(0,11);
        final String appkey=s.substring(11,51);
        final String token=s.substring(51);
        LogUtil.e(TAG,"account="+account);
        LogUtil.e(TAG,"appkey="+appkey);
        LogUtil.e(TAG,"token="+token);
        LoginClass loginClass=new LoginClass(this);
        loginClass.login(account,token,appkey);

    }
    @JavascriptInterface
    public void getAapp(int roomid){
        LoginClass login=new LoginClass(this);
    }


    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
    @Override
    public void onBackPressed() {

        if(myweb.canGoBack()){
            myweb.goBack();
        }else{
            exit();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"收到返回消息了");

        mMyWebChromeClient.onActivityResult(requestCode,resultCode,data);
    }


    private void showCustomView(View view, CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        MainActivity.this.getWindow().getDecorView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(MainActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }
    /** 隐藏视频全屏 */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setStatusBarVisibility(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        myweb.setVisibility(View.VISIBLE);
    }
    /** 全屏容器界面 */
    static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
