package com.example.musicplayer;

import java.util.ArrayList;

import android.R.anim;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class folder extends Fragment {
	ListView folderlist;
	ContentResolver foldercr;
	String[] cursorColumns,selection;
	Cursor musiccursor;
	ArrayList<String> fl;
	ArrayAdapter<String> adapter;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		fl  = new ArrayList<String>();
		foldercr = getContext().getContentResolver();
		cursorColumns = new String[]{
				"distinct SUBSTR("+MediaStore.Audio.Media.DATA+" ,"+0+" , LENGTH("+MediaStore.Audio.Media.DATA+") - LENGTH("+MediaStore.Audio.Media.DISPLAY_NAME+"))",
			};
		musiccursor = foldercr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns,null,null, null);
		if( musiccursor != null && musiccursor.getCount() > 0) {
			 musiccursor.moveToFirst();
		    while(! musiccursor.isAfterLast()) {
		    	String []arr = musiccursor.getString(0).split("/");
		        fl.add(arr[arr.length-1]);
		        musiccursor.moveToNext();
		    }
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saavedInstanceState)
	{
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.folder,container,false);
		folderlist = (ListView)layout.findViewById(R.id.folder_list);
		adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,fl);
		folderlist.setAdapter(adapter);
		folderlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				musiccursor.moveToPosition(position);
				String title = musiccursor.getString(0)+"%";
				String[] foldertitle = musiccursor.getString(0).split("/");
				Intent intent1 = new Intent(getContext(),artist_songlist.class);
				intent1.putExtra("artisttitle", title);
				intent1.putExtra("getpos",2);
				intent1.putExtra("ft", foldertitle[foldertitle.length-1]);
				startActivity(intent1);
			}
		});
		return layout;
	}
}
