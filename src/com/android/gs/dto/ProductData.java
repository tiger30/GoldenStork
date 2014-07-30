package com.android.gs.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.gs.constant.MyConstants;
import com.android.gs.utils.MyLog;
import com.android.gs.utils.StringUtil;
import com.anroid.gs.R;

public class ProductData implements Serializable {
	public int productId;
	public int catChild;
	public int catParent;
	public ArrayList<String> imageList;
	public String imageThumb;
	public String imageDetail;
	public String name;
	public String nameNoneUTF;
	public String description;
	boolean hasDiscount = false;
	long oldPrice;
	long newPrice;
	// String need auto generate
	String strOldPrice;

	String strNewPrice;
	double discountPercent;
	String discountPercentString;
	// use for cart
	public int quantity = 0;
	public boolean isAddCart = false;

	public ProductData(JSONObject json) throws JSONException {
		MyLog.i("PRODUCT LIST PARSE", json.toString());
		oldPrice = json.getLong("gia1");
		newPrice = json.getLong("gia2");
		productId = json.getInt("id");
		catChild = json.getInt("id_cat_child");
		catParent = json.getInt("id_cat_parent");
		imageDetail = json.getString("image");
		imageThumb = imageDetail;
		description = json.getString("mota");
		name = json.getString("ten");
		nameNoneUTF = json.getString("tenkhongdau");
		hasDiscount = newPrice < oldPrice;
		imageList = new ArrayList<String>();
		if (json.has("image1")) {
			imageList.add(json.getString("image1"));
		}
		if (json.has("image2")) {
			imageList.add(json.getString("image2"));
		}
		if (json.has("image3")) {
			imageList.add(json.getString("image3"));
		}
		generateData();
	}

	public static ArrayList<ProductData> parseList(String str)
			throws JSONException {
		MyLog.i("PRODUCT LIST", str);
		ArrayList<ProductData> result = new ArrayList<ProductData>();
		JSONObject json = new JSONObject(str);
		JSONArray jArray = json.getJSONArray("data");
		for (int i = 0; i < jArray.length(); i++) {
			result.add(new ProductData(jArray.getJSONObject(i)));
		}

		return result;
	}

	void generateData() {
		if (hasDiscount) {
			discountPercent = (oldPrice - newPrice) * 100 / oldPrice;
			discountPercent = (Math.round(discountPercent * 10.0) / 10.0);
			discountPercentString = discountPercent + MyConstants.STR_PERCENT;
		}
		strNewPrice = StringUtil.apendString2String(String.valueOf(newPrice),
				MyConstants.STR_DOT, 3, String.valueOf(newPrice).length())
				+ MyConstants.STR_SPACE + StringUtil.getString(R.string.vnd);
		strOldPrice = StringUtil.apendString2String(String.valueOf(oldPrice),
				MyConstants.STR_DOT, 3, String.valueOf(oldPrice).length())
				+ MyConstants.STR_SPACE + StringUtil.getString(R.string.vnd);
	}

	public long getNewPrice() {
		return newPrice;
	}

	public String getStrOldPrice() {
		return strOldPrice;
	}

	public String getStrNewPrice() {
		return strNewPrice;
	}

	public double getDiscountPercent() {
		return discountPercent;
	}

	public String getDiscountPercentString() {
		return discountPercentString;
	}

	public boolean hasDiscount() {
		return hasDiscount;
	}
}
