package com.android.gs.utils.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.gs.activity.BaseActivity;
import com.android.gs.constant.MyConstants;
import com.android.gs.dto.ProductData;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.ImageUtil;
import com.anroid.gs.R;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;

public class CartItem implements OnClickListener,TextWatcher{
	Context context;
	View view;
	ImageButton ibRemove;
	ImageView ivImage;
	TextView tvTitle;
	TextView tvContent;
	EditText edQuantity;
	TextView tvNewPrice;
	int position;
	TextView tvDelete;
	LinearLayout llCartMain;
	Bundle bundle = new Bundle();
	public CartItem(Context context,View v){
		this.context = context;
		view = v;
		ibRemove = (ImageButton)view.findViewById(R.id.ibRemove);
		ibRemove.setOnClickListener(this);
		ivImage = (ImageView)view.findViewById(R.id.ivImage);
		tvTitle = (TextView)view.findViewById(R.id.tvTitle);
		tvContent = (TextView)view.findViewById(R.id.tvContent);
		edQuantity = (EditText) view.findViewById(R.id.edQuantity);
		edQuantity.addTextChangedListener(this);
		tvNewPrice = (TextView)view.findViewById(R.id.tvNewPrice);
		
		tvDelete = (TextView) view.findViewById(R.id.tvDelete);
		tvDelete.setOnClickListener(this);
		llCartMain = (LinearLayout) view.findViewById(R.id.llCartMain);
		llCartMain.setOnClickListener(this);
				
	}
	public void renderLayout(int position,boolean isEditable,ProductData data){
		this.position = position;
		if(isEditable){
			ibRemove.setVisibility(View.VISIBLE);
			tvDelete.setTag(position);
			edQuantity.setEnabled(true);
		}else{
			ibRemove.setVisibility(View.GONE);
			edQuantity.setEnabled(false);
		}
		tvTitle.setText(data.name);
		tvContent.setText(data.description);
		edQuantity.setText(String.valueOf(data.quantity));
		tvNewPrice.setText(data.getStrNewPrice());
		
		ivImage.setTag(data.imageThumb);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == ibRemove){
			tvDelete.setVisibility(View.VISIBLE);
			llCartMain.setPadding(-(int) context.getResources().getDimension(R.dimen.item_product_height), 0, 0, 0);
		}else if( v == tvDelete){
			llCartMain.setPadding(0, 0, 0, 0);
			tvDelete.setVisibility(View.GONE);
			((OnEventControlListener)context).onEvent(OnEventControlListener.REMOVE_CART_ITEM, null, v.getTag());
		}else if(v ==  llCartMain){
			tvDelete.setVisibility(View.GONE);
			llCartMain.setPadding(0, 0, 0, 0);
		}
	}
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		bundle.putString(MyConstants.DATA_KEY, cs.toString());
		bundle.putInt(MyConstants.DATA_KEY2, position);
		((OnEventControlListener)context).onEvent(OnEventControlListener.ON_CHANGE_QUANTITY, null, bundle);
	}
}
