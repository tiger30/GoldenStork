package com.android.gs.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.gs.constant.MyConstants;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.StringUtil;
import com.anroid.gs.R;


/**
 * Confirm dialog, with Yes/No choosen
 * @author DoanDM
 *
 */
public class ConfirmDialog extends Dialog implements
		android.view.View.OnClickListener {
	Object data;
	OnEventControlListener listener;
	public Button yes, no;
	TextView txt_dia;
	
	public ConfirmDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		listener = (OnEventControlListener) context;
		setContentView(R.layout.confirm_dialog);
		yes = (Button) findViewById(R.id.btn_yes);
		no = (Button) findViewById(R.id.btn_no);
		
		txt_dia = (TextView)findViewById(R.id.txt_dia);
		
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		setCanceledOnTouchOutside(true);
		
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    	getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}


	/**
	 * set string button
	 * @param btYes - button "YES"
	 * @param btNo - button "NO"
	 * @author DoanDM
	 * @since Mar 28, 2014
	 */
	public void setButtonString(String btYes,String btNo){
		yes.setText(btYes);
		no.setText(btNo);
	}

	
	/**
	 * set alert text
	 * @param alert
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void setText(String alert){
		txt_dia.setText(alert);
	}
	
	
	/**
	 * set data for transition
	 * @param obj
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void setData(Object obj){
		data = obj;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == yes){
			listener.onEvent(OnEventControlListener.CONFIRM_YES, v, data);
		}else{
			listener.onEvent(OnEventControlListener.CONFIRM_NO, v, data);
		}
		cancel();
	}

}
