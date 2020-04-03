package com.proton.runbear.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.proton.runbear.R;

public class WarmDialogVertical extends Dialog implements SystemDialog {

    private TextView mTopText;
    private TextView mConfirmText;
    private TextView mCancelText;
    private TextView mContent;
    private Activity hostActivity;

    public WarmDialogVertical(@NonNull Activity activity) {
        super(activity);
        hostActivity = activity;
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_warm_vertical_dialog);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getContext().getResources().getDisplayMetrics();
            lp.width = (int) (d.widthPixels * 0.8);
            lp.dimAmount = 0.3F;
            dialogWindow.setAttributes(lp);
            lp.gravity = Gravity.CENTER;
            dialogWindow.setWindowAnimations(R.style.style_warm_animation);
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        initView();
    }

    private void initView() {
        mTopText = findViewById(R.id.id_top_text);
        mConfirmText = findViewById(R.id.id_confirm_text);
        mCancelText = findViewById(R.id.id_cancel_text);
        mContent = findViewById(R.id.id_content);
        setConfirmListener(null);
        setCancelListener(null);
    }

    public WarmDialogVertical setTopColor(@ColorInt int color) {
        mTopText.setBackgroundColor(color);
        return this;
    }

    public WarmDialogVertical setTopText(int text) {
        mTopText.setText(text);
        return this;
    }

    public WarmDialogVertical setConfirmText(int text) {
        mConfirmText.setText(text);
        return this;
    }

    public WarmDialogVertical setCancelText(int text) {
        mCancelText.setText(text);
        return this;
    }

    public WarmDialogVertical setContent(int text) {
        mContent.setText(text);
        return this;
    }

    public WarmDialogVertical setContent(String text) {
        mContent.setText(text);
        return this;
    }

    public WarmDialogVertical setContentColor(int color) {
        mContent.setTextColor(color);
        return this;
    }

    public WarmDialogVertical setConfirmText(String text) {
        mConfirmText.setText(text);
        return this;
    }

    public WarmDialogVertical setCancelText(String text) {
        mCancelText.setText(text);
        return this;
    }


    public WarmDialogVertical hideConfirmBtn() {
        mConfirmText.setVisibility(View.GONE);
        return this;
    }

    public WarmDialogVertical hideCancelBtn() {
        mCancelText.setVisibility(View.GONE);
        return this;
    }

    public WarmDialogVertical setConfirmListener(View.OnClickListener listener) {
        mConfirmText.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public WarmDialogVertical setCancelListener(View.OnClickListener listener) {
        mCancelText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            dismiss();
        });
        return this;
    }

    public WarmDialogVertical setConfirmTextColor(int color) {
        mConfirmText.setTextColor(color);
        return this;
    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}
