package com.android.gs.application;


import com.android.gs.constant.MyConstants;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyApplication extends Application{
	Context appContext;// application context
	Context activityContext;// activity context
	public boolean isDebugMode = false;
	private boolean isAppActive;
	public final String VERSION = "1.0";
	public final String PLATFORM_SDK_STRING = android.os.Build.VERSION.SDK;
	public final String PHONE_MODEL = "Android_" + android.os.Build.MODEL;
	static MyApplication _instance;

	static public synchronized MyApplication getInstance(){
		if(_instance == null){
			_instance = new MyApplication();
			
		}
		return _instance;
	}
	public void sentBroadcast(int action, Bundle bundle) {
		Intent intent = new Intent(MyConstants.BROADCAST_ACTION_PACKAGE);
		bundle.putInt(MyConstants.BROADCAST_ACTION, action);
		bundle.putInt(MyConstants.BROADCAST_HASHCODE,intent.getClass().hashCode());
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	public synchronized void setActivityContext(Context context) {
		this.appContext = context;
	}

	public Context getActivityContext() {
		if (this.appContext == null) {
			this.appContext = this.getApplicationContext();
		}
		return this.appContext;
	}
	/**
	 *
	 * set app co dang active hay ko
	 *
	 * @author : DoanDM since : 10:12:47 AM
	 */
	public void setAppActive(boolean isActive) {

		this.isAppActive = isActive;
	}

	/**
	 *
	 * get thong tin activte cua app
	 *
	 * @author : DoanDM since : 10:12:58 AM
	 */
	public boolean isAppActive() {

		return this.isAppActive;
	}
}
