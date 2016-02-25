package com.changhong.localmedia.music;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.changhong.localmedia.Utils;
import com.xiami.sdk.XiamiSDK;

import de.greenrobot.event.EventBus;

public class MusicController {
	
	public static Context mContext;
	private XiamiSDK xiamiSDK;
	private EventBus eventBus;
	
	private MusicScanListener listener = null;
	public List<Music> localMusics;
	
	private static MusicController instance;
	

	private static final String TAG = "MusicController";
	/**
	 * 0:顺序播放  1:随机播放   2:单曲循环
	 */
	private int playMode=0;
	public int playingId = -1;
	public static final int MODE_ALL = 0;
	public static final int MODE_RANDOM = 1;
	public static final int MODE_SINGLE = 2;
	
	/**
	 * current status
	 */
	public boolean isPlay = false;
	public Music curMusic = null;
			
	private MusicController(Context context){
		mContext = context.getApplicationContext();
		xiamiSDK = new XiamiSDK(context, "", "");
		eventBus = EventBus.getDefault();
		eventBus.register(this);
	}
	
	public void onEvent(MusicUpdateEvent event){
		switch (event.type) {
		case MusicUpdateEvent.TYPE_MUSIC_CHANGE:
			curMusic = event.music;
			break;
		case MusicUpdateEvent.TYPE_PLAY_PAUSE:
			this.isPlay = event.isPlay;
			break;
		case MusicUpdateEvent.TYPE_DURATION_GOTTON:
			curMusic.setDuration(event.progress);
			break;
		default:
			break;
		}
	}
	
	public synchronized static MusicController getInstance(Context context){
		if(null == instance)
			instance = new MusicController(context);
		return instance;
	}
	
