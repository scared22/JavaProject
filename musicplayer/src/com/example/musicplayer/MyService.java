package com.example.musicplayer;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import java.io.FileOutputStream;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service{
	int currenttime,temp=1,jari,song_id;
	String songsubtitle,songpath,songpos;
	public static String wherestr;
	Thread Playthread;
	boolean opencheck=false,mPlayjudge=false;
	int readframe_check=1,bufSize,count=0,end=0;
	private AudioTrack track;
	private FileOutputStream os;
	short[] bytes;
	IMyServiceCallback mCallback = new IMyServiceCallback.Stub() {
		@Override
		public void callback(final int num) throws RemoteException {
		}
	};
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
			//뮤직 스타트
			mPlayjudge=true;
			count++;
			callback.callback(1);
			readframe_check=1;
			Playthread = new Thread("Play thread") {
					@Override
					public void run() {
						track.play();
						do
						{
							temp = bufferengine(bytes);
							track.write(bytes, 0, temp);
							currenttime=gettime();	
						}while((temp!=0) && (readframe_check==1));
						if(temp==0)
						{
							try {
								nextsong();
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
			bufSize=0;
			bytes=null;
		}
		@Override
		public int PlayingCount() throws RemoteException {
			return count;
		}
		public String getItems(int im) throws RemoteException {
			if(im == 1) //위치 반환
				return songpos;
			if(im == 2) //경로반환
				return songpath;
			if(im == 3) //제목반환
				return songsubtitle;
			return null;
		}
		@Override
		public void changesong(int pos, int updown, int starts) throws RemoteException {
			Cursorquery qr = new Cursorquery(getApplicationContext());
			if(starts==1)
				qr.songlist(pos, updown);
			else if(starts == 2)
			{
				qr.allwhere(wherestr);
				qr.albumlist(pos, updown);
			}
			else if(starts == 3)
			{
				qr.allwhere(wherestr);
				qr.artistlist(pos, updown);
			}
			song_id = qr.position;
			songpath = qr.path;
			songsubtitle = qr.title;
			songpos = String.valueOf(qr.position);
		}
		@Override
		public void getwhere(String st) throws RemoteException {
			wherestr = st;
		}
		@Override
		public void nextsong() throws RemoteException {
			//일단 초기화 시킨다.
			Log.d("노래아이디"+song_id+"자리값:"+jari, "입력한값");
			changesong(song_id, 1, jari);
			Release();
			Log.d(songpath, "다음값경로");
			fileopen(songpath);
			play(mCallback);
			//브로드캐스트 보냄
			Intent intent = new Intent("endsong");
			sendBroadcast(intent);
		}
		@Override
		public void remotesetting(int start, int pos) throws RemoteException {
			jari = start;
			song_id = pos;
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
