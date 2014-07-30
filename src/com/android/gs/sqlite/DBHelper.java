package com.android.gs.sqlite;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.android.gs.application.MyApplication;
import com.android.gs.utils.MyLog;



import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String configSQLFile = "sqliteconfig.cfg";
	private static String SCRIPT_CREATE_DB = "";
	private static int DATABASE_NEW_VERSION = 1;
	private static String SCRIPT_FILE_NAME = "";
	static final String DATABASE_NAME = "memoclip_db";

	private static DBHelper instance = null;

	public synchronized static DBHelper getInstance() throws Exception {
		if (instance == null) {
			getConfig();
			SCRIPT_CREATE_DB = readSCriptInFile(SCRIPT_FILE_NAME);
			if(MyApplication.getInstance().getActivityContext()!=null){
				instance = new DBHelper(MyApplication.getInstance().getActivityContext(),
						SCRIPT_CREATE_DB);
			}
		}

		return instance;
	}

	private DBHelper(Context context, String CREATE_TABLE) {
		super(context, DATABASE_NAME, null, DATABASE_NEW_VERSION);
		SCRIPT_CREATE_DB = CREATE_TABLE;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] tokens = SCRIPT_CREATE_DB.trim().split(";");
		for (String table : tokens) {
			db.execSQL(table);
		}
	}

	public boolean deleteDB() {
		return MyApplication.getInstance().getActivityContext()
				.deleteDatabase(DATABASE_NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MyLog.w("Content provider database", "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");

		onCreate(db);
	}

	private static String readSCriptInFile(String fileName) throws IOException,
			FileNotFoundException, Exception {
		InputStream instream = null;
		String str = "";
		StringBuffer buf = new StringBuffer();
		if( MyApplication.getInstance().getActivityContext()!=null){
		try {
			AssetManager assetManager = MyApplication.getInstance().getActivityContext().getAssets();
			instream = assetManager.open(fileName);

			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);

				while ((str = buffreader.readLine()) != null) {
					buf.append(str);
				}
			}

			return buf.toString();

		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (instream != null) {
				try {
					instream.close();
				} catch (Exception ex) {

				}
			}
		}
		}else{
			return str;
		}
	}

	private static void getConfig() throws Exception {
		InputStream instream = null;
		try {
				if(MyApplication.getInstance().getActivityContext()!=null){
				AssetManager assetManager = MyApplication.getInstance().getActivityContext().getAssets();
				instream = assetManager.open(configSQLFile);
	
				if (instream != null) {
					Properties properties = new Properties();
					properties.load(instream);
					instream.close();
	
					SCRIPT_FILE_NAME = properties.getProperty("scriptFileName");
					Integer.valueOf(properties
							.getProperty("oldVersion"));
					DATABASE_NEW_VERSION = Integer.valueOf(properties
							.getProperty("newVersion"));
	
				} else {
					throw new Exception("Can't read config DB file.");
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
	}
}
