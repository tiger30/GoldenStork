
package com.android.gs.global;

/**
 *  Chua model event
 *  @author: DoanDM
 *  @version: 1.0
 *  @since: 1.1
 */
public class ModelEvent {
	private ActionEvent actionEvent;
	Object modelData;
	int code;//dung trong timeout cua Oauth
	String modelMessage;
	// luu giu chuoi datatext tra ve tu server, phuc vu trace log
	private String dataText = "";
	// luu giu param cua request khi truyen len server
	private String param = "";
	
	public void setActionEvent(ActionEvent actionEvent) {
		this.actionEvent = actionEvent;
	}
	public ActionEvent getActionEvent() {
		return actionEvent;
	}
	public Object getModelData() {
		return modelData;
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
	public void setModelData(Object modelData) {
		this.modelData = modelData;
	}
	public String getModelMessage() {
		return modelMessage;
	}
	public void setModelMessage(String modelMessage) {
		this.modelMessage = modelMessage;
	}
	public void setDataText(String dataText) {
		this.dataText = dataText;
	}
	public String getDataText() {
		return dataText;
	}
	public void setParams(String para) {
		this.param = para;
	}
	public String getParams() {
		return param;
	}
}
