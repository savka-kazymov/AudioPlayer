package com.savka.audioplayer.utils;

import android.app.Application;
import android.content.Context;

import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vlad-pc on 03.09.2014.
 */
public abstract class MediaProvider extends Application implements Serializable {

    private static Context context;

    public abstract ArrayList<Song> getPlayList(Context context) throws EmptySongsListException;


    @Override
    public void onCreate() {
        super.onCreate();
        MediaProvider.context = getApplicationContext();
    }

    public static Context getStaticContext() {
        return MediaProvider.context;
    }
}
