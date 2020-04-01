package com.proton.runbear.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proton.runbear.R;
import com.proton.runbear.constant.AppConfigs;
import com.proton.runbear.databinding.FragmentTutorialBinding;
import com.proton.runbear.utils.HttpUrls;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.SpUtils;

/**
 * Created by wangmengsi on 2018/3/28.
 * 使用教程
 */
public class TutorialDialogFragment extends BaseDialogFragment {

    private FragmentTutorialBinding binding;

    public static TutorialDialogFragment newInstance() {
        return new TutorialDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial, container, false);
        initWindow();
        binding.idCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> SpUtils.saveBoolean(AppConfigs.SP_KEY_SHOW_TUTORIAL, !isChecked));
        binding.idIKnow.setOnClickListener(v -> dismiss());
        binding.idWatchVideo.setOnClickListener(v -> IntentUtils.goToWeb(getContext(), HttpUrls.URL_SCAN_HELP));
        return binding.getRoot();
    }

    @Override
    public float widthRadio() {
        return 1;
    }
}
