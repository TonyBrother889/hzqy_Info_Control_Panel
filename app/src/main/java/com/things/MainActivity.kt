package com.things

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.things.util.GetInternetTimeInMillis
import com.things.util.WriteReadADBShell
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async
import java.lang.Runnable
import android.util.Log
import android.widget.RelativeLayout
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.aliyun.alink.dm.model.RequestModel
import com.aliyun.alink.linkkit.api.LinkKit
import com.aliyun.alink.linksdk.cmp.core.base.AMessage
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper
import com.aliyun.alink.linksdk.tmp.listener.IPublishResourceListener
import com.aliyun.alink.linksdk.tools.AError
import com.things.bluetooth.audio.A2dpSinkActivity
import com.things.util.TimeUtils
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : AppCompatActivity() {

    var TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        init()
        getCpuTempInfo()
        setDownStreamListener()
    }

    /**获取CPU温度*/
    private fun getCpuTempInfo() {

        var timer = Timer()

        val task = object : TimerTask() {

            override fun run() {
                //execute task

                val cpuTemp = WriteReadADBShell.getCPUTemp()

                var timeInfo = TimeUtils.getFormatTime()

                //reportHelloWorld(cpuTemp)

                runOnUiThread(Runnable {

                    tv_info_one.setText(cpuTemp.toString().plus("℃"))

                    if(cpuTemp>50){

                        view_block_one.setBackgroundResource(R.color.colorAccent)

                    }else{
                        view_block_one.setBackgroundResource(R.color.colorPrimary)
                    }

                    tv_data.setText(timeInfo)
                })
            }
        }

        timer.schedule(task, 1000, 1000)
    }

    private fun initView() {

        findViewById<RelativeLayout>(R.id.board_bottom).setOnClickListener {

            var intent=Intent()

            intent.setClass(this, A2dpSinkActivity::class.java)

            startActivity(intent)

        }
    }

    /**初始化*/
    private fun init() {

        ActivityCompat.requestPermissions(this, arrayOf("com.google.android.things.permission.SET_TIME"), 1)

        async {

            var getInternetTimeInMillis = GetInternetTimeInMillis(GetInternetTimeInMillis.SetTimeStateInterface {


            })
        }
    }

    /**
     * 数据上行
     * 上报灯的状态
     */
    fun reportHelloWorld(temp:Double) {

        try {
            val reportData = HashMap<String, ValueWrapper<*>>()

            reportData["Status"] = ValueWrapper.BooleanValueWrapper(1) // 1开 0 关
            reportData["Data"] = ValueWrapper.StringValueWrapper(temp.toString()) // 温度信息
            reportData["CurrentTemperature"] = ValueWrapper.DoubleValueWrapper(temp) // 温度信息
            reportData["Switch"] = ValueWrapper.BooleanValueWrapper(1) // 温度信息

            LinkKit.getInstance().deviceThing.thingPropertyPost(reportData, object : IPublishResourceListener {
                override fun onSuccess(s: String, o: Any) {

                    Log.d(TAG, "onSuccess() called with: s = [$s], o = [$o]")
                    Log.e("reportHelloWorld","上报 Hello, World! 成功。")
                }

                override fun onError(s: String, aError: AError) {
                    Log.d(TAG, "onError() called with: s = [$s], aError = [$aError]")
                    Log.e("reportHelloWorld","上报 Hello, World! 失败。")
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDownStreamListener() {
        LinkKit.getInstance().registerOnPushListener(notifyListener)
    }


    private val notifyListener = object : IConnectNotifyListener {
        override fun onNotify(s: String, s1: String?, aMessage: AMessage) {
            try {
                if (s1 != null && s1.contains("service/property/set")) {
                    val result = String(aMessage.data as ByteArray, "UTF-8" as Charset)
                    val receiveObj = JSONObject.parseObject<RequestModel<String>>(
                        result,
                        object : TypeReference<RequestModel<String>>() {

                        }.type
                    )
                    Log.e("Received a message: ", if (receiveObj == null) "" else receiveObj.params)
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun shouldHandle(s: String, s1: String): Boolean {
            Log.d(TAG, "shouldHandle() called with: s = [$s], s1 = [$s1]")
            return true
        }

        override fun onConnectStateChange(s: String, connectState: ConnectState) {
            Log.d(TAG, "onConnectStateChange() called with: s = [$s], connectState = [$connectState]")

        }
    }






}
