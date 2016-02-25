package com.changhong.localmedia.music;

import com.changhong.localmedia.R;
import com.changhong.localmedia.Utils;
import com.changhong.view.AlwaysMarqueeTextview;
import com.changhong.view.CircleImageView;
import com.changhong.view.MusicPlayView;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MusicPlayActivity extends Activity {
	
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrev;
	private ImageButton btnMode;
	
	MusicController musicController;
	private EventBus eventBus;
	
	private MusicPlayView circleView;
	private TextView viewTime;
	private AlwaysMarqueeTextview viewName;
	private AlwaysMarqueeTextview viewSinger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_music);
		initData();
		initView();
	}

		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateModeIcon(musicController.getPlayMode());
		updateNameSinger();
		updatePlayPause(musicController.isPlay);
		updateTime(musicController.curMusic.duration);
	}

	public void onEventMainThread(MusicUpdateEvent event){
		switch (event.type) {
		case MusicUpdateEvent.TYPE_PLAY_PAUSE:
			updatePlayPause(event.isPlay);
			break;
		case MusicUpdateEvent.TYPE_MUSIC_CHANGE:
			updateNameSinger();
			break;
		case MusicUpdateEvent.TYPE_PROGRESS:
			updateTime(event.progress);
			break;
		case MusicUpdateEvent.TYPE_MODE_CHANGED:
			updateModeIcon(event.mode);
			break;
		default:
			break;
		}
	}
	
	OnClickListener controlBtnsListener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.main_imgbtn_playorpause:
				if(musicController.isPlay){
					musicController.pause();
					updatePlayPause(false);
				}else {
					musicController.start();
					updatePlayPause(true);
				}
				break;
			case R.id.main_imgbtn_next:
				musicController.playNext();
				break;
			case R.id.main_imgbtn_pre:
				musicController.playLast();
				break;
			case R.id.main_imgbtn_changemode:
				musicController.changeMode();
				break;
			default:
				break;
			}
		}
	};
	
	private void initData(){
		musicController = MusicController.getInstance(MusicPlayActivity.this);
		eventBus = EventBus.getDefault();
		eventBus.register(this);		
	}
	
	
	private void initView(){
		
		viewTime = (TextView)this.findViewById(R.id.main_time_textview);
		updateTime(0);
		viewName = (AlwaysMarqueeTextview)this.findViewById(R.id.main_title_name);
		viewSinger = (AlwaysMarqueeTextview)this.findViewById(R.id.main_title_singer);
		circleView = (MusicPlayView)this.findViewById(R.id.layout_media_play_view);

		
		btnPlay = (ImageButton)this.findViewById(R.id.main_imgbtn_playorpause);

		btnNext = (ImageButton)this.findViewById(R.id.main_imgbtn_next);
		btnPrev = (ImageButton)this.findViewById(R.id.main_imgbtn_pre);
		btnMode = (ImageButton)this.findViewById(R.id.main_imgbtn_changemode);
		
		btnPlay.setOnClickListener(controlBtnsListener);
		btnNext.setOnClickListener(controlBtnsListener);
		btnPrev.setOnClickListener(controlBtnsListener);
		btnMode.setOnClickListener(controlBtnsListener);
	}
	

	private void updatePlayPause(boolean isPlay){
		if(isPlay){
			btnPlay.setImageResource(R.drawable.player_btn_pause_style);
			if(!circleView.isPlay())
				circleView.play();
		}else {
			btnPlay.setImageResource(R.drawable.player_btn_play_style);
			if(circleView.isPlay())
				circleView.pause();
		}
	}
	
	private void updateTime(long curPosition){
		String strCur = Utils.convertMill2HMS(curPosition);
		String strDuration = Utils.convertMill2HMS(musicController.curMusic.duration);
		viewTime.setText(strCur+"/"+strDuration);
	}
	
	private void updateNameSinger(){
		viewName.setText(musicController.curMusic.name);
		viewSinger.setText(musicController.curMusic.singer);
		circleView.previous(musicController.curMusic.songPic);
	}
	
	private void updateModeIcon(int mode){
		switch (mode) {
		case MusicController.MODE_ALL:
			btnMode.setImageResource(R.drawable.player_btn_mode_repeat_all_style);
			break;
		case MusicController.MODE_RANDOM:
			btnMode.setImageResource(R.drawable.player_btn_mode_random_style);
			break;
		case MusicController.MODE_SINGLE:
			btnMode.setImageResource(R.drawable.player_btn_mode_repeat_one_style);
			break;
		default:
			btnMode.setImageResource(R.drawable.player_btn_mode_repeat_all_style);
			break;
		}
	}
	
}
