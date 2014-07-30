package com.android.gs.model;

import java.io.Serializable;
import java.util.Vector;

import com.android.gs.constant.MyConstants;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ModelEvent;
import com.lib.network.HTTPClient;
import com.lib.network.HTTPListenner;
import com.lib.network.HTTPMessage;
import com.lib.network.HTTPRequest;
import com.lib.network.HttpAsyncTask;
import com.lib.network.NetworkUtil;

public abstract class AbstractModelService implements Serializable, HTTPListenner{
	public void onReceiveError(ModelEvent response) {

	}

	public void onReceiveData(ModelEvent mes) {

	}
	

	/**
	*  Request text
	*  @author: HieuNH
	*  @param method
	*  @param data
	*  @param actionEvent
	*  @return: void
	*  @throws:
	 */
	protected HTTPRequest sendHttpRequest(String method,String params,ActionEvent actionEvent) {
		HTTPRequest re = new HTTPRequest();
		if(HTTPRequest.GET.equals(method)){
			re.setUrl(MyConstants.SERVER_PATH+MyConstants.API_PATH+params);		
		}else{
			re.setUrl(MyConstants.SERVER_PATH);
			re.setDataText(params);
		}
		re.setAction(actionEvent.action);
		re.setContentType(HTTPMessage.CONTENT_JSON);
		re.setMethod(method);
		re.setObserver(this);
		re.setUserData(actionEvent);
		new HttpAsyncTask(re).execute();
		return re;
	}
}
