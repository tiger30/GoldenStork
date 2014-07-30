package com.android.gs.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.gs.components.slidemenu.ListFragment;
import com.android.gs.components.slidemenu.MainLayout;
import com.android.gs.components.slidemenu.SlideMenu;
import com.android.gs.constant.MyConstants;
import com.android.gs.controller.UserController;
import com.android.gs.dto.CategoryDTO;
import com.android.gs.dto.CategoryData;
import com.android.gs.dto.DistributorDTO;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.StringUtil;
import com.android.gs.utils.ui.ConfirmDialog;
import com.android.gs.utils.ui.SearchLayout;
import com.anroid.gs.R;

public class MainActivity extends BaseActivity implements OnClickListener {
	TextView title;
	ImageButton ivIcon;
	public MainLayout mLayout;

	ListFragment lFragment;
	SearchLayout mainSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_frame,
				(ViewGroup) findViewById(R.id.ll_content));
		init();
	}

	/**
	 * init layout
	 */
	void init() {
		mLayout = (MainLayout) findViewById(R.id.mLayout);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		lFragment = new ListFragment(this, mLayout, getIntent().getExtras());

		if (ft.isEmpty()) {
			ft.add(R.id.activity_main_content_fragment, lFragment);
		}
		ft.commit();

		llCart.setOnClickListener(this);

		ivIcon = (ImageButton) findViewById(R.id.ivIcon);
		ivIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lFragment.checkToogle(mainSearch, title, ivIcon);
			}
		});
		title = (TextView) findViewById(R.id.title);
		llCart = (LinearLayout) findViewById(R.id.llCart);
		llCart.setOnClickListener(cartListener);
		tvCart = (TextView) llCart.findViewById(R.id.tvCart);
		mainSearch = (SearchLayout) findViewById(R.id.mainSearch);

		SlideMenu mnSlideMenu = (SlideMenu) findViewById(R.id.mnSlideMenu);
		mnSlideMenu.setSlideWidth(llCart);
	}

	OnClickListener cartListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			gotoListCart();
		}
	};

	OnClickListener slideListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (searchLayout.getVisibility() == View.GONE) {
				hiddenSearchKeyboard();
				mLayout.toggleMenu();
			} else {
			}
		}
	};

	void loadProductList(Bundle bundle) {
		CategoryData category = (CategoryData) bundle.getSerializable(MyConstants.DATA_KEY);
		CategoryDTO catChild = (CategoryDTO) bundle.getSerializable(MyConstants.DATA_KEY2);
		String str = MyConstants.STR_BLANK;
		if(catChild!=null){
			str+=" > "+catChild.name;
		}
		title.setText(category.name + str);
		ArrayList<ProductData> arr = new ArrayList<ProductData>();
		for (ProductData data : listProduct) {
			if (catChild != null) {
				if (data.catChild == catChild.id) {
					arr.add(data);
				}
			} else {
				arr.add(data);
			}
		}
		lFragment.updateList(arr);
	}

	/**
	 * view product detail
	 */
	void viewProductDetail(Object obj) {
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_VIEW_PRODUCT_DETAIL;
		Bundle bun = new Bundle();
		bun.putInt(MyConstants.DATA_KEY, ((ProductData) obj).productId);
		e.viewData = bun;
		UserController.getInstance().handleSwitchActivity(e);
	}

	ArrayList<ProductData> performSearch(String key) {
		key = key.toLowerCase();
		ArrayList<ProductData> result = new ArrayList<ProductData>();
		for (ProductData data : listProduct) {
			if (data.name.toLowerCase().contains(key)
					|| data.description.toLowerCase().contains(key)) {
				result.add(data);
			}
		}
		return result;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == llCart) {
			gotoListCart();
		} else {
			gotoListProduct(v);
		}
	}

	/**
	 * go to list product
	 */
	void gotoListProduct(View v) {
		DistributorDTO dis = (DistributorDTO) v.getTag();
		CategoryData category = null;
		for (CategoryData data : listCategory) {
			if (data.id == dis.categoryId) {
				category = data;
				break;
			}
		}
		if (category != null) {
			Bundle bun = new Bundle();
			bun.putSerializable(MyConstants.DATA_KEY, category);
			lFragment.setCategoryData(bun);
			lFragment
					.swithMode(ActionEventConstant.ACTION_SWITH_TO_LIST_PRODUCT_SCREEN);
		} else {
//			showDialog("CATEGORY NULL---- PLEASE CHECK IT AGAIN!");
		}
	}

	@Override
	public void onEvent(int eventType, View control, Object data) {
		// TODO Auto-generated method stub
		Bundle bun;
		switch (eventType) {
		case OnEventControlListener.ON_LOAD_PRODUCT_LIST:
			loadProductList((Bundle) data);
			break;

		case OnEventControlListener.GO_TO_HOME_PAGE:
			if (mLayout.isMenuShown()) {
				mLayout.toggleMenu();
			}
			if (lFragment.isSearchMode()) {
				lFragment.setSearchMode(mainSearch, title, ivIcon, false);
			}
			if (lFragment.isListProductShown()) {
				lFragment
						.swithMode(ActionEventConstant.ACTION_SWITH_TO_CATEGORY_SCREEN);
				title.setText(StringUtil.getString(R.string.group_name));
			}
			break;
		case OnEventControlListener.ON_SELECT_PRODUCT_ITEM:
		case OnEventControlListener.VIEW_PRODUCT_DETAIL:
			viewProductDetail(data);
			break;
		case OnEventControlListener.GO_TO_CART_ACTIVITY:
			gotoListCart();
			break;
		case OnEventControlListener.ON_CHANGE_FRAGMENT_ATTACH:
			if (tvCart != null) {
				tvCart.setText(String.valueOf(listCart.size()));
			}
			break;
		case OnEventControlListener.ON_SEARCH_CLICK:
			if (mLayout.isMenuShown()) {
				mLayout.toggleMenu();
			}
			lFragment.setSearchMode(mainSearch, title, ivIcon, true);
			mainSearch.setText((String) data);
			lFragment.updateList(performSearch((String) data));
			break;
		case OnEventControlListener.ON_SELECT_CAT:
			if (mLayout.isMenuShown()) {
				mLayout.toggleMenu();
			}
			lFragment.setCategoryData((Bundle) data);
			lFragment
					.swithMode(ActionEventConstant.ACTION_SWITH_TO_LIST_PRODUCT_SCREEN);
			break;
		case OnEventControlListener.CONFIRM_YES:
			finish();
			break;
		case OnEventControlListener.CONFIRM_NO:
			break;

		}
		super.onEvent(eventType, control, data);
	}

	public void onBackPressed() {
		if (lFragment.isSearchMode()) {
			lFragment.setSearchMode(mainSearch, title, ivIcon, false);
		} else if (mLayout.isMenuShown()) {
			mLayout.toggleMenu();
		} else if (lFragment.isListProductShown()) {
			lFragment
					.swithMode(ActionEventConstant.ACTION_SWITH_TO_CATEGORY_SCREEN);
			title.setText(StringUtil.getString(R.string.group_name));
		} else {
			showExitDialog();
		}
	};
	
	/**
	 * show dialog
	 * 
	 * @param mes
	 * @return
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public AlertDialog showExitDialog() {
		AlertDialog alertDialog = null;
		try {
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setMessage(StringUtil.getString(R.string.exit_app));
			alertDialog.setButton(StringUtil.getString(R.string.no),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							return;

						}
					});
			alertDialog.setButton2(StringUtil.getString(R.string.exit),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							MainActivity.this.finish();
							return;

						}
					});
			alertDialog.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alertDialog;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		GlobalUtil.saveObject(listCart, LIST_CART_KEY);
		super.onDestroy();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());

	}

}
