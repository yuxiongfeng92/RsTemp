package com.proton.runbear.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proton.runbear.R;
import com.proton.runbear.utils.UIUtils;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by MoonlightSW on 2017/1/23.
 */

public class TabCheckedChangeView extends LinearLayout {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    public long lastClickTime = 0;
    private TextView textView1;
    private TextView textView2;
    private onSegmentViewClickListener listener;

    public TabCheckedChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabCheckedChangeView(Context context) {
        super(context);
        init();
    }

    private static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void init() {
        textView1 = new TextView(getContext());
        textView2 = new TextView(getContext());
        textView1.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        textView2.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        textView1.setText("SEG1");
        textView2.setText("SEG2");
        @SuppressWarnings("ResourceType")
        XmlPullParser xrp = getResources().getXml(R.drawable.selector_top_tab_txt);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);
            textView1.setTextColor(csl);
            textView2.setTextColor(csl);
        } catch (Exception e) {
        }
        textView1.setGravity(Gravity.CENTER);
        textView2.setGravity(Gravity.CENTER);
        int dp3 = (int) (UIUtils.dp2px(3));
        int dp6 = (int) (UIUtils.dp2px(6));
        textView1.setPadding(dp3, dp6, dp3, dp6);
        textView2.setPadding(dp3, dp6, dp3, dp6);
        setSegmentTextSize(14);
        textView1.setBackgroundResource(R.drawable.selector_tab_left_bg);
        textView2.setBackgroundResource(R.drawable.selector_tab_right_bg);
        textView1.setSelected(true);
        this.removeAllViews();
        this.addView(textView1);
        this.addView(textView2);
        this.invalidate();

        textView1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView1.isSelected()) {
                    return;
                }
                textView1.setSelected(true);
                textView2.setSelected(false);
                if (listener != null) {
                    listener.onSegmentViewClick(textView1, 0);
                }
            }
        });
        textView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView2.isSelected()) {
                    return;
                }
                textView2.setSelected(true);
                textView1.setSelected(false);
                if (listener != null) {
                    listener.onSegmentViewClick(textView2, 1);
                }
            }
        });
    }

    /**
     * 设置字体大小 单位dip
     * <p>2014年7月18日</p>
     */
    public void setSegmentTextSize(int dp) {
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    public void setOnSegmentViewClickListener(onSegmentViewClickListener listener) {
        this.listener = listener;
    }


    /**
     * 设置文字
     * <p>2014年7月18日</p>
     */
    public void setSegmentText(CharSequence text, int position) {
        if (position == 0) {
            textView1.setText(text);
        }
        if (position == 1) {
            textView2.setText(text);
        }
    }

    public void setTextView1Selected(boolean isSelected) {
        textView1.setSelected(isSelected);
    }

    public void setTextView2Selected(boolean isSelected) {
        textView2.setSelected(isSelected);
    }

    public interface onSegmentViewClickListener {
        /**
         * <p>2014年7月18日</p>
         *
         * @param position 0-左边 1-右边
         */
        void onSegmentViewClick(View v, int position);
    }
}
