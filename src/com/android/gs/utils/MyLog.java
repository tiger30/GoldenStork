/**
 * Copyright 2011 Synova Ltd. All rights reserved.
 * SYNOVA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.android.gs.utils;

import android.util.Log;

import com.android.gs.application.MyApplication;

/**
 *  Show log trace(interface)
 *  @author: DoanDM
 *  @version: 1.1
 *  @since: 1.0
 */
public class MyLog{

//	public static final com.google.code.microlog4android.Logger logger = LoggerFactory.getLogger();
	
	public static void d(String tag, String msg){
		if(MyApplication.getInstance().isDebugMode){
			Log.d(tag, msg);
		}
	}
	
	public static void d(String tag, String msg, Throwable tr){
		if(MyApplication.getInstance().isDebugMode){
			Log.d(tag, msg, tr);
		}
	}
	
	public static void e(String tag, String msg){
		if(MyApplication.getInstance().isDebugMode){
			Log.e(tag, msg);
		}
	}
	
	public static void e(String tag, String msg, Throwable tr) {
		if(MyApplication.getInstance().isDebugMode){
			Log.e(tag, msg, tr);
		}
	}
	
	public static void i(String tag, String msg){
		if(MyApplication.getInstance().isDebugMode){
			Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, String msg, Throwable tr){
		if(MyApplication.getInstance().isDebugMode){
			Log.i(tag, msg, tr);
		}
	}	

	public static synchronized void logToFile(String title, String content) {
		if(MyApplication.getInstance().isDebugMode){
//			logger.debug(title + " : " + content+"\r\n");
//			logger.debug("-------------------------------------");
		}
	}
	
	public static void v(String tag, String msg){
		if(MyApplication.getInstance().isDebugMode){
			Log.v(tag, msg);
		}
	}
	
	public static void v(String tag, String msg, Throwable tr){
		if(MyApplication.getInstance().isDebugMode){
			Log.v(tag, msg, tr);
		}
	}
	
	public static void w(String tag, String msg){
		if(MyApplication.getInstance().isDebugMode){
			Log.w(tag, msg);
		}
	}
	
	public static void w(String tag, String msg, Throwable tr){
		if(MyApplication.getInstance().isDebugMode){
			Log.w(tag, msg, tr);
		}
	}
}
