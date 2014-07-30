/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lib.network;

import java.io.UnsupportedEncodingException;
public class HTTPRequest extends HTTPMessage implements DataSupplier{

	public static final String GET = "GET";
	public static final String POST = "POST";
    public static String CONTENT_TYPE_STRING = "text";
    public static String CONTENT_TYPE_BINARY = "binary";
    
    public static String DATA_TYPE_BINARY = "binary";
    public static String DATA_TYPE_TEXT = "text";
    
    //<HaiTC>
    private String method="GET";
    //private String method=HttpConnection.GET;
    //</HaiTC>
    private String url;
//    public boolean isReceive = false;//HieuNH define de biet duoc request da duoc Receive hay chua
    private int lockTime = 0;
    private String dataTypeSend = CONTENT_TYPE_STRING;
    private String dataTypeReceive = CONTENT_TYPE_STRING;

	@Override
	public void setDataText(String dataText) {
		super.setDataText(dataText);
		dataTypeSend = DATA_TYPE_TEXT;
	}
	
	@Override
	public void setDataBinary(byte[] dataBinary) {
		// TODO Auto-generated method stub
		super.setDataBinary(dataBinary);
		dataTypeSend = DATA_TYPE_BINARY;
	}
	
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLockTime() {
        return lockTime;
    }

    public void setLockTime(int lockTime) {
        this.lockTime = lockTime;
    }

    public String getDataTypeReceive() {
        return dataTypeReceive;
    }

    public void setDataTypeReceive(String dataTypeReceive) {
        this.dataTypeReceive = dataTypeReceive;
    }

    public String getDataTypeSend() {
        return dataTypeSend;
    }

    public void setDataTypeSend(String dataTypeSend) {
        this.dataTypeSend = dataTypeSend;
    }

	/* (non-Javadoc)
	 * @see com.viettel.kunkun.kunkunlibrary.network.http.DataSupplier#getNextPart()
	 */
	@Override
	public void getNextPart(Data data) {
		// TODO Auto-generated method stub
		try{
			if (DATA_TYPE_TEXT.equals(dataTypeSend)){
				data.buffer = dataText.getBytes("UTF8");
			}else {
				data.buffer = dataBinary;
			}
			data.isFinish = true;
			data.length = data.buffer == null ? 0 : data.buffer.length;
		}catch(UnsupportedEncodingException ex){
			
		}
	}

	/* (non-Javadoc)
	 * @see com.viettel.kunkun.kunkunlibrary.network.http.DataSupplier#releaseData()
	 */
	@Override
	public void releaseData() {
		// TODO Auto-generated method stub
		dataBinary = null;
//		dataText = "";
	}

	/* (non-Javadoc)
	 * @see com.viettel.kunkun.kunkunlibrary.network.http.DataSupplier#overallDataSize()
	 */
	@Override
	public int overallDataSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.viettel.kunkun.kunkunlibrary.network.http.DataSupplier#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}
}
