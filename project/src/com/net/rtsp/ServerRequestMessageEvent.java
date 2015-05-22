package com.net.rtsp;

import java.util.EventObject;
/**
 *  This class described an Object event which is used to notify then a server request has been received.
 * @author tyazid
 *
 */

public class ServerRequestMessageEvent extends EventObject {
	private RequestMessage message;
/**
 * Construct an instance of ServerRequestMessageEvent 
 * @param source source of the event
 * @param message the received request.
 */
	public ServerRequestMessageEvent(Object source, RequestMessage message) {
		super(source);
		this.message = message;
	}
/**
 * Returns the received request message.
 * @return the request message 
 */
	public RequestMessage getMessage() {
		return message;
	}

}
