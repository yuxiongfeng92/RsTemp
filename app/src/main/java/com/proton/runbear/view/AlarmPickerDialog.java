package com.proton.runbear.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.proton.runbear.R;
import com.wms.logger.Logger;
import com.wms.utils.DensityUtils;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuxiongfeng.
 * Date: 2019/9/25
 */
public class AlarmPickerDialog extends Dialog implements SystemDialog {

    private Activity hostActivity;
    private TextView mConfirmText, mCancelText;
    private WheelView<String> wheelView;
    private int selectMin = 30;
    private String selectDesc = "";
    private List<String> items = new ArrayList<>();


    public AlarmPickerDialog(@NonNull Activity activity) {
        super(activity);
        this.hostActivity = activity;
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alarm_picker_layout);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            int screenWidth = DensityUtils.getScreenWidth(hostActivity);
            lp.width = screenWidth;
            lp.dimAmount = 0.3F;
            dialogWindow.setAttributes(lp);
            lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            dialogWindow.setWindowAnimations(R.style.animate_dialog);
            dialogWindow.setBackgroundDrawableResource(R.color.transparent);
        }
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initView();
    }

    private void initView() {
        mConfirmText = findViewById(R.id.btn_ok);
        mCancelText = findViewById(R.id.btn_cancel);
        wheelView = findViewById(R.id.wheelView);
        setCancelListener(null);
        initData();

        wheelView.setOnItemSelectedListener((wheelView, data, position) -> {
            Logger.w("wheleview select postion  : ", position, " data : ", data);
            if (position < 4) {
                selectMin = (position + 1) * 30;
            } else {
                selectMin = -1;
            }
            selectDesc = items.get(position);
        });
    }

    public AlarmPickerDialog setConfirmListener(AlarmClickCallback listener) {
        mConfirmText.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.alarmPicCallback(selectMin, selectDesc);
            }
        });
        return this;
    }

    public AlarmPickerDialog setCancelListener(View.OnClickListener listener) {
        mCancelText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            dismiss();
        });
        return this;
    }

    private void initData() {
        if (items != null) {
            items.clear();
        }
        items.add("半小时一次");
        items.add("一小时一次");
        items.add("一个半小时一次");
        items.add("两小时一次");
        items.add("两个半小时一次");
        items.add("三小时一次");
        items.add("三个半小时一次");
        items.add("四小时一次");
        items.add("从不");
        wheelView.setData(items);
        wheelView.setSelectedItemPosition(0);
        selectMin = 30;
        selectDesc = items.get(0);
        wheelView.setTextSize(20f, true);
    }


    public interface AlarmClickCallback {
        void alarmPicCallback(int min, String desc);
    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}
