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
				song_duration.setText(String.format("%d��%d��", (time/60000)%60000,(time%60000)/1000));
			}catch(NumberFormatException e){
				if(duration == null)
					time=0;
			}
		}
		else if(jari==2 || jari ==4)
		{
			String albumArt = null,title=null,songcount=null;
			TextView album_title,album_artist;
			ImageView album;
				album_title = (TextView)view.findViewById(R.id.album_title);
				album_artist = (TextView)view.findViewById(R.id.album_count);
				album = (ImageView)view.findViewById(R.id.albumimage);
			//�̹��� ó��
			if(jari == 2)
			{
				int albumartIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
				albumArt = cursor.getString(albumartIndex);
				if(getImage(albumArt) != null)
					album.setImageDrawable(getImage(albumArt));	
				else
					album.setImageResource(R.drawable.noimages);
				title =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
				songcount = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
			}
			if(jari==4)
			{
				Log.d("���� ������?", "��������������");
				album.setImageResource(R.drawable.noimages);
				title = cursor.getString(0);
				songcount = null;	
			}
			//������ ó��
			album_title.setText(title);
			album_artist.setText(songcount);
		}
		if(jari == 3)
		{
			TextView artist_name;
			artist_name = (TextView)view.findViewById(R.id.artist_name);
			String artisttitle;
			artisttitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
			artist_name.setText(artisttitle);
		}
		if(jari == 31)
		{
			TextView list_item2_title,list_item2_duration;
			ImageView item_list2_image;
			String title,duration,albumArt,songgetImage;
			Cursorquery csq = new Cursorquery(mContext);
			int time;			
			//����
			list_item2_title = (TextView)view.findViewById(R.id.list_item2_title);
			title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			list_item2_title.setText(title);
			list_item2_duration = (TextView)view.findViewById(R.id.list_item2_duration);
			duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			time = Integer.parseInt(duration);
			list_item2_duration.setText(String.format("%d��%d��", (time/60000)%60000,(time%60000)/1000));
			item_list2_image = (ImageView)view.findViewById(R.id.list_item2_songimage);
			songgetImage = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			final Uri ArtworkUri =  Uri.parse("content://media/external/audio/albumart");
			Uri uri = ContentUris.withAppendedId(ArtworkUri, Long.parseLong(songgetImage));
			String uripath = uri.toString();
			Bitmap bm = null;
			try {
				bm = Images.Media.getBitmap(mContext.getContentResolver(), uri);
			} catch (FileNotFoundException e) {
				item_list2_image.setImageResource(R.drawable.noimages);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(bm!=null)
				item_list2_image.setImageBitmap(bm);
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
			if(jari == 2 || jari == 4)
			{
				v = inflater.inflate(R.layout.album_list_item1, null);
				return v;
			}
			if(jari == 3)
			{
				v = inflater.inflate(R.layout.artist_custom, null);
				return v;
			}
			if(jari == 31)
			{
				v = inflater.inflate(R.layout.list_item2, null);
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
				//Log.d(""+d, "d��κ���");
				return d;	
			}catch(IOException e){
			}
		}
		return null;
	}
}
