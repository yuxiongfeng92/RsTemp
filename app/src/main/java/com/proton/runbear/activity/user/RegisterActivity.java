package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityRegistBinding;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.user.LoginViewModel;

/**
 * Created by wangmengsi on 2018/2/27.
 */

public class RegisterActivity extends BaseViewModelActivity<ActivityRegistBinding, LoginViewModel> {

    @Override
    protected int inflateContentView() {
        return R.layout.activity_regist;
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
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_regist);
    }

}
