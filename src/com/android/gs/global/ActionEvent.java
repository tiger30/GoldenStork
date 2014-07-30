
package com.android.gs.global;

import java.io.Serializable;

import com.android.gs.controller.AbstractController;
import com.lib.network.HTTPRequest;



/**
 *  create action for interaction
 *  @author: DoanDM
 *  @version: 1.0
 *  @since: 1.0
 */
public class ActionEvent implements Serializable{
	 public int action;
	 public Object modelData;
	 public Object viewData;
	 public Object userData;
	 public Object sender;
	 public int tag = 0;
	 
	 //AnhND: xu ly relogin do mat session
	 public AbstractController controller;
	 //AnhND: xuy ly cancel request
	 public HTTPRequest request;
	 //AnhND: request co block (show progressDialog)
	 public boolean isBlockRequest;
//	 private HttpUpdateListener updateListener;
	 public boolean isRelogin = false; // bien dung de kiem tra chi relogin 1 lan
	 public void reset(){
		 action = 0;
		 modelData = null;
		 viewData = null;
		 userData = null;
		 sender = null;
	 }
}
