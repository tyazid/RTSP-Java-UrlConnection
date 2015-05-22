package com.net.rtsp;
/**
 * RTSP/1.0  response code 
 * @author tyazid
 *
 */
public interface ResponseCodes {
	/**
	 * Status code (100) indicating the client can continue.
	 */
	 int RTSP_CONTINUE = 100;

	/**
	 * Status code (200) indicating the request succeeded normally.
	 */
	 int RTSP_OK = 200;

	/**
	 * Status code (201) indicating the request succeeded and created a new
	 * resource on the server.
	 */
	 int RTSP_CREATED = 201;

	/**
	 * Status code (250) indicating the request succeeded but the server is low
	 * on storage.
	 */
	 int RTSP_LOW_ON_STORAGE = 250;

	/**
	 * Status code (300) indicating that the requested resource corresponds to
	 * any one of a set of representations, each with its own specific location.
	 */
	 int RTSP_MULTIPLE_CHOICES = 300;

	/**
	 * Status code (301) indicating that the resource has permanently moved to a
	 * new location, and that future references should use a new URI with their
	 * requests.
	 */
	 int RTSP_MOVED_PERMANENTLY = 301;

	/**
	 * Status code (302) indicating that the resource has temporarily moved to
	 * another location, but that future references should still use the
	 * original URI to access the resource.
	 */
	 int RTSP_MOVED_TEMPORARILY = 302;

	/**
	 * Status code (303) indicating that the response to the request can be
	 * found under a different URI.
	 */
	 int RTSP_SEE_OTHER = 303;

	/**
	 * Status code (304) indicating that a conditional GET operation found that
	 * the resource was available and not modified.
	 */
	 int RTSP_NOT_MODIFIED = 304;

	/**
	 * Status code (305) indicating that the requested resource <em>MUST</em>
	 * be accessed through the proxy given by the <code><em>Location</em></code>
	 * field.
	 */
	 int RTSP_USE_PROXY = 305;

	/**
	 * Status code (400) indicating the request sent by the client was
	 * syntactically incorrect.
	 */
	 int RTSP_BAD_REQUEST = 400;

	/**
	 * Status code (401) indicating that the request requires HTTP
	 * authentication.
	 */
	 int RTSP_UNAUTHORIZED = 401;

	/**
	 * Status code (402) reserved for future use.
	 */
	 int RTSP_PAYMENT_REQUIRED = 402;

	/**
	 * Status code (403) indicating the server understood the request but
	 * refused to fulfill it.
	 */
	 int RTSP_FORBIDDEN = 403;

	/**
	 * Status code (404) indicating that the requested resource is not
	 * available.
	 */
	 int RTSP_NOT_FOUND = 404;

	/**
	 * Status code (405) indicating that the method specified in the
	 * <code><em>Request-Line</em></code> is not allowed for the resource
	 * identified by the <code><em>Request-URI</em></code>.
	 */
	 int RTSP_METHOD_NOT_ALLOWED = 405;

	/**
	 * Status code (406) indicating that the resource identified by the request
	 * is only capable of generating response entities which have content
	 * characteristics not acceptable according to the accept headers sent in
	 * the request.
	 */
	 int RTSP_NOT_ACCEPTABLE = 406;

	/**
	 * Status code (407) indicating that the client <em>MUST</em> first
	 * authenticate itself with the proxy.
	 */
	 int RTSP_PROXY_AUTHENTICATION_REQUIRED = 407;

	/**
	 * Status code (408) indicating that the client did not produce a request
	 * within the time that the server was prepared to wait.
	 */
	 int RTSP_REQUEST_TIMEOUT = 408;

	/**
	 * Status code (410) indicating that the resource is no longer available at
	 * the server and no forwarding address is known. This condition
	 * <em>SHOULD</em> be considered permanent.
	 */
	 int RTSP_GONE = 410;

	/**
	 * Status code (411) indicating that the request cannot be handled without a
	 * defined <code><em>Content-Length</em></code>.
	 */
	 int RTSP_LENGTH_REQUIRED = 411;

	/**
	 * Status code (412) indicating that the precondition given in one or more
	 * of the request-header fields evaluated to false when it was tested on the
	 * server.
	 */
	 int RTSP_PRECONDITION_FAILED = 412;

