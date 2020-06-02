package com.proton.runbear.net.center;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.proton.runbear.net.RetrofitHelper;
import com.proton.runbear.net.bean.ConfigInfo;
import com.proton.runbear.net.bean.MeasureBeginResp;
import com.proton.runbear.net.bean.MeasureEndResp;
import com.proton.runbear.net.bean.ShareHistoryBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.NetSubscriber;
import com.proton.runbear.net.callback.ParseResultException;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.utils.JSONUtils;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;


/**
 * 测量请求
 */

public class MeasureCenter extends DataCenter {

    /**
     * 开始测量
     *
     * @param mac
     * @param pid
     * @param callBack
     */
    public static void measureBegin(String mac, String pid, NetCallBack<MeasureBeginResp> callBack) {
        WeakHashMap<String, String> params = new WeakHashMap<>();
        params.put("mac", mac);
        params.put("pid", pid);
        RetrofitHelper.getMeasureCenterApi().measureBegin(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        MeasureBeginResp beginResp = JSONUtils.getObj(resultPair.getData(), MeasureBeginResp.class);
                        return beginResp;
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<MeasureBeginResp>(callBack) {
                    @Override
                    public void onNext(MeasureBeginResp measureBeginResp) {
                        callBack.onSucceed(measureBeginResp);
                    }
                });
    }

    /**
     * 测量结束
     *
     * @param examid
     * @param callBack
     */
    public static void measureEnd(String examid, NetCallBack<MeasureEndResp> callBack) {
        WeakHashMap<String, String> params = new WeakHashMap<>();
        params.put("examid", examid);
        RetrofitHelper.getMeasureCenterApi().measureEnd(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        MeasureEndResp beginResp = JSONUtils.getObj(resultPair.getData(), MeasureEndResp.class);
                        return beginResp;
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<MeasureEndResp>(callBack) {
                    @Override
                    public void onNext(MeasureEndResp measureEndResp) {
                        callBack.onSucceed(measureEndResp);
                    }
                });
    }

    public static void fetchConfigInfo(String phone, NetCallBack<ConfigInfo> callBack) {
        WeakHashMap<String, String> params = new WeakHashMap<>();
        params.put("phone", phone);
        RetrofitHelper.getMeasureCenterApi().getConfigInfo(params)
                .map(s -> {
                    if (TextUtils.isEmpty(s) || s.equalsIgnoreCase("null")) {
                        throw new ParseResultException("配置信息为空");
                    } else {
                        ResultPair resultPair = new ResultPair();
                        resultPair.setSuccess(true);
                        resultPair.setData(s);
                        if (resultPair.isSuccess()) {
                            ConfigInfo configInfo = JSONUtils.getObj(resultPair.getData(), ConfigInfo.class);
                            return configInfo;
                        } else {
                            throw new ParseResultException(resultPair.getData());
                        }
                    }
                }).compose(threadTrans())
                .subscribe(new NetSubscriber<ConfigInfo>(callBack) {
                    @Override
                    public void onNext(ConfigInfo configInfo) {
                        callBack.onSucceed(configInfo);
                    }
                });

    }

    /**
     * 分享设备
     */
    public static void shareDevice(String deviceId, String mobile, String profileId, NetCallBack<ResultPair> netCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceid", deviceId);
        map.put("mobile", mobile);
        map.put("profileid", profileId);
        RetrofitHelper.getMeasureCenterApi().shareDevice(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(netCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 获取一个设备的共享历史
     */
    public static void getShareHistory(String profileid, NetCallBack<List<ShareHistoryBean>> netCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("profileid", profileid);
        RetrofitHelper.getMeasureCenterApi().getShareHistory(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                Type type = new TypeToken<List<ShareHistoryBean>>() {
                }.getType();
                return JSONUtils.<ShareHistoryBean>getObj(resultPair.getData(), type);
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<List<ShareHistoryBean>>(netCallBack) {
            @Override
            public void onNext(List<ShareHistoryBean> shareHistoryBean) {
                netCallBack.onSucceed(shareHistoryBean);
            }
        });
    }

    /**
     * 取消向当前这个手机号用户的分享
     */
    public static void cancelShare(String mobile, String profileId, NetCallBack<ResultPair> netCallBack) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("profileid", profileId);
        RetrofitHelper.getMeasureCenterApi().cancelShareHistory(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(netCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 取消向当前这个手机号用户的分享
     */
    public static void getShareWechatUrl(long profileId, NetCallBack<String> netCallBack) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("profileId", profileId);
        RetrofitHelper.getMeasureCenterApi().getShareWechatUrl(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return JSONUtils.getString(resultPair.getData(), "url");
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<String>(netCallBack) {
            @Override
            public void onNext(String resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

}
