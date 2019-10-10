package com.things.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author 作者：张祥 on 2018/2/2 0002.
 *         邮箱：847874028@qq.com
 *         版本：v1.0
 *         功能：
 *         <p>
 *         读取树莓派CPU温度：cat /sys/class/thermal/thermal_zone0/temp
 */

public class WriteReadADBShell {

    public static String read(String sys_path) {
        try {
            Runtime runtime = Runtime.getRuntime();
            /**此处进行读操作*/
            Process process = runtime.exec("cat " + sys_path);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while (null != (line = br.readLine())) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("adb-shell", "*** ERROR *** Here is what I know: " + e.getMessage());
        }
        return null;
    }

    /**
     * adb shell 读取系统温度
     */
    public static String read() {
        try {
            Runtime runtime = Runtime.getRuntime();
            /**此处进行读操作*/
            Process process = runtime.exec("cat " + "/sys/class/thermal/thermal_zone0/temp");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while (null != (line = br.readLine())) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("adb-shell", "*** ERROR *** Here is what I know: " + e.getMessage());
        }
        return null;
    }

    public static double getCPUTemp() {
        return Integer.parseInt(read()) / 1000.0;
    }


}
