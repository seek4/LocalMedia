package com.changhong.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlwaysMarqueeTextview extends TextView{

	    public AlwaysMarqueeTextview(Context context) {  
	        super(context);  
	    }  
	  
	    public AlwaysMarqueeTextview(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	    }  
	  
	    public AlwaysMarqueeTextview(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle);  
	    }  
	      
	    @Override
		protected void onFocusChanged(boolean arg0, int arg1, Rect arg2) {
			// TODO Auto-generated method stub
			//super.onFocusChanged(arg0, arg1, arg2);
		}

		@Override  
	    public boolean isFocused() {  
	        return true;  
	    }  
	}  


