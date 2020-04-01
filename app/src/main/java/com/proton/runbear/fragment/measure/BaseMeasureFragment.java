package com.proton.runbear.fragment.measure;

import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.component.App;
import com.proton.runbear.constant.AppConfigs;
import com.proton.runbear.database.ProfileManager;
import com.proton.runbear.fragment.base.BaseViewModelFragment;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.AlarmClearTimer;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.Settings;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.DisconnectDialog;
import com.proton.runbear.enums.InstructionConstant;
import com.proton.runbear.view.RsWarmDialog;
import com.proton.runbear.view.WarmDialog;
import com.proton.runbear.viewmodel.measure.MeasureViewModel;
import com.wms.logger.Logger;
import com.wms.utils.PreferenceUtils;

/**
 * Created by wangmengsi on 2018/3/28.
 */

public abstract class BaseMeasureFragment<DB extends ViewDataBinding, VM extends MeasureViewModel> extends BaseViewModelFragment<DB, VM> {

    protected MeasureBean mMeasureInfo;
    /**
     * 是否需要关闭卡片
     */
    protected boolean isNeedCloseCard;
    /**
     * 上次显示的温度
     */
    private RsWarmDialog mHighestWarmDialog;
    private WarmDialog mBatteryLowDialog;
    private DisconnectDialog mDisconnectDialog;
    private WarmDialog mEndMeasureDialog;
    private WarmDialog mChargeDialog;

    /**
     * 是否是游客模式
     */
    private boolean isVisitorMode;
    private TextView mCurrentTempTextView, mCurrentTempUnit;

    /**
     * 是否已经显示报警对话框
     */
    private boolean hasShowWarnDialog;

