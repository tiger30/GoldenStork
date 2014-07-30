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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.gs.utils.MyLog;
import com.commonsware.cwac.bus.SimpleBus;
import com.commonsware.cwac.task.AsyncTaskEx;

public class WebImageCache extends
		AsyncCache<String, Drawable, SimpleBus, Bundle> {
	private static String TAG = "WebImageCache";
	private Drawable placeholder = null;

	static public File buildCachedImagePath(File cacheRoot, String url)
			throws Exception {
		return (new File(cacheRoot, md5(url)));
	}

	static protected String md5(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");

		md.update(s.getBytes());

		byte digest[] = md.digest();
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < digest.length; i++) {
			result.append(Integer.toHexString(0xFF & digest[i]));
		}

		return (result.toString());
	}

	public WebImageCache(File cacheRoot, SimpleBus bus,
			AsyncCache.DiskCachePolicy policy, int maxSize, Drawable placeholder) {
		super(cacheRoot, bus, policy, maxSize);

		this.placeholder = placeholder;
	}

	public void handleImageView(final ImageView image, final String url,
			String tag) throws Exception {
		Bundle message = ((SimpleBus) getBus()).createMessage(url);

		Drawable d = get(url, message);

		image.setImageDrawable(d);

		if (d == placeholder) {
			SimpleBus.Receiver r = (SimpleBus.Receiver) image.getTag();
			if (r != null) {
				getBus().unregister(r);
			}
			r = new SimpleBus.Receiver<Bundle>() {
				public void onReceive(final Bundle message) {
					((Activity) image.getContext())
							.runOnUiThread(new Runnable() {
								public void run() {
									if (url.equals(message
											.getString(SimpleBus.KEY))) {
										image.setImageDrawable(get(url, null));
									} else {
									}
								}
							});
				}
			};

			image.setTag(r);
			getBus().register(url, r, tag);
		}
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
				MyLog.e(TAG, "Exception getting cache status", t);
			}
		}

		return (result);
	}

	protected Drawable create(String key, Bundle message, int forceStyle) {
		if (getCacheRoot() != null) {
			try {
				File cache = buildCachedImagePath(key);

				if (cache.exists() && forceStyle == FORCE_NONE) {
					return (new BitmapDrawable(cache.getAbsolutePath()));
				} else {
					new FetchImageTask().execute(message, key, cache);
				}
			} catch (Throwable t) {
				MyLog.e(TAG, "Exception loading image", t);
			}
		} else {
			new FetchImageTask().execute(message, key, null);
		}

		return (placeholder);
	}

	public File buildCachedImagePath(String url) throws Exception {
		return (buildCachedImagePath(getCacheRoot(), url));
	}

	class FetchImageTask extends AsyncTaskEx<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			String url = params[1].toString();
			File cache = (File) params[2];
			Bitmap bitmap = null;
			try {
				URLConnection connection = new URL(url).openConnection();
				InputStream stream = connection.getInputStream();
				BufferedInputStream in = new BufferedInputStream(stream);
				ByteArrayOutputStream out = new ByteArrayOutputStream(10240);
				int read;
				byte[] b = new byte[4096];

				while ((read = in.read(b)) != -1) {
					out.write(b, 0, read);
				}

				out.flush();
				out.close();

				byte[] raw = out.toByteArray();

//				Bitmap bm = BitmapFactory.decodeByteArray(raw, 0, raw.length);
//				Bitmap bitmap = getRoundedCornerBitmap(bm);
//				bm.recycle();
//				bm = null;
				
				bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				
				raw = baos.toByteArray();
				
				WebImageCache.this.put(url, new BitmapDrawable(bitmap));

				Bundle message = (Bundle) params[0];

				if (message != null) {
					getBus().send(message);
				}

				if (cache != null) {
					FileOutputStream file = new FileOutputStream(cache);

					file.write(raw);
					file.flush();
					file.close();
				}
			} catch (Throwable t) {
				MyLog.e(TAG, "Exception downloading image", t);
				if(bitmap != null){
					bitmap.recycle();
					bitmap = null;
				}
			}

			return (null);
		}
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		if (bitmap != null) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
					.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect1 = new Rect(0, 0, bitmap.getWidth(), bitmap
					.getHeight());
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap
					.getHeight());
//			final Rect rect2 = new Rect(0, 0, bitmap.getWidth() + 4, bitmap
//					.getHeight() + 4);
//			final RectF rectF = new RectF(rect2);
//			final float roundPx = 0;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
//			paint.setStrokeWidth(1);
			canvas.drawColor(Color.WHITE);
//			paint.setStyle(Paint.Style.STROKE);
//			canvas.drawRect(rectF, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect1, paint);

			return output;
		} else {
			return bitmap;
		}
	}
}