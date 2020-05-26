package com.proton.runbear.net.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.proton.runbear.R;
import com.proton.runbear.utils.UIUtils;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 档案管理实体对象
 */

public class ProfileBean extends LitePalSupport implements Serializable {

    @SerializedName("PID")
    private long profileId = -1;

    @SerializedName("PatientID")
    private long uid;

    @SerializedName("PatientName")
    private String username;

    @SerializedName("Sex")
    private int gender;

    @SerializedName("ImageUrl")
    private String avatar;

    @SerializedName("Birthdate")
    private String birthday;

    @SerializedName("patchMac")
    private String macAddress;

    private long created;

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public String getAge() {
        if (!TextUtils.isEmpty(birthday)) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date birthDate = dateformat.parse(birthday);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birthDate);
                int birthYear = calendar.get(Calendar.YEAR);
                //获得当前年份
                calendar.setTime(new Date());
                int currentYear = calendar.get(Calendar.YEAR);
                int age = currentYear - birthYear;
                if (age >= 0) {
                    return UIUtils.getQuantityString(R.plurals.string_sui, age);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macaddress) {
        this.macAddress = macaddress;
    }
}
