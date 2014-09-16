package com.savka.audioplayer.utils;

import android.content.Context;

import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;

import java.util.List;

/**
 * Created by vlad-pc on 03.09.2014.
 */
public interface MediaProvider {
    public List<Song> getPlayList(Context context) throws EmptySongsListException;
}
