package com.proton.runbear.net.bean;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/5/26 16:19
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/26 16:19
 */
public class AddProfileReq {

    /**
     * PatientName : 李四
     * Birthdate : 2019-01-01 15:11:11
     * Sex : 1
     * IdentityCard : 520102199303067777
     * Address : 地址
     * MobilePhone : 18984316999
     * Nation : 汉族
     * AllergicHistory : 过敏史
     * MedicalHistory : 病史
     * HereditaryHistory : 遗传病史
     * Weight : 60
     * Height : 173
     * Region : 贵州省贵阳市南明区
     */

    private String PatientName;
    private String Birthdate;
    private int Sex;
    private String IdentityCard;
    private String Address;
    private String MobilePhone;
    private String Nation;
    private String AllergicHistory;
    private String MedicalHistory;
    private String HereditaryHistory;
    private String Weight;
    private String Height;
    private String Region;

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String PatientName) {
        this.PatientName = PatientName;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String Birthdate) {
        this.Birthdate = Birthdate;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int Sex) {
        this.Sex = Sex;
    }

    public String getIdentityCard() {
        return IdentityCard;
    }

    public void setIdentityCard(String IdentityCard) {
        this.IdentityCard = IdentityCard;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(String MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    public String getNation() {
        return Nation;
    }

    public void setNation(String Nation) {
        this.Nation = Nation;
    }

    public String getAllergicHistory() {
        return AllergicHistory;
    }

    public void setAllergicHistory(String AllergicHistory) {
        this.AllergicHistory = AllergicHistory;
    }

    public String getMedicalHistory() {
        return MedicalHistory;
    }

    public void setMedicalHistory(String MedicalHistory) {
        this.MedicalHistory = MedicalHistory;
    }

    public String getHereditaryHistory() {
        return HereditaryHistory;
    }

    public void setHereditaryHistory(String HereditaryHistory) {
        this.HereditaryHistory = HereditaryHistory;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String Weight) {
        this.Weight = Weight;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String Height) {
        this.Height = Height;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }
}
