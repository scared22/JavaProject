package com.example.musicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
import android.widget.Toast;

public class songproperties extends Activity implements OnClickListener, OnItemClickListener {
	public static Context mContext;
	ImageView properties_images,playing,mini1_view;
	ImageButton properties_back,mini1_btn;
	ListView properties_list;
	TextView properties_title,mini1_title;
	String getimages,gettitle;
	Cursor propertiesCursor;
	String[] cursorColumns,selections;
	ContentResolver propertiescr;
	MyCursorAdapter adapter;
	private IMyService mBinder = null;
	IMyServiceCallback mCallback = new IMyServiceCallback.Stub() {
		@Override
		public void callback(int num) throws RemoteException {
		}
	};
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBinder = null;
			Log.i("앨범","연결해제");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("앨범", "연결성공");
			mBinder = IMyService.Stub.asInterface(service);
			try {
				if(mBinder.singing()==true)
					properties_setting();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	//브로드캐스트
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action=="mini")
			{
				action=null;
				properties_setting();
			}
		}
	};
	//
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songproperties);
		properties_images = (ImageView)findViewById(R.id.properties_images);
		properties_title = (TextView)findViewById(R.id.properties_title);
		properties_list = (ListView)findViewById(R.id.properties_list);
		properties_back = (ImageButton)findViewById(R.id.properties_back);
		properties_back.setOnClickListener(this);
		playing = (ImageView)findViewById(R.id.playing);
		mini1_title = (TextView)findViewById(R.id.mini1_title);
		mini1_btn = (ImageButton)findViewById(R.id.mini1_btn);
		mini1_btn.setOnClickListener(this);
		mini1_view = (ImageView)findViewById(R.id.mini1_view);
		mContext=this;
		//service
			Intent ServiceIntent = new Intent(this,MyService.class);
			bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);
			startService(ServiceIntent);
		//
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
		//브로드캐스트 등록
		IntentFilter Filter = new IntentFilter("mini");        
		registerReceiver(receiver, Filter);
		//
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.properties_back)
			finish();
		if(v.getId()==R.id.mini1_btn)
		{
			try {
				if(mBinder.singing()==true)
				{
					if(mBinder.playjudge()==false)
					{
						mini1_btn.setImageResource(R.drawable.ic_pause);
						mBinder.play(mCallback);
					}
					else
					{
						mini1_btn.setImageResource(R.drawable.ic_play);
						mBinder.pause(mCallback);
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
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
		intent2.putExtra("starts", 2);
		intent2.putExtra("paths", path.toString());
		intent2.putExtra("titles",title.toString());
		intent2.putExtra("positions", position);
		intent2.putExtra("where",selections[0]);
		startActivity(intent2);
	}
	public void properties_setting()
	{
		try {
				if(mBinder.singing()==true)
				{
					String str = mBinder.getItems(3);
					mini1_title.setText(str);
					mini1_btn.setImageResource(R.drawable.ic_pause);
				}
				if(mBinder.playjudge()==false)
				mini1_btn.setImageResource(R.drawable.ic_play);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
