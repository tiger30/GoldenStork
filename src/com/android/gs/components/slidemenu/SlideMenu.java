package com.android.gs.components.slidemenu;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.gs.activity.BaseActivity;
import com.android.gs.constant.MyConstants;
import com.android.gs.dto.CategoryDTO;
import com.android.gs.dto.CategoryData;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.MyLog;
import com.anroid.gs.R;

public class SlideMenu extends RelativeLayout implements OnClickListener {

	ImageView ivNavigator;
	LinearLayout llCartMenu;
	LinearLayout llHome;

	LinearLayout llDistributor;
	LayoutInflater inflater;
	LinearLayout llMainCat;

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.slide_menu_layout, this);
		init();
	}

	/**
	 * init layout
	 * 
	 * DoanDM Jan 10, 2014
	 */
	void init() {

		llMainCat = (LinearLayout) findViewById(R.id.llGroupMainCat);

		LinearLayout llCategories = (LinearLayout) findViewById(R.id.llCategories);
		llCategories.setTag(0);
		llCategories.setOnClickListener(catListener);
		ivNavigator = (ImageView) findViewById(R.id.ivNavigator);

		llDistributor = (LinearLayout) findViewById(R.id.llDistributor);

		llCartMenu = (LinearLayout) findViewById(R.id.llCartMenu);
		llHome = (LinearLayout) findViewById(R.id.llHome);
		llCartMenu.setOnClickListener(this);
		llHome.setOnClickListener(this);

		// init distributors
		for (CategoryData cat : ((BaseActivity) getContext()).listCategory) {
			LinearLayout view = (LinearLayout) inflater.inflate(
					R.layout.slide_menu_cat_item, null);
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText(cat.name);
			view.setOnClickListener(cat1Click);
			view.setTag(false);
			// add sub cat
			MyLog.i(getClass().getSimpleName(),
					"************************************");
			MyLog.e(getClass().getSimpleName(), "cat.name:" + cat.name);
			MyLog.d(getClass().getSimpleName(),
					"------------------------------------");
			for (CategoryDTO sub : cat.listSubCategory) {
				LinearLayout catView = (LinearLayout) inflater.inflate(
						R.layout.slide_menu_cat_item2, null);
				catView.setVisibility(View.GONE);
				LinearLayout llMainCat = (LinearLayout) catView
						.findViewById(R.id.llMainCat);
				llMainCat.setOnClickListener(cat2Click);
				Bundle bun = new Bundle();
				bun.putSerializable(MyConstants.DATA_KEY, cat);
				bun.putSerializable(MyConstants.DATA_KEY2, sub);
				llMainCat.setTag(bun);
				TextView tvNameCat2 = (TextView) catView
						.findViewById(R.id.tvNameCat2);
				tvNameCat2.setText(sub.name);
				MyLog.e(getClass().getSimpleName(), "sub.name:" + sub.name);
				view.addView(catView);
			}
			MyLog.d(getClass().getSimpleName(),
					"------------------------------------");
			MyLog.i(getClass().getSimpleName(),
					"************************************");
			llDistributor.addView(view);
		}

	}

	public void setSlideWidth(View view) {
		int w = GlobalUtil.getSlideMenuWidth();
		View v = findViewById(R.id.slideMenuMain);
		if (w == 0) {
			view.measure(GlobalUtil.getDisplayWidth(),
					GlobalUtil.getDisplayHeight());
			w = (int) (GlobalUtil.getDisplayWidth() - view.getMeasuredWidth() - getContext()
					.getResources().getDimension(R.dimen.standard_padding));
			GlobalUtil.setSlideMenuWidth(w);
		}

		LayoutParams params = new LayoutParams(w,
				LinearLayout.LayoutParams.FILL_PARENT);
		v.setLayoutParams(params);
	}

	OnClickListener cat1Click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			boolean isExpanded = (Boolean) v.getTag();
			isExpanded = !isExpanded;
			v.setTag(isExpanded);

			ImageView ivIndicator = (ImageView) v
					.findViewById(R.id.ivIndicator);
			if (isExpanded) {
				ivIndicator.setImageResource(R.drawable.ic_action_expand);
				for (int i = 1, size = ((ViewGroup) v).getChildCount(); i < size; i++) {
					((ViewGroup) v).getChildAt(i).setVisibility(View.VISIBLE);

				}
			} else {
				ivIndicator.setImageResource(R.drawable.ic_action_collapse);
				for (int i = 1, size = ((ViewGroup) v).getChildCount(); i < size; i++) {
					((ViewGroup) v).getChildAt(i).setVisibility(View.GONE);

				}

			}

		}
	};
	OnClickListener cat2Click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			((OnEventControlListener) getContext()).onEvent(
					OnEventControlListener.ON_SELECT_CAT, null, v.getTag());

		}
	};

	OnClickListener catListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if ((Integer) v.getTag() == 0) {
				v.setTag(1);
				// ivNavigator.setImageResource(R.drawable.expaned_icon);
				ivNavigator.setImageResource(R.drawable.expaned_icon);
				llDistributor.setVisibility(View.VISIBLE);
				llMainCat.setBackgroundColor(getContext().getResources()
						.getColor(R.color.background_button_orange));
			} else {
				llMainCat.setBackgroundColor(getContext().getResources()
						.getColor(android.R.color.transparent));
				ivNavigator.setImageResource(R.drawable.collapsed_icon);
				v.setTag(0);
				llDistributor.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == llCartMenu) {
			// send action event to activity
			((OnEventControlListener) getContext()).onEvent(
					OnEventControlListener.GO_TO_CART_ACTIVITY, null, null);
		} else if (v == llHome) {
			// send action event to activity
			((OnEventControlListener) getContext()).onEvent(
					OnEventControlListener.GO_TO_HOME_PAGE, null, null);
		} else {
			((OnEventControlListener) getContext()).onEvent(
					OnEventControlListener.ON_SELECT_PRODUCT_ITEM, null,
					v.getTag());
		}
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// // TODO Auto-generated method stub
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// v.setPressed(true);
	// return true;
	// case MotionEvent.ACTION_UP:
	// v.setPressed(false);
	// ((OnEventControlListener) getContext()).onEvent(
	// OnEventControlListener.ON_SLIDE_MENU_ITEM_CLICK, v,
	// v.getTag());
	// break;
	// }
	// return false;
	// }

}
