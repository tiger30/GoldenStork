/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lib.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HTTPResponse extends HTTPMessage{
	static final int REGULAR_ALLOC = 32;
	static final int BUFFER_SIZE = 512;
    HTTPRequest request;
    
    int nByteReceive = 0;
    
    public HTTPRequest getRequest() {
        return request;
    }
    
    public HTTPResponse(HTTPRequest request) {
    	this.request = request;
		setContentType(HTTPResponse.CONTENT_TEXT);
		setAction(request.getAction());
		setId(request.getId());
		setTitle(request.getTitle());
		setUserData(request.getUserData());
		setObserver(request.getObserver());
    }
    
    public void setRequest(HTTPRequest request) {
        this.request = request;
    }

    public boolean isAlive() {
        boolean ret = true;
        if (this.request != null) {
            ret = request.isAlive();
        }
        return ret;
    }

    public HTTPResponse() {
        super();
    }
    
    public void readData(InputStream inputStream) throws IOException{
		if(request != null){
			if(HTTPRequest.DATA_TYPE_TEXT.equals(request.getDataTypeReceive())){
				getStringUnicode(inputStream);
			}else{//binary
				this.getBinaryData(inputStream);
			}
		}
	}
	
	private void getBinaryData(InputStream inputStream) {
        int in;        
        InputStream is = null;
        try {
            is = inputStream;             
            ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
            byte[] buff = new byte[256];
            while ((in = is.read(buff)) != -1) {
                bStrm.write(buff, 0, in);                
            }
            this.setDataBinary(bStrm.toByteArray());
            bStrm.close();
            is.close();            
        } catch (IOException e) {
        } catch (Throwable e) {
        }        
    }

	private void getStringUnicode(InputStream is) {        
        InputStreamReader iStrm = null;        
        StringBuffer response = new StringBuffer();
        final int MAX_SIZE_RECEIVE = 1024;
        try {            
            iStrm = new InputStreamReader(is, "UTF-8");            
            char cbuf[] = new char[MAX_SIZE_RECEIVE]; // test
            int readlen = 1;
            while (readlen > 0)
            {
            	readlen = iStrm.read(cbuf, 0, cbuf.length);
            	if (readlen > 0)
            	{
            		response.append(cbuf, 0, readlen);
            	}
            	
            }            
        } catch (IOException e) {
            
        } finally {
            if (iStrm != null) {
                try {
                    iStrm.close();
                    iStrm = null;
                } catch (IOException ex) {
                    
                }
            }
        }
        setDataText(response.toString());
    }   
}
