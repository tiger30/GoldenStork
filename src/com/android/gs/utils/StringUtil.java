package com.android.gs.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ParseException;
import android.widget.TextView;

import com.android.gs.application.MyApplication;
import com.android.gs.constant.MyConstants;
import com.android.gs.utils.ui.EllipsizingTextView;


public class StringUtil {
	// List of all date formats that we want to parse.
    // Add your own format here.
    @SuppressWarnings("serial")
	private static List<SimpleDateFormat> 
            dateFormats = new ArrayList<SimpleDateFormat>() {{
            add(new SimpleDateFormat("dd/MM/yyyy"));
//            add(new SimpleDateFormat("yyyy/MM/dd"));
//            add(new SimpleDateFormat("M/dd/yyyy"));
//            add(new SimpleDateFormat("dd.M.yyyy"));
//            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
//            add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
//            add(new SimpleDateFormat("dd.MMM.yyyy"));
//            add(new SimpleDateFormat("dd-MMM-yyyy"));
        }
    };
    private static SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	/**
	*  get text width
	*  @author: DoanDM
	*  @param text
	*  @return
	*  @return: int
	*  @throws:
	 */
	public static int getTextWidth(String text){
		Rect bound = new Rect();
		TextView tvView = new TextView(MyApplication.getInstance().getActivityContext());
		Paint paint = tvView.getPaint();
		paint.getTextBounds(text, 0, text.length(), bound);
		tvView = null;
		return bound.width();
	}
	
	/**
	*  get text height
	*  @author: DoanDM
	*  @param text
	*  @return
	*  @return: int
	*  @throws:
	 */
	public static int getTextHeight(String text){
		Rect bound = new Rect();
		TextView tvView = new TextView(MyApplication.getInstance().getActivityContext());
		Paint paint = tvView.getPaint();
		paint.getTextBounds(text, 0, text.length(), bound);
		tvView = null;
		return bound.height();
	}
	/**
	*  get text width
	*  @author: DoanDM
	*  @param textTracking
	*  @return
	*  @return: int
	*  @throws:
	 */
	public static int getTextWidth(TextView tvView){
		Rect bound = new Rect();
		Paint paint = tvView.getPaint();
		paint.getTextBounds(tvView.getText().toString(), 0, tvView.getText().toString().length(), bound);
		tvView = null;
		return bound.width();
	}
	
	/**
	*  get text height
	*  @author: DoanDM
	*  @param textTracking
	*  @return
	*  @return: int
	*  @throws:
	 */
	public static int getTextHeight(TextView tvView){
		Rect bound = new Rect();
		Paint paint = tvView.getPaint();
		paint.setTextSize(tvView.getTextSize());
		paint.getTextBounds(tvView.getText().toString(), 0, tvView.getText().toString().length(), bound);
		tvView = null;
		return bound.height();
	}
	

	public static String replace(String _text, String _searchStr,
			String _replacementStr) {
		// String buffer to store str
		StringBuffer sb = new StringBuffer();

		// Search for search
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();

		// Iterate to add string
		while (searchStringPos != -1) {
			sb.append(_text.substring(startPos, searchStringPos)).append(
					_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}

		// Create string
		sb.append(_text.substring(startPos, _text.length()));

		return sb.toString();
	}

	

	/**
	 * check normal characters
	 * 
	 * @author: DoanDM
	 * @param name
	 * @return: boolean
	 * @throws:
	 */
	public static boolean isValidateNormalContent(String name) {
		// Noi dung chi chua cac ky tu a-z, A-Z, 0-9, khoang trang
		Boolean isValid = false;
		int length = name.length();
		for (int i = 0; i < length; i++) {
			char ch = name.charAt(i);
			if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
					|| (ch >= '0' && ch <= '9') || (ch == ' '))) {
				isValid = true;
			} else {
				isValid = false;
				break;
			}
		}
		return isValid;
	}

	


	/**
	 * <b> validateEMail - Validated the string is valid email id or not</b>
	 * @param emailString Email ID String to be validated
	 * @return boolean if emailString is valid
	 * @author gca4kor
	 */
	public static boolean validateEMail(String emailString){
		/*final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
		          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +"\\@" +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
		          "(" +"\\." +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +")+");*/
		Pattern EMAIL_ADDRESS_PATTERN=android.util.Patterns.EMAIL_ADDRESS;
		return EMAIL_ADDRESS_PATTERN.matcher(emailString+MyConstants.STR_BLANK).matches();
	}
	
	/**
	 * <b> validatePhone - Validated the string is valid Phone number or not</b>
	 * @param phoneString Phone number String to be validated
	 * @return boolean if phoneString is valid
	 * @author gca4kor
	 */
	public static boolean validatePhoneNumber(String phoneString){
		/*final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
		          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +"\\@" +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
		          "(" +"\\." +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +")+");*/
		Pattern EMAIL_ADDRESS_PATTERN=android.util.Patterns.PHONE;
		return EMAIL_ADDRESS_PATTERN.matcher(phoneString+MyConstants.STR_BLANK).matches();
	}


	public final static void supplementCodePointToSurrogatePair(int codePoint,
			int[] surrogatePair) {
		int high4 = ((codePoint >> 16) & 0x1F) - 1;
		int mid6 = ((codePoint >> 10) & 0x3F);
		int low10 = codePoint & 0x3FF;

		surrogatePair[0] = (0xD800 | (high4 << 6) | (mid6));
		surrogatePair[1] = (0xDC00 | (low10));
	}


	public static boolean isNullOrEmpty(String aString) {
		return (aString == null) || (MyConstants.STR_BLANK.equals(aString.trim()));
	}

	

	public static String getString(int id) {
		return MyApplication.getInstance().getActivityContext().getResources()
				.getString(id);
	}

	

	public static String[] getStringArray(int id) {
		return MyApplication.getInstance().getActivityContext().getResources()
				.getStringArray(id);
	}

	
	/**
	 * 
	*  1 chuoi so thanh dang : 5-->5,5.0-->5, 5.5 --> 5.5
	*  @author: DoanDM
	*  @param value
	*  @return
	*  @return: String
	*  @throws:
	 */
