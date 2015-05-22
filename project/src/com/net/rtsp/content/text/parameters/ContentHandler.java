package com.net.rtsp.content.text.parameters;

import com.net.rtsp.content.RequestContent;

public class ContentHandler extends com.net.rtsp.content.ContentHandler {
	static final String CONTENT_TYPE = "text/parameters";
	public ContentHandler( ) {
		super(CONTENT_TYPE);
	}

	public com.net.rtsp.content.ResponseContent getResponseContent(byte[] data )  {
		return new com.net.rtsp.content.text.parameters.ResponseContent(data );
	}

	public RequestContent getRequestContent() {
		return new com.net.rtsp.content.text.parameters.RequestContent();
	}

 

}
