package com.savka.audioplayer.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.savka.audioplayer.R;
import com.savka.audioplayer.entity.Song;
import com.savka.audioplayer.exception.EmptySongsListException;
import com.savka.audioplayer.utils.MediaProvider;
import com.savka.audioplayer.utils.media_provider_impl.OnlineRadioMediaProvider;

public class PlayListActivity extends ListActivity {
    final String LOG_TAG = "PlayListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        ListView listView1 = (ListView) findViewById(android.R.id.list);
        MediaProvider mediaProvider = new OnlineRadioMediaProvider();
        ArrayAdapter<Song> adapter = null;
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
                Intent in = new Intent(getApplicationContext(),
                        MainActivity.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", position);

                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });

    }
}
