package com.proton.runbear.activity.common;

import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.proton.runbear.R;
import com.proton.runbear.activity.HomeActivity;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.activity.measure.AddNewDeviceActivity;
import com.proton.runbear.databinding.ActivityGlobalWebBinding;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.PageUtils;
import com.proton.runbear.utils.Utils;

/**
 * Created by wangmengsi on 2018/1/31.
 */

public class GlobalWebActivity extends BaseActivity<ActivityGlobalWebBinding> {

    private String url;
    private AgentWeb mAgentWeb;
    private String inTitle;
    private boolean backBtnClose;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_global_web;
    }

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        url = mIntent.getStringExtra("url");
        inTitle = mIntent.getStringExtra("title");
        backBtnClose = mIntent.getBooleanExtra("backBtnClose", false);
    }

    @Override
    public void initData() {
        super.initData();
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(binding.idRootview
                        , new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        //传入进来标题有值使用传入的否者使用网页标题
                        if (TextUtils.isEmpty(inTitle)) {
                            binding.idTopNavigation.title.setText(title);
                        } else {
                            binding.idTopNavigation.title.setText(inTitle);
                        }
                    }
                })
                .createAgentWeb().ready().go(url);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("proton", new AndroidJSInterface());
    }

    @Override
    protected int getBackIcon() {
        if (backBtnClose) {
            return R.drawable.close_x_blue;
        }
        return R.drawable.btn_back_img;
    }

    @Override
    protected boolean showBackBtn() {
        return true;
    }

    @Override
    protected void onActionBackClick() {
        if (backBtnClose) {
            if (ActivityManager.findActivity(AddNewDeviceActivity.class) != null) {
                ActivityManager.finishActivity(ScanQRCodeActivity.class);
            } else {
                IntentUtils.goToMain(mContext);
            }

            if (!ActivityManager.hasActivity(HomeActivity.class)) {
                IntentUtils.goToMain(mContext);
            }
            finish();
            return;
        }
        if (!mAgentWeb.back()) {// true表示AgentWeb处理了该事件
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onActionBackClick();
    }

    @Override
    protected void onDestroy() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected String getStatName() {
        return PageUtils.getWebStateName(url);
    }

    class AndroidJSInterface {

        /**
         * 跳转到配网
         */
        @JavascriptInterface
        public void goToDockerSetNet() {
            IntentUtils.goToDockerSetNetwork(mContext);
        }

    }
}
