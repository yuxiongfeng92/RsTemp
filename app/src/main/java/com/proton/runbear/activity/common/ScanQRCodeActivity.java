package com.proton.runbear.activity.common;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.ActivityScanQrcodeBinding;
import com.proton.runbear.enums.QRPatchType;
import com.proton.runbear.net.bean.BindDeviceInfo;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.DeviceCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.Constants;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.SpUtils;
import com.proton.runbear.utils.UrlParse;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangmengsi on 2018/2/26.
 */
public class ScanQRCodeActivity extends BaseActivity<ActivityScanQrcodeBinding> {
    private ProfileBean mProfile;
    private CaptureFragment captureFragment;
    private String deviceType;
    private String macaddress;


    @Override
    protected int inflateContentView() {
        return R.layout.activity_scan_qrcode;
    }

    @Override
    protected void init() {
        super.init();
        mProfile = (ProfileBean) getIntent().getSerializableExtra("profile");
        if (mProfile != null) {
            App.get().getHasScanQRCode().add(mProfile.getProfileId());
        }
        showScanFragment();
    }

    private void showScanFragment() {
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_camera, false);
        captureFragment.setAnalyzeCallback(new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                Logger.w("二维码扫描结果:" + result);
                parseQRCode(result);
            }

            @Override
            public void onAnalyzeFailed() {
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.id_qrcode_container, captureFragment).commit();
    }

    /**
     * 二维码格式
     * http://www.protontek.com/device/temp?type=P03&macId=0C:61:CF:C7:E7:0E&sn=18051601003
     */
    private void parseQRCode(String result) {
        if (!NetUtils.isConnected(mContext)) {
            reScan();
            return;
        }
        if (TextUtils.isEmpty(result)) {
            BlackToast.show(R.string.string_qrcode_invalid);
            reScan();
            return;
        }

        String prefix = "http://www.protontek.com/device/temp";
        if (!result.startsWith(prefix)) {
            BlackToast.show(R.string.string_qrcode_invalid);
            reScan();
            return;
        }

        //解析设备类型和mac地址
        Map<String, String> params = UrlParse.getUrlParams(result);
        if (params.containsKey("type")
                && params.containsKey("macId")
                && params.containsKey("sn")) {
            macaddress = params.get("macId");
            deviceType = params.get("type");
            //测试mac
//            macaddress = "00:81:F9:10:54:0C";

            /**
             * 只扫描润生的体温贴
             */
            QRPatchType qrPatchType = QRPatchType.getQRPatchType(deviceType);
            Logger.w("扫描到的体温贴类型是：", qrPatchType.getType(), "  ", qrPatchType.getDes());
            if (qrPatchType == QRPatchType.P07) {
                bindDevice();
                return;
            } else {
                BlackToast.show(R.string.string_cannot_recongnize);
                reScan();
                return;
            }
        } else {
            BlackToast.show(R.string.string_qrcode_invalid);
            reScan();
        }
    }

    private void reScan() {
        binding.getRoot().postDelayed(() -> {
            if (captureFragment != null) {
                captureFragment.restartPreviewAndDecode();
            }
        }, 2000);
    }
    /**
     * 绑定设备
     */
    private void bindDevice() {
        showDialog();
        if (mProfile == null || mProfile.getProfileId() == -1) {
            dismissDialog();
            IntentUtils.goToMain(mContext);
            return;
        }
        String serverMac = macaddress.replaceAll(":", "");
        DeviceCenter.bindDevice(serverMac, new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
                reScan();
            }

            @Override
            public void onSucceed(Boolean data) {
                Logger.w("设备绑定成功");
                mProfile.setMacAddress(macaddress);
                SpUtils.saveString(Constants.BIND_MAC, macaddress);
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
                finish();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
                dismissDialog();
                reScan();
            }
        });
    }

    /**
     * 获取设备详情
     */
//    private void queryDeviceDetailInfo() {
//        DeviceCenter.queryBindDeviceInfo(App.get().getServerMac(), new NetCallBack<BindDeviceInfo>() {
//            @Override
//            public void noNet() {
//                super.noNet();
//                dismissDialog();
//                BlackToast.show(R.string.string_no_net);
//                reScan();
//            }
//
//            @Override
//            public void onSucceed(BindDeviceInfo info) {
//                dismissDialog();
//                Logger.w("获取绑定设备信息:",info.toString());
//                if (TextUtils.isEmpty(info.getExamid())) {
//                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
//                    finish();
//                    return;
//                }
//
//                MeasureCenter.measureEnd(info.getExamid(), new NetCallBack<MeasureEndResp>() {
//                    @Override
//                    public void noNet() {
//                        super.noNet();
//                        reScan();
//                    }
//
//                    @Override
//                    public void onSucceed(MeasureEndResp data) {
//                        mProfile.setExamid(null);
//                        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
//                        finish();
//                    }
//
//                    @Override
//                    public void onFailed(ResultPair resultPair) {
//                        super.onFailed(resultPair);
//                        reScan();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailed(ResultPair resultPair) {
//                super.onFailed(resultPair);
//                dismissDialog();
//                BlackToast.show(resultPair.getData());
//                reScan();
//            }
//        });
//    }
    @Override
    protected void initView() {
        super.initView();
        binding.idOpenLight.setOnClickListener(v -> {
            try {
                CodeUtils.isLightEnable(!binding.getIsLightOn());
                binding.setIsLightOn(!binding.getIsLightOn());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

//        binding.idNoQrcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        binding.idNoQrcode.getPaint().setAntiAlias(true);
//        binding.idNoQrcode.setOnClickListener(v -> {
//            if (ActivityManager.hasActivity(HomeActivity.class)) {
//                if (go2ScanDevice) {
//                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
//                } else {
//                    unbind();
//                }
//            } else {
//                IntentUtils.goToMain(mContext);
//            }
//            finish();
//        });
    }

//    private void unbind() {
//        DeviceCenter.unbind(mProfile.getProfileId(), new NetCallBack<Boolean>() {
//            @Override
//            public void onSucceed(Boolean data) {
//                if (data) {
//                    mProfile.setMacAddress("");
//                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.UNBIND_DEVICE_SUCCESS, mProfile));
//                }
//            }
//        });
//    }

    @Override
    protected void onDestroy() {
//        if (mProfile != null) {
//            if (go2ScanDevice) {
//                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
//            }
//        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.setIsLightOn(false);
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected boolean showBackBtn() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_scan_temp);
    }


}
