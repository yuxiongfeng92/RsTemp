package com.proton.runbear.fragment.measure;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.databinding.Observable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.proton.runbear.R;
import com.proton.runbear.activity.profile.AddProfileActivity;
import com.proton.runbear.bean.MeasureBean;
import com.proton.runbear.databinding.FragmentMeasureChooseProfileBinding;
import com.proton.runbear.fragment.base.BaseViewModelFragment;
import com.proton.runbear.net.bean.MeasureEndResp;
import com.proton.runbear.net.bean.MessageEvent;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.MeasureCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.IntentUtils;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.view.EllipsizeTextView;
import com.proton.runbear.viewmodel.measure.MeasureViewModel;
import com.proton.runbear.viewmodel.profile.ProfileViewModel;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangmengsi on 2018/2/28.
 * 测量选择档案
 */

public class MeasureChooseProfileFragment extends BaseViewModelFragment<FragmentMeasureChooseProfileBinding, ProfileViewModel> {

    private OnChooseProfileListener onChooseProfileListener;
    private List<ProfileBean> mProfiles = new ArrayList<>();

    public static MeasureChooseProfileFragment newInstance() {
        return new MeasureChooseProfileFragment();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_choose_profile;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();

        viewmodel.profileList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                initRecyclerView();
            }

            private void initRecyclerView() {
                mProfiles.clear();
                mProfiles.addAll(viewmodel.profileList.get());
                filterMeasuringProfile();
                initProfileRecycler();
            }
        });
    }

    private void initProfileRecycler() {
        binding.idRefreshLayout.finishRefresh();
        if (CommonUtils.listIsEmpty(mProfiles)) {
            binding.idProfileLayout.setVisibility(View.GONE);
            return;
        }
        binding.idProfileLayout.setVisibility(View.VISIBLE);
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        binding.idRecyclerview.setAdapter(new CommonAdapter<ProfileBean>(mContext, mProfiles, R.layout.item_choose_profile) {
            @Override
            public void convert(CommonViewHolder holder, ProfileBean profileBean) {
                EllipsizeTextView nameText = holder.getView(R.id.id_name);
                nameText.setText(profileBean.getUsername());
                holder.setText(R.id.id_age, profileBean.getAge());
                SimpleDraweeView avatarImg = holder.getView(R.id.id_avatar);
                avatarImg.setImageURI(profileBean.getAvatar());

                holder.setText(R.id.id_macadress, TextUtils.isEmpty(profileBean.getMacAddress()) ? getString(R.string.string_not_bind_patch) : getString(R.string.string_has_bind_patch) + Utils.getShowMac(profileBean.getMacAddress()));

                holder.getView(R.id.id_measure).setOnClickListener(v -> {
                    if (onChooseProfileListener != null) {
                        if (profileBean != null) {
                            if (!TextUtils.isEmpty(profileBean.getExamid())) {//如果examid不为空，则上次测量未结束，需要先结束测量
                                MeasureCenter.measureEnd(profileBean.getExamid(), new NetCallBack<MeasureEndResp>() {
                                    @Override
                                    public void noNet() {
                                        super.noNet();
                                        BlackToast.show(R.string.string_no_net);
                                    }

                                    @Override
                                    public void onSucceed(MeasureEndResp data) {
                                        profileBean.setExamid(null);
                                        onChooseProfileListener.onClickProfile(profileBean);
                                    }

                                    @Override
                                    public void onFailed(ResultPair resultPair) {
                                        super.onFailed(resultPair);
                                        BlackToast.show(resultPair.getData());
                                    }
                                });
                            } else {
                                onChooseProfileListener.onClickProfile(profileBean);
                            }
                        }

                    }
                });

                holder.getView(R.id.id_rebind).setOnClickListener(v -> {
                    if (onChooseProfileListener != null) {
                        onChooseProfileListener.reBindDevice(profileBean);
                    }
//                    IntentUtils.goToScanQRCode(mContext, profileBean);
                });
                holder.getView(R.id.id_lay_profile_edit).setOnClickListener(v -> IntentUtils.goToEditProfile(mContext, profileBean));
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idAddProfile.setOnClickListener(v -> {
            //新增档案
            getActivity().startActivityForResult(new Intent(getActivity(), AddProfileActivity.class)
                            .putExtra("needScanQRCode", true)
                    , 10000);
        });

        initRefreshLayout(binding.idRefreshLayout, refreshlayout -> {
            viewmodel.getProfileList();
        });
    }

    @Override
    protected ProfileViewModel getViewModel() {
        return new ProfileViewModel();
    }

    @Override
    protected void initData() {
        super.initData();
        viewmodel.getProfileList();
    }

    @Override
    protected int generateEmptyLayout() {
        return 0;
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idRecyclerview;
    }

    public void setOnChooseProfileListener(OnChooseProfileListener onChooseProfileListener) {
        this.onChooseProfileListener = onChooseProfileListener;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            filterMeasuringProfile();
        }
    }

    /**
     * 过滤当前正在测量的档案
     */
    private void filterMeasuringProfile() {
        mProfiles.clear();
        if (viewmodel.profileList != null
                && !CommonUtils.listIsEmpty(viewmodel.profileList.get())) {
            mProfiles.addAll(viewmodel.profileList.get());
            Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
            if (viewmodels != null && viewmodels.size() > 0) {
                for (String key : viewmodels.keySet()) {
                    if (viewmodels.get(key) instanceof MeasureViewModel) {
                        //测量的viewmodel
                        MeasureBean measureBean = ((MeasureViewModel) viewmodels.get(key)).measureInfo.get();
                        if (measureBean == null) continue;
                        Iterator<ProfileBean> iterator = mProfiles.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getProfileId() == measureBean.getProfile().getProfileId()) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }

            initProfileRecycler();
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        MessageEvent.EventType type = event.getEventType();
        if (type == MessageEvent.EventType.PROFILE_CHANGE
                || type == MessageEvent.EventType.BIND_DEVICE_SUCCESS
                || type == MessageEvent.EventType.UNBIND_DEVICE_SUCCESS) {
            //档案编辑了或者档案绑定了贴
            viewmodel.getProfileList();
        }
    }

    public interface OnChooseProfileListener {
        void reBindDevice(ProfileBean profile);

        void onClickProfile(ProfileBean profile);
    }
}
