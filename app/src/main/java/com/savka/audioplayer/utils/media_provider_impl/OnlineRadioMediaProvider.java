package com.savka.audioplayer.utils.media_provider_impl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;
import com.savka.audioplayer.utils.MediaProvider;
import com.savka.audioplayer.utils.Parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by vlad-pc on 05.09.2014.
 */
public class OnlineRadioMediaProvider extends AsyncTask<String, Void, List> implements MediaProvider {
    final String LOG_TAG = "OnlineRadioMediaProvider";
    final String BROADCASTING_STATIONS_URL = "http://audio.rambler.ru/json/stations.js";

    @Override
    public List<Song> getPlayList(Context context) throws EmptySongsListException {
        List songList = null;
        try {
            songList = this.execute(BROADCASTING_STATIONS_URL).get();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Current thread was interrupted while waiting.", e);
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, "Computation threw an exception", e);
        }
        if (songList == null) {
            throw new EmptySongsListException();
        } else {
            return songList;
        }
    }

    @Override
    protected List<Song> doInBackground(String[] urls) {
        URLConnection connection = null;
        InputStream response = null;
        List<Song> songs = null;
        try {
            connection = new URL(urls[0]).openConnection();
            response = connection.getInputStream();
            Reader reader = filterInputStream(response);
            songs = new Parser().readJsonStream(reader);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fail to read from " + urls[0], e);
        }

        return songs;
    }

    // private members
    private Reader filterInputStream(InputStream response) throws IOException {
        response.skip(29);
        ByteArrayOutputStream read = read(response);
        return new InputStreamReader(new ByteArrayInputStream(read.toByteArray(), 0, read.size() - 2));
    }

    private ByteArrayOutputStream read(InputStream in) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
