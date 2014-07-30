package com.android.gs.listener;

import android.graphics.Bitmap;

public interface ImageDownLoadListener {
	public void onStart();
	public void onFinished(Bitmap bitmap);
}
