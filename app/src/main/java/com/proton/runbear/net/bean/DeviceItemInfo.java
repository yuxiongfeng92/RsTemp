package com.proton.runbear.net.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 设备详情
 * @Author: yxf
 * @CreateDate: 2020/5/28 9:53
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/28 9:53
 */
public class DeviceItemInfo {

    @SerializedName("deviceBindId")
    private int DeviceBindID;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("Patch_Mac")
    private String patchMac;
    private int OutType;
    private int BindState;
    private String BindTime;
    private Object UnbindTime;
    private int OrgID;
    @SerializedName("isFirst")
    private boolean IsFirst;

    public int getDeviceBindID() {
        return DeviceBindID;
    }

    public void setDeviceBindID(int deviceBindID) {
        DeviceBindID = deviceBindID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPatchMac() {
        return patchMac;
    }

    public void setPatchMac(String patchMac) {
        this.patchMac = patchMac;
    }

    public int getOutType() {
        return OutType;
    }

    public void setOutType(int outType) {
        OutType = outType;
    }

    public int getBindState() {
        return BindState;
    }

    public void setBindState(int bindState) {
        BindState = bindState;
    }

    public String getBindTime() {
        return BindTime;
    }

    public void setBindTime(String bindTime) {
        BindTime = bindTime;
    }

    public Object getUnbindTime() {
        return UnbindTime;
    }

    public void setUnbindTime(Object unbindTime) {
        UnbindTime = unbindTime;
    }

    public int getOrgID() {
        return OrgID;
    }

    public void setOrgID(int orgID) {
        OrgID = orgID;
    }

    public boolean isFirst() {
        return IsFirst;
    }

    public void setFirst(boolean first) {
        IsFirst = first;
    }
}
