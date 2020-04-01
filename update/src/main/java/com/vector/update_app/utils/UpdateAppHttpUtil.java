package com.vector.update_app.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.vector.update_app.HttpManager;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.download.SimpleDownloadListener;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Vector
 * on 2017/6/19 0019.
 */

public class UpdateAppHttpUtil implements HttpManager {

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateAppHttpUtil(Context context) {
        NoHttp.initialize(context);
    }

    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, @NonNull final Callback callBack) {
        asyncGet(url, params, null, callBack);
    }

    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, Map<String, String> header, @NonNull final Callback callBack) {
        doPostAndGet(url, params, header, RequestMethod.GET, callBack);
    }

    /**
     * 异步post
     *
     * @param url      post请求地址
     * @param params   post请求参数
     * @param callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, @NonNull final Callback callBack) {
        asyncPost(url, params, null, callBack);
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, Map<String, String> header, @NonNull final Callback callBack) {
        doPostAndGet(url, params, header, RequestMethod.POST, callBack);
    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull final String url, @NonNull final String path, @NonNull final String fileName, @NonNull final FileCallback callback) {
        callback.onBefore();
        DownloadRequest request = new DownloadRequest(url, RequestMethod.GET, path, true, false);
        final DownloadQueue queue = NoHttp.newDownloadQueue();
        queue.add(0, request, new SimpleDownloadListener() {
            @Override
            public void onStart(int what, boolean resume, long range, Headers headers, long size) {
                callback.onProgress(range * 1.0f / size, size);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                callback.onProgress(progress * 1.0f / 100, fileCount);
            }

            @Override
            public void onFinish(int what, String filePath) {
                callback.onResponse(new File(filePath));
            }

            @Override
            public void onDownloadError(int what, Exception exception) {
                super.onDownloadError(what, exception);
                queue.stop();
                redownload(url, path, fileName, callback);
            }

            @Override
            public void onCancel(int what) {
                super.onCancel(what);
                queue.stop();
                redownload(url, path, fileName, callback);
            }
        });
    }

    private void doPostAndGet(@NonNull String url, @NonNull Map<String, Object> params, Map<String, String> header, RequestMethod method, @NonNull final Callback callBack) {
        StringRequest request = new StringRequest(url, method);
        request.add(params);
        if (header != null && header.size() > 0) {
            Iterator<String> iterator = header.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                request.addHeader(key, header.get(key));
            }
        }

        NoHttp.getRequestQueueInstance().add(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                callBack.onResponse(response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                callBack.onError(response.get());
            }
        });
    }

    private void redownload(@NonNull final String url, @NonNull final String path, @NonNull final String fileName, @NonNull final FileCallback callback) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                download(url, path, fileName, callback);
            }
        }, 5000);
    }
}