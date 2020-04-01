package com.proton.runbear.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 通过广播监听系统电量，电量改变的时候获取电量
 * Created by yuxiongfeng.
 * Date: 2019/5/6
 */
public class BatteryChangeUtil {

    private Context mContext;
    private BatteryBroadcastReceiver receiver;
    private BatteryStateListener mBatteryStateListener;

    public BatteryChangeUtil(Context context) {
        mContext = context;
        receiver = new BatteryBroadcastReceiver();
    }

    public void register(BatteryStateListener listener) {
        mBatteryStateListener = listener;
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        try {
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBatteryStateListener == null) {
                return;
            }
            if (intent != null) {
                String action = intent.getAction();
                //电量发生改变
                if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    mBatteryStateListener.onStateChanged();
                } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                    mBatteryStateListener.onPowerConnect();
                } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                    mBatteryStateListener.onPowerDisconnect();
                }
            }
        }
    }

    public interface BatteryStateListener {
        void onStateChanged();

        void onPowerConnect();

        void onPowerDisconnect();
    }
}
