package com.things.log;

import android.util.Log;


public class LogUtil
{
    public static final int LOG_LEVEL_NONE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARN = 3;
    public static final int LOG_LEVEL_ERROR = 4;
    public static final int LOG_LEVEL_ALL = 5;

    private static int mLogLevel = LOG_LEVEL_NONE;

    public static void d(String tag, String msg)
    {
        if (getLogLevel() >= LOG_LEVEL_DEBUG)
        {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg)
    {
        if (getLogLevel() >= LOG_LEVEL_INFO)
        {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg)
    {
        if (getLogLevel() >= LOG_LEVEL_WARN)
        {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg)
    {
        if (getLogLevel() >= LOG_LEVEL_ERROR)
        {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg)
    {
        if (getLogLevel() >= LOG_LEVEL_ALL)
        {
            Log.v(tag, msg);
        }
    }

   
    public static int getLogLevel()
    {
        return mLogLevel;
    }


    public static void setLogLevel(int level)
    {
        LogUtil.mLogLevel = level;
    }

}
