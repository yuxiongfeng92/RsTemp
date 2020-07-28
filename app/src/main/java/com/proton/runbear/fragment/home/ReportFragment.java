package com.proton.runbear.fragment.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.proton.runbear.R;
import com.proton.runbear.activity.report.ReportDetailActivity;
import com.proton.runbear.adapter.MeasureReportAdapter;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.FragmentReportBinding;
import com.proton.runbear.fragment.base.BaseViewModelFragment;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ReportListItemBean;
import com.proton.runbear.viewmodel.report.ReportViewModel;
import com.wms.adapter.CommonViewHolder;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

/**
 * Created by wangmengsi on 2018/2/28.
 * <传入 bundle——extra>
 * type string 0:全部报告
 * type string 1:收藏报告
 * profileId int :档案id
 * </>
 */

public class ReportFragment extends BaseViewModelFragment<FragmentReportBinding, ReportViewModel> implements MeasureReportAdapter.AdapterChildClickListener {

    private MeasureReportAdapter reportListAdapter;
    private String type = "0";// 0:全部报告  1:收藏报告
    private long profileId = -1;//档案id初始值

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_report;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        Bundle reportItemBdle = getArguments();
        type = reportItemBdle.getString("type");
        profileId = reportItemBdle.getLong("profileId", -1);//档案id，如果从档案列表跳转过来需要查看单个档案下的报告列表
        initRefreshLayout(binding.idIncludeRefresh.idRefreshLayout, refreshlayout -> viewmodel.getOneBabayAllReportList(profileId, true));
        binding.idIncludeRefresh.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        reportListAdapter = new MeasureReportAdapter(getActivity(), null, R.layout.item_report_list);
        binding.idIncludeRefresh.idRecyclerview.setAdapter(reportListAdapter);
    }

    @Override
    protected ReportViewModel getViewModel() {
        return ViewModelProviders.of(this).get(ReportViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initView() {
        super.initView();
        setListener();
    }

    private void setListener() {
        if (type.equals("0")) {
            viewmodel.allReportListObF.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    if (!CommonUtils.listIsEmpty(viewmodel.allReportListObF.get())) {
                        reportListAdapter.setDatas(viewmodel.allReportListObF.get());
                        setLoadSuccess();
                    } else {
                        setLoadEmpty();
                    }
                }
            });
            viewmodel.allSelectedObserveList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    if (viewmodel.allSelectedObserveList.get().size() > 0) {
                        binding.idLayDelete.setBackgroundColor(App.get().getResources().getColor(R.color.color_orange_fc));
                        binding.idLayDelete.setEnabled(true);
                    } else {
                        binding.idLayDelete.setBackgroundColor(App.get().getResources().getColor(R.color.color_gray_b2));
                        binding.idLayDelete.setEnabled(false);
                    }
                }
            });

        } else if (type.equals("1")) {
            viewmodel.collectReportListObF.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    if (!CommonUtils.listIsEmpty(viewmodel.collectReportListObF.get())) {
                        reportListAdapter.setDatas(viewmodel.collectReportListObF.get());
                        setLoadSuccess();
                    } else {
                        setLoadEmpty();
                    }
                }
            });
            viewmodel.collectSelectedObserveList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    if (viewmodel.collectSelectedObserveList.get().size() > 0) {
                        binding.idLayDelete.setBackgroundColor(App.get().getResources().getColor(R.color.color_orange_fc));
                        binding.idLayDelete.setEnabled(true);
                    } else {
                        binding.idLayDelete.setBackgroundColor(App.get().getResources().getColor(R.color.color_gray_cc));
                        binding.idLayDelete.setEnabled(false);
                    }
                }
            });
        }
        //监听刷新
        viewmodel.isStaredRefreshing.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (!viewmodel.isStaredRefreshing.get()) {
                    binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
                }
            }
        });
        //监听加载
        viewmodel.isStartedLoadMore.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.isStartedLoadMore.get() == false) {
                    binding.idIncludeRefresh.idRefreshLayout.finishLoadmore();
                }
            }
        });
        //批量删除操作
        binding.idLayDelete.setOnClickListener(v -> {
            viewmodel.clickAllReportDelete(profileId, type);
        });
        reportListAdapter.setAdapterChildClickListener(this);
    }

    @Override
    protected void initData() {

        if (!App.get().isLogined()) {
            setLoadError();
            return;
        }

        super.initData();
        binding.idIncludeRefresh.idRefreshLayout.autoRefresh();
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idIncludeRefresh.idRecyclerview;
    }

    @Override
    protected int generateEmptyLayout() {
        return R.layout.layout_empty;
    }

    @Override
    protected String getEmptyText() {
        if (type.equals("0")) {
            return getResString(R.string.string_allreport_empty);
        } else {
            return getResString(R.string.string_collectreport_empty);
        }
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_view_report;
    }

    @Override
    public void onAdapterChildClick(View view, CommonViewHolder commonViewHolder, ReportListItemBean reportListItemBean) {
        switch (view.getId()) {
            case R.id.id_iv_more_operationgs:
                View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_item_report_collect, null);
                PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                TextView reportCollectTv = contentView.findViewById(R.id.id_tv_report_collect);
                reportCollectTv.setText(R.string.string_collect);
                reportCollectTv.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_collect_yellow_false), null, null, null);
                popupWindow.showAsDropDown(view);
                break;
            case R.id.id_lay_report_content:
                //测量Item点击事件
                if (!reportListAdapter.isEditMeasureReport()) {
                    //列表展示状态
                    Intent mIntent = new Intent(getActivity(), ReportDetailActivity.class);
                    mIntent.putExtra("reportId", reportListItemBean.getReportID());
                    mIntent.putExtra("recordId", reportListItemBean.getRecordID());
                    mIntent.putExtra("profileId", reportListItemBean.getPID());
                    mIntent.putExtra("maxTemp", reportListItemBean.getTempMax());
                    mIntent.putExtra("profileName", reportListItemBean.getProfileName());
                    startActivity(mIntent);
                } else {
                    //编辑报告删除状态
                    //ImageView checkIv = (ImageView) view.findViewById(R.id.id_iv_report_choose);
//                    viewmodel.itemClick(type, commonViewHolder.getItemPosition());
//                    reportListAdapter.notifyItemRangeChanged(commonViewHolder.getItemPosition(), 1);
                }
                break;
            case R.id.id_tv_delete:
                ((SwipeMenuLayout) commonViewHolder.getView(R.id.id_swipe_refresh_layout)).quickClose();
