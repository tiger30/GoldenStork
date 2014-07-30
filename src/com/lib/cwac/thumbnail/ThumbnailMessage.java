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

package com.lib.cwac.thumbnail;


import android.widget.ImageView;

public class ThumbnailMessage {
	public static final int STATUS_NONE = 0;
	public static final int STATUS_CANCEL = 1;
	public static final int STATUS_SUCCEED = 2;
	public static final int STATUS_NOT_FOUND = 3;
	public static final int STATUS_ERR = 4;

	private String key;
	private ImageView image;
	private String url;
	public boolean isRecycling;

	public int maxDimension = 0;
	// trang thai download
	public int status = STATUS_NONE;
	// vi tri cua item
	public int position = -1;

	public ThumbnailMessage(String key) {
		this.key = key;
	}

	public String getKey() {
		return (key);
	}

	public ImageView getImageView() {
		return (image);
	}

	public void setImageView(ImageView image) {
		this.image = image;
	}

	public String getUrl() {
		return (url);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		ThumbnailMessage msg = (ThumbnailMessage) o;
		if (this.url == msg.url)
			return true;
		return false;
	}
}