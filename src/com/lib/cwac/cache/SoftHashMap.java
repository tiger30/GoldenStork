/**
 * Copyright 2011 Synova Ltd. All rights reserved.
 * SYNOVA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.lib.cwac.cache;

/**
 *  Lop hash map quan ly cac doi tuong soft reference bitmap
 *  @author: PhucNT
 *  @version: 1.1
 *  @since: Nov 23, 2011
 */
//: SoftHashMap.java
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import android.graphics.drawable.BitmapDrawable;

import com.android.gs.utils.MyLog;


public class SoftHashMap extends AbstractMap {
	/** The internal HashMap that will hold the SoftReference. */
	// private final Map hash = new HashMap();
	private final Map hash = Collections.synchronizedMap(new LinkedHashMap(101,
			.75F, true) {
		/**
 * 
 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return (size() > limitSizePhoto);
		}
	});
	/** so luong max size cho nhung hinh anh nho. */
	private static final int MAX_SIZE_FOR_SMALL_PHOTO = 50;

	/** Reference queue for cleared SoftReference objects. */
	private final ReferenceQueue queue = new ReferenceQueue();
	// so luong hinh anh toi da duoc luu trong hashmap
	private int limitSizePhoto = 0;
	public String idHashMap = null;

	public SoftHashMap() {
		this(100);
	}

	public SoftHashMap(int hardSize) {
		// HARD_SIZE = hardSize;
	}

	/**
	 * @param maxSize
	 * @param isSmallPhoto
	 */
	public SoftHashMap(String id, int maxSize, boolean isBigPhoto) {
		// TODO Auto-generated constructor stub
		limitSizePhoto = MAX_SIZE_FOR_SMALL_PHOTO;
		idHashMap = id;

	}

	public void setLimitMaxSize(int maxSize) {
		// TODO Auto-generated constructor stub

		limitSizePhoto = maxSize;

	}

	public Object get(Object key) {
		Object result = null;
		// We get the SoftReference represented by that key
		SoftReference soft_ref = (SoftReference) hash.get(key);
		if (soft_ref != null) {
			// From the SoftReference we get the value, which can be
			// null if it was not in the map, or it was removed in
			// the processQueue() method defined below
			result = soft_ref.get();
			if (result == null) {
				// If the value has been garbage collected, remove the
				// entry from the HashMap.
				hash.remove(key);
			} else {
				// We now add this object to the beginning of the hard
				// reference queue. One reference can occur more than
				// once, because lookups of the FIFO queue are slow, so
				// we don't want to search through it each time to remove
				// duplicates.
				// hardCache.addFirst(result);
				// if (hardCache.size() > HARD_SIZE) {
				// // Remove the last entry if list longer than HARD_SIZE
				// hardCache.removeLast();
				// }
			}
		}
		return result;
	}

	/**
	 * We define our own subclass of SoftReference which contains not only the
	 * value but also the key to make it easier to find the entry in the HashMap
	 * after it's been garbage collected.
	 */
	private static class SoftValue extends SoftReference {
		private final Object key; // always make data member final
		public Object value;

		/**
		 * Did you know that an outer class can access private data members and
		 * methods of an inner class? I didn't know that! I thought it was only
		 * the inner class who could access the outer class's private
		 * information. An outer class can also access private members of an
		 * inner class inside its inner class.
		 */
		private SoftValue(Object k, Object key, ReferenceQueue q) {
			super(k, q);
			value = k;
			this.key = key;
		}

	}

	/**
	 * Here we put the key, value pair into the HashMap using a SoftValue
	 * object.
	 */
	public Object put(Object key, Object value) {
		// if (hash.size() > limitSizePhoto)
		// hash.clear();
		return hash.put(key, new SoftValue(value, key, queue));
	}

	public Object remove(Object key) {
		return hash.remove(key);
	}

	public void clear() {
		hash.clear();

	}

	/**
	 * recycle bitmap trong hashmap voi param la mot key
	 * 
	 * @author: PhucNT
	 * @return: void
	 * @throws:
	 */
	public void recycleOneBitmap(Object bitmap) {
		// TODO Auto-generated method stub
		if (bitmap == null)
			return;
		try {
			// SoftValue sv = (SoftValue) hash.get(key);
			BitmapDrawable bm = (BitmapDrawable) bitmap;
			if (null != bm && !bm.getBitmap().isRecycled()) {
				bm.setCallback(null);
				bm.getBitmap().recycle();
				MyLog.e("BigPhotoCache", "bitmap is already recycled"
						+ hash.size());
			} else {
				MyLog.e("BigPhotoCache", "bitmap is not already recycled");
			}
			bm = null;
		} catch (Exception e) {
			MyLog.e("BigPhotoCache", "bm null " + e);
		}
	}

	/**
	 * recycle all bitmap trong hash map
	 * 
	 * @author: PhucNT
	 * @return: void
	 * @throws:
	 */
	public void recycleAllBitmap() {
		// TODO Auto-generated method stub
		try {
			Collection set = hash.values();
			synchronized (set) {
				Iterator iter = set.iterator();
				while (iter.hasNext()) {
					SoftValue sv = (SoftValue) iter.next();
					recycleOneBitmap(sv.value);
					iter.remove();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MyLog.e("BigPhotoCache", "exception " + e.toString());
		}
	}

	/**
	 * recycle mot nua bitmap trong hash map
	 * 
	 * @author: PhucNT
	 * @return: void
	 * @throws:
	 */
	public void recycleHalfAllBitmap() {
		// TODO Auto-generated method stub
		try {
			Collection set = hash.values();
			synchronized (set) {
				int size = size();
				Iterator iter = set.iterator();
				int i = 0;
				while (iter.hasNext() && i < size / 2) {
					SoftValue sv = (SoftValue) iter.next();
					recycleOneBitmap(sv.value);
					iter.remove();
					i++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int size() {
		return hash.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractMap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return hash.containsKey(key);
	}

	public Set entrySet() {
		// no, no, you may NOT do that!!! GRRR
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		// TODO Auto-generated method stub
		SoftHashMap obj = (SoftHashMap) object;
		if (this.idHashMap == obj.idHashMap)
			return true;
		return false;
	}
}