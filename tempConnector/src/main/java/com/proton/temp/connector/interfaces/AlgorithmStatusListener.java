package com.proton.temp.connector.interfaces;

/**
 * Created by wangmengsi on 2018/3/15.
 * 算法回调
 */
public class AlgorithmStatusListener {
    /**
     * 测量状态int值
     */
    public void receiveMeasureStatus(int status) {
    }

    /**
     * 手势状态值
     */
    public void receiveGesture(int gesture) {
    }

    /**
     * 同时返回状态和手势
     *
     * @param status
     * @param gesture
     */
    public void receiveStatusAndGesture(int status, int gesture) {
    }
}