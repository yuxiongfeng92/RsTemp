package com.proton.runbear.activity.device;

import android.text.TextUtils;
import android.view.View;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.databinding.ActivityDockerDetailBinding;
import com.proton.runbear.net.center.DeviceCenter;
import com.proton.runbear.utils.DateUtils;
import com.proton.runbear.utils.IntentUtils;

/**
 * p03设备详情页
 * <传入extra>
 * profileId int 设备id
 * </extra>
 */
public class DockerDetailActivity extends BaseActivity<ActivityDockerDetailBinding> {

    private String mac;
    @Override
    protected int inflateContentView() {
        return R.layout.activity_docker_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.title.setText(getString(R.string.string_device_detail));
        binding.idIncludeTop.idTvRightOperate.setText(R.string.string_wifi_reconnect);
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
        mac = getIntent().getStringExtra("mac");
        if (TextUtils.isEmpty(mac)) {
            return;
        }

        //设备名称
        binding.idTvDeviceDetailName.setText(device.getDeviceTypeName());
        //充电器固件版本
        String version = device.getVersion();
        if (!TextUtils.isEmpty(version) && (version.startsWith("V") || version.startsWith("v"))) {
            binding.idTvBaseHardVersion.setText(version);
        }

        if (device.getLastUpdated() != 0) {
            binding.idTvBaseLatestUse.setText(DateUtils.dateStrToYMDHM(device.getLastUpdated()));
        }

        if (!TextUtils.isEmpty(device.getBtaddress())) {
            binding.idDeviceMac.setText(device.getBtaddress());
        }
        if (!TextUtils.isEmpty(device.getWifiName())) {
            binding.idTvWifiAddress.setText(device.getWifiName());
        }

        binding.idIncludeTop.idTvRightOperate.setTextSize(10);
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(v -> {
            //重新联网
            IntentUtils.goToDockerSetNetwork(mContext, true, device.getBtaddress());
        });
    }

    private void getDeviceInfo(){
        DeviceCenter.queryDeviceInfo();
    }
}
