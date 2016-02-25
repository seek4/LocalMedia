package com.changhong.localmedia.music;

import java.util.List;
import java.util.zip.Inflater;

import com.changhong.localmedia.R;
import com.changhong.localmedia.Utils;
import com.changhong.localmedia.music.MusicController.MusicScanListener;

import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicFragment extends Fragment {
	
	protected static final String TAG = "MusicFragment";
	private View mView;
	private ListView listMusic;
	MuiscAdapter musicAdapter = null;
	private MusicController musicController;
	private List<Music> musics;
	private Context mContext;
	

	private EventBus eventBus;
	
	boolean isLongClick = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		if(mView==null||listMusic==null){
			mView = inflater.inflate(R.layout.fragment_music, container, false);
			listMusic = (ListView)mView.findViewById(R.id.list_music);
			listMusic.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(isLongClick){
						isLongClick=false;
						return;
					}
					if(eventBus==null||musics.get(position)==null)
						return;			
					if(musicController.playingId!=musics.get(position).id){
						musicController.playMusic(position);
					}else {
						if(!musicController.isPlay){
							musicController.start();
						}
					}
					Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
					getActivity().startActivity(intent);			
				}
			});
			listMusic.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO delete the item selected
					isLongClick = true;
					if(musics.get(position)!=null){
						showDeleteDialog(musics.get(position));
					}
					return false;
				}
			});
		}
		if(musicController==null){
			musicController = MusicController.getInstance(getActivity());
			musicController.setScanListener(new MusicScanListener() {
				ProgressDialog scanDialog;
				@Override
				public void onScanStarted() {
					Log.i(TAG,"onScanStarted");
					scanDialog = new ProgressDialog(getActivity());
					scanDialog.setTitle("加载歌曲中");
					scanDialog.setMessage("正在扫描歌曲，请稍后...");
					scanDialog.setCancelable(false);
					scanDialog.show();
				}
				
				@Override
				public void onScanFinished(List<Music> mMusics) {
					Log.i(TAG,"onScanFinished");
					if(scanDialog!=null)
						scanDialog.dismiss();
					musics = mMusics;
					if(musicAdapter==null){
						musicAdapter = new MuiscAdapter(getActivity());				
						listMusic.setAdapter(musicAdapter);
					}else {
						musicAdapter.notifyDataSetChanged();
					}
				}
			});
			musicController.startScanMusic();		
		}
		if(eventBus==null){
			eventBus = EventBus.getDefault();
			eventBus.register(this);
		}
		return mView;
	}

	
	
	
	@Override
	public void onResume() {
		Log.i(TAG,"onResume");
		if(musicAdapter!=null){
			musicAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}




	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * 接收并处理播放信息变更
	 */
	public void onEventMainThread(MusicUpdateEvent event){
		if(event==null){
			return;
		}
		switch (event.type) {
		case MusicUpdateEvent.TYPE_PLAY_PAUSE:
			musicAdapter.notifyDataSetChanged();
			break;
		case MusicUpdateEvent.TYPE_MUSIC_CHANGE:
			musicAdapter.notifyDataSetChanged();
			break;
		case MusicUpdateEvent.TYPE_PROGRESS:
			
			break;
		case MusicUpdateEvent.TYPE_MUSIC_DELETED:
			if(event.isSuccess){
				Toast.makeText(mContext, event.music.name+"已被删除", Toast.LENGTH_SHORT).show();
				musics.remove(event.music);
				musicAdapter.notifyDataSetChanged();
			}else {
				Toast.makeText(mContext, event.music.name+"删除失败", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	private void showDeleteDialog(final Music music){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getString(R.string.delete_dialog_title))
		.setMessage(getString(R.string.delete_dialog_intro)+music.name)
		.setIcon(R.drawable.playlist_item_delete)
		.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO delete the music
				musicController.deleteMusic(music);
			}
		});
		builder.create().show();
	}
	
	class MuiscAdapter extends BaseAdapter{

		LayoutInflater mInflater;
		
		public MuiscAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			
		}
		
		@Override
		public int getCount() {		
			return musics==null?0:musics.size();
		}

		@Override
		public Object getItem(int position) {
			return musics.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			Music song = musics.get(position);
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.music_item, null);
				holder.songNameView = (TextView)convertView.findViewById(R.id.text_songname);
				holder.singerView = (TextView)convertView.findViewById(R.id.text_item_singer);
				holder.itemModeImage = (ImageView)convertView.findViewById(R.id.image_item_mode);
				holder.playingImage  = (ImageView)convertView.findViewById(R.id.image_playing);
				holder.timeView = (TextView)convertView.findViewById(R.id.text_item_time);
				holder.singerImage = (ImageView)convertView.findViewById(R.id.image_item_singer);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.songNameView.setText(song.getName());
			holder.singerView.setText(song.getSinger());
			if(song.songPic!=null){
				holder.singerImage.setImageBitmap(song.songPic);
			}else {
				holder.singerImage.setImageResource(R.drawable.music_default);
			}
			if(song.getId()==musicController.playingId){
				holder.playingImage.setVisibility(View.VISIBLE);
				holder.playingImage.setImageResource(musicController.isPlay?R.drawable.playlist_playing:R.drawable.playing_5);
			}else {
				holder.playingImage.setVisibility(View.INVISIBLE);
			}
//			if(song.getDuration()==0){
//				holder.timeView.setText("--:--");
//			}else {
//				holder.timeView.setText(Utils.convertMill2HMS((int)song.getDuration()));
//			}			
			return convertView;
		}
		
		public final class ViewHolder{
			TextView songNameView;
			TextView singerView;
			TextView timeView;
			ImageView typeImage;
			ImageView singerImage;
			ImageView itemModeImage;
			ImageView categoryImage;
			ImageView playingImage; 
		}
		
	}
	
}
