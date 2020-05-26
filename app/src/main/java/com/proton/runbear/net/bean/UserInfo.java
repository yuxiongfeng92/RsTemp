package com.proton.runbear.net.bean;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/5/26 14:11
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/26 14:11
 */
public class UserInfo {

    /**
     * Account : {"GuarderID":6885,"Phone":"18186121932","Name":"yxf","Sex":null,"BirthDate":null,"OpenID":"","SmsCode":"804268","SmsTime":null,"IsVaild":null,"OrgName":null,"OrgID":-1,"IsDelete":false,"HeadImgurl":null,"Nickname":null,"City":null,"LastSessionTime":null,"SmallProgramOpenID":"1590473196778wx-app-register","SmallProgramIsVaild":true,"UnionID":"","CurrentLoginUserId":0,"CurrentLoginUserName":null,"CurrentLoginUserPassword":null,"CurrentLoginUserMac":null,"CurrentLoginUserIp":null,"CurrentLoginUserConnectId":null,"ErrorCode":null,"Tag":null,"ObjectT":null,"Data1":null,"Data2":null,"Data3":null,"Data4":null,"Data5":null,"Data6":null,"Data7":null,"Data8":null,"Data9":null}
     * Token : a08a72f66c63453da71f5bb73bfac986
     */

    private AccountBean Account;
    private String Token;

    public AccountBean getAccount() {
        return Account;
    }

    public void setAccount(AccountBean Account) {
        this.Account = Account;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }

    public static class AccountBean {
        /**
         * GuarderID : 6885
         * Phone : 18186121932
         * Name : yxf
         * Sex : null
         * BirthDate : null
         * OpenID :
         * SmsCode : 804268
         * SmsTime : null
         * IsVaild : null
         * OrgName : null
         * OrgID : -1
         * IsDelete : false
         * HeadImgurl : null
         * Nickname : null
         * City : null
         * LastSessionTime : null
         * SmallProgramOpenID : 1590473196778wx-app-register
         * SmallProgramIsVaild : true
         * UnionID :
         * CurrentLoginUserId : 0
         * CurrentLoginUserName : null
         * CurrentLoginUserPassword : null
         * CurrentLoginUserMac : null
         * CurrentLoginUserIp : null
         * CurrentLoginUserConnectId : null
         * ErrorCode : null
         */

        private int GuarderID;
        private String Phone;
        private String Name;
        private Object Sex;
        private Object BirthDate;
        private String OpenID;
        private String SmsCode;
        private Object SmsTime;
        private Object IsVaild;
        private Object OrgName;
        private int OrgID;
        private boolean IsDelete;
        private Object HeadImgurl;
        private Object Nickname;
        private Object City;
        private Object LastSessionTime;
        private String SmallProgramOpenID;
        private boolean SmallProgramIsVaild;
        private String UnionID;
        private int CurrentLoginUserId;
        private Object CurrentLoginUserName;
        private Object CurrentLoginUserPassword;
        private Object CurrentLoginUserMac;
        private Object CurrentLoginUserIp;
        private Object CurrentLoginUserConnectId;
        private Object ErrorCode;

        public int getGuarderID() {
            return GuarderID;
        }

        public void setGuarderID(int GuarderID) {
            this.GuarderID = GuarderID;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String Phone) {
            this.Phone = Phone;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public Object getSex() {
            return Sex;
        }

        public void setSex(Object Sex) {
            this.Sex = Sex;
        }

        public Object getBirthDate() {
            return BirthDate;
        }

        public void setBirthDate(Object BirthDate) {
            this.BirthDate = BirthDate;
        }

        public String getOpenID() {
            return OpenID;
        }

        public void setOpenID(String OpenID) {
            this.OpenID = OpenID;
        }

        public String getSmsCode() {
            return SmsCode;
        }

        public void setSmsCode(String SmsCode) {
            this.SmsCode = SmsCode;
        }

        public Object getSmsTime() {
            return SmsTime;
        }

        public void setSmsTime(Object SmsTime) {
            this.SmsTime = SmsTime;
        }

        public Object getIsVaild() {
            return IsVaild;
        }

        public void setIsVaild(Object IsVaild) {
            this.IsVaild = IsVaild;
        }

        public Object getOrgName() {
            return OrgName;
        }

        public void setOrgName(Object OrgName) {
            this.OrgName = OrgName;
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

        public Object getHeadImgurl() {
            return HeadImgurl;
        }

        public void setHeadImgurl(Object HeadImgurl) {
            this.HeadImgurl = HeadImgurl;
        }

        public Object getNickname() {
            return Nickname;
        }

        public void setNickname(Object Nickname) {
            this.Nickname = Nickname;
        }

        public Object getCity() {
            return City;
        }

        public void setCity(Object City) {
            this.City = City;
        }

        public Object getLastSessionTime() {
            return LastSessionTime;
        }

        public void setLastSessionTime(Object LastSessionTime) {
            this.LastSessionTime = LastSessionTime;
        }

        public String getSmallProgramOpenID() {
            return SmallProgramOpenID;
        }

        public void setSmallProgramOpenID(String SmallProgramOpenID) {
            this.SmallProgramOpenID = SmallProgramOpenID;
        }

        public boolean isSmallProgramIsVaild() {
            return SmallProgramIsVaild;
        }

        public void setSmallProgramIsVaild(boolean SmallProgramIsVaild) {
            this.SmallProgramIsVaild = SmallProgramIsVaild;
        }

        public String getUnionID() {
            return UnionID;
        }

        public void setUnionID(String UnionID) {
            this.UnionID = UnionID;
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

    }
}
