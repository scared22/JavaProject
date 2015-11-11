package com.example.musicplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class artist_songlist extends Activity implements OnClickListener, OnItemClickListener {
	public static Context mContext;
	TextView artist_title;
	String getartist;
	ImageButton artist_back;
	ListView artist_songlist_list;
	MyCursorAdapter adapter;
	String[] cursorColumns,selections;
	Cursor artistsonglistCursor;
	ContentResolver artistsonglistcr;
	@Override
	public void onCreate(Bundle SavedInstanceState){
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.artist_songlist);
		Intent intent1 = getIntent();
		mContext = this;
		//¼ÂÆÃ
		artist_title = (TextView)findViewById(R.id.artist_songlist_title);
		getartist = intent1.getStringExtra("artisttitle");
		artist_title.setText(getartist);
		artist_back = (ImageButton)findViewById(R.id.artist_back);
		artist_back.setOnClickListener(this);
		artist_songlist_list = (ListView)findViewById(R.id.artist_songlist_list);
		artistsonglistcr = getContentResolver();
		cursorColumns = new String[]{
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.ALBUM_ID
		};
		selections = new String[]{
				intent1.getStringExtra("artisttitle"),
		};
		artistsonglistCursor = artistsonglistcr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.ARTIST+" LIKE ?", selections, null);
		adapter = new MyCursorAdapter(this, artistsonglistCursor, 31);
		artist_songlist_list.setAdapter(adapter);
		artist_songlist_list.setOnItemClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.artist_back)
			finish();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		artistsonglistCursor.moveToPosition(position);
		String path = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		String title = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		Intent intent4 = new Intent(this,player.class);
		intent4.putExtra("starts", 3);
		intent4.putExtra("paths", path.toString());
		intent4.putExtra("titles", title.toString());
		intent4.putExtra("positions", position);
		intent4.putExtra("where", selections[0]);
		startActivity(intent4);
	}
	public int getpos(int pos ,int set)
	{
		if(pos<artistsonglistCursor.getCount()-1&&set==1)
			pos++;
		else if(pos>0 && set==0)
			pos--;
		else if(pos==0)
			pos=artistsonglistCursor.getCount()-1;
		else if(pos==artistsonglistCursor.getCount()-1 && set==1)
			pos=0;
		else 
			pos=0;
		return pos;
	}
	public String getpath(int pos)
	{
		artistsonglistCursor.moveToPosition(pos);
		String path = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		return path;
	}
	public String gettitle(int pos)
	{
		artistsonglistCursor.moveToPosition(pos);
		String title = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		return title;
	}
}
