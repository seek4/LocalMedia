package com.changhong.localmedia.vedio;

public class VedioControlEvent {
	public static final int CMD_PLAY_PAUSE = 0;
	public static final int CMD_PLAY_VEDIO = 1;
	public static final int CMD_SEEK = 2;
	
	public int command = -1;
	public Vedio vedio = null;
	public long progress = 0;
	public boolean playOrPause = false;
	
	public VedioControlEvent(int command,Vedio vedio){
		this.command = command;
		this.vedio = vedio;
	}
	
	public VedioControlEvent(int command,long progress){
		this.command = command;
		this.progress = progress;
	}
	
	public VedioControlEvent(int command,boolean playOrPause){
		this.command = command;
		this.playOrPause = playOrPause;
	}
} 
