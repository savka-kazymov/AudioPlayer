package com.savka.audioplayer.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by vlad-pc on 03.09.2014.
 */
public class Song implements Serializable{

    private static final long serialVersionUID = -8710807837751591298L;

    private long id;
    private long ownerId;
    private String title;
    private String path;
    private String artist;
    private int duration;
    private long lyricsId;
    private int genreId;
    private String album;
    private String year;
    private String bitRate;


    public static Song parse(JSONObject json) throws JSONException{
        Song song = new Song();
        song.id = json.getLong("id");
        song.ownerId = json.getLong("owner_id");
        song.title = json.getString("title");
        song.artist = json.getString("artist");
        song.duration = json.getInt("duration");
        song.path = json.getString("url");


        return song;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getLyricsId() {
        return lyricsId;
    }

    public void setLyricsId(long lyricsId) {
        this.lyricsId = lyricsId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object)this).getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (id != song.id) return false;
        if (path != null ? !path.equals(song.path) : song.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return artist + "-" + title;
    }
}
