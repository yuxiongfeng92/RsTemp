package com.proton.runbear.fragment.measure;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proton.runbear.R;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.FragmentMeasureScanDeviceBinding;
import com.proton.runbear.databinding.LayoutEmptyDeviceListBinding;
import com.proton.runbear.databinding.LayoutScanDeviceHeaderBinding;
import com.proton.runbear.fragment.base.BaseFragment;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.DeviceCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.recyclerheader.HeaderAndFooterWrapper;
import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.proton.temp.connector.bluetooth.callback.OnScanListener;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 测量搜索设备
 */

public class MeasureScanDeviceFragment extends BaseFragment<FragmentMeasureScanDeviceBinding> {

    private List<DeviceBean> mDeviceList = new ArrayList<>();
    private LayoutScanDeviceHeaderBinding headerBinding;
    private HeaderAndFooterWrapper mAdapter;
    private LayoutEmptyDeviceListBinding emptyDeviceBinding;
    private OnScanDeviceListener onScanDeviceListener;
    private ProfileBean mProfile;

    /**
     * 是否正在扫描设备
     */
    private boolean isScanDevice;
    private OnScanListener mScanListener = new OnScanListener() {
        @Override
        public void onDeviceFound(DeviceBean device) {
            //看看当前设备是否已经添加或者已经连接了
            if (!CommonUtils.listIsEmpty(mDeviceList)) {
                for (DeviceBean tempDevice : mDeviceList) {
                    if (tempDevice.getMacaddress().equalsIgnoreCase(device.getMacaddress())) {
                        return;
                    }
                }
            }
            //没绑定贴则添加到设备列表
            mDeviceList.add(device);
            headerBinding.setHasDevice(true);
            mAdapter.notifyItemInserted(mDeviceList.size());
        }

        @Override
        public void onScanStopped() {
            Logger.w("搜索设备结束");
            doSearchStoped();
            stopSearch(true);
        }

        @Override
        public void onScanCanceled() {
            Logger.w("搜索设备取消");
            stopSearch(false);
        }
    };

