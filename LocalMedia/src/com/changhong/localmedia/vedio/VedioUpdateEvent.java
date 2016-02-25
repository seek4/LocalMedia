package com.changhong.localmedia.vedio;

import com.changhong.localmedia.music.Music;

public class VedioUpdateEvent {
	public static final int TYPE_PLAY_PAUSE = 0;
	public static final int TYPE_VEDIO_CHANGE = 1;
	public static final int TYPE_PROGRESS = 2;
	public static final int TYPE_DURATION_GOTTON = 3;
	
	public int type = -1;
	public Vedio vedio = null;
	public boolean isPlay = false;
	public long duration = -1;
	
	public VedioUpdateEvent(int type,boolean isPlay){
		this.type = type;
		this.isPlay = isPlay;
	}
	
	
	public VedioUpdateEvent(int type,Vedio vedio){
		this.type = type;
		this.vedio = vedio;
	}

	/**
	 * if type==DurationGotton then duration means the whole duration
	 * if type==TypeProgress the duration means the current duration
	 * @param type
	 * @param duration
	 */
	public VedioUpdateEvent(int type, long duration) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.duration = duration;
	}
}
