package com.proton.runbear.viewmodel.profile;

import android.databinding.ObservableField;

import com.proton.runbear.R;
import com.proton.runbear.component.App;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.ProfileCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.viewmodel.BaseViewModel;
import com.wms.utils.CommonUtils;

import java.util.List;

/**
 * Created by yxf
 */

public class ProfileViewModel extends BaseViewModel {

    public ObservableField<List<ProfileBean>> profileList = new ObservableField<>();

    /**
     * 获取档案列表
     */
    public void getProfileList() {
        ProfileCenter.getProfileList(new NetCallBack<List<ProfileBean>>() {
            @Override
            public void noNet() {
                super.noNet();
                //刷新无网
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(List<ProfileBean> data) {
                if (CommonUtils.listIsEmpty(data)) {
                    profileList.set(data);
                    status.set(Status.Empty);
                } else {
                    profileList.set(data);
                    status.set(Status.Success);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                status.set(Status.Fail);
            }
        });
    }


    /**
     * 删除档案
     */
    public void deleteProfile(long id) {
        ProfileCenter.deleteProfile(id, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                //无网提示
                BlackToast.show(App.get().getResources().getString(R.string.string_no_net));
            }

            @Override
            public void onSucceed(ResultPair data) {
                //档案删除成功
                BlackToast.show(R.string.string_delete_success);
                //刷新列表
                getProfileList();
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DELETE_PROFILE));
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_delete_failed);
                }
            }
        });
    }
}
