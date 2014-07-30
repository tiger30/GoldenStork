package com.android.gs.components.slidemenu;


import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.gs.activity.MainActivity;
import com.android.gs.constant.MyConstants;
import com.android.gs.dto.CategoryDTO;
import com.android.gs.dto.CategoryData;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.listener.OnSlideMenuChangeState;
import com.android.gs.utils.ui.ProductItem;
import com.android.gs.utils.ui.SearchLayout;
import com.anroid.gs.R;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailAdapter;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;

public class ListFragment extends Fragment implements 
		OnItemClickListener,OnSlideMenuChangeState {
	static final int NUMBER_ITEM_LOAD = 10;
	View view;
	
	ListView lvProducts;
	OnEventControlListener listener;
	LinearLayout llMainContent;// main layout
	private static final int[] IMAGE_IDS = { R.id.ivProduct};

	
	public MainLayout mLayout;
	ArrayList<ProductData> listData;
	ArrayList<ProductData> listSearchData ;
	CategoryData category;
	CategoryDTO categoryChild;
	
	TextView tvNoItem;

	int mode = 0;
	private ThumbnailAdapter thumbs = null;
	
	MainCategoryLayout mainCat;

	public ListFragment() {
		// avoid crash when changing language
	}

	public ListFragment(OnEventControlListener parent, MainLayout mLayout,
			Bundle bun) {
		listener = parent;
		this.mLayout = mLayout;
		this.mLayout.setChildListener(this);
//		category = (CategoryData) bun.getSerializable(MyConstants.DATA_KEY);
	}

	/**
	 * set category information
	 * 
	 * @param category
	 */
	public void setCategoryData(Bundle bundle) {
		this.category = (CategoryData) bundle.getSerializable(MyConstants.DATA_KEY);
		this.categoryChild = (CategoryDTO) bundle.getSerializable(MyConstants.DATA_KEY2);
		
		listener.onEvent(OnEventControlListener.ON_LOAD_PRODUCT_LIST, null,
				bundle);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_list_layout, container,
					false);
			init();
			
		}

		return view;
	}
	

	/**
	 * init layout
	 * 
	 * @param v
	 *            DoanDM Feb 19, 2014
	 */
	private void init() {
		if (listener == null || view == null)
			return;
		tvNoItem = (TextView) view.findViewById(R.id.tvNoItem);
		// list view
		lvProducts = (ListView) view.findViewById(R.id.lvProducts);
		lvProducts.setOnItemClickListener(this);
		listData = new ArrayList<ProductData>();
		listSearchData = new ArrayList<ProductData>();
		
		

//		listener.onEvent(OnEventControlListener.ON_LOAD_PRODUCT_LIST, null,
//				category);
		listener.onEvent(OnEventControlListener.ON_CHANGE_FRAGMENT_ATTACH,
				null, null);
		
		
		mainCat = new MainCategoryLayout((MainActivity)listener, view);
		
	}

	boolean isSearchMode = false;
	
	
	public void checkToogle(SearchLayout mainSearch,TextView title,ImageButton ivIcon){
		if (isSearchMode) {
			setSearchMode(mainSearch,title,ivIcon,false);
		} else {
			mLayout.toggleMenu();
		}
	}

	public void setSearchMode(SearchLayout mainSearch,TextView title,ImageButton ivIcon,boolean isSearch) {
		isSearchMode = isSearch;
		if (isSearch) {
			mainSearch.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			ivIcon.setImageResource(R.drawable.back_icon);
			lvProducts.setVisibility(View.VISIBLE);
			mainCat.setVisibility(View.GONE);
		} else {
			mainSearch.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			ivIcon.setImageResource(R.drawable.ic_drawer);
			try {
				ThumbnailBus bus = new ThumbnailBus();
				thumbs = new ThumbnailAdapter(
					getActivity(),
						new ProductAdapter(listData),
						new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(
								null, null, 101, bus), IMAGE_IDS);
				thumbs.setRetryLoadImage(true);
			} catch (Exception e1) {

			}
			lvProducts.setAdapter(thumbs);
			if (listData.size() > 0) {
				tvNoItem.setVisibility(View.GONE);
				lvProducts.setVisibility(View.VISIBLE);
			} else {
				tvNoItem.setVisibility(View.VISIBLE);
				lvProducts.setVisibility(View.GONE);
			}
			swithMode(mode);
		}
	}

	public boolean isSearchMode() {
		return isSearchMode;
	}

	OnClickListener cartListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			listener.onEvent(OnEventControlListener.GO_TO_CART_ACTIVITY, null,
					null);
		}
	};

	/**
	 * Product Adapter
	 * 
	 * @author DoanDM
	 * 
	 */
	public class ProductAdapter extends ArrayAdapter<ProductData> {

		ProductAdapter(ArrayList<ProductData> list) {
			super((Context) listener, R.layout.product_item_layout, list);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ProductItem cell = null;
			if (row == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = vi.inflate(R.layout.product_item_layout, parent, false);
				cell = new ProductItem(getContext(), row);
				row.setTag(cell);
			} else {
				cell = (ProductItem) row.getTag();
			}
			cell.renderLayout(getItem(position));
			return row;
		}
	}

	/**
	 * reset list data
	 */
	public void resetList() {
		listData = new ArrayList<ProductData>();
		listSearchData = new ArrayList<ProductData>();
	}

	/**
	 * update list data
	 */
	@SuppressWarnings("unchecked")
	public void updateList(Object obj) {
		ArrayList<ProductData> arr = (ArrayList<ProductData>) obj;
		if(isSearchMode){
			listSearchData = arr;
			try {
				ThumbnailBus bus = new ThumbnailBus();
				thumbs = new ThumbnailAdapter(
					getActivity(),
						new ProductAdapter(listSearchData),
						new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(
								null, null, 101, bus), IMAGE_IDS);
				thumbs.setRetryLoadImage(true);
			} catch (Exception e1) {

			}
			lvProducts.setAdapter(thumbs);
			if (listSearchData.size() > 0) {
				tvNoItem.setVisibility(View.GONE);
				lvProducts.setVisibility(View.VISIBLE);
			} else {
				tvNoItem.setVisibility(View.VISIBLE);
				lvProducts.setVisibility(View.GONE);
			}
			
		}else{
			listData = arr;
			try {
				ThumbnailBus bus = new ThumbnailBus();
				thumbs = new ThumbnailAdapter(
					getActivity(),
						new ProductAdapter(listData),
						new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(
								null, null, 101, bus), IMAGE_IDS);
				thumbs.setRetryLoadImage(true);
			} catch (Exception e1) {

			}
			lvProducts.setAdapter(thumbs);
			
			if (listData.size() > 0) {
				tvNoItem.setVisibility(View.GONE);
				lvProducts.setVisibility(View.VISIBLE);
			} else {
				tvNoItem.setVisibility(View.VISIBLE);
				lvProducts.setVisibility(View.GONE);
			}
		}
		
	}
	
	public void swithMode(int mode){
		this.mode = mode;
		if(mode == ActionEventConstant.ACTION_SWITH_TO_LIST_PRODUCT_SCREEN){
			lvProducts.setVisibility(View.VISIBLE);
			mainCat.setVisibility(View.GONE);
		}else{
			lvProducts.setVisibility(View.GONE);
			mainCat.setVisibility(View.VISIBLE);
		}
	}
	
	public boolean isListProductShown(){
		return mainCat.getVisibility() != View.VISIBLE;
	}


	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		listener.onEvent(OnEventControlListener.VIEW_PRODUCT_DETAIL, null,
				adapter.getItemAtPosition(position));
	}

	@Override
	public void onStateChange(boolean isMenuOpened) {
		// TODO Auto-generated method stub
	}

}
