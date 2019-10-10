package com.things.util;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    Application mApplication;

    public MyUncaughtExceptionHandler(Application application) {

        this.mApplication = application;


    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        // 保存错误日志
      //  saveCatchInfo2File(throwable);

        // 1秒钟后重启应用
        //AlarmManager mgr = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
        // mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称
     */
  /*  private String saveCatchInfo2File(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String sb = writer.toString();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String time = formatter.format(new Date());
            String fileName = time + ".txt";
            System.out.println("fileName:" + fileName);
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String filePath = Environment.getExternalStorageDirectory() + "/HKDownload/" + packgeName
                        + "/crash/";
                File dir = new File(filePath);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        // 创建目录失败: 一般是因为SD卡被拔出了
                        return "";
                    }
                }
                System.out.println("filePath + fileName:" + filePath + fileName);
                FileOutputStream fos = new FileOutputStream(filePath + fileName);
                fos.write(sb.getBytes());
                fos.close();
                //文件保存完了之后,在应用下次启动的时候去检查错误日志,发现新的错误日志,就发送给开发者
            }
            return fileName;
        } catch (Exception e) {
            System.out.println("an error occured while writing file..." + e.getMessage());
        }
        return null;
    }*/

}
