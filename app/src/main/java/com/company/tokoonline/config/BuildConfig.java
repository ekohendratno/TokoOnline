package com.company.tokoonline.config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public final class BuildConfig {
    public int VERSION_CODE = 0;
    public String VERSION_NAME;

    public BuildConfig(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.VERSION_NAME = pInfo.versionName;   //version name
            this.VERSION_CODE = pInfo.versionCode;      //version code
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}