package com.proton.runbear.fragment.measure;

import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.component.App;
import com.proton.runbear.constant.AppConfigs;
import com.proton.runbear.database.ProfileManager;
import com.proton.runbear.fragment.base.BaseViewModelFragment;

import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.view.SearchDeviceDialog;
import com.proton.runbear.view.WarmDialog;
import com.proton.runbear.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.TempDataBean;

import java.util.Iterator;
import java.util.List;

public abstract class BaseMeasureFragment<DB extends ViewDataBinding, VM extends MeasureViewModel> extends BaseViewModelFragment<DB, VM> {

    protected MeasureBean mMeasureInfo;
    private SearchDeviceDialog mSearchDeviceDialog;
    protected WarmDialog mHighestWarmDialog;

    protected Observable.OnPropertyChangedCallback mConnectStatusCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {

            if (viewmodel.isConnecting()) {
                showSearchDeviceDialog();
            } else {
                if (mSearchDeviceDialog != null && mSearchDeviceDialog.isShowing()) {
                    mSearchDeviceDialog.dismiss();
                }
            }

            if (!viewmodel.isDisconnect() && mHighestWarmDialog != null) {
                mHighestWarmDialog.dismiss();
            }

        }
    };


    /**
     * 是否需要显示搜索对话框的回调
     */
    protected Observable.OnPropertyChangedCallback mSearchDeviceCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (viewmodel.needShowSearchDeviceDialog.get()) {
                showSearchDeviceDialog();
            } else {
                if (mSearchDeviceDialog != null && mSearchDeviceDialog.isShowing()) {
                    mSearchDeviceDialog.dismiss();
                }
            }
        }
    };


    @Override
    protected void fragmentInit() {
        mMeasureInfo = (MeasureBean) getArguments().getSerializable("measureInfo");
        viewmodel.connectStatus.addOnPropertyChangedCallback(mConnectStatusCallback);
        viewmodel.needShowSearchDeviceDialog.addOnPropertyChangedCallback(mSearchDeviceCallback);
    }

    @Override
    protected void initView() {
        super.initView();
    }


    /**
     * 显示搜索设备的对话框
     */
    protected void showSearchDeviceDialog() {
        if (mSearchDeviceDialog == null) {
            mSearchDeviceDialog = new SearchDeviceDialog(ActivityManager.currentActivity());
            //只有搜索结束后，点击才有效
            mSearchDeviceDialog.setCallback((isSearching) -> {
                if (isSearching) {
                    new WarmDialog(ActivityManager.currentActivity())
                            .setTopText(R.string.string_warm_tips)
                            .setContent("是否停止搜索设备")
                            .setConfirmListener(v -> viewmodel.disConnect())
                            .show();
                } else {
                    mSearchDeviceDialog.swithSearchStyle(true);
                    viewmodel.getConfigInfo();
                }

            });
        }

        mSearchDeviceDialog.setOnDismissListener(dialog -> {
            if (viewmodel.connectStatus.get() != 2) {
                viewmodel.connectStatus.set(3);
            }
            viewmodel.needShowSearchDeviceDialog.set(false);
            mSearchDeviceDialog.stopWaveView();
        });

        if (!mSearchDeviceDialog.isShowing()) {
            mSearchDeviceDialog.swithSearchStyle(true);
            mSearchDeviceDialog.show();
        }
    }

    /**
     * 迭代器删除集合中的元素
     *
     * @param temps
     * @return
     */
    public List<TempDataBean> clearAvailableTemp(List<TempDataBean> temps) {

        Iterator iterator = temps.iterator();
        while (iterator.hasNext()) {
            TempDataBean next = (TempDataBean) iterator.next();
            if (next.getAlgorithmTemp() <= 0) {
                // 注意！！！这里时Iterator.remove()!!!而不是list.remove()!!!
                iterator.remove();
            }
        }
        return temps;
    }




}
