package com.proton.runbear.enums;

/**
 * @Description: 设备的绑定状态枚举
 * @Author: yxf
 * @CreateDate: 2020/5/29 10:56
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/29 10:56
 */
public enum BindStatus {
    UN_BIND(1, "未绑定"),
    MATCH(2, "仅设备与设备配对"),
    MATCH_BIND(3, "设备已配对且与用户绑定"),
    RELIEVE_BIND(4, "已解绑");
    private int type;
    private String desc;

    BindStatus(int type, String desc) {
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

    public static BindStatus getBindStatusByType(int type) {
        for (BindStatus status : values()) {
            if (type == status.getType()) {
                return status;
            }
        }
        return UN_BIND;
    }
}
