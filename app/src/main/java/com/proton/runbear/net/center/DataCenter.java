package com.proton.runbear.net.center;

import com.proton.runbear.R;
import com.proton.runbear.component.App;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.UIUtils;
import com.wms.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataCenter {

    public static boolean isSuccess(String ret) {
        return Constants.SUCCESS.equalsIgnoreCase(ret);
    }

    public static <T> ObservableTransformer<T, T> threadTrans() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static boolean noNet() {
        boolean noNet = !NetUtils.isConnected(App.get());
        if (noNet) {
            BlackToast.show(R.string.string_no_net);
        }
        return noNet;
    }

    public static ResultPair parseResult(String data) {

        if (data.contains("LOGIN")) {
            if (!data.contains("1")) {
                //token与uid匹配错误，需要重新登录
                ResultPair resultPair = new ResultPair();
                resultPair.setErrorMessage(Constants.FAIL);
                resultPair.setData("");
                App.get().kickOff();
                return resultPair;
            } else {
                //未登录
                ResultPair resultPair = new ResultPair();
                resultPair.setErrorMessage(Constants.SUCCESS);
                resultPair.setData("");
                return resultPair;
            }
        }

        JSONObject response;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            ResultPair resultPair = new ResultPair();
            resultPair.setErrorMessage(Constants.FAIL);
            resultPair.setData(UIUtils.getString(R.string.string_parse_data_failed));
            return resultPair;
        }

        ResultPair resultPair = new ResultPair();
        resultPair.setErrorMessage(Constants.FAIL);

        try {
            resultPair.setErrorMessage(response.getString("ErrorMessage"));
            resultPair.setData(response.getString("Data"));
            resultPair.setSuccess(response.getBoolean("Success"));
            int code = response.getInt("Code");
            resultPair.setCode(code);
            if (code == 10005) {//token失效,重新登录
                App.get().kickOff();
            }
        } catch (JSONException e) {
            resultPair.setErrorMessage(Constants.FAIL);
            resultPair.setData(UIUtils.getString(R.string.string_parse_data_failed));
        }

        return resultPair;
    }
}
