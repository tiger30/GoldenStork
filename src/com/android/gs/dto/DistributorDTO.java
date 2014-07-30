package com.android.gs.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.gs.utils.MyLog;

public class DistributorDTO implements Serializable{
	public int id;
	public String title;
	public int rank;
	public int status;
	public String content;
	public String image;
	public int categoryId;
	public DistributorDTO(JSONObject obj) throws JSONException{
		id = obj.getInt("id");
		title = obj.getString("title");
		rank = obj.getInt("rank");
		status = obj.getInt("status");
		content = obj.getString("content");
		image = obj.getString("image");
		categoryId = obj.getInt("cate_id");
	}
	public static ArrayList<DistributorDTO> parseList(String jsonText) throws JSONException{
		MyLog.e("DistributorDTO", jsonText);
		ArrayList<DistributorDTO> result = new ArrayList<DistributorDTO>();
		JSONObject json = new JSONObject(jsonText);
		JSONArray jArray = json.getJSONArray("data");
		for(int i = 0;i<jArray.length();i++){
			result.add(new DistributorDTO(jArray.getJSONObject(i)));
		}
		
		return result;
	}
}
