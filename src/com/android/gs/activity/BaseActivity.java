package com.android.gs.activity;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gs.application.MyApplication;
import com.android.gs.constant.MyConstants;
import com.android.gs.controller.UserController;
import com.android.gs.dto.CartItemData;
import com.android.gs.dto.CategoryData;
import com.android.gs.dto.DistributorDTO;
import com.android.gs.dto.ProductData;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ActionEventConstant;
import com.android.gs.global.ErrorConstants;
import com.android.gs.global.ModelEvent;
import com.android.gs.listener.OnEventControlListener;
import com.android.gs.utils.GlobalUtil;
import com.android.gs.utils.MyLog;
import com.android.gs.utils.Setting;
import com.android.gs.utils.StringUtil;
import com.android.gs.utils.ui.LayoutThatDetectsSoftKeyboard;
import com.android.gs.utils.ui.SearchLayout;
import com.anroid.gs.R;
import com.lib.cwac.cache.HashMapManager;
import com.lib.cwac.cache.SimpleWebImageCache;
import com.lib.cwac.thumbnail.ThumbnailBus;
import com.lib.cwac.thumbnail.ThumbnailMessage;
import com.lib.network.HTTPClient;
import com.lib.network.HTTPRequest;

public class BaseActivity extends FragmentActivity implements OnEventControlListener{
	//global data
	public static ArrayList<DistributorDTO> listDistributor = new ArrayList<DistributorDTO>();
	static final String LIST_DISTRIBUTOR_KEY = "LIST_DISTRIBUTOR_KEY";
	
	public static ArrayList<CategoryData> listCategory = new ArrayList<CategoryData>();
	static final String LIST_CATEGORY_KEY = "LIST_CATEGORY_KEY";
	
	public static ArrayList<ProductData> listProduct = new ArrayList<ProductData>();
	static final String LIST_PRODUCT_KEY = "LIST_PRODUCT_KEY";
	
	public static ArrayList<CartItemData> listCart = new ArrayList<CartItemData>();
	public static final String LIST_CART_KEY = "LIST_CART_KEY";
	//end
	static final String LOG_FILE_PATH = "GoldenStorkLog";
	View rootView;
	// dialog processing
	private static ProgressDialog progressDlg;

	private boolean broadcast = false;
	protected InputMethodManager imm;

	private static boolean isStartService = false;
	static boolean isAddException = false;

	public SearchLayout searchLayout;
	public LinearLayout llCart;
	TextView tvCart;
	public boolean isFinished = false;
	
	ThumbnailBus bus;
	public  SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache ;
	// broadcast receiver
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getExtras()
					.getInt(MyConstants.BROADCAST_ACTION);
			int hasCode = intent.getExtras().getInt(
					MyConstants.BROADCAST_HASHCODE);
			if (hasCode != BaseActivity.this.hashCode()) {
				receiveBroadcast(action, intent.getExtras());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initActivity();

		try {
			IntentFilter filter = new IntentFilter(
					MyConstants.BROADCAST_ACTION_PACKAGE);
			registerReceiver(receiver, filter);
			this.broadcast = true;
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * receive broadcast or not
	 * 
	 * @author: DoanDM
	 * @param savedInstanceState
	 * @param broadcast
	 *            : true if need receive broadcast
	 */
	protected void onCreate(Bundle savedInstanceState, boolean broadcast) {
		super.onCreate(savedInstanceState);

		initActivity();

		if (broadcast) {
			try {
				IntentFilter filter = new IntentFilter(
						MyConstants.BROADCAST_ACTION_PACKAGE);
				registerReceiver(receiver, filter);
				this.broadcast = broadcast;
			} catch (Exception e) {
			}
		}

	}
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		llCart = (LinearLayout)findViewById(R.id.llCart);
		rootView = findViewById(R.id.rootView);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(LIST_DISTRIBUTOR_KEY, listDistributor);
		outState.putSerializable(LIST_CATEGORY_KEY, listCategory);
		outState.putSerializable(LIST_PRODUCT_KEY, listProduct);
		outState.putSerializable(LIST_CART_KEY, listCart);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState.containsKey(LIST_DISTRIBUTOR_KEY)){
			listDistributor = (ArrayList<DistributorDTO>) savedInstanceState.getSerializable(LIST_DISTRIBUTOR_KEY);
		}
		if(savedInstanceState.containsKey(LIST_CATEGORY_KEY)){
			listCategory = (ArrayList<CategoryData>) savedInstanceState.getSerializable(LIST_CATEGORY_KEY);
		}
		if(savedInstanceState.containsKey(LIST_PRODUCT_KEY)){
			listProduct = (ArrayList<ProductData>) savedInstanceState.getSerializable(LIST_PRODUCT_KEY);
		}
		if(savedInstanceState.containsKey(LIST_CART_KEY)){
			listCart = (ArrayList<CartItemData>) savedInstanceState.getSerializable(LIST_CART_KEY);
		}
	}

