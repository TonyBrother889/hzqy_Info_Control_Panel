package com.things;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;

public class PlayMusicActivity extends Activity {


    String mcUrl = "http://m10.music.126.net/20190925011835/1c5dcc50d6c629883041a5d2c67fd58d/ymusic/23ba/1af9/aef3/02eb5011ddedda48195a2dd3b245a1bd.mp3";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sound_info);



            Uri uri= Uri.parse(mcUrl);

            MediaPlayer mediaPlayer = MediaPlayer.create(this, uri);

            mediaPlayer.start();

    }
}
