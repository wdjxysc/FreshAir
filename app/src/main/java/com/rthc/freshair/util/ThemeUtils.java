package com.rthc.freshair.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.view.WindowManager;

import com.rthc.freshair.R;

/**
 * User:Shine
 * Date:2015-11-20
 * Description:
 */
public class ThemeUtils {

    private static final int[] APPCOMPAT_CHECK_ATTRS = {R.attr.colorPrimary};

    public static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }


    /**
     * 设置状态栏透明
     * @param activity
     */
    public static void setStatusBarTranslucent(Activity activity) {
        // 如果版本在4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
