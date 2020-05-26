package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityWechatLoginLayoutBinding;
import com.proton.runbear.socailauth.PlatformType;
import com.proton.runbear.socailauth.SocialApi;
import com.proton.runbear.socailauth.listener.AuthListener;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.user.LoginViewModel;
import com.wms.logger.Logger;

import java.util.Map;

public class WeChatLoginActivity extends BaseViewModelActivity<ActivityWechatLoginLayoutBinding, LoginViewModel> {

    /**
     * 第三方登录工具类
     */
    private SocialApi mSocialApi;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_wechat_login_layout;
    }

    @Override
    protected LoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void init() {
        super.init();
        Utils.checkUpdate(this, false);
        mSocialApi = SocialApi.get(this);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idLogin.setOnClickListener(v -> weChatLogin());
    }

    private void weChatLogin() {
        if (true) {
            IntentUtils.goToMain(mContext);
        } else {
            mSocialApi.doOauthVerify(this, PlatformType.WEIXIN, new MyAuthListener());
        }
    }

    public class MyAuthListener implements AuthListener {
        @Override
        public void onComplete(PlatformType platform_type, Map<String, String> map) {
            String code = map.get("code");
            Logger.w("微信登录code :", code);
//            viewmodel.weChatLogin(code);
        }

        @Override
        public void onError(PlatformType platform_type, String err_msg) {
            Logger.w("微信登录code", err_msg);
            BlackToast.show(err_msg);
        }

        @Override
        public void onCancel(PlatformType platform_type) {
            Logger.w("微信登录code", "login onCancel");
            BlackToast.show(R.string.string_login_cancel);
        }
    }

    @Override
    public String getTopCenterText() {
        return "体温管家APP简版";
    }

    @Override
    protected boolean showBackBtn() {
        return false;
    }
}