    public static MeasureScanDeviceFragment newInstance(ProfileBean profile) {
        Bundle bundle = new Bundle();
        MeasureScanDeviceFragment fragment = new MeasureScanDeviceFragment();
        bundle.putSerializable("profile", profile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_scan_device;
    }

    @Override
    protected void fragmentInit() {
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new HeaderAndFooterWrapper(new CommonAdapter<DeviceBean>(mContext, mDeviceList, R.layout.item_device) {

            @SuppressLint("CheckResult")
            @Override
            public void convert(CommonViewHolder holder, DeviceBean device) {
                holder.setText(R.id.id_device_mac, Utils.getShowMac(device.getMacaddress()));
                holder.getView(R.id.id_connect).setOnClickListener(v -> {
                    bindDevice(device.getMacaddress());
                });
                holder.setText(R.id.id_connect, getString(R.string.string_click_use));
            }
        });

        mProfile = (ProfileBean) getArguments().getSerializable("profile");
        headerBinding = LayoutScanDeviceHeaderBinding.inflate(getLayoutInflater());
        headerBinding.setProfile(mProfile);
        headerBinding.idSwitchProfile.setOnClickListener(v -> {
            if (onScanDeviceListener != null) {
                onScanDeviceListener.onSwitchProfile();
            }
        });

        headerBinding.idSwitchAvatar.setVisibility(App.get().isLogined() ? View.VISIBLE : View.GONE);
        headerBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter.addHeaderView(headerBinding.getRoot());
        //设置动画
        Utils.setRecyclerViewDeleteAnimation(binding.idRecyclerview);
        binding.idRecyclerview.setAdapter(mAdapter);
    }


    public void setProfile(ProfileBean profile) {
        this.mProfile = profile;
        if (headerBinding != null) {
            headerBinding.setProfile(profile);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        getBindDevice();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idScanDevice.setOnClickListener(v -> getBindDevice());
    }

    /**
     * 获取绑定的设备
     */
    private void getBindDevice() {
        if (isScanDevice) return;
        SpUtils.saveString(Constants.BIND_MAC, "");
        scanDevice();
    }

    /**
     * 扫描设备
     */
    private void scanDevice() {
        if (binding == null) return;
        if (!BluetoothUtils.isBluetoothOpened()) {
            BluetoothUtils.openBluetooth();
            return;
        }
        if (emptyDeviceBinding != null) {
            mAdapter.removeHeader(emptyDeviceBinding.getRoot());
        }
        headerBinding.setHasDevice(false);
        mDeviceList.clear();
        binding.idRecyclerview.getAdapter().notifyDataSetChanged();
        binding.idWave.start();
        isScanDevice = true;
        binding.idScanDevice.setText(R.string.string_searching);
        BleConnector.scanDevice(mScanListener);
    }

    private void stopSearch(boolean showEmpty) {
        isScanDevice = false;
        binding.idWave.stop();
        binding.idScanDevice.setText(R.string.string_rescan);
        if (showEmpty) {
            if (CommonUtils.listIsEmpty(mDeviceList)) {
                showEmptyTips();
                headerBinding.setHasDevice(false);
            }
        }
    }

    private void doSearchStoped() {
        if (!CommonUtils.listIsEmpty(mDeviceList)) {
            headerBinding.setHasDevice(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 绑定设备
     */
    private void bindDevice(String mac) {
        DeviceCenter.bindDevice(mac, new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(Boolean data) {
                Logger.w("设备绑定成功");
                if (onScanDeviceListener != null) {
                    onScanDeviceListener.onBindResult(mac);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
                if (onScanDeviceListener != null) {
                    onScanDeviceListener.onBindResult(mac);
                }
            }
        });
    }

    /**
     * 显示无设备提示
     */
    private void showEmptyTips() {
        if (emptyDeviceBinding == null) {
            emptyDeviceBinding = LayoutEmptyDeviceListBinding.inflate(LayoutInflater.from(App.get()));
            emptyDeviceBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
          /*  String[] clickAryStr = new String[]{getResString(R.string.string_rebind), getResString(R.string.string_not_show_green), getResString(R.string.string_click_here)};
            UIUtils.spanStr(emptyDeviceBinding.idTvP03empty, getResString(R.string.string_p03device_empty2), clickAryStr, R.color.color_blue_005c, true, position -> {
                switch (position) {
                    case 0:
                        //重新绑定
                        IntentUtils.goToScanQRCode(mContext, mProfile);
                        break;
                    case 1:
                        //重新配网
                        IntentUtils.goToDockerSetNetwork(mContext, true);
                        break;
                    case 2:
                        //点击这里
                        startActivity(new Intent(getActivity(), GlobalWebActivity.class).putExtra("url", HttpUrls.URL_NO_DEVICE_SEARCH));
                        break;
                }
            });
        }*/
            emptyDeviceBinding.idTvP03empty.setText("① 体温贴白灯闪烁并靠近手机\n② 在设置中重启蓝牙");
            emptyDeviceBinding.idTitle.setText(String.format(getString(R.string.string_can_not_get_data_please_confirm), "体温贴"));
            mAdapter.removeHeader(emptyDeviceBinding.getRoot());
            mAdapter.addHeaderView(emptyDeviceBinding.getRoot());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            headerBinding.setHasDevice(false);
            binding.idWave.stop();
            mDeviceList.clear();
            if (emptyDeviceBinding != null) {
                mAdapter.removeHeader(emptyDeviceBinding.getRoot());
            }
            mAdapter.notifyDataSetChanged();
            BleConnector.stopScan();
        } else {
            if (App.get().isLogined()) {
                getBindDevice();
            }
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.LOGIN) {
            if (headerBinding != null) {
                headerBinding.idSwitchAvatar.setVisibility(View.VISIBLE);
            }
        } else if (event.getEventType() == MessageEvent.EventType.SCAN_COMPLETE) {
            if (isHidden()) return;
            getBindDevice();
        } else if (event.getEventType() == MessageEvent.EventType.FIREWARE_UPDATE_SUCCESS) {
            if (isHidden()) return;
            scanDevice();
        } else if (event.getEventType() == MessageEvent.EventType.PROFILE_CHANGE && !TextUtils.isEmpty(event.getMsg()) && event.getMsg().equals("isEdit")) {
            //档案编辑了
            ProfileBean profileBean = (ProfileBean) event.getObject();
            mProfile = profileBean;
            headerBinding.setProfile(profileBean);
        }
    }

    public void setOnScanDeviceListener(OnScanDeviceListener onScanDeviceListener) {
        this.onScanDeviceListener = onScanDeviceListener;
    }

    public interface OnScanDeviceListener {
        /**
         * 连接设备
         */
        void onBindResult(String mac);

        /**
         * 切换档案
         */
        void onSwitchProfile();
    }
}
