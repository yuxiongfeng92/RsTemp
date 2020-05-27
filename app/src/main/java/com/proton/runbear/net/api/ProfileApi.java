package com.proton.runbear.net.api;

import com.proton.runbear.net.bean.AddProfileReq;
import com.proton.runbear.net.bean.DeleteProfileReq;
import com.proton.runbear.net.bean.UpdateProfileReq;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ProfileApi {

    /**
     * 新增档案
     */
    String addProfile = "docment/insert";

    /**
     * 删除档案
     */
    String deleteProfile = "docment/delete";

    /**
     * 更新档案
     */
    String editProfile = "docment/update";

    /**
     * 按PID查询档案
     */
    String queryProfileById = "docment/findbypid";

    /**
     * 查询所有患者档案
     */
    String getProfileList = "docment/findbyaccount";

    @POST(addProfile)
    Observable<String> addProfile(@Header("Content-Type") String contentType, @Body AddProfileReq req);

    @POST(deleteProfile)
    Observable<String> deleteProfile(@Header("Content-Type") String contentType, @Body DeleteProfileReq req);

    /**
     * 编辑档案
     *
     * @param contentType
     * @param req
     * @return
     */
    @POST(editProfile)
    Observable<String> editProfile(@Header("Content-Type") String contentType, @Body UpdateProfileReq req);

    @GET(queryProfileById)
    Observable<String> getProfileById(@QueryMap Map<String, Long> map);

    @GET(getProfileList)
    Observable<String> getProfileFileList();

}
