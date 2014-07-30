/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lib.network;
public class HTTPMessage {

    public static final String CONTENT_TEXT = "text";
    public static final String CONTENT_BINARY = "binary";
    public static final String CONTENT_JSON = "application/json";
    public static final String MULTIPART_JSON="multipart/form-data; boundary=" + NetworkUtil.getBoundaryString();

    int id = -1;
    byte[] dataBinary;
    String dataText;//tao ra 2 cai de ko phai convert tu byte qua gay lang phi
    int action = -1;
    String contentType =CONTENT_TEXT;
    String title;
    boolean alive=true;
    //AnhND added: move from httpRequest
    private HTTPListenner observer;
    //End AnhND
    //AnhND added
    private String errMessage = "";
    private Object userData;
    private boolean cache = false;
    private String m_Key = "";
    private boolean onCahce = false;
    int code;
    //End AnhND
    
    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    /**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
    public byte[] getDataBinary() {
        return dataBinary;
    }

    public void setDataBinary(byte[] dataBinary) {
        this.dataBinary = dataBinary;
//        this.contentType=CONTENT_BINARY;
    }

    public String getDataText() {
        return dataText;
    }

    public void setDataText(String dataText) {
        this.dataText = dataText;
        //this.contentType=CONTENT_TEXT;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

   /**
    * get ID of this message
    * ID can use to different of the same Message type
    * example when you download image from many thread and receive at one point
    * you need to an ID to indentify what image come from
    * @return ID of message
    */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }
    /**
     * set type of the message
     * Type can be CONTENT_TEXT or CONTENT_BINARY
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    //AnhND added: move from httpRequest
    public HTTPListenner getObserver() {
        return observer;
    }

    public void setObserver(HTTPListenner observer) {
        this.observer = observer;
    }
    //End AnhND

    //AnhND added
    public void setError(String strErrorMsg){
        code = HTTPClient.ERR_UNKNOWN;
        setDataText(strErrorMsg);
    }

    public void setError(int errorCode, String strErrorMsg) {
        code = errorCode;
        setDataText(strErrorMsg);
    }

    public void setError(int errorCode, String errMessage, String dataText) {
        this.code = errorCode;
        this.errMessage = errMessage;
        setDataText(dataText);
    }
    
    public int getErrorCode(){
        return code;
    }
    
    public String getErrMessage(){
    	return errMessage;
    }

    /**
     * @return the userData
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * @param userData the userData to set
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * @return the cache
     */
    public boolean isRequireCache() {
        return cache;
    }

    /**
     * @param cache the cache to set
     */
    public void setCache(boolean cache, String key) {
        this.cache = cache;
        m_Key = key;
    }

    /**
     * @return the m_Key
     */
    public String getKeyCache() {
        return m_Key;
    }

    /**
     * @return the onCahce
     */
    public boolean isOnCahce() {
        return onCahce;
    }

    /**
     * @param onCahce the onCahce to set
     */
    public void setOnCahce(boolean onCahce) {
        this.onCahce = onCahce;
    }
    //End AnhND
}
