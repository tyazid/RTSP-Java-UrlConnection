package com.net.rtsp;

import java.io.IOException;
/**
 * A client processor interface .
 */
public interface ClientProcessor extends DataProcessor {
	/**
	 * send the request to the server through current established connection and fills the response with the retrieved response values .
	 * @param request the request object to perform
	 * @param response the response to fill
	 * @throws IOException is thrown of an IO error occures. 
	 */
	 void  sendRequest(RequestMessage request, ResponseMessage response) throws IOException;

}
