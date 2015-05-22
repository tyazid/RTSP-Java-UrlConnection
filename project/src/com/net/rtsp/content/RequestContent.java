package com.net.rtsp.content;

import com.net.rtsp.FieldAttributes;

/**
 * The abstract class <code>RequestContent</code> is the superclass of all
 * RTSP Request Content classes that read a request body content Object from a
 * RtspURLConnection.
 * 
 * 
 * @author tyazid
 * 
 */
public abstract class RequestContent extends Content{
	private FieldAttributes attribute;
	/**
	 * Constructs a new <code>RequestContent</code> using the content mimeType
	 * @param mimeType
	 */
	protected RequestContent( String mimeType) {
		super(mimeType);
		attribute = new FieldAttributes();
		
	}
	/**
	 * 
	 * @return
	 */
	public FieldAttributes getContentAttributes() {
		return attribute;
	}
	/**
	 * Returns the content length of this request content
	 * @return the content length
	 */
	public final int getContentLength(){
		byte[] cnt = getContent();
		return cnt!=null?cnt.length:0;
	}
}
