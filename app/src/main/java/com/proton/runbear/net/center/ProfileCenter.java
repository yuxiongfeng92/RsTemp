package com.proton.runbear.net.center;

import com.google.gson.reflect.TypeToken;
import com.proton.runbear.database.ProfileManager;
import com.proton.runbear.net.RetrofitHelper;
import com.proton.runbear.net.bean.AddProfileReq;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.NetSubscriber;
import com.proton.runbear.net.callback.ParseResultException;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.utils.JSONUtils;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                        ProfileManager.saveAll(profiles);
                        return profiles;
                    } else {
                        throw new ParseResultException(resultPair.getData());
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

    public static void addProfile(NetCallBack<ProfileBean> resultPairNetCallBack, AddProfileReq req) {
        RetrofitHelper.getProfileApi().addProfile("application/json", req).map(
                json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair != null && resultPair.isSuccess()) {
                        ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                        ProfileManager.save(profileBean);
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("profileid", profileId);
        RetrofitHelper.getProfileApi().deleteProfile(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                ProfileManager.delete(profileId);
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
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
    public static void editProfile(HashMap<String, String> map, NetCallBack<ProfileBean> netCallBack) {
        RetrofitHelper.getProfileApi().editProfile(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                ProfileManager.update(profileBean);
                return profileBean;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ProfileBean>(netCallBack) {
            @Override
            public void onNext(ProfileBean profileBean) {
                netCallBack.onSucceed(profileBean);
            }
        });
    }
}
