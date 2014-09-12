package com.savka.audioplayer.utils;

import android.content.Context;

import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;

import java.util.ArrayList;

/**
 * Created by naz on 9/10/2014.
 */
public class VkMediaProvider extends MediaProvider {

    private static final long serialVersionUID = 7126786942124878541L;
    public ArrayList<Song> songs;

    public VkMediaProvider(ArrayList<Song> songs) {
        this.songs = songs;
    }


    @Override
    public ArrayList<Song> getPlayList(Context context) throws EmptySongsListException {

        return getSongs();

    }




    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
