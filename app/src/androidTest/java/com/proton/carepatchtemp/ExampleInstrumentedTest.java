package com.proton.runbear;

import android.content.Context;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.proton.runbear.utils.StatusBarUtil;
import com.wms.logger.Logger;
import com.wms.utils.DensityUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Logger.w("test:", appContext.getFilesDir().getAbsolutePath());
        //HUAWEI
        Logger.w("手机型号:", Build.MANUFACTURER, "--", Build.MODEL);
    }

    @Test
    public void testBuglyAppid() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String appId = appContext.getString(R.string.buglyAppid);
        if (appId.equals("fef8b1ea78")) {
            Logger.w("国际版");
        } else if (appId.equals("847567d215")) {
            Logger.w("国内debug");
        } else if (appId.equals("ae87846642")) {
            Logger.w("国内release");
        }
    }

    @Test
    public void getStatusHeight() {
        int statusHeight = -1;
        try {
            Context appContext = InstrumentationRegistry.getTargetContext();
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = appContext.getResources().getDimensionPixelSize(height);
            System.out.println("statusHeight:" + StatusBarUtil.getStatusBarHeight());
            System.out.println("statusHeight:" + DensityUtils.dip2px(appContext, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUtc() {
        String utcTime="2020-05-29T10:42:07.297";
        long time = formatStringTT(utcTime);
        String s = dateStrToYMDHMS(time);
        Logger.w("utc is :",s);
    }


    /**
     * 将时间转换成年月日 时分秒
     */
    public static String dateStrToYMDHMS(long dateStr) {
        SimpleDateFormat ymdSimpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return ymdSimpleDate.format(new Date(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long formatStringTT(String dateNew) {
        //yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
            Date dateTime = sdf.parse(dateNew);
            return dateTime.getTime();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    /**
     *
     * <p>Description: 本地时间转化为UTC时间</p>
     * @param localTime
     * @return
     * @author wgs
     * @date  2018年10月19日 下午2:23:43
     *
     */
    public static Date localToUTC(String localTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date localDate= null;
        try {
            localDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long localTimeInMillis=localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate=new Date(calendar.getTimeInMillis());
        return utcDate;
    }

    /**
     *
     * <p>Description:UTC时间转化为本地时间 </p>
     * @param utcTime
     * @return
     * @author wgs
     * @date  2018年10月19日 下午2:23:24
     *
     */
    public static Date utcToLocal(String utcTime){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate;
    }


}
