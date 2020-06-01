package com.proton.runbear.net.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/5/29 15:25
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/29 15:25
 */
public class MeasureBeginResp {

    /**
     * ExamRecordID : 14217
     * PID : 9740
     * GuarderID : 6885
     * Age : 1天11小时
     * RegCode : null
     * ExamType : 1
     * ExamState : 1
     * RegDate : 2020-05-29T11:57:53.347
     * OrgID : 3
     * CreateUserID : -1
     * Source : 0
     * ObjectT : 0081F910540C
     * Data1 : Qqq
     * Data2 : 0
     * Data3 : 2020/5/29 11:57:53
     */
    @SerializedName("ExamRecordID")
    private int examid;
    private int PID;
    private int GuarderID;
    private String Age;
    private Object RegCode;
    private int ExamType;
    private int ExamState;
    private String RegDate;
    private int OrgID;
    private int CreateUserID;
    private int Source;
    private String ObjectT;
    private String Data1;
    private String Data2;
    private String Data3;

    public int getExamid() {
        return examid;
    }

    public void setExamid(int examid) {
        this.examid = examid;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public int getGuarderID() {
        return GuarderID;
    }

    public void setGuarderID(int GuarderID) {
        this.GuarderID = GuarderID;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String Age) {
        this.Age = Age;
    }

    public Object getRegCode() {
        return RegCode;
    }

    public void setRegCode(Object RegCode) {
        this.RegCode = RegCode;
    }

    public int getExamType() {
        return ExamType;
    }

    public void setExamType(int ExamType) {
        this.ExamType = ExamType;
    }

    public int getExamState() {
        return ExamState;
    }

    public void setExamState(int ExamState) {
        this.ExamState = ExamState;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String RegDate) {
        this.RegDate = RegDate;
    }

    public int getOrgID() {
        return OrgID;
    }

    public void setOrgID(int OrgID) {
        this.OrgID = OrgID;
    }

    public int getCreateUserID() {
        return CreateUserID;
    }

    public void setCreateUserID(int CreateUserID) {
        this.CreateUserID = CreateUserID;
    }

    public int getSource() {
        return Source;
    }

    public void setSource(int Source) {
        this.Source = Source;
    }

    public String getObjectT() {
        return ObjectT;
    }

    public void setObjectT(String ObjectT) {
        this.ObjectT = ObjectT;
    }

    public String getData1() {
        return Data1;
    }

    public void setData1(String Data1) {
        this.Data1 = Data1;
    }

    public String getData2() {
        return Data2;
    }

    public void setData2(String Data2) {
        this.Data2 = Data2;
    }

    public String getData3() {
        return Data3;
    }

    public void setData3(String Data3) {
        this.Data3 = Data3;
    }
}
