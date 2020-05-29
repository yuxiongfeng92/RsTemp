package com.proton.runbear.adapter;

import android.content.Context;
import android.content.Intent;

import com.proton.runbear.R;
import com.proton.runbear.activity.device.DeviceDetailActivity;
import com.proton.runbear.net.bean.BindDeviceInfo;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

public class DeviceListAdapter extends CommonAdapter<BindDeviceInfo> {

    public DeviceListAdapter(Context context, List<BindDeviceInfo> datum, int layoutId) {
        super(context, datum, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, BindDeviceInfo device) {
        holder.setText(R.id.id_mac, String.format("体温贴:%s", device.getPatchMac()))
                .setText(R.id.id_phone, String.format("手机号:%s", device.getPhone()));

        holder.getView(R.id.id_lay_p245content).setOnClickListener(v -> mContext.startActivity(new Intent(mContext, DeviceDetailActivity.class).putExtra("mac",device.getPatchMac())));



    }

}
