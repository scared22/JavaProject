package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class Cursorquery {
	Cursor db;
	int position;
	ContentResolver cr;
	public String path,title,img;
	public static Context mContext;
	public static String selections1; 
	String[] cursorColumns;
	String[] selections;
	
	public Cursorquery(Context c) {
		mContext=c.getApplicationContext();
		cr = mContext.getContentResolver();
		cursorColumns = new String[]{
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.AudioColumns.TITLE,
				MediaStore.Audio.AudioColumns.DATA,
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.DURATION};
	}
	public void songlist(int pos ,int set,int option)
	{	
		db=cr.query(Audio.Media.EXTERNAL_CONTENT_URI , cursorColumns, null, null, null);
		result(pos,set,option);
	}
	public void albumlist(int pos, int set,int option)
	{
		selections = new String[]{
				selections1
		};	
		db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns,MediaStore.Audio.Media.ALBUM+" LIKE ? ",selections, null);
		result(pos,set,option);
	}
	public void artistlist(int pos, int set,int option)
	{
		selections = new String[]{
				selections1
		};
		db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.ARTIST+" LIKE ?", selections, null);
		result(pos,set,option);
	}
	public void result(int pos, int set, int option)
	{
		if(option == 0)
		{
			if(pos<db.getCount()-1&&set==1)
				pos++;
			else if(pos>0 && set==0)
				pos--;
			else if(pos==0)
				pos=db.getCount()-1;
			else if(pos==db.getCount()-1 && set==1)
				pos=0;
			else 
				pos=0;
		}
		else if(option == 2)//셔플일때 
		{
			try {
				pos = (int)((Math.random()*db.getCount())+1);
				Log.d(""+pos,"랜덤 노래 나와라  ");
			} catch (NumberFormatException e) {
				pos=db.getCount()/3;
			}

		}
		
		db.moveToPosition(pos);
		position = pos;
		path = db.getString(db.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
		title = db.getString(db.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
		img = db.getString(db.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
	}
	public void allwhere(String s)
	{
		selections1 = s;
	}
}
