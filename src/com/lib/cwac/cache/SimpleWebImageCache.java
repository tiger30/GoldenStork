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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.android.gs.utils.MyLog;
import com.commonsware.cwac.bus.AbstractBus;
import com.commonsware.cwac.task.AsyncTaskEx;
import com.lib.cwac.thumbnail.ThumbnailMessage;


public class SimpleWebImageCache<B extends AbstractBus, M> extends
		CacheBase<String, Drawable> {

	private static final long MAX_SIZE_IMAGE_CACHE = 10 * 1024 * 1024;// 10
																		// megabyte
	protected static final long DELAY_TIME_TO_RELOAD_IMAGE = 2000;// thoi gian
																	// de reload
																	// hinh anh

	// thoi gian moi lan retry 3s
	public static final long TIME_DELAY_RETRY_LOAD_IMAGE = 3000;
	// so lan toi da retry
	public static final int MAX_NUM_TIMES_RETRY = 6;
	// megabyte
	// private static final String TAG="SimpleWebImageCache";
	protected B bus = null;
	// tham so policy de xoa cache
	private DiskCachePolicy mPolicy;
	// kich thuoc byte cua folder cache
	private long rootCacheSize;
	// bien nay de biet co su dung cache hay khong
	private boolean isUseCache;
	// so lan toi da de retry mot hinh anh
	protected int maxNumRetry = 0;
	// bien can de retry
	private boolean isRetry;

	static public File buildCachedImagePath(File cacheRoot, String url)
			throws Exception {
		return (new File(cacheRoot, md5(url)));
	}

	/**
	 * lay name image tu link url
	 * 
	 * @author: PhucNT
	 * @return: void
	 * @throws:
	 */
	static public String getNameImage(String s) throws Exception {
		if (s != null) {
			String[] token = s.split("/");
			if (token.length > 0)
				return token[token.length - 1];
		}
		return "";
	}

	static public String md5(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");

		md.update(s.getBytes());

		byte digest[] = md.digest();
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < digest.length; i++) {
			result.append(Integer.toHexString(0xFF & digest[i]));
		}

		return (result.toString());
	}

	public SimpleWebImageCache(File cacheRoot,
			AsyncCache.DiskCachePolicy policy, int maxSize, B bus) {
		super(cacheRoot, policy, maxSize);
		isUseCache = true;
		initCacheRoot(cacheRoot, policy);
		this.bus = bus;
	}

	/**
	 * set co su dung cache hinh anh hay khong
	 * 
	 * @author: PhucNT
	 * @return: void
	 * @throws:
	 */
	public void setUserCacheRoot(boolean isUse) {
		isUseCache = isUse;
		if (!isUse) {
			cacheRoot = null;
			setCacheRoot(cacheRoot);
		}
	}

	/**
	 * set retry de lay hinh anh trong mot time delay maxNumRetry so lan toi da
	 * de lay hinh anh
	 * 
	 * @author: PhucNT
	 * @return: void
	 * @throws:
	 */

	public void setRetryLoadImage(boolean isRetry) {
		if (isRetry)
			maxNumRetry = MAX_NUM_TIMES_RETRY;
	}

	/**
	 * Tao duong dan thu muc chua anh cache va xoa thu muc neu so luong file
	 * vuot qua so luong cho phep
	 * 
	 * @author: KunKun
	 * @return: void
	 * @throws:
	 */
	private void initCacheRoot(File cacheRoot, AsyncCache.DiskCachePolicy policy) {
		// TODO Auto-generated method stub
		// khoi tao quyen de xoa file
		mPolicy = new DiskCachePolicy() {
			@Override
			public boolean eject(File cachedFile) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		cacheRoot = MemoryUtils.getInstance().cacheRootPath;
		setCacheRoot(cacheRoot);
		rootCacheSize = MemoryUtils.getInstance().rootCacheSize;
	}

	@Override
	public int getStatus(String key) {
		int result = super.getStatus(key);

		if (result == CACHE_NONE && getCacheRoot() != null) {
			try {
				File cache = buildCachedImagePath(key);
				if (cache.exists()) {
					result = CACHE_DISK;
				}
			} catch (Throwable t) {
				// MyLog.e(TAG, "Exception getting cache status", t);
			}
		}

		return (result);
	}

	public File buildCachedImagePath(String url) throws Exception {
		if (getCacheRoot() == null) {
			return (null);
		}

		return (buildCachedImagePath(getCacheRoot(), url));
	}

	public void notify(String key, ThumbnailMessage message) throws Exception {
		int status = getStatus(key);
		if (status == CACHE_NONE) {
			MyLog.e("KunKun", "-----------CACHE_NONECACHE_NONECACHE_NONE");
			new FetchImageTask().execute(message, key,
					buildCachedImagePath(key));
		} else if (status == CACHE_DISK) {
			MyLog.e("KunKun",
					"-----------CACHE_DISKCACHE_DISKCACHE_DISKCACHE_DISK");
			new LoadImageTask()
					.execute(message, key, buildCachedImagePath(key));
		} else {
			MyLog.e("KunKun", "-----------MEMORYMEMORYMEMORYMEMORY");
			bus.send(message);
		}
	}

	public B getBus() {
		return (bus);
	}

	class FetchImageTask extends AsyncTaskEx<Object, Object, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			String url = params[1].toString();
			File cache = (File) params[2];

			URLConnection connection = null;
			InputStream stream = null;

			int countRetry = 0;
			ThumbnailMessage message = (ThumbnailMessage) params[0];
			do {
				countRetry++;
				isRetry = false;
				try {
					MyLog.i("ImageCache", "---------> countRetry: "
							+ countRetry + " - url :" + url);
					connection = new URL(url).openConnection();

					String contentEncoding = connection.getContentEncoding();
					if (contentEncoding != null
							&& contentEncoding.equals("gzip")) {
						stream = new GZIPInputStream(
								connection.getInputStream());
					} else {
						stream = connection.getInputStream();
					}
					contentEncoding = null;

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					int read;
					byte[] b = new byte[4096];

					while ((read = stream.read(b)) != -1) {
						out.write(b, 0, read);
					}
					out.flush();
					out.close();

					byte[] raw = out.toByteArray();

					BitmapFactory.Options resample = new BitmapFactory.Options();
					resample.inJustDecodeBounds = false;
					resample.inSampleSize = computeSampleSize(raw,
							message.maxDimension, message.maxDimension);
					Bitmap bitmap = BitmapFactory.decodeByteArray(raw, 0,
							raw.length, resample);

					// if (!isIconCategory(getNameImage(url))) {
					// ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					// raw = baos.toByteArray();
					// }
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					boolean success=bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					if (success)
						raw = baos.toByteArray();
					else
						MyLog.i("ImageCache", " compress bitmap fail ");
					
					put(url, new BitmapDrawable(bitmap));

					message = (ThumbnailMessage) params[0];

					if (message != null) {
						bus.send(message);

					}

					if (cache != null && success) {
						FileOutputStream file = new FileOutputStream(cache);
						// bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						// file);
						file.write(raw);
						MyLog.i("ImageCache", " write on disk successful");
						file.flush();
						file.close();
						checkCleanCache(cache);
					}
					bitmap = null;

				} catch (OutOfMemoryError out) {
					MyLog.i("MEMORY", " SIMPLE WEB IMAGE CACHE");
					SimpleWebImageCache.this.cache.clear();
					System.gc();
					System.runFinalization();
					System.gc();
				} catch (FileNotFoundException e) {
					isRetry = true;
					// neu cache khong ton tai thi khong retry
					try {
						FileOutputStream file = new FileOutputStream(cache);
					} catch (FileNotFoundException e2) {
						// TODO Auto-generated catch block
						isRetry = false;
					}

					try {
						Thread.sleep(TIME_DELAY_RETRY_LOAD_IMAGE);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					MyLog.i("ImageCache",
							" FileNotFoundException isRetry = " + e.toString());
				} catch (IOException e) {

					MyLog.i("ImageCache",
							" FileNotFoundException isRetry = " + isRetry);

				} catch (Exception e) {
					MyLog
							.i("ImageCache", " Exception isRetry = " + isRetry);

				} catch (Throwable t) {

					message = (ThumbnailMessage) params[0];

					if (message != null) {
						bus.send(message);
					}
					MyLog
							.i("ImageCache", " Throwable isRetry = " + isRetry);
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} while (isRetry && countRetry <= maxNumRetry);
			return (null);
		}

		/**
		 * Kiem tra co phai la icon categories hong
		 * 
		 * @author: PhucNT
		 * @param cache
		 * @return
		 * @return: boolean
		 * @throws:
		 */
		private boolean isIconCategory(String url) {
			// TODO Auto-generated method stub
			if (url != null && url.contains("category"))
				return true;
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.commonsware.cwac.task.AsyncTaskEx#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			// super.onProgressUpdate(values);
			ThumbnailMessage message = (ThumbnailMessage) values[0];

			if (message != null) {
				ImageView image = (ImageView) message.getImageView();
				Bitmap bm = (Bitmap) values[1];
				if (bm != null)
					image.setImageBitmap(bm);
			}
		}
	}

	public class LoadImageTask extends AsyncTaskEx<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			String url = params[1].toString();
			File cache = (File) params[2];
			ThumbnailMessage message = (ThumbnailMessage) params[0];
			try {
//				BitmapFactory.Options resample = new BitmapFactory.Options();
//				resample.inJustDecodeBounds = false;
//				resample.inSampleSize = computeSampleSizeFromFile(cache,
//						message.maxDimension, message.maxDimension);
				Bitmap bmp = BitmapFactory.decodeFile(cache.getAbsolutePath());
				if (bmp != null) {
					MyLog.i("ImageCache", " LoadImageTask  " +url);
					put(url, new BitmapDrawable(bmp));

					if (params[0] != null) {
						bus.send(params[0]); 
					}
				}else {
					// neu hinh khong co trong sdcard load lai tu tren mang
					MyLog.i("ImageCache", " load lai ");
					new FetchImageTask().execute(message, url,
							buildCachedImagePath(url));
				}
				bmp = null;
			} catch (Throwable t) {
				// MyLog.e(TAG, "Exception downloading image", t);
			}

			return (null);
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		if (bitmap != null) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect1 = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			// final Rect rect2 = new Rect(0, 0, bitmap.getWidth() + 4, bitmap
			// .getHeight() + 4);
			// final RectF rectF = new RectF(rect2);
			// final float roundPx = 0;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			// paint.setStrokeWidth(1);
			canvas.drawColor(Color.WHITE);
			// paint.setStyle(Paint.Style.STROKE);
			// canvas.drawRect(rectF, paint);

			// phuc paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect1, paint);

			return output;
		} else {
			return bitmap;
		}
	}

	/**
	 * tinh sample size tu chuoi byte 
	 * 
	 * @author: PhucNT
	 * @return
	 * @return: int
	 * @throws:
	 */
	public int computeSampleSize(byte[] raw, int maxWidth, int maxHeight) {
		// TODO Auto-generated method stub
		if (maxWidth == 0 || maxHeight == 0)
			return 1;
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(raw, 0, raw.length, bmpFactoryOptions);
		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) maxHeight);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) maxWidth);
		// If both of the ratios are greater than 1,
		// one of the sides of the image is greater than the screen
		bmpFactoryOptions.inSampleSize = 1;
		if (heightRatio > 1 && widthRatio > 1) {
			if (heightRatio > widthRatio) {
				// Height ratio is larger, scale according to it
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				// Width ratio is larger, scale according to it
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}

		MyLog.e("BigPhotoCache", "bmpFactoryOptions.inSampleSize "
				+ bmpFactoryOptions.inSampleSize + "   " + maxWidth + "   "
				+ maxHeight);
	
		return bmpFactoryOptions.inSampleSize;
	}

	/**
	 * tinh sample size tu file
	 * 
	 * @author: PhucNT
	 * @return
	 * @return: int
	 * @throws:
	 */
	public int computeSampleSizeFromFile(File file, int maxWidth, int maxHeight) {
		// TODO Auto-generated method stub
		if (maxWidth == 0 || maxHeight == 0)
			return 1;
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), bmpFactoryOptions);
		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) maxHeight);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) maxWidth);
		// If both of the ratios are greater than 1,
		// one of the sides of the image is greater than the screen
		bmpFactoryOptions.inSampleSize = 1;
		if (heightRatio > 1 && widthRatio > 1) {
			if (heightRatio > widthRatio) {
				// Height ratio is larger, scale according to it
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				// Width ratio is larger, scale according to it
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}
		MyLog.e("BigPhotoCache", "bmpFactoryOptions.inSampleSize "
				+ bmpFactoryOptions.inSampleSize);

		return bmpFactoryOptions.inSampleSize;
	}

	
	/**
	 * Xoa nhung hinh anh nho neu vuot qua so luong cho phep
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @throws:
	 */
	protected void checkCleanCache(File file) {
		// TODO Auto-generated method stub
		if (cacheRoot != null) {
			rootCacheSize += file.length();
			MemoryUtils.getInstance().rootCacheSize = rootCacheSize;
			MyLog.e("KunKun", "kich thuoc vung nho cache "
					+ (rootCacheSize) + " mb");
			if (rootCacheSize > MAX_SIZE_IMAGE_CACHE) {
				rootCacheSize = 0;
				MemoryUtils.getInstance().rootCacheSize = 0;
				new CacheCleanTask().execute(mPolicy);
			}
		}
	}

}