package com.example.musicplayer;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import java.io.FileOutputStream;
import java.util.Arrays;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service{
	int currenttime,temp=1;
	Thread Playthread;
	boolean opencheck=false,mPlayjudge=false;
	int readframe_check=1,bufSize,count=0,end=0;
	private AudioTrack track;
	private FileOutputStream os;
	short[] bytes;
	@Override
	public void onCreate(){
		super.onCreate();
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
//		Log.i("superdroid", "start");
		return START_NOT_STICKY;
		
	};
	IMyService.Stub mBinder = new IMyService.Stub() {
		@Override
		public void play(final IMyServiceCallback callback) throws RemoteException {
			//¹ÂÁ÷ ½ºÅ¸Æ®
			mPlayjudge=true;
			count++;
			callback.callback(1);
			readframe_check=1;
			Playthread = new Thread("Play thread") {
					@Override
					public void run() {
						track.play();
						while((temp!=0) && (readframe_check==1))
						{
							temp = bufferengine(bytes);
							track.write(bytes, 0, temp);
							currenttime=gettime();	
						}
						if(temp==0)
						{
							try {
								callback.callback(3);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
				};
				Playthread.start();
				
		}
		@Override
		public void pause(IMyServiceCallback callback) throws RemoteException {
			callback.callback(2);
			track.pause();
			mPlayjudge=false;
			readframe_check=0;
		}
		@Override
		public boolean playjudge() throws RemoteException {
			return mPlayjudge;
		}

		@Override
		public int musicduration() throws RemoteException {
			return getduration();
		}

		@Override
		public int current() throws RemoteException {
			return currenttime;
		}

		@Override
		public void getvalue(int value) throws RemoteException {
				setseeking(value);
		}
		@Override
		public void fileopen(String temp) throws RemoteException {
			if(opencheck==false)
			{
				createEngine();
				int temp1=initplayer(temp);
				bufSize = 8 * AudioTrack.getMinBufferSize(44100,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT);
				track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT, bufSize,AudioTrack.MODE_STREAM);
				bytes = new short[bufSize];
				opencheck = true;
			}
		}
		@Override
		public void Release() throws RemoteException {
			mPlayjudge=false;
			readframe_check=0;
			track.release();
			ffmpegRelease();
			opencheck=false;
			Playthread=null;
			track=null;
			bytes=null;
		}
		@Override
		public int PlayingCount() throws RemoteException {
			return count;
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("superdroid", "onBind");
		return mBinder;
	}
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i("superdroid", "onUnBind");
		return super.onUnbind(intent);
	};
	@Override
	public void onDestroy()
	{
		Log.i("superdroid","onDestroy");
		super.onDestroy();
	}
	static{System.loadLibrary("ffmpeg");}
	private native int bufferengine(short[] array);
	private native void ffmpegRelease();
	private native void createEngine();
	private native int initplayer(String path);
	private native int getduration();
	private native int gettime();
	private native int setseeking(int temp);
}
