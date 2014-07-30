package com.android.gs.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.gs.utils.MyLog;

public class CategoryData implements Serializable{
	public int id;
	public int rank;
	public String name;
	public String nameNoneUTF;
	public ArrayList<CategoryDTO> listSubCategory = new ArrayList<CategoryDTO>();
	
	public CategoryData(JSONObject json) throws JSONException{
		id = json.getInt("id");
		rank = json.getInt("stt");
		name = json.getString("ten");
		nameNoneUTF = json.getString("tenkhongdau");
		JSONArray jArray = json.getJSONArray("product_cat");
		for(int i = 0;i<jArray.length();i++){
			listSubCategory.add(new CategoryDTO(jArray.getJSONObject(i)));
		}
	}
	public static ArrayList<CategoryData> parseList(String jsonText) throws JSONException{
		MyLog.d("CATEGORY DATA", jsonText);
		ArrayList<CategoryData> result = new ArrayList<CategoryData>();
		JSONObject json = new JSONObject(jsonText);
		JSONArray jArray = json.getJSONArray("data");
		for(int i = 0;i<jArray.length();i++){
			result.add(new CategoryData(jArray.getJSONObject(i)));
		}
		
		return result;
	}
}
