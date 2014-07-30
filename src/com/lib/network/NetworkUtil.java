package com.lib.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class NetworkUtil {
	/** Default encoding */
	public static final String defaultEncoding = "UTF-8";// viet ga
	static final String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";
	/** Delimiter insert in request, response */
	public static final String DELIMITER = "�";
	// file type
	public static String PNG = "image/png";
	public static String GIF89 = "GIF89";
	// file field
	public static String AVATAR = "avatar";
	public static String PHOTO = "photo";

	public static String urlEncode(String sUrl) {
		if (sUrl == null) {
			return null;
		}
		StringBuffer urlOK = new StringBuffer();
		for (int i = 0; i < sUrl.length(); i++) {
			char ch = sUrl.charAt(i);
			switch (ch) {
			// case '<': urlOK.append("%3C"); break;
			// case '>': urlOK.append("%3E"); break;
			// case '/': urlOK.append("%2F"); break;
			case ' ':
				urlOK.append("%20");
				break;
			case '\n':
				urlOK.append("");
				break;
			// case ':': urlOK.append("%3A"); break;
			// case '-': urlOK.append("%2D"); break;
			default:
				urlOK.append(ch);
				break;
			}
		}
		return urlOK.toString();
	}

	// <HaiTC>
	public static Bitmap getImage(String url) {
		// public static Images getImage(String url) {
		// </HaiTC>
		try {
			// #ifdef DEBUG
			// # //System.out.println(url);
			// #endif
			// <HaiTC>
			URL _url = new URL(url);
			HttpURLConnection http = (HttpURLConnection) _url.openConnection();

			// url = urlEncode(url);
			// ContentConnection connection = (ContentConnection)
			// Connector.open(url);
			// </HaiTC>
			// UIController.busy = true;
			// * There is a bug in MIDP 1.0.3 in which read() sometimes returns
			// an invalid length. To work around this, I have changed the
			// stream to DataInputStream and called readFully() instead of
			// read()
			// InputStream iStrm = connection.openInputStream();
			// if(connection.getLength()>1000) Integer.parseInt("a");
			// <HaiTC>
			// DataInputStream iStrm = connection.openDataInputStream();
			DataInputStream iStrm = (DataInputStream) http.getInputStream();
			// </HaiTC>
			// DataInputStream iStrm = connection.openDataInputStream();

			ByteArrayOutputStream bStrm = null;
			// <HaiTC>
			Bitmap im = null;
			// Images im = null;
			// </HaiTC>
			try {
				// ContentConnection includes a length method
				byte imageData[];

				// <HaiTC>
				// int length = (int) connection.getLength();
				int length = (int) http.getContentLength();
				// </HaiTC>
				if (length != -1) {
					imageData = new byte[length];

					// Read the png into an array
					// iStrm.read(imageData);
					iStrm.readFully(imageData);
				} else // Length not available...
				{
					bStrm = new ByteArrayOutputStream();
					int ch;
					while ((ch = iStrm.read()) != -1) {
						bStrm.write(ch);
					}
					imageData = bStrm.toByteArray();
					bStrm.close();
				}
				// Create the image from the byte array
				// #ifdef DEBUG
				// # Log.logDebug(String.valueOf(imageData.length));
				// #endif

				if (imageData.length > 0) {
					// KunKunInfo.imgData = imageData;

					// <HaiTC
					im = BitmapFactory.decodeByteArray(imageData, 0,
							imageData.length);
					// im = Images.createImage(imageData, 0, imageData.length);
					// </HaiTC>
				}

			} catch (Exception e) {
				// #ifdef DEBUG
				// # e.printStackTrace();
				// # Log.logDebug(url);
				// #endif
			} finally {
				// UIController.busy = false;
				// #ifdef DEBUG
				// # if (im != null) {
				// # System.out.println("Done has image");
				// # } else {
				// # System.out.println("Done hasn't image");
				// # }
				// #endif
				// Clean up
				if (iStrm != null) {
					iStrm.close();
				}
				// <HaiTC>
				if (http != null) {
					http.disconnect();
				}
				// if (connection != null) {
				// connection.close();
				// }
				// </HaiTC>
				if (bStrm != null) {
					bStrm.close();
				}
			}

			return (im == null ? null : im);
		} catch (Exception e) {
			// UIController.busy = false;
			// #ifdef DEBUG
			// # Log.logDebug(e.getMessage());
			// #endif
		}
		return null;

	}

	public static String getResultFromServer(InputStreamReader iStrm,
			int bufSize) {
		String kq = "";
		StringBuffer response = new StringBuffer();
		char cbuf[] = new char[bufSize];
		int dptr = 0;

		try {
			if (iStrm != null) {
				int size = iStrm.read(cbuf);
				while (size > 0) {
					size--;
					// char c = (char) (cbuf[dptr++] & 0xff);
					response.append(cbuf[dptr++]);
				}
				kq = new String(response);
			}
		} catch (IOException e) {
			// #ifdef DEBUG
			// # e.printStackTrace();
			// #endif
		}
		return kq;
	}

	/**
	 * get json data to put on server
	 * 
	 * @param method
	 * @param dt
	 *            : vector of actions and its param
	 * @return
	 * @Author : DoanDM Since Dec 6, 2010
	 */
	public static String getJSONString(String method, Vector dt) {
		String org = null;
		try {
			JSONObject data = new JSONObject();
			data.put("method", method);
			JSONObject params = new JSONObject();
			if (dt != null) {
				for (int i = 0, size = dt.size(); i < size; i += 2) {
					params.put(dt.elementAt(i).toString(), dt.elementAt(i + 1)
							.toString());
				}
			}
			if (dt != null && dt.size() > 0) {
				data.put("params", params);
			}
			org = data.toString();
		} catch (Exception ex) {

		}
		return org;
	}

	/**
	 * get json data to put on server
	 * 
	 * @param method
	 * @param dt
	 *            : vector of actions and its param
	 * @return
	 * @Author : DoanDM Since Dec 6, 2010
	 */
//	public static String getJSONStringRSA(String method, Vector dt,
//			ActionEvent actionEvent,boolean usingAES) {
//		String org = "";
//		try {
//			
//
//			JSONObject orgObj = new JSONObject();
//			if(usingAES){
//				AESUtil aesUtil = new AESUtil();
//				String aesKey = aesUtil.getAESKey();
//				orgObj.put(IntentConstants.INTENT_AES_KEY, aesKey);
//				actionEvent.userData = aesKey;
//			}
//			for (int i = 0, size = dt.size(); i < size; i += 2) {
//				orgObj.put(dt.elementAt(i).toString(), dt.elementAt(i + 1)
//						.toString());
//			}
//
//			org = RSAUtil.getInstance().rsaEncrypt(orgObj.toString());
//
//			org = org.replace("\r", "");
//			org = org.replace("\n", "");
//
//			JSONObject params = new JSONObject();
//			params.put("request", org);
//
//			JSONObject data = new JSONObject();
//			data.put("method", method);
//			data.put("params", params);
//
//			
//			org = data.toString();
//			KunKunLog.i("-----------------", "-----------");
//			KunKunLog.i("REQUEST RSA:" + method, org);
//			KunKunLog.i("-----------------", "-----------");
//		} catch (Exception ex) {
//
//		}
//		return org;
//	}

	public static JSONObject getJSONObject(Vector params) {
		JSONObject result = new JSONObject();
		try {
			for (int i = 0, size = params.size(); i < size; i += 2) {
				result.put(params.elementAt(i).toString(),
						params.elementAt(i + 1).toString());
			}
		} catch (Exception e) {

		}
		return result;
	}

	public static String getRequestParams(String method, JSONObject params) {
		JSONObject result = new JSONObject();
		try {
			result.put("method", method);
			result.put("params", params);
		} catch (Exception e) {

		}
		return result.toString();

	}

	public static byte[] getMultiPartByte(String method, Vector dt,
			byte[] fileBytes, String fileName) {
		byte[] postBytes = null;
		try {
			Hashtable params = new Hashtable();
			params.put("request", "{\"method\":\"" + method + "\"}");
			for (int i = 0, size = dt.size(); i < size; i += 2) {
				params.put(dt.elementAt(i).toString(), dt.elementAt(i + 1)
						.toString());
			}
			// begin setting multipart
			String boundary = getBoundaryString();

			String boundaryMessage = getBoundaryMessage(boundary, params,
					"avatar", fileName, PNG);

			String endBoundary = "\r\n--" + boundary + "--\r\n";

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			bos.write(boundaryMessage.getBytes());

			bos.write(fileBytes);

			bos.write(endBoundary.getBytes("UTF8"));

			postBytes = bos.toByteArray();

			bos.close();
		} catch (Exception ex) {

		}
		return postBytes;
	}

	// use for register
	public static byte[] getMultiPartByte(String dataText, byte[] fileBytes,
			String fileName, String fileField, String fileType) {
		byte[] postBytes = null;
		try {
			Hashtable params = new Hashtable();
			params.put("request", dataText);

			// begin setting multipart
			String boundary = getBoundaryString();

			String boundaryMessage = getBoundaryMessage(boundary, params,
					fileField, fileName, fileType);

			String endBoundary = "\r\n--" + boundary + "--\r\n";

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			bos.write(boundaryMessage.getBytes());

			bos.write(fileBytes);

			bos.write(endBoundary.getBytes("UTF8"));

			postBytes = bos.toByteArray();

			bos.close();
		} catch (Exception ex) {

		}
		return postBytes;
	}

	static String getBoundaryMessage(String boundary, Hashtable params,
			String fileField, String fileName, String fileType) {
		StringBuffer res = new StringBuffer("--").append(boundary).append(
				"\r\n");

		Enumeration keys = params.keys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) params.get(key);

			res.append("Content-Disposition: form-data; name=\"").append(key)
					.append("\"\r\n").append("\r\n").append(value)
					.append("\r\n").append("--").append(boundary)
					.append("\r\n");
		}
		res.append("Content-Disposition: form-data; name=\"").append(fileField)
				.append("\"; filename=\"").append(fileName).append("\"\r\n")
				.append("Content-Type: ").append(fileType).append("\r\n\r\n");

		return res.toString();
	}

	public static String getBoundaryString() {
		return BOUNDARY;
	}
}
