package com.proton.runbear.net.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/7/27 10:36
 * @UpdateUser: yxf
 * @UpdateDate: 2020/7/27 10:36
 */
public class ReportDetailBean {

    /**
     * TempRecordID : 7562
     * ExamRecordID : 13774
     * PID : 9228
     * TempData : null
     * HighestTemp : 36.90999984741211
     * LowestTemp : 20.020000457763672
     * StartTime : 2020-02-08T16:09:34.98
     * EndTime : 2020-05-06T16:17:19.653
     * UpperLimit : 0.0
     * LowerLimit : 0.0
     * TotalDuration : 7603665
     * NormalDuration : 46649
     * HighHeatDuration : 0
     * HighTimes : 0
     * LowTimes : 0
     * DeviceBindID : 6236
     * Path : D:\BTMS\1047\T\2020\2\8\9228\2\13774_C464E37AE841.txt
     * CreateUserID : -1
     * CreateDate : 2020-02-08T16:09:34.98
     * PlanTimeLength : null
     * OrgID : 137
     * LowDuration : 0
     * ModerateDuration : 0
     * SuperDuration : 0
     * ModerateTimes : 0
     * SuperTimes : 0
     * CareTime : null
     * CurrentLoginUserId : 0
     * CurrentLoginUserName : null
     * CurrentLoginUserPassword : null
     * CurrentLoginUserMac : null
     * CurrentLoginUserIp : null
     * CurrentLoginUserConnectId : null
     * ErrorCode : null
     * Tag : null
     * ObjectT : null
     * Data1 : 罗再云
     * Data2 : null
     * Data3 : [{"Raw_d":"2020-02-08T16:12:10.86+08:00","Raw_t":24.11,"Raw_r":24.11,"Sate":0},{"Raw_d":"2020-02-08T16:12:16.123+08:00","Raw_t":23.36,"Raw_r":23.36,"Sate":0}]
     * Data4 : 0
     * Data5 :
     * Data6 : null
     * Data7 : null
     * Data8 : null
     * Data9 : null
     */

    private int TempRecordID;
    private int ExamRecordID;
    private int PID;
    private Object TempData;
    private double HighestTemp;
    private double LowestTemp;
    private String StartTime;
    private String EndTime;
    private double UpperLimit;
    private double LowerLimit;
    private int TotalDuration;
    private int NormalDuration;
    private int HighHeatDuration;
    private int HighTimes;
    private int LowTimes;
    private int DeviceBindID;
    private String Path;
    private int CreateUserID;
    private String CreateDate;
    private Object PlanTimeLength;
    private int OrgID;
    private int LowDuration;
    private int ModerateDuration;
    private int SuperDuration;
    private int ModerateTimes;
    private int SuperTimes;
    private Object CareTime;
    private int CurrentLoginUserId;
    private Object CurrentLoginUserName;
    private Object CurrentLoginUserPassword;
    private Object CurrentLoginUserMac;
    private Object CurrentLoginUserIp;
    private Object CurrentLoginUserConnectId;
    private Object ErrorCode;
    private Object Tag;
    private Object ObjectT;
    private String Data1;
    private Object Data2;
    @SerializedName("Data3")
    private String tempSourceJson;
    private String Data4;
    private String Data5;
    private Object Data6;
    private Object Data7;
    private Object Data8;
    private Object Data9;

    public int getTempRecordID() {
        return TempRecordID;
    }

    public void setTempRecordID(int TempRecordID) {
        this.TempRecordID = TempRecordID;
    }

    public int getExamRecordID() {
        return ExamRecordID;
    }

