package com.things.util;

import android.util.Log;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 作者：张祥 on 2018/3/5 0005.
 *         邮箱：847874028@qq.com
 *         版本：v1.0
 *         功能：
 */

public class GetCpuTemperature {

    GetCPUTempInfoInterface getCPUTempInfoInterface;

    public GetCpuTemperature(GetCPUTempInfoInterface getCPUTempInfoInterface) {
        super();
        this.getCPUTempInfoInterface = getCPUTempInfoInterface;
        getDeviceTemplate(1);
    }

    /**
     * 获取CPU温度信息
     */
    public void getDeviceTemplate(int seconds) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        long initialDelay = 1;
        // 从现在开始1秒钟之后，每隔1秒钟执行一次job1
        scheduledExecutorService.scheduleAtFixedRate(new RemindTask(), initialDelay, seconds, TimeUnit.SECONDS);
    }


    class RemindTask implements Runnable {
        @Override
        public void run() {
            String read = WriteReadADBShell.read("/sys/class/thermal/thermal_zone0/temp");
            final double temp = Integer.parseInt(read) / 1000.0;
            Log.d("temp", temp + "");
            getCPUTempInfoInterface.getCpuTempDoubleInfo(temp);

            // getNetTime();
        /*    Message message = new Message();
            message.obj = temp;
          //  myHandler.sendMessage(message);
            JSONObject jsonObject = new JSONObject();
            try {
                String temperatureJSON = jsonObject.put("temperature", temp).toString();
                MqttMessage mqttMessage = new MqttMessage(temperatureJSON.getBytes());
                //  ttsOutput.startPlaySuond(temp+"", GpioControlActivity.this);
                mThingsboardMqttClient.publish("v1/devices/me/attributes", mqttMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }


    public interface GetCPUTempInfoInterface {

        /**
         * 异步获取的温度信息
         */
        void getCpuTempDoubleInfo(double tempInfo);
    }
}
