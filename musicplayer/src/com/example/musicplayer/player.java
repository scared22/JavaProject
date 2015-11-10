package com.example.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.BroadcastReceiver;
public class player extends Activity implements OnClickListener, OnSeekBarChangeListener {
	Thread seekthread;
	int pos,changecheck=0,updown=1,start;
	public static Context mContext;
	boolean seekcheck=false;
	SeekBar Playerseekbar;
	ImageButton btn_play,btn_list,btn_fast_foward,btn_fast;
	TextView text_current,text_alltime,title;
	String temp,subtitle;
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
			Log.d(action, "입니다.");
			if(action=="endsong")
			{
				action=null;
				songchange();
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
				temp = intent1.getStringExtra("paths");
				start = intent1.getIntExtra("starts", 0);
				subtitle = intent1.getStringExtra("titles");
				pos = intent1.getIntExtra("positions", -1);
				title.setText(subtitle);
			}
			else
				changecheck=0;
			if(mBinder.PlayingCount()!=0)
				mBinder.Release();
			mBinder.fileopen(temp);
			Playerseekbar.setMax(mBinder.musicduration());
			text_alltime.setText(String.format("%02d:%02d", (int)mBinder.musicduration()/60,(int)mBinder.musicduration()%60));
			text_current.setText(String.format("%02d:%02d",mBinder.current()/60,mBinder.current()%60));
			btn_test();
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
		Cursorquery qr = new Cursorquery(mContext);
		Log.d(""+pos, "변경전");
		seekcheck=true;
		if(start==1)
		{
			qr.songlist(pos,updown);
			temp=qr.path;
			subtitle=qr.title;
			pos = qr.position;
		}
		if(start==2)
		{
			pos = ((songproperties)songproperties.mContext).getpos(pos, updown);
			temp = ((songproperties)songproperties.mContext).getpath(pos);
			subtitle = ((songproperties)songproperties.mContext).gettitle(pos);
		}
		if(start==3)
		{
			pos = ((artist_songlist)artist_songlist.mContext).getpos(pos, updown);
			temp = ((artist_songlist)artist_songlist.mContext).getpath(pos);
			subtitle = ((artist_songlist)artist_songlist.mContext).gettitle(pos);
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
		mContext=this;
		//service
		Intent ServiceIntent = new Intent(this,MyService.class);
		bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);
		startService(ServiceIntent);
		Playerseekbar.setOnSeekBarChangeListener(this);
		//브로드캐스트 리시버 등록
		IntentFilter Filter = new IntentFilter("endsong");        
		registerReceiver(receiver, Filter);
	}
	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btn_play)
			btn_test();
		if(v.getId()==R.id.btn_list)
			finish();
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
	public void onDestroy(){
		super.onDestroy();
		unbindService(mConnection);
	}
	
}