    private Observable.OnPropertyChangedCallback mBatteryCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            showBatteryWarmDialog(viewmodel.battery.get());
        }
    };

    private Observable.OnPropertyChangedCallback mConnectStatusCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (viewmodel.isDisconnect()) {
                showDisconnectDialog();
            } else if (viewmodel.isConnected()) {
                if (mDisconnectDialog != null) {
                    mDisconnectDialog.dismiss();
                }
            }
        }
    };

    private Observable.OnPropertyChangedCallback mSaveReportCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (isHidden()) return;
            if (isNeedCloseCard) {
                doCardClose();
            }
        }
    };

    private Observable.OnPropertyChangedCallback mChargeCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (viewmodel.isCharge.get()) {
                showChargeDialog();
            } else {
                if (mChargeDialog != null && mChargeDialog.isShowing()) {
                    mChargeDialog.dismiss();
                }
            }
        }
    };

    private Observable.OnPropertyChangedCallback mTempChangeCallBack=new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            doWarnOperator(viewmodel.currentTemp.get());
        }
    };

    @Override
    protected void fragmentInit() {
        mMeasureInfo = (MeasureBean) getArguments().getSerializable("measureInfo");
        if (mMeasureInfo == null) return;
        if (mMeasureInfo.getProfile().getProfileId() == -1) {
            isVisitorMode = true;
        }
        setViewModel();
    }

    @Override
    protected void initView() {
        super.initView();
        mCurrentTempTextView = binding.getRoot().findViewById(R.id.id_current_temp);
        mCurrentTempUnit = binding.getRoot().findViewById(R.id.id_current_temp_unit);

        if (mCurrentTempTextView != null) {
            if (App.get().getInstructionConstant() == InstructionConstant.aa) {
                mCurrentTempTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/demo.ttf"));
            } else {
                mCurrentTempTextView.setTypeface(null);
            }
        }
    }


    /**
     * 关闭卡片
     */
    protected void closeCard() {
        isNeedCloseCard = true;
        if (mEndMeasureDialog == null) {
            mEndMeasureDialog = new WarmDialog(getHostActivity())
                    .setTopText(R.string.string_close_card)
                    .setConfirmText(R.string.string_confirm)
                    .setConfirmListener(v -> saveReport());
        }

        mEndMeasureDialog.setContent((viewmodel.isConnected() ? R.string.string_end_measure_tips : R.string.string_ensure_close_card));

        if (viewmodel.isConnected()) {
            mEndMeasureDialog.setConfirmText(R.string.string_end_measure);
            mEndMeasureDialog.setConfirmTextColor(Color.parseColor("#ef6a58"));
        } else {
            mEndMeasureDialog.setConfirmText(R.string.string_confirm);
        }
        if (!mEndMeasureDialog.isShowing()) {
            mEndMeasureDialog.show();
        }
    }

    /**
     * 仅仅关闭卡片不保存数据
     */
    protected void closeCardOnly() {
        viewmodel.disConnect();
        doCardClose();
    }


    protected void setViewModel() {
        viewmodel = getViewModel();
        viewmodel.battery.addOnPropertyChangedCallback(mBatteryCallback);
        viewmodel.connectStatus.addOnPropertyChangedCallback(mConnectStatusCallback);
        viewmodel.isCharge.addOnPropertyChangedCallback(mChargeCallback);
        viewmodel.currentTemp.addOnPropertyChangedCallback(mTempChangeCallBack);
        viewmodel.measureInfo.set(mMeasureInfo);
    }

    /**
     * 保存报告
     */
    protected void saveReport() {
        if (!App.get().isLogined() && viewmodel.isConnected()) {
            //弹出未登录对话框
            new WarmDialog(getHostActivity())
                    .setTopText(R.string.string_end_measure)
                    .setContent(R.string.string_not_login_can_not_save_report)
                    .showFirstBtn()
                    .setFirstBtnListener(v -> IntentUtils.goToLogin(mContext))
                    .setConfirmText(getString(R.string.string_not_save))
                    .setConfirmListener(v -> {
                        viewmodel.doSaveReportFail();
                        viewmodel.disConnect();
                    }).show();
            return;
        }

        if (viewmodel.isManualDisconnect()) {
            //手动断开连接状态，则直接关闭
            viewmodel.disConnect();
            viewmodel.saveReport.notifyChange();
            return;
        }
        viewmodel.saveReport();
    }


    /**
     * 显示断开连接对话框
     */
    protected void showDisconnectDialog() {
        if (!SpUtils.getBoolean(AppConfigs.getSpKeyConnectInterrupt(), true)) {
            return;
        }
        if (Utils.needRecreateDialog(mDisconnectDialog)) {
            mDisconnectDialog = new DisconnectDialog(ActivityManager.currentActivity(), viewmodel.patchMacAddress.get());
        }
        if (!mDisconnectDialog.isShowing()) {
            //震动
            Utils.vibrateAndSound();
            mDisconnectDialog.show();
        }

    }

    /**
     * 显示电量警告
     */
    protected void showBatteryWarmDialog(int battery) {
        if (isHidden()) return;
        boolean hasOpenLowerWarm = SpUtils.getBoolean(AppConfigs.getSpKeyLowPower(), true);
        long lowPowerDuration = SpUtils.getLong(Utils.getLowPowerSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
        if (!hasOpenLowerWarm || lowPowerDuration == -1) return;
        if (battery <= Settings.MIN_BATTERY) {
            if (Utils.needRecreateDialog(mBatteryLowDialog)) {
                mBatteryLowDialog = new WarmDialog(ActivityManager.currentActivity())
                        .setTopText(R.string.string_battery_low)
                        .setContent(R.string.string_battery_low_tips)
                        .setConfirmText(R.string.string_i_konw)
                        .hideCancelBtn()
                        .setConfirmListener(v -> {
                            //-1代表不再提醒
                            Utils.cancelVibrateAndSound();
                            SpUtils.saveLong(Utils.getLowPowerSharedPreferencesKey(mMeasureInfo.getMacaddress()), -1);
                        });
            }
            if (!mBatteryLowDialog.isShowing()) {
                //震动
                Utils.vibrateAndSound();
                mBatteryLowDialog.show();
            }
        } else {
            if (mBatteryLowDialog != null && mBatteryLowDialog.isShowing()) {
                Utils.cancelVibrateAndSound();
                mBatteryLowDialog.dismiss();
            }
        }
    }

    /**
     * 显示充电对话框
     */
    protected void showChargeDialog() {
        if (mChargeDialog == null) {
            mChargeDialog = new WarmDialog(getHostActivity())
                    .setTopText(R.string.string_close_card)
                    .setContent(R.string.string_is_charge_end_measure)
                    .setConfirmText(R.string.string_confirm)
                    .setConfirmListener(v -> {
                        saveReport();
                    });
        }

        mChargeDialog.setConfirmText(R.string.string_end_measure);
        mChargeDialog.setConfirmTextColor(Color.parseColor("#ef6a58"));
        mChargeDialog.show();
    }

    /**
     * 高温报警
     *
     * @param temp
     */
    private void doWarnOperator(float temp) {
        if (isNeedWarn(temp)) {
            showWarnDialog();
        } else {
            if (mHighestWarmDialog != null) {
                mHighestWarmDialog.dismiss();
            }
            hasShowWarnDialog = false;
            Utils.cancelVibrateAndSound();
        }
    }


    /**
     * 显示报警对话框
     */
    private void showWarnDialog() {
        hasShowWarnDialog = true;
        if (mHighestWarmDialog == null) {
            mHighestWarmDialog = new RsWarmDialog(getActivity())
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
            PreferenceUtils.setSettingLong(App.get(), Utils.getHighTempWarmSpKey(App.get().getPhone()), System.currentTimeMillis());

        });
        mHighestWarmDialog.setContent(String.format("宝宝的体温已经%s了！", Utils.getTempAndUnit(viewmodel.currentTemp.get())));
        if (!mHighestWarmDialog.isShowing()) {
            Utils.vibrateAndSound();
            mHighestWarmDialog.show();
        }
    }


    /**
     * 是否需要显示报警对话框
     *
     * @param currentTemp 当前温度值
     * @return
     */
    protected boolean isNeedWarn(float currentTemp) {
        if (!viewmodel.isConnected()) return false;
        if (hasShowWarnDialog) return false;
        if (currentTemp < viewmodel.configInfo.get().getSettings().getWarmTemp()) return false;
        //上次报警时间
        long lastAlarmTime = PreferenceUtils.getPrefLong(App.get(), Utils.getHighTempWarmSpKey(App.get().getPhone()), 0);
        long duration = PreferenceUtils.getPrefLong(App.get(), Utils.getHighTempDurationSpKey(), 0);
        if (duration == -1) return false;
        Logger.w("上次报警时间: ", lastAlarmTime, " 当前时间距上次报警间隔 ：", (System.currentTimeMillis() - lastAlarmTime) / 1000 + " 秒", "  报警interval: ", duration + "秒");
        if ((System.currentTimeMillis() - lastAlarmTime) / 1000 > duration) return true;
        return false;
    }


    /**
     * 取消所有提醒对话框
     */
    protected void dismissAllDialog() {
        if (mBatteryLowDialog != null && mBatteryLowDialog.isShowing()) {
            mBatteryLowDialog.dismiss();
        }
        if (mEndMeasureDialog != null && mEndMeasureDialog.isShowing()) {
            mEndMeasureDialog.dismiss();
        }
        if (mDisconnectDialog != null && mDisconnectDialog.isShowing()) {
            mDisconnectDialog.dismiss();
        }
    }

    protected void updateView() {
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.PROFILE_CHANGE) {
            //更改档案信息，重新查询当前档案
            ProfileBean profile = ProfileManager.getById(mMeasureInfo.getProfile().getProfileId());
            if (profile != null) {
                viewmodel.measureInfo.get().setProfile(profile);
                viewmodel.measureInfo.notifyChange();
            }
        } else if (event.getEventType() == MessageEvent.EventType.INPUT_INSTRUCTION) {
            String msg = event.getMsg();
            InstructionConstant instructionConstant = InstructionConstant.getInstructionConstant(msg);
            switch (instructionConstant) {
                case ab:
                    App.get().setInstructionConstant(InstructionConstant.ab);
                    break;
                case aa:
                    App.get().setInstructionConstant(InstructionConstant.aa);
                    break;
                case bb:
                    App.get().setInstructionConstant(InstructionConstant.bb);
                    break;
            }

            runOnUiThread(() -> {
                if (mCurrentTempTextView != null) {
                    if (App.get().getInstructionConstant() == InstructionConstant.aa) {
                        boolean tempUnitVisibility = Utils.getTempUnitVisibility(viewmodel.connectStatus.get(), viewmodel.needShowPreheating.get(), false);
                        if (tempUnitVisibility) mCurrentTempUnit.setVisibility(View.VISIBLE);
                        mCurrentTempTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/demo.ttf"));
                    } else {
                        mCurrentTempTextView.setTypeface(null);
                        mCurrentTempUnit.setVisibility(View.GONE);
                    }
                }
            });
            viewmodel.currentTemp.notifyChange();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewmodel != null) {
            viewmodel.battery.removeOnPropertyChangedCallback(mBatteryCallback);
            viewmodel.connectStatus.removeOnPropertyChangedCallback(mConnectStatusCallback);
            viewmodel.isCharge.removeOnPropertyChangedCallback(mChargeCallback);
            viewmodel.currentTemp.removeOnPropertyChangedCallback(mTempChangeCallBack);
        }
        dismissAllDialog();
    }

    private FragmentActivity getHostActivity() {
        return ActivityManager.findActivity(Settings.MEASURE_ACTIVITY);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.cancelVibrateAndSound();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isHighestWarmDialogShowing = mHighestWarmDialog != null && mHighestWarmDialog.isShowing();
        boolean isBatteryWarmDialogShowing = mBatteryLowDialog != null && mBatteryLowDialog.isShowing();
        if (isHighestWarmDialogShowing
//                || isLowestWarmDialogShowing
                || isBatteryWarmDialogShowing) {
            Utils.vibrateAndSound();
        }
    }

    /**
     * 关闭卡片
     */
    protected void doCardClose() {
        long profileId = mMeasureInfo.getProfile().getProfileId();
        if (isVisitorMode) {
            profileId = -1;
        }
        Utils.clearMeasureViewModel(mMeasureInfo.getMacaddress(), profileId);
    }


}
