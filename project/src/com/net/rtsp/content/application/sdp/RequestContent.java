package com.net.rtsp.content.application.sdp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RequestContent extends com.net.rtsp.content.RequestContent {
	

	public RequestContent() {
		super(com.net.rtsp.content.application.sdp.ContentHandler.CONTENT_TYPE);
		
	}

	public byte[] getContent() {
		try {
			String[] keys = getContentAttributes().getKeys();
			if (keys.length == 0)
				return null;
			byte[] EOL_BYTES = "\r\n".getBytes();
			byte eq = '=';
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			String v;
			for (int i = 0; i < keys.length; i++) {
				buffer.write(keys[i].getBytes());
				
				if ((v = getContentAttributes().getField(keys[i])) != null) {
					buffer.write(eq);
					buffer.write(v.getBytes());
				}
				buffer.write(EOL_BYTES);
			}
			return buffer.toByteArray();
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	return null;
	}
}
