package com.things.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;



import java.util.regex.Pattern;

/**
 * @author 作者：张祥 on 2018/3/5 0005.
 *         邮箱：847874028@qq.com
 *         版本：v1.0
 *         功能：
 */

public class Utils {

    private static Toast mToast;
    /**获取Iemi*/
    public static String getImei(Activity activity, String imei) {
        String ret = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getDeviceId();
        } catch (Exception e) {
            myLog(e.getMessage());
        }
        if (isReadableASCII(ret)){
            return ret;
        } else {
            return imei;
        }
    }


    private static boolean isReadableASCII(CharSequence string){
        if (TextUtils.isEmpty(string)) return false;
        try {
            Pattern p = Pattern.compile("[\\x20-\\x7E]+");
            return p.matcher(string).matches();
        } catch (Throwable e){
            return true;
        }
    }

    /**
     * 退出当前应用
     */
    public static void exitApp(Activity activity) {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        wakeLock.release();
    }

    /**
     * * 获得系统亮度
     * *
     * * @return
     */
    public static int getSystemBrightness(Activity activity) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    public static void setScrennManualMode(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */
    public static void changeAppBrightness(Activity activity, int brightness) {

        myLog(brightness + "");
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        }
        window.setAttributes(lp);
    }

//    public void saveBrightness(Activity activity, int brightness) {
//        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
//        Settings.System.putInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
//        activity.getContentResolver().notifyChange(uri, null);
//    }

    /**
     * 自定义LOG
     */
    public static void myLog(String logInfo) {
        Log.e("-----------MQTT-------------", logInfo);
    }


    public static void toastShow(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(message);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        });
    }
}
