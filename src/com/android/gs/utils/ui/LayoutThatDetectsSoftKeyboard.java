package com.android.gs.utils.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.gs.listener.OnEventControlListener;



public class LayoutThatDetectsSoftKeyboard extends RelativeLayout {

	View activityRootView;
    public LayoutThatDetectsSoftKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // listener
	protected OnEventControlListener listener;
	
    public void setListener(OnEventControlListener listener) {
		this.listener = listener;
	}
    
    public void setRootView(View activityRootView){
    	this.activityRootView = activityRootView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	if(activityRootView!=null){
	    	Rect r = new Rect();
			// r will be populated with the coordinates of your view
			// that area still visible.
			activityRootView.getWindowVisibleDisplayFrame(r);
	
			int heightDiff = activityRootView.getRootView()
					.getHeight() - (r.bottom - r.top);
	        if (listener != null) {
	            listener.onSoftKeyboardShown(heightDiff>100,heightDiff); // assume all soft keyboards are at least 128 pixels high
	        }
    	}
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);       
    }

    }

