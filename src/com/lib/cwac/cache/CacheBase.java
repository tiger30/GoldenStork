/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package com.lib.cwac.cache;

import java.io.File;

import android.os.Debug;

import com.android.gs.application.MyApplication;
import com.android.gs.utils.MyLog;
import com.commonsware.cwac.task.AsyncTaskEx;

abstract public class CacheBase<K, V> {
	public static final int CACHE_MEMORY = 1;
	public static final int CACHE_DISK = 2;
	public static final int CACHE_NONE = 3;
	public static final int FORCE_NONE = 1;
	public static final int FORCE_SOFT = 2;
	public static final int FORCE_HARD = 3;
	private static String TAG = "CacheBase";
	protected File cacheRoot = null;
	private int maxSize = 0;
	public CacheCleanTask cacheCleanTask = new CacheCleanTask();
	// bang true hashmap danh cho big photo va nguoc lai
	protected boolean isBigPhoto = false;
	// bang true dang thuc hien recycle
	public boolean isRecycling;
	// cache dung cho bitmap voi con tro soft reference
	protected SoftHashMap cache = new SoftHashMap();
	
	public CacheBase(File cacheRoot, DiskCachePolicy policy, int maxSize) {
		String activityId = null;
		if (MyApplication.getInstance().getActivityContext()!=null)
			activityId = MyApplication.getInstance().getActivityContext().toString();
		MyLog.e("DoanDM", "activity Id "+ activityId);
		cache = HashMapManager.getInstance().creatSoftHashMap(activityId, maxSize, isBigPhoto);
	
		this.cacheRoot = cacheRoot;
		this.maxSize = maxSize;

	}
	/**
	 * doi voi nhung man hinh dung hinh anh nho thi khong goi ham nay mac dinh so luong maximum hinh anh la 50
	 * doi voi nhung man hinh dung nhung hinh anh lon thi goi ham nay de set lai gia tri so luong hinh anh toi 
	 * da luu trong hashmap
	 * @author: DoanDM
	 * @return: void
	 * @throws:
	 */
	public void setReleaseBitmap(boolean bigPhoto) {
		isBigPhoto = bigPhoto;
		if (isBigPhoto)
			cache.setLimitMaxSize(maxSize);
	}
	/**
	 * recycle all bitmaps trong hash map trong onDestroy moi man hinh recylce
	 * mot nua trong man hinh xem chi tiet
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @throws:
	 */
	public void recycleAllBitmaps(boolean isRecycleAll) {
		// TODO Auto-generated method stub
		
		if (isRecycleAll)
			cache.recycleAllBitmap();
		else 
			cache.recycleHalfAllBitmap();
		
		System.gc();
		System.runFinalization();

	}

	public V get(K key) {
		return (V) (cache.get(key));
	}

	public int getStatus(K key) {
		if (cache.containsKey(key)) {
			return (CACHE_MEMORY);
		}

		return (CACHE_NONE);
	}
	public int getHashmapSize() {
		
		return cache.size();
	}

	public SoftHashMap getSoftHashMap() {		
		return cache;
	}
	public void recyleOneBitmap(String key) {		
		cache.recycleOneBitmap(key);
	}
	protected void put(K key, V value) {
		MyLog.e("BigPhotoCache",
				"Allocate size  " + Debug.getNativeHeapAllocatedSize() / 1024
						+ "Free size  " + Debug.getNativeHeapFreeSize() / 1024
						+ "Heap size  " + Debug.getNativeHeapSize() / 1024
						+ "size hashmap" + cache.size());

		cache.put(key, value);
	}

	public void remove(K key) {
		cache.remove(key);
	}

	protected File getCacheRoot() {
		return (cacheRoot);
	}

	protected void setCacheRoot(File cache) {
		this.cacheRoot = cache;
	}

	public interface DiskCachePolicy {
		boolean eject(File cachedFile);
	}

	public class CacheCleanTask extends
			AsyncTaskEx<DiskCachePolicy, Void, Void> {
		@Override
		protected Void doInBackground(DiskCachePolicy... policies) {
			try {
				walkDir(cacheRoot, policies[0]);
			} catch (Throwable t) {
				MyLog.e(TAG, "Exception cleaning cache" + t);
			}

			return (null);
		}

		void walkDir(File dir, DiskCachePolicy policy) {
			if (dir.isDirectory()) {
				String[] children = dir.list();

				for (int i = 0; i < children.length; i++) {
					walkDir(new File(dir, children[i]), policy);
				}
			} else if (policy.eject(dir)) {
				dir.delete();
			}
		}
	}
}