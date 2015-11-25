package com.example.musicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("unused")

public class MyCursorAdapter extends CursorAdapter {
	static class ViewHolder{
		TextView album_title,album_artist,list_item2_title,list_item2_duration;
		ImageView album,item_list2_image;
	};
	int jari;
	View v;
	Context mContext;
	@SuppressWarnings("deprecation")
	public MyCursorAdapter(Context context, Cursor c, int pos) {
		super(context, c);
		jari = pos;
		mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//custom �ϴ°�
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
			try{
				time = Integer.parseInt(duration);
				song_duration.setText(String.format("%02d:%02d", (time/60000)%60000,(time%60000)/1000));
			}catch(NumberFormatException e){
				if(duration == null)
					time=0;
			}
		}
		else if(jari==2)
		{
			ViewHolder holder = (ViewHolder)view.getTag();
			String albumArt = null,title=null,songcount=null;
			//�̹��� ó��
			if(jari == 2)
			{
				int albumartIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
				albumArt = cursor.getString(albumartIndex);
				if(getImage(albumArt) != null)
					holder.album.setImageDrawable(getImage(albumArt));	
				else
					holder.album.setImageResource(R.drawable.noimages);
				title =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
				songcount = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
			}
			//������ ó��
			holder.album_title.setText(title);
			holder.album_artist.setText(songcount);
		}
		if(jari == 31)
		{
			ViewHolder holder = (ViewHolder)view.getTag();
			String title,duration,albumArt,songgetImage;
			Cursorquery csq = new Cursorquery(mContext);
			int time;			
			//����
			title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			holder.list_item2_title.setText(title);
			duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			time = Integer.parseInt(duration);
			holder.list_item2_duration.setText(String.format("%d��%d��", (time/60000)%60000,(time%60000)/1000));
			songgetImage = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			final Uri ArtworkUri =  Uri.parse("content://media/external/audio/albumart");
			Uri uri = ContentUris.withAppendedId(ArtworkUri, Long.parseLong(songgetImage));
			String uripath = uri.toString();
			Bitmap bm = null;
			try {
				bm = Images.Media.getBitmap(mContext.getContentResolver(), uri);
			} catch (FileNotFoundException e) {
				holder.item_list2_image.setImageResource(R.drawable.noimages);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(bm!=null)
				holder.item_list2_image.setImageBitmap(bm);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
			if(jari == 1 || jari == 21)
			{
				v = inflater.inflate(R.layout.list_item, null);
				return v;
			}
			if(jari == 2)
			{
				v = inflater.inflate(R.layout.album_list_item1, null);
				ViewHolder holder = new ViewHolder();
				holder.album_title = (TextView)v.findViewById(R.id.album_title);
				holder.album_artist = (TextView)v.findViewById(R.id.album_count);
				holder.album = (ImageView)v.findViewById(R.id.albumimage);
				v.setTag(holder);
				return v;
			}
			if(jari == 31)
			{
				v = inflater.inflate(R.layout.list_item2, null);
				ViewHolder holder = new ViewHolder();
				holder.list_item2_title = (TextView)v.findViewById(R.id.list_item2_title);
				holder.list_item2_duration = (TextView)v.findViewById(R.id.list_item2_duration);
				holder.item_list2_image = (ImageView)v.findViewById(R.id.list_item2_songimage);
				v.setTag(holder);
				return v;
			}
			else
				return null;
	}
	//�̹�����
	public Drawable getImage(String albumArt)
	{
		Drawable d;
		if(albumArt != null)
		{
			try{
				File artWorkFile = new File(albumArt);
				InputStream in = new FileInputStream(artWorkFile);
				d = Drawable.createFromStream(in, null);	
				return d;	
			}catch(IOException e){
			}
		}
		return null;
	}
}
