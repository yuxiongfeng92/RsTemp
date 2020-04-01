package com.proton.runbear.activity.device;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.databinding.ActivityDeviceBaseConnectFailBinding;
import com.proton.runbear.utils.UIUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.WiFiDisconnectDialog;

public class DeviceBaseConnectFailActivity extends BaseActivity<ActivityDeviceBaseConnectFailBinding> {
    WiFiDisconnectDialog wiFiDisconnectDialog;
    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_base_connect_fail;
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idBtnRetry.setOnClickListener(v -> {
            //重试
            finish();
        });

        binding.ivExplain.setOnClickListener(v->{
            if (Utils.needRecreateDialog(wiFiDisconnectDialog)) {
                wiFiDisconnectDialog=new WiFiDisconnectDialog(this);
            }

            if (!wiFiDisconnectDialog.isShowing()) {
                wiFiDisconnectDialog.show();
            }
        });


    }

    @Override
    public String getTopCenterText() {
        return UIUtils.getString(R.string.string_base_connect_wifi);
    }
}
