package com.proton.runbear.utils;

import com.proton.runbear.component.App;
import com.proton.runbear.component.PushService;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.center.UserCenter;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.wms.logger.Logger;


/**
 * Created by Api on 2016/11/3.
 */

public class UmengUtils {

    private static String deviceToken;

    public static void initUmeng() {
        //友盟推送
        PushAgent mPushAgent = PushAgent.getInstance(App.get());
        mPushAgent.setPushIntentServiceClass(PushService.class);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String token) {
                //注册成功会返回device token
                deviceToken = token;
                Logger.w("友盟deviceToken:", deviceToken);
//                if (App.get().isLogined()) {
//                    //登录直接设置
//                    UserCenter.setUmengToken(deviceToken, new NetCallBack<Boolean>() {
//                        @Override
//                        public void onSucceed(Boolean data) {
//                        }
//                    });
//                }
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });


    }

    public static String getDeviceToken() {
        return deviceToken;
    }
}
