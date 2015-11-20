package com.example.musicplayer;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import java.io.FileOutputStream;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service{
	public static boolean IS_SERVICE_RUNNING = false;
	int currenttime,temp=1,jari,song_id,option=0,startpos;
	String songsubtitle,songpath,songpos,songimg;
	public static String wherestr;
	Thread Playthread;
	boolean opencheck=false,mPlayjudge=false,sing=false;
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
		//Log.i("superdroid", "start");
		if(intent.getAction().equals(Constants.ACTION.START_ACTION))
			showNotification();
		return START_NOT_STICKY;
	};
	IMyService.Stub mBinder = new IMyService.Stub() {
		@Override
		public void play(final IMyServiceCallback callback) throws RemoteException {
			//���� ��ŸƮ
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
							try{
								currenttime=gettime();
							}catch(NumberFormatException e)
							{
								currenttime=0;
							}
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
				sing=true;
				createEngine();
				int temp1=initplayer(temp);
				bufSize = 8 * AudioTrack.getMinBufferSize(44100,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT);
				track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT, bufSize,AudioTrack.MODE_STREAM);
				bytes = new short[bufSize];
				opencheck = true;
				Intent intent = new Intent("mini");
				sendBroadcast(intent);				
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
			Log.d("��ġ"+songpos+"���"+songpath+"����"+songsubtitle, "�Դϴ�.");
			if(im == 1) //��ġ ��ȯ
				return songpos;
			if(im == 2) //��ι�ȯ
				return songpath;
			if(im == 3) //�����ȯ
				return songsubtitle;
			if(im == 4) //�̹�����ȯ
				return songimg;
			return null;
		}
		@Override
		public void changesong(int pos, int updown, int starts) throws RemoteException {
			Cursorquery qr = new Cursorquery(getApplicationContext());
			if(starts==1)
				qr.songlist(pos, updown,option);
			else if(starts == 2)
			{
				qr.allwhere(wherestr);
				qr.albumlist(pos, updown,option);
			}
			else if(starts == 3)
			{
				qr.allwhere(wherestr);
				qr.artistlist(pos, updown,option);
			}
			else if(starts == 4)
			{
				qr.allwhere(wherestr);
				qr.folderlist(pos, updown, option);
			}
			song_id = qr.position;
			songpath = qr.path;
			songsubtitle = qr.title;
			songimg = qr.img;
			songpos = String.valueOf(qr.position);
		}
		@Override
		public void getwhere(String st) throws RemoteException {
			wherestr = st;
		}
		@Override
		public void nextsong() throws RemoteException {
			//�ϴ� �ʱ�ȭ ��Ų��.
			Log.d("�뷡���̵�"+song_id+"�ڸ���:"+jari, "�Է��Ѱ�");
			changesong(song_id, 1, jari);
			Release();
			Log.d(songpath, "���������");
			fileopen(songpath);
			play(mCallback);
			//��ε�ĳ��Ʈ ����
			Intent intent = new Intent("endsong");
			sendBroadcast(intent);
		}
		@Override
		public void remotesetting(int start, int pos, String str, String bt) throws RemoteException {
			jari = start;
			song_id = pos;
			songsubtitle = str;
			songimg = bt;
			songpos = String.valueOf(pos);
		}
		@Override
		public boolean singing() throws RemoteException {
			// �̴� �÷��̾�� ��� ���� Ȯ�� �ҷ��� ���� ����, �뷡���� Ȯ�ο��ε�
			return sing;
		}
		@Override
		public String songsimages() throws RemoteException {
			// �̴� �÷��̾�� �̹��� ���ü� �ְ� ����
			return songimg;
		}
		@Override
		public void getsongoption(int pos) throws RemoteException {
			option=pos;
		}
		@Override
		public int getstart() throws RemoteException {
			// ��ŸƮ �� ȣ��
			return jari;
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
		Intent intent = new Intent("clear");
		sendBroadcast(intent);		
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
	private void showNotification()
	{
		//������
		Intent previousIntent = new Intent(getApplicationContext(),MyService.class);
		previousIntent.setAction(Constants.ACTION.PREV_ACTION);
		PendingIntent ppreviousIntent = PendingIntent.getService(getApplicationContext(), 0, previousIntent, 0);
		//play
		Intent playIntent = new Intent(getApplicationContext(),MyService.class);
		playIntent.setAction(Constants.ACTION.PLAY_ACTION);
		PendingIntent pplayIntent = PendingIntent.getService(getApplicationContext(), 0, playIntent, 0);
		//������
		
	}
}
