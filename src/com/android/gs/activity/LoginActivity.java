package com.android.gs.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.gs.constant.MyConstants;
import com.android.gs.controller.UserController;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.global.ErrorConstants;
import com.android.gs.global.ModelEvent;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.Setting;
import com.android.gs.utils.StringUtil;
import com.anroid.gs.R;

public class LoginActivity extends BaseActivity{
	Button btLogin;
	EditText edAccount;
	EditText edPassword;
	CheckBox cbox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		initLayout();
	}
	
	/**
	 * init layout
	 */
	void initLayout(){
		btLogin = (Button)findViewById(R.id.btLogin);
		btLogin.setOnClickListener(loginRequestListener);
		
		edAccount = (EditText)findViewById(R.id.edAccount);
		edPassword = (EditText) findViewById(R.id.edPassword);
		
		cbox = (CheckBox) findViewById(R.id.cbox);
		//check remember pwd
		if(!StringUtil.isNullOrEmpty(Setting.getInstance().getStringByKey(MyConstants.PWD_KEY, MyConstants.STR_BLANK))){
			cbox.setChecked(true);
			edAccount.setText(Setting.getInstance().getStringByKey(MyConstants.ACC_KEY, MyConstants.STR_BLANK));
			edPassword.setText(Setting.getInstance().getStringByKey(MyConstants.PWD_KEY, MyConstants.STR_BLANK));
		}
	}
	
	OnClickListener loginRequestListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(isInputValid()){
				loginRequest();
			}else{
				showDialog(StringUtil.getString(R.string.invalid_input_ac_pwd));
			}
		}
	};
	
	/*
	 * request login
	 */
	void loginRequest(){
		showProgressDialog(StringUtil.getString(R.string.processing), false);
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_REQUEST_LOGIN;
		Bundle bun = new Bundle();
		bun.putString(MyConstants.ACC_KEY, edAccount.getText().toString());
		bun.putString(MyConstants.PWD_KEY, edPassword.getText().toString());
		e.viewData = bun;
		UserController.getInstance().handleViewEvent(e);
	}
	
	@Override
	public void handleModelViewEvent(ModelEvent modelEvent) {
		// TODO Auto-generated method stub
		super.handleModelViewEvent(modelEvent);
		ActionEvent e = modelEvent.getActionEvent();
		switch(e.action){
		case ActionEventConstant.ACTION_REQUEST_LOGIN:
			goMainScreen();
			break;
		}
		
	}
	
	@Override
	public void handleErrorModelViewEvent(ModelEvent modelEvent) {
		// TODO Auto-generated method stub
		super.handleErrorModelViewEvent(modelEvent);
		switch(modelEvent.getCode()){
		case ErrorConstants.INVALID_USNAME_PWD:
			showDialog(StringUtil.getString(R.string.err_invalid_login));
			break;
		}
		
	}
	
	/**
	 * go to main screen (list of distributor)
	 */
	void goMainScreen(){
		checkSavePwd();
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_SWITH_TO_CATEGORY_SCREEN;
		UserController.getInstance().handleSwitchActivity(e);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		checkSavePwd();
		super.onBackPressed();
	}
	
	/**
	 * check to save account and password
	 */
	void checkSavePwd(){
		String acc = MyConstants.STR_BLANK;
		String pwd = MyConstants.STR_BLANK;
		if(cbox.isChecked()){
				acc = edAccount.getText().toString();
				pwd = edPassword.getText().toString();
		}
		Setting.getInstance().saveValue(MyConstants.ACC_KEY, acc, Setting.TYPE_STRING);
		Setting.getInstance().saveValue(MyConstants.PWD_KEY, pwd, Setting.TYPE_STRING);
	}
	
	
	/**
	 * check account and password valid
	 * @return
	 */
	boolean isInputValid(){
		String ac = edAccount.getText().toString();
		String pwd = edPassword.getText().toString();
		boolean isAccEmpty = StringUtil.isNullOrEmpty(ac);
		boolean isPwdEmpty = StringUtil.isNullOrEmpty(pwd);
		if(isAccEmpty){
			edAccount.requestFocus();
		}else if(isPwdEmpty){
			edPassword.requestFocus();
		}
		return !( isAccEmpty|| isPwdEmpty);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		GlobalUtil.saveObject(listCart, LIST_CART_KEY);
		super.onDestroy();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
		
	}
}