	/**
	 * change the play mode
	 */
	public void changeMode() {
		playMode++;
		if(playMode>2){
			playMode = 0;
		}
		eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_MODE_CHANGED, playMode));
	}
	
	public int getPlayMode(){
		return playMode;
	}
	
	
	public void startScanMusic(){
		ScanMusicTask scanMusicTask = new ScanMusicTask();
		scanMusicTask.execute();
	}
	
	public void playMusic(int position){
		if(localMusics.get(position)!=null){
			Intent i = new Intent("com.android.music.musicservicecommand");
			i.putExtra("command", "pause");
			mContext.sendBroadcast(i);
			eventBus.post(new MusicControlEvent(MusicControlEvent.CMD_PLAY_SONG, localMusics.get(position)));
			curMusic = localMusics.get(position);
			playingId = localMusics.get(position).getId();
		}
	}
	
	public void start(){
		eventBus.post(new MusicControlEvent(MusicControlEvent.CMD_PLAY_PAUSE,true));
	}
	
	public void pause(){
		eventBus.post(new MusicControlEvent(MusicControlEvent.CMD_PLAY_PAUSE,false));
	}
	
	public void playLast(){
		if(localMusics==null||localMusics.size()<=0){
			return;
		}
		
		int nextPosition = playingId;
		switch (playMode) {
		case MODE_ALL:
			nextPosition--;
			if(nextPosition<0)
				nextPosition = localMusics.size()-1;
			break;
		case MODE_RANDOM:
			nextPosition = Utils.getRamdomInt(localMusics.size());
			while (localMusics.get(nextPosition).getId()==playingId&&localMusics.size()>1) {
				nextPosition = Utils.getRamdomInt(localMusics.size());
			}
			break;
		case MODE_SINGLE:
			break;
		default:
			nextPosition--;
			if(nextPosition<0)
				nextPosition = localMusics.size()-1;
			break;
		}
		playMusic(nextPosition);
	}
	
	public void playNext(){
		if(localMusics==null||localMusics.size()<=0){
			return;
		}
		int nextPosition = playingId;
		switch (playMode) {
		case MODE_ALL:
			nextPosition++;
			if(nextPosition>=localMusics.size())
				nextPosition = 0;
			break;
		case MODE_RANDOM:
			nextPosition = Utils.getRamdomInt(localMusics.size());
			while (localMusics.get(nextPosition).getId()==playingId&&localMusics.size()>1) {
				nextPosition = Utils.getRamdomInt(localMusics.size());
			}
			break;
		case MODE_SINGLE:
			break;
		default:
			nextPosition++;
			if(nextPosition>=localMusics.size())
				nextPosition = 0;
			break;
		}
		playMusic(nextPosition);
	}
	
	public void seekProgress(int progress){
		eventBus.post(new MusicControlEvent(MusicControlEvent.CMD_SEEK, progress));
	}
	
	public void deleteMusic(Music music) {
		if(music.getId()==playingId){
			pause();
		}
		File file = new File(music.getPath());
		if(file.exists()){
			if(file.delete()){
//				MyApp.playHelper.reloadList(listType);
//				songs = MyApp.playHelper.getSongList();
				localMusics.remove(music);
				eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_MUSIC_DELETED, true,music));
			}else {
				eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_MUSIC_DELETED, false,music));

			}
		}else {
			eventBus.post(new MusicUpdateEvent(MusicUpdateEvent.TYPE_MUSIC_DELETED, false,music));

		}
	}
	
	/**
	 * set callback when start scan music or finish scan music
	 */
	public void setScanListener(MusicScanListener listener){
		this.listener = listener;
	}
	
	public abstract interface MusicScanListener{
		public abstract void onScanFinished(List<Music> musics);
		public abstract void onScanStarted();
	}
	

	/**
	 * Scan local musics 
	 * @author yangtong
	 *
	 */
	class ScanMusicTask extends AsyncTask<File, Integer, List<Music>>{

		List<File> mMusicFiles;	
		List<Music> mLocalMusics;
		/**外部存储文件的根目录*/
		//private	static final String externalBoot = "/storage/external_storage/";//MX
		private	static final String usbBoot = "/mnt/usb_storage/";//RK		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(TAG,"external >>"+Environment.getExternalStorageDirectory().getAbsoluteFile());
			if(listener!=null)
				listener.onScanStarted();
		}

		@Override
		protected List<Music> doInBackground(File... file) {
			mMusicFiles = new ArrayList<File>();
			findMusic(new File(usbBoot));
			findMusic(Environment.getExternalStorageDirectory());
			mLocalMusics = new ArrayList<Music>();
			int size = mMusicFiles.size();
			musicCount = size+1;
			for(int i=0;i<size;i++){
				Bitmap songPic = xiamiSDK.readAPIC(mMusicFiles.get(i).getAbsolutePath(), new BitmapFactory.Options());				
				Map<String, String> fileTags = xiamiSDK.readFileTags(mMusicFiles.get(i).getAbsolutePath());
	            if (fileTags != null) {
	            	Music song = new Music();
	            	song.setSongPic(songPic);
	                song.setName(fileTags.get("TITLE")==null?mMusicFiles.get(i).getName():fileTags.get("TITLE").trim());
	                song.setAlbum(fileTags.get("ALBUM")==null?"<未知专辑>":fileTags.get("ALBUM").trim());
	                song.setSinger(fileTags.get("SINGER")==null?"<未知艺术家>":fileTags.get("SINGER").trim());	//演唱艺人名
	                song.setPath(mMusicFiles.get(i).getAbsolutePath());
	                song.setId(i);
	                mLocalMusics.add(song);
	            }
			}
//			if(mLocalMusics!=null&&mLocalMusics.size()>=0){
//				grepDuration(mLocalMusics.get(0));
//			}
			
			localMusics = mLocalMusics;
			return mLocalMusics;
		}

		private void findMusic(File f){
			File[] childs = f.listFiles(new FileFilter() {			
				public boolean accept(File file) {
					if(file.isDirectory()){
						return true;
					}else {
						String name = file.getName();
						if(name.endsWith(".mp3")||name.endsWith(".wma")||
								name.endsWith(".ape")||name.endsWith(".flac")||
								name.endsWith(".m4a")||name.endsWith(".ac3")||
								name.endsWith(".aiff")||name.endsWith(".mid")||
								name.endsWith(".mka")||name.endsWith(".wav")||
								name.endsWith(".ogg")){
							mMusicFiles.add(file);
							return true;
						}
					}
					return false;
				}

			});
			if(null==childs || 0==childs.length){
				return;
			}
			for(int i=0;i<childs.length;i++){
				if(childs[i].isDirectory()){
					findMusic(childs[i]);
				}
			}
		}
		
		
		@Override
		protected void onPostExecute(List<Music> result) {
			if(listener!=null)
				listener.onScanFinished(mLocalMusics);
			super.onPostExecute(result);
		}		
		
		/**
		 * 未使用，本来是想要获取时长的，结果发现耗时太久
		 */
		private int gotton = 0;
		private void grepDuration(final Music music){
				MediaPlayer mediaPlayer = new MediaPlayer();
				try {
					mediaPlayer.setDataSource(music.path);
					mediaPlayer.setOnPreparedListener(new OnPreparedListener() {					
						@Override
						public void onPrepared(MediaPlayer mp) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(MSG_DURATION_GOTTON);
							music.setDuration(mp.getDuration());
							gotton++;
							grepDuration(mLocalMusics.get(gotton));
							Log.i(TAG,"mp.getDuration()"+mp.getDuration());
						}
					});
					mediaPlayer.prepareAsync();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			}
		}
		
		private static final int MSG_DURATION_GOTTON = 0x01;
		int musicCount = 0;
		int tmpCount = 0;
		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case MSG_DURATION_GOTTON:
					tmpCount++;
					if(tmpCount>=musicCount){
						localMusics = mLocalMusics;
						Log.i(TAG,"duration >>"+localMusics.get(0).duration);
						if(listener!=null)
							listener.onScanFinished(mLocalMusics);
					}
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
			
		};
		
	}






}
