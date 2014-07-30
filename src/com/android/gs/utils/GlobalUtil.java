package com.android.gs.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.gs.application.MyApplication;


public class GlobalUtil {
	static DisplayMetrics metrics = null;
	static int typeOfSpecFone = -1;
	static int statusBarHeight = -1;
	
	static final int STANDARD_WIDTH = 480;
	static final int STANDARD_HEIGHT = 800;
	
	static float SCALE_RATE = 0;
	static int slideMenuWidth = 0;
	/**
	 * 
	 * 
	 * get display screen width (DP unit)
	 * @params : 
	 * @author : doandm 
	 * @since : May 25, 2012
	 */
	public static int getDisplayWidth(){
		if(metrics == null){
			if( MyApplication.getInstance().getActivityContext() instanceof Activity){
				metrics = new DisplayMetrics();
				((Activity) MyApplication.getInstance().getActivityContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			}else{
				metrics = MyApplication.getInstance().getActivityContext().getResources().getDisplayMetrics();
			}
		}
		return metrics.widthPixels;
	}
	
	/**
	 * get slide menu width
	 * @return
	 * DoanDM
	 * Feb 20, 2014
	 */
	public static int getSlideMenuWidth(){
		return slideMenuWidth;
	}
	
	public static void setSlideMenuWidth(int w){
		slideMenuWidth = w;
	}
	/**
	 * 
	 * 
	 * get display screen height (DP unit)
	 * @params : 
	 * @author : doandm 
	 * @since : May 25, 2012
	 */
	public static int getDisplayHeight(){
		if(metrics == null){
			if( MyApplication.getInstance().getActivityContext() instanceof Activity){
				metrics = new DisplayMetrics();
				((Activity)MyApplication.getInstance().getActivityContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			}else{
				metrics = MyApplication.getInstance().getActivityContext().getResources().getDisplayMetrics();
			}
		}
		if(statusBarHeight < 0){
			Context context = MyApplication.getInstance().getActivityContext();
			statusBarHeight = (int) Math.ceil(25 * context.getResources().getDisplayMetrics().density);
		}
		return metrics.heightPixels -statusBarHeight ;
	}
	
	public static int getMetricHeight(){
		if(metrics == null){
			if( MyApplication.getInstance().getActivityContext() instanceof Activity){
				metrics = new DisplayMetrics();
				((Activity)MyApplication.getInstance().getActivityContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			}else{
				metrics = MyApplication.getInstance().getActivityContext().getResources().getDisplayMetrics();
			}
		}
		return metrics.heightPixels;
	}
	
	public static int getMetricWidth(){
		if(metrics == null){
			if( MyApplication.getInstance().getActivityContext() instanceof Activity){
				metrics = new DisplayMetrics();
				((Activity)MyApplication.getInstance().getActivityContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			}else{
				metrics = MyApplication.getInstance().getActivityContext().getResources().getDisplayMetrics();
			}
		}
		return metrics.widthPixels;
	}
	
	//get type of spec fone
	/**
	 * get fone type
	 * 
	 * @return DisplayMetrics.DENSITY_LOW|DisplayMetrics.DENSITY_MEDIUM|DisplayMetrics.DENSITY_HIGH|DisplayMetrics.DENSITY_XHIGH
	 */
	public int getPhoneDisplayMetrics()
	{
		if(typeOfSpecFone < 0){
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)MyApplication.getInstance().getActivityContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        typeOfSpecFone = metrics.densityDpi;
		}
		return typeOfSpecFone;
	}
	
	/**
	 * check tablet type
	 */
	public  boolean isTablet() 
	{
	    boolean xlarge = ((MyApplication.getInstance().getActivityContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
	    boolean large = ((MyApplication.getInstance().getActivityContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    
	   return (xlarge || large);
	}
	
	/**
	 * Removes the reference to the activity from every view in a view hierarchy (listeners, images etc.).
	 * This method should be called in the onDestroy() method of each activity
	 * @author: DoanDM
	 * @param viewID normally the id of the root layout
	 * @return
	 * @throws:
	 */
	
	public static void nullViewDrawablesRecursive(View view)
	  {
	    if(view != null)
	    {
	      try
	      {
	        ViewGroup viewGroup = (ViewGroup)view;

	        int childCount = viewGroup.getChildCount();
	        for(int index = 0; index < childCount; index++)
	        {
	          View child = viewGroup.getChildAt(index);
	          nullViewDrawablesRecursive(child);
	        }
	      }
	      catch(Exception e)
	      {          
	      }

	      unbindViewReferences(view);
	    }    
	  }
	/**
	*  Remove view reference
	*  @author: DoanDM
	*  @param view
	*  @return: void
	*  @throws:
	 */
	@SuppressWarnings("deprecation")
	private static void unbindViewReferences(View view) {
		// set all listeners to null (not every view and not every API level supports the methods)
		try {view.setOnClickListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnCreateContextMenuListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnFocusChangeListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnKeyListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnLongClickListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnClickListener(null);} catch (Throwable mayHappen) {};

		// set background to null
		Drawable d = view.getBackground(); 
		if (d!=null) d.setCallback(null);
		if (view instanceof ImageView) {
			ImageView imageView = (ImageView) view;
			d = imageView.getDrawable();
			if (d!=null) d.setCallback(null);
			imageView.setImageDrawable(null);
			imageView.setBackgroundDrawable(null);
//			if ( d instanceof BitmapDrawable){	
//				((BitmapDrawable)d).setCallback(null);
//				((BitmapDrawable)d).getBitmap().recycle();	
//				KunKunLog.e("BigPhotoCache", "set call back null and recycle    " + ((BitmapDrawable)d).getBitmap().isRecycled() );
//				d=null;
//			}
			imageView=null;
		}

		// destroy webview
		if (view instanceof WebView) {
			((WebView) view).destroyDrawingCache();
			((WebView) view).destroy();
		}
		view=null;
	}
	
	/**
	 * get left cordinate of view on screen
	 * @param myView
	 * @return
	 * DoanDM
	 * Nov 20, 2013
	 */
	public static int getRelativeLeft(View myView) {
	    if (myView.getParent() == myView.getRootView())
	        return myView.getLeft();
	    else
	        return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}
	
	/**
	 * get top cordinate of view on screen
	 * @param myView
	 * @return
	 * DoanDM
	 * Nov 20, 2013
	 */
	public static int getRelativeTop(View myView) {
	    if (myView.getParent() == myView.getRootView())
	        return myView.getTop();
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
	
//	public static void sendIntent(Activity activity,String content){
//		Intent sendIntent = new Intent();
//		sendIntent.setAction(Intent.ACTION_SEND);
//		sendIntent.putExtra(Intent.EXTRA_TEXT, content);
//		sendIntent.setType("text/plain");
//		activity.startActivity(Intent.createChooser(sendIntent, activity.getResources().getText(R.string.share_text)));
//	}
	
	/**
	 * return rate scale of screen
	 * @return
	 * DoanDM
	 * Jan 14, 2014
	 */
	public static float getScaleRate(){
		if(SCALE_RATE == 0){
			SCALE_RATE = (GlobalUtil.getDisplayWidth()*GlobalUtil.getDisplayHeight())/(STANDARD_WIDTH*STANDARD_HEIGHT);
			if(SCALE_RATE <= 0 ){
				SCALE_RATE = 1;
			}
		}
		return SCALE_RATE;
	}
	
	/**
	 * save object vao file
	 * 
	 * @author: DoanDM
	 * @param object
	 * @param fileName
	 * @return: void
	 * @throws:
	 */
	public static void saveObject(Object object, String fileName) {
		try {
			FileOutputStream fos = MyApplication.getInstance().getActivityContext()
					.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Doc file
	 * 
	 * @author: DoanDM
	 * @param ct
	 * @param fileName
	 * @return: void
	 * @throws:
	 */
	public static Object readObject(String fileName) {
		Object object = null;
		try {
			if (isExistFile(fileName)) {
				FileInputStream fis = MyApplication.getInstance().getActivityContext()
						.openFileInput(fileName);
				if (fis != null) {// ton tai file
					ObjectInputStream ois = new ObjectInputStream(fis);
					object = ois.readObject();
					ois.close();
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	public static boolean isExistFile(String fileName) {
		try {
			if (!StringUtil.isNullOrEmpty(fileName)) {
				String[] s =  MyApplication.getInstance().getActivityContext()
						.fileList();
				for (int i = 0, size = s.length; i < size; i++) {
					if (fileName.equals(s[i].toString())) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
