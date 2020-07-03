package com.proton.runbear.net.center;

import com.google.gson.reflect.TypeToken;
import com.proton.runbear.net.RetrofitHelper;
import com.proton.runbear.net.bean.AddProfileReq;
import com.proton.runbear.net.bean.DeleteProfileReq;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.bean.UpdateProfileReq;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.NetSubscriber;
import com.proton.runbear.net.callback.ParseResultException;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.utils.JSONUtils;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ProfileCenter extends DataCenter {

    /**
     * 获取档案列表
     */
    public static void getProfileList(NetCallBack<List<ProfileBean>> netCallBack) {
        RetrofitHelper
                .getProfileApi()
                .getProfileFileList()
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        Type type = new TypeToken<List<ProfileBean>>() {
                        }.getType();
                        List<ProfileBean> profiles = JSONUtils.getObj(resultPair.getData(), type);
                        return profiles;
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<List<ProfileBean>>(netCallBack) {
                    @Override
                    public void onNext(List<ProfileBean> profileMangeItemList) {
                        netCallBack.onSucceed(profileMangeItemList);
                    }
                });
    }

    /**
     * 根据pid获取档案详情
     *
     * @param profileId
     * @param callBack
     */
    public static void getProfileByProfileId(long profileId, NetCallBack<ProfileBean> callBack) {
        Map<String, Long> params = new WeakHashMap<>();
        params.put("PID", profileId);
        RetrofitHelper.getProfileApi().getProfileById(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                        return profileBean;
                    } else {
                        throw new ParseResultException(resultPair.getErrorMessage());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<ProfileBean>(callBack) {
                    @Override
                    public void onNext(ProfileBean profileBean) {
                        callBack.onSucceed(profileBean);
                    }
                });
    }

    public static void addProfile(NetCallBack<ProfileBean> resultPairNetCallBack, AddProfileReq req) {
        RetrofitHelper.getProfileApi().addProfile("application/json", req).map(
                json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair != null && resultPair.isSuccess()) {
                        ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                        return profileBean;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }
        ).compose(threadTrans()).subscribe(new NetSubscriber<ProfileBean>(resultPairNetCallBack) {
            @Override
            public void onNext(ProfileBean profileBean) {
                resultPairNetCallBack.onSucceed(profileBean);
            }
        });
    }

    /**
     * 删除档案
     */
    public static void deleteProfile(long profileId, NetCallBack<ResultPair> resultPairNetCallBack) {
        DeleteProfileReq req = new DeleteProfileReq();
        req.setPID(profileId);
        RetrofitHelper.getProfileApi().deleteProfile("application/json", req).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getErrorMessage());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 编辑档案
     */
    public static void editProfile(UpdateProfileReq req, NetCallBack<ProfileBean> netCallBack) {
        RetrofitHelper.getProfileApi().editProfile("application/json", req).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                return profileBean;
            } else {
                throw new ParseResultException(resultPair.getErrorMessage());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ProfileBean>(netCallBack) {
            @Override
            public void onNext(ProfileBean profileBean) {
                netCallBack.onSucceed(profileBean);
            }
        });
    }
}
