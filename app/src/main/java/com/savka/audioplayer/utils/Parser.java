package com.savka.audioplayer.utils;

import android.net.Uri;
import android.util.JsonReader;

import com.savka.audioplayer.entity.Song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad-pc on 05.09.2014.
 */
public class Parser {
    final String LOG_TAG = "Parser";

    public List<Song> readJsonStream(Reader in) throws IOException {
        JsonReader reader = new JsonReader(in);
        return readSongArray(reader);

    }

    private List<Song> readSongArray(JsonReader reader) throws IOException {
        List<Song> songs = new ArrayList<Song>();
        reader.beginArray();
        while (reader.hasNext()) {
            songs.add(readSong(reader));
        }
        reader.endArray();
        return songs;
    }

    private Song readSong(JsonReader reader) throws IOException {
        Song song = new Song();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("icon")) {
                song.setIcon(Uri.parse(reader.nextString()));
            } else if (name.equals("name")) {
                song.setTitle(reader.nextString());
            } else if (name.equals("id")) {
                song.setArtist(reader.nextString());
            } else if (name.equals("alias")) {
                song.setAlbum(reader.nextString());
            } else if (name.equals("bitrates")) {
                Bitrates bitrates = readBitrates(reader);
                song.setBitRate(bitrates.getBitrates());
                song.setPath(bitrates.getPath());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return song;
    }

    private Bitrates readBitrates(JsonReader reader) throws IOException {
        Bitrates bitrates = new Bitrates();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("128")) {
                bitrates.setBitrates(128);
                bitrates.setPath(urlReader(reader.nextString()));
            } else if (name.equals("96")) {
                bitrates.setBitrates(96);
                bitrates.setPath(urlReader(reader.nextString()));
            } else if (name.equals("64")) {
                bitrates.setBitrates(64);
                bitrates.setPath(urlReader(reader.nextString()));
            } else if (name.equals("48")) {
                bitrates.setBitrates(48);
                bitrates.setPath(urlReader(reader.nextString()));
            } else if (name.equals("32")) {
                bitrates.setBitrates(32);
                bitrates.setPath(urlReader(reader.nextString()));
            } else if (name.equals("22")) {
                bitrates.setBitrates(22);
                bitrates.setPath(urlReader(reader.nextString()));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return bitrates;
    }

    private Uri urlReader(String url) throws IOException {
        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        inputLine = in.readLine();
        in.close();
        return Uri.parse(inputLine);
    }


    private class Bitrates {
        int Bitrates;
        Uri path;

        public int getBitrates() {
            return Bitrates;
        }

        public void setBitrates(int bitrates) {
            Bitrates = bitrates;
        }

        public Uri getPath() {
            return path;
        }

        public void setPath(Uri path) {
            this.path = path;
        }
    }


}
