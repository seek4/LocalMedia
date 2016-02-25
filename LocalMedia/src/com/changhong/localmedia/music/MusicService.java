package com.changhong.localmedia.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service{

	private static final String TAG = "MusicService";
	EventBus eventBus; 
	MediaPlayer mMediaPlayer;
	MusicController musicController;
	private boolean isPlay = false;
	
	Timer timer;
	TimerTask timerTask;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		if(eventBus==null){
			eventBus = EventBus.getDefault();
			eventBus.register(this);
		}
		musicController = MusicController.getInstance(MusicService.this);
		if(mMediaPlayer==null){
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					musicController.playNext();
				}	
			});
		}
		if(timer==null){
			timer = new Timer();
			timer.schedule(new TimerTask() {			
				@Override
				public void run() {
					if(isPlay!=mMediaPlayer.isPlaying()){
						isPlay = mMediaPlayer.isPlaying();
						eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_PLAY_PAUSE, mMediaPlayer.isPlaying()));
					}
					eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_PROGRESS, mMediaPlayer.getCurrentPosition()));
				}
			}, 0, 1000);
		}		
		return super.onStartCommand(intent, flags, startId);
	}

	public void onEvent(MusicControlEvent event){
		switch (event.command) {
		case MusicControlEvent.CMD_PLAY_SONG:
			startPlay(event.music);
			break;
		case MusicControlEvent.CMD_PLAY_PAUSE:
			if(mMediaPlayer!=null){
				if(event.playOrPause){
					mMediaPlayer.start();
				}else {
					mMediaPlayer.pause();
				}
//				if(mMediaPlayer.isPlaying()){
//					mMediaPlayer.pause();
//				}else {
//					mMediaPlayer.start();
//				}
			}
			break;
		case MusicControlEvent.CMD_SEEK:
			if(mMediaPlayer!=null){
				mMediaPlayer.seekTo(event.progress);
			}
			break;
		default:
			break;
		}
	}

	
	public int startPlay(final Music music){
		Log.i(TAG,"startPlay "+music.name);
		mMediaPlayer.reset();
		File file = new File(music.path);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			mMediaPlayer.setDataSource(fis.getFD());
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {			
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();	
					isPlay = true;
					eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_MUSIC_CHANGE, music));
					eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_PLAY_PAUSE, true));
					eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_DURATION_GOTTON, mMediaPlayer.getDuration()));
				}
			});
			mMediaPlayer.prepareAsync();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 1;
	}
	
}
