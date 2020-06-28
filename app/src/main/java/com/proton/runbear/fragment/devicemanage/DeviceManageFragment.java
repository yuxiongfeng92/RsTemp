package com.proton.runbear.fragment.devicemanage;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.proton.runbear.R;
import com.proton.runbear.activity.HomeActivity;
import com.proton.runbear.activity.device.DeviceDetailActivity;
import com.proton.runbear.adapter.DeviceListAdapter;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.FragmentDeviceListManageBinding;
import com.proton.runbear.fragment.base.BaseFragment;
import com.proton.runbear.fragment.base.BaseViewModelFragment;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.BindDeviceInfo;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.DeviceCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.MainViewModel;
import com.proton.temp.connector.bean.DeviceType;
import com.wms.adapter.recyclerview.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.proton.runbear.utils.Utils.hasMeasureItem;

/**
 * Created by luochune on 2018/3/29.
 * 设备管理片段
 */

public class DeviceManageFragment extends BaseViewModelFragment<FragmentDeviceListManageBinding, MainViewModel> {
    private DeviceListAdapter mDeviceManageAdapter;
    private List<BindDeviceInfo> mDeviceManageList = new ArrayList<>();
    private OnDeviceManageListener onDeviceManageListener;

    public static DeviceManageFragment newInstance() {
        return new DeviceManageFragment();
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idTopLayout.idTopRight.setVisibility(View.GONE);
        binding.idTopLayout.idTitle.setText(mContext.getResources().getString(R.string.string_bluetooth_device_manage));
        binding.idTopLayout.ivTopRight.setVisibility(View.VISIBLE);
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        initRefreshLayout(binding.idRefreshLayout, refreshLayout -> getDeviceList());
        mDeviceManageAdapter = new DeviceListAdapter(getActivity(), mDeviceManageList, R.layout.item_device_list);
        binding.idRecyclerview.setAdapter(mDeviceManageAdapter);
        setListener();

    }

    private void setListener() {
        binding.idTopLayout.idToogleDrawer.setOnClickListener(v -> {
            if (onDeviceManageListener != null) {
                onDeviceManageListener.onDrawClick();
            }
        });

        binding.idTopLayout.ivTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasMeasureItem() && Utils.checkPatchIsMeasuring(App.get().getDeviceMac())) {
                    ((HomeActivity)getActivity()).setFromDeviceManagePage(true);
                    ((HomeActivity)getActivity()).showMeasureFragment();
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.GO_TO_MEASURING_FRAGMENT));
                    return;
                } else {
                    BlackToast.show("没有正在测量的页面");
                }

            }
        });

    }



    @Override
    public void initData() {
        if (!App.get().isLogined()) {
            setLoadError();
            return;
        }
        super.initData();
        binding.idRefreshLayout.autoRefresh();
    }

    /**
     * 显示选中的设备列表
     */
    public void getDeviceList() {
        if (!App.get().isLogined()) {
            setLoadError();
            return;
        }
        DeviceCenter.queryDevices(new NetCallBack<List<BindDeviceInfo>>() {
            @Override
            public void noNet() {
                super.noNet();
                binding.idRefreshLayout.finishRefresh();
                setLoadError();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(List<BindDeviceInfo> data) {
                binding.idRefreshLayout.finishRefresh();
                if (data != null && data.size() > 0) {
                    if (mDeviceManageList != null) {
                        mDeviceManageList.clear();
                    } else {
                        mDeviceManageList = new ArrayList<>();
                    }
                    mDeviceManageList.addAll(data);
                    mDeviceManageAdapter.setDatas(mDeviceManageList);
                    setLoadSuccess();
                } else {
                    setLoadEmpty();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                binding.idRefreshLayout.finishRefresh();
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
                setLoadError();
            }
        });
    }

    @Override
    protected MainViewModel getViewModel() {
        return ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_device_list_manage;
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idRefreshLayout;
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_bluetooth_device_manage);
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_view_device;
    }

    public void setOnDeviceManageListener(OnDeviceManageListener onDeviceManageListener) {
        this.onDeviceManageListener = onDeviceManageListener;
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.string_deviceManage_empty);
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.SWITCH_DEVICE) {
            //切换设备
            if (event.getObject() != null && event.getObject() instanceof DeviceType) {
                getDeviceList();
            }
        } else if (event.getEventType() == MessageEvent.EventType.LOGIN) {
            initData();
        } else if (event.getEventType() == MessageEvent.EventType.DEVICE_CHANGED) {
            getDeviceList();
        }
    }

    public interface OnDeviceManageListener {
        void onDrawClick();
    }
}
