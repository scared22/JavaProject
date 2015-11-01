package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressWarnings("unused")
public class MyCursorAdapter extends CursorAdapter {

	@SuppressWarnings("deprecation")
	public MyCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//custom 하는곳
		TextView song_title,song_artist,song_duration;
		int time,getid;
	
		song_title = (TextView)view.findViewById(R.id.song_title);
		song_artist = (TextView)view.findViewById(R.id.song_artist);
		song_duration = (TextView)view.findViewById(R.id.song_duration);
		
		String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
		String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
		song_title.setText(title);
		song_artist.setText(artist);
		time = Integer.parseInt(duration);
		song_duration.setText(String.format("%d분%d초", (time/60000)%60000,(time%60000)/1000));
	}

	@SuppressLint("InflateParams")
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item, null);
		return v;
	}

}
