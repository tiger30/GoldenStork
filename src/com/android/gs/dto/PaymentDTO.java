package com.android.gs.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.gs.constant.MyConstants;

public class PaymentDTO implements Serializable {

	public int paymentMethod;// 1 : cash; 2 : tranfer
	public String name;
	public String email;
	public String mobile;
	public String content;
	public ArrayList<ProductData> listData;
	String message;

	public PaymentDTO(int method, String name, String email, String mobile,
			String content, ArrayList<ProductData> listData) {
		this.paymentMethod = method;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.content = content;
		this.listData = listData;
	}

	public String generateParams() {
		String params = MyConstants.STR_BLANK;
		params += "fullname=" + name + "&email=" + email + "&mobile=" + mobile
				+ "&content=" + content + "&payment_method=" + paymentMethod;
		for (ProductData data : listData) {
			params += "&p[id]=" + data.productId + "&p[qty]=" + data.quantity;
		}
		return params;
	}
	
	public void setMessage(String json) throws JSONException{
		JSONObject obj = new JSONObject(json);
		message = obj.getString("message");
	}
	
	public String getMessage(){
		return message;
	}
}
