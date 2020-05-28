package com.proton.runbear.fragment.measure;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.MotionEvent;

import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.FragmentMeasureItemBinding;
import com.proton.runbear.enums.InstructionConstant;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.AlarmClearTimer;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.UIUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.AlarmPickerDialog;
import com.proton.runbear.view.CustomClickableSpan;
import com.proton.runbear.view.WarmDialog;
import com.proton.runbear.view.WarmDialogHorizental;
import com.proton.runbear.viewmodel.measure.MeasureViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

/**
 * Created by wangmengsi on 2018/2/28.
 */

public class MeasureItemFragment extends BaseMeasureFragment<FragmentMeasureItemBinding, MeasureViewModel> {

    private static AlarmManager mAlarmManager;
    private static PendingIntent wakeIntent;
    private boolean hasShowWarnDialog;
    private boolean isHaveSetType;

    public static MeasureItemFragment newInstance(MeasureBean measureBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("measureInfo", measureBean);
        MeasureItemFragment fragment = new MeasureItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_item;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        binding.setViewmodel(viewmodel);
        viewmodel.currentTemp.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                doAlarm(viewmodel.currentTemp.get());
                if (!isHaveSetType) {
                    binding.idCurveView.setChartType(InstructionConstant.bb);
                    isHaveSetType = true;
                }

                if (viewmodel.currentTemp.get() > 0) {
                    binding.idCurveView.addData(viewmodel.currentTemp.get(), viewmodel.algorithmTemp.get());
                }
            }
        });

        viewmodel.connectStatus.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewmodel.isConnected()) {
                    binding.idCurveView.setMinTemp(App.get().getConfigInfo().getSettings().getShowTemp());
                }
            }
        });

        viewmodel.cacheTempList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!isHaveSetType) {
                    binding.idCurveView.setChartType(InstructionConstant.bb);
                    isHaveSetType = true;
                }

                binding.idCurveView.addDatas(clearAvailableTemp(viewmodel.cacheTempList.get()));
            }
        });

        viewmodel.configInfo.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Logger.w("唤醒服务开启");
                if (viewmodel.configInfo.get().getSettings() != null) {
                    initAlarmManager(viewmodel.configInfo.get().getSettings().getTempInterval());
                }
                if (viewmodel.configInfo.get().getStatus().equalsIgnoreCase("error")) {
                    finish();
                    Utils.goToLogin(mContext);
                }
                initTips();
            }
        });

        showBackgroundTips();
        if (!BluetoothUtils.isBluetoothOpened()) {
            BluetoothUtils.openBluetooth();
        }
        clearAlarm();
        Logger.w("算法版本:", App.get().getAlgorithmVersion());

    }

    @Override
    protected void initView() {
        super.initView();
        initTips();
        binding.ivSetting.setOnClickListener(v -> initSetDialog());

        //解决测量滑动和ScrollView滑动的冲突
        if (binding.idCurveView.getLineChart() != null) {
            binding.idCurveView.getLineChart().setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {//滑动事件由ScrollView处理
                    binding.idScrollview.requestDisallowInterceptTouchEvent(false);
                } else {//事件由curveView处理
                    binding.idScrollview.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            });
        }
    }

    @Override
    public void initData() {
        super.initData();
//        doNetwork();
    }

    public static void initAlarmManager(float wait) {
        if (wait <= 0) {
            wait = 30;
        }
        if (wakeIntent == null) {
            wakeIntent = PendingIntent.getBroadcast(App.get(), 0, new Intent(App.get(), WakeReceiver.class), 0);
        }
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) App.get().getSystemService(Context.ALARM_SERVICE);
        }
        mAlarmManager.cancel(wakeIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) (SystemClock.elapsedRealtime() + wait * 1000), wakeIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) (SystemClock.elapsedRealtime() + wait * 1000), wakeIntent);
        }
    }

    /**
     * 高温报警
     *
     * @param temp
     */
    private void doAlarm(float temp) {
        if (isNeedWarm(temp)) {
            showWarmDialog();
        } else {
            showNormalTemp();
        }
    }

    private void showNormalTemp() {
        if (mHighestWarmDialog != null) {
            mHighestWarmDialog.dismiss();
        }
        hasShowWarnDialog = false;
        Utils.cancelVibrateAndSound();
    }

    /**
     * 显示报警对话框
     */
    private void showWarmDialog() {
        hasShowWarnDialog = true;
        if (mHighestWarmDialog == null) {
            mHighestWarmDialog = new WarmDialog(getActivity())
                    .setTopBackGround(R.drawable.main_round_red_10_topbg)
                    .setTopText("高温报警")
                    .setConfirmText(R.string.string_i_konw)
                    .hideCancelBtn();
        }
        mHighestWarmDialog.setOnDismissListener(dialog -> {
            AlarmClearTimer.setCountDownTime(viewmodel.configInfo.get().getSettings().getClearAlarmInterval());
            AlarmClearTimer.setAlarmClearTimerCallback(() -> hasShowWarnDialog = false);
            AlarmClearTimer.start();
            Utils.cancelVibrateAndSound();
            SpUtils.saveLong(Utils.getHighTempWarmSpKey(App.get().getPhone()), System.currentTimeMillis());

        });
        mHighestWarmDialog.setContent(String.format("宝宝的体温已经%s了！", Utils.getTempAndUnit(viewmodel.currentTemp.get())));
        if (!mHighestWarmDialog.isShowing()) {
            Utils.vibrateAndSound();
            mHighestWarmDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.w("销毁");
        clearAlarm();
    }

    private void clearAlarm() {
        Logger.w("报警:清除");
        Utils.cancelVibrateAndSound();
        showNormalTemp();
    }

    public static class WakeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.w("唤醒服务");
            EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.WAKE));
            if (App.get().getConfigInfo() != null && App.get().getConfigInfo().getSettings() != null) {
                initAlarmManager(App.get().getConfigInfo().getSettings().getTempInterval());
            }
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent messeage) {
        super.onMessageEvent(messeage);
        if (messeage.getEventType() == MessageEvent.EventType.WAKE) {
            viewmodel.uploadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewmodel.uploadData();
    }

    /**
     * 初始化底部说明
     */
    private void initTips() {
        String[] clickAryStr = new String[]{UIUtils.getString(R.string.string_kefu_phone)};
        String tip = UIUtils.getString(R.string.string_tip);
        if (viewmodel.configInfo.get() != null && viewmodel.configInfo.get().getSettings() != null
                && viewmodel.configInfo.get().getSettings().getShowTemp() > 0) {
            tip = tip.replace("30", String.valueOf(viewmodel.configInfo.get().getSettings().getShowTemp()));
        }
        UIUtils.spanStr(binding.idTips, tip, clickAryStr, R.color.color_main, false, new CustomClickableSpan.SpanClickListener() {
            @Override
            public void clickPosition(int position) {
                AndPermission.with(mContext)
                        .runtime()
                        .permission(Manifest.permission.CALL_PHONE)
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Utils.callPhone(mContext, UIUtils.getString(R.string.string_kefu_phone));
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                Logger.w("拒绝打电话的权限");
                            }
                        })
                        .start();
            }
        });
    }

    /**
     * 点击设置对话框
     */
    private void initSetDialog() {
        final String[] stringItems = {getString(R.string.string_alarm_set), getString(R.string.string_change_account)};
        ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
        dialog.titleTextSize_SP(14F);
        dialog.cancelText(getString(R.string.string_cancel));
        dialog.show();
        dialog.setOnOperItemClickL((adapterView, view, position, l) -> {
            switch (position) {
                case 0://报警设置
                    setAlarm();
                    break;
                case 1://切换账号
                    changeAccount();
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
    }

    /**
     * 报警时间频率设置
     */
    private void setAlarm() {
        AlarmPickerDialog dialog = new AlarmPickerDialog(ActivityManager.currentActivity());
        dialog.setConfirmListener((min, desc) -> {
            if (min == -1) {
                SpUtils.saveLong(Utils.getHighTempDurationSpKey(), -1);
            } else {
                SpUtils.saveLong(Utils.getHighTempDurationSpKey(), min * 60);
            }
            BlackToast.show("设置成功");
        });
        dialog.show();
    }

    /**
     * 切换账号
     */
    private void changeAccount() {
        WarmDialogHorizental dialogHorizental = new WarmDialogHorizental(ActivityManager.currentActivity());
        dialogHorizental.setTopText(R.string.string_change_account);
        dialogHorizental.setContent(R.string.string_change_account_tip);
        dialogHorizental.setConfirmListener(v -> {
            if (viewmodel.connectStatus.get() == 2) {
                viewmodel.disConnect();
                Utils.clearMeasureViewModel(App.get().getDeviceMac(), viewmodel.measureInfo.get().getProfile().getProfileId());
            }
            SpUtils.saveString(Constants.PHONE, "");
            //重置报警设置
            SpUtils.saveLong(Utils.getHighTempDurationSpKey(), 0);
            SpUtils.saveLong(Utils.getHighTempWarmSpKey(App.get().getPhone()), 0);
            Utils.goToLogin(mContext);
        });
        dialogHorizental.show();
    }

    /**
     * 是否需要显示报警对话框
     *
     * @param currentTemp 当前温度值
     * @return
     */
    protected boolean isNeedWarm(float currentTemp) {
        if (!viewmodel.isConnected()) return false;
        if (hasShowWarnDialog) return false;
        if (currentTemp < viewmodel.configInfo.get().getSettings().getWarmTemp()) return false;
        //上次报警时间
        long lastAlarmTime = SpUtils.getLong(Utils.getHighTempWarmSpKey(App.get().getPhone()), 0);
        long duration = SpUtils.getLong(Utils.getHighTempDurationSpKey(), 0);
        if (duration == -1) return false;
        Logger.w("上次报警时间: ", lastAlarmTime, " 当前时间距上次报警间隔 ：", (System.currentTimeMillis() - lastAlarmTime) / 1000 + " 秒", "  报警interval: ", duration + "秒");
        if ((System.currentTimeMillis() - lastAlarmTime) / 1000 > duration) return true;
        return false;
    }

    /**
     * 设置忽略电量优化
     */
    private void showBackgroundTips() {
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && manager != null) {
            if (!manager.isIgnoringBatteryOptimizations(mContext.getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected MeasureViewModel getViewModel() {
        return Utils.getMeasureViewmodel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
    }


}
