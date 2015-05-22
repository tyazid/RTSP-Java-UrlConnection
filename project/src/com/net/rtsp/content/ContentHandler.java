package com.net.rtsp.content;

import java.io.IOException;
import java.net.URLConnection;
/**
 * The abstract class <code>ContentHandler</code> is the superclass of all RTSP Content classes that read an Object from a RtspURLConnection.


 * @author tyazid
 *
 */
public abstract class ContentHandler extends java.net.ContentHandler {
	
	private String contentType;
	
	protected ContentHandler(String contentType) {
		super();
		this.contentType = contentType;
	}
	
	public String getContentType() {
		return contentType;
	}
	public abstract  com.net.rtsp.content.ResponseContent getResponseContent(byte[] data);
	
	public abstract  com.net.rtsp.content.RequestContent getRequestContent( );
	
	//add requect content builder

	/* (non-Javadoc)
	 * @see java.net.ContentHandler#getContent(java.net.URLConnection)
	 */
	public final Object getContent(URLConnection urlc) throws IOException {
		int l = urlc.getContentLength();
		com.net.rtsp.Debug.println("### BODY CONTENT LENGTH = "+l);
		
		
		if (l > 0) {
			
			byte[] c = new byte[l];
			
			
			urlc.getInputStream().read(c);
			
			com.net.rtsp.Debug.println("### BODY CONTENT  = \n"+new String(c)+"\n####");
			
			if(urlc.getContentType().toLowerCase().equals(getContentType().toLowerCase()))
			   return getResponseContent(c);
		}
		return null;
	}
	
	

}