    public void setExamRecordID(int ExamRecordID) {
        this.ExamRecordID = ExamRecordID;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public Object getTempData() {
        return TempData;
    }

    public void setTempData(Object TempData) {
        this.TempData = TempData;
    }

    public double getHighestTemp() {
        return HighestTemp;
    }

    public void setHighestTemp(double HighestTemp) {
        this.HighestTemp = HighestTemp;
    }

    public double getLowestTemp() {
        return LowestTemp;
    }

    public void setLowestTemp(double LowestTemp) {
        this.LowestTemp = LowestTemp;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public double getUpperLimit() {
        return UpperLimit;
    }

    public void setUpperLimit(double UpperLimit) {
        this.UpperLimit = UpperLimit;
    }

    public double getLowerLimit() {
        return LowerLimit;
    }

    public void setLowerLimit(double LowerLimit) {
        this.LowerLimit = LowerLimit;
    }

    public int getTotalDuration() {
        return TotalDuration;
    }

    public void setTotalDuration(int TotalDuration) {
        this.TotalDuration = TotalDuration;
    }

    public int getNormalDuration() {
        return NormalDuration;
    }

    public void setNormalDuration(int NormalDuration) {
        this.NormalDuration = NormalDuration;
    }

    public int getHighHeatDuration() {
        return HighHeatDuration;
    }

    public void setHighHeatDuration(int HighHeatDuration) {
        this.HighHeatDuration = HighHeatDuration;
    }

    public int getHighTimes() {
        return HighTimes;
    }

    public void setHighTimes(int HighTimes) {
        this.HighTimes = HighTimes;
    }

    public int getLowTimes() {
        return LowTimes;
    }

    public void setLowTimes(int LowTimes) {
        this.LowTimes = LowTimes;
    }

    public int getDeviceBindID() {
        return DeviceBindID;
    }

    public void setDeviceBindID(int DeviceBindID) {
        this.DeviceBindID = DeviceBindID;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    public int getCreateUserID() {
        return CreateUserID;
    }

    public void setCreateUserID(int CreateUserID) {
        this.CreateUserID = CreateUserID;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

    public Object getPlanTimeLength() {
        return PlanTimeLength;
    }

    public void setPlanTimeLength(Object PlanTimeLength) {
        this.PlanTimeLength = PlanTimeLength;
    }

    public int getOrgID() {
        return OrgID;
    }

    public void setOrgID(int OrgID) {
        this.OrgID = OrgID;
    }

    public int getLowDuration() {
        return LowDuration;
    }

    public void setLowDuration(int LowDuration) {
        this.LowDuration = LowDuration;
    }

    public int getModerateDuration() {
        return ModerateDuration;
    }

    public void setModerateDuration(int ModerateDuration) {
        this.ModerateDuration = ModerateDuration;
    }

    public int getSuperDuration() {
        return SuperDuration;
    }

    public void setSuperDuration(int SuperDuration) {
        this.SuperDuration = SuperDuration;
    }

    public int getModerateTimes() {
        return ModerateTimes;
    }

    public void setModerateTimes(int ModerateTimes) {
        this.ModerateTimes = ModerateTimes;
    }

    public int getSuperTimes() {
        return SuperTimes;
    }

    public void setSuperTimes(int SuperTimes) {
        this.SuperTimes = SuperTimes;
    }

    public Object getCareTime() {
        return CareTime;
    }

    public void setCareTime(Object CareTime) {
        this.CareTime = CareTime;
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

    public String getData1() {
        return Data1;
    }

    public void setData1(String Data1) {
        this.Data1 = Data1;
    }

    public Object getData2() {
        return Data2;
    }

    public void setData2(Object Data2) {
        this.Data2 = Data2;
    }

    public String getTempSourceJson() {
        return tempSourceJson;
    }

    public void setTempSourceJson(String tempSourceJson) {
        this.tempSourceJson = tempSourceJson;
    }

    public String getData4() {
        return Data4;
    }

    public void setData4(String Data4) {
        this.Data4 = Data4;
    }

    public String getData5() {
        return Data5;
    }

    public void setData5(String Data5) {
        this.Data5 = Data5;
    }

    public Object getData6() {
        return Data6;
    }

    public void setData6(Object Data6) {
        this.Data6 = Data6;
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
