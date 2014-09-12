package com.savka.audioplayer.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.savka.audioplayer.R;
import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;
import com.savka.audioplayer.utils.MediaProvider;
import com.savka.audioplayer.utils.SdCardMediaProvider;
import com.savka.audioplayer.utils.VkMediaProvider;
import com.vk.sdk.VKUIHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PlayListActivity extends ListActivity {
    final String LOG_TAG = "PlayListActivity";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ListView listView1;
    ArrayAdapter<Song> adapter;

    MediaProvider mediaProvider;
    ArrayList<Song> songsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        setContentView(R.layout.playlist);
        Log.d(LOG_TAG, "onCreate");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        editor = sharedPreferences.edit();
        listView1 = (ListView) findViewById(android.R.id.list);
        Intent i = getIntent();
        final String flag = i.getStringExtra("flag");
        switch(flag) {
            case "VK":
                songsList = (ArrayList<Song>) i.getSerializableExtra("songs");
                saveVkSongs(songsList);
                mediaProvider = new VkMediaProvider(songsList);

                break;
            case "SD":
                mediaProvider = new SdCardMediaProvider();

                break;
            case "VK_RESTORE":
                ArrayList<Song> restoreSongs = restoreVkSongs();
                mediaProvider = new VkMediaProvider(restoreSongs);

                break;

//            case "RD":
//                mediaProvider = new RadioMediaProvider();
//                break;
        }
        // Create adapter
        try {
            adapter = new ArrayAdapter<Song>(this,
                    android.R.layout.simple_list_item_1, mediaProvider.getPlayList(getApplicationContext()));
        } catch (EmptySongsListException e) {
            Log.e(LOG_TAG, "EmptySongsListException. Add songs to card");
        }

        listView1.setAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Starting new intent
                Intent in = new Intent();
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", position);
                in.putExtra("media", mediaProvider);
                setResult(RESULT_OK, in);
                // Closing PlayListView
                finish();
            }
        });

    }

    private void saveVkSongs(ArrayList<Song> songs) {
        Gson gson = new Gson();
        String json = gson.toJson(songs);
        editor.putString("songs", json);
        editor.commit();
    }


    private ArrayList<Song> restoreVkSongs() {
        ArrayList<Song> songs;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("songs", null);
        Type type = new TypeToken<ArrayList<Song>>(){}.getType();
        songs = gson.fromJson(json, type);

        return songs;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        Log.d(LOG_TAG, "onResume()");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }


}
