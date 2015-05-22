package com.net.rtsp;

import java.util.EventListener;
/**
 * This interface is implemented by objects that listen to <code>ServerRequestMessageEvent</code>s.
 * @author tyazid
 *
 */
public interface ServerRequestMessageListener extends EventListener{
	/** 
     * This method is called to notify that a server request message has been received.
     * 
     * @param event the event which identifies the server request message
     */
	void requestMessageReceived(ServerRequestMessageEvent event);

}
