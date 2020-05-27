package com.proton.runbear.viewmodel.measure;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.bean.rs.ShareBean;
import com.proton.runbear.component.App;
import com.proton.runbear.net.bean.ConfigInfo;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.MeasureCenter;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.BatteryChangeUtil;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.MQTTShareManager;
import com.proton.runbear.utils.SPConstant;
import com.proton.runbear.utils.Settings;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.WarmDialog;
import com.proton.runbear.viewmodel.BaseViewModel;
import com.proton.temp.connector.TempConnectorManager;
import com.proton.temp.connector.bean.ConnectionType;
import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.interfaces.AlgorithmStatusListener;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.DataListener;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;
import com.wms.utils.NetUtils;
import com.wms.utils.PreferenceUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 测量viewModel
 */
public class MeasureViewModel extends BaseViewModel {

    public ObservableField<MeasureBean> measureInfo = new ObservableField<>();

    public ObservableField<DeviceBean> device = new ObservableField<>();
    /**
     * 当前温度
     */
    public ObservableFloat currentTemp = new ObservableFloat(0);

    /**
     * 当前算法温度
     */
    public ObservableFloat algorithmTemp = new ObservableFloat(0);

    /**
     * 当前真实温度
     */
    public ObservableFloat currentRealTemp = new ObservableFloat(0);
    /**
     * 最高温
     */
    public ObservableFloat highestTemp = new ObservableFloat(0);
    /**
     * 电量
     */
    public ObservableInt battery = new ObservableInt(-1);
    /**
     * 手表电量
     */
    public ObservableInt watchBattery = new ObservableInt(-1);
    /**
     * 手表电量
     */
    public ObservableBoolean powerIsConnect = new ObservableBoolean(false);
    /**
     * 是否连接上了设备
     * 0 未连接 1连接中 2已连接 3手动断开连接
     */
    public ObservableInt connectStatus = new ObservableInt(0);
    /**
     * 温度稳定状态
     */
    public ObservableBoolean tempStabled = new ObservableBoolean(false);

    /**
     * 是否需要
     */
    public ObservableBoolean needShowPreheating = new ObservableBoolean(false);

    /**
     * 需要显示搜索设备的对话框
     */
    public ObservableBoolean needShowSearchDeviceDialog = new ObservableBoolean(false);

    /**
     * 固件版本
     */
    public ObservableField<String> hardVersion = new ObservableField<>("");
    /**
     * 序列号
     */
    public ObservableField<String> serialNumber = new ObservableField<>("");
    /**
     * 是否充电
     */
    public ObservableBoolean isCharge = new ObservableBoolean(false);
    /**
     * 测量提示文字
     */
    public ObservableField<String> measureTips = new ObservableField<>();

    /**
     * 心率
     */
    public ObservableInt heartRate = new ObservableInt();
    /**
     * 上次更新心率时间
     */
    public ObservableLong lastUpdateHeartTime = new ObservableLong();
    /**
     * 当前接收数据的序列号和上次不一致
     */
    private ObservableBoolean isNotSameDevice = new ObservableBoolean(false);
    /**
     * 设备是否损坏
     */
    public ObservableBoolean isDamaged = new ObservableBoolean(false);
    /**
     * 配置信息
     */
    public ObservableField<ConfigInfo> configInfo = new ObservableField<>();

    /**
     * 缓存温度,缓存温度只会在app打开的时候获取一次
     */
    public ObservableField<List<TempDataBean>> cacheTempList = new ObservableField<>();

    /**
     * mqtt的连接状态，每30秒获取一次
     */
    public ObservableBoolean isMqttConnectStatus = new ObservableBoolean(false);
    public Timer mMqttConnectStatusTimer;

    private Disposable mResetTempDisposed;
    private List<Float> rawTemps = new ArrayList<>();
    private List<Float> algorithmTemps = new ArrayList<>();
    private Timer mHeartTimer;
    private SensorManager mSensorManager;
    private Sensor mHeartSensor;
    private SensorEventListener mSensorEventListener;
    /**
     * 最后温度的状态值和手势，用户上传温度时使用
     */
    private int lastMeasureStatus;
    private int lastGesture;

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private BatteryChangeUtil batteryChangeUtil;

    /**
     * 算法姿势10分钟之后的判断
     */
    private boolean isAfterTenMin = false;

