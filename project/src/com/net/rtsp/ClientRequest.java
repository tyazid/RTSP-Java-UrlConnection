package com.net.rtsp;

import java.io.IOException;

import com.net.rtsp.content.RequestContent;

/**
 * The abstract class <code>ClientRequest</code> is the superclass of all
 * classes that implements an RTSP request definition
 * 
 * @author tyazid
 * 
 */
public abstract class ClientRequest implements RtspMethods {
	//protected static final boolean DBG = true;

	private Object appData;

	protected ClientRequest() {
	}

	/**
	 * Sets the application data
	 * 
	 * @param appData
	 *            to set
	 */

	public void setAppData(Object appData) {
		this.appData = appData;
	}

	/**
	 * Returns the first line request header.
	 * 
	 * @param method
	 *            the RTPS method of the request.
	 * @param url
	 *            the RTSP session URL <code>String</code> value.
	 * @return the <code>String</code> value of the first line of the
	 *         request§.
	 */

	protected final String getCMDHeader(String method, String url) {
		StringBuffer sb = new StringBuffer(method);
		sb.append(' ');
		sb.append(url);
		sb.append(' ');
		sb.append(RTSP_VERSION);
		return sb.toString();
	}

	/**
	 * Writes the content of the request in current connection pipe to be sent
	 * to the server.
	 * 
	 * @param client
	 *            the instance of client processor used be url connection to
	 *            ensure Client - TO -Server request / response transaction.
	 * @param request
	 *            The request object to send
	 * @param response
	 *            the response object to fill with server response fields.
	 * @throws IOException
	 *             if an IO error occurs during the process.
	 */

	protected final void writeRequest(com.net.rtsp.ClientProcessor client, RequestMessage request, ResponseMessage response) throws IOException {
		client.sendRequest(request, response);// lock until time out or
		// receive data
			com.net.rtsp.Debug.println("*-*-*-*-*-*-*-* Method.writeRequest() THE RESPONSE CSEQ IS  = " + response.findValue("CSeq"));
	}

	/**
	 * Return the request application data.
	 * 
	 * @return application data
	 */
	public Object getAppData() {
		return appData;
	}

	protected abstract void fillRequest(com.net.rtsp.RtspURLConnection urlc, RequestMessage request, int cseq) throws IOException;

	public int doRequest(com.net.rtsp.RtspURLConnection urlC, com.net.rtsp.ClientProcessor client, RequestMessage request, ResponseMessage response, int _Cseq)
			throws IOException {
		request.setEmpty();
		response.setEmpty();
		fillRequest(urlC, request, _Cseq++);
		setWithAppData(request);
		writeRequest(client, request, response);
		return _Cseq;

	}

	/**
	 * Fills the message header with some attribute. If the attribute already
	 * exists in the header, it's value will be changed
	 * 
	 * @param request the request message to set
	 * @param attrib attributes to set
	 */
	protected void fillWithAttributes(RequestMessage request, FieldAttributes attrib) {
		String[] keys = attrib.getKeys();
		for (int i = 0; i < keys.length; i++) {
			request.set(keys[i], attrib.getField(keys[i]));
		}
	}
	/**
	 * Sets the message body with the request content object. 
	 * @param request the request message to set
	 * @param content this request content to set.
	 */
	protected void fillWithContent(RequestMessage request, RequestContent content) {
		int cl = content.getContentLength();
		if (cl > 0) {
			request.set("Content-Type", "" + content.getContentType());
			request.set("Content-Length", "" + cl);
			request.setContent(content);
		}
	}
/**
 * Set the request with application data if its type is supported. The data is already a field of this RequestMessage object (see getAppData())
 * @param request request message to set 
 */
	protected void setWithAppData(RequestMessage request) {
		Object data = getAppData();
		if (data instanceof Object[]) {
			Object[] arr = (Object[]) data;
			for (int i = 0; i < arr.length; i++)
				if (arr[i] instanceof FieldAttributes)
					fillWithAttributes(request, (FieldAttributes) arr[i]);
				else if (arr[i] instanceof RequestContent) {
					fillWithContent(request, (RequestContent) arr[i]);
				}
		} else if (data instanceof FieldAttributes)
			fillWithAttributes(request, (FieldAttributes) data);
		else if (data instanceof RequestContent) {
			fillWithContent(request, (RequestContent) data);
		}
	}
}
