package com.things.util;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.things.device.TimeManager;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Created by zhangxiang on 2018/3/12.
 */

public class GetInternetTimeInMillis  {

    private static long ucDate;

    SetTimeStateInterface setTimeStateInterface;

    public GetInternetTimeInMillis(SetTimeStateInterface setTimeStateInterface) {
        super();
        this.setTimeStateInterface = setTimeStateInterface;

        try {
            URL url = new URL("http://www.baidu.com");
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
            setTimeStateInterface.setTimeState(true);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.myLog("获取网络时间异常");
            setTimeStateInterface.setTimeState(false);
        }
    }

    public interface SetTimeStateInterface {
        void setTimeState(boolean state);
    }
}
