package com.android.gs.model;

import java.io.Serializable;

import org.json.JSONException;

import android.os.AsyncTask;

import com.android.gs.activity.BaseActivity;
import com.android.gs.controller.UserController;
import com.android.gs.dto.CategoryData;
import com.android.gs.dto.DistributorDTO;
import com.android.gs.dto.PaymentDTO;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.global.ErrorConstants;
import com.android.gs.global.ModelEvent;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.MyLog;
import com.lib.network.HTTPMessage;
import com.lib.network.HTTPRequest;
import com.lib.network.HTTPResponse;

/**
 * User's model
 * 
 * @author: DoanDM
 * @version: 1.1
 * @since: 1.0
 */
public class UserModel extends AbstractModelService implements Serializable {
	protected static UserModel instance;

	protected UserModel() {
	}

	@Override
	public void onReceiveData(ModelEvent mes) {

		UserController.getInstance().handleModelEvent(mes);
	}

	@Override
	public void onReceiveError(ModelEvent mes) {

		UserController.getInstance().handleErrorModelEvent(mes);
	}

	@Override
	public void onReceiveData(HTTPMessage mes) {
		// TODO Auto-generated method stub
		ActionEvent actionEvent = (ActionEvent) mes.getUserData();
		ModelEvent model = new ModelEvent();
		model.setDataText(mes.getDataText());
		model.setCode(mes.getCode());
		model.setParams(((HTTPResponse) mes).getRequest().getDataText());
		try {
			switch (mes.getAction()) {
			case ActionEventConstant.ACTION_REQUEST_LOGIN:
				break;
			case ActionEventConstant.ACTION_LOAD_BANNER:
				// prase to get list banners
				actionEvent.modelData = DistributorDTO.parseList(mes
						.getDataText());
				break;
			case ActionEventConstant.ACTION_LOAD_ALL_CATEGORY:
				// prase to get list banners
				actionEvent.modelData = CategoryData.parseList(mes
						.getDataText());
				break;
			case ActionEventConstant.ACTION_LOAD_PRODUCT_LIST:
				MyLog.d(getClass().getSimpleName(), "******************** START PARSE LIST PRODUCT *************************");
				actionEvent.modelData = ProductData.parseList(mes
						.getDataText());
				MyLog.d(getClass().getSimpleName(), "******************** END PARSE LIST PRODUCT *************************");
				break;
			case ActionEventConstant.ACTION_REQUEST_PAYMENT:
				PaymentDTO pm = (PaymentDTO) actionEvent.viewData;
				pm.setMessage(mes.getDataText());
				actionEvent.modelData = pm;
				break;
			}
		} catch (JSONException e) {
			model.setCode(ErrorConstants.ERROR_COMMON);
			model.setActionEvent(actionEvent);
			UserController.getInstance().handleErrorModelEvent(model);
			return;
		}
		model.setActionEvent(actionEvent);
		UserController.getInstance().handleModelEvent(model);
	}

	@Override
	public void onReceiveError(HTTPResponse response) {
		// TODO Auto-generated method stub
		ActionEvent actionEvent = (ActionEvent) response.getUserData();
		ModelEvent model = new ModelEvent();
		model.setCode(response.getCode());
		model.setParams(((HTTPResponse) response).getRequest().getDataText());
		model.setActionEvent(actionEvent);
		UserController.getInstance().handleErrorModelEvent(model);
	}

	public static UserModel getInstance() {
		if (instance == null) {
			instance = new UserModel();
		}
		return instance;
	}

	/**
	 * request login
	 * 
	 * @param e
	 */
	public HTTPRequest requestLogin(ActionEvent e) {
		// for testing, return ok
		// ModelEvent me = new ModelEvent();
		// me.setActionEvent(e);
		// me.setModelCode(ErrorConstants.SUCCESS);
		// me.getActionEvent().observe.onReceiveData(me);
		// end

		HTTPRequest re = null;
		try {
			re = sendHttpRequest(HTTPRequest.GET,
					"getconfig?v=1&test=khoisang", e);
		} catch (Throwable throwable) {
			ModelEvent modelEvent = new ModelEvent();
			modelEvent.setActionEvent(e);
			modelEvent.setCode(ErrorConstants.ERROR_COMMON);
			modelEvent.setModelMessage("requestLogin - "
					+ throwable.getMessage() + "/" + throwable.toString());
			UserController.getInstance().handleErrorModelEvent(modelEvent);

		}
		return re;
	}

