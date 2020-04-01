package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityRegistInternationnalBinding;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.user.InternationalLoginViewModel;

public class RegistInternationnalActivity extends BaseViewModelActivity<ActivityRegistInternationnalBinding, InternationalLoginViewModel> {

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        viewmodel.isRegist.set(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idContent.setOnClickListener(v -> Utils.hideKeyboard(this,binding.idContent));
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_regist_internationnal;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_regist);
    }
}
