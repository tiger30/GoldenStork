package com.android.gs.utils.ui;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.gs.activity.BaseActivity;
import com.android.gs.dto.ProductData;
import com.android.gs.utils.ImageUtil;
import com.anroid.gs.R;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;

public class ProductItem {
	Context context;
	ImageView ivProduct;
	TextView tvTitle;
	TextView tvContent;
	TextView tvOldPrice;
	TextView tvNewPrice;
	LinearLayout llDiscount;
	TextView tvDiscount;
	public ProductItem(Context context,View v){
		this.context = context;
		ivProduct = (ImageView) v.findViewById(R.id.ivProduct);
		tvTitle = (TextView) v.findViewById(R.id.tvTitle);
		tvContent = (TextView) v.findViewById(R.id.tvContent);
		tvOldPrice = (TextView) v.findViewById(R.id.tvOldPrice);
		tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
		tvNewPrice = (TextView) v.findViewById(R.id.tvNewPrice);
		llDiscount = (LinearLayout) v.findViewById(R.id.llDiscount);
		tvDiscount = (TextView) v.findViewById(R.id.tvDiscount);
	}
	
	public void renderLayout(ProductData data){
		tvTitle.setText(data.name);
		tvContent.setText(Html.fromHtml(data.description));
		if(data.hasDiscount()){
			llDiscount.setVisibility(View.VISIBLE);
			tvOldPrice.setVisibility(View.VISIBLE);
			tvOldPrice.setText(data.getStrOldPrice());
			tvDiscount.setText(data.getDiscountPercentString());
		}else{
			llDiscount.setVisibility(View.GONE);
			tvOldPrice.setVisibility(View.GONE);
		}
		tvNewPrice.setText(data.getStrNewPrice());
		ivProduct.setTag(data.imageThumb);
	}
}
