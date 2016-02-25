package com.changhong.localmedia.vedio;

import java.util.List;

import com.changhong.localmedia.R;
import com.changhong.localmedia.Utils;
import com.changhong.localmedia.music.MusicControlEvent;
import com.changhong.localmedia.vedio.VedioController.VedioScanListener;

import de.greenrobot.event.EventBus;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class VedioFragment extends Fragment {
	
	protected static final String TAG = "VedioFragment";
	private View mView;
	private ListView listVedio;
	private EventBus eventBus;
	private List<Vedio> vedios;
	private VedioController vedioController;
	private VedioAdapter vedioAdapter;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mView==null||listVedio==null){
			mView = inflater.inflate(R.layout.fragment_vedio, container, false);
			listVedio = (ListView)mView.findViewById(R.id.list_vedio);
			listVedio.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(eventBus==null||vedios.get(position)==null)
						return;		
					eventBus.post(new MusicControlEvent(MusicControlEvent.CMD_PLAY_PAUSE, false));
					Intent intent = new Intent(getActivity(), VedioPlayActivity.class);
					intent.putExtra("position", position);
					getActivity().startActivity(intent);			
				}
			});
		}
		if(vedioController==null){
			vedioController = VedioController.getInstance(getActivity());
			vedioController.setScanListener(new VedioScanListener() {
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
				public void onScanFinished(List<Vedio> mvedios) {
					Log.i(TAG,"onScanFinished");
					if(scanDialog!=null)
						scanDialog.dismiss();
					vedios = mvedios;
					uiHandler.sendEmptyMessage(MSG_DATA_GOTTON);
				}

			});
			vedioController.startScanMusic();		
		}
		if(eventBus==null){
			eventBus = EventBus.getDefault();
			eventBus.register(this);
		}
		return mView;
	}
	
	public static final int MSG_DATA_GOTTON = 0x01;
	
	Handler uiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_DATA_GOTTON:
				if(vedioAdapter==null){
					vedioAdapter = new VedioAdapter(getActivity());				
					listVedio.setAdapter(vedioAdapter);
				}else {
					vedioAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
		
	};
	
	
	public void onEvent(VedioUpdateEvent event){
		
	}
	
	class VedioAdapter extends BaseAdapter{

		LayoutInflater mInflater;
		
		public VedioAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			
		}
		
		@Override
		public int getCount() {		
			return vedios==null?0:vedios.size();
		}

		@Override
		public Object getItem(int position) {
			return vedios.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			Vedio vedio = vedios.get(position);
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.vedio_item, null);
				holder.songNameView = (TextView)convertView.findViewById(R.id.text_vedioname);
				holder.timeView = (TextView)convertView.findViewById(R.id.text_duration);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.songNameView.setText(vedio.getName());
			if(vedio.getDuration()==0){
				holder.timeView.setText("--:--");
			}else {
				holder.timeView.setText(Utils.convertMill2HMS((int)vedio.getDuration()));
			}			
			return convertView;
		}
	    
		
		public final class ViewHolder{
			TextView songNameView;
			TextView singerView;
			TextView timeView;
			ImageView typeImage;
			ImageView singerImage;
			ImageView itemModeImage;
		}
		
	}
	
}
