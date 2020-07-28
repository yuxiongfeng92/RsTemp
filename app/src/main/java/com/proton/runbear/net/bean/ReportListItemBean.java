package com.proton.runbear.net.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by MoonlightSW on 2016/11/17.
 */

public class ReportListItemBean implements Serializable {

    /**
     * ReportID : 16357
     * PID : 9918
     * RecordID : 8227
     * ReportType : 0
     * Contents : null
     * CreateUserID : -1
     * CreateDate : null
     * OrgID : 1
     * IsDelete : false
     * CurrentLoginUserId : 0
     * CurrentLoginUserName : null
     * CurrentLoginUserPassword : null
     * CurrentLoginUserMac : null
     * CurrentLoginUserIp : null
     * CurrentLoginUserConnectId : null
     * ErrorCode : null
     * Tag : null
     * ObjectT : null
     * Data1 : 2020/7/1 10:29:39
     * Data2 : 0
     * Data3 : 0
     * Data4 : 15
     * Data5 : 8227
     * Data6 : 哈哈
     * Data7 : null
     * Data8 : null
     * Data9 : null
     */

    private String ReportID;
    private String PID;
    private String RecordID;
    private int ReportType;
    private Object Contents;
    private int CreateUserID;
    private Object CreateDate;
    private int OrgID;
    private boolean IsDelete;
    private int CurrentLoginUserId;
    private Object CurrentLoginUserName;
    private Object CurrentLoginUserPassword;
    private Object CurrentLoginUserMac;
    private Object CurrentLoginUserIp;
    private Object CurrentLoginUserConnectId;
    private Object ErrorCode;
    private Object Tag;
    private Object ObjectT;

    @SerializedName("Data1")
    private String dateTime;
    @SerializedName("Data2")
    private String tempMax;
    @SerializedName("Data3")
    private String tempMin;
    @SerializedName("Data4")
    private long measureLength;
    @SerializedName("Data6")
    private String profileName;
    private Object Data7;
    private Object Data8;
    private Object Data9;

    public String getReportID() {
        return ReportID;
    }

    public void setReportID(String reportID) {
        ReportID = reportID;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getRecordID() {
        return RecordID;
    }

    public void setRecordID(String recordID) {
        RecordID = recordID;
    }

    public int getReportType() {
        return ReportType;
    }

    public void setReportType(int ReportType) {
        this.ReportType = ReportType;
    }

    public Object getContents() {
        return Contents;
    }

    public void setContents(Object Contents) {
        this.Contents = Contents;
    }

    public int getCreateUserID() {
        return CreateUserID;
    }

    public void setCreateUserID(int CreateUserID) {
        this.CreateUserID = CreateUserID;
    }

    public Object getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(Object CreateDate) {
        this.CreateDate = CreateDate;
    }

    public int getOrgID() {
        return OrgID;
    }

    public void setOrgID(int OrgID) {
        this.OrgID = OrgID;
    }

    public boolean isIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(boolean IsDelete) {
        this.IsDelete = IsDelete;
    }

    public int getCurrentLoginUserId() {
        return CurrentLoginUserId;
    }

    public void setCurrentLoginUserId(int CurrentLoginUserId) {
        this.CurrentLoginUserId = CurrentLoginUserId;
    }

    public Object getCurrentLoginUserName() {
        return CurrentLoginUserName;
    }

    public void setCurrentLoginUserName(Object CurrentLoginUserName) {
        this.CurrentLoginUserName = CurrentLoginUserName;
    }

    public Object getCurrentLoginUserPassword() {
        return CurrentLoginUserPassword;
    }

    public void setCurrentLoginUserPassword(Object CurrentLoginUserPassword) {
        this.CurrentLoginUserPassword = CurrentLoginUserPassword;
    }

    public Object getCurrentLoginUserMac() {
        return CurrentLoginUserMac;
    }

    public void setCurrentLoginUserMac(Object CurrentLoginUserMac) {
        this.CurrentLoginUserMac = CurrentLoginUserMac;
    }

    public Object getCurrentLoginUserIp() {
        return CurrentLoginUserIp;
    }

    public void setCurrentLoginUserIp(Object CurrentLoginUserIp) {
        this.CurrentLoginUserIp = CurrentLoginUserIp;
    }

    public Object getCurrentLoginUserConnectId() {
        return CurrentLoginUserConnectId;
    }

    public void setCurrentLoginUserConnectId(Object CurrentLoginUserConnectId) {
        this.CurrentLoginUserConnectId = CurrentLoginUserConnectId;
    }

    public Object getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(Object ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

    public Object getTag() {
        return Tag;
    }

    public void setTag(Object Tag) {
        this.Tag = Tag;
    }

    public Object getObjectT() {
        return ObjectT;
    }

    public void setObjectT(Object ObjectT) {
        this.ObjectT = ObjectT;
    }

    public boolean isDelete() {
        return IsDelete;
    }

    public void setDelete(boolean delete) {
        IsDelete = delete;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public long getMeasureLength() {
        return measureLength;
    }

    public void setMeasureLength(long measureLength) {
        this.measureLength = measureLength;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Object getData7() {
        return Data7;
    }

    public void setData7(Object Data7) {
        this.Data7 = Data7;
    }

    public Object getData8() {
        return Data8;
    }

    public void setData8(Object Data8) {
        this.Data8 = Data8;
    }

    public Object getData9() {
        return Data9;
    }

    public void setData9(Object Data9) {
        this.Data9 = Data9;
    }
}
