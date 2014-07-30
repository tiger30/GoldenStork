package com.lib.network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.HeaderGroup;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Build;

import com.android.gs.utils.MyLog;
import com.lib.network.DataSupplier.Data;

/**
 * DataSuplier interface
 * 
 * @author: DoanDM
 * @version: 1.0
 * @since: 1.0
 */
interface DataSupplier {
	public class Data {
		byte[] buffer;
		boolean isFinish;
		int length;
	}

	void getNextPart(Data data);

	void releaseData();

	int overallDataSize();

	void reset();
}

/**
 * 
 * request http
 * 
 * @author: DoanDM
 * @version: 1.0
 * @since: 1.0
 */
public class HttpAsyncTask extends AsyncTask<Void, Void, Void> {
	private static int CONNECT_TIMEOUT = 60000; // miliseconds: 1 minute
	static final int NUM_RETRY = 1;
	private HTTPRequest request;
	private HTTPResponse response;
	private boolean isSuccess;
	private static final String LOG_TAG = "HttpAsyncTask";
	static HttpClient httpclient;

	public HttpAsyncTask(HTTPRequest re) {
		this.request = re;
		if (httpclient == null) {
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not
			// used.
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					CONNECT_TIMEOUT);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for
			// data.
			HttpConnectionParams.setSoTimeout(httpParameters,
					CONNECT_TIMEOUT);
			httpclient = new DefaultHttpClient(httpParameters);
		}

	}

	@Override
	protected Void doInBackground(Void... params) {
		if (request == null || request.isAlive() == false) {
			if (request == null) {
				MyLog.i(LOG_TAG, "Request null");
			} else {
				MyLog.i(LOG_TAG, "Request NOT alive");
			}
			return null;
		}

		int countRetry = 0;
		
		boolean isRetry = false;
		do {
			isRetry = false;
			countRetry++;
			isSuccess = true;
			HttpRequestBase req = null;
			// bug sometime response code = -1
			disableConnectionReuseIfNecessary();
			try {
				response = new HTTPResponse(request);

				if (HTTPRequest.POST.equals(request.getMethod())) {
					Data data = new Data();
					request.getNextPart(data);
				}else{
					req = new HttpGet(request.getUrl());
					MyLog.e("HTTP ASYNCTASK", request.getUrl());
				}
				
				if (request.getContentType() != null) {
					req.setHeader("Content-type",
							request.contentType);
				}
				if (HTTPClient.sessionID != null) {
					req.setHeader("Cookie",
							HTTPClient.sessionID);
				}

				req.setHeader("Cache-Control", "no-cache");
				req.setHeader("Cache-Control", "max-age=0");
				req.setHeader("Cache-Control", "only-if-cached");

				
				HttpResponse res = httpclient.execute(req);
				response.readData(res.getEntity().getContent());
				response.setCode(res.getStatusLine().getStatusCode());
				if (response.getDataText() == null
						&& response.getDataBinary() == null
						&& request.isAlive()) {
					isSuccess = false;
					isRetry = true;
					StringBuffer strBuffer = new StringBuffer();
					strBuffer.append("ResponseCode: "
							+ res.getStatusLine().getStatusCode());
					strBuffer.append("/ResponseMsg: "
							+ res.getStatusLine().getReasonPhrase());
					response.setError(HTTPClient.ERR_UNKNOWN,
							strBuffer.toString());
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				isSuccess = false;
				response.setError(HTTPClient.ERR_INVALID_URL,
						"ERR_INVALID_URL", e.getMessage() + "/" + e.toString());
				MyLog.i(LOG_TAG, "MalformedURLException - " + e.getMessage()
						+ "/" + e.toString());
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				isSuccess = false;
				response.setError(HTTPClient.ERR_NOT_FOUND,
						"HTTPClient.ERR_NOT_FOUND",
						e.getMessage() + "/" + e.toString());
				MyLog.i(LOG_TAG, "FileNotFoundException - " + e.getMessage()
						+ "/" + e.toString());
				MyLog.logToFile(
						LOG_TAG,
						"FileNotFoundException - " + e.getMessage() + "/"
								+ e.toString());
			} catch (SocketTimeoutException e) {
				// TODO: handle exception
				isSuccess = false;
				response.setError(HTTPClient.ERR_TIME_OUT,
						"HTTPClient.ERR_TIME_OUT",
						e.getMessage() + "/" + e.toString());
				MyLog.i(LOG_TAG, "FileNotFoundException - " + e.getMessage()
						+ "/" + e.toString());
				MyLog.logToFile(
						LOG_TAG,
						"FileNotFoundException - " + e.getMessage() + "/"
								+ e.toString());
			} catch (UnknownHostException e) {
				// TODO: handle exception
				isSuccess = false;
				isRetry = true;
				response.setError(HTTPClient.ERR_NO_CONNECTION,
						"HTTPClient.ERR_NO_CONNECTION", e.getMessage() + "/"
								+ e.toString());
				MyLog.i(LOG_TAG, "FileNotFoundException - " + e.getMessage()
						+ "/" + e.toString());
				MyLog.logToFile(
						LOG_TAG,
						"FileNotFoundException - " + e.getMessage() + "/"
								+ e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				isSuccess = false;
				isRetry = true;
				response.setError(HTTPClient.ERR_UNKNOWN,
						"HTTPClient.ERR_UNKNOWN",
						e.getMessage() + "/" + e.toString());
				MyLog.i(LOG_TAG,
						"IOException - " + e.getMessage() + "/" + e.toString());
				MyLog.logToFile(LOG_TAG, "IOException - " + e.getMessage()
						+ "/" + e.toString());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				isSuccess = false;
				isRetry = true;
				response.setError(HTTPClient.ERR_UNKNOWN,
						"HTTPClient.ERR_UNKNOWN",
						e.getMessage() + "/" + e.toString());
				MyLog.i(LOG_TAG,
						"Throwable - " + e.getMessage() + "/" + e.toString());
				MyLog.logToFile(LOG_TAG, "Throwable - " + e.getMessage() + "/"
						+ e.toString());
			} finally {

			}
		} while (countRetry <= NUM_RETRY && isRetry);

		HTTPListenner listenner = null;
		// boolean isAlive = true;
		if (response != null) {
			listenner = response.getObserver();
			// isAlive = response.request.isAlive();
		}
		if (listenner != null) {
			if (isSuccess) {
				listenner.onReceiveData(response);
			} else {
				listenner.onReceiveError(response);
			}
		}
		return null;
	}

	/**
	 * check connection reuse
	 * 
	 * @author: DoanDM
	 * @return: void
	 * @throws:
	 */
	private void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
		System.setProperty("networkaddress.cache.ttl", "0");
		System.setProperty("networkaddress.cache.negative.ttl", "0");
	}

	/**
	 * 
	 * send data
	 * 
	 * @author: DoanDM
	 * @param outputStream
	 * @param dataSupplier
	 * @throws IOException
	 * @return: void
	 * @throws:
	 */
	void writeData(OutputStream outputStream, DataSupplier dataSupplier)
			throws IOException {
		Data data = new Data();
		while (true) {
			dataSupplier.getNextPart(data);
			if (data.buffer != null && data.length > 0) {
				outputStream.write(data.buffer, 0, data.length);
				outputStream.flush();
				dataSupplier.releaseData();
			}
			if (data.isFinish) {
				break;
			}
		}

	}
}
