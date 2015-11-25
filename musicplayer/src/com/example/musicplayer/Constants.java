package com.example.musicplayer;

public class Constants {
	public interface ACTION{
		 public static String PREV_ACTION = "com.example.musicplayer.action.prev";
	     public static String PLAY_ACTION = "com.example.musicplayer.action.play";
	     public static String NEXT_ACTION = "com.example.musicplayer.action.next";
	     public static String EXIT_ACTION = "com.example.musicplayer.action.clear";
	     public static String START_ACTION = "com.example.musicplayer.action.startforeground";
	     public static String STOP_ACTION = "com.example.musicplayer.action.stopforeground";
	     public static String MAIN_ACTION = "com.example.musicplayer.action.main";
	     public static String MAIN_STOP_ACTION = "com.example.musicplayer.action.main_stop";
	     public static String MAIN_START_ACTION = "com.example.musicplayer.action.main_start";
	}
	public interface NOTIFICATION_ID{
		public static int FOREGROUND_SERVICE = 101;
	}
}