//	public static String checkDoubleString(String str,String addition){
//		String result=MyConstants.STR_BLANK;
//		Vector tmp = split(String.valueOf(str), '.');
//		int tmpNumber = Integer.parseInt(tmp.get(0).toString());
//		double n = Double.parseDouble(str);
//		if(tmpNumber==n){
//			result+=String.valueOf(tmp.get(0).toString())+addition;
//		}else{
//			result+=String.valueOf(tmp.get(0))+","+tmp.get(1).toString()+addition;
//		}
//		return result;
//	}
	/**
	 * 
	*  replace blank into %20
	*  @author: DoanDM
	*  @return: void
	*  @throws:
	 */
	public static String replaceSpaceByHtmlCode(String ori){
		if(isNullOrEmpty(ori)) return MyConstants.STR_BLANK;
		return ori.replace(" ", "%20");
	}

	public static String md5(String s) {
		byte[] defaultBytes = s.getBytes();
		try {
			MessageDigest algorithm;
			algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MyConstants.STR_BLANK;
	}
	/**
	 * get current time with HH:MM:SS format
	 * @return
	 * DoanDM
	 * Nov 15, 2013
	 */
	@SuppressWarnings("deprecation")
	public static String getCurrentTime(String format){
//		Time now = new Time();
//		now.setToNow();
//		String expectedPattern = MyConstants.STR_BLANK.equals(format) ? "HH:MM:SS" : format;
		Date date = new Date(System.currentTimeMillis());
		String hh = String.valueOf(date.getHours());
		if(hh.length()  == 1){
			hh = "0"+hh;
		}
		
		String mm = String.valueOf(date.getMinutes());
		if(mm.length()  == 1){
			mm = "0"+mm;
		}
		String ss = String.valueOf(date.getSeconds());
		if(ss.length()  == 1){
			ss = "0"+ss;
		}
		return hh+":"+mm+":"+ss;
	}
	
	/**
	 * get current time with HH:MM format
	 * @return
	 * DoanDM
	 * Nov 15, 2013
	 */
	@SuppressWarnings("deprecation")
	public static String getCurrentTimeWithoutSecond(String format){
//		Time now = new Time();
//		now.setToNow();
//		String expectedPattern = MyConstants.STR_BLANK.equals(format) ? "HH:MM:SS" : format;
		Date date = new Date(System.currentTimeMillis());
		String hh = String.valueOf(date.getHours());
		if(hh.length()  == 1){
			hh = "0"+hh;
		}
		
		String mm = String.valueOf(date.getMinutes());
		if(mm.length()  == 1){
			mm = "0"+mm;
		}
//		String ss = String.valueOf(date.getSeconds());
//		if(ss.length()  == 1){
//			ss = "0"+ss;
//		}
		return hh+":"+mm;
	}
	
	/**
	 * get current date with spec format, if format is empty, default is "DD/MM/yyyy"
	 * @param format
	 * @return
	 * @throws Exception
	 * DoanDM
	 * Nov 15, 2013
	 */
	public static String getCurrentDate(String format){
		try {
			return dateToString(new Date(System.currentTimeMillis()),format);

		} catch (Exception ex) {
//			throw ex;
			return MyConstants.STR_BLANK;
		}
	}
	
	/**
	 * get current date time with format HH:MM:SS DD/MM/YYYY
	 * @return
	 * DoanDM
	 * Nov 15, 2013
	 */
	public static String getCurrentTimeDate(){
		return getCurrentTime(MyConstants.STR_BLANK) + MyConstants.STR_TAB + getCurrentDate(MyConstants.STR_BLANK);
	}
	
	/**
	 * get current date time with format HH:MM DD/MM/YYYY
	 * @return
	 * DoanDM
	 * Nov 15, 2013
	 */
	public static String getCurrentTimeDateWithoutSecond(){
		return getCurrentTimeWithoutSecond(MyConstants.STR_BLANK) + MyConstants.STR_TAB + getCurrentDate(MyConstants.STR_BLANK);
	}
	
	

	/**
	 * get current date time with format YYYY/MM/DD HH:MM:SS 
	 * @return
	 * DoanDM
	 * Nov 15, 2013
	 */
	public static String getCurrentDateTime(){
		return   getCurrentDate("yyyy/MM/dd")+MyConstants.STR_TAB+getCurrentTime(MyConstants.STR_BLANK);
	}
	
	public static String dateToString(Date input, String format)
			 {
		try {
			String expectedPattern = isNullOrEmpty(format) ? "dd/MM/yyyy" : format;
			SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

			return formatter.format(input);

		} catch (Exception ex) {
		}
		return MyConstants.STR_BLANK;
	}

	public static Date stringToDate(String input, String format)
			throws Exception {
		try {
			String expectedPattern = isNullOrEmpty(format) ? "dd/MM/yyyy" : format;
			SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

			return formatter.parse(input);

		} catch (Exception ex) {
			throw ex;
		}
	}

	public static Date addDate(Date input, int number) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(input);

		cal.add(Calendar.DATE, number);

		return cal.getTime();
	}
	
	/*
	 * append string in offset (begin from right to left)
	 */
	public static String apendString2String(String ori,String append,int blockLength,int offset){
		String rs = MyConstants.STR_BLANK;
		while(offset>blockLength){
			rs = append + ori.substring(offset - blockLength, offset)+rs;
			offset-=blockLength;
		}
		if(offset>0){
			rs = ori.substring(0,offset)+rs;
		}
		
		return rs;
	}
	/**
	 * add new string to offset position
	 * your description here
	 * @params : 
	 * @author : doandm 
	 * @since : Jun 6, 2012
	 */
