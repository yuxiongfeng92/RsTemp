package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityLoginBinding;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.user.LoginViewModel;

/**
 * <传入Extra>
 * String from measureSave 从测量保存报告进来
 * </Extra>
 */

public class LoginActivity extends BaseViewModelActivity<ActivityLoginBinding, LoginViewModel> implements View.OnClickListener {
    @Override
    protected LoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        binding.setViewClickListener(this);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        binding.getRoot().setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.getRoot()));
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_login);
    }

    @Override
    public void onClick(View view) {
    }
}
