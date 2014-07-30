package com.android.gs.controller;

/**
 *  Controller User's interaction
 *  @author: DoanDM
 *  @version: 1.1
 *  @since: 1.0
 */
import android.content.Intent;
import android.os.Bundle;

import com.android.gs.activity.BaseActivity;
import com.android.gs.activity.CartListActivity;
import com.android.gs.activity.MainActivity;
import com.android.gs.activity.PaymentActivity;
import com.android.gs.activity.ProductDetailActivity;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.global.ErrorConstants;
import com.android.gs.global.ModelEvent;
import com.android.gs.model.UserModel;
import com.lib.network.HTTPRequest;

public class UserController extends AbstractController {

	static UserController instance;

	protected UserController() {
	}

	public static UserController getInstance() {
		if (instance == null) {
			instance = new UserController();
		}
		return instance;
	}

	/**
	 * 
	 * handle request from view
	 * 
	 * @author: DoanDM
	 * @param e
	 * @return: void
	 * @throws:
	 */
	public void handleViewEvent(ActionEvent e) {
		HTTPRequest request = null;
		// boolean isBlockRequest = false;
		boolean isProcessRequest = true;
		BaseActivity baseActivity = (BaseActivity) e.sender;
		if (baseActivity != null && e.isBlockRequest) {
			if (baseActivity.checkExistRequestProcessing(e.action)) {
				isProcessRequest = false;
			}
		}
		if (isProcessRequest) {
			switch (e.action) {
			case ActionEventConstant.ACTION_REQUEST_LOGIN:
				request = UserModel.getInstance().requestLogin(e);
				break;
			case ActionEventConstant.ACTION_LOAD_CART_LIST:
				UserModel.getInstance().requestLoadCartList(e);
				break;
			case ActionEventConstant.ACTION_LOAD_BANNER:
				request = UserModel.getInstance().requestLoadBanners(e);
				break;
			case ActionEventConstant.ACTION_LOAD_ALL_CATEGORY:
				request = UserModel.getInstance().requestLoadCategories(e);
				break;
			case ActionEventConstant.ACTION_LOAD_PRODUCT_LIST:
				request = UserModel.getInstance().requestLoadProductList(e);
				break;
			case ActionEventConstant.ACTION_REQUEST_PAYMENT:
				request = UserModel.getInstance().requestPayment(e);
				break;
			default:// test

				break;
			}
			// add request to view to cancel
			if (request != null && baseActivity != null) {
				baseActivity.addProcessingRequest(request, e.isBlockRequest);
				// e.isBlockRequest = isBlockRequest;
				e.request = request;
			}
		}
	}

	/**
	 * 
	 * handle data from model return
	 * 
	 * @author: DoanDM
	 * @param modelEvent
	 * @return: void
	 * @throws:
	 */
	public void handleModelEvent(final ModelEvent modelEvent) {
		if (modelEvent.getCode() == ErrorConstants.SUCCESS) {
			final ActionEvent e = modelEvent.getActionEvent();
			if (e.sender != null) {
				final BaseActivity sender = (BaseActivity) e.sender;
				sender.runOnUiThread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						sender.handleModelViewEvent(modelEvent);
					}
				});
			}
		} else {
			handleErrorModelEvent(modelEvent);
		}

	}

	@Override
	public void handleSwitchActivity(ActionEvent e) {
		Intent intent;
		Bundle extras;
		BaseActivity sender;

		switch (e.action) {
		case ActionEventConstant.ACTION_SWITH_TO_CATEGORY_SCREEN:
			sender = (BaseActivity) e.sender;
			extras = (Bundle) e.viewData;
			intent = new Intent(sender, MainActivity.class);
			if (extras != null) {
				intent.putExtras(extras);
			}
			sender.startActivity(intent);
			break;
		// case ActionEventConstant.ACTION_SWITH_TO_LIST_PRODUCT_SCREEN:
		// sender = (BaseActivity) e.sender;
		// extras = (Bundle) e.viewData;
		// intent = new Intent(sender, ProductListLayout.class);
		// if (extras != null) {
		// intent.putExtras(extras);
		// }
		// sender.startActivity(intent);
		// break;
		case ActionEventConstant.ACTION_SWITH_TO_CART_SCREEN:
			sender = (BaseActivity) e.sender;
			extras = (Bundle) e.viewData;
			intent = new Intent(sender, CartListActivity.class);
			if (extras != null) {
				intent.putExtras(extras);
			}
			sender.startActivity(intent);
			break;
		case ActionEventConstant.ACTION_VIEW_PRODUCT_DETAIL:
			sender = (BaseActivity) e.sender;
			extras = (Bundle) e.viewData;
			intent = new Intent(sender, ProductDetailActivity.class);
			if (extras != null) {
				intent.putExtras(extras);
			}
			sender.startActivity(intent);
			break;
		case ActionEventConstant.ACTION_SWITH_TO_VIEW_PAYMENT:
			sender = (BaseActivity) e.sender;
			extras = (Bundle) e.viewData;
			intent = new Intent(sender, PaymentActivity.class);
			if (extras != null) {
				intent.putExtras(extras);
			}
			sender.startActivityForResult(intent,
					CartListActivity.ORDERING_REQUEST);
			break;

		}

	}

}
