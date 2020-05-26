package com.proton.runbear.net.center;

import com.proton.runbear.net.RetrofitHelper;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.NetSubscriber;
import com.proton.runbear.net.callback.ParseResultException;
import com.proton.runbear.net.callback.ResultPair;

import java.util.Map;
import java.util.WeakHashMap;

public class UserCenter extends DataCenter {

    /**
     * 发送验证码，说明：注册和登录的验证码都是通过这个接口返回
     *
     * @param phone
     * @param callBack
     */
    public static void sendSms(String phone, NetCallBack<String> callBack) {
        Map<String, String> params = new WeakHashMap<>();
        params.put("phone", phone);
        RetrofitHelper.getUserApi().sendSms(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String data) {
                        callBack.onSucceed(data);
                    }
                });
    }

    /**
     * 注册
     *
     * @param name
     * @param phone
     * @param code
     * @param callBack
     */
    public static void register(String name, String phone, String code, NetCallBack<String> callBack) {
        Map<String, String> params = new WeakHashMap<>();
        params.put("Name", name);
        params.put("Phone", phone);
        params.put("SmsCode", code);
        RetrofitHelper.getUserApi().register(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String result) {
                        callBack.onSucceed(result);
                    }
                });
    }

    /**
     * 登录
     *
     * @param phone
     * @param code
     * @param callBack
     */
    public static void login(String phone, String code, NetCallBack<String> callBack) {
        Map<String, String> params = new WeakHashMap<>();
        params.put("Phone", phone);
        params.put("SmsCode", code);
        RetrofitHelper.getUserApi().login(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String s) {
                        callBack.onSucceed(s);
                    }
                });
    }

    /**
     * 退出登录
     */
    public static void logout(NetCallBack<Boolean> callBack) {
        RetrofitHelper.getUserApi().logout()
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return true;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        callBack.onSucceed(aBoolean);
                    }
                });
    }

}
