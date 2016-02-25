package com.changhong.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
/**
 * show this dialog when want to delete music/vedio
 * @author yangtong
 *
 */
public class DeleteDialog extends Dialog {
	
	
	public interface OnDialogButtonListener{
		public void onDeleteButton();
		public void onCancelButton();
	}

	public DeleteDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	

}
