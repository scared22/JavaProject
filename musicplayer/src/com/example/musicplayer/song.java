package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class song extends Fragment {
	TextView title;
	Intent intent1;
	public static Context mContext;
	ListView songlist;
	MyCursorAdapter myAdapter;
	ContentResolver cr;
	String[] cursorColumns;
	String sortOrder;
	Cursor musiccursor;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		cr = getContext().getContentResolver();
		mContext = getContext();
		cursorColumns = new String[]{
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.AudioColumns.TITLE,
			MediaStore.Audio.AudioColumns.DATA,
			MediaStore.Audio.Media.DURATION};
		musiccursor= cr.query(Audio.Media.EXTERNAL_CONTENT_URI , cursorColumns, null, null, sortOrder);
		myAdapter = new MyCursorAdapter(getContext(), musiccursor);
		//Log.d(""+musiccursor.getCount(), "입니다.");
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saavedInstanceState)
	{
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.song,container,false);
		songlist = (ListView)layout.findViewById(R.id.songlist);
		songlist.setAdapter(myAdapter);
		songlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				musiccursor.moveToPosition(position);
				Log.d(""+position, "입니다.");
				String path = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
				String title = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
				//player에게  파일 경로를 넘긴다
				intent1 = new Intent(getContext(),player.class);
				intent1.putExtra("paths", path.toString());
				intent1.putExtra("titles", title.toString());
				intent1.putExtra("positions", position);
				startActivity(intent1);
			}
		});
		return layout;
	}
}
