/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lib.network;


public interface HTTPListenner {

    /**
     * this function use to client listen the response from server
     * @param Message from server
     */
    public abstract void onReceiveData(HTTPMessage mes);

    /**
     * This function use to notify to highter class the error if it appear
     * @param error
     */
    public abstract void onReceiveError(HTTPResponse response);
}

