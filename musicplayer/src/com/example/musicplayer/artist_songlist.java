package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class artist_songlist extends Activity implements OnClickListener, OnItemClickListener {
	public static Context mContext;
	TextView artist_title,mini2_title;
	String getartist;
	ImageButton artist_back,mini2_btn;
	ListView artist_songlist_list;
	MyCursorAdapter adapter;
	String[] cursorColumns,selections;
	Cursor artistsonglistCursor;
	ContentResolver artistsonglistcr;
	ImageView mini2_view;
	private IMyService mBinder = null;
	int start;
	IMyServiceCallback mCallback = new IMyServiceCallback.Stub() {
		@Override
		public void callback(int num) throws RemoteException {
		}
	};
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBinder = null;
			Log.i("가수", "연결끊김");
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("가수", "연결");
			mBinder = IMyService.Stub.asInterface(service);
			artist_setting();
		}
	};
	//브로드캐스트
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action=="mini" || action == "back")
			{
				action=null;
				artist_setting();
			}
		}
	};
	//
	@Override
	public void onCreate(Bundle SavedInstanceState){
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.artist_songlist);
		Intent intent1 = getIntent();
		mContext = this;
		//service
			Intent ServiceIntent = new Intent(this,MyService.class);
			bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);
			startService(ServiceIntent);
		//
		//브로드캐스트 등록
		IntentFilter Filter = new IntentFilter("mini");
		IntentFilter Filter1 = new IntentFilter("back");
		registerReceiver(receiver, Filter);
		registerReceiver(receiver, Filter1);
		//셋팅
		mini2_btn = (ImageButton)findViewById(R.id.mini2_btn);
		mini2_btn.setOnClickListener(this);
		mini2_view = (ImageView)findViewById(R.id.mini2_view);
		mini2_title = (TextView)findViewById(R.id.mini2_title);
		artist_title = (TextView)findViewById(R.id.artist_songlist_title);
		getartist = intent1.getStringExtra("artisttitle");
		start=intent1.getIntExtra("getpos", 0);
		if(start==1)	
			artist_title.setText(getartist);
		else
		{
			getartist = intent1.getStringExtra("ft");
			artist_title.setText(getartist);
		}
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
		start=intent1.getIntExtra("getpos", 0);
		if(start==1)
			artistsonglistCursor = artistsonglistcr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.ARTIST+" LIKE ?", selections, null);		
		if(start==2)
			artistsonglistCursor = artistsonglistcr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorColumns, MediaStore.Audio.Media.DATA+" LIKE ?", selections, null);
		adapter = new MyCursorAdapter(this, artistsonglistCursor, 31);
		artist_songlist_list.setAdapter(adapter);
		artist_songlist_list.setOnItemClickListener(this);
	}
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.artist_back)
		{
			Intent intent = new Intent("back");
			sendBroadcast(intent);
			finish();
		}
		if(v.getId()==R.id.mini2_btn)
		{
			try {
				if(mBinder.singing()==true)
				{
					if(mBinder.playjudge() == false)
					{
						mini2_btn.setImageResource(R.drawable.ic_pause);
						mBinder.play(mCallback);
					}
					else
					{
						mini2_btn.setImageResource(R.drawable.ic_play);
						mBinder.pause(mCallback);
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		artistsonglistCursor.moveToPosition(position);
		String path = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		String title = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		String img = artistsonglistCursor.getString(artistsonglistCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		Intent intent4 = new Intent(this,player.class);
		if(start==1)
			intent4.putExtra("starts", 3);
		else
			intent4.putExtra("starts", 4);
		intent4.putExtra("paths", path.toString());
		intent4.putExtra("titles", title.toString());
		intent4.putExtra("positions", position);
		intent4.putExtra("where", selections[0]);
		intent4.putExtra("imgs", img);
		startActivity(intent4);
	}
	@Override
	public void onDestroy(){
		  super.onDestroy();
		  unbindService(mConnection);
	}
	public void artist_setting()
	{
		try {
			if(mBinder.singing()==true)
			{
				String str = mBinder.getItems(3);
				mini2_title.setText(str);
				mini2_btn.setImageResource(R.drawable.ic_pause);
				//이미지 처리해야 하는 부분
				final Uri ArtworkUri =  Uri.parse("content://media/external/audio/albumart");
				Uri uri = ContentUris.withAppendedId(ArtworkUri, Long.parseLong(mBinder.songsimages()));
				String uripath = uri.toString();
				Bitmap bm = null;
				try {
					bm = Images.Media.getBitmap(getContentResolver(), uri);
				} catch (FileNotFoundException e) {
					mini2_view.setImageResource(R.drawable.noimages);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(bm!=null)
					mini2_view.setImageBitmap(bm);
			}
			if(mBinder.playjudge()==false)
				mini2_btn.setImageResource(R.drawable.ic_play);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
			Intent intent = new Intent("back");
			sendBroadcast(intent);
			finish();
        }
        return false;
    }
	
}
