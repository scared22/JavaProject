package com.example.musicplayer;
import com.example.musicplayer.IMyServiceCallback;
interface IMyService
{
	boolean playjudge();
	oneway void play(IMyServiceCallback callback);
	oneway void pause(IMyServiceCallback callback);
	void fileopen(String temp);
	int musicduration();
	int current();
	void getvalue(int value);
	void Release();
	int PlayingCount();
}
