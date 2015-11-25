package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.BroadcastReceiver;
public class player extends Activity implements OnClickListener, OnSeekBarChangeListener {
	Thread seekthread;
	int pos,changecheck=0,updown=1,start,optionpos=0;
	public static Context mContext;
	String cm=null,cs = null;
	boolean seekcheck=false;
	SeekBar Playerseekbar,volumeseek;
	ImageButton btn_play,btn_list,btn_fast_foward,btn_fast,sing_option;
	TextView text_current,text_alltime,title;
	String temp,subtitle,wheres,img;
	ImageView playeralbums;
	Intent ServiceIntent;
	private IMyService mBinder = null;
	IMyServiceCallback mCallback = new IMyServiceCallback.Stub() {
		@Override
		public void callback(final int num) throws RemoteException {
		}
	};
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBinder = null;
			Log.i("superdroid", "disconnect");
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("superdroid", "connect");
			mBinder = IMyService.Stub.asInterface(service);
			setting();
		}
	};
	//broadcast
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action=="endsong")
			{
				action=null;
				setting2();
			}
			if(action=="chsong")
			{			
				try {
					changecheck=1;
					pos=Integer.parseInt(mBinder.getItems(1));
					temp=mBinder.getItems(2);		
					subtitle=mBinder.getItems(3);
					if(cs != null && cm != null)
					{
						img = mBinder.getItems(4);
						playeralbum(img);
					}
					title.setText(subtitle);
					title.setSelected(true);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}	
			}
			if(action=="noticontrol")
			{
				try {
					if(mBinder.playjudge()==false)
					{
						btn_play.setImageResource(R.drawable.ic_play);
						seekcheck=true;
					}
					else
					{
						btn_play.setImageResource(R.drawable.ic_pause);
						seekcheck=false;
						servicethread();
					}
				} catch (RemoteException e) {e.printStackTrace();}				
			}
		}
	};
	//
	public void setting()
	{	
		try {		
			if(changecheck==0)
			{
				Intent intent1 = getIntent();
				start = intent1.getIntExtra("starts", 0);
				if(start!=5)
				{
					temp = intent1.getStringExtra("paths");
					subtitle = intent1.getStringExtra("titles");
					pos = intent1.getIntExtra("positions", -1);
					img = intent1.getStringExtra("imgs");
					mBinder.remotesetting(start, pos, subtitle,img);
					title.setText(subtitle);
					title.setSelected(true);
					if(cm!=null && cs!= null)
					playeralbum(img);
					if((start == 2) || (start == 3) ||(start == 4))
					{
						wheres = intent1.getStringExtra("where");
						mBinder.getwhere(wheres);
					}
				}
				
			}
			else
				changecheck=0;
			ServiceIntent.setAction(Constants.ACTION.START_ACTION);
			startService(ServiceIntent);
			if(start!=5)
			{
				
				if(mBinder.PlayingCount()!=0)
					mBinder.Release();
				mBinder.fileopen(temp);
				Playerseekbar.setMax(mBinder.musicduration());
				text_alltime.setText(String.format("%02d:%02d", (int)mBinder.musicduration()/60,(int)mBinder.musicduration()%60));
				cm=Long.toString(mBinder.current()/60);
				cs=Long.toString(mBinder.current()%60);	
				text_current.setText(cm.trim()+":"+cs.trim());
				btn_test();
			}
			else
			{
				start=mBinder.getstart();
				setting2();
			}
		}
		 catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setting2()
	{
		try {
				Log.d("나옴", "setting2");
				pos=Integer.parseInt(mBinder.getItems(1));		
				temp=mBinder.getItems(2);		
				subtitle=mBinder.getItems(3);
				img = mBinder.getItems(4);
				playeralbum(img);
				title.setText(subtitle);
				Playerseekbar.setMax(mBinder.musicduration());
				text_alltime.setText(String.format("%02d:%02d", (int)mBinder.musicduration()/60,(int)mBinder.musicduration()%60));
				text_current.setText(String.format("%02d:%02d",mBinder.current()/60,mBinder.current()%60));
				servicethread();
				seekcheck=false;
				btn_play.setImageResource(R.drawable.ic_pause);
				if(mBinder.playjudge()==false)
					btn_play.setImageResource(R.drawable.ic_play);
			} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void seeking()
	{
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					text_current.setText(String.format("%02d:%02d",mBinder.current()/60,mBinder.current()%60));
					Playerseekbar.setProgress(mBinder.current());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void servicethread()
	{
		seekthread = new Thread("Service Thread") {
			@Override
			public void run()
			{
				try {
					while((seekcheck!=true)&&(mBinder.playjudge()==true))
					{
						try {
							Thread.sleep(1000);
							seeking();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(seekcheck)
						seekthread=null;
				} catch (RemoteException e) {}
			}
		};
		seekthread.start();
	}
	public void btn_test()
	{
		try {
			Intent intent10 = new Intent(this,MyService.class);
			intent10.setAction(Constants.ACTION.START_ACTION);
			startService(intent10);
			if(mBinder.playjudge()==false)
			{
				btn_play.setImageResource(R.drawable.ic_pause);
				mBinder.play(mCallback);
				servicethread();
				seekcheck=false;
			}
			else
			{
				btn_play.setImageResource(R.drawable.ic_play);
				seekcheck=true;
				mBinder.pause(mCallback);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void songchange()
	{
		Log.d(""+pos, "변경전");
		seekcheck=true;
			try {
				if(start == 1)
					mBinder.changesong(pos, updown, 1);
				if(start == 2)		
					mBinder.changesong(pos, updown, 2);
				if(start == 3)
					mBinder.changesong(pos, updown, 3);
				if(start == 4)
					mBinder.changesong(pos, updown, 4);
				pos=Integer.parseInt(mBinder.getItems(1));		
				temp=mBinder.getItems(2);		
				subtitle=mBinder.getItems(3);
				img = mBinder.getItems(4);
				playeralbum(img);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		title.setText(subtitle);
		changecheck=1;
		updown=1;
		setting();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		btn_play = (ImageButton)findViewById(R.id.btn_play);
		btn_play.setOnClickListener(this);
		btn_list = (ImageButton)findViewById(R.id.btn_list);
		btn_list.setOnClickListener(this);
		btn_fast = (ImageButton)findViewById(R.id.btn_fast);
		btn_fast.setOnClickListener(this);
		btn_fast_foward = (ImageButton)findViewById(R.id.btn_fast_foward);
		btn_fast_foward.setOnClickListener(this);
		text_alltime = (TextView)findViewById(R.id.text_alltime);
		text_current = (TextView)findViewById(R.id.text_current);
		Playerseekbar = (SeekBar)findViewById(R.id.playerBar);
		title = (TextView)findViewById(R.id.title);
		volumeseek = (SeekBar)findViewById(R.id.volumeseek);
		playeralbums = (ImageView)findViewById(R.id.playeralbum);
		final AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curvol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sing_option = (ImageButton)findViewById(R.id.select_option);
		sing_option.setOnClickListener(this);
		volumeseek.setMax(nMax);
		volumeseek.setProgress(curvol);
		volumeseek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}	
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress, 0);
			}
		});
		mContext=this;
		//service
		ServiceIntent = new Intent(this,MyService.class);
		bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);
		Playerseekbar.setOnSeekBarChangeListener(this);
		//브로드캐스트 리시버 등록
		IntentFilter Filter = new IntentFilter("endsong");   
		IntentFilter Filter1 = new IntentFilter("chsong");
		IntentFilter Filter2 = new IntentFilter("noticontrol");
		registerReceiver(receiver, Filter);
		registerReceiver(receiver, Filter1);
		registerReceiver(receiver, Filter2);
	}
	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btn_play)
			btn_test();
		if(v.getId()==R.id.btn_list)
		{
			finish();
			Intent intent = new Intent("back");
			sendBroadcast(intent);
		}
		if(v.getId()==R.id.btn_fast)
		{
			updown=0;
			songchange();
		}
		if(v.getId()==R.id.btn_fast_foward)
		{
			updown=1;
			songchange();
		}
		if(v.getId()==R.id.select_option)
		{
			try {
			if(optionpos == 0)
				sing_option.setImageResource(R.drawable.unrepet);
			else if(optionpos == 1)
				sing_option.setImageResource(R.drawable.repet);
			else if(optionpos == 2)
				sing_option.setImageResource(R.drawable.shuffle);
			mBinder.getsongoption(optionpos);
			if(optionpos>2)
			{
				optionpos=0;
				sing_option.setImageResource(R.drawable.unrepet);
				mBinder.getsongoption(optionpos);
			}
			else
				optionpos++;
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser)
		{
			seekcheck=true;
			try {
				mBinder.pause(mCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		seekcheck=true;
		try {
			mBinder.pause(mCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int curprogress = seekBar.getProgress();
		try {
			mBinder.getvalue(curprogress);
			seekcheck=false;
			if(mBinder.playjudge()==false)
				btn_play.setImageResource(R.drawable.ic_pause);
			mBinder.play(mCallback);
			servicethread();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
              super.onConfigurationChanged(newConfig);
    }
	public void playeralbum(String temp)
	{
		final Uri ArtworkUri =  Uri.parse("content://media/external/audio/albumart");
		Uri uri = ContentUris.withAppendedId(ArtworkUri, Long.parseLong(temp));
		String uripath = uri.toString();
		Bitmap bm = null;
		try {
			bm = Images.Media.getBitmap(mContext.getContentResolver(), uri);
		} catch (FileNotFoundException e) {
			playeralbums.setImageResource(R.drawable.noimages2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bm!=null)
			playeralbums.setImageBitmap(bm);
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

