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

public class artist extends Fragment {
	ListView artistlist;
	ContentResolver artistcr;
	String[] cursorColumns;
	MyCursorAdapter adapter;
	Cursor musiccursor;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		artistcr = getContext().getContentResolver();
		cursorColumns = new String[]{
			MediaStore.Audio.Artists._ID,
			MediaStore.Audio.Artists.ARTIST
		};
		musiccursor = artistcr.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, cursorColumns, null, null, null);
		adapter = new MyCursorAdapter(getContext(), musiccursor, 3);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saavedInstanceState)
	{
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.artist,container,false);
		artistlist = (ListView)layout.findViewById(R.id.artist_list);
		artistlist.setAdapter(adapter);
		artistlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				musiccursor.moveToPosition(position);
				String title = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
				Intent intent1 = new Intent(getContext(),artist_songlist.class);
				intent1.putExtra("artisttitle", title);
				intent1.putExtra("getpos",1);
				startActivity(intent1);
			}
		});
		return layout;
	}
}
