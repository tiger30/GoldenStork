package com.android.gs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class Setting {
	public static final int TYPE_BOOLEAN = 0;
	public static final int TYPE_INT = 1;
	public static final int TYPE_STRING = 2;
	
	private SharedPreferences sharedPreferences;
	
	static Setting instance;
	public static Setting getInstance(){
		if(instance == null){
			instance = new Setting();
		}
		return instance;
	}
	boolean isLoaded = false;
	
	/**
	 * load setting sharedPreference
	 * @param context
	 * @author DoanDM
	 * @since Dec 25, 2013
	 */
	public void loadSetting(Context context){
		if(!isLoaded){
			//load setting
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			//load sth here
			
			isLoaded = true;
		}
	}
	/**
	 * get String by key
	 * @param key
	 * @param defaultVal
	 * @return
	 * @author DoanDM
	 * @since Dec 25, 2013
	 */
	public String getStringByKey(String key,String defaultVal){
		return sharedPreferences.getString(key, defaultVal);
	}
	
	/**
	 * get Integer by key
	 * @param key
	 * @param defaultVal
	 * @return
	 * @author DoanDM
	 * @since Dec 25, 2013
	 */
	public int getIntByKey(String key,int defaultVal){
		return sharedPreferences.getInt(key, defaultVal);
	}
	
	/**
	 * get Boolean by key
	 * @param key
	 * @param defaultValue
	 * @return
	 * @author DoanDM
	 * @since Dec 25, 2013
	 */
	public boolean getBooleanByKey(String key,boolean defaultValue){
		return sharedPreferences.getBoolean(key, defaultValue);
	}
	
	/**
	 * save value into SharedPreference
	 * @param key
	 * @param value
	 * @param type
	 * @author DoanDM
	 * @since Dec 25, 2013
	 */
	public synchronized void saveValue(String key,Object value,int type){
		synchronized(sharedPreferences){
			Editor editor = sharedPreferences.edit();
			switch(type){
			case TYPE_BOOLEAN:
				editor.putBoolean(key, (Boolean) value);
				break;
			case TYPE_INT:
				editor.putInt(key, (Integer) value);
				break;
			case TYPE_STRING:
				editor.putString(key, (String) value);
				break;
			}
			
			editor.commit();
		}
	}
	
	public synchronized void saveSetting(){
		synchronized(sharedPreferences){
			Editor editor = sharedPreferences.edit();
			//save setting here
			//....
			//commit all
			editor.commit();
		}
	}
	
}
