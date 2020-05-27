package com.proton.runbear.fragment.measure;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.databinding.FragmentMeasureBinding;
import com.proton.runbear.fragment.base.BaseFragment;

/**
 * 单个测量卡片
 */

public class MeasureFragment extends BaseFragment<FragmentMeasureBinding> {
    public MeasureBean mMeasureInfo;
    private MeasureItemFragment mMeasuringItemFragment;

    private BaseMeasureFragment mCurrentFragment;
    private OnMeasureListener onMeasureListener;

    public static MeasureFragment newInstance(MeasureBean measureBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("measureInfo", measureBean);
        MeasureFragment fragment = new MeasureFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure;
    }

    @Override
    protected void fragmentInit() {
        mMeasureInfo = (MeasureBean) getArguments().getSerializable("measureInfo");
        showMeasuring(mMeasureInfo);
    }
    /**
     * 显示正在测量界面
     */
    public void showMeasuring(MeasureBean measureBean) {
        if (mMeasuringItemFragment == null) {
            mMeasuringItemFragment = MeasureItemFragment.newInstance(measureBean);
        }
        showFragment(mMeasuringItemFragment);
    }


    /**
     * 显示fragment
     */
    private void showFragment(BaseMeasureFragment fragment) {
        if (fragment == null || fragment.isDetached()) return;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_in,
                R.anim.card_out,
                R.anim.card_in,
                R.anim.card_out
        );
        transaction.replace(R.id.id_measure_container, fragment);
        transaction.commitAllowingStateLoss();
        mCurrentFragment = fragment;
    }


    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    @Override
    protected boolean openStat() {
        return false;
    }

    public interface OnMeasureListener {
        /**
         * 停止当前测量
         */
        void closeCard();
    }
}
