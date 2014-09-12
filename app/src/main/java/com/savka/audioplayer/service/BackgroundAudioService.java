package com.savka.audioplayer.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.savka.audioplayer.R;
import com.savka.audioplayer.activity.MainActivity;
import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;
import com.savka.audioplayer.utils.MediaProvider;
import com.savka.audioplayer.utils.SdCardMediaProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Admin on 28.08.2014.
 */
public class BackgroundAudioService extends Service implements MediaPlayer.OnCompletionListener {

    final String LOG_TAG = "BackgroundAudioService";

    private Handler mHandler;

    private int currentSongIndex = 0;
    private int currentSongPosition = 0;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private MediaProvider mediaProvider;
    private ArrayList<Song> songsList;
    static MediaPlayer mp;
    MyBinder binder = new MyBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
        stopForeground(true);
        mp.release();
        mp = null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "onStartCommand");
        mHandler = new Handler();

        // Mediaplayer
        mp = new MediaPlayer();
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);// todo create Wi-Fi lock?
        //listeners
        mp.setOnCompletionListener(this);

        mediaProvider = new SdCardMediaProvider();

        // Getting all songs list
        try {
            songsList = mediaProvider.getPlayList(getApplicationContext());

            // play current song
            playSong(currentSongIndex);
        } catch (EmptySongsListException e) {
            Log.e(LOG_TAG, "EmptySongsListException. Add songs to card");// todo make UI massage
            createErrorMassage();
        }

        return START_STICKY;
    }

    //button handlers
    public boolean btnPlay() {
        if (mp.isPlaying()) {
            mp.pause();
            return false;
        } else {
            mp.start();
            return true;
        }
    }

    public void btnForward() {
        // get current song position
        int currentPosition = mp.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mp.getDuration()) {
            // forward song
            mp.seekTo(currentPosition + seekForwardTime);
        } else {
            // forward to end position
            mp.seekTo(mp.getDuration());
        }
    }

    public void btnBackWard() {
        // get current song position
        int currentPosition = mp.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mp.seekTo(currentPosition - seekBackwardTime);
        } else {
            // backward to starting position
            mp.seekTo(0);
        }
    }

    public void btnNext() {
        // check if next song is there or not
        if (currentSongIndex < (songsList.size() - 1)) {
            playSong(currentSongIndex + 1);
            currentSongIndex = currentSongIndex + 1;
        } else {
            // play first song
            playSong(0);
            currentSongIndex = 0;
        }
    }

    public void btnPrevious() {
        if (currentSongIndex > 0) {
            playSong(currentSongIndex - 1);
            currentSongIndex = currentSongIndex - 1;
        } else {
            // play last song
            playSong(songsList.size() - 1);
            currentSongIndex = songsList.size() - 1;
        }
    }

    public boolean btnRepeat() {
        if (isRepeat) {
            isRepeat = false;
            return false;
        } else {
            // make repeat to true
            isRepeat = true;
            // make shuffle to false
            isShuffle = false;
            return true;
        }
    }

    public boolean btnShuffle() {
        if (isShuffle) {
            isShuffle = false;
            return false;
        } else {
            // make repeat to true
            isShuffle = true;
            // make shuffle to false
            isRepeat = false;
            return true;
        }
    }

    public void seekTo(int currentPosition) {
        mp.seekTo(currentPosition);
    }


    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {
        Log.v(LOG_TAG, "PlaySong");
        currentSongIndex = songIndex;
        // Play song
        try {
            mp.reset();
            mp.setDataSource(getApplicationContext(), Uri.parse(songsList.get(songIndex).getPath()));
            mp.prepare();
            mp.start();
            mp.seekTo(currentSongPosition);
            Log.v("Play song: Song Position:", "" + currentSongPosition);


            String songTitle = songsList.get(songIndex).toString();
            startForegroundNotificationService(songTitle);


            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);

            // Task for Displaying Song title
            intent.putExtra(MainActivity.PARAM_TASK, 1);
            intent.putExtra(MainActivity.PARAM_TITLE, songTitle);
            sendBroadcast(intent);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 500);
    }

    public void mHandlerRemoveCallbacks() {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);

            // start new task for updating progress bar
            intent.putExtra(MainActivity.PARAM_TASK, 2);
            intent.putExtra(MainActivity.PARAM_TOTAL_DURATION, totalDuration);
            intent.putExtra(MainActivity.PARAM_CORENT_DURATION, currentDuration);
            sendBroadcast(intent);

            // Running this thread after 500 milliseconds
            mHandler.postDelayed(this, 500);
        }
    };

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) + 1);
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(++currentSongIndex);

            } else {
                // play first song
                currentSongIndex = 0;
                playSong(currentSongIndex);

            }
        }
    }

    /*
    Running as a foreground service
     */
    private void startForegroundNotificationService(String songName) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification();
        //  notification.tickerText = "text";
        notification.icon = R.drawable.btn_play;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "MusicPlayer",
                "Playing: " + songName, pi);
        startForeground(1337, notification);

    }

    // binder
    public IBinder onBind(Intent i) {
        Log.d(LOG_TAG, "MyService onBind");
        currentSongIndex = i.getIntExtra("songIndex", 0);
        songsList = (ArrayList<Song>)i.getSerializableExtra("songs");
        if (currentSongIndex != 0)
            playSong(currentSongIndex);
        return binder;
    }

    public class MyBinder extends Binder {
        public BackgroundAudioService getService() {
            return BackgroundAudioService.this;
        }
    }

    /**
     * generate "add song's to card" error dialog
     */
    private void createErrorMassage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ERROR")
                .setMessage("Song's List is Empty. Add songs to card.")
                .setIcon(R.drawable.btn_playlist)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public ArrayList<Song> getSongsList() {
        return songsList;
    }

    public void setSongsList(ArrayList<Song> songsList) {
        this.songsList = songsList;
    }
}
