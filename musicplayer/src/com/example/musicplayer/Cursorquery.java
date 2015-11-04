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
	public String path,title;
	public static Context mContext;
	public Cursorquery(Context c) {
		mContext=c;
	}
	public void songlist(int pos ,int set)
	{
		String[] cursorColumns;
		cr = mContext.getContentResolver();
		cursorColumns = new String[]{
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.AudioColumns.TITLE,
				MediaStore.Audio.AudioColumns.DATA,
				MediaStore.Audio.Media.DURATION};
		db=cr.query(Audio.Media.EXTERNAL_CONTENT_URI , cursorColumns, null, null, null);
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
		db.moveToPosition(pos);
		position = pos;
		path = db.getString(db.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
		title = db.getString(db.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
	}
}
