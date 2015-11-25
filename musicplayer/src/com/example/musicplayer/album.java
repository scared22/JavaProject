package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class album extends Fragment {
	ListView albumlist;
	ContentResolver albumcr;
	String[] cursorColumns;
	Cursor musiccursor;
	MyCursorAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		albumcr = getContext().getContentResolver();
		cursorColumns = new String[]{
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.LAST_YEAR
		};
		musiccursor = albumcr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, cursorColumns, null, null,null);
		adapter = new MyCursorAdapter(getContext(), musiccursor, 2);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saavedInstanceState)
	{
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.album,container,false);
		albumlist = (ListView)layout.findViewById(R.id.albumlist);
		albumlist.setAdapter(adapter);
		albumlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				musiccursor.moveToPosition(position);
				String title = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				int albumartindex = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
				String images = musiccursor.getString(albumartindex);
				String count = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
				String year = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
				Intent intent1 = new Intent(getContext(),songproperties.class);
				intent1.putExtra("albumtitle", title);
				intent1.putExtra("albumimages", images);
				intent1.putExtra("albumcounts", count);
				intent1.putExtra("albumyear", year);
				startActivity(intent1);
			}
		});
		return layout;
	}
}
