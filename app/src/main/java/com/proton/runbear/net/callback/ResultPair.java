package com.proton.runbear.net.callback;


import com.proton.runbear.utils.Constants;

public class ResultPair {

    private String Data = "";
    private String ErrorMessage = Constants.FAIL;
    private boolean Success;
    private int Code;

    public ResultPair() {
    }

    public ResultPair(String data, String errorMessage, boolean success, int code) {
        Data = data;
        ErrorMessage = errorMessage;
        Success = success;
        Code = code;
    }

    public boolean isSuccess() {
        return Success;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getRet() {
        return ErrorMessage;
    }


    public String getData() {
        return Data;
    }

    public void setData(String data) {
        this.Data = data;
    }

    @Override
    public String toString() {
        return "ResultPair{" +
                "Data='" + Data + '\'' +
                ", ErrorMessage='" + ErrorMessage + '\'' +
                '}';
    }
}