    /**
     * 连接状态的回调
     */
    private ConnectStatusListener mConnectorListener = new ConnectStatusListener() {

        @Override
        public void onConnectSuccess() {
            Logger.w("连接成功");
            measureTips.set("预热中");
            needShowSearchDeviceDialog.set(false);
            needShowPreheating.set(true);
            connectStatus.set(2);
            getHeartRate();
            uploadData();
            //建立mqtt连接
            mainHandler.postDelayed(() -> MQTTShareManager.getInstance().connectMQTTServer(), 2000);
            startMqttStatusTimer();
            //倒计时10分钟
            mainHandler.postDelayed(() -> isAfterTenMin = true, 10 * 60 * 1000);

        }

        @Override
        public void onConnectFaild() {
            //连接断开则重连
            Logger.w("连接失败");
            connectStatus.set(1);
        }

        @Override
        public void onDisconnect(boolean isManual) {
            connectStatus.set(isManual ? 3 : 0);
            clear();
            Logger.w("连接断开");
        }

        @Override
        public void receiveReconnectTimes(int retryCount, int leftCount) {
            //测量准备尝试一次就提示

            //重连过程中需要检测蓝牙是否开启
            if (!BluetoothUtils.isBluetoothOpened()) {
                Logger.w("重连过程中发现蓝牙关闭。。。");
                BluetoothUtils.openBluetooth();
            }

            Logger.w("重连次数:", retryCount, ",剩余次数:", leftCount);
        }

        @Override
        public void receiveNotSampleDevice(String oldMac, String newMac) {
            Logger.w("receiveNotSampleDevice 断开连接");
            connectStatus.set(1);
        }

        @Override
        public void receiveDockerOffline(boolean isOffline) {
            connectStatus.set(isOffline ? 1 : 2);
            Logger.w("receiveDockerOffline 断开连接");
        }
    };

    /**
     * 数据接收
     */
    private DataListener mDataListener = new DataListener() {
        private Runnable mDealWithTempRunnable = new Runnable() {
            @Override
            public void run() {
                dealWithTemp(new ArrayList<>(mCurrentTemps.get(mCurrentTemps.size() - 1)));
                mCurrentTemps.clear();
            }
        };
        private List<List<TempDataBean>> mCurrentTemps = new ArrayList<>();

        @Override
        public void receiveHardVersion(String version) {
            super.receiveHardVersion(version);
            PreferenceUtils.setPrefString(getContext(), SPConstant.PATCH_VERSION, version);
            hardVersion.set(version);
        }

        @Override
        public void receiveSerial(String serial) {
            super.receiveSerial(serial);
            if (!TextUtils.isEmpty(serialNumber.get()) && !serial.equalsIgnoreCase(serialNumber.get())) {
                isNotSameDevice.set(true);
                return;
            }
            serialNumber.set(serial);
        }

        @Override
        public void receiveCurrentTemp(List<TempDataBean> temps) {
            Logger.w("收到数据---实时");
            connectStatus.set(2);
            if (CommonUtils.listIsEmpty(temps)) {
                return;
            }
            for (TempDataBean temp : temps) {
                algorithmTemps.add(temp.getAlgorithmTemp());
                rawTemps.add(temp.getAlgorithmTemp());
            }
            //连续的数据，需要处理的原因是因为嵌入式有一批设备有问题
            mLastTemp = temps.get(temps.size() - 1).getAlgorithmTemp();
            mCurrentTemps.add(temps);
            mainHandler.removeCallbacks(mDealWithTempRunnable);
            startDealWithTempCountDown();
        }

        private void startDealWithTempCountDown() {
            mainHandler.postDelayed(mDealWithTempRunnable, 2000);
        }

        private void dealWithTemp(List<TempDataBean> temps) {
            float rawTemp = temps.get(temps.size() - 1).getTemp();
            if (rawTemp > 100) {
                isDamaged.set(true);
                measureTips.set("体温贴断裂\n请联系护士");
                return;
            } else {
                isDamaged.set(false);
                if (rawTemp < App.get().getConfigInfo().getSettings().getShowTemp()) {
                    measureTips.set("预热中");
                }
            }
            if (isConnected()) {
                currentTemp.set(temps.get(temps.size() - 1).getAlgorithmTemp());
            }
            algorithmTemp.set(temps.get(temps.size() - 1).getTemp());
            Logger.w("实时温度 getAlgorithmTemp: ", currentTemp.get(), " 算法温度 getTemp： ", algorithmTemp.get());
            currentRealTemp.set(rawTemp);
            uploadData();
            Logger.w("当前温度:", currentTemp.get(), ",size:", temps.size());
        }

        @Override
        public void receiveBattery(Integer batteryValue) {
            battery.set(batteryValue);
        }

        @Override
        public void receiveCharge(boolean charge) {
            isCharge.set(charge);
        }

        @Override
        public void receiveCacheTotal(Integer cacheCount) {
            super.receiveCacheTotal(cacheCount);
        }

        @Override
        public void receiveCacheTemp(List<TempDataBean> cacheTemps) {
            super.receiveCacheTemp(cacheTemps);
            Logger.w("收到数据---缓存");
            if (!App.get().getConfigInfo().isFirst()) {
                cacheTempList.set(cacheTemps);
            }
            //上传缓存温度
            uploadCacheData();
        }
    };

