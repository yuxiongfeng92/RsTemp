package com.proton.runbear.activity.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.proton.runbear.R;
import com.proton.runbear.activity.base.BaseActivity;
import com.proton.runbear.bean.rs.JsonBean;
import com.proton.runbear.databinding.ActivityAddProfileBinding;
import com.proton.runbear.databinding.AddAdditionalBaseInfoLayoutBinding;
import com.proton.runbear.databinding.CaseHistoryLayoutBinding;
import com.proton.runbear.net.bean.AddProfileReq;
import com.proton.runbear.net.bean.ProfileBean;
import com.proton.runbear.net.callback.NetCallBack;
import com.proton.runbear.net.callback.ResultPair;
import com.proton.runbear.net.center.ProfileCenter;
import com.proton.runbear.utils.BlackToast;
import com.proton.runbear.utils.DateUtils;
import com.proton.runbear.utils.FileUtils;
import com.proton.runbear.utils.GetJsonDataUtil;
import com.proton.runbear.utils.Utils;
import com.proton.runbear.utils.net.OSSUtils;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 增加档案类
 */
public class AddProfileActivity extends BaseActivity<ActivityAddProfileBinding> implements OnDateSetListener {
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    protected Uri cropUri = Uri.fromFile(new File(FileUtils.getAvatar()));
    private String ossAvatorUri;
    private TimePickerDialog mDialogYearMonthDay;
    private Uri tempUri;
    private File mCameraFile = new File(FileUtils.getDataCache(), "image.jpg");
    private boolean canSkip;

