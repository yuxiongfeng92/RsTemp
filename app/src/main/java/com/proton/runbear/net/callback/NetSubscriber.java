package com.proton.runbear.net.callback;

import android.text.TextUtils;

import com.proton.runbear.R;
import com.proton.runbear.activity.user.LoginActivity;
import com.proton.runbear.component.App;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.FileUtils;
import com.proton.runbear.utils.JSONUtils;
import com.proton.runbear.utils.UIUtils;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class NetSubscriber<T> implements Observer<T> {
    private NetCallBack mCallBack;

    public NetSubscriber(NetCallBack<T> callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (!NetUtils.isConnected(App.get())) {
            if (mCallBack != null) {
                mCallBack.noNet();
            }
            d.dispose();
            return;
        }
        if (mCallBack != null) {
            mCallBack.onSubscribe();
        }
    }

    @Override
    public void onNext(T t) {
        Logger.w(t);
    }

    @Override
    public void onError(Throwable e) {
        Logger.w(e.toString());

        ResultPair resultPair = new ResultPair();
        resultPair.setErrorMessage(Constants.FAIL);

        if (e instanceof NoRouteToHostException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof SocketTimeoutException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof ConnectException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof HttpException) {
            try {
                String ret = ((HttpException) e).response().errorBody().string();
                String ErrorMessage = JSONUtils.getString(ret, "ErrorMessage");
                String Data = JSONUtils.getString(ret, "Data");
                int Code = Integer.parseInt(JSONUtils.getString(ret, "Code"));
                if (Code==10005) {//token失效
                    BlackToast.show(ErrorMessage);
                    App.get().logout();
                }else {
                    resultPair.setData(ErrorMessage);
                }
            } catch (Exception ex) {
                resultPair.setData(ex.getMessage());
                ex.printStackTrace();
            }
        } else if (e instanceof IOException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof ParseResultException) {
            String failData = JSONUtils.getString(e.getMessage(), "data");
            if (!TextUtils.isEmpty(failData)) {
                resultPair.setData(failData);
            } else {
                resultPair.setData(e.getMessage());
            }
        } else {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        }

        if (mCallBack != null) {
            if (!TextUtils.isEmpty(resultPair.getData())) {
                mCallBack.onFailed(resultPair);
            }
        }
    }

    @Override
    public void onComplete() {
        if (mCallBack != null) {
            mCallBack.onComplete();
        }
    }
}
