
package com.android.gs.listener;

import android.view.View;

/**
 *  Interface xu ly xu kien khi show dialog
 *  @author: TruongHN
 *  @version: 1.2
 *  @since: 1.1
 */
public interface OnDialogControlListener {
	// Cac loai event tu dinh nghia
	public static final int EVENT_TYPE_OTHER = -1;
	// Xu ly cac TH trong ErrorConstants --> loi do MS tra ve
	public static final int EVENT_TYPE_ERROR_CONSTANTS = 0;
	// Xu ly cac TH trong BankCode --> loi do Bank Plus tra ve
	public static final int EVENT_TYPE_BANK_CODE = 1;
	void onDialogListener(int eventType, int eventCode, Object data);
}
