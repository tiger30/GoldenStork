package com.android.gs.global;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.http.entity.SerializableEntity;

import com.android.gs.listener.OnEventControlListener;



import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Lop string dinh dang spannable
 * @author: DoanDM
 * @version: 1.0
 * @since: 1.0
 */
public class SpannableObject implements Serializable{
	// chuoi text
	private String text;
	// danh sach cac doan text can dinh dang font, color
	private ArrayList<Bundle> bundleList = new ArrayList<Bundle>();
	// chuoi text
	private StringBuffer strBuff = new StringBuffer();
	SpannableString message;//instance cua SpannableObject
	
	private static final String BEGIN_POSITION = "BEGIN_POSITION";
	private static final String END_POSITION = "END_POSITION";
	private static final String TEXT_COLOR = "TEXT_COLOR";
	private static final String TEXT_STYLE = "TEXT_STYLE";
	private static final String TEXT_SIZE = "TEXT_SIZE";
	private static final String EXIST = "EXIST";
	private static final String DATA = "DATA";
	
	//listener, textView
	private boolean isAddListener = false;//kiem tra add listener?
	OnEventControlListener listener = null;
	int actionEvent = -1;
	TextView textView = null;	
	Bundle bundle = null;//bien tam
	
	public void addSpan(int begin, int end, int color, int style){
		bundle = new Bundle();
		bundle.putInt(BEGIN_POSITION, begin);
		bundle.putInt(END_POSITION, end);
		bundle.putInt(TEXT_COLOR, color);
		bundle.putInt(TEXT_STYLE, style);
		bundleList.add(bundle);
	}
	
	/** 
	*  Add text kem theo set span
	*  @author: DoanDM
	*  @param text
	*  @param color
	*  @param style
	*  @return: void
	*  @throws:
	 */
	public void addSpan(String text, int color, int style){
		strBuff.append(text);
		int endPos = strBuff.toString().length();
		bundle = new Bundle();
		bundle.putInt(BEGIN_POSITION, endPos - text.length());
		bundle.putInt(END_POSITION, endPos);
		bundle.putInt(TEXT_COLOR, color);
		bundle.putInt(TEXT_STYLE, style);
		bundleList.add(bundle);
	}
	
	/** 
	*  Add text kem theo set span
	*  @author: DoanDM
	*  @param text
	*  @param color
	*  @param style
	*  @return: void
	*  @throws:
	 */
	public void addSpan(String text, int color, int style, int size){
		strBuff.append(text);
		int endPos = strBuff.toString().length();
		bundle = new Bundle();
		bundle.putInt(BEGIN_POSITION, endPos - text.length());
		bundle.putInt(END_POSITION, endPos);
		bundle.putInt(TEXT_COLOR, color);
		bundle.putInt(TEXT_STYLE, style);
		bundle.putInt(TEXT_SIZE, size);
		bundleList.add(bundle);
	}
	
	/**
	 * 
	*  Click vo text nay --> handle + tra nguoc data lai cho listener
	*  @author: HieuNH
	*  @param text
	*  @param color
	*  @param style
	*  @param data
	*  @return: void
	*  @throws:
	 */
	public void addSpan(String text, int color, int style, Serializable data){
		strBuff.append(text);
		int endPos = strBuff.toString().length();
		bundle = new Bundle();
		bundle.putInt(BEGIN_POSITION, endPos - text.length());
		bundle.putInt(END_POSITION, endPos);
		bundle.putInt(TEXT_COLOR, color);
		bundle.putInt(TEXT_STYLE, style);
		bundle.putSerializable(DATA, data);
		bundleList.add(bundle);
	}
	
	/** 
	*  Add text kem theo set span
	*  @author: DoanDM
	*  @param text
	*  @param color
	*  @param style
	*  @return: void
	*  @throws:
	 */
	public void addSpan(String text, int style){
		strBuff.append(text);
		int endPos = strBuff.toString().length();
		bundle = new Bundle();
		bundle.putInt(BEGIN_POSITION, endPos - text.length());
		bundle.putInt(END_POSITION, endPos);
		bundle.putInt(TEXT_STYLE, style);
		bundleList.add(bundle);
	}
	
	
	/**
	*  Add text (khong co set span)
	*  @author: DoanDM
	*  @param text
	*  @return: void
	*  @throws:
	 */
	public void addSpan(String text){
		strBuff.append(text);
	}

