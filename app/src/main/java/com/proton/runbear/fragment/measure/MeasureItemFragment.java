package com.proton.runbear.fragment.measure;

import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.proton.runbear.BuildConfig;
import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.FragmentMeasureItemBinding;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.MeasureCenter;
import com.proton.runbear.socailauth.PlatformType;
import com.proton.runbear.socailauth.SocialApi;
import com.proton.runbear.socailauth.listener.ShareListener;
import com.proton.runbear.socailauth.share_media.IShareMedia;
import com.proton.runbear.socailauth.share_media.ShareWebMedia;
import com.proton.runbear.utils.AlarmClearTimer;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.HttpUrls;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.MeasureStatusView;
import com.proton.runbear.view.RsWarmDialog;
import com.proton.runbear.view.SearchDeviceDialog;
import com.proton.runbear.view.WarmDialog;
import com.proton.runbear.viewmodel.measure.MeasureViewModel;
import com.wms.logger.Logger;
import com.wms.utils.PreferenceUtils;


import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.Type;

/**
 * Created by wangmengsi on 2018/2/28.
 */

public class MeasureItemFragment extends BaseMeasureFragment<FragmentMeasureItemBinding, MeasureViewModel> {

    private OnMeasureItemListener onMeasureItemListener;

    private SearchDeviceDialog mSearchDeviceDialog;
    protected RsWarmDialog mHighestWarmDialog;

    /**
     * 微信分享，登录工具类
     */
    private SocialApi mSocialApi;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    /**
     * 是否能开始进行报警，首次进入的时候需要先延时2秒，因为可能因为测量准备界面调用onDestoryed()方法导致报警关掉
     */
    private boolean isCanVibrateAndSound = false;
    /**
     * 是否已经显示报警对话框
     */
    private boolean hasShowWarnDialog;

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
            public void onPropertyChanged(Observable observable, int i) {
                updateView();
            }
        });
        updateView();
        showBackgroundTips();
        mSocialApi = SocialApi.get(mContext);
        mMainHandler.postDelayed(() -> isCanVibrateAndSound = true, 2000);

    }

    private void showBackgroundTips() {
        if (App.get().hasShowBackgroundTip
                || SpUtils.getBoolean("hasShowBackgroundTip", false)
                || BuildConfig.IS_INTERNAL) {
            return;
        }
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && manager != null) {
            if (!manager.isIgnoringBatteryOptimizations(mContext.getPackageName())) {
                SweetAlertDialog dialog = new SweetAlertDialog(mContext, Type.NORMAL_TYPE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.showCancelButton(true)
                        .setTitleText(getString(R.string.string_warm_tips))
                        .setCancelText(getString(R.string.string_remind_next_time))
                        .setConfirmText(getString(R.string.string_view_help))
                        .setContentText(getString(R.string.string_background_tips))
                        .setConfirmClickListener(alertDialog -> {
                            alertDialog.dismiss();
                            IntentUtils.goToWeb(mContext, HttpUrls.URL_ATTENTION);
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                            try {
                                startActivity(intent);
                            } finally {
                                SpUtils.saveBoolean("hasShowBackgroundTip", true);
                            }
                        })
                        .setCancelClickListener(alertDialog -> {
                            App.get().hasShowBackgroundTip = true;
                            alertDialog.dismiss();
                        })
                        .show();
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idClose.setOnClickListener(v -> closeCard());
    }

    /**
     * 重新测量
     */
    private void reMeasure() {
        if (onMeasureItemListener != null) {
            Utils.clearMeasureViewModel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
            onMeasureItemListener.remeasure(mMeasureInfo);
        }
    }

    private void shareWeChat() {
        showDialog();
        MeasureCenter.getShareWechatUrl(mMeasureInfo.getProfile().getProfileId(), new NetCallBack<String>() {

            @Override
            public void noNet() {
                BlackToast.show(R.string.string_please_check_your_network);
                dismissDialog();
            }

            @Override
            public void onSucceed(String data) {

                IShareMedia shareMedia = new ShareWebMedia();
                ((ShareWebMedia) shareMedia).setWebPageUrl(data);
                ((ShareWebMedia) shareMedia).setDescription(getResString(R.string.string_invite_to_checkMeasureReport) + mMeasureInfo.getProfile().getRealname() + getResString(R.string.string_one_temp) + "\n此链接24小时内有效");
                ((ShareWebMedia) shareMedia).setTitle(getResString(R.string.stirng_carePatch_shrare));
                mSocialApi.doShare(getActivity(), PlatformType.WEIXIN, shareMedia, new ShareListener() {
                    @Override
                    public void onComplete(PlatformType platform_type) {
                        Logger.w("微信分享onComplete");
//                        BlackToast.show(R.string.string_share_success);
                    }

                    @Override
                    public void onError(PlatformType platform_type, String err_msg) {
                        Logger.w("微信分享onError", err_msg);
                        BlackToast.show(err_msg);

                    }

                    @Override
                    public void onCancel(PlatformType platform_type) {
                        Logger.w("微信分享onCancel");
                        BlackToast.show(R.string.string_share_cancel);
                    }
                });

                dismissDialog();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                BlackToast.show(resultPair.getData());
                dismissDialog();
            }
        });
    }

    @Override
    protected MeasureViewModel getViewModel() {
        return Utils.getMeasureViewmodel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
    }

    public void setOnMeasureItemListener(OnMeasureItemListener onMeasureItemListener) {
        this.onMeasureItemListener = onMeasureItemListener;
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
    }

    @Override
    protected void doCardClose() {
        super.doCardClose();
        if (onMeasureItemListener != null) {
            onMeasureItemListener.closeCard(this);
        }
    }

    public interface OnMeasureItemListener {
        /**
         * 停止当前测量
         */
        void closeCard(MeasureItemFragment fragment);

        /**
         * 重新测量
         */
        void remeasure(MeasureBean measureBean);
    }


    /**
     * 高温报警
     *
     * @param temp
     */
    private void doWarnOperator(float temp) {
        if (isNeedWarn(temp)) {
            showWarmDialog();
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
    private void showWarmDialog() {
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

}