	/**
	 * request get banners
	 * 
	 * @param e
	 */
	public HTTPRequest requestLoadBanners(ActionEvent e) {
		// for testing, return ok
		// end

		HTTPRequest re = null;
		try {
			re = sendHttpRequest(HTTPRequest.GET, "getbanner?test=khoisang", e);
		} catch (Throwable throwable) {
			ModelEvent modelEvent = new ModelEvent();
			modelEvent.setActionEvent(e);
			modelEvent.setCode(ErrorConstants.ERROR_COMMON);
			modelEvent.setModelMessage("requestLogin - "
					+ throwable.getMessage() + "/" + throwable.toString());
			UserController.getInstance().handleErrorModelEvent(modelEvent);

		}
		return re;
	}

	/**
	 * request get categories
	 * 
	 * @param e
	 */
	public HTTPRequest requestLoadCategories(ActionEvent e) {
		// for testing, return ok
		// end

		HTTPRequest re = null;
		try {
			re = sendHttpRequest(HTTPRequest.GET,
					"getcategories?test=khoisang", e);
		} catch (Throwable throwable) {
			ModelEvent modelEvent = new ModelEvent();
			modelEvent.setActionEvent(e);
			modelEvent.setCode(ErrorConstants.ERROR_COMMON);
			modelEvent.setModelMessage("requestLogin - "
					+ throwable.getMessage() + "/" + throwable.toString());
			UserController.getInstance().handleErrorModelEvent(modelEvent);

		}
		return re;
	}
	
	/**
	 * load list of product
	 * 
	 * @param e
	 */
	public HTTPRequest requestLoadProductList(ActionEvent e) {
		HTTPRequest re = null;
		try {
			re = sendHttpRequest(HTTPRequest.GET,
					"getproducts?test=khoisang", e);
		} catch (Throwable throwable) {
			ModelEvent modelEvent = new ModelEvent();
			modelEvent.setActionEvent(e);
			modelEvent.setCode(ErrorConstants.ERROR_COMMON);
			modelEvent.setModelMessage("requestLogin - "
					+ throwable.getMessage() + "/" + throwable.toString());
			UserController.getInstance().handleErrorModelEvent(modelEvent);

		}
		return re;
	}
	
	
	/**
	 * request payment
	 * 
	 * @param e
	 */
	public HTTPRequest requestPayment(ActionEvent e) {
		HTTPRequest re = null;
		try {
			PaymentDTO dto = (PaymentDTO) e.viewData;
			re = sendHttpRequest(HTTPRequest.GET,
					"order?test=khoisang&"+dto.generateParams(), e);
		} catch (Throwable throwable) {
			ModelEvent modelEvent = new ModelEvent();
			modelEvent.setActionEvent(e);
			modelEvent.setCode(ErrorConstants.ERROR_COMMON);
			modelEvent.setModelMessage("requestLogin - "
					+ throwable.getMessage() + "/" + throwable.toString());
			UserController.getInstance().handleErrorModelEvent(modelEvent);

		}
		return re;
	}

	public void requestLoadCartList(ActionEvent e) {
		// // end
		e.userData = this;
		new LoadCartList().execute(e);
	}
	
	class LoadCartList extends AsyncTask<ActionEvent, Void, Void>{

		@Override
		protected Void doInBackground(ActionEvent... params) {
			// TODO Auto-generated method stub
			ActionEvent e = params[0];
			ModelEvent modelEvent = new ModelEvent();
			//load data from file
			e.modelData = GlobalUtil.readObject(BaseActivity.LIST_CART_KEY);
			//
			modelEvent.setActionEvent(e);
			modelEvent.setCode(ErrorConstants.SUCCESS);
			((UserModel)e.userData).onReceiveData(modelEvent);
			return null;
		}
		
	}

}