	/**
	 * init common properties of activity
	 * 
	 * DoanDM Mar 13, 2014
	 */
	void initActivity() {
		MyApplication.getInstance().setActivityContext(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		Setting.getInstance().loadSetting(this); 

		// if (!isAddException) {
		// isAddException = true;
		// Thread.setDefaultUncaughtExceptionHandler(new
		// Thread.UncaughtExceptionHandler() {
		// @Override
		// public void uncaughtException(Thread thread, Throwable e) {
		// handleUncaughtException(thread, e);
		// }
		// });
		// }

		imm = (InputMethodManager) getSystemService(
				Context.INPUT_METHOD_SERVICE);
		
		 bus = new ThumbnailBus();
		 cache =  new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(
					null, null, 101, bus);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().setActivityContext(this);
		startService();
		super.onRestart();
	}

	/**
	 * check screen is in searching
	 * 
	 * @return DoanDM Feb 27, 2014
	 */
	public boolean isInSearchMode() {
		if(searchLayout!=null)
		return searchLayout.getVisibility() == View.VISIBLE;
		return false;
	}

	/**
	 * start application service
	 * 
	 * @author DoanDM
	 * @since Jan 3, 2014
	 */
	private void startService() {
		if (!isStartService) {
			isStartService = true;
//			Intent intent = new Intent(this, ContactService.class);
//			startService(intent);
//
//			Intent intent2 = new Intent(this, ClipboardService.class);
//			startService(intent2);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		removeAllProcessingRequest();
		GlobalUtil.nullViewDrawablesRecursive(rootView);
		 HashMapManager.getInstance().clearHashMapById(this.toString());
		System.gc();
		System.runFinalization();

		// Destroy the AdView.

		super.onDestroy();

	}
	
	/**
	 * hide virtual keyboard
	 * @param ed
	 * @author DoanDM
	 * @since Apr 18, 2014
	 */
	protected void hideKeyboard(EditText ed){
		imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
	}

	/**
	 * 
	 * show progress dialog
	 * 
	 * @author: DoanDM
	 * @param content
	 * @param cancleable
	 */
	public void showProgressDialog(String content, boolean cancleable) {
		if (progressDlg != null && progressDlg.isShowing()) {
			closeProgressDialog();
		}
		progressDlg = ProgressDialog.show(this, MyConstants.STR_BLANK, content,
				true, true);
		progressDlg.setCancelable(cancleable);
		// progressDlg.setOnCancelListener(this);
	}

	/**
	 * close progress dialog
	 * 
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void closeProgressDialog() {
		if (progressDlg != null) {
			try {
				progressDlg.dismiss();
				progressDlg = null;
			} catch (Exception e) {
				MyLog.i("Exception", e.toString());
			}
		}
	}

	/**
	 * show progress percentage dialog
	 * 
	 * @param content
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void showProgressPercentDialog(String content) {
		progressDlg = new ProgressDialog(this);
		progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDlg.setMessage(content);
		progressDlg.setCancelable(false);
		progressDlg.show();
	}

	/**
	 * update percentage dialog
	 * 
	 * @param percent
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void updateProgressPercentDialog(int percent) {
		if (progressDlg != null) {
			progressDlg.setProgress(percent);
		}
	}

	/**
	 * close progress percentage dialog
	 * 
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void closeProgressPercentDialog() {
		if (progressDlg != null) {
			progressDlg.dismiss();
			progressDlg = null;
		}
	}

	public void receiveBroadcast(int action, Bundle bundle) {

	}

	/**
	 * show dialog
	 * 
	 * @param mes
	 * @return
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public AlertDialog showDialog(final String mes) {
		AlertDialog alertDialog = null;
		try {
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setMessage(mes);
			alertDialog.setButton(StringUtil.getString(R.string.close),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							return;

						}
					});
			alertDialog.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alertDialog;
	}

	public void handleModelViewEvent(ModelEvent modelEvent) {
		closeProgressDialog();
		ActionEvent e = modelEvent.getActionEvent();
		switch (e.action) {
		default:
			break;
		}
	}
	public void handleErrorModelViewEvent(ModelEvent modelEvent) {
		closeProgressDialog();
		switch (modelEvent.getCode()) {
		case ErrorConstants.ERROR_COMMON:
			// show error here
			Toast.makeText(this, StringUtil.getString(R.string.common_error), Toast.LENGTH_LONG).show();
			break;
		case HTTPClient.ERR_NO_CONNECTION:
			Toast.makeText(this, StringUtil.getString(R.string.err_no_connection), Toast.LENGTH_LONG).show();
			break;
		case HTTPClient.ERR_OUT_OF_MEM:
			Toast.makeText(this, StringUtil.getString(R.string.err_out_of_memory), Toast.LENGTH_LONG).show();
			break;
		case HTTPClient.ERR_NOT_FOUND:
			Toast.makeText(this, StringUtil.getString(R.string.err_not_found), Toast.LENGTH_LONG).show();
			break;
		case HTTPClient.ERR_NO_ACCECSS_INTERNET:
			Toast.makeText(this, StringUtil.getString(R.string.err_no_internet), Toast.LENGTH_LONG).show();
			break;
		case HTTPClient.ERR_INVALID_URL:
			Toast.makeText(this, StringUtil.getString(R.string.err_invalid_url), Toast.LENGTH_LONG).show();
			break;
		case HTTPClient.ERR_TIME_OUT:
			Toast.makeText(this, StringUtil.getString(R.string.err_time_out), Toast.LENGTH_LONG).show();
			break;

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MyApplication.getInstance().setAppActive(false);
		System.gc();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(llCart!=null){
			if(tvCart == null){
				tvCart = (TextView)findViewById(R.id.tvCart);
			}
			if(tvCart != null){
				tvCart.setText(String.valueOf(listCart.size()));
			}
		}
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			MyApplication.getInstance().isDebugMode = ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
		} catch (Exception ex) {
			MyApplication.getInstance().isDebugMode = false;
		}
		MyApplication.getInstance().setAppActive(true);
		startService();
		hiddenSearchKeyboard();

		super.onResume();
	}

	@Override
	public void onEvent(int eventType, View control, Object data) {
		// TODO Auto-generated method stub
		
		hiddenSearchKeyboard();
	}
	
	/**
	 * go to list cart activity
	 */
	void gotoListCart(){
		ActionEvent e = new ActionEvent();
		e.sender = this;
		e.action = ActionEventConstant.ACTION_SWITH_TO_CART_SCREEN;
		UserController.getInstance().handleSwitchActivity(e);
	}

	/**
	 * hide virtual keyboard of search region
	 * 
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void hiddenSearchKeyboard() {
		if (searchLayout != null) {
			searchLayout.hideKeyboard();
		}
	}


	Toast myToast;

	/**
	 * make toast
	 * 
	 * @param str
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	public void showStaticToast(String str) {
		if (myToast == null) {
			myToast = new Toast(getApplicationContext());
		}
		if (myToast.getView() == null) {
			myToast = Toast.makeText(getApplicationContext(), str,
					Toast.LENGTH_SHORT);
		}
		if (!myToast.getView().isShown()) {
			myToast.show();
		}
	}

	/**
	 * set screen full (without phone title)
	 * 
	 * @param on
	 * @author DoanDM
	 * @since Dec 20, 2013
	 */
	protected void setFullscreen(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		if (on) {
			winParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		} else {
			winParams.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		win.setAttributes(winParams);
	}


	@Override
	public void onSoftKeyboardShown(boolean isShowing, int diff) {
		// TODO Auto-generated method stub
	}


	public void handleUncaughtException(Thread thread, Throwable e) {
		writeLogCat();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	void writeLogCat() {
		String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ? "logcat -d -v time BluetoothScanner:v dalvikvm:v System.err:v *:s"
				: "logcat -d -v time";
		Process process;
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder log = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line);
			}
			writeLog(log.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void writeLog(String txtData) {
		String fileName = LOG_FILE_PATH + "_"
				+ StringUtil.getCurrentDate("yyyy/MM/dd").replace("/", "_")
				+ ".txt";
		File myFile = null;
		if (isExternalStorageWritable()) {
			myFile = new File(Environment.getExternalStorageDirectory() + "/"
					+ getResources().getString(R.string.app_name));

		} else {
			myFile = new File(getFilesDir(), getResources().getString(
					R.string.app_name));
		}
		if (myFile != null) {
			try {
				if (!myFile.exists()) {
					myFile.mkdirs();
				}
				myFile = new File(myFile.getAbsolutePath(), fileName);
				myFile.createNewFile();

				FileWriter fileWriter = new FileWriter(myFile, true);

				// Use BufferedWriter instead of FileWriter for better
				// performance
				BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);

				fileWriter.write("*********************"
						+ StringUtil.getCurrentDateTime()
						+ "*********************" + "\r\n" + txtData + "\r\n"
						+ "*********************"
						+ StringUtil.getCurrentDateTime()
						+ "*********************" + "\r\n\r\n");

				// Don't forget to close Streams or Reader to free
				// FileDescriptor associated with it
				bufferFileWriter.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
	
	private ArrayList<HTTPRequest> blockReqs = new ArrayList<HTTPRequest>();
	private ArrayList<HTTPRequest> unblockReqs = new ArrayList<HTTPRequest>();
	
	/**
	*  Kiem tra co ton tai request dang xu ly hay khong
	*  @author: TruongHN
	*  @param reqAction
	*  @return: boolean
	*  @throws:
	 */
	public boolean checkExistRequestProcessing(int reqAction){
		boolean res = false;
		HTTPRequest curReq = null;
		for (int i = 0, n = blockReqs.size(); i < n; i++) {
			curReq = blockReqs.get(i);
			if (curReq.isAlive() && curReq.getAction() == reqAction) {
				res = true;
				break;
			}
		}
		return res;
	}
	

	/**
	 * 
	 * add processing request
	 * 
	 * @author: AnhND
	 * @param req
	 *            , isBlock
	 * @return: void
	 * @throws:
	 */
	public void addProcessingRequest(HTTPRequest req, boolean isBlock) {
		if (isBlock) {
			blockReqs.add(req);
		} else {
			unblockReqs.add(req);
		}
	}

	/**
	 * 
	 * remove all processing request
	 * 
	 * @author: AnhND
	 * @return: void
	 * @throws:
	 */
	public void removeAllProcessingRequest() {
		cancelRequest(blockReqs);
		cancelRequest(unblockReqs);
	}
	
	/**
	 * 
	 * remove processing request
	 * 
	 * @author: AnhND
	 * @param req
	 * @return: void
	 * @throws:
	 */
	public void removeProcessingRequest(HTTPRequest req, boolean isBlock) {
		if (isBlock) {
			cancelRequest(blockReqs, req);
		} else {
			cancelRequest(unblockReqs, req);
		}
	}


	private void cancelRequest(ArrayList<HTTPRequest> arrReq) {
		HTTPRequest req = null;
		for (int i = 0, n = arrReq.size(); i < n; i++) {
			req = arrReq.get(i);
			req.setAlive(false);
		}
		arrReq.clear();
	}

	private void cancelRequest(ArrayList<HTTPRequest> arrReq, HTTPRequest req) {
		HTTPRequest curReq = null;
		for (int i = 0, n = arrReq.size(); i < n; i++) {
			curReq = arrReq.get(i);
			if (curReq == req) {
				arrReq.remove(i);
				req.setAlive(false);
				break;
			}
		}
		arrReq.clear();
	}
	
	@Override
	public void finish() {
		isFinished = true;
		super.finish();
	}

	@Override
	public void finishActivity(int requestCode) {
		isFinished = true;
		super.finishActivity(requestCode);
	}

	

}
