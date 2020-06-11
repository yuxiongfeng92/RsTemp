package com.proton.runbear.component;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baidu.mobstat.StatService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.proton.runbear.BuildConfig;
import com.proton.runbear.R;
import com.proton.runbear.bean.AliyunToken;
import com.proton.runbear.constant.AppConfigs;
import com.proton.runbear.database.ProfileManager;
import com.proton.runbear.enums.InstructionConstant;
import com.proton.runbear.net.bean.ConfigInfo;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.socailauth.PlatformConfig;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.BuglyUtils;
import com.proton.runbear.utils.ClassicsFooter;
import com.proton.runbear.utils.ClassicsHeader;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.Density;
import com.proton.runbear.utils.DockerSetNetworkManager;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.ImagePipelineConfigFactory;
import com.proton.runbear.utils.MQTTShareManager;
import com.proton.runbear.utils.NotificationUtils;
import com.proton.runbear.utils.SPConstant;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.UmengUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.utils.ViberatorManager;
import com.proton.runbear.utils.net.OSSUtils;
import com.proton.temp.algorithm.AlgorithmManager;
import com.proton.temp.connector.TempConnectorManager;
import com.proton.temp.connector.bean.MQTTConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.taobao.sophix.SophixManager;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;
import com.wms.utils.PreferenceUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import blufi.espressif.apputil.utils.BlufiApp;

/**
 * Created by wangmengsi on 2018/2/26.b
 */

@Keep
public class App extends BlufiApp {

    private static App mInstance;
    public AliyunToken aliyunToken;//阿里云token
    public ExecutorService mCachePool = null;
    //    public IWXAPI wxApi;
    public boolean hasShowBackgroundTip;
    private String version;
    private String systemInfo;
    private long outTime = 0;
    /**
     * 是否已经显示gps警告
     */
    private boolean hasShowGpsWarm;
    /**
     * 上次扫描的设备的id
     */
    private String lastScanDeviceId;
    /**
     * 是否扫描了二维码
     */
    private List<Long> hasScanQRCode = new ArrayList<>();

    /**
     * 定义算法时长，及姿势（2 3 4 ）的占比
     */
    private long duration = 600;

    /**
     * 算法姿势（2 3 4 ）在duration内占比阈值
     */
    private float percentage = 70;

    /**
     * 第三方登录或者验证码登录的时候需要判断是否新用户,密码登录可肯定是老用户
     */
    private boolean isNewUser = false;

    /**
     * 测量页面的指令，默认是aa
     */
    private InstructionConstant instructionConstant = InstructionConstant.aa;

    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;
    /**
     * app是否在前台
     */
    private boolean isForeground;

    private ConfigInfo configInfo;
    private String imei;
    private String appVersion;

    public static App get() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
        UmengUtils.initUmeng();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private void init() {
        String processName = Utils.getProcessName(this);
        Logger.w("进程名称:" + processName + ",packageName = " + getPackageName());
        if (processName != null && !processName.equals(getPackageName())) {
            return;
        }
        ActivityManager.registerActivityListener(this);
        //Fresco初始化
        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        }
        Density.setDensity(this, 375, 667);
        NotificationUtils.initNotificationChannel(this);
        //初始化日志
        Logger.newBuilder()
                .tag("RS_log")
                .showThreadInfo(false)
                .methodCount(1)
                .saveLogCount(7)
                .context(this)
                .deleteOnLaunch(false)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
        //连接器初始化

        if (BuildConfig.DEBUG) {
            TempConnectorManager.init(this, new MQTTConfig("tcp://47.93.2.87:1883", "admin", "public"));
        } else {
            TempConnectorManager.init(this);
        }
        //下拉刷新初始化
        initRefresh();
        //数据库初始化
        LitePal.initialize(this);
        //阿里云token初始化
        OSSUtils.initOss();
        ViberatorManager.init(this);
        //注册微信
//        wxApi = WXAPIFactory.createWXAPI(App.get(), AppConfigs.WX_APP_ID, true);
//        wxApi.registerApp(AppConfigs.WX_APP_ID);
        // 初始化微信  将该app注册到微信
        PlatformConfig.setWeixin(AppConfigs.WX_APP_ID);

