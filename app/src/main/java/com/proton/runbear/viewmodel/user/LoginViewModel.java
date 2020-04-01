package com.proton.runbear.viewmodel.user;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.proton.runbear.BuildConfig;
import com.proton.runbear.R;
import com.proton.runbear.activity.HomeActivity;
import com.proton.runbear.bean.BindBean;
import com.proton.runbear.bean.WechatUserInfo;
import com.proton.runbear.component.App;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.UserCenter;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.UIUtils;
import com.proton.runbear.utils.UmengUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.BaseViewModel;
import com.tencent.bugly.crashreport.CrashReport;
import com.wms.logger.Logger;

import cn.pedant.SweetAlert.Type;

/**
 * @Description: 润生登录viewModel
 * @Author: yxf
 * @CreateDate: 2020/3/31 16:43
 * @UpdateUser: yxf
 * @UpdateDate: 2020/3/31 16:43
 */
public class LoginViewModel extends BaseViewModel {
    /**
     * 手机号码
     */
    public ObservableField<String> phoneNum = new ObservableField<>("");
    /**
     * 验证码
     */
    public ObservableField<String> codeNum = new ObservableField<>("");
    /**
     * 验证码发送文字
     */
    public ObservableField<String> sendCodeStr = new ObservableField<>(UIUtils.getString(R.string.string_resend));
    /**
     * 发送验证码按钮是否可用
     */
    public ObservableBoolean isCanSendCode = new ObservableBoolean(false);

    /**
     * 验证码能重新发送的倒计时
     */
    public ObservableField<Integer> leftTimes = new ObservableField<>(0);
    /**
     * 发送验证码后是否可以继续编辑手机号
     */
    public ObservableBoolean isCanEditPhone = new ObservableBoolean(true);

    /**
     * 验证码倒计时
     */
    private CountDownTimer mCodeCountDownTimer;

    /**
     * 微信登录的checkToken
     */
    private String checkToken;


    /**
     * 发送验证码的回调
     */
    private NetCallBack<String> mSendCodeCallback = new NetCallBack<String>() {

        @Override
        public void noNet() {
            super.noNet();
            BlackToast.show(R.string.string_please_check_your_network);
        }

        @Override
        public void onSubscribe() {
            super.onSubscribe();
            showDialog(R.string.string_send_coding);
        }

        @Override
        public void onSucceed(String data) {
            dismissDialog();
            BlackToast.show(R.string.string_get_code_success);
            doCodeCountDown(60);
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            dismissDialog();
            try {
                leftTimes.set(Integer.parseInt(resultPair.getData()));
                doCodeCountDown(leftTimes.get());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                BlackToast.show(resultPair.getData());
            }
        }
    };

    /**
     * 手机号绑定回调
     */
    class BindCallback extends NetCallBack<BindBean> {

        @Override
        public void noNet() {
            super.noNet();
            BlackToast.show(R.string.string_please_check_your_network);
        }

