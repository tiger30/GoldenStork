package com.android.gs.utils.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.anroid.gs.R;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailAdapter;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;

/**
 * Confirm dialog, with Yes/No choosen
 * 
 * @author DoanDM
 * 
 */
public class GalleryDialog extends Dialog implements
		android.view.View.OnClickListener {
	private ThumbnailAdapter thumbs = null;
	private static final int[] IMAGE_IDS = { R.id.imageView };
	public SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache;
	ThumbnailBus bus;
	Activity activity;
	ImageView ivLeft;
	ImageView ivRight;
	Gallery gallery;
	View ibClose;
	TextView tvNum;
	public GalleryDialog(ArrayList<String> arr, Activity context) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery_dialog);
		setCanceledOnTouchOutside(true);

		bus = new ThumbnailBus();
		cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null,
				null, 101, bus);
		activity = context;
		init(arr);
	}

	void init(final ArrayList<String> arr) {
		tvNum = (TextView) findViewById(R.id.tvNum);
		ivRight = (ImageView) findViewById(R.id.ivRight);
		ivLeft =(ImageView) findViewById(R.id.ivLeft);		
		ivRight.setOnClickListener(this);
		ivLeft.setOnClickListener(this);
		gallery = (Gallery) findViewById(R.id.gallery);
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if(position == 0){
					ivLeft.setVisibility(View.GONE);
					ivRight.setVisibility(View.VISIBLE);
				}else if(position == arr.size() - 1){
					ivLeft.setVisibility(View.VISIBLE);
					ivRight.setVisibility(View.GONE);
				}else{
					ivLeft.setVisibility(View.VISIBLE);
					ivRight.setVisibility(View.VISIBLE);
				}
				tvNum.setText((position+1)+"/"+adapter.getCount());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		ibClose = findViewById(R.id.ibClose);
		ibClose.setOnClickListener(this);
		try {
			thumbs = new ThumbnailAdapter(activity, new GalleryImageAdapter(
					getContext(), arr), cache, IMAGE_IDS);
			thumbs.setRetryLoadImage(true);
			gallery.setAdapter(thumbs);
			gallery.setSelection(0);
		} catch (Exception e1) {

		}
		gallery.setSelection(0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == ibClose){
			dismiss();
		}else if(v == ivRight){
			gallery.setSelection(gallery.getSelectedItemPosition()+1);
		}else if (v == ivLeft){
			gallery.setSelection(gallery.getSelectedItemPosition()-1);
		}
	}

}
