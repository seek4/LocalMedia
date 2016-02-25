package com.changhong.localmedia.vedio;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.changhong.localmedia.Utils;
import com.changhong.localmedia.music.Music;
import com.xiami.sdk.XiamiSDK;

import de.greenrobot.event.EventBus;

public class VedioController {
	public static Context mContext;
	private XiamiSDK xiamiSDK;
	private EventBus eventBus;
	
	private VedioScanListener listener = null;
	public List<Vedio> vedios;
	
	private static VedioController instance;
	

	private static final String TAG = "VedioController";
	/**
	 * 0:顺序播放  1:随机播放   2:单曲循环
	 */
	private int playMode=0;
	public int playingId = -1;
	public int curPosition = -1;
	
	/**
	 * current status
	 */
	public boolean isPlay = false;
	public Vedio curVedio = null;
			
	private VedioController(Context context){
		mContext = context.getApplicationContext();
		xiamiSDK = new XiamiSDK(context, "", "");
		eventBus = EventBus.getDefault();
		eventBus.register(this);
	}
	
	public void onEvent(VedioUpdateEvent event){
		switch (event.type) {
		case VedioUpdateEvent.TYPE_VEDIO_CHANGE:
			curVedio = event.vedio;
			break;
		case VedioUpdateEvent.TYPE_PLAY_PAUSE:
			this.isPlay = event.isPlay;
			break;
		case VedioUpdateEvent.TYPE_DURATION_GOTTON:
			curVedio.setDuration(event.duration);
			break;
		default:
			break;
		}
	}
	
	public synchronized static VedioController getInstance(Context context){
		if(null == instance)
			instance = new VedioController(context);
		return instance;
	}
	
	
	public void startScanMusic(){
		ScanVedioTask scanVedioTask = new ScanVedioTask(mContext);
		scanVedioTask.execute();
	}
	
	public void playVedio(int position){
		if(vedios.get(position)!=null){
			eventBus.post(new VedioControlEvent(VedioControlEvent.CMD_PLAY_VEDIO, vedios.get(position)));
			curVedio = vedios.get(position);
			playingId = vedios.get(position).getId();
			curPosition = position;
		}
	}
	
	public void start(){
		eventBus.post(new VedioControlEvent(VedioControlEvent.CMD_PLAY_PAUSE,true));
	}
	
	public void pause(){
		eventBus.post(new VedioControlEvent(VedioControlEvent.CMD_PLAY_PAUSE,false));
	}
	
	public void playLast(){
		if(vedios==null||vedios.size()<=0){
			return;
		}
		
		int nextPosition = curPosition;
		switch (playMode) {
		case 0:
			nextPosition--;
			if(nextPosition<0)
				nextPosition = vedios.size()-1;
			break;
		case 1:
			nextPosition = Utils.getRamdomInt(vedios.size());
			while (vedios.get(nextPosition).getId()==playingId&&vedios.size()>1) {
				nextPosition = Utils.getRamdomInt(vedios.size());
			}
			break;
		case 2:
			break;
		default:
			break;
		}
		playVedio(nextPosition);
	}
	
	public void playNext(){
		if(vedios==null||vedios.size()<=0){
			return;
		}
		int nextPosition = curPosition;
		switch (playMode) {
		case 0:
			nextPosition++;
			if(nextPosition>=vedios.size())
				nextPosition = 0;
			break;
		case 1:
			nextPosition = Utils.getRamdomInt(vedios.size());
			while (vedios.get(nextPosition).getId()==playingId&&vedios.size()>1) {
				nextPosition = Utils.getRamdomInt(vedios.size());
			}
			break;
		case 2:
			break;
		default:
			break;
		}
		playVedio(nextPosition);
	}
	
	public void seekProgress(long progress){
		eventBus.post(new VedioControlEvent(VedioControlEvent.CMD_SEEK, progress));
	}
	
	
	public int getNextPosition() {
		// TODO Auto-generated method stub
		int nextPostion = curPosition;
		nextPostion++;
		if(nextPostion>=vedios.size()){
			nextPostion=0;
		}
		return nextPostion;
	}
	
	
	public void setScanListener(VedioScanListener listener){
		this.listener = listener;
	}
	
	public abstract interface VedioScanListener{
		public abstract void onScanFinished(List<Vedio> vedios);
		public abstract void onScanStarted();
	}
	

	/**
	 * 想要跟音乐一样用搜索文件然后解析出文件信息来实现，可是未找到解析的库，先用android自身提供的ContentProvider来实现
	 *
	 */
	class ScanVedioTask extends AsyncTask<File, Integer, List<File>>{

		private Context mContext;
		private List<Vedio> mVedios;		
		/**外部存储文件的根目录*/
		//private	static final String externalBoot = "/storage/external_storage/";//MX
		private	static final String usbBoot = "/mnt/usb_storage/";//RK		
		
		public ScanVedioTask(Context context){
			this.mContext = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(listener!=null)
				listener.onScanStarted();
		}

		@Override
		protected List<File> doInBackground(File... file) {
			Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
					null, null, null, null);
			if(cursor!=null&&cursor.getCount()>0){
				mVedios = new ArrayList<Vedio>();
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Vedio vedio = new Vedio();
					vedio.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
					vedio.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
					vedio.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
					Log.i(TAG,"duration >>"+cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
					mVedios.add(vedio);
					cursor.moveToNext();
				}
				cursor.close();
			}
			vedios = mVedios;
			if(listener!=null){
				listener.onScanFinished(mVedios);
			}
			return null;
		}

//		private void findMusic(File f){
//			File[] childs = f.listFiles(new FileFilter() {			
//				public boolean accept(File file) {
//					if(file.isDirectory()){
//						return true;
//					}else {
//						String name = file.getName();
//						if(name.endsWith(".avi")||name.endsWith(".asf")||
//								name.endsWith(".mkv")||name.endsWith(".mov")||
//								name.endsWith(".m4v")||name.endsWith(".mp4")||
//								name.endsWith(".aiff")||name.endsWith(".rmvb")||
//								name.endsWith(".mpg")||name.endsWith(".vob")||
//								name.endsWith(".wmv")){
//							mMusicFiles.add(file);
//							return true;
//						}
//					}
//					return false;
//				}
//
//			});
//			if(null==childs || 0==childs.length){
//				return;
//			}
//			for(int i=0;i<childs.length;i++){
//				if(childs[i].isDirectory()){
//					findMusic(childs[i]);
//				}
//			}
//		}
//		
		
		@Override
		protected void onPostExecute(List<File> result) {
//			int size = result.size();
//			for(int i=0;i<size;i++){
//				Bitmap songPic = xiamiSDK.readAPIC(result.get(i).getAbsolutePath(), new BitmapFactory.Options());				
//				Map<String, String> fileTags = xiamiSDK.readFileTags(result.get(i).getAbsolutePath());
//	            if (fileTags != null) {
//	            	Music song = new Music();
//	            	song.setSongPic(songPic);
//	                song.setName(fileTags.get("TITLE")==null?result.get(i).getName():fileTags.get("TITLE").trim());
//	                song.setAlbum(fileTags.get("ALBUM")==null?"<未知专辑>":fileTags.get("ALBUM").trim());
//	                song.setSinger(fileTags.get("SINGER")==null?"<未知艺术家>":fileTags.get("SINGER").trim());	//演唱艺人名
//	                song.setPath(result.get(i).getAbsolutePath());
//	                song.setId(i);
//	                mvedios.add(song);
//	            }
//			}
//			vedios = mvedios;
//			if(listener!=null)
//				listener.onScanFinished(mvedios);
			super.onPostExecute(result);
		}		
	}
}
