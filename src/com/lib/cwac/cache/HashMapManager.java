package com.lib.cwac.cache;

/**
 * Copyright 2011 Synova Ltd. All rights reserved.
 * SYNOVA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import java.util.ArrayList;

import com.android.gs.utils.MyLog;




/**
 * Quan ly cac Soft Hash Map chua du lieu hinh anh duoc tao ra trong cac
 * activity
 * 
 * @author: PhucNT
 * @version: 1.1
 * @since: 1.0
 */
public class HashMapManager {

	private ArrayList<SoftHashMap> arrayHashMap = new ArrayList<SoftHashMap>();

	private static HashMapManager instance = null;

	public static HashMapManager getInstance() {
		if (instance == null) {
			instance = new HashMapManager();
		}
		return instance;
	}

	public static void setNullInstance() {
		if (instance != null) {
			instance.clearAllHashMap();
			instance = null;
		}
	}

	/**
	 * tra ve mot thuc the SoftHashMap duoc tao ra va add vao mang cac doi tuong
	 * hash map
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @param: id la activity Id xac dinh hashmap nay do activity do su dung
	 * @throws:
	 */
	public SoftHashMap creatSoftHashMap(String id, int maxSize,
			boolean isBigPhoto) {
		// Config custom message

		// for (int i = 0; i < arrayHashMap.size(); i++) {
		// SoftHashMap item = arrayHashMap.get(i);
		// if (item.idHashMap.equals(id))
		// return item;
		// }
		SoftHashMap shm = new SoftHashMap(id, maxSize, isBigPhoto);
		arrayHashMap.add(0, shm);
		MyLog.e("DoanDM", "creatSoftHashMap   " + arrayHashMap.size());
		return shm;
	}

	/**
	 * xoa nhung hash map cua mot activity nao do trong ham onDestroy
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @param: file
	 * @throws:
	 */
	public void clearHashMapById(String idActivity) {

		try {
			for (int i = 0; i < arrayHashMap.size(); i++) {
				SoftHashMap shm = arrayHashMap.get(i);
				if (shm.idHashMap.equals(idActivity)) {
					shm.clear();
					shm = null;
					arrayHashMap.remove(i);
					i--;
					MyLog.e("DoanDM", "clearHashMapById " + idActivity
							+ "size " + arrayHashMap.size());
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * xoa tat ca hash map
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @param:
	 * @throws:
	 */
	public void clearAllHashMapExcept(String idAct) {

		try {
			for (int i = 0; i < arrayHashMap.size(); i++) {
				SoftHashMap shm = arrayHashMap.get(i);
				if (!shm.idHashMap.equals(idAct)) {
					shm.clear();
					shm = null;
					arrayHashMap.remove(i);
					i--;
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * xoa tat ca hash map
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @param:
	 * @throws:
	 */
	public void clearAllHashMap() {

		try {
			for (int i = 0; i < arrayHashMap.size(); i++) {
				SoftHashMap shm = arrayHashMap.get(i);
				shm.clear();
				shm = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		arrayHashMap.clear();
	}


}