	/**
	 * Status code (413) indicating that the server is refusing to process the
	 * request because the request entity is larger than the server is willing
	 * or able to process.
	 */
	 int RTSP_REQUEST_ENTITY_TOO_LARGE = 413;

	/**
	 * Status code (414) indicating that the server is refusing to service the
	 * request because the <code><em>Request-URI</em></code> is longer than
	 * the server is willing to interpret.
	 */
	 int RTSP_REQUEST_URI_TOO_LONG = 414;

	/**
	 * Status code (415) indicating that the server is refusing to service the
	 * request because the entity of the request is in a format not supported by
	 * the requested resource for the requested method.
	 */
	 int RTSP_UNSUPPORTED_MEDIA_TYPE = 415;

	/**
	 * Status code (451) indicating that the server did not understand the
	 * parameter given.
	 */
	 int RTSP_PARAMETER_NOT_UNDERSTOOD = 451;

	/**
	 * Status code (452) indicating that the conference requested could not be
	 * found.
	 */
	 int RTSP_CONFERENCE_NOT_FOUND = 452;

	/**
	 * Status code (453) indicating that there is not enough bandwidth to send
	 * the resource requested.
	 */
	 int RTSP_NOT_ENOUGH_BANDWIDTH = 453;

	/**
	 * Status code (454) indicating that the session given with the request is
	 * not found in this server.
	 */
	 int RTSP_SESSION_NOT_FOUND = 454;

	/**
	 * Status code (455) indicating that the method used in the request is not
	 * valid in the current state of the presentation.
	 */
	 int RTSP_METHOD_NOT_VALID_IN_THIS_STATE = 455;

	/**
	 * Status code (456) indicating that the header is not valid for this the
	 * resource.
	 */
	 int RTSP_HEADER_FIELD_NOT_VALID_FOR_RESOURCE = 456;

	/**
	 * Status code (457) indicating that the range is not valid for this
	 * presentation. This could be because the range is to large.
	 */
	 int RTSP_INVALID_RANGE = 457;

	/**
	 * Status code (458) indicating that the parameter requested to be set using
	 * the SET_PARAMETER method is a read only parameter.
	 */
	 int RTSP_PARAMETER_IS_READONLY = 458;

	/**
	 * Status code (459) indicating that this server does not support aggregate
	 * operation on its presentation.
	 */
	 int RTSP_AGGREGATE_OPERATION_NOT_ALLOWED = 459;

	/**
	 * Status code (460) indicating that this server requires that there be used
	 * aggregate operation when requisting a resource.
	 */
	 int RTSP_ONLY_AGGREGATE_OPERATION_ALLOWED = 460;

	/**
	 * Status code (461) indicating that not suitable transport could be
	 * configured after a request with the SETUP method.
	 */
	 int RTSP_UNSUPPORTED_TRANSPORT = 461;

	/**
	 * Status code (462) indicating that the destination of the presentation
	 * could not be reached.
	 */
	 int RTSP_DESTINATION_UNREACHABLE = 462;

	/**
	 * Status code (500) indicating an error inside the RTSP server which
	 * prevented it from fulfilling the request.
	 */
	 int RTSP_INTERNAL_SERVER_ERROR = 500;

	/**
	 * Status code (501) indicating the RTSP server does not support the
	 * functionality needed to fulfill the request.
	 */
	 int RTSP_NOT_IMPLEMENTED = 501;

	/**
	 * Status code (502) indicating that the RTSP server received an invalid
	 * response from a server it consulted when acting as a proxy or gateway.
	 */
	 int RTSP_BAD_GATEWAY = 502;

	/**
	 * Status code (503) indicating that the RTSP server is temporarily
	 * overloaded, and unable to handle the request.
	 */
	 int RTSP_SERVICE_UNAVAILABLE = 503;

	/**
	 * Status code (504) indicating that the server did not receive a timely
	 * response from the upstream server while acting as a gateway or proxy.
	 */
	 int RTSP_GATEWAY_TIMEOUT = 504;

	/**
	 * Status code (505) indicating that the server does not support or refuses
	 * to support the RTSP protocol version that was used in the request
	 * message.
	 */
	 int RTSP_VERSION_NOT_SUPPORTED = 505;

	/**
	 * Status code (551) indicating that the required option is not supported.
	 */
	 int RTSP_OPTION_NOT_SUPPORTED = 551;

}
