package com.android.gs.dto;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.gs.utils.MyLog;

public class CategoryDTO implements Serializable{
	public int id;
	public String photo;
	public int rank;
	public String name;
	public String nameNonUTF;
	public String thumb;
	public CategoryDTO(){
		
	}
	public CategoryDTO(JSONObject json) throws JSONException{
		MyLog.i("CategoryDTO", json.toString());
		id = json.getInt("id");
		photo = json.getString("photo");
		rank = json.getInt("stt");
		name = json.getString("ten");
		nameNonUTF = json.getString("tenkhongdau");
		thumb = json.getString("thumb");
	}
}