//	public static String apendString2String(String ori,String append,int blockFirst,int blockMid){
//		String rs = MyConstants.STR_BLANK;
//		if(ori.length()<blockFirst){
//			rs += ori;
//			return rs;
//		}
//		rs += ori.substring(0, blockFirst)+append;
//		ori = ori.substring(blockFirst);
//		while(ori.length()>blockMid){
//			rs += ori.substring(0, blockMid)+append;
//			ori = ori.substring(blockMid);
//		}
//		rs += ori;
//		return rs;
//	}

	/**
	 * create text like
	 * ffffff...(390)
	 * 
	 * @author: DoanDM
	 * @param source
	 * @return
	 * @return: String
	 * @throws:
	 */
//	public static void creatTextViewEllipsize(EllipsizingTextView tv_ellipsize,
//			String more, int maxLine) {
//		if (tv_ellipsize != null && !isNullOrEmpty(more)) {
//			tv_ellipsize.setStringMore(more);
//			tv_ellipsize.setMaxLines(maxLine);
//		}
//	}

	/**
	 * replace multispcae by one space
	 * 
	 * @author: DoanDM
	 * @param source
	 * @return
	 * @return: String
	 * @throws:
	 */
	public static String itrim(String source) {
		if (!isNullOrEmpty(source)) {
			return source.replaceAll("\\b\\s{2,}\\b", " ");
		}
		return source;
	}
	/**
	 * get Font, name comes from MyConstant
	 * @param name
	 * @return
	 * @author DoanDM
	 * @since Apr 17, 2014
	 */
	public static Typeface getFont(String name){
		 // Font path
        String fontPath = "fonts/"+name;
        Typeface tf = Typeface.createFromAsset(MyApplication.getInstance().getActivityContext().getAssets(), fontPath);
        return tf;
	}
	
	 /**
     * Convert String with various formats into java.util.Date
     * 
     * @param input
     *            Date as a string
     * @return java.util.Date object if input string is parsed 
     *          successfully else returns null
     */
    public static Date convertToDate(String input) {
        Date date = null;
        if(null == input) {
            return null;
        }
        for (SimpleDateFormat format : dateFormats) {
            try {
                format.setLenient(false);
                date = format.parse(input);
            } catch (ParseException e) {
                //Shhh.. try other formats
            } catch (Exception ex){
            	
            }
            if (date != null) {
                break;
            }
        }
 
        return date;
    }

	 /**
    * Convert String with various formats into java.util.Date
    * 
    * @param input
    *            Date as a string
    * @return java.util.Date object if input string is parsed 
    *          successfully else returns null
    */
   public static Date checkFullDateTimeFormat(String input) {
       Date date = null;
       if(null == input) {
           return null;
       }
       try {
    	   fullDateTimeFormat.setLenient(false);
           date = fullDateTimeFormat.parse(input);
       } catch (ParseException e) {
           //Shhh.. try other formats
       } catch (Exception ex){
       	
       }

       return date;
   }
   
   /**
    * replace enter character by space
    * @param org
    * @return
    * DoanDM
    * Mar 14, 2014
    */
   public static String replaceEnterCharacterBySpace(String org){
	   return org.replace("\r\n", MyConstants.STR_SPACE).replace("\n", MyConstants.STR_SPACE);
   }
   
	
}
