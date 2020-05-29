package com.proton.runbear.net.bean;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/5/29 15:29
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/29 15:29
 */
public class MeasureEndResp {

    /**
     * ExamRecordID : 14217
     * PID : 9740
     * GuarderID : 6885
     * Age : 1天11小时
     * RegCode : null
     * ExamType : 1
     * ExamState : 1
     * Weight : null
     * Height : null
     * RegDate : 2020-05-29T11:57:53.347
     * OrgID : 3
     * ObjectT : 0081F910540C
     */

    private int ExamRecordID;
    private int PID;
    private int GuarderID;
    private String Age;
    private Object RegCode;
    private int ExamType;
    private int ExamState;
    private Object Weight;
    private Object Height;
    private String RegDate;
    private int OrgID;
    private String ObjectT;

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

    public Object getWeight() {
        return Weight;
    }

    public void setWeight(Object Weight) {
        this.Weight = Weight;
    }

    public Object getHeight() {
        return Height;
    }

    public void setHeight(Object Height) {
        this.Height = Height;
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

    public String getObjectT() {
        return ObjectT;
    }

    public void setObjectT(String ObjectT) {
        this.ObjectT = ObjectT;
    }
}
