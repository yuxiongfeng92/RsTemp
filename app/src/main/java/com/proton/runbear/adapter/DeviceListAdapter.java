package com.proton.runbear.adapter;

import android.content.Context;

import com.proton.runbear.R;
import com.proton.runbear.net.bean.DeviceItemInfo;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

public class DeviceListAdapter extends CommonAdapter<DeviceItemInfo> {

    public DeviceListAdapter(Context context, List<DeviceItemInfo> datum, int layoutId) {
        super(context, datum, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, DeviceItemInfo device) {
        holder.setText(R.id.id_mac,String.format("体温贴:%s"))
                .setText(R.id.id_phone,String.format("手机号:%s"));
    }

}
