package com.proton.runbear.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.proton.runbear.R;
import com.proton.runbear.enums.InstructionConstant;
import com.proton.runbear.utils.Utils;
import com.proton.temp.connector.bean.TempDataBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmengsi on 2018/3/28.
 * 实时温度曲线
 */

public class TempCurveView2 extends FrameLayout {
    private List<Entry> entries = new ArrayList<>();
    private LineChart mLineChart;
    private LineDataSet mDataSet;
    private LineData mLineData;
    /**
     * 最大温度
     */
    private float mHighestTemp = Utils.getTemp(37.50f);
    /**
     * 最低温度
     */
    private float mLowestTemp = Utils.getTemp(35.00f);
    /**
     * 保存温度数据
     */
    private List<TempDataBean> mTempList = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private LimitLine highestLine, lowLimitLine;

    /**
     * 温度曲线类型
     */
    private InstructionConstant chartType = InstructionConstant.bb;

    public static final String REAL_LABEL = "实时温度";
    public static final String ALGORITHM_LABEL = "算法温度";

    public TempCurveView2(@NonNull Context context) {
        this(context, null);
    }

    public TempCurveView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempCurveView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_temp_curve, this);
        mLineChart = findViewById(R.id.id_line_chart);
        initLineChart();
    }

    /**
     * 设置曲线类型
     *
     * @param chartType
     */
    public void setChartType(InstructionConstant chartType) {
        this.chartType = chartType;
        switch (chartType) {
            case aa://算法温度曲线
                initDataSet(ALGORITHM_LABEL, R.color.color_temp_high);
                break;
            case ab://算法温度+真实温度曲线
                initDataSet(ALGORITHM_LABEL, R.color.color_temp_high);
                initDataSet(REAL_LABEL, R.color.color_main);
                break;
            case bb://真实温度曲线
                initLineData();
                break;
        }
        addMarkView();
    }

    public LineChart getLineChart() {
        if (mLineChart != null) {
            return mLineChart;
        }
        return null;
    }

    /**
     * 设置最小温度值
     */
    public void setMinTemp(float temp) {
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setAxisMinimum(temp);
        mLineChart.invalidate();
    }

    @SuppressLint("SetTextI18n")
    private void addLineLimit() {
        if (highestLine == null) {
            highestLine = new LimitLine(mHighestTemp, Utils.getTempAndUnit(mHighestTemp));
            highestLine.setTextSize(10f);
            highestLine.setLineWidth(3f);
            highestLine.enableDashedLine(10f, 10f, 0f);
            highestLine.setTextColor(Color.parseColor("#ff604f"));
            highestLine.setLineColor(Color.parseColor("#ff604f"));
            mLineChart.getAxisLeft().addLimitLine(highestLine);
        }

        if (lowLimitLine == null) {
            lowLimitLine = new LimitLine(mLowestTemp, Utils.getTempAndUnit(mLowestTemp));
            lowLimitLine.setTextSize(10f);
            lowLimitLine.setLineWidth(3f);
            lowLimitLine.enableDashedLine(10f, 10f, 0f);
            lowLimitLine.setTextColor(Color.parseColor("#7aabe2"));
            lowLimitLine.setLineColor(Color.parseColor("#7aabe2"));
            mLineChart.getAxisLeft().addLimitLine(lowLimitLine);
        }
    }

    private void initLineData() {
        mDataSet = new LineDataSet(entries, REAL_LABEL);
        Drawable fillDrawable = ContextCompat.getDrawable(getContext(), R.drawable.drawable_line_chart);
        fillDrawable.setAlpha(120);
        mDataSet.setFillDrawable(fillDrawable);
        mDataSet.setDrawFilled(true);
        mDataSet.setDrawCircles(false);
        mDataSet.disableDashedLine();
        mDataSet.setColor(Color.TRANSPARENT);
        mDataSet.setCircleColor(Color.BLACK);
        mDataSet.setLineWidth(0f);
        mDataSet.setCircleRadius(3f);
        mDataSet.setDrawCircleHole(false);
        mDataSet.setDrawValues(false);
        mDataSet.setDrawFilled(true);
        mDataSet.setFormLineWidth(1f);
        mDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        mDataSet.setFormSize(15.f);
        mDataSet.setHighLightColor(Color.BLACK);
        mDataSet.enableDashedHighlightLine(10f, 10f, 0f);
        if (mLineData == null) {
            mLineData = new LineData();
        }
        mLineData.addDataSet(mDataSet);
        mLineData.setDrawValues(false);
        mLineChart.setData(mLineData);
    }

    private void initLineChart() {
        mLineChart.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_gray_f4));
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(false);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setScaleYEnabled(false);

        mLineChart.getXAxis().setDrawGridLines(true);
        mLineChart.getXAxis().setGridColor(Color.parseColor("#E8E8E8"));
        mLineChart.getXAxis().enableGridDashedLine(dip2px(10), dip2px(10), 0);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //x轴文字
        mLineChart.getXAxis().setValueFormatter((value, axis) -> {

            if (value < 0 || value >= mTempList.size()) {
                return "";
            }
            if (value == (int) value) {
                return format.format(mTempList.get((int) value).getTime());
            } else {
                return "";
            }
        });
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setAxisMaximum(42.0f);
//        leftAxis.setAxisMinimum(30.0f);
        leftAxis.setAxisMinimum(20.0f);
        leftAxis.enableGridDashedLine(dip2px(10), dip2px(10), 0);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E8E8E8"));
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter((value, axis) -> Utils.getTempAndUnit(value));
//        addMarkView();
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getViewPortHandler().setMaximumScaleX(1);
        mLineChart.getViewPortHandler().setMaximumScaleY(1);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setNoDataText(getResources().getString(R.string.string_no_chart_data));
    }

    private void addMarkView() {
        final DecimalFormat tempFormat = new DecimalFormat("###.00");
        CurveMarkView markView = new CurveMarkView(getContext(), chartType == InstructionConstant.bb ? 0 : 0);
//        final NewMarkerView markView = new NewMarkerView(getContext());
        markView.setOnMarkViewListener((timeText, tempText, e) -> {
            timeText.setText(getContext().getString(R.string.string_time) + format.format(mTempList.get((int) e.getX()).getTime()));
            tempText.setText(getContext().getString(R.string.string_temp) + Utils.getTempAndUnit(tempFormat.format(e.getY())));
        });
        markView.setChartView(mLineChart); // For bounds control
        mLineChart.setMarker(markView); // Set the marker to the chart
    }

    public void addDatas(List<TempDataBean> datas) {
        LineDataSet dataSetReal = (LineDataSet) mLineData.getDataSetByLabel(REAL_LABEL, true);
        LineDataSet dataSetAlgorithm = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_LABEL, true);
        if (datas == null || datas.size() <= 0) {
            entries.clear();
            mLineChart.clear();
            return;
        }
        for (TempDataBean dataBean : datas) {
            mTempList.add(dataBean);
            addEntry(dataBean, dataSetReal);
            addEntry(dataBean, dataSetAlgorithm);
        }

        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    public void addData(float data, float algorithmTemp) {
        TempDataBean tempDataBean = new TempDataBean(System.currentTimeMillis(), data, algorithmTemp);
        mTempList.add(tempDataBean);
        if (chartType != InstructionConstant.bb) {
            LineDataSet dataSetAlgorithm = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_LABEL, true);
            addEntry(tempDataBean, dataSetAlgorithm);
        }

        if (chartType != InstructionConstant.aa) {
            LineDataSet dataSetReal = (LineDataSet) mLineData.getDataSetByLabel(REAL_LABEL, true);
            addEntry(tempDataBean, dataSetReal);
        }
