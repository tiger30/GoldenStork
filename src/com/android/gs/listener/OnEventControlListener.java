
package com.android.gs.listener;

import android.view.View;

/**
 * @author: DoanDM
 * @since : May 21, 2011
 * 
 */
public interface OnEventControlListener {
	int MENU_ITEM_DLG_CLICK = 0;
	int CHECK_BOX_CLICK = 1;
	int CONFIRM_YES = 2;
	int CONFIRM_NO = 3;
	int ON_SEARCH_CLICK = 4;
	int ON_SLIDE_MENU_ITEM_CLICK = 5;
	int ON_LOAD_PRODUCT_LIST = 6;
	int GO_TO_HOME_PAGE = 7;
	int VIEW_PRODUCT_DETAIL = 8;
	int GO_TO_CART_ACTIVITY = 9;
	int REMOVE_CART_ITEM = 10;
	int ON_CHANGE_QUANTITY = 11;
	int ON_CHANGE_FRAGMENT_ATTACH = 12;
	int ON_SELECT_PRODUCT_ITEM = 13;
	int ON_SELECT_CAT = 14;
	void onEvent( int eventType, View control, Object data);
	void onSoftKeyboardShown(boolean isShowing,int diff);
}
