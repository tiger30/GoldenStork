package com.android.gs.utils.ui;

import java.util.ArrayList;

import com.anroid.gs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
/**
 * adapter use for Gallery
 * @author doandm
 *
 */
public class GalleryImageAdapter extends ArrayAdapter<String> {
	ArrayList<String> dataList;
	public GalleryImageAdapter(Context context,ArrayList<String> dataList) {
		super(context, 0, dataList);
		this.dataList = dataList;
	}
	
	public ArrayList<String> getList(){
		return this.dataList;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		GalleryItem cell = null;
		if (row == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = vi.inflate(R.layout.simple_gallery_item, parent, false);
			cell = new GalleryItem( row);
			row.setTag(cell);
		} else {
			cell = (GalleryItem) row.getTag();
		}
		cell.renderLayout(getItem(position));
		return row;
	}
	
	class GalleryItem{
		ImageView imageView;
		GalleryItem(View v){
			imageView = (ImageView) v.findViewById(R.id.imageView);
		}
		
		void renderLayout(String url){
			imageView.setTag(url);
		}
	}
	
}