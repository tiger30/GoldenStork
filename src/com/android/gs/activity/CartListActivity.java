package com.android.gs.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.gs.constant.MyConstants;
import com.android.gs.controller.UserController;
import com.android.gs.dto.CartItemData;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.StringUtil;
import com.android.gs.utils.ui.CartItem;
import com.anroid.gs.R;
import com.lib.cwac.thumbnail.ThumbnailAdapter;

public class CartListActivity extends BaseActivity implements OnClickListener {
	public static final int ORDERING_REQUEST = 0;
	ImageButton ivIcon;
	TextView tvNoItem;
	Button btEdit;
	TextView tvCost;
	Button btPayment;
	ListView lvProducts;

	ArrayList<ProductData> listData = new ArrayList<ProductData>();
	ArrayList<ProductData> pListData = new ArrayList<ProductData>();
	boolean isEditable = false;
	long totalCost = 0;
	private ThumbnailAdapter thumbs = null;
	private static final int[] IMAGE_IDS = { R.id.ivImage};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_cart_layout);
		init();
		requestLoadData();
	}

	/**
	 * init layout
	 */
	void init() {
		ivIcon = (ImageButton) findViewById(R.id.ivIcon);
		ivIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isEditable) {
					isEditable = false;
					listData = (ArrayList<ProductData>) pListData.clone();
					btEdit.setText(StringUtil.getString(R.string.edit));
					btPayment.setText(StringUtil.getString(R.string.payment));
					totalCost = 0;
					calculateTotalCost();

					if (listData.size() > 0) {
						tvNoItem.setVisibility(View.GONE);
					} else {
						tvNoItem.setVisibility(View.VISIBLE);
					}
					try {
						thumbs = new ThumbnailAdapter(
							CartListActivity.this,
							new ProductAdapter(),
								cache, IMAGE_IDS);
						thumbs.setRetryLoadImage(true);
					} catch (Exception e1) {

					}
					lvProducts.setAdapter(thumbs);
					
				} else {
					CartListActivity.this.finish();
				}
			}
		});

		btEdit = (Button) findViewById(R.id.btEdit);
		btEdit.setOnClickListener(this);
		tvCost = (TextView) findViewById(R.id.tvCost);
		btPayment = (Button) findViewById(R.id.btPayment);
		btPayment.setOnClickListener(this);

		// list view
		lvProducts = (ListView) findViewById(R.id.lvProducts);
		tvNoItem = (TextView) findViewById(R.id.tvNoItem);
	}

	/**
	 * calculate total cost
	 */
	void calculateTotalCost() {

		for (ProductData data : listData) {
			totalCost += data.getNewPrice() * data.quantity;
		}
		tvCost.setText(StringUtil.apendString2String(String.valueOf(totalCost),
				MyConstants.STR_DOT, 3, String.valueOf(totalCost).length())
				+ MyConstants.STR_SPACE + StringUtil.getString(R.string.vnd));
	}

	/**
	 * go to payment activity
	 */
	void gotoPaymentActivity() {
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_SWITH_TO_VIEW_PAYMENT;
		Bundle bun = new Bundle();
		bun.putSerializable(MyConstants.DATA_KEY, listData);
		e.viewData = bun;
		UserController.getInstance().handleSwitchActivity(e);

	}

	/**
	 * load data
	 */
	void requestLoadData() {
		listData = new ArrayList<ProductData>();
		
		listData = new ArrayList<ProductData>();
		for (ProductData data : listProduct) {
			if (data.isAddCart) {
				listData.add(data);
			}
		}
		setBackgroundButton(btPayment, listData.size() > 0);
		calculateTotalCost();
		if (listData.size() > 0) {
			tvNoItem.setVisibility(View.GONE);
			btEdit.setVisibility(View.VISIBLE);
		} else {
			tvNoItem.setVisibility(View.VISIBLE);
			if(!isEditable){
				btEdit.setVisibility(View.GONE);
			}
		}
		try {
			thumbs = new ThumbnailAdapter(
				CartListActivity.this,
				new ProductAdapter(),
					cache, IMAGE_IDS);
			thumbs.setRetryLoadImage(true);
		} catch (Exception e1) {

		}
		lvProducts.setAdapter(thumbs);
	}

	/**
	 * set background button enable/disable
	 */
	public void setBackgroundButton(Button bt, boolean isEnable) {
		bt.setEnabled(isEnable);
		if (isEnable) {
			bt.setBackgroundResource(R.xml.button_selector);
		} else {
			bt.setBackgroundResource(R.color.grey);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btEdit) {
			isEditable = !isEditable;
			if (isEditable) {
				btEdit.setText(StringUtil.getString(R.string.done));
				btPayment.setText(StringUtil.getString(R.string.finish_bill));
				pListData = (ArrayList<ProductData>) listData.clone();
			} else {// done
				btEdit.setText(StringUtil.getString(R.string.edit));
				btPayment.setText(StringUtil.getString(R.string.payment));
				setBackgroundButton(btPayment, listData.size() > 0);
				// update listcart
				new UpdateListCart().execute(this,listData);

			}
			thumbs.notifyDataSetChanged();
		} else if (v == btPayment) {
			if (isEditable) {
				isEditable = false;
				btEdit.setText(StringUtil.getString(R.string.edit));
				btPayment.setText(StringUtil.getString(R.string.payment));
				thumbs.notifyDataSetChanged();
				setBackgroundButton(btPayment, listData.size() > 0);
			} else {
				gotoPaymentActivity();
			}
		}
	}


	@Override
	public void onEvent(int eventType, View control, Object data) {
		// TODO Auto-generated method stub
		switch (eventType) {
		case OnEventControlListener.REMOVE_CART_ITEM:
			int position = (Integer) data;
			totalCost -= listData.get(position).getNewPrice()
					* listData.get(position).quantity;
			tvCost.setText(StringUtil.apendString2String(String
					.valueOf(totalCost), MyConstants.STR_DOT, 3, String
					.valueOf(totalCost).length())
					+ MyConstants.STR_SPACE
					+ StringUtil.getString(R.string.vnd));
			if (position < listData.size()) {
				listData.remove(position);
			}
			thumbs.notifyDataSetChanged();
			if (listData.size() > 0) {
				tvNoItem.setVisibility(View.GONE);
				btEdit.setVisibility(View.VISIBLE);
			} else {
				tvNoItem.setVisibility(View.VISIBLE);
				if(!isEditable){
					btEdit.setVisibility(View.GONE);
				}
			}
			break;
		case OnEventControlListener.ON_CHANGE_QUANTITY:
			Bundle bundle = (Bundle) data;
			int pos = bundle.getInt(MyConstants.DATA_KEY2);
			String str = bundle.getString(MyConstants.DATA_KEY);
			if (StringUtil.isNullOrEmpty(str)) {
				str = "0";
			}
			try {
				int quantity = Integer.parseInt(str);
				totalCost += listData.get(pos).getNewPrice()
						* Math.abs((listData.get(pos).quantity - quantity));
				listData.get(pos).quantity = quantity;
				tvCost.setText(StringUtil.apendString2String(
						String.valueOf(totalCost), MyConstants.STR_DOT, 3,
						String.valueOf(totalCost).length())
						+ MyConstants.STR_SPACE
						+ StringUtil.getString(R.string.vnd));
			} catch (Exception ex) {

			}

			
			break;
		}
		super.onEvent(eventType, control, data);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case ORDERING_REQUEST:
			finish();
			break;
		}
	}

	/**
	 * Product Adapter
	 * 
	 * @author DoanDM
	 * 
	 */
	public class ProductAdapter extends ArrayAdapter<ProductData> {

		ProductAdapter() {
			super(CartListActivity.this, R.layout.product_item_layout, listData);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			CartItem cell = null;
			if (row == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = vi.inflate(R.layout.cart_item_layout, parent, false);
				cell = new CartItem(getContext(), row);
				row.setTag(cell);
			} else {
				cell = (CartItem) row.getTag();
			}
			cell.renderLayout(position, isEditable, getItem(position));
			return row;
		}
	}
	
	class UpdateListCart extends AsyncTask<Object, Void, Void>{

		@Override
		protected Void doInBackground(Object... params) {
			// TODO Auto-generated method stub
			BaseActivity base = (BaseActivity) params[0];
			ArrayList<ProductData> listData = (ArrayList<ProductData>) params[1];
			base.listCart = new ArrayList<CartItemData>();
			for(ProductData data : listData){
				base.listCart.add(new CartItemData(data.productId, data.quantity));
				
			}
			for(int i = 0,size = base.listProduct.size();i<size;i++){
				base.listProduct.get(i).isAddCart = false;
				for(ProductData data : listData){
					if(data.productId ==  listProduct.get(i).productId){
						base.listProduct.get(i).isAddCart = true;
						base.listProduct.get(i).quantity = data.quantity;
						break;
					}
				}
			}
			return null;
		}
		
	}

}
