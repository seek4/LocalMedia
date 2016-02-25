package com.changhong.localmedia.music;

public class MusicUpdateEvent {
	public static final int TYPE_PLAY_PAUSE = 0;
	public static final int TYPE_MUSIC_CHANGE = 1;
	public static final int TYPE_PROGRESS = 2;
	public static final int TYPE_DURATION_GOTTON = 3;
	public static final int TYPE_MODE_CHANGED = 4;	// play mode changed
	public static final int TYPE_MUSIC_DELETED = 5;
	
	public final int type;
	public int progress = 0;
	public Music music = null;
	public boolean isPlay = false;
	public int mode = -1;
	
	public MusicUpdateEvent(int type,boolean isPlay){
		this.type = type;
		this.isPlay = isPlay;
	}
	
	public MusicUpdateEvent(int type,int value){
		this.type = type;
		if(type==TYPE_PROGRESS){
			this.progress = value;
		}else if (type==TYPE_MODE_CHANGED) {
			this.mode = value;
		}else if (type==TYPE_DURATION_GOTTON) {
			this.progress = value;
		}
	}
	
	public MusicUpdateEvent(int type,Music music){
		this.type = type;
		this.music = music;
	}
	
	public boolean isSuccess;
	public MusicUpdateEvent(int type,boolean isSuccess,Music music){
		this.type = type;
		this.isSuccess = isSuccess;
		this.music = music;
	}
}
