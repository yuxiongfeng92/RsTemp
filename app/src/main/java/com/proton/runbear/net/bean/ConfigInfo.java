package com.proton.runbear.net.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 王梦思 on 2018-12-13.
 * <p/>
 */
public class ConfigInfo {
    //是否需要清空体温贴蓝牙缓存，true表示清空  false表示不清空
    private boolean isFirst;
    @SerializedName("patch_mac")
    private String patchMac;
    @SerializedName("watch_imei")
    private String watchImei;
    @SerializedName("bind_id")
    private int bindId;
    @SerializedName("error_msg")
    private String errodMsg;
    @SerializedName("status")
    private String status;
    @SerializedName("org_id")
    private String orgId;
    @SerializedName("alg_type")
    private int algorithType;
    @SerializedName("setting")
    private Settings settings;

    public static class Settings {
        @SerializedName("mqtt_server_ip")
        private String mqttServer;
        @SerializedName("mqtt_server_port")
        private String mqttPort;
        @SerializedName("mqtt_client_username")
        private String mqttUsername;
        @SerializedName("mqtt_client_password")
        private String mqttPassword;
        @SerializedName("heart_rate_interval")
        private int heartRateInterval;
        @SerializedName("temp_interval")
        private int tempInterval;
        @SerializedName("temp_warning_value")
        private List<WarmBean> warmRange;
        @SerializedName("get_heart_rate_temp_min_value")
        private float heartRateMinTemp;
        @SerializedName("send_temp_min_value")
        private float uploadTemp;
        @SerializedName("show_temp_min_value")
        private float showTemp;
        @SerializedName("reset_temp_value")
        private float clearAlarmTemp;
        @SerializedName("warning_interval")
        private long clearAlarmInterval = 60 * 60 * 2;
        @SerializedName("temp_warning")
        private float warmTemp = 37.5f;



        public String getMqttServer() {
            return mqttServer;
        }

        public void setMqttServer(String mqttServer) {
            this.mqttServer = mqttServer;
        }

        public String getMqttPort() {
            return mqttPort;
        }

        public void setMqttPort(String mqttPort) {
            this.mqttPort = mqttPort;
        }

        public String getMqttUsername() {
            return mqttUsername;
        }

        public void setMqttUsername(String mqttUsername) {
            this.mqttUsername = mqttUsername;
        }

        public String getMqttPassword() {
            return mqttPassword;
        }

        public void setMqttPassword(String mqttPassword) {
            this.mqttPassword = mqttPassword;
        }

        public int getHeartRateInterval() {
            return heartRateInterval;
        }

        public void setHeartRateInterval(int heartRateInterval) {
            this.heartRateInterval = heartRateInterval;
        }

        public int getTempInterval() {
            return tempInterval;
        }

        public void setTempInterval(int tempInterval) {
            this.tempInterval = tempInterval;
        }

        public List<WarmBean> getWarmRange() {
            return warmRange;
        }

        public void setWarmRange(List<WarmBean> warmRange) {
            this.warmRange = warmRange;
        }

        public float getHeartRateMinTemp() {
            return heartRateMinTemp;
        }

        public void setHeartRateMinTemp(float heartRateMinTemp) {
            this.heartRateMinTemp = heartRateMinTemp;
        }

        public float getUploadTemp() {
            return uploadTemp;
        }

        public void setUploadTemp(float uploadTemp) {
            this.uploadTemp = uploadTemp;
        }

        public float getShowTemp() {
            return showTemp;
        }

        public void setShowTemp(float showTemp) {
            this.showTemp = showTemp;
        }

        public float getClearAlarmTemp() {
            return clearAlarmTemp;
        }

        public void setClearAlarmTemp(float clearAlarmTemp) {
            this.clearAlarmTemp = clearAlarmTemp;
        }

        public long getClearAlarmInterval() {
            return clearAlarmInterval;
        }

        public void setClearAlarmInterval(long clearAlarmInterval) {
            this.clearAlarmInterval = clearAlarmInterval;
        }

        public float getWarmTemp() {
            return warmTemp;
        }

        public void setWarmTemp(float warmTemp) {
            this.warmTemp = warmTemp;
        }
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public String getPatchMac() {
        return patchMac;
    }

    public void setPatchMac(String patchMac) {
        this.patchMac = patchMac;
    }

    public String getWatchImei() {
        return watchImei;
    }

    public void setWatchImei(String watchImei) {
        this.watchImei = watchImei;
    }

    public int getBindId() {
        return bindId;
    }

    public void setBindId(int bindId) {
        this.bindId = bindId;
    }

    public String getErrodMsg() {
        return errodMsg;
    }

    public void setErrodMsg(String errodMsg) {
        this.errodMsg = errodMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public int getAlgorithType() {
        return algorithType;
    }

    public void setAlgorithType(int algorithType) {
        this.algorithType = algorithType;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
