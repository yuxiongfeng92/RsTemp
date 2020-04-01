package com.proton.runbear.socailauth.listener;


import com.proton.runbear.socailauth.PlatformType;


public interface ShareListener {
    void onComplete(PlatformType platform_type);

    void onError(PlatformType platform_type, String err_msg);

    void onCancel(PlatformType platform_type);
}
