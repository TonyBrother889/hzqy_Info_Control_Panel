package com.things.util;

import android.os.AsyncTask;

import com.google.android.things.device.TimeManager;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Created by zhangxiang on 2018/3/12.
 */

public class GetInternetTimeInMillisAnsy extends AsyncTask<String, Integer, Boolean> {

    private static long ucDate;

    SetTimeStateInterface setTimeStateInterface;

    public GetInternetTimeInMillisAnsy(SetTimeStateInterface setTimeStateInterface) {
        super();
        this.setTimeStateInterface = setTimeStateInterface;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            //取得网站日期时间
            ucDate = uc.getDate();
            Utils.myLog(ucDate + "");
            TimeManager timeManager = TimeManager.getInstance();
            timeManager.setTimeFormat(TimeManager.FORMAT_24);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(TimeUtils.getInternetTime());
            long timeStamp = calendar.getTimeInMillis();
            timeManager.setTime(timeStamp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Utils.myLog("获取网络时间异常");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        setTimeStateInterface.setTimeState(aBoolean);
    }

    public interface SetTimeStateInterface {
        void setTimeState(boolean state);
    }
}
