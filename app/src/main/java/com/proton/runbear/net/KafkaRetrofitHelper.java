package com.proton.runbear.net;

import android.text.TextUtils;
import android.util.Log;

import com.proton.runbear.BuildConfig;
import com.proton.runbear.component.App;
import com.proton.runbear.net.api.ArticleApi;
import com.proton.runbear.net.api.ManagerCenterApi;
import com.proton.runbear.net.api.MeasureCenterApi;
import com.proton.runbear.net.api.ProfileApi;
import com.proton.runbear.net.api.ReportResultApi;
import com.proton.runbear.net.api.SystemMessagCenterApi;
import com.proton.runbear.net.api.UserApi;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.Settings;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class KafkaRetrofitHelper {

    private static Retrofit mRetrofit;
    private static UserApi mUserApi;
    private static ArticleApi mArticleApi;
    private static Cache cache = null;
    private static File httpCacheDirectory;
    private static ManagerCenterApi managerCenterApi;//管理中心
    private static SystemMessagCenterApi systemMessagCenterApi;//消息中心
    private static ReportResultApi reportResultApi;//测量报告
    private static ProfileApi profitFileManageApi;//档案管理
    /**
     * 测量中心
     */
    private static MeasureCenterApi measureCenterApi;

    private KafkaRetrofitHelper() {
    }

    public static void addHeader() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(KafkaRetrofitHelper::addHeaderInterceptor)
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();

        mRetrofit = mRetrofit.newBuilder()
                .baseUrl(BuildConfig.KAFAKA_SERVER_PATH)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        mUserApi = mRetrofit.create(UserApi.class);
    }

    private static Response addHeaderInterceptor(Interceptor.Chain chain) throws IOException {
        String requestToken = App.get().getToken();
        String requestUid = App.get().getApiUid();
        Request.Builder builder = chain.request().newBuilder()
                .addHeader("company", Settings.COMPANY)
                .addHeader("user-agent", "Android")
                .addHeader("version", App.get().getVersion())
                .addHeader("model", App.get().getSystemInfo());
        if (!TextUtils.isEmpty(requestToken) && !TextUtils.isEmpty(requestUid)) {
            builder.addHeader(Constants.APITOKEN, requestToken);
            builder.addHeader(Constants.APIUID, requestUid);
        }
        return chain.proceed(builder.build());
    }

    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    public static <T> T create(final Class<T> service) {
        if (mRetrofit == null) {
            init();
        }
        return mRetrofit.create(service);
    }

    private static void init() {
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(App.get().getCacheDir(), "net_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
            }
        } catch (Exception e) {
            Log.e("OKHttp", "Could not create http cache", e);
        }

        //获取request
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(KafkaRetrofitHelper::addHeaderInterceptor)
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.KAFAKA_SERVER_PATH)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        mUserApi = mRetrofit.create(UserApi.class);
    }

    /**
     * @return 用户中心api
     */
    public static UserApi getUserApi() {
        if (null == mUserApi)
            mUserApi = create(UserApi.class);
        return mUserApi;
    }

    /**
     * @return 管理中心api
     */
    public static ManagerCenterApi getManagerCenterApi() {
        if (null == managerCenterApi)
            managerCenterApi = create(ManagerCenterApi.class);
        return managerCenterApi;
    }

    public static ArticleApi getArticleApi() {
        if (null == mArticleApi)
            mArticleApi = create(ArticleApi.class);
        return mArticleApi;
    }

    public static SystemMessagCenterApi getMsgCenterApi() {
        if (null == systemMessagCenterApi) {
            systemMessagCenterApi = create(SystemMessagCenterApi.class);
        }
        return systemMessagCenterApi;
    }

    public static ReportResultApi getReportResultApi() {
        if (null == reportResultApi) {
            reportResultApi = create(ReportResultApi.class);
        }
        return reportResultApi;
    }

    public static ProfileApi getProfileApi() {
        if (null == profitFileManageApi) {
            profitFileManageApi = create(ProfileApi.class);
        }
        return profitFileManageApi;
    }

    public static MeasureCenterApi getMeasureCenterApi() {
        if (null == measureCenterApi) {
            measureCenterApi = create(MeasureCenterApi.class);
        }
        return measureCenterApi;
    }

}
