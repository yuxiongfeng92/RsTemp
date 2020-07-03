package com.proton.runbear.viewmodel.user;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.proton.runbear.R;
import com.proton.runbear.activity.HomeActivity;
import com.proton.runbear.component.App;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.UserInfo;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.UserCenter;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.JSONUtils;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.UIUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.BaseViewModel;
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
     * 用户名
     */
    public ObservableField<String> userName = new ObservableField<>("");

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


    Observable.OnPropertyChangedCallback phoneChangeCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (!Utils.isMobilePhone(phoneNum.get()) || !Utils.isMobile(phoneNum.get())) {
                isCanSendCode.set(false);
            } else {
                isCanSendCode.set(true);
            }
        }
    };

    public LoginViewModel() {
        phoneNum.addOnPropertyChangedCallback(phoneChangeCallback);
    }

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
     * 发送绑定手机号验证码
     */
    public void sendRegisterCode() {
        if (checkCanSend(false, false)) {
            UserCenter.sendSms(phoneNum.get(), mSendCodeCallback);
        }
    }

    /**
     * 注册
     */
    public void register() {
        if (checkCanSend(true, true)) {
            UserCenter.register(userName.get(), phoneNum.get(), codeNum.get(), new NetCallBack<String>() {
                @Override
                public void noNet() {
                    super.noNet();
                    BlackToast.show(R.string.string_no_net);
                }

                @Override
                public void onSucceed(String result) {
                    Logger.w("注册成功,用户信息：", result);
                    UserInfo userInfo = JSONUtils.getObj(result, UserInfo.class);
                    SpUtils.saveString(Constants.APITOKEN, userInfo.getToken());
                    SpUtils.saveString(Constants.APIUID, String.valueOf(userInfo.getAccount().getGuarderID()));
                    SpUtils.saveString(Constants.PHONE,userInfo.getAccount().getPhone());
                    doLoginSuccess();
                }

                @Override
                public void onFailed(ResultPair resultPair) {
                    super.onFailed(resultPair);
                    BlackToast.show(resultPair.getData());
                }
            });
        }
    }

    /**
     * 登录
     */
    public void login() {
        if (checkCanSend(true, false)) {
            UserCenter.login(phoneNum.get(), codeNum.get(), new NetCallBack<String>() {
                @Override
                public void noNet() {
                    super.noNet();
                    BlackToast.show(R.string.string_no_net);
                }

                @Override
                public void onSucceed(String result) {
                    Logger.w("登录成功，result is :", result);
                    UserInfo userInfo = JSONUtils.getObj(result, UserInfo.class);
                    SpUtils.saveString(Constants.APITOKEN, userInfo.getToken());
                    SpUtils.saveString(Constants.APIUID, String.valueOf(userInfo.getAccount().getGuarderID()));
                    SpUtils.saveString(Constants.PHONE,userInfo.getAccount().getPhone());
                    doLoginSuccess();
                }

                @Override
                public void onFailed(ResultPair resultPair) {
                    super.onFailed(resultPair);
                    BlackToast.show(resultPair.getData());
                }
            });

        }
    }

    /**
     * 退出登录
     */
    public void logout() {
        UserCenter.logout(new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(Boolean data) {
                Logger.w("退出登录成功");
                App.get().logout();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }


    /**
     * 跳转到登录页面
     */
    public void goToLogin() {
        IntentUtils.goToLogin(getContext());
        finishActivity();
    }

    /**
     * 跳转到注册页面
     */
    public void goToRegister() {
        IntentUtils.goToRegist(getContext());
        finishActivity();
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
    private boolean checkCanSend(boolean checkCode, boolean checkRegister) {

        if (checkRegister) {
            if (TextUtils.isEmpty(userName.get())) {
                BlackToast.show(R.string.string_username_not_be_null);
                return false;
            }
        }

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
            if (codeNum.get().length() < 6) {
                BlackToast.show(R.string.string_code_invalid);
                return false;
            }
        }
        return true;
    }

    /**
     * 登录成功进入首页
     */
    private void doLoginSuccess() {
        showDialog(R.string.string_login_success, Type.SUCCESS_MSG_TYPE);
        //开启阿里云服务
//        IntentUtils.startAliyunService(getContext());
        //通知需要因为登录状态而刷新的地方
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.LOGIN));
        getDialog().setOnDismissListener(dialog -> {
            //首次使用app,是否选择过设备型号，没有则选择
            IntentUtils.goToMain(getContext());
            ActivityManager.finishOthersActivity(HomeActivity.class);

        });
        dismissDialog();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mCodeCountDownTimer != null) {
            mCodeCountDownTimer.cancel();
        }

    }
}
