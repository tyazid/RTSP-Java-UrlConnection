package com.net.rtsp.content;
/**
 * The abstract class <code>Content</code> is the superclass of all
 * classes that implements an RTSP request/response body content
 * 
 * @author tyazid
 * 
 */
public abstract class Content {

	private String mimeType;
/**
 * Constructs a new <code>Content</code>
 * @param mimeType
 */
	public Content( String mimeType) {
		
		this.mimeType = mimeType;
	}
/**
 * Returns the content type of this content
 * @return the content type as <code>String</code>
 */
	public final String getContentType() {
		return mimeType;
	}
	
	/**
	 * Returns the bytes content of this RTSP body content 
	 * @return byte array expressing the content. 
	 */

	public abstract byte[] getContent() ;
	
	
	
	 
}