//        addLineLimit();
        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
        mLineChart.getViewPortHandler().refresh(new Matrix(), mLineChart, true);
    }

    private void addEntry(TempDataBean data, LineDataSet dataSet) {
        if (dataSet == null) {
            return;
        }
        String label = dataSet.getLabel();
        if (label.equalsIgnoreCase(REAL_LABEL)) {
            dataSet.addEntry(new Entry(dataSet.getEntryCount(), data.getTemp()));
        } else if (label.equalsIgnoreCase(ALGORITHM_LABEL)) {
            dataSet.addEntry(new Entry(dataSet.getEntryCount(), data.getAlgorithmTemp()));
        }
    }

    private float dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    /**
     * 初始化算法温度曲线数据
     */
    private void initDataSet(String lable, int color) {

        Legend legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setEnabled(true);

        List<Entry> mValue = new ArrayList<>();
        LineDataSet lineDataSet = new LineDataSet(mValue, lable);
        lineDataSet.setDrawValues(true);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), color));
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);//是否显示折线上的数据
        lineDataSet.setFormLineWidth(3f);
        lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        lineDataSet.setFormSize(15.f);
        lineDataSet.enableDashedHighlightLine(10f, 10f, 0f);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        if (mLineData == null) {
            mLineData = new LineData();
        }
        mLineData.addDataSet(lineDataSet);
        mLineChart.setData(mLineData);
    }
}
