package com.android.gs.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gs.constant.MyConstants;
import com.android.gs.dto.CartItemData;
import com.android.gs.dto.CategoryDTO;
import com.android.gs.dto.CategoryData;
import com.android.gs.dto.ProductData;
import com.android.gs.utils.ImageUtil;
import com.android.gs.utils.StringUtil;
import com.android.gs.utils.ui.GalleryDialog;
import com.anroid.gs.R;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;

public class ProductDetailActivity extends BaseActivity implements OnClickListener{

	ImageButton ivIcon;
	TextView title;
	TextView tvName;
	LinearLayout llPrice;
	TextView tvOldPrice;
	TextView tvPrice;
	LinearLayout llDiscount;
	TextView tvDiscount;
	Button btAddCart;
	TextView tvDescription;
	
	int position = -1;
	GalleryDialog gDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_detail_layout);
		initLayout();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(listProduct.get(position).isAddCart){
			btAddCart.setText(StringUtil.getString(R.string.remove_cart));
		}else{
			btAddCart.setText(StringUtil.getString(R.string.add_to_cart));
		}
	}
	
	/**
	 * init layout
	 */
	void initLayout(){
		int productId =  getIntent().getExtras().getInt(MyConstants.DATA_KEY);
		for(int i = 0,size =listProduct.size();i<size;i++){
			if(listProduct.get(i).productId == productId){
				position = i;
				break;
			}
		}
		if(position == -1){
			Toast.makeText(this, StringUtil.getString(R.string.common_error), Toast.LENGTH_SHORT).show();
			finish();
		}
		
		llCart = (LinearLayout) findViewById(R.id.llCart);
		llCart.setOnClickListener(this);
		ivIcon = (ImageButton) findViewById(R.id.ivIcon);
		ivIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ProductDetailActivity.this.finish();
			}
		});
		
		title = (TextView) findViewById(R.id.title);
		tvName = (TextView) findViewById(R.id.tvName);
		llPrice = (LinearLayout) findViewById(R.id.llPrice);
		tvOldPrice = (TextView) findViewById(R.id.tvOldPrice);
		tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
		tvPrice = (TextView) findViewById(R.id.tvPrice);
		llDiscount = (LinearLayout) findViewById(R.id.llDiscount);
		tvDiscount = (TextView) findViewById(R.id.tvDiscount);
		btAddCart = (Button) findViewById(R.id.btAddCart);
		btAddCart.setOnClickListener(this);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvDescription.setMovementMethod(new ScrollingMovementMethod());
		
		View ibOpenDlg = findViewById(R.id.ibOpenDlg);
		ibOpenDlg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gDialog.show();
			}
		});
		setData();
	}
	
	/**
	 * set data for layout
	 */
	void setData(){
//		StringBuilder textTitle = new StringBuilder();
//		for(CategoryData catData : listCategory){
//			if(listProduct.get(position).catParent == catData.id){
//				textTitle.append(catData.name+" > ");
//				for(CategoryDTO catDTO : catData.listSubCategory){
//					if(listProduct.get(position).catChild == catDTO.id){
//						textTitle.append(catDTO.name);
//						break;
//					}
//				}
//				break;
//			}
//		}
		gDialog = new GalleryDialog(listProduct.get(position).imageList, this);
		title.setText(listProduct.get(position).name);
		tvName.setText(listProduct.get(position).name);
		if(listProduct.get(position).hasDiscount()){
//			llPrice.setBackgroundColor(R.color.price_bg);
			llDiscount.setVisibility(View.VISIBLE);
			tvOldPrice.setVisibility(View.VISIBLE);
			tvOldPrice.setText(listProduct.get(position).getStrOldPrice());
			tvDiscount.setText(listProduct.get(position).getDiscountPercentString());
		}else{
			llDiscount.setVisibility(View.GONE);
			tvOldPrice.setVisibility(View.GONE);
//			llPrice.setBackgroundColor(android.R.color.transparent);
		}
		
		tvPrice.setText(listProduct.get(position).getStrNewPrice());
		tvDescription.setText(Html.fromHtml(listProduct.get(position).description));
		ImageView ivProduct = (ImageView) findViewById(R.id.ivProduct);
		ImageUtil.getImageFromUrlUseCache(listProduct.get(position).imageDetail, cache, this , ivProduct);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == llCart){
			gotoListCart();
		}else if(v ==  btAddCart){
			boolean isAddCart = listProduct.get(position).isAddCart;
			listProduct.get(position).isAddCart = !isAddCart;
			if(listProduct.get(position).isAddCart){
				btAddCart.setText(StringUtil.getString(R.string.remove_cart));
			}else{
				btAddCart.setText(StringUtil.getString(R.string.add_to_cart));
			}
			if(listProduct.get(position).isAddCart){
				listProduct.get(position).quantity = 1;
				listCart.add(new CartItemData(listProduct.get(position).productId, listProduct.get(position).quantity));
			}else{
				listProduct.get(position).quantity = 0;
				for(int i = 0,size = listCart.size();i<size;i++){
					if(listCart.get(i).productId == listProduct.get(position).productId){
						listCart.remove(i);
						break;
					}
				}
			}
			if(tvCart != null){
				tvCart.setText(String.valueOf(listCart.size()));
			}
		}
	}
}