	/**
	*  Add text (khong co set span)
	*  @author: DoanDM
	*  @param text
	*  @return: void
	*  @throws:
	 */
	public void addText(String text){
		strBuff.append(text);
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public SpannableString getSpan(String text){
		this.text = text;
		return getSpan();
	}
	
	public void setSpanableString (SpannableString msg){
		message = msg;
	}
	public SpannableString getSpanableString (){
		if(message == null){
			message = new SpannableString(strBuff.toString());
		}
		return message;
	}
	
	/**
	*  Lay text sau khi set span
	*  @author: DoanDM
	*  @return
	*  @return: SpannableString
	*  @throws:
	 */
	public SpannableString getSpan(){
		if(!checkIsInitInstance()){		
			message = new SpannableString(strBuff.toString());						
			for (int i = 0, size = bundleList.size(); i< size; i++){
				bundle = (Bundle)bundleList.get(i);
				bundle.putBoolean(EXIST, true);
				message.setSpan(new ForegroundColorSpan(bundle.getInt(TEXT_COLOR)), bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), 0);
				message.setSpan(new StyleSpan(bundle.getInt(TEXT_STYLE)),  bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if(bundle.containsKey(TEXT_SIZE)){
					message.setSpan(new AbsoluteSizeSpan(bundle.getInt(TEXT_SIZE)), bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if(listener != null && textView != null && bundle.getSerializable(DATA) != null){					
					MovementMethod m = textView.getMovementMethod();					
					if ((m == null) || !(m instanceof LinkMovementMethod))
					{
						textView.setMovementMethod(LinkMovementMethod.getInstance());
					}
//					textView.setLinkTextColor(bundle.getInt(TEXT_COLOR));
					ExtClickableSpan ext = new ExtClickableSpan(bundle.getSerializable(DATA));
//					ext.colorLink = bundle.getInt(TEXT_COLOR);
					ext.setColorLink(bundle.getInt(TEXT_COLOR));
					message.setSpan(ext,  bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), 0);
				}					
			}
		}
		return message;
	}
	
	private boolean checkIsInitInstance(){
		if(bundleList.size() == 0){
			return false;
		}
		for (int i = 0, size = bundleList.size(); i< size; i++){
			bundle = (Bundle)bundleList.get(i);
			if(!bundle.containsKey(EXIST) || !bundle.getBoolean(EXIST)){
				return false;//chua ton tai
			}
		}
		return true;//ton tai
	}
	
	public SpannableString getSpanNotColor(){
		if(!checkIsInitInstance()){	
			message = new SpannableString(strBuff.toString());					
					
			for (int i = 0, size = bundleList.size(); i< size; i++){
				bundle = (Bundle)bundleList.get(i);
				bundle.putBoolean(EXIST, true);
				message.setSpan(new StyleSpan(bundle.getInt(TEXT_STYLE)),  bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if(bundle.containsKey(TEXT_SIZE)){
					message.setSpan(new AbsoluteSizeSpan(bundle.getInt(TEXT_SIZE)), bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if(listener != null && textView != null && bundle.getSerializable(DATA) != null){					
					MovementMethod m = textView.getMovementMethod();					
					if ((m == null) || !(m instanceof LinkMovementMethod))
					{
						textView.setMovementMethod(LinkMovementMethod.getInstance());
					}
//					textView.setLinkTextColor(bundle.getInt(TEXT_COLOR));
					ExtClickableSpan ext = new ExtClickableSpan(bundle.getSerializable(DATA));
//					ext.colorLink = bundle.getInt(TEXT_COLOR);
					ext.setColorLink(bundle.getInt(TEXT_COLOR));
					message.setSpan(ext,  bundle.getInt(BEGIN_POSITION), bundle.getInt(END_POSITION), 0);
				}				
			}			
		}
		return message;
	}
	
	/**
	 * @author TruongHN
	 * add listener va su kien trong chuoi span
	 */
	public void addListener() {
		for (int i = 0, size = bundleList.size(); i < size; i++) {
			bundle = (Bundle) bundleList.get(i);
			if (listener != null && textView != null
					&& bundle.getSerializable(DATA) != null) {
				MovementMethod m = textView.getMovementMethod();
				if ((m == null) || !(m instanceof LinkMovementMethod)) {
					textView.setMovementMethod(LinkMovementMethod.getInstance());
				}
//				textView.setLinkTextColor(bundle.getInt(TEXT_COLOR));
				ExtClickableSpan ext = new ExtClickableSpan(
						bundle.getSerializable(DATA));
//				ext.colorLink = bundle.getInt(TEXT_COLOR);
				ext.setColorLink(bundle.getInt(TEXT_COLOR));
				message.setSpan(ext, bundle.getInt(BEGIN_POSITION),
						bundle.getInt(END_POSITION), 0);
			}
		}
		isAddListener = true;
	}
	
	public boolean isAddListener(){
		return isAddListener;
	}
	
	
	
	private class ExtClickableSpan extends ClickableSpan{
		private Object data;
		public int colorLink = -1;
		public ExtClickableSpan(Object data){
			this.data = data;
		}
		@Override
		public void onClick(View widget)
		{
			// When the span is clicked, show some text on-screen.							
			listener.onEvent(actionEvent, textView, this.data);		
		}
		/**
		 * @param colorLink the colorLink to set
		 */
		public void setColorLink(int colorLink) {
			this.colorLink = colorLink;
		}
		@Override
        public void updateDrawState(TextPaint ds) {			
			if(colorLink==-1){
				ds.setColor(ds.linkColor);
			}else{
				ds.setColor(colorLink);
			}
           ds.setUnderlineText(false); // set to false to remove underline
        }
	}
	
	/**
	*  Tao text voi span
	*  @author: DoanDM
	*  @param text
	*  @param color
	*  @param style
	*  @return: SpannableString
	*  @throws:
	 */
	public SpannableString getSpan(String text, int color, int style){
		strBuff.append(text);
		SpannableString message = new SpannableString(strBuff.toString());
		int textLength = strBuff.toString().length();
		message.setSpan(new ForegroundColorSpan(color),textLength - text.length(),textLength, 0);
		message.setSpan(new StyleSpan(style),textLength - text.length(),textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return message;
	}
	
	/**
	 * 
	*  get text length
	*  @author: AnhND
	*  @return
	*  @return: int
	*  @throws:
	 */
	public int getTextLength(){
		int length = 0;
		if (strBuff != null){
			length = strBuff.length();
		}
		return length;
	}
	
	/**
	 * @return the listener
	 */
	public OnEventControlListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(OnEventControlListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the textView
	 */
	public TextView getTextView() {
		return textView;
	}

	/**
	 * @param textView the textView to set
	 */
	public void setTextView(TextView textView) {
		this.textView = textView;
	}

	/**
	 * @return the actionEvent
	 */
	public int getActionEvent() {
		return actionEvent;
	}

	/**
	 * @param actionEvent the actionEvent to set
	 */
	public void setActionEvent(int actionEvent) {
		this.actionEvent = actionEvent;
	}
}
