package com.proton.runbear.utils;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStores;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.proton.runbear.BuildConfig;
import com.proton.runbear.R;
import com.proton.runbear.activity.HomeActivity;
import com.proton.runbear.activity.user.LoginActivity;
import com.proton.runbear.bean.AlarmBean;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.bean.ReportBean;
import com.proton.runbear.component.App;
import com.proton.runbear.constant.AppConfigs;
import com.proton.runbear.enums.InstructionConstant;
import com.proton.runbear.net.bean.ConfigInfo;
import com.proton.runbear.utils.net.OSSUtils;
import com.proton.runbear.view.SystemDialog;
import com.proton.runbear.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.DeviceType;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.utils.UpdateAppHttpUtil;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;
import com.wms.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by wangmengsi on 2018/2/26.
 */

public class Utils {
    private static DecimalFormat mTempFormatter;

    public static void setStatusBarTextColor(Activity activity, boolean isDark) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            MIUISetStatusBarLightMode(activity.getWindow(), isDark);
            FlymeSetStatusBarLightMode(activity.getWindow(), isDark);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isMobilePhone(String phoneNum) {
        return !TextUtils.isEmpty(phoneNum)
                && phoneNum.length() == 11
                && phoneNum.startsWith("1");
    }


    /**
     * 检测有效是否有效
     *
     * @param emai
     * @return
     */
    public static boolean isEmail(String emai) {
        if (emai == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(emai);
        if (m.matches())
            return true;
        else
            return false;
    }


    public static String encrypt(String input, String key) {
        input += input + "proton521";
        byte[] crypted = null;

        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(crypted));
    }

