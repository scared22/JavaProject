package com.example.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class player extends Activity implements OnClickListener, OnSeekBarChangeListener {
	Thread seekthread;
	int cur_min,cur_sec,threadkill=0;
	public static Context mContext;
	boolean seekcheck=false;
	SeekBar Playerseekbar;
	ImageButton btn_play,btn_list;
	TextView text_current,text_alltime,title;
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
	public void setting()
	{
		try {
			Intent intent1 = getIntent();
			String temp = intent1.getStringExtra("paths");
			String subtitle = intent1.getStringExtra("titles");
			title.setText(subtitle);
			int start = intent1.getIntExtra("start", 0);
			if(start==1)
			{
				if(mBinder.PlayingCount()!=0)
					mBinder.Release();
				mBinder.fileopen(temp);
				Playerseekbar.setMax(mBinder.musicduration());
				text_alltime.setText(String.format("%02d:%02d", (int)mBinder.musicduration()/60,(int)mBinder.musicduration()%60));
				text_current.setText(String.format("%02d:%02d",mBinder.current()/60,cur_sec=mBinder.current()%60));
				btn_test();
			}
		}
		 catch (RemoteException e) {
			e.printStackTrace();
		}
		 
		 
		 
	}
	public void seeking()
	{
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					text_current.setText(String.format("%02d:%02d",mBinder.current()/60,cur_sec=mBinder.current()%60));
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
					while(seekcheck!=true&&mBinder.playjudge()==true)
					{
						try {
							Thread.sleep(1000);
							seeking();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(threadkill==1)
						{
							threadkill=0;
							break;
						}
					}
				} catch (RemoteException e) {}
			}
		};
		seekthread.start();
	}
	public void btn_test()
	{
		try {
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		btn_play = (ImageButton)findViewById(R.id.btn_play);
		btn_play.setOnClickListener(this);
		btn_list = (ImageButton)findViewById(R.id.btn_list);
		btn_list.setOnClickListener(this);
		text_alltime = (TextView)findViewById(R.id.text_alltime);
		text_current = (TextView)findViewById(R.id.text_current);
		Playerseekbar = (SeekBar)findViewById(R.id.playerBar);
		title = (TextView)findViewById(R.id.title);
		mContext=this;
		//service
		Intent ServiceIntent = new Intent(this,MyService.class);
		bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);
		startService(ServiceIntent);
		Playerseekbar.setOnSeekBarChangeListener(this);
	}
	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btn_play)
			btn_test();
		if(v.getId()==R.id.btn_list)
		{
			Intent intent = new Intent(player.this,MainActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
			mBinder.play(mCallback);
			servicethread();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void onDestroy(){
		super.onDestroy();
		unbindService(mConnection);
	}
}

