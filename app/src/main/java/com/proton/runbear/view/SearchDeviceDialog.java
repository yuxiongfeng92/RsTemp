package com.proton.runbear.view;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.proton.runbear.R;
import com.proton.runbear.component.App;
import com.proton.runbear.utils.UIUtils;
import com.proton.runbear.utils.Utils;
import com.wms.logger.Logger;
import com.wms.utils.DensityUtils;
import com.yanzhenjie.permission.AndPermission;

import io.reactivex.annotations.NonNull;

/**
 * Created by yuxiongfeng.
 * Date: 2019/9/24
 */
public class SearchDeviceDialog extends Dialog implements SystemDialog {

    private Activity hostActivity;
    private Button btnScan;
    private WaveView waveView;
    private TextView txtSearchTips, txtHelpTips;
    /**
     * 是否正在搜索
     */
    private boolean isSearching = false;

    public SearchDeviceDialog(@NonNull Activity activity) {
        super(activity);
        hostActivity = activity;
        initDialog();
    }

    public void stopWaveView() {
        if (waveView != null) {
            waveView.stop();
        }
    }

    public void startWaveView() {
        if (waveView != null) {
            waveView.start();
        }
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search_device_layout);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            int screenWidth = DensityUtils.getScreenWidth(App.get());
            lp.width = screenWidth;
            lp.dimAmount = 0.3F;
            dialogWindow.setAttributes(lp);
            lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            dialogWindow.setWindowAnimations(R.style.animate_dialog);
            dialogWindow.setBackgroundDrawableResource(R.color.transparent);
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
        waveView.start();
    }


    /**
     * 切换搜索按钮的样式
     *
     * @param isSearching 是否在搜索中
     */
    public void swithSearchStyle(boolean isSearching) {
        swithSearchStyle(isSearching, -1);
    }

    /**
     * 切换搜索按钮的样式
     *
     * @param isSearching    是否在搜索中
     * @param resultFailCode 搜索失败code：  0表示从服务器获取设备信息失败  1表示未搜索到体温贴
     */
    public void swithSearchStyle(boolean isSearching, int resultFailCode) {
        this.isSearching = isSearching;
        if (isSearching) {
            btnScan.setBackgroundResource(R.drawable.icon_start_measure_bg);
            btnScan.setTextColor(ContextCompat.getColor(getContext(), R.color.color_main));
            btnScan.setText("搜索中...");
            txtSearchTips.setText(R.string.string_search_device_tips);
            txtSearchTips.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_33));
            startWaveView();
        } else {
            btnScan.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            btnScan.setBackgroundResource(R.drawable.icon_stop_measure_bg);
            btnScan.setText("重新搜索");
            initTips(resultFailCode);
            stopWaveView();
        }
    }

    private void initView() {
        btnScan = findViewById(R.id.id_scan_device);
        waveView = findViewById(R.id.id_wave);
        txtSearchTips = findViewById(R.id.txt_search_tips);
        txtHelpTips = findViewById(R.id.txt_help_tips);

        btnScan.setOnClickListener(v -> callback.searchCallback(isSearching));

        initHelpTips();
    }

    public void initTips(int resultFailCode) {
        String[] clickAryStr = new String[]{UIUtils.getString(R.string.string_kefu_phone)};
        String str;
        if (resultFailCode == 0) {
            str = UIUtils.getString(R.string.string_search_fail_tip1);
        } else {
            str = UIUtils.getString(R.string.string_search_fail_tip2);
        }
        UIUtils.spanStr(txtSearchTips, str, clickAryStr, R.color.color_search_fail, false, position -> AndPermission.with(getContext())
                .runtime()
                .permission(Manifest.permission.CALL_PHONE)
                .onGranted(data -> Utils.callPhone(getContext(), UIUtils.getString(R.string.string_kefu_phone)))
                .onDenied(data -> Logger.w("拒绝打电话的权限"))
                .start());
    }

    private void initHelpTips() {
        String[] clickAryStr = new String[]{UIUtils.getString(R.string.string_kefu_phone)};
        String str = UIUtils.getString(R.string.string_help_tip);
        UIUtils.spanStr(txtHelpTips, str, clickAryStr, R.color.color_main, false, position -> AndPermission.with(getContext())
                .runtime()
                .permission(Manifest.permission.CALL_PHONE)
                .onGranted(data -> Utils.callPhone(getContext(), UIUtils.getString(R.string.string_kefu_phone)))
                .onDenied(data -> Logger.w("拒绝打电话的权限"))
                .start());

    }

    public void setCallback(BtnSearchClickCallback callback) {
        this.callback = callback;
    }

    BtnSearchClickCallback callback;

    public interface BtnSearchClickCallback {
        void searchCallback(boolean isSearching);
    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}
