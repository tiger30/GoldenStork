package com.android.gs.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gs.constant.MyConstants;
import com.android.gs.controller.UserController;
import com.android.gs.dto.CartItemData;
import com.android.gs.dto.PaymentDTO;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.global.ModelEvent;
import com.android.gs.global.SpannableObject;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.StringUtil;
import com.android.gs.utils.ui.CartItem;
import com.android.gs.utils.ui.HorizontalListView;
import com.anroid.gs.R;
import com.lib.cwac.thumbnail.ThumbnailAdapter;

public class PaymentActivity extends BaseActivity implements OnClickListener {

	ArrayList<ProductData> listData = new ArrayList<ProductData>();
	HorizontalListView lvCart;
	TextView tvCost;
	private ThumbnailAdapter thumbs = null;
	private static final int[] IMAGE_IDS = { R.id.ivImage };
	RadioButton rbCash;
	RadioButton rbTranfer;
	EditText edName;
	EditText edEmail;
	EditText edPhone;
	EditText edContent;
	Button btPayment;
	ImageButton ivIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish_payment);
		init();
	}

	/**
	 * init layout
	 */
	void init() {

		rbCash = (RadioButton) findViewById(R.id.rbCash);
		rbTranfer = (RadioButton) findViewById(R.id.rbTranfer);
		edName = (EditText) findViewById(R.id.edName);
		edEmail = (EditText) findViewById(R.id.edEmail);
		edPhone = (EditText) findViewById(R.id.edPhone);
		edContent = (EditText) findViewById(R.id.edContent);

		listData = (ArrayList<ProductData>) getIntent().getExtras()
				.getSerializable(MyConstants.DATA_KEY);
		lvCart = (HorizontalListView) (findViewById(R.id.lvCart));
		tvCost = (TextView) findViewById(R.id.tvCost);

		long totalCost = 0;
		for (ProductData data : listData) {
			totalCost += data.getNewPrice() * data.quantity;
		}
		tvCost.setText(StringUtil.apendString2String(String.valueOf(totalCost),
				MyConstants.STR_DOT, 3, String.valueOf(totalCost).length())
				+ MyConstants.STR_SPACE + StringUtil.getString(R.string.vnd));

		try {
			thumbs = new ThumbnailAdapter(PaymentActivity.this,
					new ProductAdapter(), cache, IMAGE_IDS);
			thumbs.setRetryLoadImage(true);
		} catch (Exception e1) {

		}
		lvCart.setAdapter(thumbs);
		//ImageView ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
		//ImageView ivNext = (ImageView) findViewById(R.id.ivNext);
		//lvCart.setIndicatorImage(ivNext, ivPrevious);

		TextView tvNameTitle = (TextView) findViewById(R.id.tvNameTitle);
		SpannableObject span = new SpannableObject();
		span.addSpan(StringUtil.getString(R.string.name));
		span.addSpan(MyConstants.STR_SPACE + MyConstants.STR_STAR,
				getResources().getColor(R.color.red), Typeface.NORMAL);
		tvNameTitle.setText(span.getSpan());

		TextView tvMobile = (TextView) findViewById(R.id.tvMobile);
		SpannableObject obj = new SpannableObject();
		obj.addSpan(StringUtil.getString(R.string.mobile));
		obj.addSpan(MyConstants.STR_SPACE + MyConstants.STR_STAR,
				getResources().getColor(R.color.red), Typeface.NORMAL);
		tvMobile.setText(obj.getSpan());

		TextView tvEmailTitle = (TextView) findViewById(R.id.tvEmailTitle);
		SpannableObject obj2 = new SpannableObject();
		obj2.addSpan(StringUtil.getString(R.string.email));
		obj2.addSpan(MyConstants.STR_SPACE + MyConstants.STR_STAR,
				getResources().getColor(R.color.red), Typeface.NORMAL);
		tvEmailTitle.setText(obj2.getSpan());

		btPayment = (Button) findViewById(R.id.btPayment);
		btPayment.setOnClickListener(this);
		ivIcon = (ImageButton) findViewById(R.id.ivIcon);
		ivIcon.setOnClickListener(this);
	}

	boolean isAllDataValid() {
		String name = edName.getText().toString();
		String phone = edPhone.getText().toString();
		String email = edEmail.getText().toString();
		if (StringUtil.isNullOrEmpty(name)) {
			Toast.makeText(this, StringUtil.getString(R.string.empty_name),
					Toast.LENGTH_SHORT).show();
			edName.requestFocus();
			return false;
		} else if (StringUtil.isNullOrEmpty(email)) {
			Toast.makeText(this, StringUtil.getString(R.string.empty_email),
					Toast.LENGTH_SHORT).show();
			edEmail.requestFocus();
			return false;
		} else if (StringUtil.isNullOrEmpty(phone)) {
			Toast.makeText(this, StringUtil.getString(R.string.empty_phone),
					Toast.LENGTH_SHORT).show();
			edPhone.requestFocus();
			return false;
		} 
		if (!StringUtil.validateEMail(email)) {
			Toast.makeText(this, StringUtil.getString(R.string.invalid_email),
					Toast.LENGTH_SHORT).show();
			edEmail.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void handleModelViewEvent(ModelEvent modelEvent) {
		// TODO Auto-generated method stub
		super.handleModelViewEvent(modelEvent);
		ActionEvent e = modelEvent.getActionEvent();
		switch (e.action) {
		case ActionEventConstant.ACTION_REQUEST_PAYMENT:
			PaymentDTO pm = (PaymentDTO) e.modelData;
			Toast.makeText(this, pm.getMessage(), Toast.LENGTH_SHORT).show();
			for(int i = 0,size = listProduct.size();i<size;i++){
				listProduct.get(i).isAddCart = false;
				listProduct.get(i).quantity = 0;		
			}
			listCart = new ArrayList<CartItemData>();
			GlobalUtil.saveObject(listCart, LIST_CART_KEY);
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
			super(PaymentActivity.this, R.layout.product_item_layout, listData);
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
			cell.renderLayout(position, false, getItem(position));
			return row;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (btPayment == v) {
			if (isAllDataValid()) {
				requestPayment();
			}
		} else if (ivIcon == v) {
			finish();
		}
	}

	void requestPayment() {
		showProgressDialog(StringUtil.getString(R.string.processing), true);
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_REQUEST_PAYMENT;
		PaymentDTO paymentDTO = new PaymentDTO(rbCash.isChecked() ? 1 : 2,
				edName.getText().toString(), edEmail.getText().toString(),
				edPhone.getText().toString(), edContent.getText().toString(),
				listData);
		e.viewData = paymentDTO;
		UserController.getInstance().handleViewEvent(e);
	}
}