        DockerSetNetworkManager.init(this);
        MQTTShareManager.init(this);
        BuglyUtils.init(this);
        //百度统计
        if (!BuildConfig.IS_INTERNAL) {
            StatService.setAppKey("f5f6af6091");
        }
        StatService.setAppChannel(this, "新版", true);
        StatService.setDebugOn(BuildConfig.DEBUG);
        ZXingLibrary.initDisplayOpinion(this);
        Logger.w("手机型号:", Build.MANUFACTURER, ":", Build.MODEL, "当前版本:", getVersion());

        Logger.w("current token is :", getToken(), " ,uid is :", getApiUid(), " ,phone is :", getPhone(), " ,macAddress is :", getDeviceMac());
    }

    public void initRefresh() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater((context, layout) -> new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate));
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater((context, layout) -> new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate));
    }

    public String getToken() {
        String token = SpUtils.getString(Constants.APITOKEN, "");
//        String token = "48b4d2c19a6a4f5886477add4c5e45a1";
        return token;
    }

    public String getApiUid() {
        String uid = SpUtils.getString(Constants.APIUID, "");
        return uid;
    }

    public String getPhone() {
        return SpUtils.getString(Constants.PHONE, "");
    }


    public String getVersion() {
        if (TextUtils.isEmpty(version)) {
            version = CommonUtils.getAppVersion(this) + "&" + CommonUtils.getAppVersionCode(this);
        }
        return version;
    }

    public int getVersionCode() {
        int appVersionCode;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersionCode = info.versionCode; //版本名
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        return appVersionCode;
    }


    /**
     * 获取绑定的设备mac
     *
     * @return
     */
    public String getDeviceMac() {
        return SpUtils.getString(Constants.BIND_MAC, "");
    }

    /**
     * 与服务器交互的mac地址不能有冒号
     *
     * @return
     */
    public String getServerMac() {
        String mac = SpUtils.getString(Constants.BIND_MAC, "");
        if (!TextUtils.isEmpty(mac)) {
            String serverMac = mac.replaceAll(":", "");
            return serverMac;
        }
        return null;
    }

    public ExecutorService getCachePool() {
        if (null == mCachePool) {
            mCachePool = Executors.newCachedThreadPool();
        }
        return mCachePool;
    }

    public String getSystemInfo() {
        if (TextUtils.isEmpty(systemInfo)) {
            systemInfo = android.os.Build.MODEL + "&" + android.os.Build.VERSION.RELEASE;
        }
        return systemInfo;
    }

    public void logout() {
        clearCache();
        if (!BuildConfig.DEBUG) {
            SophixManager.getInstance().queryAndLoadNewPatch();
        }
        //停用服务
//        stopService(new Intent(this, AliyunService.class));
        //关闭共享
        MQTTShareManager.getInstance().close();
        //断开所有连接
        TempConnectorManager.close();
        //取消震动，防止一些意外情况导致震动一直存在
        Utils.cancelVibrateAndSound();
        ActivityManager.appExit();
        restartApplication();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        ProfileManager.deleteAll();
        //清空用户数据
        SpUtils.saveString(Constants.APITOKEN, "");
        SpUtils.saveString(Constants.APIUID, "");
        SpUtils.saveString(Constants.PHONE, "");
        SpUtils.saveString(Constants.BIND_MAC, "");
        hasScanQRCode.clear();
        hasShowBackgroundTip = false;
    }

    public void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showLoginOut() {
        if (System.currentTimeMillis() - outTime > 5 * 1000) {
            outTime = System.currentTimeMillis();
            BlackToast.show(R.string.string_please_login_again);
        }
    }

    /**
     * 是否已经登录
     */
    public boolean isLogined() {
        return !TextUtils.isEmpty(getToken());
    }

    public boolean hasShowGpsWarm() {
        return hasShowGpsWarm;
    }

    public void setHasShowGpsWarm(boolean hasShowGpsWarm) {
        this.hasShowGpsWarm = hasShowGpsWarm;
    }

    public String getLastScanDeviceId() {
        return lastScanDeviceId;
    }

    public void setLastScanDeviceId(String lastScanDeviceId) {
        this.lastScanDeviceId = lastScanDeviceId;
    }

    public InstructionConstant getInstructionConstant() {
        Logger.w("指令 is : ", instructionConstant.getInstruction());
        return instructionConstant;
    }

    public void setInstructionConstant(InstructionConstant instructionConstant) {
        this.instructionConstant = instructionConstant;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public List<Long> getHasScanQRCode() {
        return hasScanQRCode;
    }

    public void kickOff() {
        showLoginOut();
        logout();
    }

    public boolean isNewUser() {
        return isNewUser;
    }

//    public boolean isNewUser() {
//        return true;
//    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    /**
     * 退出应用时销毁所有的Activity
     */
    public static void clear() {
        try {
            ActivityManager.finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }
    }

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activityAount == 0) {
                //app回到前台
                isForeground = true;
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.APP_ISFOREGROUND, isForeground));
            }
            activityAount++;
            Logger.w("app isForeground : ", isForeground, " ,activityAount : ", activityAount);
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
                //app切换到后台
                isForeground = false;
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.APP_ISFOREGROUND, isForeground));
            }
            Logger.w("app isForeground : ", isForeground, " ,activityAount : ", activityAount);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public boolean isForeground() {
        return isForeground;
    }


    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        if (configInfo != null && !TextUtils.isEmpty(configInfo.getPatchMac())) {
            String mac = configInfo.getPatchMac();
            if (!TextUtils.isEmpty(mac)) {
                if (!mac.contains(":")) {
                    mac = Utils.parseBssid2Mac(mac);
                }
                PreferenceUtils.setPrefString(get(), SPConstant.PATCH_MAC, mac);
            }
        }
        AlgorithmManager.init(configInfo.getAlgorithType());
    }

    public ConfigInfo getConfigInfo() {
        return configInfo;
    }


    public String getIMEI() {
        if (TextUtils.isEmpty(imei)) {
            imei = getDeviceImei();
        }
        return imei;
    }

    @SuppressLint("HardwareIds")
    public String getDeviceImei() {
        if (ActivityCompat.checkSelfPermission(get(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) get().getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                return manager.getDeviceId();
            }
        }
        return "";
    }


    public String getAppVersion() {
        if (TextUtils.isEmpty(appVersion)) {
            appVersion = BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE;
        }
        return appVersion;
    }

    public String getAlgorithmVersion() {
        return AlgorithmManager.getVersion();
    }

    /**
     * 获取上传实时温度的topic
     *
     * @return
     */
    public String getRawTempUploadTopic() {
        ConfigInfo configInfo = getConfigInfo();
        if (configInfo == null || TextUtils.isEmpty(configInfo.getOrgId()) || TextUtils.isEmpty(configInfo.getPatchMac())) {
            return null;
        }
        String orgId = configInfo.getOrgId();
        String patchMac = configInfo.getPatchMac();
        String mac = patchMac.replace(":", "");
        Logger.w("上传实时温度的topic ： ", "temp/" + orgId + "/" + mac);
        return "temp/" + orgId + "/" + mac;
    }

    /**
     * 获取上传缓存温度的topic
     *
     * @return
     */
    public String getCacheTempUploadTopic() {
        ConfigInfo configInfo = getConfigInfo();
        if (configInfo == null || TextUtils.isEmpty(configInfo.getOrgId()) || TextUtils.isEmpty(configInfo.getPatchMac())) {
            return null;
        }
        String orgId = configInfo.getOrgId();
        String patchMac = configInfo.getPatchMac();
        String mac = patchMac.replace(":", "");
        Logger.w("上传缓存温度的topic ： ", "tempcache/" + orgId + "/" + mac);
        return "tempcache/" + orgId + "/" + mac;
    }

    /**
     * 获取报警时间间隔
     *
     * @return
     */
    public long getAlarmDuration() {
        return PreferenceUtils.getPrefLong(get(), Utils.getHighTempDurationSpKey(), 0);
    }

}
