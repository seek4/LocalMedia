package com.changhong.localmedia.music;

import android.graphics.Bitmap;

public class Music {
	
	public int id;
	public String name;
	public String singer;
	public long duration;
	public String album;
	public String path;
	public Bitmap songPic;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}

	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Bitmap getSongPic() {
		return songPic;
	}
	public void setSongPic(Bitmap songPic) {
		this.songPic = songPic;
	}
	
}