        @Override
        public void onSucceed(BindBean data) {
            Logger.w(data.isBind() ? "绑定手机号成功" : "绑定手机号失败");
            if (data.isBind()) {
                SpUtils.saveString(Constants.ACCOUNT, data.getMobile());
                SpUtils.saveString(Constants.APITOKEN, data.getToken());
                SpUtils.saveString(Constants.APIUID, data.getUid());
                if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                    UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                        @Override
                        public void onSucceed(Boolean data) {
                            Logger.w("友盟推送token设置成功");
                        }
                    });
                }
                CrashReport.setUserId(App.get().getApiUid());
                doLoginSuccess();
            } else {//防止绑定不成功，重复校验
                checkToken = data.getCheckToken();
                IntentUtils.goToBindPhone(getContext());
            }
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            BlackToast.show(resultPair.getData());
            dismissDialog();
        }
    }

    /**
     * 微信登录
     */
    public void weChatLogin(String code) {

        if (BuildConfig.DEBUG) {
            IntentUtils.goToBindPhone(getContext());
            return;
        }

        UserCenter.wechatLogin(code, new NetCallBack<WechatUserInfo>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSucceed(WechatUserInfo wechatUserInfo) {
                Logger.w("微信登录成功", wechatUserInfo.toString());
                if (wechatUserInfo.isBind()) {
                    Logger.w("已绑定手机号，直接进入首页");
                    SpUtils.saveString(Constants.APITOKEN, wechatUserInfo.getToken());
                    SpUtils.saveString(Constants.APIUID, wechatUserInfo.getUid());
                    SpUtils.saveString(Constants.ACCOUNT, wechatUserInfo.getMobile());
                    if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                        UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                            @Override
                            public void onSucceed(Boolean data) {
                                Logger.w("友盟推送token设置成功");
                            }
                        });
                    }
                    CrashReport.setUserId(App.get().getApiUid());
                    doLoginSuccess();
                } else {
                    Logger.w("没有绑定手机号。。。");
                    checkToken = wechatUserInfo.getCheckToken();
                    IntentUtils.goToBindPhone(getContext());
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 发送绑定手机号验证码
     */
    public void sendBindCode() {
        if (checkCanBind(false)) {
            UserCenter.sendBindCode(phoneNum.get(), checkToken, mSendCodeCallback);
        }
    }

    /**
     * 绑定手机号
     */
    public void bindPhone() {
        if (checkCanBind(true)) {
            UserCenter.bindPhone(phoneNum.get(), codeNum.get(), checkToken, new LoginViewModel.BindCallback());
        }
    }

    /**
     * 清空手机号
     */
    public void clearPhoneNum() {
        if (!TextUtils.isEmpty(phoneNum.get())) {
            phoneNum.set("");
        }
    }

    /**
     * 清空验证码
     */
    public void clearCodeNum() {
        if (!TextUtils.isEmpty(codeNum.get())) {
            codeNum.set("");
        }
    }

    /**
     * 验证码倒计时逻辑处理
     *
     * @param leftTime
     */
    public void doCodeCountDown(int leftTime) {
        mCodeCountDownTimer = new CountDownTimer(leftTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isCanSendCode.set(false);
                sendCodeStr.set((int) (millisUntilFinished / 1000) + "s");
                leftTimes.set((int) (millisUntilFinished / 1000));
                Logger.w("left time is :", leftTimes.get());
            }

            @Override
            public void onFinish() {
                isCanEditPhone.set(true);
                isCanSendCode.set(true);
                sendCodeStr.set(UIUtils.getString(R.string.string_resend));
                leftTimes.set(0);
            }
        };
        isCanEditPhone.set(false);
        mCodeCountDownTimer.start();
    }

    /**
     * 检查是否能绑定
     */
    private boolean checkCanBind(boolean checkCode) {
        if (!Utils.isMobilePhone(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return false;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return false;
        }
        if (checkCode) {
            if (TextUtils.isEmpty(codeNum.get())) {
                BlackToast.show(R.string.string_code_can_not_be_null);
                return false;
            }
        }
        return true;
    }

    /**
     * 校验是否满足点击绑定手机号按钮的条件
     *
     * @param phone
     * @param code
     * @return
     */
    public boolean checkCanBindPhone(String phone, String code) {
        if (!Utils.isMobilePhone(phone)) {
            return false;
        }
        if (!Utils.isMobile(phone)) {
            return false;
        }

        if (TextUtils.isEmpty(code) || code.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * 登录成功进入首页
     */
    private void doLoginSuccess() {
        showDialog(R.string.string_login_success, Type.SUCCESS_MSG_TYPE);
        //开启阿里云服务
        IntentUtils.startAliyunService(getContext());
        //通知需要因为登录状态而刷新的地方
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.LOGIN));
        getDialog().setOnDismissListener(dialog -> {
            //首次使用app,是否选择过设备型号，没有则选择
            IntentUtils.goToMain(getContext());
            ActivityManager.finishOthersActivity(HomeActivity.class);

        });
        dismissDialog();
    }
}
