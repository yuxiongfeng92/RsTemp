package com.proton.runbear.net.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 王梦思 on 2018/12/19.
 * <p/>
 */
public class WarmBean {
    @SerializedName("start_range")
    private float rangeStartTemp;
    @SerializedName("end_range")
    private float rangeEndTemp;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public float getRangeStartTemp() {
        return rangeStartTemp;
    }

    public void setRangeStartTemp(float rangeStartTemp) {
        this.rangeStartTemp = rangeStartTemp;
    }

    public float getRangeEndTemp() {
        return rangeEndTemp;
    }

    public void setRangeEndTemp(float rangeEndTemp) {
        this.rangeEndTemp = rangeEndTemp;
    }

    @Override
    public String toString() {
        return "WarmBean{" +
                "rangeStartTemp=" + rangeStartTemp +
                ", rangeEndTemp=" + rangeEndTemp +
                ", time=" + time +
                '}';
    }
}
