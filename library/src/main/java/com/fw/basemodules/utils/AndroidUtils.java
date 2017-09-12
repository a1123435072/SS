package com.fw.basemodules.utils;

import android.content.Context;

public class AndroidUtils {

    public static String getAppName(Context ctx) {
        return ctx.getApplicationInfo().loadLabel(ctx.getPackageManager()).toString();
    }
}
