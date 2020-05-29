package com.proton.runbear.enums;

/**
 * @Description: 出库类型
 * @Author: yxf
 * @CreateDate: 2020/5/29 11:01
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/29 11:01
 */
public enum OutType {
    SELL(1, "销售"),
    RENT(2, "租赁"),
    RECEIVE(3, "领用");
    private int type;
    private String desc;

    OutType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static OutType getOutTypeByType(int type) {
        for (OutType outType :
                values()) {
            if (type == outType.getType()) {
                return outType;
            }
        }
        return SELL;
    }
}
