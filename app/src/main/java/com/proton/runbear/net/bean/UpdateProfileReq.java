package com.proton.runbear.net.bean;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/5/27 9:16
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/27 9:16
 */
public class UpdateProfileReq {

    /**
     * PID : 4
     * PatientID : 4
     * PatientName : 小黄
     * Birthdate : 2014-04-19T10:24:08.76
     * Sex : 1
     * Address :
     * AllergicHistory :过敏史
     * MedicalHistory :用药史
     * Weight : null
     * Height : null
     * Region : null
     * HereditaryHistory : 遗传史
     * ImageUrl : null
     */

    private String PID;
    private String PatientID;
    private String PatientName;
    private String Birthdate;
    private int Sex;
    private String Address;
    private String AllergicHistory;
    private String MedicalHistory;
    private String Weight;
    private String Height;
    private String Region;
    private String ImageUrl;

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        PatientID = patientID;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAllergicHistory() {
        return AllergicHistory;
    }

    public void setAllergicHistory(String allergicHistory) {
        AllergicHistory = allergicHistory;
    }

    public String getMedicalHistory() {
        return MedicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        MedicalHistory = medicalHistory;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
