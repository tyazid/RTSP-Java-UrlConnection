package com.net.rtsp;

import java.io.IOException;
/**
 * The abstract class <code>ServerRequestHandler</code> is the superclass 
 * of all classes that handle an RTSP request from server (such as ANNOUCE request)  
 * 
 * @author tyazid
 *
 */
public abstract class ServerRequestHandler {
	/**
	 * Handles the server request, so this request can be treated by this handler
	 * @param request server request object
	 * @param server server processor instance through which the request has been received
	 * @throws IOException thrown if an IO error occurs.
	 */
	public abstract void handleRequest(RequestMessage request, ServerProcessor server) throws IOException;
/**
 * Sends the response of the received request
 * @param response the response object to send
 * @param server the server processor instance through which the request has been received
 */
	protected void sendResponse(ResponseMessage response, ServerProcessor server) {
		server.sendResponse(response);
	}
}