//                viewmodel.deleteReport(profileId, reportListItemBean.getId(), type);
                break;
        }
    }

    /**
     * 分享测量报告
     *
     * @param shareReportPosition 列表位置
     */
//    private void shareReport(int shareReportPosition) {
//        List<ReportListItemBean> reportList = reportListAdapter.getReportList();
//        if (reportList == null || reportList.size() == 0) {
//            BlackToast.show(R.string.string_data_empty);
//            return;
//        }
//        ReportListItemBean reportListItemBean = reportList.get(shareReportPosition);
//        ShareReportFragment shareReportFragment = new ShareReportFragment();
//        //shareReportFragment.setShareReportListener(this);
//        Bundle paramsBdle = new Bundle();
//        //报告id
//        paramsBdle.putString("reportId", reportListItemBean.getId());
//        //报告起始时间
//        paramsBdle.putLong("starttime", reportListItemBean.getStarttime());
//        //报告结束时间
//        paramsBdle.putLong("endtime", reportListItemBean.getEndtime());
//        //最高温度
//        paramsBdle.putString("maxTemp", reportListItemBean.getData().getTemp_max());
//        //档案id
//        paramsBdle.putLong("profileId", reportListItemBean.getProfileid());
//        //档案姓名
//        paramsBdle.putString("profileName", reportListItemBean.getProfilename());
//        //报告下载地址
//        paramsBdle.putString("reportUrlPath", reportListItemBean.getFilepath());
//        shareReportFragment.setArguments(paramsBdle);
//        shareReportFragment.show(getActivity().getFragmentManager(), reportListItemBean.getId());
//    }

    /**
     * @param isEdit 是:编辑报告 否:展示报告
     */
    public void editOrCancel(boolean isEdit) {
        if (isEdit) {
            //编辑报告
            reportListAdapter.setEditMeasureReport(true);
            binding.idLayDelete.setVisibility(View.VISIBLE);
            binding.idLayDelete.setEnabled(false);
        } else {
            //展示报告
            reportListAdapter.setEditMeasureReport(false);
            binding.idLayDelete.setVisibility(View.GONE);
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        MessageEvent.EventType eventType = event.getEventType();
        if (eventType == MessageEvent.EventType.ADD_REPORT
                || event.getEventType() == MessageEvent.EventType.PROFILE_CHANGE) {
            Logger.w("添加报告，刷新列表");
            initData();
        } else if (event.getEventType() == MessageEvent.EventType.SWITCH_UNIT) {
            reportListAdapter.notifyDataSetChanged();
        } else if (eventType == MessageEvent.EventType.UPDATE_ALL_REPORT && type.equals("0")) {
            initData();
        } else if (eventType == MessageEvent.EventType.UPDATE_COLLECT_REPORT && type.equals("1")) {
            initData();
        } else if (eventType == MessageEvent.EventType.LOGIN) {
            initData();
        } else if (eventType == MessageEvent.EventType.DELETE_PROFILE) {
            initData();
        }
    }

    @Override
    protected boolean openStat() {
        return false;
    }
}
