package com.net.rtsp;
/**
 * This interface is an extension of {@link DataProcessor}. It define a specific server processor API
 * @author tyazid
 *
 */
public interface ServerProcessor extends DataProcessor {
	/**
	 * send a response of a received server request. 
	 * @param response the response to send
	 */
	 void sendResponse(ResponseMessage response);
	 /**
	  * Retrieves the server request message object.
	  * @return the request message object.
	  */
	 RequestMessage getServerMessage();

}
