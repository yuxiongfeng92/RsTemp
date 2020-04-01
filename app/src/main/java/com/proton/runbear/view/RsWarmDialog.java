package com.proton.runbear.view;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.proton.runbear.R;

import io.reactivex.annotations.NonNull;

/**
 * Created by wangmengsi on 2018/04/18.
 */
public class RsWarmDialog extends Dialog implements SystemDialog {

    private TextView mTopText;
    private TextView mConfirmText;
    private TextView mCancelText;
    private TextView mFirstBtn;
    private TextView mContent;
    private Activity hostActivity;

    public RsWarmDialog(@NonNull Activity activity) {
        super(activity);
        hostActivity = activity;
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_rs_warm_dialog);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getContext().getResources().getDisplayMetrics();
            lp.width = (int) (d.widthPixels * 0.8);
            lp.dimAmount = 0.3F;
            dialogWindow.setAttributes(lp);
            lp.gravity = Gravity.CENTER;
            dialogWindow.setBackgroundDrawableResource(R.color.transparent);
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
        mFirstBtn = findViewById(R.id.id_first_btn);
        setConfirmListener(null);
        setCancelListener(null);
    }

    public RsWarmDialog setTopBackGround(int drawable) {
        mTopText.setBackground(hostActivity.getResources().getDrawable(drawable));
        return this;
    }

    public RsWarmDialog setTopText(int text) {
        mTopText.setText(text);
        return this;
    }

    public RsWarmDialog setTopText(String text) {
        mTopText.setText(text);
        return this;
    }

    public RsWarmDialog setConfirmText(int text) {
        mConfirmText.setText(text);
        return this;
    }

    public RsWarmDialog setCancelText(int text) {
        mCancelText.setText(text);
        return this;
    }

    public RsWarmDialog setContent(int text) {
        mContent.setText(text);
        return this;
    }

    public RsWarmDialog setContent(String text) {
        mContent.setText(text);
        return this;
    }

    public RsWarmDialog setConfirmText(String text) {
        mConfirmText.setText(text);
        return this;
    }

    public RsWarmDialog setCancelText(String text) {
        mCancelText.setText(text);
        return this;
    }

    public RsWarmDialog setFirstBtnText(String text) {
        mFirstBtn.setText(text);
        return this;
    }

    public RsWarmDialog hideConfirmBtn() {
        mConfirmText.setVisibility(View.GONE);
        return this;
    }

    public RsWarmDialog hideCancelBtn() {
        mCancelText.setVisibility(View.GONE);
        findViewById(R.id.id_second_divider).setVisibility(View.GONE);
        return this;
    }

    public RsWarmDialog showFirstBtn() {
        mFirstBtn.setVisibility(View.VISIBLE);
        findViewById(R.id.id_first_divider).setVisibility(View.VISIBLE);
        return this;
    }

    public RsWarmDialog setFirstBtnListener(View.OnClickListener listener) {
        mFirstBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            dismiss();
        });
        return this;
    }

    public RsWarmDialog setConfirmListener(View.OnClickListener listener) {
        mConfirmText.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public RsWarmDialog setCancelListener(View.OnClickListener listener) {
        mCancelText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            dismiss();
        });
        return this;
    }

    public RsWarmDialog setConfirmTextColor(int color) {
        mConfirmText.setTextColor(color);
        return this;
    }

//    @Override
//    public void onBackPressed() {
//        dismiss();
//    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}