    /**
     * 获取进程名称
     */
    public static String getProcessName(Context context) {
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return CommonUtils.getAppPackageName(context);
        List<android.app.ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (android.app.ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * 截取mac地址，后五位
     */
    public static String getShowMac(String address) {
        if (TextUtils.isEmpty(address) || address.length() < 5) return "";
        return address.substring(address.length() - 5, address.length());
    }

    /**
     * 当前是否是摄氏度
     */
    public static boolean isSelsiusUnit() {
        return AppConfigs.SP_VALUE_TEMP_C == SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT);
    }

    /**
     * 将float温度根据实际温度单位转换成对应数值
     */
    public static float getTemp(float temp) {
        if (isSelsiusUnit()) {
            return formatTemp(temp);
        } else {
            return selsiusToFahrenheit(temp);
        }
    }

    /**
     * 根据指定单位的温度获取实际温度
     *
     * @param temp      当前温度
     * @param isSelsius 指定当前温度是否是设置度
     * @return 返回当前单位的温度(摄氏度)
     */
    public static float getTemp(float temp, boolean isSelsius) {
        if (!isSelsius) {
            //华氏度
            temp = fahrenheitToCelsius(temp);
        }
        return temp;
    }

    public static String getTempStr(float temp) {
        return String.valueOf(getTemp(temp));
    }

    public static String getFormartTempStr(float temp) {
        if (temp <= 0) {
            return "--.--";
        }
        return formatTempToStr(getTemp(temp));
    }

    public static String getFormartTempAndUnitStr(float temp) {
        return getFormartTempStr(temp) + getTempUnit();
    }

    /**
     * 根据浮点温度值转为两位精度格式的字符串
     */
    public static String formatTempToStr(float temp) {
//        if (mTempFormatter == null) {
//            mTempFormatter = new DecimalFormat("##0.00");
//        }
//        return mTempFormatter.format(temp);
        return roundByScale(temp, 2);
    }


    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全
     *
     * @param v     需要格式化的数字
     * @param scale 小数点后保留几位
     * @return
     */
    public static String roundByScale(float v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }


    public static String getTempUnit() {
        if (isSelsiusUnit()) {
            return UIUtils.getString(R.string.string_temp_C);
        } else {
            return UIUtils.getString(R.string.string_temp_F);
        }
    }

    public static String getTempAndUnit(float temp) {
        return getTemp(temp) + " " + getTempUnit();
    }

    public static String getTempAndUnit(String temp) {
        return getTemp(Float.parseFloat(temp)) + " " + getTempUnit();
    }

    public static String getReportJsonPath(long startTime) {
        return FileUtils.getJson_filepath() + "/" + startTime + ".json";
    }

    public static String getLocalReportPath(String filePath) {
        filePath = OSSUtils.getSaveUrl(filePath);
        if (TextUtils.isEmpty(filePath)) return "";
        return FileUtils.getJson_filepath() + "/" + new File(filePath).getName();
    }

    /**
     * m
     * 摄氏度转华氏度
     */
    public static float selsiusToFahrenheit(float celsius) {
        return formatTemp(((9.0f / 5) * celsius + 32));
    }

    /**
     * 华氏度转摄氏度
     */
    public static float fahrenheitToCelsius(float fahrenhei) {
        return formatTemp((fahrenhei - 32) * (5.0f / 9));
    }

    /**
     * 获取低电量prefrence的key
     */
    public static String getLowPowerSharedPreferencesKey(String macaddress) {
        return "low_power:" + macaddress;
    }

    /**
     * 获取高温提醒prefrence的key
     */
    public static String getHighTempWarmSharedPreferencesKey(String macaddress) {
        return "hight_warm:" + macaddress;
    }

    /**
     * 获取低温提醒prefrence的key
     */
    public static String getLowTempWarmSharedPreferencesKey(String macaddress) {
        return "low_warm:" + macaddress;
    }

    /**
     * 设置页面的透明度
     *
     * @param bgAlpha 1表示不透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        if (activity == null) return;
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 将float 温度值转为两位小数的温度值
     */
    public static float formatTemp(double temp) {
        if (mTempFormatter == null) {
            mTempFormatter = new DecimalFormat("##0.00");
        }
        return Float.valueOf(mTempFormatter.format(temp));
    }

    /**
     * 获取提醒时间间隔
     */
    public static long getWarmDuration() {
        return SpUtils.getLong(AppConfigs.SP_KEY_NOTIFY_DURATION + ":" + App.get().getApiUid(), Settings.DEFAULT_WARM_DURATION);
    }

    /**
     * 设置提醒时间间隔
     */
    public static void setWarmDuration(long duration) {
        if (duration == 0) {
            duration = Settings.DEFAULT_WARM_DURATION;
        }
        SpUtils.saveLong(AppConfigs.SP_KEY_NOTIFY_DURATION + ":" + App.get().getApiUid(), duration);
    }


    /**
     * 获取提醒时间间隔
     */
    public static String getWarmDurationStr() {
        return String.valueOf(getWarmDuration() / 60000) + UIUtils.getString(R.string.string_minutes_unit);
    }

    public static String getWifiConnectedSsidAscii(Context context, String ssid) {
        final long timeout = 100;
        final long interval = 20;
        String ssidAscii = ssid;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        boolean isBreak = false;
        long start = System.currentTimeMillis();
        do {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignore) {
                isBreak = true;
                break;
            }
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult scanResult : scanResults) {
                if (scanResult.SSID != null && scanResult.SSID.equals(ssid)) {
                    isBreak = true;
                    try {
                        Field wifiSsidfield = ScanResult.class.getDeclaredField("wifiSsid");
                        wifiSsidfield.setAccessible(true);
                        Class<?> wifiSsidClass = wifiSsidfield.getType();
                        Object wifiSsid = wifiSsidfield.get(scanResult);
                        Method method = wifiSsidClass.getDeclaredMethod("getOctets");
                        byte[] bytes = (byte[]) method.invoke(wifiSsid);
                        ssidAscii = new String(bytes, "ISO-8859-1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } while (System.currentTimeMillis() - start < timeout && !isBreak);

        return ssidAscii;
    }

    public static String getWifiConnectedBssid(Context context) {
        WifiInfo mWifiInfo = getConnectionInfo(context);
        String bssid = null;
        if (mWifiInfo != null && NetUtils.isWifiConnected(context)) {
            bssid = mWifiInfo.getBSSID();
        }
        return bssid;
    }

    private static WifiInfo getConnectionInfo(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.getConnectionInfo();
    }

    /**
     * 获取指定activity下所有的viewmodel
     */
    public static Map<String, ViewModel> getAllViewModel(FragmentActivity activity) {
        try {
            ViewModelStore viewModelStore = ViewModelStores.of(activity);
            Field field = viewModelStore.getClass().getDeclaredField("mMap");
            field.setAccessible(true);
            return (Map<String, ViewModel>) field.get(viewModelStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除指定activity的指定key的viewmodel
     */
    public static void clearViewModel(FragmentActivity activity, String key) {
        //清除viewmodel
        if (activity == null) return;
        try {
            Map<String, ViewModel> mMapValue = getAllMeasureViewModel();
            Logger.w("销毁了viewmodel前size:" + mMapValue.size());
            if (mMapValue.containsKey(key)) {
                mMapValue.remove(key);
                Logger.w("销毁了viewmodel后size:" + mMapValue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.w("反射关闭viewmodel失败");
            ViewModelStores.of(activity).clear();
        }
    }

    /**
     * 清除所有测量viewmodel
     */
    public static void clearAllMeasureViewModel() {
        Map<String, ViewModel> allViewModel = getAllViewModel(ActivityManager.findActivity(Settings.MEASURE_ACTIVITY));
        if (allViewModel != null) {
            Logger.w("销毁所有测量viewmodel");
            allViewModel.clear();
        }
    }

    /**
     * 获取测量的viewmodel
     */
    public static MeasureViewModel getMeasureViewmodel(String macaddress, long profileId) {
        if (TextUtils.isEmpty(macaddress)) return null;
        return ViewModelProviders.of(ActivityManager.findActivity(Settings.MEASURE_ACTIVITY)).get(macaddress + ":" + profileId, MeasureViewModel.class);
    }

    /**
     * 获取测量的viewmodel
     *
     * @param macaddress 通过mac地址去匹配viewmodel
     */
    public static MeasureViewModel getMeasureViewmodel(String macaddress) {
        if (TextUtils.isEmpty(macaddress)) return null;
        Map<String, ViewModel> allViewmodel = getAllMeasureViewModel();
        for (String key : allViewmodel.keySet()) {
            if (key.startsWith(macaddress)) {
                return (MeasureViewModel) allViewmodel.get(key);
            }
        }
        return null;
    }

    public static Map<String, ViewModel> getAllMeasureViewModel() {
        return getAllViewModel(ActivityManager.findActivity(Settings.MEASURE_ACTIVITY));
    }

    /**
     * 清除测量viewmodel
     */
    public static void clearMeasureViewModel(String macaddress, long profileId) {
        clearViewModel(ActivityManager.findActivity(Settings.MEASURE_ACTIVITY), macaddress + ":" + profileId);
    }

    /**
     * 未登录下隐藏某个view
     */
    public static void notLoginViewHide(View view) {
        if (!App.get().isLogined()) {
            view.setVisibility(View.GONE);
        }
    }

    public static String getShareId(long profileId) {
        Random random = new Random();
        return codeProfileID("" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + App.get().getApiUid() + "+" + profileId + "+2" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10));
    }

    public static String codeProfileID(String string) {
        return string.replaceAll("0", "A").replaceAll("1", "C").replaceAll("2", "E").replaceAll("3", "G").replaceAll("4", "H").replaceAll("5", "K").replaceAll("6", "M").
                replaceAll("7", "P").replaceAll("8", "S").replaceAll("9", "T").replaceAll("\\+", "Z");
    }

    /**
     * 是否打开测量卡片设备
     */
    public static boolean hasMeasureItem() {
        boolean hasMeasureItem = false;
        if (ActivityManager.hasActivity(HomeActivity.class)) {
            HomeActivity activity = ActivityManager.findActivity(HomeActivity.class);
            hasMeasureItem = activity.hasMeasureItem();
        }
        return hasMeasureItem;
    }

    /**
     * 一个mac地址对应多少个measureviewmodel
     */
    public static int getPatchMeasureSize(String macaddress) {
        Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
        int count = 0;
        if (viewmodels != null && viewmodels.size() > 0) {
            for (String key : viewmodels.keySet()) {
                if (viewmodels.get(key) instanceof MeasureViewModel) {
                    //测量的viewmodel
                    MeasureViewModel viewModel = (MeasureViewModel) viewmodels.get(key);
                    if (viewModel == null || viewModel.measureInfo == null) continue;
                    if (viewModel.measureInfo.get().getMacaddress().equalsIgnoreCase(macaddress)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * 检查贴是否在测量
     */
    public static boolean checkPatchIsMeasuring(String macaddress) {
        Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
        if (viewmodels != null && viewmodels.size() > 0) {
            for (String key : viewmodels.keySet()) {
                if (viewmodels.get(key) instanceof MeasureViewModel) {
                    //测量的viewmodel
                    MeasureViewModel viewModel = (MeasureViewModel) viewmodels.get(key);
                    if (viewModel == null || TextUtils.isEmpty(viewModel.measureInfo.get().getMacaddress()))
                        continue;
                    if (viewModel.measureInfo.get().getMacaddress().equalsIgnoreCase(macaddress) && viewModel.isConnected()) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * 检查连接的mac地址是否在测量
     */
    public static boolean checkMacIsMeasuring(String dockerMac) {
        Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
        if (viewmodels != null && viewmodels.size() > 0) {
            for (String key : viewmodels.keySet()) {
                if (viewmodels.get(key) instanceof MeasureViewModel) {
                    MeasureBean measureBean = ((MeasureViewModel) viewmodels.get(key)).measureInfo.get();
                    if (measureBean == null) return false;
                    if (measureBean.getMacaddress().equalsIgnoreCase(dockerMac)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查当前档案是否在测量
     */
    public static boolean checkProfileIsMeasuring(long profileId) {
        Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
        if (viewmodels != null && viewmodels.size() > 0) {
            boolean isMeasuring = false;
            for (String key : viewmodels.keySet()) {
                if (viewmodels.get(key) instanceof MeasureViewModel) {
                    //测量的viewmodel
                    MeasureBean measureBean = ((MeasureViewModel) viewmodels.get(key)).measureInfo.get();
                    if (measureBean == null) continue;
                    if (measureBean.getProfile().getProfileId() == profileId&&((MeasureViewModel) viewmodels.get(key)).isConnected()) {
                        isMeasuring = true;
                    }
                }
            }
            return isMeasuring;
        }

        return false;
    }

    /**
     * 获取共享viewmodel的key防止和实时测量冲突
     */
    public static String getShareViewModelKey(String macaddress) {
        return "share:" + macaddress;
    }

    public static boolean isMobile(String mobiles) {
        String telRegex = "[19][0-9]\\d{9}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }


    /**
     * 判断wifi是否为5G
     */
    public static boolean is5GWIFI(Context context) {
        int freq = 0;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return false;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }

    public synchronized static String createJsonSkipLitepal(ReportBean report) {
        try {
            return new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().equals("associatedModelsMapForJoinTable")
                            || f.getName().equals("associatedModelsMapWithFK")
                            || f.getName().equals("associatedModelsMapWithoutFK")
                            || f.getName().equals("baseObjId")
                            || f.getName().equals("listToClearAssociatedFK")
                            || f.getName().equals("listToClearSelfFK");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).create().toJson(report);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isMyTestPhone() {
        return BuildConfig.DEBUG && ("SMARTISAN".equals(Build.BRAND)
                || "STF-AL10".equalsIgnoreCase(Build.MODEL) || "google".equalsIgnoreCase(Build.BRAND));
//        return BuildConfig.DEBUG;
    }

    /**
     * 是否需要重新创建对话框
     */
    public static boolean needRecreateDialog(SystemDialog dialog) {
        return dialog == null || dialog.getHostActivity() != ActivityManager.currentActivity();
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && view != null && view.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示测量
     */
    public static void showHomeMeasure() {
        if (ActivityManager.findActivity(Settings.MEASURE_ACTIVITY) != null) {
            ActivityManager.findActivity(Settings.MEASURE_ACTIVITY).showMeasureFragment();
        }
    }

    public static DeviceType getDeviceType(int type) {
        if (type == 2) {
            return DeviceType.P02;
        } else if (type == 3) {
            return DeviceType.P03;
        } else if (type == 4) {
            return DeviceType.P04;
        } else if (type == 5) {
            return DeviceType.P05;
        } else {
            return DeviceType.None;
        }
    }

    public static void vibrateAndSound() {
        Logger.w("报警开始...");
        boolean isOpenVibrator = SpUtils.getBoolean(AppConfigs.getSpKeyVibrator(), true);
        if (isOpenVibrator) {
            ViberatorManager.getInstance().vibrate();
        }
        List<AlarmBean> all = LitePal.where("uid = ?", App.get().getApiUid()).find(AlarmBean.class);
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getIsSelected() == 1) {
                MediaManager.getInstance().setAlarmFileName(all.get(i).getFileName());
            }
        }
        MediaManager.getInstance().playSound(true);
    }

    public static void cancelVibrateAndSound() {
        Logger.w("报警停止...");
        boolean isOpenVibrator = SpUtils.getBoolean(AppConfigs.getSpKeyVibrator(), true);
        if (isOpenVibrator) {
            ViberatorManager.getInstance().cancel();
        }
        ViberatorManager.getInstance().cancel();
        MediaManager.getInstance().stop();
    }

    public static String getShareTopic(String macaddress) {
        return "patch/" + macaddress;
    }

    /**
     * 检测应用更新
     */
    public static void checkUpdate(Activity activity, boolean showToast) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(activity)
                //更新地址
                .setUpdateUrl(BuildConfig.PROTON_SERVER_PATH + "openapi/android/version/get")
                .setHttpManager(new UpdateAppHttpUtil(activity.getApplicationContext()) {
                    @Override
                    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Callback callBack) {
                        params.put("version", CommonUtils.getAppVersion(activity) + "." + CommonUtils.getAppVersionCode(activity));
                        Map<String, String> headers = new HashMap<>();
                        headers.put("company", Settings.COMPANY);
                        super.asyncGet(url, params, headers, callBack);
                    }
                })
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(JSONUtils.getString(json, "data"));
                            updateAppBean.setUpdate(true)
                                    .setNewVersion(jsonObject.optString("currentVersion"))
                                    .setApkFileUrl(jsonObject.optString("apkUrl"))
                                    .setTargetSize(jsonObject.optString("size"))
                                    .setUpdateLog(jsonObject.optString("updateLog"))
                                    .setForce(jsonObject.optBoolean("isForce"))
                                    .setNewMd5(jsonObject.optString("md5"));

                            JSONArray jsonArray = jsonObject.optJSONArray("excludeVersion");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                List<String> excludeVersion = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    excludeVersion.add(jsonArray.getString(i));
                                }
                                updateAppBean.setExcludeVersion(excludeVersion);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            updateAppBean.setUpdate(false);
                        }
                        return updateAppBean;
                    }

                    @Override
                    protected void noNewApp(String error) {
                        if (showToast) {
                            BlackToast.show("当前已是最新版本");
                        }
                    }
                });
    }

    /**
     * 版本号比较
     *
     * @return 0代表相等，1代表version1大于version2，-1代表version1小于version2
     */
    public static int compareVersion(String version1, String version2) {
        try {
            if (version1.startsWith("V") || version1.startsWith("v")) {
                version1 = version1.substring(1);
            }
            if (version2.startsWith("V") || version2.startsWith("v")) {
                version2 = version2.substring(1);
            }
            if (version1.equals(version2)) {
                return 0;
            }
            String[] version1Array = version1.split("\\.");
            String[] version2Array = version2.split("\\.");
            int index = 0;
            // 获取最小长度值
            int minLen = Math.min(version1Array.length, version2Array.length);
            int diff = 0;
            // 循环判断每位的大小
            while (index < minLen
                    && (diff = Integer.parseInt(version1Array[index])
                    - Integer.parseInt(version2Array[index])) == 0) {
                index++;
            }
            if (diff == 0) {
                // 如果位数不一致，比较多余位数
                for (int i = index; i < version1Array.length; i++) {
                    if (Integer.parseInt(version1Array[i]) > 0) {
                        return 1;
                    }
                }

                for (int i = index; i < version2Array.length; i++) {
                    if (Integer.parseInt(version2Array[i]) > 0) {
                        return -1;
                    }
                }
                return 0;
            } else {
                return diff > 0 ? 1 : -1;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 判断是否要显示温度单位
     *
     * @return android:visibility="@{((viewmodel.needShowPreheating &amp;&amp; viewmodel.connectStatus == 2)||(viewmodel.needShowTempLow&amp;&amp;viewmodel.connectStatus==2)||App.get().getInstructionConstant()!=InstructionConstant.aa) ? View.GONE : View.VISIBLE}" />
     */
    public static boolean getTempUnitVisibility(int connectStatus, boolean needShowPreheating, boolean needShowTempLow) {
        InstructionConstant instructionConstant = App.get().getInstructionConstant();
        if (connectStatus == 2) {
            if (instructionConstant != InstructionConstant.aa) {
                return false;
            } else {
                if (needShowPreheating || needShowTempLow) {
                    return false;
                }
            }
        } else {
            return true;
        }
        return true;
    }

    /**
     * 获取wifi的ssid，兼容部分华为手机显示unknown ssid
     *
     * @return
     */
    public static String getWifiSsid() {
        if (NetUtils.isConnected(App.get())) {
            String connectWifiSsid = NetUtils.getConnectWifiSsid(App.get());
            if (isWifiSsidAvailable(connectWifiSsid)) {
                return connectWifiSsid;
            } else {
                return getWifiFormConfigNetWorks();
            }
        } else {
            BlackToast.show("wifi未连接成功");
            return null;
        }
    }


    /**
     * 检测wifi名称是否有用
     *
     * @param wifiSsid
     * @return
     */
    public static boolean isWifiSsidAvailable(String wifiSsid) {
        if (TextUtils.isEmpty(wifiSsid)) {
            return false;
        }
        if (wifiSsid.equalsIgnoreCase("<unknown ssid>")) {
            return false;
        }
        return true;
    }

    /**
     * 通过wifi列表获取wifi,兼容部分9.0华为手机显示unknown ssid
     *
     * @return
     */
    private static String getWifiFormConfigNetWorks() {
        WifiManager my_wifiManager = ((WifiManager) App.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        assert my_wifiManager != null;
        WifiInfo wifiInfo = my_wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = my_wifiManager.getConfiguredNetworks();
        if (isWifiSsidAvailable(ssid)) {
            return ssid;
        }
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.networkId == networkId) {
                ssid = wifiConfiguration.SSID;
                break;
            }
        }
        return ssid;
    }


    public static String parseBssid2Mac(String bssid) {
        StringBuilder macbuilder = new StringBuilder();
        for (int i = 0; i < bssid.length() / 2; i++) {
            macbuilder.append(bssid, i * 2, i * 2 + 2).append(":");
        }
        macbuilder.delete(macbuilder.length() - 1, macbuilder.length());
        return macbuilder.toString();
    }

    public static int getBattery() {
        BatteryManager manager = (BatteryManager) App.get().getSystemService(Context.BATTERY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return 0;
    }

    public static boolean isCharing() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = App.get().registerReceiver(null, ifilter);
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }


    /**
     * 获取高温提醒prefrence的key
     */
    public static String getHighTempWarmSpKey(String phone) {
        return "hight_warm:" + phone;
    }

    /**
     * 高温报警提醒时间间隔key
     *
     * @return
     */
    public static String getHighTempDurationSpKey() {
        return "hight_warm_duration";
    }

    public static void goToLogin(Context context) {
        goToLogin(context, null);
    }

    public static void goToLogin(Context context, String msg) {
        context.startActivity(new Intent(context, LoginActivity.class).putExtra("msg", msg));
        ActivityManager.finishOthersActivity(LoginActivity.class);
    }

    /**
     * 返回实时温度的颜色值
     *
     * @param temp
     * @return
     */
    public static String getCurrentTempColor(float temp) {
        ConfigInfo configInfo = App.get().getConfigInfo();
        float showTemp;
        if (App.get().getConfigInfo() != null && !App.get().getConfigInfo().getStatus().equalsIgnoreCase("error")) {
            showTemp = configInfo.getSettings().getShowTemp();
        } else {
            showTemp = 35;
        }

        if (temp < showTemp) {
            return "#FFB66F";
        } else if (temp >= showTemp && temp < 38.5) {
            return "#00B8FF";
        } else {
            return "#FF0D00";
        }
    }

    public static String getConnectStr(int connectStatus) {
        if (connectStatus == 0 || connectStatus == 3) {
            return App.get().getString(R.string.string_connect_bluetooth);
        } else if (connectStatus == 1) {
            return App.get().getString(R.string.string_connecting);
        } else {
            return App.get().getString(R.string.string_disconnect_bluetooth);
        }
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNum
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 设置recyclerview删除动画
     */
    public static void setRecyclerViewDeleteAnimation(RecyclerView mRecyclerView) {
        SlideInRightAnimator animator = new SlideInRightAnimator();
        animator.setRemoveDuration(400);
        animator.setAddDuration(400);
        mRecyclerView.setItemAnimator(animator);
    }


}
