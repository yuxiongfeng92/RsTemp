package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityBindEmailBinding;
import com.proton.runbear.viewmodel.user.InternationalLoginViewModel;

public class BindEmailActivity extends BaseViewModelActivity<ActivityBindEmailBinding, InternationalLoginViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setViewModel(viewmodel);
        String certToken=getIntent().getStringExtra("cert_token");
        viewmodel.certToken.set(certToken);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_bind_email;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }
}
