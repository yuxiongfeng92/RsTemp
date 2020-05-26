package com.proton.runbear.net.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface UserApi {

    /**
     * 发送短信验证码(通用接口)
     */
    String sendSms = "account/sendsms";

    /**
     * 注册
     */
    String register = "account/register";

    /**
     * 登录
     */
    String login = "account/login";

    /**
     * 退出登录
     */
    String logout = "account/logout";

    /**
     * 发送短信
     *
     * @param params
     * @return
     */
    @GET(sendSms)
    Observable<String> sendSms(@QueryMap Map<String, String> params);

    /**
     * 注册
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(register)
    Observable<String> register(@FieldMap Map<String, String> params);

    /**
     * 登录
     *
     * @param params
     * @return
     */
    @GET(login)
    Observable<String> login(@QueryMap Map<String, String> params);

    /**
     * 退出登录
     *
     * @return
     */
    @GET(logout)
    Observable<String> logout();


}
