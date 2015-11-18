package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener{
	private ViewPager mPager;
	Button btn_song,btn_artist,btn_album,btn_folder;
	ImageButton mini_btn;
	ImageView mini_view;
	TextView mini_title;
	int Max_PAGE = 4;
	Fragment cur_fragment = new Fragment();
	//service단
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
			Log.i("연결이 끊어졌습니다.", "메인액티비티");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("연결 되었습니다.", "메인액티비티");
			mBinder = IMyService.Stub.asInterface(service);
			main_setting();
		}
	};
	//
	//브로드캐스트
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if((action=="mini") || (action=="back"))
			{
				action=null;
				main_setting();
			}
		}
	};
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//viewpager
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
		mPager.setCurrentItem(0);
		//서비스 셋팅
			Intent ServiceIntent = new Intent(this,MyService.class);
			bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);
			startService(ServiceIntent);
		//
		//미니 플레이어바 셋팅
		mini_btn = (ImageButton)findViewById(R.id.mini_btn);
		mini_btn.setOnClickListener(this);
		mini_title = (TextView)findViewById(R.id.mini_title);
		mini_view = (ImageView)findViewById(R.id.mini_view);
		//
		//브로드캐스트 등록
		IntentFilter Filter = new IntentFilter("mini");
		IntentFilter Filter1 = new IntentFilter("back");
		registerReceiver(receiver, Filter);
		registerReceiver(receiver, Filter1);
		//
		//버튼 셋팅
		btn_song = (Button)findViewById(R.id.btn_song);
		btn_song.setOnClickListener(this);
		btn_artist = (Button)findViewById(R.id.btn_artist);
		btn_artist.setOnClickListener(this);
		btn_album = (Button)findViewById(R.id.btn_album);
		btn_album.setOnClickListener(this);
		btn_folder = (Button)findViewById(R.id.btn_folder);
		btn_folder.setOnClickListener(this);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int position) {
				btn_song.setSelected(false);
				btn_album.setSelected(false);
				btn_artist.setSelected(false);
				btn_folder.setSelected(false);
				switch(position)
				{
				case 0:
					btn_song.setSelected(true);
					break;
				case 1:
				{
					btn_album.setSelected(true);
					break;
				}
				case 2:
				{
					btn_artist.setSelected(true);
					break;
				}
				case 3:
					btn_folder.setSelected(true);
					break;
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		btn_song.setSelected(true);
	}
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item cliint형 로그cks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_song)
			mPager.setCurrentItem(0);
		else if(v.getId() == R.id.btn_album)
			mPager.setCurrentItem(1);
		else if(v.getId() == R.id.btn_artist)
			mPager.setCurrentItem(2);
		else if(v.getId() == R.id.btn_folder)
			mPager.setCurrentItem(3);
		else if(v.getId() == R.id.mini_btn)
		{
			try {
				if(mBinder.singing()== true)
				{
					if(mBinder.playjudge() == false)
					{
						mini_btn.setImageResource(R.drawable.ic_pause);
						mBinder.play(mCallback);
					}
					else
					{
						mini_btn.setImageResource(R.drawable.ic_play);
						mBinder.pause(mCallback);
	
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	private class pagerAdapter extends FragmentPagerAdapter
	{
		public pagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int position) {
			switch(position)
			{
			case 0:
				return new song();
			case 1:
				return new album();
			case 2:
				return new artist();
			case 3:
				return new folder();
			}
			return cur_fragment;
		}
		@Override
		public int getCount() {
			return 4;
		}
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unbindService(mConnection);
	}
	public void main_setting()
	{
		try {
			if(mBinder.singing()==true)
			{
				String str = mBinder.getItems(3);
				mini_title.setText(str);
				//이미지 처리해야 하는 부분
				final Uri ArtworkUri =  Uri.parse("content://media/external/audio/albumart");
				Uri uri = ContentUris.withAppendedId(ArtworkUri, Long.parseLong(mBinder.songsimages()));
				String uripath = uri.toString();
				Bitmap bm = null;
				try {
					bm = Images.Media.getBitmap(getContentResolver(), uri);
				} catch (FileNotFoundException e) {
					mini_view.setImageResource(R.drawable.noimages);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(bm!=null)
					mini_view.setImageBitmap(bm);
			}
			if(mBinder.playjudge()==true)
				mini_btn.setImageResource(R.drawable.ic_pause);
			else
				mini_btn.setImageResource(R.drawable.ic_play);
		} catch (RemoteException e) {
			e.printStackTrace();}
	}
}


