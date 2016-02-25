package com.changhong.localmedia.vedio;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import com.changhong.localmedia.R;
import com.changhong.localmedia.Utils;

import de.greenrobot.event.EventBus;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class VedioPlayActivity extends Activity {
	
	private static final String TAG = "VedioPlayActivity";
	VedioController vedioController;
	MediaPlayer mediaPlayer;
	EventBus eventBus;
	UIHandler uiHandler = new UIHandler(this);
	private SurfaceView surfaceView;
	private Point point;
	private SeekBar seekBar;
	private SurfaceHolder surfaceHolder;
	private TextView textCurTime;
	private TextView textEndTime;
	private TextView textName;
	private ImageView imagePlay;
	
	int position = 0;
	public boolean libInited = false;
	public boolean viewCreated = false;

	//	get the current progress and status
	private long duration = 0; 	// duration of the playing vedio
	Timer updateTimer;
	public boolean isPlay = false;
	
	private boolean viewInited = false;
	// control the seekbar
	private int seekProgress = 0;
	boolean isDragging = false;
	private Timer hideInfoTimer;
	private HideInfoTask hideInfoTask;
	private Timer seekTaskTimer;
	private SeekTask seekTask;
	
	public boolean playNext = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_vedio);
		
		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);
		initView();	
		
		vedioController = VedioController.getInstance(VedioPlayActivity.this);
		
		eventBus = EventBus.getDefault();
		eventBus.register(this);
		// init vitamio lib
		new AsyncTask<Object, Object, Boolean>() {
			@Override
			protected void onPreExecute() {

			}
			@Override
			protected Boolean doInBackground(Object... params) {
				return Vitamio.initialize(VedioPlayActivity.this);
			}
			@Override
			protected void onPostExecute(Boolean inited) {
				if(!inited)
					return;
				libInited = true;
				if (viewInited) {
					vedioController.playVedio(position);
				}
			}

		}.execute();	
	}
	
	
	@Override
	protected void onDestroy() {
		eventBus.unregister(this);	
		if(mediaPlayer!=null&&mediaPlayer.isPlaying())
			mediaPlayer.stop();
		mediaPlayer.release();
		if(updateTimer!=null){
			updateTimer.cancel();
		}
		surfaceView.setVisibility(View.GONE);
		if(playNext){
			int nextPosition = vedioController.getNextPosition();
			Intent intent = new Intent(VedioPlayActivity.this, VedioPlayActivity.class);
			intent.putExtra("position", nextPosition);
			startActivity(intent);
		}
		super.onDestroy();
	}

	
	private void initView(){
		surfaceView = (SurfaceView)this.findViewById(R.id.surface_video);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new Callback() {			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
				Log.i(TAG,"surfaceDestroyed!!");
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.i(TAG,"surfaceCreated!!");
				viewInited = true;
				if(libInited)
					vedioController.playVedio(position);
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				Log.i(TAG,"surfaceChanged!!");
//				if(mediaPlayer!=null){
//					mediaPlayer.setDisplay(holder);
//				}
				
			}
		});
		seekBar = (SeekBar)this.findViewById(R.id.skbProgress);
		seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		textCurTime = (TextView)this.findViewById(R.id.cur_time);
		textEndTime = (TextView)this.findViewById(R.id.end_time);
		textName = (TextView)this.findViewById(R.id.moive_name);
		imagePlay = (ImageView)this.findViewById(R.id.play_pause);
	}
	
	
	private static final int MSG_SHOW_INFO = 0x01;
	private static final int MSG_HIDE_INFO = 0x02;
	private static final int MSG_SEEK = 0x03;
	private class UIHandler extends Handler{
		private WeakReference<Activity> mActivity;
		
		public UIHandler(Activity activity){
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_INFO:
				showPlayInfo(true);
				break;
			case MSG_HIDE_INFO:
				showPlayInfo(false);
				break;
			case MSG_SEEK:
				
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	}
	



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
				vedioController.pause();
			}else if (mediaPlayer!=null&&!mediaPlayer.isPlaying()) {
				vedioController.start();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			showPlayInfo(true);
			scheduleHide();
			break;
		case KeyEvent.KEYCODE_BACK:
			exitBy2Click();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:	
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			break;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	public void onEvent(VedioControlEvent event){
		switch (event.command) {
		case VedioControlEvent.CMD_PLAY_PAUSE:
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				imagePlay.setVisibility(View.VISIBLE);
				imagePlay.bringToFront();
			}else {
				mediaPlayer.start();
				imagePlay.setVisibility(View.INVISIBLE);
			}
			break;
		case VedioControlEvent.CMD_PLAY_VEDIO:
			startPlay(event.vedio);
			break;
		case VedioControlEvent.CMD_SEEK:
			mediaPlayer.seekTo(event.progress);
			break;
		default:
			break;
		}
	}
	
	public void onEventMainThread(VedioUpdateEvent event){
		switch (event.type) {
		case VedioUpdateEvent.TYPE_DURATION_GOTTON:
			duration = event.duration;
			textEndTime.setText(Utils.convertMill2HMS(event.duration));
			break;
		case VedioUpdateEvent.TYPE_PROGRESS:
			if(!isDragging){
				textCurTime.setText(Utils.convertMill2HMS(event.duration));
				seekBar.setProgress((int) (event.duration*seekBar.getMax()/duration));
			}
			break;
		case VedioUpdateEvent.TYPE_VEDIO_CHANGE:
			textName.setText(event.vedio.name);
		default:
			break;
		}
	}

	
	OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser){
				isDragging = true;
				seekProgress = progress;
				textCurTime.setText(Utils.convertMill2HMS(duration*progress/seekBar.getMax()));
				if(seekTaskTimer==null){
					seekTaskTimer = new Timer();
					seekTask = new SeekTask();
					seekTaskTimer.schedule(seekTask, 1000);
				}else {
					seekTask.cancel();
					seekTaskTimer.cancel();
					seekTaskTimer = new Timer();
					seekTask = new SeekTask();
					seekTaskTimer.schedule(seekTask, 1000);
				}
			}
			
		}
	};
	
	
	private int startPlay(Vedio vedio){
		Log.i(TAG,"startPlay "+vedio.name);
			mediaPlayer = new MediaPlayer(VedioPlayActivity.this);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {			
				@Override
				public void onCompletion(MediaPlayer mp) {
//					finish();
//					playNext = true;
					//Toast.makeText(VedioPlayActivity.this, getString(R.string.vedio_play_next), Toast.LENGTH_SHORT).show();;
					//vedioController.playNext();
				}
			});
			//mediaPlayer.setDisplay(surfaceView.getHolder());
			mediaPlayer.setScreenOnWhilePlaying(true);
			updateTimer = new Timer();
			updateTimer.schedule(new UpdateTask(), 0, 1000);	
		FileInputStream fis = null;
		try {
			mediaPlayer.setDataSource(vedio.path);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {			
				@Override
				public void onPrepared(MediaPlayer mp) {
					resizeSurfaceview();					
					mp.start();	
					uiHandler.sendEmptyMessage(MSG_SHOW_INFO);
					scheduleHide();
					eventBus.post(new VedioUpdateEvent(VedioUpdateEvent.TYPE_PLAY_PAUSE, true));
					eventBus.post(new VedioUpdateEvent(VedioUpdateEvent.TYPE_DURATION_GOTTON, mediaPlayer.getDuration()));
				}
			});
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			return -1;
		} catch (SecurityException e1) {
			e1.printStackTrace();
			return -1;
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
			return -1;
		} catch (IOException e1) {
			e1.printStackTrace();
			return -1;
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}


	private void resizeSurfaceview() {
		// TODO Auto-generated method stub
		Log.i(TAG,"resizeSurfaceview");
		RelativeLayout.LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
		
		DisplayMetrics disp = getResources().getDisplayMetrics();
		int windowWidth = disp.widthPixels, windowHeight = disp.heightPixels;	
		int mSurfaceHeight = mediaPlayer.getVideoHeight();
		int mSurfaceWidth = mediaPlayer.getVideoWidth();
		
		lp.width = windowWidth;
		lp.height = windowHeight;
		surfaceView.setLayoutParams(lp);

		Log.i(TAG,"windowWidth >>"+windowWidth+"   windowHeight>>"+windowHeight);
		surfaceView.getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);  
		mediaPlayer.setDisplay(surfaceView.getHolder());
	}
	
	/**
	 * the task to excute seek
	 *
	 */
	class SeekTask extends TimerTask{

		@Override
		public void run() {
			Log.i(TAG,"SeekTask");
			vedioController.seekProgress(seekProgress*duration/seekBar.getMax());
			isDragging = false;
		}
		
	}
	
	/**
	 * hide the play info
	 */
	class HideInfoTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG,"HideSeekbarTask");
			uiHandler.sendEmptyMessage(MSG_HIDE_INFO);
		}
		
	}
	

	/**
	 * the task to get the current position and status
	 * @author yangtong
	 *
	 */
	class UpdateTask extends TimerTask{

		@Override
		public void run() {
			boolean nowPlay = false;
			if(mediaPlayer.isPlaying()){
				nowPlay = true;
				eventBus.post(new VedioUpdateEvent(VedioUpdateEvent.TYPE_PROGRESS, mediaPlayer.getCurrentPosition()));
			}else {
				nowPlay = false;
			}
			if(nowPlay!=isPlay){
				isPlay = nowPlay;
				eventBus.post(new VedioUpdateEvent(VedioControlEvent.CMD_PLAY_PAUSE, isPlay));
			}
		}
		
	}
	
	private boolean isExit = false;
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, R.string.click_again_exit_video, Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
		}
	}
	
	
	private void scheduleHide(){
		if(hideInfoTimer==null){
			hideInfoTimer = new Timer();
			hideInfoTask = new HideInfoTask();
			hideInfoTimer.schedule(hideInfoTask, 3000);
		}else {
			hideInfoTask.cancel();
			hideInfoTimer.cancel();
			hideInfoTimer = new Timer();
			hideInfoTask = new HideInfoTask();
			hideInfoTimer.schedule(hideInfoTask, 3000);
		}
	}

	/**
	 * show/hide the play info
	 * @param ifShow
	 */
	public void showPlayInfo(boolean ifShow){
		if(ifShow){
			seekBar.setVisibility(View.VISIBLE);
			textCurTime.setVisibility(View.VISIBLE);
			textEndTime.setVisibility(View.VISIBLE);
		}else {
			seekBar.setVisibility(View.INVISIBLE);
			textCurTime.setVisibility(View.INVISIBLE);
			textEndTime.setVisibility(View.INVISIBLE);
		}
	}
	
}
