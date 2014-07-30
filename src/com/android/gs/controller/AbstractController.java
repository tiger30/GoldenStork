
package com.android.gs.controller;
import com.android.gs.activity.BaseActivity;
import com.android.gs.global.ActionEvent;
import com.android.gs.global.ModelEvent;
import com.lib.network.HTTPRequest;



public abstract class AbstractController {
	 private int isController = -1;

	abstract public void handleViewEvent(ActionEvent e);
    abstract public void handleModelEvent(ModelEvent modelEvent);
    abstract public void handleSwitchActivity(ActionEvent e);
    
    public void handleErrorModelEvent(final ModelEvent modelEvent) {
    	final ActionEvent e = modelEvent.getActionEvent();
		// end 
    	BaseActivity ac = (BaseActivity) e.sender;
		HTTPRequest request = e.request;
		if (ac.isFinished) {
			return;
		}
		handleCommonError(modelEvent);
		if(e.sender!=null){

			
			ac.runOnUiThread(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					BaseActivity view = (BaseActivity) e.sender;
					view.handleErrorModelViewEvent(modelEvent);
				}
			
			});
		}
		
		if (request != null) {
			ac.removeProcessingRequest(request, e.isBlockRequest);
			e.request = null;
		}
		
	}
    
    /**
     * 
    *  handle common error
    *  @author: DoanDM
    *  @param modelEvent
    *  @return: void
    *  @throws:
     */
    public void handleCommonError(ModelEvent modelEvent){    	
    	ActionEvent actionEvent = modelEvent.getActionEvent();
    	switch(modelEvent.getCode()){
    	
    	}
    }
}
