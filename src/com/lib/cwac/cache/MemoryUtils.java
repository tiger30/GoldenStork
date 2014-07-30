package com.lib.cwac.cache;

/**
 * Copyright 2011 Synova Ltd. All rights reserved.
 * SYNOVA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.io.File;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;

import com.android.gs.application.MyApplication;
import com.android.gs.utils.MyLog;


/**
 * Quan ly dung luong bo nho external and internal
 * 
 * @author: PhucNT
 * @version: 1.1
 * @since: Oct 26, 2011
 */
public class MemoryUtils {
	private static final int TYPE_CACHE_EXTERNAL = 0;
	private static final int TYPE_CACHE_INTERNAL = 1;
	private static final float ALLOWED_MEMORY_SIZE = 20;
	private static String KUNKUN = "KUNKUN";
	private static String IMAGE_CACHE = "IMAGE_CACHE";
	// private CacheCleanTask cacheCleanCache = new CacheCleanTask();
	public File cacheRootPath = null;
	public long rootCacheSize = 0;
	private static MemoryUtils instance = null;

	public static MemoryUtils getInstance() {
		if (instance == null) {
			instance = new MemoryUtils();
			instance.initialize();
		}
		return instance;
	}

	public static void setNullInstance() {
		instance = null;
	}

	private void initialize() {
		// Config custom message
		cacheRootPath = initCacheRoot();
		rootCacheSize = getFileSize(cacheRootPath);
	}

	/**
	 * init Cache root
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @param: file
	 * @throws:
	 */
	public File initCacheRoot() {
		File cacheRoot = null;
		float memAvailExternal = megabytesAvailable(new File(Environment
				.getExternalStorageDirectory().getPath()));
		MyLog.e("DoanDM", "dung luong external available "
				+ memAvailExternal);
		float memAvailInternal = megabytesAvailable(new File(Environment
				.getDataDirectory().getPath()));
		MyLog.e("DoanDM", "dung luong internal available "
				+ memAvailInternal);
		File rootExternal = getImageCachePath(TYPE_CACHE_EXTERNAL);
		File rootInternal = getImageCachePath(TYPE_CACHE_INTERNAL);
		if (isSdPresent() && (memAvailExternal > ALLOWED_MEMORY_SIZE)) {
			cacheRoot = rootExternal;
		} else if (memAvailInternal > ALLOWED_MEMORY_SIZE) {
			cacheRoot = rootInternal;
			if (rootExternal != null && rootExternal.exists())
				new CacheCleanTask().execute(rootExternal);
		} else {
			cacheRoot = null;
			if (rootExternal != null && rootExternal.exists())
				new CacheCleanTask().execute(rootExternal);
			if (rootInternal != null && rootInternal.exists())
				new CacheCleanTask().execute(rootInternal);
		}
		if (cacheRoot != null)
			MyLog.e("DoanDM", "thu muc cache root " + cacheRoot.getPath());

		return cacheRoot;
	}

	/**
	 * Check dung luong bo nho free
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @param: file
	 * @throws:
	 */
	public static float megabytesAvailable(File f) {
		StatFs stat = new StatFs(f.getPath());
		long bytesAvailable = (long) stat.getBlockSize()
				* (long) stat.getAvailableBlocks();
		return bytesAvailable / (1024.f * 1024.f);
	}

	public static boolean isSdPresent() {

		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

	}

	/**
	 * Tao file duong dan thu muc cach image
	 * 
	 * @author: DoanDM
	 * @param: isExternal lay thu muc duong dan trong the nho hay la lay trong
	 *         bo nho trong
	 * @return: void
	 * @throws:
	 */
	protected static File initKunKunImageCacheFolder(int typeCache) {
		String extStorageDirectory = null;
		File folderRoot = null;
		try {
			if (typeCache == TYPE_CACHE_EXTERNAL)
				extStorageDirectory = Environment.getExternalStorageDirectory()
						.toString();
			if (typeCache == TYPE_CACHE_INTERNAL)
				extStorageDirectory = MyApplication.getInstance().getActivityContext()
						.getCacheDir().toString();
			folderRoot = new File(extStorageDirectory, KUNKUN);
			if (!folderRoot.exists()) {
				if (folderRoot.mkdir()) {
				} else {
					folderRoot = null;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return folderRoot;
	}

	/**
	 * Tao file duong dan thu muc cach image cho nhung anh nho
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @throws:
	 */
	private File getImageCachePath(int typeCache) {
		File cache = initKunKunImageCacheFolder(typeCache);
		if (cache == null)
			return null;
		String server_revision = MyApplication.getInstance().getActivityContext().getPackageName();
		File fileCacheToDisk = new File(cache.getAbsolutePath(), IMAGE_CACHE
				+ "_" + server_revision);
		
		if (!fileCacheToDisk.exists()) {
			deleteDir(new File(cache.getAbsolutePath()));
			if (fileCacheToDisk.mkdir()) {
			} else {
				fileCacheToDisk = null;
			}
		}
		return fileCacheToDisk;
	}

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				if (children[i].contains(IMAGE_CACHE))	{	
					new CacheCleanTask().execute(new File(dir,children[i]));
					(new File(dir,children[i])).delete();
				}
				
			}
		}
	}

	/**
	 * Get size of folder root
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @throws:
	 */
	public long getFileSize(File folder) {

		if (folder == null)
			return 0;
		MyLog.e("DoanDM", "Folder: " + folder.getName());
		long foldersize = 0;

		File[] filelist = folder.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
				foldersize += getFileSize(filelist[i]);
			} else {

				foldersize += filelist[i].length();
			}
		}
		return foldersize;
	}

	/**
	 * 
	 * Xoa cache
	 * 
	 * @author: DoanDM
	 * @return
	 * @return: CacheCleanTask
	 * @throws:
	 */
	public CacheCleanTask getCacheCleanTask() {
		return new CacheCleanTask();
	}

	public class CacheCleanTask extends AsyncTask<File, Void, Void> {

		@Override
		protected Void doInBackground(File... files) {
			try {
				walkDir(files[0]);
			} catch (Throwable t) {
				MyLog.e("DoanDM", "Exception cleaning cache", t);
			}
			return null;
		}

		void walkDir(File dir) {
			if (dir.isDirectory()) {
				String[] children = dir.list();

				for (int i = 0; i < children.length; i++) {
					walkDir(new File(dir, children[i]));
				}
			} else {
				dir.delete();
			}
		}

	}

}
