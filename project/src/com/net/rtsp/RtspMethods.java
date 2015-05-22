package com.net.rtsp;

/**
 * Defines the platform supported RTSP/1.0 method.
 * See <A HREF="http://www.ietf.org/rfc/rfc2326.txt"> the RFC-2326 </A> for method's detail.
 * @author tyazid
 * 
 */
public interface RtspMethods extends Protocol {

	/**
	 *  RTSP/1.0 OPTIONS method label.
	 */
	String OPTIONS = "OPTIONS";

	/**
	 *  RTSP/1.0 OPTIONS method label.
	 */
	String DESCRIBE = "DESCRIBE";

	/**
	 *  RTSP/1.0 PLAY method label.
	 */
	String PLAY = "PLAY";

	/**
	 *   RTSP/1.0 PAUSE method label.
	 */
	String PAUSE = "PAUSE";

	/**
	 *   RTSP/1.0 TEARDOWN method label.
	 */
	String TEARDOWN = "TEARDOWN";

	/**
	 *   RTSP/1.0 SETUP method label.
	 */
	String SETUP = "SETUP";

	/**
	 *   RTSP/1.0 ANNOUNCE method label.
	 */
	String ANNOUNCE = "ANNOUNCE";

	/**
	 *  RTSP/1.0 SET_PARAMETER method label.
	 */
	String SET_PARAMETER = "SET_PARAMETER";

	/**
	 *   RTSP/1.0 GET_PARAMETER method label.
	 */
	String GET_PARAMETER = "GET_PARAMETER";
}
