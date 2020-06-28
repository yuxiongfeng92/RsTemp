package com.proton.runbear.activity.device;

import android.text.TextUtils;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.ActivityDeviceDetailBinding;
import com.proton.runbear.enums.BindStatus;
import com.proton.runbear.enums.OutType;
import com.proton.runbear.net.bean.BindDeviceInfo;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.DeviceCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.DateUtils;

import java.text.ParseException;

/**
 * p03设备详情页
 * <传入extra>
 * profileId int 设备id
 * </extra>
 */
public class DeviceDetailActivity extends BaseActivity<ActivityDeviceDetailBinding> {

    private String mac;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.title.setText(getString(R.string.string_device_detail));
        binding.idIncludeTop.ivBack.setImageResource(R.drawable.close_x_blue);
        mac = getIntent().getStringExtra("mac");
        if (TextUtils.isEmpty(mac)) {
            return;
        }
        queryDeviceDetailInfo();
    }

    @Override
    protected boolean showBackBtn() {
        return true;
    }

    /**
     * 获取设备详情
     */
    private void queryDeviceDetailInfo() {
        DeviceCenter.queryBindDeviceInfo(mac, new NetCallBack<BindDeviceInfo>() {
            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showDialog();
            }

            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(BindDeviceInfo info) {
                dismissDialog();
                updateView(info);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 刷新view
     */
    private void updateView(BindDeviceInfo deviceInfo) {
        binding.idMac.setText(mac);
        binding.idBindPhone.setText(App.get().getPhone());
        binding.idBindDate.setText(DateUtils.dateStrToYMDHMS(DateUtils.formatStringT(deviceInfo.getBindTime())));
        try {
//            binding.idBindDays.setText(DateUtils.daysBetween(DateUtils.dateStrToYMDHMS(System.currentTimeMillis()),DateUtils.dateStrToYMDHMS(DateUtils.formatStringT(deviceInfo.getBindTime())))+"天");
            binding.idBindDays.setText(DateUtils.daysBetween(DateUtils.dateStrToYMDHMS(DateUtils.formatStringT(deviceInfo.getBindTime())-2*24*60*60*1000),DateUtils.dateStrToYMDHMS(System.currentTimeMillis()))+"天");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        binding.idOutType.setText(OutType.getOutTypeByType(deviceInfo.getOutType()).getDesc());
        binding.idBindStatus.setText(BindStatus.getBindStatusByType(deviceInfo.getBindState()).getDesc());
    }

}
