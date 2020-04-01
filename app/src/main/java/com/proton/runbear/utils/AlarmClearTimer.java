package com.proton.runbear.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.proton.runbear.component.App;
import com.wms.logger.Logger;

/**
 * Created by 王梦思 on 2019-06-13.
 * <p/>
 */
public class AlarmClearTimer {
    private static AlarmManager mAlarmManager;
    private static PendingIntent wakeIntent;
    private static long totalTime = 0;
    private static int count;
    private static boolean isStop = true;

    public static class CountDownReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isStop) {
                AlarmClearTimer.start();
            }
        }
    }

    public static void setCountDownTime(long time) {
        totalTime = time;
    }

    public static void start() {
        isStop = false;
        int interval = 15;
        Logger.w("报警:清除定时器:" + count + ",totalTime:" + totalTime);
        if (count * interval >= totalTime) {
            if (alarmTimerCallback != null) {
                alarmTimerCallback.onFinish();
            }
            stop();
            return;
        }
        if (wakeIntent == null) {
            wakeIntent = PendingIntent.getBroadcast(App.get(), 0, new Intent(App.get(), CountDownReceiver.class), 0);
        }
        getAlarmManager().cancel(wakeIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, (SystemClock.elapsedRealtime() + interval * 1000), wakeIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getAlarmManager().setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, (SystemClock.elapsedRealtime() + interval * 1000), wakeIntent);
        }
        count++;
    }

    private static AlarmManager getAlarmManager() {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) App.get().getSystemService(Context.ALARM_SERVICE);
        }
        return mAlarmManager;
    }

    public static void stop() {
        totalTime = 0;
        count = 0;
        isStop = true;
        if (wakeIntent != null) {
            getAlarmManager().cancel(wakeIntent);
        }
        alarmTimerCallback = null;
    }

    public static boolean isStop () {
        return isStop;
    }

    public interface AlarmClearTimerCallback {

        void onFinish();
    }

    private static AlarmClearTimerCallback alarmTimerCallback;

    public static void setAlarmClearTimerCallback(AlarmClearTimerCallback alarmTimerCallback) {
        AlarmClearTimer.alarmTimerCallback = alarmTimerCallback;
    }
}
