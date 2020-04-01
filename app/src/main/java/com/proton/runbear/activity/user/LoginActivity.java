package com.proton.runbear.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.view.View;

import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseViewModelActivity;
import com.proton.runbear.databinding.ActivityLoginBinding;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.viewmodel.user.LoginViewModel2;

/**
 * Created by wangmengsi on 2018/2/26.
 * <传入Extra>
 * String from measureSave 从测量保存报告进来
 * </Extra>
 */

public class LoginActivity extends BaseViewModelActivity<ActivityLoginBinding, LoginViewModel2> implements View.OnClickListener {
    @Override
    protected LoginViewModel2 getViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel2.class);
    }

    @Override
    protected void init() {
        super.init();
        binding.setViewmodel(viewmodel);
        binding.setViewClickListener(this);
        String from = getIntent().getStringExtra("from");
        viewmodel.from.set(from);
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
        switch (view.getId()) {
            case R.id.id_tv_forgetpwd:
                //忘记密码
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
        }
    }
}
