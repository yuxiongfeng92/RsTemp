package com.proton.runbear.bean.rs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 王梦思 on 2018/12/3.
 * <p/>
 */
public class ShareBean {
    @SerializedName("heart_rate")
    private int heartRate;
    @SerializedName("heart_rate_interval")
    private int heartRateInterval;
    @SerializedName("alg_temp")
    private List<Float> temp;
    @SerializedName("raw_temp")
    private List<Float> rawTemp;
    @SerializedName("temp_interval")
    private int tempInterval;
    private long timestamp;
    @SerializedName("patch_mac")
    private String patchMac;
    @SerializedName("watch_imei")
    private String watchIMEI;
    @SerializedName("watch_battery")
    private int watchBattery;
    private int sample;
    private int battery;
    @SerializedName("alg_type")
    private int algorithmType;
    @SerializedName("patch_ver")
    private String version;
    @SerializedName("alg_status")
    private int measureStatus;
    @SerializedName("alg_gesture")
    private int gesture;
    private int packageNum;
    private int totalSize;
    private String appVersion;
    private String algorithmVersion;
    private boolean isDamaged;

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getHeartRateInterval() {
        return heartRateInterval;
    }

    public void setHeartRateInterval(int heartRateInterval) {
        this.heartRateInterval = heartRateInterval;
    }

    public List<Float> getTemp() {
        return temp;
    }

    public void setTemp(List<Float> temp) {
        this.temp = temp;
    }

    public int getTempInterval() {
        return tempInterval;
    }

    public void setTempInterval(int tempInterval) {
        this.tempInterval = tempInterval;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPatchMac() {
        return patchMac;
    }

    public void setPatchMac(String patchMac) {
        this.patchMac = patchMac;
    }

    public String getWatchIMEI() {
        return watchIMEI;
    }

    public void setWatchIMEI(String watchIMEI) {
        this.watchIMEI = watchIMEI;
    }

    public int getSample() {
        return sample;
    }

    public void setSample(int sample) {
        this.sample = sample;
    }

    public List<Float> getRawTemp() {
        return rawTemp;
    }

    public void setRawTemp(List<Float> rawTemp) {
        this.rawTemp = rawTemp;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMeasureStatus() {
        return measureStatus;
    }

    public void setMeasureStatus(int measureStatus) {
        this.measureStatus = measureStatus;
    }

    public int getWatchBattery() {
        return watchBattery;
    }

    public void setWatchBattery(int watchBattery) {
        this.watchBattery = watchBattery;
    }

    public int getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(int packageNum) {
        this.packageNum = packageNum;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
    }

    public int getGesture() {
        return gesture;
    }

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }

    public boolean isDamaged() {
        return isDamaged;
    }

    public void setDamaged(boolean damaged) {
        isDamaged = damaged;
    }
}
