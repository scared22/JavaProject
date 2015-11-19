package com.example.musicplayer;

import java.util.ArrayList;

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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class artist extends Fragment {
	ListView artistlist;
	ContentResolver artistcr;
	String[] cursorColumns;
	ArrayList<String> at;
	ArrayAdapter<String>adapter;
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
		at = new ArrayList<String>();
		if(musiccursor != null && musiccursor.getCount()>0)
		{
			musiccursor.moveToFirst();
			while(!musiccursor.isAfterLast())
			{
				if(musiccursor.getString(1)!=null)
				{
					String arr = musiccursor.getString(1);
					at.add(arr);
				}
				musiccursor.moveToNext();
			}
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saavedInstanceState)
	{
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.artist,container,false);
		artistlist = (ListView)layout.findViewById(R.id.artist_list);
		adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,at);
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