    /**
     * 算法姿势状态回调
     */
    private AlgorithmStatusListener mAlgorithmStatusListener = new AlgorithmStatusListener() {
        @Override
        public void receiveStatusAndGesture(int status, int gesture) {

            lastMeasureStatus = status;
            lastGesture = gesture;

            Logger.w("算法状态: ", status, " 手势: ", gesture, " 是否在十分钟后: ", isAfterTenMin);
            float showTemp = configInfo.get().getSettings().getShowTemp();
            if (showTemp > 0 && mLastTemp < showTemp) {
                needShowPreheating.set(true);
                measureTips.set("预热中");
                return;
            }

            if (isAfterTenMin && (gesture == 2 || gesture == 3)) {
                measureTips.set("请夹紧手臂\n手臂张开，温度偏低");
                return;
            }

            if (status == 3) {//温度稳定阶段
                measureTips.set("");
                tempStabled.notifyChange();
            } else {
                measureTips.set("请夹紧手臂");
            }
        }


    };

    private int packageNum = 1;
    private IMqttActionListener mqttCallback = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Logger.w("mqtt数据发送成功");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Logger.w("mqtt数据发送失败:", exception != null ? exception.getMessage() : "");
        }
    };
    private float mLastTemp;

    public MeasureViewModel() {
        Logger.w("初始化measureviewmodel...");
        currentTemp.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                //共享和mqtt连接不用发布消息
                highestTemp.set(Math.max(currentTemp.get(), highestTemp.get()));
            }
        });
        connectStatus.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Logger.w("connectStatus is ：", connectStatus.get());
                if (!isConnected()) {
                    if (!isManualDisconnect()) {
                        mResetTempDisposed = io.reactivex.Observable.just(1)
                                .delay(10, TimeUnit.MINUTES)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> {
                                    currentTemp.set(0);
                                    algorithmTemp.set(0);
                                    highestTemp.set(0);
                                });
                    } else {//添加手动断开蓝牙连接的时候，断开mqtt
                        measureTips.set("");
                        currentTemp.set(0);
                        algorithmTemp.set(0);
                        MQTTShareManager.getInstance().unsubscribe(App.get().getRawTempUploadTopic());
                        MQTTShareManager.getInstance().unsubscribe(App.get().getCacheTempUploadTopic());
                    }
                    needShowPreheating.set(false);
                } else {
                    if (mResetTempDisposed != null && !mResetTempDisposed.isDisposed()) {
                        mResetTempDisposed.dispose();
                    }
                }
            }
        });

        getBattery();
    }

    /**
     * 修改成通过广播获取系统电量
     */
    private void getBattery() {
        watchBattery.set(Utils.getBattery());
        powerIsConnect.set(Utils.isCharing());
        batteryChangeUtil = new BatteryChangeUtil(App.get());
        batteryChangeUtil.register(new BatteryChangeUtil.BatteryStateListener() {
            @Override
            public void onStateChanged() {
                watchBattery.set(Utils.getBattery());
                Logger.w("电量:", watchBattery.get());
            }

            @Override
            public void onPowerConnect() {
                Logger.w("电源插上");
                powerIsConnect.set(true);
            }

            @Override
            public void onPowerDisconnect() {
                Logger.w("电源拔出");
                powerIsConnect.set(false);
            }
        });
    }

    /**
     * 连接设备
     */
    public void connectDevice() {
        if (configInfo.get() == null || configInfo.get().getStatus().equalsIgnoreCase("error") || TextUtils.isEmpty(configInfo.get().getPatchMac())) {
            return;
        }
        connectDevice(Integer.MAX_VALUE);
    }

    public void connectDevice(int retryCount) {
        if (!getConnectorManager().isConnected()) {
            if (connectStatus.get() == 1) {
                connectStatus.notifyChange();
            } else {
                connectStatus.set(1);
            }
        }
        getConnectorManager()
                .setReconnectCount(retryCount)
                .setConnectTimeoutTime(60000)
                .setEnableCacheTemp(!App.get().getConfigInfo().isFirst())
                .addAlgorithmStatusListener(mAlgorithmStatusListener)
                .connect(mConnectorListener, mDataListener, true);
    }

    /**
     * 手动断开连接
     */
    public void disConnect() {
        clear();
        getConnectorManager().disConnect();
    }

    public TempConnectorManager getConnectorManager() {
        return TempConnectorManager.getInstance(device.get());
    }

    /**
     * 上传数据到mqtt（向指定的topic写数据）
     */
    public void uploadData() {
        Logger.w("数据大小:", algorithmTemps.size());
        if (device.get() == null || TextUtils.isEmpty(device.get().getMacaddress())) return;
        String mac = device.get().getMacaddress().replace(":", "");
        int timeInterval = configInfo.get().getSettings().getTempInterval();
        if (timeInterval <= 0) {
            timeInterval = 30;
        }
        if (CommonUtils.listIsEmpty(algorithmTemps)
                || algorithmTemps.size() < timeInterval
        ) return;
        float maxTemp = Collections.max(algorithmTemps);
        if (maxTemp < configInfo.get().getSettings().getUploadTemp()) {
            Logger.w("温度小于上传值，不上传，最高温度:", maxTemp);
            algorithmTemps.clear();
            rawTemps.clear();
            return;
        }

        ShareBean shareBean = new ShareBean();
        shareBean.setPackageNum(packageNum++);
        shareBean.setTotalSize(algorithmTemps.size());
        shareBean.setHeartRate(heartRate.get());
        shareBean.setTemp(new ArrayList<>(algorithmTemps));
        shareBean.setRawTemp(new ArrayList<>(rawTemps));
        shareBean.setHeartRateInterval(configInfo.get().getSettings().getHeartRateInterval());
        shareBean.setWatchIMEI(App.get().getIMEI());
        shareBean.setPatchMac(mac);
        shareBean.setSample(1);
        shareBean.setAppVersion(App.get().getAppVersion());
        shareBean.setTempInterval(configInfo.get().getSettings().getTempInterval());
        shareBean.setTimestamp(System.currentTimeMillis());
        shareBean.setBattery(battery.get());
        shareBean.setVersion(hardVersion.get());
        shareBean.setAlgorithmType(configInfo.get().getAlgorithType());
        shareBean.setWatchBattery(Utils.getBattery());
        shareBean.setAlgorithmVersion(App.get().getAlgorithmVersion());
        shareBean.setDamaged(isDamaged.get());
        shareBean.setGesture(lastGesture);
        shareBean.setMeasureStatus(lastMeasureStatus);
        Logger.w("mqtt数据准备发送,包序:" + (packageNum - 1) + ",数据大小:", algorithmTemps.size(), ",是否联网:", NetUtils.isConnected(getContext()), ",是否为4G信号:", !NetUtils.isWifi(getContext()));
        algorithmTemps.clear();
        rawTemps.clear();
        MQTTShareManager.getInstance().publish(App.get().getRawTempUploadTopic(), shareBean, mqttCallback);
    }

    /**
     * 上传缓存温度
     */
    private void uploadCacheData() {
        //isFirst为true表示不需要上传缓存温度
        if (App.get().getConfigInfo().isFirst()) {
            App.get().getConfigInfo().setFirst(false);
            return;
        }
        if (CommonUtils.listIsEmpty(cacheTempList.get())) return;

        int status = cacheTempList.get().get(cacheTempList.get().size() - 1).getMeasureStatus();
        int gesture = cacheTempList.get().get(cacheTempList.get().size() - 1).getGesture();

        Logger.w("缓存数据大小:", cacheTempList.get().size());
        if (device.get() == null || TextUtils.isEmpty(device.get().getMacaddress())) return;
        String mac = device.get().getMacaddress().replace(":", "");
        //如果缓存中的最高温度小于配置中的温度，则不上传到mqtt
        boolean isCanUpload = false;
        List<Float> rawCacheTemps = new ArrayList<>();
        List<Float> algorithmCacheTemps = new ArrayList<>();
        for (int i = 0; i < cacheTempList.get().size(); i++) {
            float algorithmTemp = cacheTempList.get().get(i).getAlgorithmTemp();
            float temp = cacheTempList.get().get(i).getTemp();
            algorithmCacheTemps.add(algorithmTemp);
            rawCacheTemps.add(temp);
            if (algorithmTemp >= configInfo.get().getSettings().getUploadTemp()) {
                isCanUpload = true;
            }
        }
        if (!isCanUpload) {
            Logger.w("温度小于上传值，不上传");
            cacheTempList.get().clear();
            return;
        }
        ShareBean shareBean = new ShareBean();
        shareBean.setPackageNum(packageNum++);
        shareBean.setTotalSize(cacheTempList.get().size());
        shareBean.setTemp(algorithmCacheTemps);
        shareBean.setRawTemp(rawCacheTemps);
        shareBean.setPatchMac(mac);
        shareBean.setSample(1);
        shareBean.setAppVersion(App.get().getAppVersion());
        shareBean.setTempInterval(configInfo.get().getSettings().getTempInterval());
        shareBean.setTimestamp(System.currentTimeMillis());
        shareBean.setBattery(battery.get());
        shareBean.setVersion(hardVersion.get());
        shareBean.setAlgorithmType(configInfo.get().getAlgorithType());
        shareBean.setMeasureStatus(status);
        shareBean.setGesture(gesture);
        shareBean.setWatchBattery(Utils.getBattery());
        shareBean.setAlgorithmVersion(App.get().getAlgorithmVersion());
        shareBean.setDamaged(isDamaged.get());
        Logger.w("mqtt数据准备发送,包序:" + (packageNum - 1) + ",数据大小:", algorithmTemps.size(), ",是否联网:", NetUtils.isConnected(getContext()), ",是否为4G信号:", !NetUtils.isWifi(getContext()));
        rawCacheTemps.clear();
        algorithmCacheTemps.clear();
        cacheTempList.get().clear();
        MQTTShareManager.getInstance().publish(App.get().getCacheTempUploadTopic(), shareBean, mqttCallback);
    }

    /**
     * 点击连接蓝牙按钮
     */
    public void clickConnectBluetoothBtn() {
        /**
         * 断开或者未连接的时候
         */
        if (connectStatus.get() == 0 || connectStatus.get() == 3) {
            needShowSearchDeviceDialog.set(true);
            getConfigInfo();
        } else if (connectStatus.get() == 2) {//已连接，点击则断开蓝牙

            new WarmDialog(ActivityManager.currentActivity())
                    .setTopText(R.string.string_warm_tips)
                    .setContent("确定断开蓝牙连接？")
                    .setConfirmListener(v -> disConnect())
                    .show();
        }
    }

    /**
     * 该版本暂没有这个功能
     */
    private void getHeartRate() {
        //先去掉该功能
        if (true) {
            return;
        }
        long interval = configInfo.get().getSettings().getHeartRateInterval();
        if (interval <= 0) {
            return;
        }
        if (mHeartTimer != null) {
            mHeartTimer.cancel();
        }
        mHeartTimer = new Timer();
        mHeartTimer.schedule(new TimerTask() {

            private boolean canShowHeartRate;

            @Override
            public void run() {

                float minTemp = configInfo.get().getSettings().getHeartRateMinTemp();
                if (currentTemp.get() < minTemp) {
                    Logger.w("当前温度小于最小获取心率温度:temp:", currentTemp.get(), ",min:", minTemp);
                    return;
                }

                if (!isConnected()) {
                    Logger.w("为连接不获取心率");
                    return;
                }

                if (mHeartSensor != null) {
                    mSensorManager.registerListener(mSensorEventListener, mHeartSensor, SensorManager.SENSOR_DELAY_UI);
                }
                if (mSensorManager == null) {
                    mSensorManager = (SensorManager) App.get().getSystemService(Context.SENSOR_SERVICE);
                }
                if (mHeartSensor == null) {
                    mHeartSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
                }
                if (mSensorEventListener == null) {
                    mSensorEventListener = new SensorEventListener() {
                        @Override
                        public void onAccuracyChanged(Sensor sensor, int arg1) {
                        }

                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor == mHeartSensor) {
                                int rate = (int) event.values[0];
                                Logger.w("心率:", rate, ",canShowHeartRate:", canShowHeartRate);
                                if (canShowHeartRate) {
                                    lastUpdateHeartTime.set(System.currentTimeMillis());
                                    heartRate.set(rate);
                                }
                            }
                        }
                    };
                }
                mSensorManager.registerListener(mSensorEventListener, mHeartSensor, SensorManager.SENSOR_DELAY_UI);
                mainHandler.postDelayed(() -> {
                    canShowHeartRate = true;
                    mainHandler.postDelayed(() -> {
                        canShowHeartRate = false;
                        mSensorManager.unregisterListener(mSensorEventListener);
                    }, 5000);
                }, 15000);
            }
        }, 15000, interval * 1000);
    }

    /**
     * 获取润生服务器上的配置信息
     */
    public void getConfigInfo() {
        measureTips.set("正在获取\n配置信息");
        MeasureCenter.fetchConfigInfo(App.get().getPhone(), new NetCallBack<ConfigInfo>() {
            @Override
            public void noNet() {
                super.noNet();
            }

            @Override
            public void onSucceed(ConfigInfo config) {
                if (config == null || config.getStatus().equalsIgnoreCase("error")) {//获取配置信息失败，重新登录
                    SpUtils.saveString(Constants.PHONE, "");
                    //重置报警设置
                    SpUtils.saveLong(Utils.getHighTempDurationSpKey(), 0);
                    SpUtils.saveLong(Utils.getHighTempWarmSpKey(App.get().getPhone()), 0);
                    Utils.goToLogin(ActivityManager.getTopActivity());
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.CONFIG_NULL).setMsg(config.getErrodMsg()));
                    return;
                }
                App.get().setConfigInfo(config);
                configInfo.set(config);

                //进入界面显示搜索对话框
                needShowSearchDeviceDialog.set(true);
                if (TextUtils.isEmpty(config.getPatchMac())) {
                    measureTips.set("该手机未绑定体温贴");
                    needShowSearchDeviceDialog.set(false);
                    return;
                }

                //保存高温报警周期interval
                long alarmInterval = configInfo.get().getSettings().getClearAlarmInterval();
                Logger.w("服务器上的报警周期: ", alarmInterval);
                if (App.get().getAlarmDuration() == 0) {
                    SpUtils.saveLong(Utils.getHighTempDurationSpKey(), alarmInterval <= 0 ? Settings.HIGH_TEMP_ALARM_DURATION_DEFAULT : alarmInterval);
                }
                device.set(new DeviceBean(App.get().getDeviceMac()));
                connectStatus.set(1);
                //扫描设备，判断附近是否有该贴，并且已打开

                //获取配置成功，连接体温贴
                measureTips.set("正在连接体温贴...");
                mainHandler.postDelayed(() -> {
                    connectDevice();//连接设备
                }, 2000);


            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("获取配置信息失败:", resultPair.getData());
                measureTips.set("获取配置信息失败");
            }
        });
    }

    /**
     * 清空状态
     */
    private void clear() {
        if (mHeartTimer != null) {
            mHeartTimer.cancel();
        }
        if (mResetTempDisposed != null && !mResetTempDisposed.isDisposed()) {
            mResetTempDisposed.dispose();
        }
        connectStatus.set(3);
        algorithmTemp.set(0);
        highestTemp.set(0);
    }

    /**
     * 开始获取mqtt状态的定时器
     */
    private void startMqttStatusTimer() {
        if (mMqttConnectStatusTimer == null) {
            mMqttConnectStatusTimer = new Timer();
            mMqttConnectStatusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isMqttConnectStatus.set(MQTTShareManager.getInstance().isConnect());
                }
            }, 6 * 1000, 30 * 1000);
        }
    }

    /**
     * 设备是否连接
     */
    public boolean isConnected() {
        return connectStatus.get() == 2;
    }

    /**
     * 设备是否连接中
     */
    public boolean isConnecting() {
        return connectStatus.get() == 1;
    }

    /**
     * 设备是否自动断开连接
     */
    public boolean isDisconnect() {
        return connectStatus.get() == 0;
    }

    /**
     * 设备是否手动断开连接
     */
    public boolean isManualDisconnect() {
        return connectStatus.get() == 3;
    }

    /**
     * 是否是蓝牙连接
     */
    public boolean isBluetoothConnect() {
        return getConnectorManager().isBluetoothConnect();
    }

    /**
     * 只有蓝牙连接一种方式
     *
     * @return
     */
    public ConnectionType getConnectionType() {
        return ConnectionType.BLUETOOTH;
    }


}
