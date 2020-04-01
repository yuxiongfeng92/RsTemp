package com.proton.runbear.activity.measure;

import android.support.v4.app.FragmentTransaction;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.databinding.ActivityAddNewDeviceBinding;
import com.proton.runbear.fragment.base.BaseFragment;
import com.proton.runbear.fragment.measure.MeasureContainerFragment;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.utils.EventBusManager;
import com.wms.logger.Logger;

public class AddNewDeviceActivity extends BaseActivity<ActivityAddNewDeviceBinding> {
    private BaseFragment mCurrentFragment;
    private MeasureContainerFragment mMeasureFragment;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_add_new_device;
    }

    @Override
    protected void initView() {
        super.initView();
        showMeasureFragment();
    }

    /**
     * 显示测量
     */
    private void showMeasureFragment() {
        boolean directToScan = getIntent().getBooleanExtra("directToScan", false);
        ProfileBean profile = (ProfileBean) getIntent().getSerializableExtra("profile");
        if (mMeasureFragment == null) {
            mMeasureFragment = MeasureContainerFragment.newInstance(true, directToScan, profile);
            mMeasureFragment.setOnMeasureContainerListener(new MeasureContainerFragment.OnMeasureContainerListener() {
                @Override
                public void onAddMeasureItem(MeasureBean measureInfo) {
                    Logger.w("添加测量界面");
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.ADD_MEASURE_ITEM, measureInfo));
                    finish();
                }

                @Override
                public void onToggleDrawer() {
                    finish();
                }

                @Override
                public void onShowMeasuring() {
                }
            });
        }
        showFragment(mMeasureFragment);
    }

    /**
     * 显示fragment
     */
    private void showFragment(BaseFragment fragment) {
        if (fragment == null || fragment == mCurrentFragment) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            if (fragment.isAdded()) {
                //fragment已经添加了
                transaction.hide(mCurrentFragment).show(fragment);
            } else {
                transaction.hide(mCurrentFragment).add(R.id.id_container, fragment);
            }
        } else {
            if (fragment.isAdded()) {
                //fragment已经添加了
                transaction.show(fragment);
            } else {
                transaction.add(R.id.id_container, fragment);
            }
        }
        mCurrentFragment = fragment;
        transaction.commit();
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
    }
}
