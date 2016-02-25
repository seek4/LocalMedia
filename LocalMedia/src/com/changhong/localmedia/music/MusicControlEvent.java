package com.changhong.localmedia.music;

public class MusicControlEvent {

	public static final int CMD_PLAY_PAUSE = 0;
	public static final int CMD_PLAY_SONG = 1;
	public static final int CMD_SEEK = 2;
	
	public int command = -1;
	public Music music = null;
	public int progress = 0;
	public boolean playOrPause = false;
	
	/**
	 * 
	 */
	public MusicControlEvent(int command,Music music){
		this.command = command;
		this.music = music;
				
	}
	
	/**
	 * 
	 * @param command
	 * @param playOrPause true:play  false:pause
	 */
	public MusicControlEvent(int command,boolean playOrPause){
		this.command = command;
		this.playOrPause = playOrPause;
	}
	
	public MusicControlEvent(int command,int progress){
		this.command = command;
		this.progress = progress;
	}
}
