package com.android.gs.sqlite;



import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public abstract class SQLiteTable {

	public String[] columns;
	public String sqlGetCountQuerry = "SELECT COUNT(*) FROM ";
	public String sqlDelete = "DELETE_FROM ";
	public String tableName = "";
	public static final String ID = "ID";
	
	public static final int NUMBER_OF_RECORD = 10;
	SQLiteDatabase db;

	abstract protected long getCount() throws Exception;

	abstract protected long insert(Object ent) throws Exception;

	public int update(ContentValues values, String whereClause,
			String[] whereArgs) {
		int upda = -1;
		try {
			db = DBHelper.getInstance().getWritableDatabase();
			upda = db.update(tableName, values, whereClause, whereArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}
		return upda;
	}

	public Cursor rawQuery(String sqlQuery, String[] params) {
		Cursor c = null;
		try {
			if(DBHelper.getInstance()!=null){
				db = DBHelper.getInstance().getWritableDatabase();
				c = DBHelper.getInstance().getWritableDatabase()
						.rawQuery(sqlQuery, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}

		return c;
	}

	public void execSQL(String sql) {
		try {
			db = DBHelper.getInstance().getWritableDatabase();
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}

	}

	public SQLiteStatement compileStatement(String sqlQuery) {
		SQLiteStatement rs = null;
		try {
			db = DBHelper.getInstance().getWritableDatabase();
			rs = db.compileStatement(sqlQuery);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}

		return rs;
	}

	public long insert(String nullColumnHack, ContentValues values) {
		long insert = -1;
		try {
			db = DBHelper.getInstance().getWritableDatabase();
			insert = db.insert(tableName, nullColumnHack, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}
		return insert;
	}

	public int delete(String whereClause, String[] whereArgs) {
		int delete = -1;
		try {
			db = DBHelper.getInstance().getWritableDatabase();
			delete = db.delete(tableName, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}

		return delete;
	}

	public Cursor query(String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy) {
		Cursor c = null;
		try {
			db = DBHelper.getInstance().getWritableDatabase();
			c = db.query(tableName, columns, selection, selectionArgs,
							groupBy, having, orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (db != null) {
//				db.close();
//			}
		}
		return c;

	}
}
