package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Collections;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class Cursorquery {
	Cursor db;
	int position,shuffle_pos=0,shuffle_end;
	ArrayList<Integer>arr = new ArrayList<Integer>();
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
		result(pos,set,option,1);
	}
	public void albumlist(int pos, int set,int option)
	{
		selections = new String[]{
				selections1
		};	
		db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns,MediaStore.Audio.Media.ALBUM+" LIKE ? ",selections, null);
		result(pos,set,option,2);
	}
	public void artistlist(int pos, int set,int option)
	{
		selections = new String[]{
				selections1
		};
		db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.ARTIST+" LIKE ?", selections, null);
		result(pos,set,option,3);
	}
	public void folderlist(int pos, int set, int option)
	{
		selections = new String[]{selections1};
		db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.DATA+" LIKE ?", selections, null);
		result(pos,set,option,4);
	}
	public void result(int pos, int set, int option, int jari)
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
		//else if(option == 1)
			//shufflesetting(jari);
		else if(option == 2)//셔플일때 
		{
			if(shuffle_pos<shuffle_end-1&&set==1)
				pos = arr.get(++shuffle_pos);
			else if(shuffle_pos>0 && set==0)
				pos = arr.get(--shuffle_pos);
			else if(shuffle_pos==0)
			{
				shuffle_pos = shuffle_end-1;
				pos=arr.get(shuffle_pos);
			}
			else if(pos==shuffle_end-1 && set==1)
			{
				shuffle_pos=0;
				pos=arr.get(shuffle_pos);
			}
		}
		db.moveToPosition(pos);
		position = pos;
		path = db.getString(db.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
		title = db.getString(db.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
		img = db.getString(db.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		db.close();
	}
	public void allwhere(String s)
	{
		selections1 = s;
	}
	public void shufflesetting(int jari)
	{
		if(jari == 1)
			db=cr.query(Audio.Media.EXTERNAL_CONTENT_URI , cursorColumns, null, null, null);
		if(jari == 2)
		{
			selections = new String[]{selections1};
			db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns,MediaStore.Audio.Media.ALBUM+" LIKE ? ",selections, null);
		}
		if(jari == 3)
		{
			selections = new String[]{selections1};
			db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.ARTIST+" LIKE ?", selections, null);
		}
		if(jari == 4)
		{
			selections = new String[]{selections1};
			db=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.DATA+" LIKE ?", selections, null);
		}
		//셔플 알고리즘
		shuffle_end = db.getCount();
		shuffle_pos=0;	
		for(int i=0;i<shuffle_end;i++)
			arr.add(i);
		Collections.shuffle(arr);
		for(int i=0;i<shuffle_end;i++)
			Log.d(""+arr.get(i), "입니다.");
		db.close();
	}
}
