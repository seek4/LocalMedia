package com.changhong.localmedia;


import java.util.Timer;
import java.util.TimerTask;

import com.changhong.localmedia.music.MusicController;
import com.changhong.localmedia.music.MusicFragment;
import com.changhong.localmedia.vedio.VedioFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "LocalMedia";	
	private TextView titleMusic;
	private TextView titleVedio;
	Looper looper;
	Handler handler;
	/**
	 * curMode:  0:Music
	 *           1:Vedio
	 */
	private int curMode = 0;
	private static final int MODE_MUSIC = 0;
	private static final int MODE_VEDIO = 1;	
	/**
	 * the size of possible mode
	 */
	private static int SIZE_MODE = 2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);	
		titleMusic = (TextView)this.findViewById(R.id.title_music);
		titleVedio = (TextView)this.findViewById(R.id.title_vedio);	
		initData();
		initService();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initService(){
		Intent musicIntent = new Intent();
		musicIntent.setAction("action.changhong.localmusic.musicservice");		
		try {
			startService(musicIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void initData(){
		mFragmentManager = getFragmentManager();
		curMode = MODE_MUSIC;		
		switchContent(curMode);	
		updateTitle(curMode);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			curMode--;
			if(curMode<0){
				curMode=0;
			}else {
				switchContent(curMode);
				updateTitle(curMode);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			curMode++;
			if(curMode>=SIZE_MODE){
				curMode = SIZE_MODE-1;
			}else {
				switchContent(curMode);
				updateTitle(curMode);
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			exitBy2Click();
			return true;
		default:
			break;
		}	
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean isExit = false;
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, R.string.click_again_exit, Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}
	
	
	private FragmentManager mFragmentManager;
	private Fragment musicFragment;
	private Fragment vedioFragment;
	private void switchContent(int mode){
		switch (mode) {
		case MODE_MUSIC:
			if(musicFragment==null){
				musicFragment = new MusicFragment();
			}
			mFragmentManager.beginTransaction().replace(R.id.layout_content, musicFragment).commit();
			break;
		case MODE_VEDIO:
			if(vedioFragment==null){
				vedioFragment = new VedioFragment();
			}			
			mFragmentManager.beginTransaction().replace(R.id.layout_content, vedioFragment).commit();
			break;
		default:
			break;
		}
	}
	
	private void updateTitle(int mode){
		switch(mode){
		case MODE_MUSIC:
			titleMusic.setBackgroundResource(R.drawable.more_filter_option_selected_nor);
			titleVedio.setBackgroundColor(Color.TRANSPARENT);
			break;
		case MODE_VEDIO:
			titleMusic.setBackgroundColor(Color.TRANSPARENT);
			titleVedio.setBackgroundResource(R.drawable.more_filter_option_selected_nor);
			break;
		default:
			break;
		}
	}
	
}
