package com.net.rtsp.content.text.parameters;

public class ResponseContent extends com.net.rtsp.content.ResponseContent {

	protected ResponseContent(byte[] content ) {
		super(content, com.net.rtsp.content.text.parameters.ContentHandler.CONTENT_TYPE);
	}
}