    /**
     * 消息设置打开view
     */
    private View additionalSetOpenView;
    private View caseHistorySetOpenView;
    private AddAdditionalBaseInfoLayoutBinding additionalBaseInfoLayoutBinding;
    private CaseHistoryLayoutBinding caseHistoryLayoutBinding;

    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    /**
     * 数据是否解析完成
     */
    private static boolean isLoaded = false;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        Toast.makeText(AddProfileActivity.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    isLoaded = false;
                    break;
            }
        }
    };


    @Override
    protected void init() {
        super.init();
        long minMillSeconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//时间格式化类
        try {
            Date date = sdf.parse("1900-1-1");//解析到一个时间
            minMillSeconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mDialogYearMonthDay = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getResources().getString(R.string.string_cancel))
                .setSureStringId(getResources().getString(R.string.string_finish))
                .setTitleStringId(getResources().getString(R.string.string_choose_birthday_title))
                .setYearText(getResources().getString(R.string.string_year))
                .setMonthText(getResources().getString(R.string.string_month))
                .setDayText(getResources().getString(R.string.day))
                .setHourText(getResources().getString(R.string.hour))
                .setMinuteText(getResources().getString(R.string.minute))
                .setMinMillseconds(minMillSeconds)
                .setMaxMillseconds(System.currentTimeMillis())
                .setType(Type.YEAR_MONTH_DAY)
                .setThemeColor(getResources().getColor(R.color.color_gray_b3))
                .setToolBarTextColor(getResources().getColor(R.color.color_main))
                .build();
        canSkip = getIntent().getBooleanExtra("canSkip", true);

        initJsonData();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_add_profile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case UCrop.REQUEST_CROP:
                    uploadPic();
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    private void uploadPic() {
        Observable.just(FileUtils.getAvatar())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> OSSUtils.uploadImg(FileUtils.getAvatar()))
                .subscribe(s -> {
                    ossAvatorUri = s;
                    binding.idSdvProfileAddavator.setImageURI(ossAvatorUri);
                }, throwable -> com.wms.logger.Logger.w(throwable.toString()));
    }

    /**
     * 裁剪图片方法实现
     */
    protected void startPhotoZoom(Uri uri) {
        UCrop.of(uri, cropUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(300, 300)
                .start(this);
    }

    private void openGallery() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    private void openCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".FileProvider",
                    mCameraFile);
        } else {
            tempUri = Uri.fromFile(mCameraFile);
        }
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(takePhotoIntent, TAKE_PICTURE);
    }

    @Override
    protected void setListener() {
        super.setListener();
       /* binding.idSdvProfileAddavator.setOnClickListener(v -> {
            //添加头像
            showChoosePicDialog();
        });*/
        binding.idSdvProfileAddavator.setImageResource(R.drawable.icon_default_profile);
        //选择出生日期
        binding.idSelectBirthday.setOnClickListener(v -> mDialogYearMonthDay.show(getSupportFragmentManager(), ""));
        //添加档案
        binding.idBtnFinish.setOnClickListener(v -> addProfileRequest());
        binding.getRoot().setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.getRoot()));

        /**
         * 基本信息
         */
        binding.layMsgSetting.setOnClickListener(v -> openAdditionalInfoSet());
        binding.ivAdditionalSetDown.setOnClickListener(v -> openAdditionalInfoSet());
        /**
         * 病史
         */
        binding.layCaseHistorySetting.setOnClickListener(v -> openCaseHistorySet());
        binding.ivCaseHistorySetDown.setOnClickListener(v -> openCaseHistorySet());


    }

    /**
     * 打开基本信息设置
     */
    private void openAdditionalInfoSet() {
        //展开
        if (!binding.idVsAdditionalSet.isInflated() || additionalSetOpenView.getVisibility() == View.GONE) {

            if (additionalSetOpenView == null) {
                ViewStub msgSetVs = binding.idVsAdditionalSet.getViewStub();
                additionalSetOpenView = msgSetVs.inflate();
                additionalBaseInfoLayoutBinding = DataBindingUtil.bind(additionalSetOpenView);
            } else {
                additionalSetOpenView.setVisibility(View.VISIBLE);
            }
            binding.ivAdditionalSetDown.setImageResource(R.drawable.icon_setarrow_on);
            binding.layMsgSetting.setBackgroundColor(Color.parseColor("#eeeeee"));


        } else {
            //关闭
            additionalSetOpenView.setVisibility(View.GONE);
            binding.ivAdditionalSetDown.setImageResource(R.drawable.icon_setarrow_off);
            binding.layMsgSetting.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        additionalBaseInfoLayoutBinding.llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoaded) {
                    showPickerView();
                }else {
                    BlackToast.show("parse error");
                    initJsonData();
                }
            }
        });
    }

    /**
     * 打开病史设置
     */
    private void openCaseHistorySet() {
        //展开
        if (!binding.idVsCaseHistorySet.isInflated() || caseHistorySetOpenView.getVisibility() == View.GONE) {
            if (caseHistorySetOpenView == null) {
                ViewStub caseHistorySetVS = binding.idVsCaseHistorySet.getViewStub();
                caseHistorySetOpenView = caseHistorySetVS.inflate();
                caseHistoryLayoutBinding = DataBindingUtil.bind(caseHistorySetOpenView);
            } else {
                caseHistorySetOpenView.setVisibility(View.VISIBLE);
            }
            binding.layCaseHistorySetting.setBackgroundColor(Color.parseColor("#eeeeee"));
            binding.ivCaseHistorySetDown.setImageResource(R.drawable.icon_setarrow_on);
        } else {
            caseHistorySetOpenView.setVisibility(View.GONE);
            binding.ivCaseHistorySetDown.setImageResource(R.drawable.icon_setarrow_off);
            binding.layCaseHistorySetting.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    private void addProfileRequest() {

        AddProfileReq addProfileReq = new AddProfileReq();
//        if (TextUtils.isEmpty(ossAvatorUri)) {
//            ossAvatorUri = AppConfigs.DEFAULT_AVATOR_URL;
//        } else {
//            ossAvatorUri = OSSUtils.getSaveUrl(ossAvatorUri);
//        }
//        map.put("avatar", ossAvatorUri);
        //姓名
        String name = binding.idEtProfileaAddname.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            BlackToast.show(R.string.string_name_profile_tip);
            return;
        } else {
            addProfileReq.setPatientName(name);
        }
        //性别
        if (binding.idRbProfileBoy.isChecked()) {
            addProfileReq.setSex(1);//男
        } else if (binding.idRbProfileGirl.isChecked()) {
            addProfileReq.setSex(2);//女
        }
        //生日
        String birthday = binding.idSelectBirthday.getText().toString().trim();
        if (TextUtils.isEmpty(birthday)) {
            BlackToast.show(R.string.string_choose_birthday);
            return;
        } else {
            addProfileReq.setBirthdate(birthday);
        }

        /**
         * 基本信息
         */
        if (additionalBaseInfoLayoutBinding!=null) {

            if (!TextUtils.isEmpty(additionalBaseInfoLayoutBinding.idHeight.getText().toString())) {
                String height = additionalBaseInfoLayoutBinding.idHeight.getText().toString().trim();
                addProfileReq.setHeight(height);
            }

            if (!TextUtils.isEmpty(additionalBaseInfoLayoutBinding.idWeight.getText().toString())) {
                String weight = additionalBaseInfoLayoutBinding.idWeight.getText().toString().trim();
                addProfileReq.setWeight(weight);
            }

            if (!TextUtils.isEmpty(additionalBaseInfoLayoutBinding.idLocation.getText().toString())) {
                String location = additionalBaseInfoLayoutBinding.idLocation.getText().toString().trim();
                addProfileReq.setRegion(location);
            }
        }

        /**
         * 病史
         */
        if (caseHistoryLayoutBinding!=null) {
            if (!TextUtils.isEmpty(caseHistoryLayoutBinding.idCaseHistory.getText().toString())) {
                String caseHistory = caseHistoryLayoutBinding.idCaseHistory.getText().toString().trim();
                addProfileReq.setAllergicHistory(caseHistory);
            }
        }

        showDialog();
        requestAddProfile(addProfileReq);
    }

    /**
     * 从哪个页面进来添加档案
     */
    private void requestAddProfile(AddProfileReq req) {
        ProfileCenter.addProfile(new NetCallBack<ProfileBean>() {
            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ProfileBean data) {
                dismissDialog();
                BlackToast.show(R.string.string_profile_add);
//                if (!TextUtils.isEmpty(App.get().getLastScanDeviceId())) {
//                    //上次扫描了直接绑定
//                    bindDevice(data.getProfileId());
//                } else {
//                    if (getIntent().getBooleanExtra("needScanQRCode", false)) {
//                        IntentUtils.goToScanQRCode(mContext, data, true);
//                    }
//                }
                finish();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
            }
        }, req);
    }

    /**
     * 弹出地址选择框
     */
    private void showPickerView() {

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
                additionalBaseInfoLayoutBinding.idLocation.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }



    /**
     * 解析数据
     */
    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        final String[] stringItems = {getResString(R.string.string_choose_localpic), getResString(R.string.string_take_picture)};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title(getResString(R.string.string_set_avatar));
        dialog.titleTextSize_SP(14F);
        dialog.cancelText(getString(R.string.string_cancel));
        dialog.show();
        dialog.setOnOperItemClickL((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    openCamera();
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
    }

    @Override
    protected boolean showBackBtn() {
        return canSkip;
    }

    @Override
    public void onBackPressed() {
        if (canSkip) {
            super.onBackPressed();
        }
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_add_profile);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        binding.idSelectBirthday.setText(DateUtils.dateStrToYMD(millseconds));
    }
}
