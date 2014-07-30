package com.android.gs.components.slidemenu;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.gs.activity.BaseActivity;
import com.android.gs.activity.MainActivity;
import com.android.gs.dto.DistributorDTO;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.ImageUtil;
import com.anroid.gs.R;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;

public class MainCategoryLayout {

	public LinearLayout llContent;
	ImageView ivBanner;
	MainActivity activity;
	ArrayList<DistributorDTO> listDistributor = new ArrayList<DistributorDTO>();
	ThumbnailBus bus;
	public  SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache ;
	public MainCategoryLayout(MainActivity activity,View v){
		this.activity = activity;
		llContent = (LinearLayout) v.findViewById(R.id.llContent);
		ivBanner = (ImageView) v.findViewById(R.id.ivBanner);
		for(int i = 1,size = BaseActivity.listDistributor.size();i<size;i++){
			listDistributor.add(BaseActivity.listDistributor.get(i));
		}
		 bus = new ThumbnailBus();
		 cache =  new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(
					null, null, 101, bus);
		 ImageUtil.scaleImage(ivBanner, GlobalUtil.getDisplayWidth()+ 2, 1024, 250);
		ImageUtil.getImageFromUrlUseCache(BaseActivity.listDistributor.get(0).image, cache, activity, ivBanner);
		renderLayout();
	}
	
	public void setVisibility(int visibility){
		llContent.setVisibility(visibility);
	}
	
	public int getVisibility(){
		return llContent.getVisibility();
	}
	
	void renderLayout(){
		int size = listDistributor.size();
		if(size < 1) return;
		if(size == 1){
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.single_item, null);
			v.setTag(listDistributor.get(0));
			setSingleData(v,listDistributor.get(0));
			v.setOnClickListener(activity);
			llContent.addView(v);
		}else{
			if(size%2 == 0){
				float perCent = 2.0f/(float) size;
				for(int i= 0;i<size;i+=2){
					LayoutInflater inflater = (LayoutInflater) activity
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View v = inflater.inflate(R.layout.double_item, null);
					View llLayout1 = v.findViewById(R.id.llLayout1);
					View llLayout2 = v.findViewById(R.id.llLayout2);
					llLayout1.setTag(listDistributor.get(i));
					llLayout2.setTag(listDistributor.get(i+1));
					llLayout1.setOnClickListener(activity);
					llLayout2.setOnClickListener(activity);
					
					LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LayoutParams.FILL_PARENT,
                            LayoutParams.FILL_PARENT, perCent);
					v.setLayoutParams(param);
					
					setDoubleData(v, listDistributor.get(i), listDistributor.get(i+1));
					llContent.addView(v);
				}
			}else{
				int i = 0;
				float perCent = 2.0f/(float) (size+1);
				while(i<size){
					if(i==0||(i-1)%2==0){
						LayoutInflater inflater = (LayoutInflater) activity
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View v = inflater.inflate(R.layout.double_item, null);
						View llLayout1 = v.findViewById(R.id.llLayout1);
						View llLayout2 = v.findViewById(R.id.llLayout2);
						llLayout1.setTag(listDistributor.get(i));
						llLayout2.setTag(listDistributor.get(i+1));
						llLayout1.setOnClickListener(activity);
						llLayout2.setOnClickListener(activity);
						
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
	                            LayoutParams.FILL_PARENT,
	                            LayoutParams.FILL_PARENT, perCent);
						v.setLayoutParams(param);
						setDoubleData(v, listDistributor.get(i), listDistributor.get(i+1));
						llContent.addView(v);
						i+=2;
					}else{
						LayoutInflater inflater = (LayoutInflater) activity
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View v = inflater.inflate(R.layout.single_item, null);
						v.setTag(listDistributor.get(i));
						v.setOnClickListener(activity);
						
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
	                            LayoutParams.FILL_PARENT,
	                            LayoutParams.FILL_PARENT, perCent);
						v.setLayoutParams(param);
						setSingleData(v,listDistributor.get(i));
						llContent.addView(v);
						i++;
					}
				}
			}
		}
	}
	
	void setSingleData(View v,DistributorDTO data){
		TextView textView = (TextView)v.findViewById(R.id.textView);
		textView.setText(data.title);
		ImageView image = (ImageView) v.findViewById(R.id.image);
		ImageUtil.getImageFromUrlUseCache(data.image,activity.cache, activity, image);
	}
	
	void setDoubleData(View v,DistributorDTO data1,DistributorDTO data2){
		ImageView image1 = (ImageView) v.findViewById(R.id.image1);
		ImageUtil.getImageFromUrlUseCache(data1.image, activity.cache, activity, image1);
		
		ImageView image2 = (ImageView) v.findViewById(R.id.image2);
		ImageUtil.getImageFromUrlUseCache(data2.image, activity.cache, activity, image2);
	}
	
}
