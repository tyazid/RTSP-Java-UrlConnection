package com.net.rtsp;

/**
 * This interface define
 * 
 * @author tyazid
 * 
 */
public interface Protocol {
	/**
	 * Protocol definition
	 */
	String PROTOCOL = "RTSP";

	/**
	 * URL separator string
	 */
	String URL_SEPARATOR = "/";

	/**
	 * Protocol & version value
	 */

	String RTSP_VERSION = PROTOCOL + URL_SEPARATOR + "1.0";

	/**
	 * default RTSP port values
	 */

	int[] PORT = { 554, 5554 };// possible def ports

}
