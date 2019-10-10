package com.things.bluetooth.audio;

import android.service.notification.NotificationListenerService;
import android.media.RemoteController;
import android.util.Log;

public class MusicStateListener extends NotificationListenerService implements RemoteController.OnClientUpdateListener {

    @Override
    public void onClientChange(boolean b) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int i) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int i, long l, long l1, float v) {

    }

    @Override
    public void onClientTransportControlUpdate(int i) {

    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {


        Log.e("bbb", metadataEditor.toString());

    }
}
