package com.example.musicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("unused")
public class MyCursorAdapter extends CursorAdapter {
	int jari;
	@SuppressWarnings("deprecation")
	public MyCursorAdapter(Context context, Cursor c, int pos) {
		super(context, c);
		jari = pos;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//custom 하는곳
		if(jari == 1 || jari == 21)
		{
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
		else if(jari==2)
		{
			TextView album_title,album_artist;
			ImageView album;
			String albumArt,title,songcount;
			album_title = (TextView)view.findViewById(R.id.album_title);
			album_artist = (TextView)view.findViewById(R.id.album_count);
			album = (ImageView)view.findViewById(R.id.albumimage);
			//이미지 처리
			int albumartIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
			albumArt = cursor.getString(albumartIndex);
			if(albumArt != null)
			{
				try{
					File artWorkFile = new File(albumArt);
					InputStream in = new FileInputStream(artWorkFile);
					Drawable d = Drawable.createFromStream(in, null);
					album.setImageDrawable(d);	
				}catch(IOException e){
				}
			}
			else
				album.setImageResource(R.drawable.noimages);
			//나머지 처리
			title =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
			album_title.setText(title);
			songcount = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
			album_artist.setText(songcount);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if(jari == 1 || jari == 21)
		{
			View v = inflater.inflate(R.layout.list_item, null);
			return v;
		}
		if(jari == 2)
		{
			View v = inflater.inflate(R.layout.album_list_item1, null);
			return v;
		}
		else
			return null;
		
	}

}
