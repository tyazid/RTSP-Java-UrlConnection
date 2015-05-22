package com.net.rtsp;

import java.util.HashMap;
import java.util.Map;

/**
 * this utility class is used to build status line string according to the status value.
 * @author tyazid
 *
 */
public class StatusLineBuilder implements ResponseCodes {
	private static Map map = new HashMap();
	static {
		map.put(new Integer(RTSP_CONTINUE), "Continue");
		map.put(new Integer(RTSP_OK), "OK");
		map.put(new Integer(RTSP_CREATED), "Created");
		map.put(new Integer(RTSP_LOW_ON_STORAGE), "Low on Storage Space");
		map.put(new Integer(RTSP_MULTIPLE_CHOICES), "Multiple Choices");
		map.put(new Integer(RTSP_MOVED_PERMANENTLY), "Moved Permanently");
		map.put(new Integer(RTSP_MOVED_TEMPORARILY), "Moved Temporarily");
		map.put(new Integer(RTSP_SEE_OTHER), "See Other");
		map.put(new Integer(RTSP_NOT_MODIFIED), "Not Modified");
		map.put(new Integer(RTSP_USE_PROXY), "Use Proxy");
		map.put(new Integer(RTSP_BAD_REQUEST), "Bad Request");
		map.put(new Integer(RTSP_UNAUTHORIZED), "Unauthorized");
		map.put(new Integer(RTSP_PAYMENT_REQUIRED), "Payment Required");
		map.put(new Integer(RTSP_FORBIDDEN), "Forbidden");
		map.put(new Integer(RTSP_NOT_FOUND), "Not Found");
		map.put(new Integer(RTSP_METHOD_NOT_ALLOWED), "Method Not Allowed");
		map.put(new Integer(RTSP_NOT_ACCEPTABLE), "Not Acceptable");
		map.put(new Integer(RTSP_PROXY_AUTHENTICATION_REQUIRED), "Proxy Authentication Required");
		map.put(new Integer(RTSP_REQUEST_TIMEOUT), "Request Time-out");
		map.put(new Integer(RTSP_GONE), "Gone");
		map.put(new Integer(RTSP_LENGTH_REQUIRED), "Length Required");
		map.put(new Integer(RTSP_PRECONDITION_FAILED), "Precondition Failed");
		map.put(new Integer(RTSP_REQUEST_ENTITY_TOO_LARGE), "Request Entity Too Large");
		map.put(new Integer(RTSP_REQUEST_URI_TOO_LONG), "Request-URI Too Large");
		map.put(new Integer(RTSP_UNSUPPORTED_MEDIA_TYPE), "Unsupported Media Type");
		map.put(new Integer(RTSP_PARAMETER_NOT_UNDERSTOOD), "Parameter Not Understood");
		map.put(new Integer(RTSP_CONFERENCE_NOT_FOUND), "Conference Not Found");
		map.put(new Integer(RTSP_NOT_ENOUGH_BANDWIDTH), "Not Enough Bandwidth");
		map.put(new Integer(RTSP_SESSION_NOT_FOUND), "Session Not Found");
		map.put(new Integer(RTSP_METHOD_NOT_VALID_IN_THIS_STATE), "Method Not Valid in This State");
		map.put(new Integer(RTSP_HEADER_FIELD_NOT_VALID_FOR_RESOURCE), "Header Field Not Valid for Resource");
		map.put(new Integer(RTSP_INVALID_RANGE), "Invalid Range");
		map.put(new Integer(RTSP_PARAMETER_IS_READONLY), "Parameter Is Read-Only");
		map.put(new Integer(RTSP_AGGREGATE_OPERATION_NOT_ALLOWED), "Aggregate operation not allowed");
		map.put(new Integer(RTSP_ONLY_AGGREGATE_OPERATION_ALLOWED), "Only aggregate operation allowed");
		map.put(new Integer(RTSP_UNSUPPORTED_TRANSPORT), "Unsupported transport");
		map.put(new Integer(RTSP_DESTINATION_UNREACHABLE), "Destination unreachable");
		map.put(new Integer(RTSP_INTERNAL_SERVER_ERROR), "Internal Server Error");
		map.put(new Integer(RTSP_NOT_IMPLEMENTED), "Not Implemented");
		map.put(new Integer(RTSP_BAD_GATEWAY), "Bad Gateway");
		map.put(new Integer(RTSP_SERVICE_UNAVAILABLE), "Service Unavailable");
		map.put(new Integer(RTSP_GATEWAY_TIMEOUT), "Gateway Time-out");
		map.put(new Integer(RTSP_VERSION_NOT_SUPPORTED), "RTSP Version not supported");
		map.put(new Integer(RTSP_OPTION_NOT_SUPPORTED), "Option not supported");
	}
	/**
	 * Build the status line according the status code
	 * @param code the status code 
	 * @return status line as <code>String</code>
	 */

	public static String getStatusLine(int code) {
		Integer k = new Integer(code);
		String label = (String) map.get(k);
		if (label != null)
			return Protocol.RTSP_VERSION + ' ' + code + ' ' + label;
		return null;
	}
}
