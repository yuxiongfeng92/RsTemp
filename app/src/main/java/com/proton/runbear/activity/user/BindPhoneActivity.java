package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityBindPhoneBinding;
import com.proton.runbear.utils.ActivityManager;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.user.LoginViewModel;

public class BindPhoneActivity extends BaseViewModelActivity<ActivityBindPhoneBinding, LoginViewModel> {

    @Override
    protected int inflateContentView() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void init() {
        super.init();
        viewmodel.phoneNum.set("");
        binding.setViewModel(viewmodel);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idContent.setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.idContent));
    }

    @Override
    protected LoginViewModel getViewModel() {
        //获取登录页面的ViewModel
        return ViewModelProviders.of(ActivityManager.findActivity(LoginActivity.class)).get(LoginViewModel.class);
    }

}
