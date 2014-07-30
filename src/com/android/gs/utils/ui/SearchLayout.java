package com.android.gs.utils.ui;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.android.gs.constant.MyConstants;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.StringUtil;
import com.anroid.gs.R;

public class SearchLayout extends RelativeLayout implements OnClickListener {
	public RelativeLayout rlSearch;
	ImageButton ivSearch;// image of seach in search filtering
	EditText edSearch;// search filtering text box
	protected InputMethodManager imm;
	OnEventControlListener listener;

	public SearchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public SearchLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	void init(Context context) {
		listener = (OnEventControlListener) context;
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.search_layout, this);
		rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
		imm = (InputMethodManager) getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		edSearch = (EditText) findViewById(R.id.edSearch);
		ivSearch = (ImageButton) findViewById(R.id.ivSearch);
		edSearch.setTypeface(StringUtil.getFont(MyConstants.MYRIADPRO_IT));
		
		ivSearch.setOnClickListener(this);
		ivSearch.setTag(0);
	}

	public void hideKeyboard() {
		imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
	}

	public void addTextChangedListener(TextWatcher tw) {
		edSearch.addTextChangedListener(tw);
	}

	public void clearText() {
		edSearch.setText(MyConstants.STR_BLANK);
	}

	public void setClearVisibility(boolean isClear) {
		if (isClear) {
			// ivSearch.setImageResource(R.drawable.close_icon);
			ivSearch.setTag(1);
			ivSearch.setPadding(
					(int) getContext().getResources().getDimension(
							R.dimen.standard_wide_padding) * 2,
					(int) getContext().getResources().getDimension(
							R.dimen.standard_wide_padding),
					(int) getContext().getResources().getDimension(
							R.dimen.standard_wide_padding) * 2,
					(int) getContext().getResources().getDimension(
							R.dimen.standard_wide_padding));
		} else {
			ivSearch.setTag(0);
			// ivSearch.setImageResource(R.drawable.search_icon);
			ivSearch.setPadding(
					(int) getContext().getResources().getDimension(
							R.dimen.standard_padding),
					(int) getContext().getResources().getDimension(
							R.dimen.small_padding),
					(int) getContext().getResources().getDimension(
							R.dimen.standard_padding), (int) getContext()
							.getResources().getDimension(R.dimen.small_padding));
		}
	}

	public void setSearchType(boolean isSearchRealtime) {
		if (isSearchRealtime) {
			edSearch.setText(edSearch.getText().toString());
		} else {
			ivSearch.setTag(0);
			// ivSearch.setImageResource(R.drawable.search_icon);
		}
	}

	public String getText() {
		return edSearch.getText().toString();
	}

	public void setText(String text) {
		edSearch.setText(text);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		listener.onEvent(OnEventControlListener.ON_SEARCH_CLICK, v, getText());
	}

	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		edSearch.requestFocus();
		super.setVisibility(visibility);
	}

}
