package com.example.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FragmentActivity implements OnClickListener{
	private ViewPager mPager;
	Button btn_song,btn_artist,btn_album,btn_folder;
	int Max_PAGE = 4;
	Fragment cur_fragment = new Fragment();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//viewpager
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
		mPager.setCurrentItem(0);
		//버튼 셋팅
		btn_song = (Button)findViewById(R.id.btn_song);
		btn_song.setOnClickListener(this);
		btn_artist = (Button)findViewById(R.id.btn_artist);
		btn_artist.setOnClickListener(this);
		btn_album = (Button)findViewById(R.id.btn_album);
		btn_album.setOnClickListener(this);
		btn_folder = (Button)findViewById(R.id.btn_folder);
		btn_folder.setOnClickListener(this);
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
}


