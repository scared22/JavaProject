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
	void changesong(int pos, int updown, int starts);
	String getItems(int im);
	void getwhere(String st);
	void nextsong();
	void remotesetting(int start, int pos, String str);
	boolean singing();
}
