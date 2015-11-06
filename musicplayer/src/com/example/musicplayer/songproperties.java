package com.example.musicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class songproperties extends Activity implements OnClickListener, OnItemClickListener {
	public static Context mContext;
	ImageView properties_images;
	ImageButton properties_back;
	ListView properties_list;
	TextView properties_title;
	String getimages,gettitle;
	Cursor propertiesCursor;
	String[] cursorColumns,selections;
	ContentResolver propertiescr;
	MyCursorAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songproperties);
		properties_images = (ImageView)findViewById(R.id.properties_images);
		properties_title = (TextView)findViewById(R.id.properties_title);
		properties_list = (ListView)findViewById(R.id.properties_list);
		properties_back = (ImageButton)findViewById(R.id.properties_back);
		properties_back.setOnClickListener(this);
		mContext=this;
		Intent intent1 = getIntent();
		propertiescr = getContentResolver();
		cursorColumns = new String[]{
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.AudioColumns.TITLE,
				MediaStore.Audio.AudioColumns.DATA,
				MediaStore.Audio.Media.DURATION
		};
		gettitle=intent1.getStringExtra("albumtitle");
		properties_title.setText(gettitle);
		getimages = intent1.getStringExtra("albumimages");
		if(Getalbumimages(getimages)!=null)
			properties_images.setImageDrawable(Getalbumimages(getimages));
		else
			properties_images.setImageResource(R.drawable.noimages);
		selections = new String[]{
			intent1.getStringExtra("albumtitle")	
		};
		propertiesCursor = propertiescr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns,MediaStore.Audio.Media.ALBUM+" LIKE ? ",selections, null);
		adapter = new MyCursorAdapter(this, propertiesCursor, 21);
		properties_list.setAdapter(adapter);
		properties_list.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.properties_back)
			finish();
	}
	public Drawable Getalbumimages(String temp)
	{
		Drawable d = null;
		if(temp!=null)
		{
			try {
				File artWorkFile = new File(temp);
				InputStream in;
				in = new FileInputStream(artWorkFile);
				return d = Drawable.createFromStream(in, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else
			;
		return d;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		propertiesCursor.moveToPosition(position);
		String path = propertiesCursor.getString(propertiesCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
		String title = propertiesCursor.getString(propertiesCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));	
		Intent intent2 = new Intent(this,player.class);
		intent2.putExtra("paths", path.toString());
		intent2.putExtra("titles",title.toString());
		intent2.putExtra("positions", position);
		startActivity(intent2);
	}
	public int getpos(int pos ,int set)
	{
		if(pos<propertiesCursor.getCount()-1&&set==1)
			pos++;
		else if(pos>0 && set==0)
			pos--;
		else if(pos==0)
			pos=propertiesCursor.getCount()-1;
		else if(pos==propertiesCursor.getCount()-1 && set==1)
			pos=0;
		else 
			pos=0;
		return pos;
	}
	public String getpath(int pos)
	{
		propertiesCursor.moveToPosition(pos);
		String path = propertiesCursor.getString(propertiesCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
		return path;
	}
	public String gettitle(int pos)
	{
		propertiesCursor.moveToPosition(pos);
		String title = propertiesCursor.getString(propertiesCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
		return title;
	}
}
