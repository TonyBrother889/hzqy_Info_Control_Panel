/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.things.bluetooth.audio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import com.google.android.things.bluetooth.BluetoothProfileManager;
import com.things.R;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class A2dpSinkActivity extends Activity {

    private static final String TAG = "A2dpSinkActivity";

    private static final String ADAPTER_FRIENDLY_NAME = "My Android Things device";
    private static final int DISCOVERABLE_TIMEOUT_MS = 300;
    private static final int REQUEST_CODE_ENABLE_DISCOVERABLE = 100;

    private static final String UTTERANCE_ID =
            "com.example.androidthings.bluetooth.audio.UTTERANCE_ID";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothProfile mA2DPSinkProxy;

//    private ButtonInputDriver mPairingButtonDriver;
    //   private ButtonInputDriver mDisconnectAllButtonDriver;

    private TextToSpeech mTtsEngine;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.e("nn", msg.toString());

        }
    };


    private final BroadcastReceiver mAdapterStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int oldState = A2dpSinkHelper.getPreviousAdapterState(intent);
            int newState = A2dpSinkHelper.getCurrentAdapterState(intent);
            Log.d(TAG, "Bluetooth Adapter changing state from " + oldState + " to " + newState);
            if (newState == BluetoothAdapter.STATE_ON) {
                Log.i(TAG, "Bluetooth Adapter is ready");
                initA2DPSink();
            }
        }
    };

    private final BroadcastReceiver mSinkProfileStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(A2dpSinkHelper.ACTION_CONNECTION_STATE_CHANGED)) {
                int oldState = A2dpSinkHelper.getPreviousProfileState(intent);
                int newState = A2dpSinkHelper.getCurrentProfileState(intent);
                BluetoothDevice device = A2dpSinkHelper.getDevice(intent);
                Log.d(TAG, "Bluetooth A2DP sink changing connection state from " + oldState +
                        " to " + newState + " device " + device);
                if (device != null) {
                    String deviceName = Objects.toString(device.getName(), "a device");
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        speak("Connected to " + deviceName);
                        registerRemoteController();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        speak("Disconnected from " + deviceName);
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mSinkProfilePlaybackChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(A2dpSinkHelper.ACTION_PLAYING_STATE_CHANGED)) {

                int oldState = A2dpSinkHelper.getPreviousProfileState(intent);
                int newState = A2dpSinkHelper.getCurrentProfileState(intent);
                BluetoothDevice device = A2dpSinkHelper.getDevice(intent);
                Log.d(TAG, "Bluetooth A2DP sink changing playback state from " + oldState +
                        " to " + newState + " device " + device);
                if (device != null) {
                    if (newState == A2dpSinkHelper.STATE_PLAYING) {
                        Log.i(TAG, "Playing audio from device " + device.getAddress());
                    } else if (newState == A2dpSinkHelper.STATE_NOT_PLAYING) {
                        Log.i(TAG, "Stopped playing audio from " + device.getAddress());
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sound_info);

        initView();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "No default Bluetooth adapter. Device likely does not support bluetooth.");
            return;
        }

        // We use Text-to-Speech to indicate status change to the user
        initTts();

        registerReceiver(mAdapterStateChangeReceiver, new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mSinkProfileStateChangeReceiver, new IntentFilter(
                A2dpSinkHelper.ACTION_CONNECTION_STATE_CHANGED));
        registerReceiver(mSinkProfilePlaybackChangeReceiver, new IntentFilter(
                A2dpSinkHelper.ACTION_PLAYING_STATE_CHANGED));

        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth Adapter is already enabled.");
            initA2DPSink();
        } else {
            Log.d(TAG, "Bluetooth adapter not enabled. Enabling.");
            mBluetoothAdapter.enable();
        }

    }

    private void initView() {

        findViewById(R.id.bt_bluetooth_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_P:
                // Enable Pairing mode (discoverable)
                enableDiscoverable();
                return true;
            case KeyEvent.KEYCODE_D:
                // Disconnect any currently connected devices
                disconnectConnectedDevices();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

  /*      try {
            if (mPairingButtonDriver != null) mPairingButtonDriver.close();
        } catch (IOException e) { *//* close quietly *//*}
        try {
            if (mDisconnectAllButtonDriver != null) mDisconnectAllButtonDriver.close();
        } catch (IOException e) { *//* close quietly *//*}*/

        unregisterReceiver(mAdapterStateChangeReceiver);
        unregisterReceiver(mSinkProfileStateChangeReceiver);
        unregisterReceiver(mSinkProfilePlaybackChangeReceiver);

        if (mA2DPSinkProxy != null) {
            mBluetoothAdapter.closeProfileProxy(A2dpSinkHelper.A2DP_SINK_PROFILE, mA2DPSinkProxy);
        }

        if (mTtsEngine != null) {
            mTtsEngine.stop();
            mTtsEngine.shutdown();
        }
    }

    private void setupBTProfiles() {

        BluetoothProfileManager bluetoothProfileManager = BluetoothProfileManager.getInstance();
        List<Integer> enabledProfiles = bluetoothProfileManager.getEnabledProfiles();
        if (!enabledProfiles.contains(A2dpSinkHelper.A2DP_SINK_PROFILE)) {
            Log.d(TAG, "Enabling A2dp sink mode.");
            List<Integer> toDisable = Arrays.asList(BluetoothProfile.A2DP);
            List<Integer> toEnable = Arrays.asList(
                    A2dpSinkHelper.A2DP_SINK_PROFILE,
                    A2dpSinkHelper.AVRCP_CONTROLLER_PROFILE);
            bluetoothProfileManager.enableAndDisableProfiles(toEnable, toDisable);
        } else {
            Log.d(TAG, "A2dp sink profile is enabled.");
        }
    }

    /**
     * Initiate the A2DP sink.
     */
    private void initA2DPSink() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth adapter not available or not enabled.");
            return;
        }
        setupBTProfiles();
        Log.d(TAG, "Set up Bluetooth Adapter name and profile");
        mBluetoothAdapter.setName(ADAPTER_FRIENDLY_NAME);
        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                mA2DPSinkProxy = proxy;
                enableDiscoverable();

                Log.e("bbb", proxy.getConnectedDevices().toString());
            }

            @Override
            public void onServiceDisconnected(int profile) {
            }
        }, A2dpSinkHelper.A2DP_SINK_PROFILE);

        //  configureButton();


    }

    /**
     * Enable the current {@link BluetoothAdapter} to be discovered (available for pairing) for
     * the next {@link #DISCOVERABLE_TIMEOUT_MS} ms.
     */
    private void enableDiscoverable() {
        Log.d(TAG, "Registering for discovery.");
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                DISCOVERABLE_TIMEOUT_MS);
        startActivityForResult(discoverableIntent, REQUEST_CODE_ENABLE_DISCOVERABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_DISCOVERABLE) {
            Log.d(TAG, "Enable discoverable returned with result " + resultCode);

            // ResultCode, as described in BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE, is either
            // RESULT_CANCELED or the number of milliseconds that the device will stay in
            // discoverable mode. In a regular Android device, the user will see a popup requesting
            // authorization, and if they cancel, RESULT_CANCELED is returned. In Android Things,
            // on the other hand, the authorization for pairing is always given without user
            // interference, so RESULT_CANCELED should never be returned.
            if (resultCode == RESULT_CANCELED) {
                Log.e(TAG, "Enable discoverable has been cancelled by the user. " +
                        "This should never happen in an Android Things device.");
                return;
            }
            Log.i(TAG, "Bluetooth adapter successfully set to discoverable mode. " +
                    "Any A2DP source can find it with the name " + ADAPTER_FRIENDLY_NAME +
                    " and pair for the next " + DISCOVERABLE_TIMEOUT_MS + " ms. " +
                    "Try looking for it on your phone, for example.");

            // There is nothing else required here, since Android framework automatically handles
            // A2DP Sink. Most relevant Bluetooth events, like connection/disconnection, will
            // generate corresponding broadcast intents or profile proxy events that you can
            // listen to and react appropriately.

            speak("Bluetooth audio sink is discoverable for " + DISCOVERABLE_TIMEOUT_MS +
                    " milliseconds. Look for a device named " + ADAPTER_FRIENDLY_NAME);

        }
    }

    private void disconnectConnectedDevices() {
        if (mA2DPSinkProxy == null || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }
        speak("Disconnecting devices");
        for (BluetoothDevice device : mA2DPSinkProxy.getConnectedDevices()) {
            Log.i(TAG, "Disconnecting device " + device);
            A2dpSinkHelper.disconnect(mA2DPSinkProxy, device);
        }
    }

 /*   private void configureButton() {
        try {
            mPairingButtonDriver = new ButtonInputDriver(BoardDefaults.getGPIOForPairing(),
                    Button.LogicState.PRESSED_WHEN_LOW, KeyEvent.KEYCODE_P);
            mPairingButtonDriver.register();
            mDisconnectAllButtonDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForDisconnectAllBTDevices(),
                    Button.LogicState.PRESSED_WHEN_LOW, KeyEvent.KEYCODE_D);
            mDisconnectAllButtonDriver.register();
        } catch (IOException e) {
            Log.w(TAG, "Could not register GPIO button drivers. Use keyboard events to trigger " +
                    "the functions instead", e);
        }
    }*/

    private void initTts() {
        mTtsEngine = new TextToSpeech(A2dpSinkActivity.this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            mTtsEngine.setLanguage(Locale.US);
                        } else {
                            Log.w(TAG, "Could not open TTS Engine (onInit status=" + status
                                    + "). Ignoring text to speech");
                            mTtsEngine = null;
                        }
                    }
                });
    }

    private void speak(String utterance) {
        Log.i(TAG, utterance);
        if (mTtsEngine != null) {
            mTtsEngine.speak(utterance, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
        }
    }


    public void registerRemoteController() {

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.registerAudioRecordingCallback(new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                super.onRecordingConfigChanged(configs);

                for (int i = 0; i < configs.size(); i++) {

                    Log.e("bb", configs.get(i).toString());
                }

            }
        }, handler);

        audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);

                for (int i = 0; i < addedDevices.length; i++) {

                    addedDevices[i].toString();
                }

            }
        },handler);

        audioManager.registerAudioRecordingCallback(new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                super.onRecordingConfigChanged(configs);

                for (int i = 0; i < configs.size(); i++) {

                    Log.e("bb", configs.get(i).toString());
                }

            }
        }, handler);


    }


    private void contralAudio() {





       /* MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象mmr

        mmr.setDataSource();//设置mmr对象的数据源为上面file对象的绝对路径
        String ablumString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);//获得音乐专辑的标题
        String artistString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//获取音乐的艺术家信息
        String titleString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);//获取音乐标题信息
        String mimetypeString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);//获取音乐mime类型
        String durationString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取音乐持续时间
        String bitrateString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);//获取音乐比特率，位率
        String dateString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);//获取音乐的日期
        *//* 设置文本的内容 *//*
         *//* ablum.setText("专辑标题为："+ablumString);
        artist.setText("艺术家名称为："+artistString);
        title.setText("音乐标题为："+titleString);
        mimetype.setText("音乐的MIME类型为："+mimetypeString);
        duration.setText("duration为："+durationString);
        bitrate.setText("bitrate为："+bitrateString);
        date.setText("date为："+dateString);

*//*

       Log.e("vvv", "专辑标题为："+ablumString+"艺术家名称为："+artistString+"音乐标题为："+titleString
       +"音乐的MIME类型为："+mimetypeString+ "bitrate为："+bitrateString+"date为："+dateString);*/
    }
}
