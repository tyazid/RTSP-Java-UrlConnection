package com.net.rtsp.content.application.sdp;

public class ResponseContent extends com.net.rtsp.content.ResponseContent {

	protected ResponseContent(byte[] content ) {
		super(content, com.net.rtsp.content.application.sdp.ContentHandler.CONTENT_TYPE);
	}
 

}
