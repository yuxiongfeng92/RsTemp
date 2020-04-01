package com.proton.runbear.view.chartview;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.proton.runbear.constant.AppConfigs;
import com.proton.runbear.utils.SpUtils;

import java.text.DecimalFormat;

public class MyAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT) == AppConfigs.SP_VALUE_TEMP_F) {
            return mFormat.format(value) + "℉";
        } else {
            return mFormat.format(value) + "℃";
        }
    }
}
