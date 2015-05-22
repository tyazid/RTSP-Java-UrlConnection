package com.net.rtsp;

/**
 *  The data processor which processes data which has been retrieved(message) from or sent(request) by server through current established connection  
 *  @author tyazid
 * 
 */
public interface DataProcessor {
	/**
	 * processes the retrieved data.
	 * @param data
	 */
	void processData(byte[] data);
}
