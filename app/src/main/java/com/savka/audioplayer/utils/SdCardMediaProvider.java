package com.savka.audioplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;

import java.util.ArrayList;

/**
 * Created by Admin on 27.08.2014.
 */
public class SdCardMediaProvider extends MediaProvider {

    private static final long serialVersionUID = 7758891816814763067L;
    private ArrayList<Song> songsList = new ArrayList<Song>();
    
    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     */
    public ArrayList<Song> getPlayList(Context context) throws EmptySongsListException {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            Log.e("SdCardMediaProvider", "query failed, cursor = null"); // query failed,
        } else if (!cursor.moveToFirst()) {
            throw new EmptySongsListException();
        } else {
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int yearColumn = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
            do {
                Uri contentUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(idColumn));

                Song song = new Song();
                song.setId(cursor.getLong(idColumn));
                song.setTitle(cursor.getString(titleColumn));
                song.setPath(contentUri.toString());
                song.setArtist(cursor.getString(artistColumn));
                song.setAlbum(cursor.getString(albumColumn));
                song.setYear(cursor.getString(yearColumn));
                //todo set  bitRate
                songsList.add(song);
            } while (cursor.moveToNext());
        }
        return songsList;
    }


}
