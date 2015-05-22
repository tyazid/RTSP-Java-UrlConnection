package com.net.rtsp.content.application.sdp;

import com.net.rtsp.content.RequestContent;
import com.net.rtsp.content.ResponseContent;

public class ContentHandler extends com.net.rtsp.content.ContentHandler {
	static final String CONTENT_TYPE = "application/sdp";
	public ContentHandler() {
	    super(CONTENT_TYPE);
	}
	
	public  ResponseContent getResponseContent(byte[] data ) {
		return new com.net.rtsp.content.application.sdp.ResponseContent(data);
	}
	
	public RequestContent getRequestContent( ) {
		return new com.net.rtsp.content.application.sdp.RequestContent();
	}

}
