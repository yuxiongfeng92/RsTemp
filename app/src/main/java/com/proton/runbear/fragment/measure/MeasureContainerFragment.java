package com.proton.runbear.fragment.measure;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.proton.runbear.R;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.component.App;
import com.proton.runbear.databinding.FragmentMeasureContainerBinding;
import com.proton.runbear.fragment.base.BaseFragment;
import com.proton.runbear.fragment.base.BaseLazyFragment;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.EventBusManager;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.LongClickUtils;
import com.proton.runbear.enums.InstructionConstant;
import com.proton.runbear.view.InstructionDialog;

/**
 * Created by wangmengsi on 2018/2/28.
 * 测量界面容器
 */

public class MeasureContainerFragment extends BaseLazyFragment<FragmentMeasureContainerBinding> {

    private MeasureChooseProfileFragment mChooseProfileFragment;
    private BaseFragment mCurrentFragment;
    private MeasureCardsFragment mMeasuringFragment;
    private OnMeasureContainerListener onMeasureContainerListener;
    private boolean isAddDevice;
    private boolean isUnbindInto;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static MeasureContainerFragment newInstance(boolean isAddDevice) {
        return newInstance(isAddDevice, false, null);
    }

    public static MeasureContainerFragment newInstance(boolean isAddDevice, boolean directToScan, ProfileBean profile) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAddDevice", isAddDevice);
        bundle.putBoolean("directToScan", directToScan);
        bundle.putSerializable("profile", profile);
        MeasureContainerFragment fragment = new MeasureContainerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_container;
    }

    @Override
    protected void fragmentInit() {
        isAddDevice = getArguments().getBoolean("isAddDevice");
        if (isAddDevice) {
            binding.idTopLayout.idToogleDrawer.setImageResource(R.drawable.btn_back_img);
        } else {
            binding.idTopLayout.idToogleDrawer.setImageResource(R.drawable.icon_toolbar_left);
        }
        showChooseProfile();

        LongClickUtils.setLongClick(mHandler, binding.idTopLayout.idTitle, 1500, v -> {
            InstructionDialog dialog = new InstructionDialog(getActivity());
            dialog.setCancelListener(v1 -> dialog.dismiss());
            dialog.setConfirmListener(v12 -> {
                String inputInstruction = dialog.getInputInstruction();
                if (TextUtils.isEmpty(inputInstruction)) {
                    BlackToast.show("请输入指令");
                    return;
                }
                InstructionConstant instructionConstant = InstructionConstant.getInstructionConstant(inputInstruction);
                if (instructionConstant == null) {
                    BlackToast.show("无该指令");
                    return;
                }
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.INPUT_INSTRUCTION, inputInstruction));
                dialog.dismiss();
            });
            dialog.show();
            return true;
        });

    }

    @Override
    protected void initView() {
        super.initView();
        if (!isAddDevice) {
            binding.idTopLayout.idTitle.setText(getActivity().getResources().getString(R.string.string_measure));
        } else {
            binding.idTopLayout.idTitle.setText(getActivity().getResources().getString(R.string.string_add_new_device));
        }
        binding.idTopLayout.idTopRight.setOnClickListener(v -> IntentUtils.goToAddNewDevice(mContext));
        binding.idTopLayout.idToogleDrawer.setOnClickListener(v -> {
            if (onMeasureContainerListener != null) {
                onMeasureContainerListener.onToggleDrawer();
            }
        });
    }

    /**
     * 显示选择档案
     */
    public void showChooseProfile() {
        if (hasMeasureItem()) {
            return;
        }
        if (App.get().isLogined()) {
            //未登录不显示档案界面
            BlackToast.show("未登陆");
            IntentUtils.goToLogin(mContext);
            return;
        }
        if (mChooseProfileFragment == null) {
            mChooseProfileFragment = MeasureChooseProfileFragment.newInstance();
        }
        //档案选择
        mChooseProfileFragment.setOnChooseProfileListener(new MeasureChooseProfileFragment.OnChooseProfileListener() {
            @Override
            public void onClickProfile(ProfileBean profile) {
                /**
                 * 先设置档案信息，连接之前需要设置设备信息
                 */
                MeasureBean measureBean = new MeasureBean(profile);
                showMeasuring(measureBean);
            }

        });
        showFragment(mChooseProfileFragment);
    }


    /**
     * 显示正在测量界面
     */
    public void showMeasuring(MeasureBean measureBean) {
        if (measureBean == null || measureBean.getProfile() == null) return;
        if (mMeasuringFragment == null) {
            mMeasuringFragment = MeasureCardsFragment.newInstance();
        }
        mMeasuringFragment.setOnMeasureFragmentListener(new MeasureCardsFragment.OnMeasureFragmentListener() {
            @Override
            public void onCloseAllMeasure() {
                showChooseProfile();
            }

            @Override
            public void onMeasureStatusChanged(boolean isBeforeMeasure) {
                binding.idTopLayout.idTitle.setText(isBeforeMeasure ? R.string.string_measure_preparing : R.string.string_measure);
            }
        });
        showFragment(mMeasuringFragment);
        mMeasuringFragment.addItem(measureBean);
        if (onMeasureContainerListener != null) {
            onMeasureContainerListener.onShowMeasuring();
        }
    }

    /**
     * 显示fragment
     */
    private void showFragment(BaseFragment fragment) {
        if (fragment == null || fragment == mCurrentFragment) return;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_right
        );
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
        transaction.commitAllowingStateLoss();

        doMeasuringCallback(fragment);
    }

    private void doMeasuringCallback(BaseFragment fragment) {
        if (fragment instanceof MeasureCardsFragment && App.get().isLogined()) {
            //显示测量界面
            binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
        } else {
            binding.idTopLayout.idTopRight.setVisibility(View.GONE);
        }
    }

    /**
     * 当前是否有测量
     */
    public boolean hasMeasureItem() {
        return (mMeasuringFragment != null && mMeasuringFragment.hasMeasureItem());
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
    }

    public void closeAllCards() {
        if (mMeasuringFragment != null) {
            mMeasuringFragment.closeAllCards();
        }
    }

    public void setOnMeasureContainerListener(OnMeasureContainerListener onMeasureContainerListener) {
        this.onMeasureContainerListener = onMeasureContainerListener;
    }

    @Override
    protected boolean openStat() {
        return false;
    }

    public interface OnMeasureContainerListener {
        /**
         * 添加测量卡片
         */
        void onAddMeasureItem(MeasureBean measureInfo);

        /**
         * 打开关闭drawer
         */
        void onToggleDrawer();

        /**
         * 当前正在显示测量界面
         */
        void onShowMeasuring();
    }
}