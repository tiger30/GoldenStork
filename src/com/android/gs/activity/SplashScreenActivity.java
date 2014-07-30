package com.android.gs.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

import com.android.gs.constant.MyConstants;
import com.android.gs.controller.UserController;
import com.android.gs.dto.CartItemData;
import com.android.gs.dto.CategoryData;
import com.android.gs.dto.DistributorDTO;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.global.ModelEvent;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.Setting;
import com.android.gs.utils.StringUtil;
import com.anroid.gs.R;


public class SplashScreenActivity extends BaseActivity {

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setFullscreen(true);
		setContentView(R.layout.splash_screen);
		// thread for displaying the SplashScreen
		Setting.getInstance().loadSetting(this);
		loadBanners();

	}

	private void lauchActivity() {
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void handleModelViewEvent(ModelEvent modelEvent) {
		// TODO Auto-generated method stub
		super.handleModelViewEvent(modelEvent);
		ActionEvent e = modelEvent.getActionEvent();
		switch(e.action){
		case ActionEventConstant.ACTION_LOAD_BANNER:
			listDistributor = (ArrayList<DistributorDTO>) e.modelData;
			loadAllCategory();
			break;
		case ActionEventConstant.ACTION_LOAD_ALL_CATEGORY:
			listCategory = (ArrayList<CategoryData>) e.modelData;
			loadAllProduct();
			break;
		case ActionEventConstant.ACTION_LOAD_PRODUCT_LIST:
			listProduct = (ArrayList<ProductData>) e.modelData;
			loadCart();
			break;
		case ActionEventConstant.ACTION_LOAD_CART_LIST:
			if(e.modelData!=null){
				listCart = (ArrayList<CartItemData>) e.modelData;
				preProcessProduct();
			}
			lauchActivity();
			break;
		}
	}
	
	void preProcessProduct(){
		for(CartItemData item : listCart){
			for(int i = 0,size = listProduct.size();i<size;i++){
				if(item.productId ==  listProduct.get(i).productId){
					listProduct.get(i).isAddCart = true;
					listProduct.get(i).quantity = item.quantity;
					break;
				}
			}
		}
	}
	
	@Override
	public void handleErrorModelViewEvent(ModelEvent modelEvent) {
		// TODO Auto-generated method stub
		super.handleErrorModelViewEvent(modelEvent);
		finish();
	}
	
	void loadBanners(){
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_LOAD_BANNER;
		UserController.getInstance().handleViewEvent(e);
	}
	
	void loadAllCategory(){
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_LOAD_ALL_CATEGORY;
		UserController.getInstance().handleViewEvent(e);
	}
	
	void loadAllProduct(){
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_LOAD_PRODUCT_LIST;
		UserController.getInstance().handleViewEvent(e);
	}
	
	void loadCart(){
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_LOAD_CART_LIST;
		UserController.getInstance().handleViewEvent(e);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
